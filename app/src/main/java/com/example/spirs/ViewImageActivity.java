package com.example.spirs;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewImageActivity extends AppCompatActivity {

    private static final String TAG = "ViewImageActivity";

    private ImageView imageViewFull;

    private static final String BASE_URL =
            "http://10.123.192.159/SPIRS/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        imageViewFull = findViewById(R.id.imageViewFull);

        String fotoUri = getIntent().getStringExtra("fotoUri");

        Log.d(TAG, "fotoUri diterima = " + fotoUri);

        if (fotoUri == null || fotoUri.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Foto tidak tersedia",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        tampilkanFoto(fotoUri.trim());
    }

    private void tampilkanFoto(String fotoPath) {

        try {

            String finalUrl;

            // Jika sudah URL lengkap
            if (fotoPath.startsWith("http://")
                    || fotoPath.startsWith("https://")) {

                finalUrl = fotoPath;

            } else {

                // Jika database menyimpan:
                // uploads/1781594890_3935.jpg
                finalUrl = BASE_URL + fotoPath;
            }

            Log.d(TAG, "FINAL URL = " + finalUrl);

            Toast.makeText(
                    this,
                    finalUrl,
                    Toast.LENGTH_LONG
            ).show();

            Glide.with(this)
                    .load(finalUrl)
                    .placeholder(R.drawable.picture)
                    .error(R.drawable.picture)
                    .into(imageViewFull);

        } catch (Exception e) {

            Log.e(TAG,
                    "Error tampilkan foto",
                    e);

            Toast.makeText(
                    this,
                    "Gagal menampilkan foto : " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}