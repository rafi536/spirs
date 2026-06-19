package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class InputDeskripsiActivity extends AppCompatActivity {

    private EditText etJudul, etKonten;
    private Button btnNext;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inputdeskripsi);

        etJudul = findViewById(R.id.etJudul);
        etKonten = findViewById(R.id.etKonten);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnNext.setOnClickListener(v -> lanjut());
    }

    private void lanjut() {

        String judul = etJudul.getText().toString().trim();
        String konten = etKonten.getText().toString().trim();

        if (judul.isEmpty()) {
            etJudul.setError("Judul wajib diisi");
            return;
        }

        if (konten.isEmpty()) {
            etKonten.setError("Deskripsi wajib diisi");
            return;
        }

        Intent intent = new Intent(this, UploadFotoActivity.class);
        intent.putExtra("judul", judul);
        intent.putExtra("konten", konten);

        startActivity(intent);
    }
}