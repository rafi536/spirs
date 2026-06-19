package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BerandaActivity extends AppCompatActivity {

    private LinearLayout menuBuatLaporan;
    private LinearLayout menuDaftarLaporan;
    private LinearLayout menuProfile;
    private TextView tvHiUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        menuBuatLaporan = findViewById(R.id.menuBuatLaporan);
        menuDaftarLaporan = findViewById(R.id.menuDaftarLaporan);
        menuProfile = findViewById(R.id.menuProfile);
        tvHiUser = findViewById(R.id.tvHiUser);

        String namaUser = SessionManager.getNama(this);

        if (namaUser != null && !namaUser.isEmpty()) {
            tvHiUser.setText("Hi, " + namaUser + " 👋");
        }

        // ================= MENU BUAT LAPORAN =================
        menuBuatLaporan.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BerandaActivity.this,
                    InputDeskripsiActivity.class
            );
            startActivity(intent);
        });

        // ================= MENU DAFTAR LAPORAN =================
        menuDaftarLaporan.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BerandaActivity.this,
                    DaftarLaporanActivity.class
            );
            startActivity(intent);
        });

        // ================= MENU PROFILE =================
        menuProfile.setOnClickListener(v -> {
            Intent intent = new Intent(
                    BerandaActivity.this,
                    ProfileActivity.class
            );
            startActivity(intent);
        });
    }
}