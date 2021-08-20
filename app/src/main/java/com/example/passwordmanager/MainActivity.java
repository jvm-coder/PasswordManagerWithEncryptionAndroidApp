package com.example.passwordmanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    ImageButton buttonAdd;

    private String getName="", getPass="";

    private static final String SECRET_KEY
            = "my_super_secret_ky_ho_ho_ho";
    private static final String SALT = "ssshhhhhhhhhhh!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAdd = (ImageButton) findViewById(R.id.buttonAdd);

        SharedPreferences preferences = getSharedPreferences("password_pref" ,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int size = preferences.getInt("size", 0);
        ArrayList<String> myArrayList = new ArrayList<String>();

        for(int i=0;i<size;i++) {
            myArrayList.add(preferences.getString(("name_"+i),("")));
        }

        ListView listView = (ListView) findViewById(R.id.listView);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myArrayList);
        listView.setAdapter(myAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("CREDENTIALS");
                builder.setIcon(R.drawable.dialog_icon);

                final View myView = getLayoutInflater().inflate(R.layout.alert_dialog_input, null);
                final EditText inputName = (EditText) myView.findViewById(R.id.username);
                final EditText inputPass = (EditText) myView.findViewById(R.id.password);

                builder.setView(myView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getName = inputName.getText().toString();
                        getPass = inputPass.getText().toString();

                        if(getName.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Name field cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                        else if ((preferences.getString(getName,null))!=null) {
                            Toast.makeText(getApplicationContext(), "Credential already present", Toast.LENGTH_SHORT).show();
                        }
                        else if (getName.length()>25) {
                            Toast.makeText(getApplicationContext(), "Name size exceeded 25 characters", Toast.LENGTH_SHORT).show();
                        }
                        else if (getPass.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Password field cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                        else if (getPass.length()>16) {
                            Toast.makeText(getApplicationContext(), "Password exceeded 16 characters", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int prevSize = preferences.getInt("size", 0);
                            String encPass = encrypt(getPass);

                            editor.putString(getName, encPass);

                            editor.putString("name_" + prevSize, getName);
                            editor.putString("pass_" + prevSize, encPass);

                            editor.putInt("size", prevSize + 1);
                            myArrayList.add(getName);

                            listView.setAdapter(myAdapter);

                            editor.commit();
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent goToSecond = new Intent();
                goToSecond.setClass(getApplicationContext(), SecondActivity.class);
                goToSecond.putExtra("Key", (myAdapter.getItem(position)));
                startActivity(goToSecond);
            }
        });
    }

    public String encrypt(String strToEncrypt) {
        try {
            String encr = "";

            // Create default byte array
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0 };
            IvParameterSpec ivspec
                    = new IvParameterSpec(iv);

            // Create SecretKeyFactory object
            SecretKeyFactory factory
                    = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            // Create KeySpec object and assign with
            // constructor
            KeySpec spec = new PBEKeySpec(
                    SECRET_KEY.toCharArray(), SALT.getBytes(),
                    65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(
                    tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(
                    "AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                    ivspec);
            // Return encrypted string
            encr = Base64.getEncoder().encodeToString(
                    cipher.doFinal(strToEncrypt.getBytes(
                            StandardCharsets.UTF_8)));

            return encr;
        }
        catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }

    public void showText(View view) {
        Toast.makeText(this, "Image button clicked !!!", Toast.LENGTH_SHORT).show();
    }
}