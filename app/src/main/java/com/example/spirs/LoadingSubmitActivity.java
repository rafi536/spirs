package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoadingSubmitActivity extends AppCompatActivity {

    private Handler handler;
    private Runnable runnable;

    private String idLaporan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_submit);

        handler = new Handler(Looper.getMainLooper());

        // ================= AMBIL DATA DARI INTENT =================
        idLaporan = getIntent().getStringExtra("idLaporan");

        if (idLaporan == null || idLaporan.trim().isEmpty()) {
            idLaporan = "UNKNOWN-" + System.currentTimeMillis();
        }

        // ================= DELAY NAVIGATION =================
        runnable = () -> {

            if (isFinishing() || isDestroyed()) return;

            Intent intent = new Intent(
                    LoadingSubmitActivity.this,
                    SuccessSubmitActivity.class
            );

            intent.putExtra("idLaporan", idLaporan);
            startActivity(intent);

            finish();
        };

        handler.postDelayed(runnable, 2000); // 2 detik (lebih smooth UX)
    }

    // ================= MEMORY SAFE =================
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}