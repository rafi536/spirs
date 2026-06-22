package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuccessSubmitActivity extends AppCompatActivity {

    private TextView tvIdLaporan;
    private Button btnBeranda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_submit);

        tvIdLaporan = findViewById(R.id.tvIdLaporan);
        btnBeranda = findViewById(R.id.btnBeranda);

        // ================= AMBIL DATA =================
        String idLaporan = getIntent().getStringExtra("idLaporan");

        // ================= SAFETY CHECK =================
        if (idLaporan == null || idLaporan.trim().isEmpty()) {
            idLaporan = "ID tidak tersedia";
        }

        tvIdLaporan.setText("ID LAPORAN: " + idLaporan);

        // ================= BUTTON HOME =================
        btnBeranda.setOnClickListener(v -> {

            Intent intent = new Intent(
                    SuccessSubmitActivity.this,
                    BerandaActivity.class
            );

            // bersihkan stack biar tidak bisa back ke loading
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }
}