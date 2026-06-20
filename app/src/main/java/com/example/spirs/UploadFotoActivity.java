package com.example.spirs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class UploadFotoActivity extends AppCompatActivity {

    private LinearLayout layoutGaleri, layoutKamera;
    private Button btnNext;
    private ImageButton btnBack;

    private String fotoUri = "";

    private String judul, konten;

    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        fotoUri = uri.toString();
                        Toast.makeText(this, "Foto dipilih", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadfoto);

        layoutGaleri = findViewById(R.id.layoutGaleri);
        layoutKamera = findViewById(R.id.layoutKamera);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        judul = getIntent().getStringExtra("judul");
        konten = getIntent().getStringExtra("konten");

        btnBack.setOnClickListener(v -> finish());

        layoutGaleri.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        layoutKamera.setOnClickListener(v ->
                Toast.makeText(this, "Kamera belum diaktifkan", Toast.LENGTH_SHORT).show()
        );

        btnNext.setOnClickListener(v -> lanjut());
    }

    private void lanjut() {

        if (fotoUri.isEmpty()) {
            Toast.makeText(this, "Pilih foto dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, LokasiActivity.class);
        intent.putExtra("judul", judul);
        intent.putExtra("konten", konten);
        intent.putExtra("fotoUri", fotoUri);

        startActivity(intent);
    }
}