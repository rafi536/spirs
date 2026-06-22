package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BerandaAdminActivity extends AppCompatActivity {

    private LinearLayout menuDaftarLaporan;
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= SESSION VALIDATION (ADMIN ONLY) =================
        if (!SessionManager.isLogin(this)) {
            forceLogout();
            return;
        }

        String role = SessionManager.getRole(this);

        if (role == null || !role.equalsIgnoreCase("admin")) {
            Toast.makeText(this,
                    "Akses ditolak: bukan admin",
                    Toast.LENGTH_SHORT).show();

            forceLogout();
            return;
        }

        setContentView(R.layout.activity_beranda_admin);

        initView();
        setupListener();
    }

    // ================= INIT VIEW =================
    private void initView() {
        menuDaftarLaporan = findViewById(R.id.menuDaftarLaporan);
        btnLogOut = findViewById(R.id.btnLogOut);
    }

    // ================= CLICK LISTENER =================
    private void setupListener() {

        menuDaftarLaporan.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BerandaAdminActivity.this,
                    DaftarLaporanActivity.class
            );
            startActivity(intent);
        });

        btnLogOut.setOnClickListener(v -> logout());
    }

    // ================= FORCE LOGOUT =================
    private void forceLogout() {

        SessionManager.logout(this);

        Intent intent = new Intent(
                this,
                LoginRegisterActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    // ================= MANUAL LOGOUT =================
    private void logout() {
        forceLogout();
    }
}