package com.example.spirs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginAdminActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private ImageButton btnBack;
    private TextView tvForgotPassword;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= SESSION CHECK =================
        if (SessionManager.getUserId(this) > 0) {

            String role = SessionManager.getRole(this);

            if ("admin".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, BerandaAdminActivity.class));
            } else {
                SessionManager.logout(this);
                startActivity(new Intent(this, LoginActivity.class));
            }

            finish();
            return;
        }

        setContentView(R.layout.activity_login_admin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        preferences = getSharedPreferences("AdminPrefs", MODE_PRIVATE);

        loadRememberMe();

        btnBack.setOnClickListener(v -> finish());

        tvForgotPassword.setOnClickListener(v -> {
            String phone = "6281234567890";
            String message = "Halo Admin, saya lupa password akun admin SPIRS.";

            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://wa.me/" + phone + "?text=" + Uri.encode(message))
            ));
        });

        btnLogin.setOnClickListener(v -> loginAdmin());
    }

    private void loginAdmin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        String url = "http://10.123.192.159/spirs/login.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    btnLogin.setEnabled(true);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            String role = obj.getString("role");

                            if (!"admin".equalsIgnoreCase(role)) {
                                Toast.makeText(this,
                                        "Akses ditolak! Bukan admin",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int userId = obj.getInt("id");
                            String nama = obj.getString("nama");
                            String emailRes = obj.getString("email");

                            saveRememberMe(email);

                            SessionManager.saveLogin(
                                    this,
                                    userId,
                                    nama,
                                    emailRes,
                                    role
                            );

                            Toast.makeText(this,
                                    "Login Admin Berhasil",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(
                                    this,
                                    BerandaAdminActivity.class
                            ));

                            finish();

                        } else {
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Response Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this,
                            "Koneksi Error: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void loadRememberMe() {

        boolean remember = preferences.getBoolean("remember", false);

        if (remember) {
            etEmail.setText(preferences.getString("email", ""));
            cbRememberMe.setChecked(true);
        }
    }

    private void saveRememberMe(String email) {

        SharedPreferences.Editor editor = preferences.edit();

        if (cbRememberMe.isChecked()) {
            editor.putBoolean("remember", true);
            editor.putString("email", email);
        } else {
            editor.clear();
        }

        editor.apply();
    }
}