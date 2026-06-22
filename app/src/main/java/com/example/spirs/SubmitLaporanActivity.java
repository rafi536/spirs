package com.example.spirs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SubmitLaporanActivity extends AppCompatActivity {

    private EditText etJudul, etLokasi, etDeskripsi, etKoordinat;
    private TextView tvFotoStatus;

    private Button btnSubmit;
    private ImageButton btnBack;

    private String judul, konten, alamat, koordinat, fotoUri;

    // DIJADIKAN FIELD CLASS
    private String fotoBase64 = "";

    private static final String URL_INSERT =
            "http://10.123.192.159/SPIRS/insert_laporan_full.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SESSION CHECK
        if (!SessionManager.isLogin(this)) {
            forceLogout();
            return;
        }

        setContentView(R.layout.activity_submitlaporan);

        initView();
        getIntentData();
        showPreview();

        btnBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> kirimKeServer());
    }

    private void initView() {
        etJudul = findViewById(R.id.etJudul);
        etLokasi = findViewById(R.id.etLokasi);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etKoordinat = findViewById(R.id.etKoordinat);

        tvFotoStatus = findViewById(R.id.tvFotoStatus);

        btnSubmit = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
    }

    private void getIntentData() {
        Intent i = getIntent();

        judul = i.getStringExtra("judul");
        konten = i.getStringExtra("konten");
        alamat = i.getStringExtra("alamat");
        koordinat = i.getStringExtra("koordinat");
        fotoUri = i.getStringExtra("fotoUri");
    }

    private void showPreview() {

        etJudul.setText(judul);
        etLokasi.setText(alamat);
        etDeskripsi.setText(konten);
        etKoordinat.setText(koordinat);

        etJudul.setEnabled(false);
        etLokasi.setEnabled(false);
        etDeskripsi.setEnabled(false);
        etKoordinat.setEnabled(false);

        if (fotoUri != null && !fotoUri.isEmpty()) {
            tvFotoStatus.setText("Foto sudah di upload");
        } else {
            tvFotoStatus.setText("Tidak ada foto");
        }
    }

    private void kirimKeServer() {

        if (judul == null || konten == null ||
                alamat == null || koordinat == null) {

            Toast.makeText(this,
                    "Data tidak lengkap",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = SessionManager.getUserId(this);

        if (userId <= 0) {
            Toast.makeText(this,
                    "Silakan login ulang",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Konversi foto ke Base64
        if (fotoUri != null && !fotoUri.isEmpty()) {
            fotoBase64 = convertImageToBase64(fotoUri);
        } else {
            fotoBase64 = "";
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Mengirim...");

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_INSERT,

                response -> {

                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("SUBMIT LAPORAN");

                    try {

                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status")
                                .equalsIgnoreCase("success")) {

                            String idLaporan =
                                    obj.optString("id_laporan", "UNKNOWN");

                            Toast.makeText(
                                    SubmitLaporanActivity.this,
                                    "Laporan berhasil dikirim",
                                    Toast.LENGTH_SHORT
                            ).show();

                            Intent intent = new Intent(
                                    SubmitLaporanActivity.this,
                                    LoadingSubmitActivity.class
                            );

                            intent.putExtra(
                                    "idLaporan",
                                    idLaporan
                            );

                            startActivity(intent);
                            finish();

                        } else {

                            Toast.makeText(
                                    SubmitLaporanActivity.this,
                                    obj.optString(
                                            "message",
                                            "Gagal mengirim laporan"
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                SubmitLaporanActivity.this,
                                "Response Error : " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                        e.printStackTrace();
                    }
                },

                error -> {

                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("SUBMIT LAPORAN");

                    Toast.makeText(
                            SubmitLaporanActivity.this,
                            "Koneksi Error : " + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }

        ) {

            @Override
            protected Map<String, String> getParams()
                    throws AuthFailureError {

                Map<String, String> p = new HashMap<>();

                p.put("user_id",
                        String.valueOf(userId));

                p.put("judul",
                        judul);

                p.put("konten",
                        konten);

                p.put("alamat",
                        alamat);

                p.put("koordinat",
                        koordinat);

                p.put("foto",
                        fotoBase64);

                return p;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private String convertImageToBase64(String uriString) {

        try {

            Uri uri = Uri.parse(uriString);

            InputStream inputStream =
                    getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                return "";
            }

            ByteArrayOutputStream buffer =
                    new ByteArrayOutputStream();

            byte[] data = new byte[1024];

            int nRead;

            while ((nRead = inputStream.read(data)) != -1) {
                buffer.write(data, 0, nRead);
            }

            inputStream.close();

            return Base64.encodeToString(
                    buffer.toByteArray(),
                    Base64.DEFAULT
            );

        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }
    }

    private void forceLogout() {

        SessionManager.logout(this);

        Intent intent =
                new Intent(
                        this,
                        LoginRegisterActivity.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}