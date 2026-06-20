package com.example.spirs;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ValidasiInputActivity extends AppCompatActivity {

    private TextView tvNamaLaporan;
    private TextView tvIdLaporan;
    private TextView tvTanggal;
    private TextView tvStatusValidasi;
    private TextView tvIdHasil;

    private Button btnSimpan;
    private ImageButton btnBack;

    private int laporanId = -1;
    private String statusBaru = "";

    private static final String BASE_URL =
            "http://10.123.192.159/SPIRS/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= SESSION VALIDATION (ADMIN ONLY) =================
        String role = SessionManager.getRole(this);

        if (role == null || !role.equalsIgnoreCase("admin")) {
            Toast.makeText(this, "Akses ditolak! Hanya admin", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_validasi_input);

        initView();

        Intent intent = getIntent();
        laporanId = intent.getIntExtra("laporan_id", -1);
        statusBaru = intent.getStringExtra("statusBaru");

        if (laporanId == -1) {
            Toast.makeText(this, "ID laporan tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDataLaporan();

        btnBack.setOnClickListener(v -> finish());
        btnSimpan.setOnClickListener(v -> updateStatus());
    }

    private void initView() {
        tvNamaLaporan = findViewById(R.id.tvNamaLaporan);
        tvIdLaporan = findViewById(R.id.tvIdLaporan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvStatusValidasi = findViewById(R.id.tvStatusValidasi);
        tvIdHasil = findViewById(R.id.tvIdHasil);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnBack = findViewById(R.id.btnBack);
    }

    // ================= AMBIL DETAIL LAPORAN =================
    private void loadDataLaporan() {

        String url = BASE_URL + "get_laporan_by_id.php?id=" + laporanId;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            JSONObject data = obj.getJSONObject("data");

                            tvNamaLaporan.setText("Nama Laporan : " + data.getString("judul"));
                            tvIdLaporan.setText("ID Laporan : LP-" + data.getInt("id"));
                            tvTanggal.setText("Tanggal Pembuatan : " + data.getString("tanggal"));
                            tvStatusValidasi.setText("Status Baru : " + statusBaru);
                            tvIdHasil.setText("ID Hasil : LP-" + data.getInt("id"));

                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Parse Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> Toast.makeText(this, "Koneksi Error : " + error.getMessage(), Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ================= UPDATE STATUS LAPORAN =================
    private void updateStatus() {

        int adminId = SessionManager.getUserId(this);

        if (adminId <= 0) {
            Toast.makeText(this, "Session admin tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnSimpan.setEnabled(false);

        String url = BASE_URL + "update_status_laporan.php";

        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    btnSimpan.setEnabled(true);

                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            Toast.makeText(this, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, DaftarLaporanActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Response Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    btnSimpan.setEnabled(true);
                    Toast.makeText(this, "Koneksi Error : " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("laporan_id", String.valueOf(laporanId));
                params.put("admin_id", String.valueOf(adminId));
                params.put("status_baru", statusBaru);

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}