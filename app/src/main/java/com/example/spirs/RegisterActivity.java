package com.example.spirs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etNama, etAlamat;
    private Button btnCreateAccount;
    private ImageButton btnBack, btnGoogle;
    private TextView tvSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etNama = findViewById(R.id.etNama);
        etAlamat = findViewById(R.id.etAlamat);

        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnBack = findViewById(R.id.btnBack);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvSignIn = findViewById(R.id.tvSignIn);

        btnBack.setOnClickListener(v -> finish());

        btnGoogle.setOnClickListener(v -> {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
            ));
        });

        tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(
                    RegisterActivity.this,
                    LoginActivity.class
            ));
        });

        btnCreateAccount.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String url = "http://10.123.192.159/SPIRS/register.PHP";

        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();

        if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || alamat.isEmpty()) {
            Toast.makeText(this, "Semua data harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.getString("status");
                        String message = obj.getString("message");

                        if (status.equals("success")) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(
                                    RegisterActivity.this,
                                    LoginActivity.class
                            ));
                            finish();

                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response error", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> Toast.makeText(this,
                        "Koneksi error: " + error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("email", email);
                params.put("password", password);
                params.put("alamat", alamat);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}