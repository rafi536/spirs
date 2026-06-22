package com.example.spirs;

import android.content.Intent;
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

public class UpdateStatusLaporanActivity extends AppCompatActivity {

    private TextView tvNamaLaporan, tvIdLaporan, tvTanggal, tvStatusSaat;
    private Spinner spStatus;
    private Button btnSimpan;
    private ImageButton btnBack, btnLihatFoto;

    private int laporanId;
    private String statusLama = "";
    private String imagePathGlobal = "";

    private static final String BASE_URL =
            "http://10.123.192.159/SPIRS/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= SESSION CHECK =================
        if (!SessionManager.isLogin(this)) {
            forceLogout();
            return;
        }

        String role = SessionManager.getRole(this);

        if (role == null || !role.equalsIgnoreCase("admin")) {
            Toast.makeText(this,
                    "Akses ditolak: hanya admin",
                    Toast.LENGTH_SHORT).show();

            forceLogout();
            return;
        }

        setContentView(R.layout.activity_update_status_laporan);

        initView();
        setupSpinner();

        laporanId = getIntent().getIntExtra("laporan_id", -1);

        if (laporanId == -1) {
            Toast.makeText(this, "ID laporan tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadDataDariServer();

        btnBack.setOnClickListener(v -> finish());

        btnLihatFoto.setOnClickListener(v -> {
            if (imagePathGlobal != null && !imagePathGlobal.isEmpty()) {
                Intent intent = new Intent(this, ViewImageActivity.class);
                intent.putExtra("fotoUri", imagePathGlobal);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Foto tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        btnSimpan.setOnClickListener(v -> simpanStatus());
    }

    // ================= FORCE LOGOUT =================
    private void forceLogout() {
        SessionManager.logout(this);

        Intent intent = new Intent(this, LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish();
    }

    private void initView() {
        tvNamaLaporan = findViewById(R.id.tvNamaLaporan);
        tvIdLaporan = findViewById(R.id.tvIdLaporan);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvStatusSaat = findViewById(R.id.tvStatusSaat);

        spStatus = findViewById(R.id.spStatus);

        btnSimpan = findViewById(R.id.btnSimpan);
        btnBack = findViewById(R.id.btnBack);
        btnLihatFoto = findViewById(R.id.btnLihatFoto);
    }

    private void setupSpinner() {
        String[] statusList = {
                "Menunggu Verifikasi",
                "Validasi Input",
                "Survey Lapangan",
                "Dalam Proses",
                "Selesai"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                statusList
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spStatus.setAdapter(adapter);
    }

    private void loadDataDariServer() {

        String url = BASE_URL + "get_laporan_by_id.php?id=" + laporanId;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        if (obj.getString("status").equals("success")) {

                            JSONObject data = obj.getJSONObject("data");

                            statusLama = data.getString("status");
                            imagePathGlobal = data.optString("foto", "");

                            tvNamaLaporan.setText("Nama: " + data.getString("judul"));
                            tvIdLaporan.setText("ID: LP-" + data.getString("id"));
                            tvTanggal.setText("Tanggal: " + data.getString("tanggal"));
                            tvStatusSaat.setText("Status: " + statusLama);

                            ArrayAdapter<String> adapter =
                                    (ArrayAdapter<String>) spStatus.getAdapter();

                            int pos = adapter.getPosition(statusLama);
                            if (pos >= 0) spStatus.setSelection(pos);

                        } else {
                            Toast.makeText(this, "Laporan tidak ditemukan", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Koneksi error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void simpanStatus() {

        String statusBaru = spStatus.getSelectedItem().toString();

        if (statusBaru.equals(statusLama)) {
            Toast.makeText(this, "Status belum berubah", Toast.LENGTH_SHORT).show();
            return;
        }

        int adminId = SessionManager.getUserId(this);

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

                            Toast.makeText(this,
                                    "Status berhasil diperbarui",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, DaftarLaporanActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(this, "Parse error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    btnSimpan.setEnabled(true);
                    Toast.makeText(this, "Koneksi error", Toast.LENGTH_SHORT).show();
                }
        ) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> p = new HashMap<>();

                p.put("laporan_id", String.valueOf(laporanId));
                p.put("admin_id", String.valueOf(adminId));
                p.put("status_baru", statusBaru);

                return p;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}