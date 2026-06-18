package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginRegisterActivity extends AppCompatActivity {

    private Button btnLogin, btnRegistrasi, btnLoginAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrasi = findViewById(R.id.btnRegistrasi);
        btnLoginAdmin = findViewById(R.id.btnLoginAdmin);

        // Tombol Login
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Tombol Registrasi
        btnRegistrasi.setOnClickListener(v -> {
            Intent intent = new Intent(LoginRegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Tombol Login Admin
        btnLoginAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginRegisterActivity.this, LoginAdminActivity.class);
            startActivity(intent);
        });
    }
}