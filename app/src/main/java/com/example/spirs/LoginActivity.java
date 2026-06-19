package com.example.spirs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private ImageButton btnBack, btnGoogle;
    private TextView tvForgotPassword, tvSignUp;

    private SharedPreferences rememberPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= AUTO LOGIN CHECK =================
        if (SessionManager.getUserId(this) > 0) {

            String role = SessionManager.getRole(this);

            if ("admin".equalsIgnoreCase(role)) {
                startActivity(new Intent(this, BerandaAdminActivity.class));
            } else {
                startActivity(new Intent(this, BerandaActivity.class));
            }

            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        btnGoogle = findViewById(R.id.btnGoogle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignUp);

        rememberPrefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);

        loadRememberMe();

        btnBack.setOnClickListener(v -> finish());

        btnGoogle.setOnClickListener(v ->
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com")
                ))
        );

        tvSignUp.setOnClickListener(v ->
                startActivity(
                        new Intent(
                                LoginActivity.this,
                                RegisterActivity.class
                        )
                )
        );

        tvForgotPassword.setOnClickListener(v -> {

            String phone = "6281234567890";
            String message = "Halo Admin, saya lupa password akun SPIRS.";

            startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(
                                    "https://wa.me/"
                                            + phone
                                            + "?text="
                                            + Uri.encode(message)
                            )
                    )
            );
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Email dan Password wajib diisi",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        btnLogin.setEnabled(false);

        String url = "http://10.123.192.159/SPIRS/login.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,

                response -> {

                    btnLogin.setEnabled(true);

                    try {

                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            int userId = obj.getInt("id");
                            String nama = obj.getString("nama");
                            String emailRes = obj.getString("email");
                            String role = obj.getString("role");

                            // TAMBAHAN
                            String alamat = obj.getString("alamat");

                            // Cegah admin login di user page
                            if ("admin".equalsIgnoreCase(role)) {

                                Toast.makeText(
                                        this,
                                        "Silakan login melalui halaman Admin",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            saveRememberMe(email);

                            // ==========================
                            // SIMPAN KE SQLITE
                            // ==========================
                            DatabaseHelper db =
                                    new DatabaseHelper(this);

                            db.saveOrUpdateUser(
                                    nama,
                                    emailRes,
                                    password,
                                    alamat
                            );
                            // ==========================

                            SessionManager.saveLogin(
                                    this,
                                    userId,
                                    nama,
                                    emailRes,
                                    role
                            );

                            Toast.makeText(
                                    this,
                                    "Login berhasil",
                                    Toast.LENGTH_SHORT
                            ).show();

                            startActivity(
                                    new Intent(
                                            this,
                                            BerandaActivity.class
                                    )
                            );

                            finish();

                        } else {

                            Toast.makeText(
                                    this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Response error: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    btnLogin.setEnabled(true);

                    Toast.makeText(
                            this,
                            "Koneksi error: "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }

        ) {
            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {

                Map<String, String> params =
                        new HashMap<>();

                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void loadRememberMe() {

        if (rememberPrefs.getBoolean("remember", false)) {

            etEmail.setText(
                    rememberPrefs.getString(
                            "email",
                            ""
                    )
            );

            cbRememberMe.setChecked(true);
        }
    }

    private void saveRememberMe(String email) {

        SharedPreferences.Editor editor =
                rememberPrefs.edit();

        if (cbRememberMe.isChecked()) {

            editor.putBoolean("remember", true);
            editor.putString("email", email);

        } else {

            editor.clear();
        }

        editor.apply();
    }
}