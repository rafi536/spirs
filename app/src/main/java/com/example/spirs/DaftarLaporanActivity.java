package com.example.spirs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DaftarLaporanActivity extends AppCompatActivity {

    private RecyclerView rvDaftarLaporan;
    private ImageButton btnBack;

    private ArrayList<Laporan> laporanList;
    private LaporanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_laporan);

        rvDaftarLaporan = findViewById(R.id.rvDaftarLaporan);
        btnBack = findViewById(R.id.btnBack);

        laporanList = new ArrayList<>();

        rvDaftarLaporan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        boolean isAdmin =
                "admin".equalsIgnoreCase(
                        SessionManager.getRole(this)
                );

        adapter = new LaporanAdapter(
                this,
                laporanList,
                isAdmin
        );

        rvDaftarLaporan.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {

        int userId =
                SessionManager.getUserId(this);

        String role =
                SessionManager.getRole(this);

        String url =
                "http://10.123.192.159/SPIRS/get_laporan.php" +
                        "?user_id=" + userId +
                        "&role=" + role;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                response -> {

                    try {

                        JSONObject obj =
                                new JSONObject(response);

                        if (obj.getString("status")
                                .equals("success")) {

                            laporanList.clear();

                            JSONArray data =
                                    obj.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {

                                JSONObject item =
                                        data.getJSONObject(i);

                                Laporan laporan =
                                        new Laporan();

                                laporan.setId(
                                        item.optInt("id")
                                );

                                laporan.setJudul(
                                        item.optString("judul")
                                );

                                laporan.setDeskripsi(
                                        item.optString("konten")
                                );

                                laporan.setLokasi(
                                        item.optString("alamat")
                                );

                                laporan.setKoordinat(
                                        item.optString("koordinat")
                                );

                                laporan.setFotoUri(
                                        item.optString("foto")
                                );

                                laporan.setStatus(
                                        item.optString("status")
                                );

                                laporan.setTanggal(
                                        item.optString("tanggal")
                                );

                                laporanList.add(laporan);
                            }

                            boolean isAdmin =
                                    "admin".equalsIgnoreCase(
                                            SessionManager.getRole(
                                                    DaftarLaporanActivity.this
                                            )
                                    );

                            adapter.updateData(
                                    laporanList,
                                    isAdmin
                            );

                            adapter.notifyDataSetChanged();

                            if (laporanList.isEmpty()) {

                                rvDaftarLaporan.setVisibility(
                                        View.GONE
                                );

                                Toast.makeText(
                                        this,
                                        "Belum ada laporan",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                rvDaftarLaporan.setVisibility(
                                        View.VISIBLE
                                );
                            }

                        } else {

                            Toast.makeText(
                                    this,
                                    obj.optString(
                                            "message",
                                            "Gagal mengambil data"
                                    ),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Parse Error : " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> Toast.makeText(
                        this,
                        "Koneksi Error : " + error.getMessage(),
                        Toast.LENGTH_LONG
                ).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}