package com.example.spirs;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class DetailLaporanActivity extends AppCompatActivity {

    private EditText etJudul, etLokasi, etDeskripsi, etKoordinat, etStatus;
    private ImageButton btnBack;
    private Button btnLihatFoto, btnEdit;

    private String imagePathGlobal = "";
    private int laporanId;
    private boolean isAdmin;

    private static final String BASE_URL =
            "http://10.123.192.159/SPIRS/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ================= SESSION VALIDATION =================
        if (!SessionManager.isLogin(this)) {
            forceLogout();
            return;
        }

        setContentView(R.layout.activity_detail_laporan);

        initView();

        // ================= ROLE =================
        String role = SessionManager.getRole(this);

        if (role == null || role.trim().isEmpty()) {
            forceLogout();
            return;
        }

        isAdmin = "admin".equalsIgnoreCase(role);

        // ================= GET ID =================
        laporanId = getIntent().getIntExtra("laporan_id", -1);

        Log.d("DETAIL_ID", "laporanId = " + laporanId);

        if (laporanId == -1) {

            Toast.makeText(
                    this,
                    "ID laporan tidak valid",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // ================= BACK HANDLER =================
        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        confirmBack();
                    }
                }
        );

        btnBack.setOnClickListener(v -> confirmBack());

        // ================= FOTO =================
        btnLihatFoto.setOnClickListener(v -> bukaFoto());

        // ================= ADMIN ONLY =================
        if (isAdmin) {

            btnEdit.setVisibility(View.VISIBLE);

            btnEdit.setOnClickListener(v -> {

                Intent intent = new Intent(
                        DetailLaporanActivity.this,
                        UpdateStatusLaporanActivity.class
                );

                intent.putExtra(
                        "laporan_id",
                        laporanId
                );

                startActivity(intent);
            });

        } else {

            btnEdit.setVisibility(View.GONE);
        }

        loadLaporan();
    }

    // ================= INIT VIEW =================
    private void initView() {

        etJudul = findViewById(R.id.etJudul);
        etLokasi = findViewById(R.id.etLokasi);
        etDeskripsi = findViewById(R.id.etDeskripsi);
        etKoordinat = findViewById(R.id.etKoordinat);
        etStatus = findViewById(R.id.etStatus);

        btnBack = findViewById(R.id.btnBack);
        btnLihatFoto = findViewById(R.id.btnLihatFoto);
        btnEdit = findViewById(R.id.btnEdit);

        // Debug null view
        if (etJudul == null ||
                etLokasi == null ||
                etDeskripsi == null ||
                etKoordinat == null ||
                etStatus == null ||
                btnBack == null ||
                btnLihatFoto == null ||
                btnEdit == null) {

            Toast.makeText(
                    this,
                    "Ada ID View yang tidak ditemukan",
                    Toast.LENGTH_LONG
            ).show();

            Log.e(
                    "DETAIL_VIEW",
                    "Salah satu View bernilai NULL"
            );
        }
    }

    // ================= LOAD DATA =================
    private void loadLaporan() {

        String url =
                BASE_URL +
                        "get_laporan_by_id.php?id=" +
                        laporanId;

        Log.d("DETAIL_URL", url);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,

                response -> {

                    Log.d(
                            "DETAIL_RESPONSE",
                            response
                    );

                    try {

                        if (response == null ||
                                response.trim().isEmpty()) {

                            Toast.makeText(
                                    this,
                                    "Response kosong dari server",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        JSONObject obj =
                                new JSONObject(response);

                        String status =
                                obj.optString(
                                        "status",
                                        "error"
                                );

                        if ("success".equalsIgnoreCase(status)) {

                            JSONObject data =
                                    obj.optJSONObject("data");

                            if (data == null) {

                                Toast.makeText(
                                        this,
                                        "Data laporan kosong",
                                        Toast.LENGTH_LONG
                                ).show();

                                return;
                            }

                            etJudul.setText(
                                    data.optString(
                                            "judul",
                                            "-"
                                    )
                            );

                            etLokasi.setText(
                                    data.optString(
                                            "alamat",
                                            "-"
                                    )
                            );

                            etDeskripsi.setText(
                                    data.optString(
                                            "konten",
                                            "-"
                                    )
                            );

                            etKoordinat.setText(
                                    data.optString(
                                            "koordinat",
                                            "-"
                                    )
                            );

                            etStatus.setText(
                                    data.optString(
                                            "status",
                                            "-"
                                    )
                            );

                            imagePathGlobal =
                                    data.optString(
                                            "foto",
                                            ""
                                    );

                        } else {

                            Toast.makeText(
                                    this,
                                    obj.optString(
                                            "message",
                                            "Data tidak ditemukan"
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (Exception e) {

                        Log.e(
                                "DETAIL_PARSE_ERROR",
                                response,
                                e
                        );

                        Toast.makeText(
                                this,
                                "Parse Error : "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },

                error -> {

                    Log.e(
                            "DETAIL_VOLLEY_ERROR",
                            error.toString()
                    );

                    Toast.makeText(
                            this,
                            "Koneksi Error : "
                                    + error.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ================= VIEW FOTO =================
    private void bukaFoto() {

        if (imagePathGlobal == null ||
                imagePathGlobal.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Foto tidak tersedia",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        Intent intent = new Intent(
                DetailLaporanActivity.this,
                ViewImageActivity.class
        );

        intent.putExtra(
                "fotoUri",
                imagePathGlobal
        );

        startActivity(intent);
    }

    // ================= BACK =================
    private void confirmBack() {

        new AlertDialog.Builder(this)
                .setTitle("Kembali")
                .setMessage(
                        "Yakin ingin kembali ke halaman sebelumnya?"
                )
                .setPositiveButton(
                        "Ya",
                        (dialog, which) -> finish()
                )
                .setNegativeButton(
                        "Tidak",
                        null
                )
                .show();
    }

    // ================= LOGOUT =================
    private void forceLogout() {

        SessionManager.logout(this);

        Intent intent = new Intent(
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