package com.example.passwordmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecondActivity extends AppCompatActivity {

    TextView nameVal, passVal;

    private static final String SECRET_KEY
            = "my_super_secret_ky_ho_ho_ho";
    private static final String SALT = "ssshhhhhhhhhhh!!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        nameVal = (TextView) findViewById(R.id.textViewNameVal);
        passVal = (TextView) findViewById(R.id.textViewPassVal);

        Intent caller = getIntent();
        nameVal.setText(caller.getStringExtra("Key"));

        SharedPreferences preferences = getSharedPreferences("password_pref", MODE_PRIVATE);
        String password = preferences.getString(caller.getStringExtra("Key"),"");

        passVal.setText(decrypt(password));
    }

    public String decrypt(String strToDecrypt) {
        try {

            String decr = "";

            // Default byte array
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0 };
            // Create IvParameterSpec object and assign with
            // constructor
            IvParameterSpec ivspec
                    = new IvParameterSpec(iv);

            // Create SecretKeyFactory Object
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
                    "AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    ivspec);
            // Return decrypted string
            decr = new String(cipher.doFinal(
                    Base64.getDecoder().decode(strToDecrypt)));

            return decr;
        }
        catch (Exception e) {
            e.printStackTrace();

            return "";
        }
    }
}