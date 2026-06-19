package com.example.spirs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Locale;

public class LokasiActivity extends AppCompatActivity {

    private EditText etAlamat, etKoordinat;
    private Button btnNext;
    private ImageView btnBack;

    private FusedLocationProviderClient fusedLocationClient;

    private String judul, konten, fotoUri;
    private String alamat = "", koordinat = "";

    private static final int LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lokasi);

        etAlamat = findViewById(R.id.etAlamat);
        etKoordinat = findViewById(R.id.etKoordinat);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        judul = getIntent().getStringExtra("judul");
        konten = getIntent().getStringExtra("konten");
        fotoUri = getIntent().getStringExtra("fotoUri");

        btnBack.setOnClickListener(v -> finish());

        getLocation();

        btnNext.setOnClickListener(v -> lanjut());
    }

    // ================= GET LOCATION =================
    private void getLocation() {

        LocationManager locationManager =
                (LocationManager) getSystemService(LOCATION_SERVICE);

        boolean gpsEnabled =
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            Toast.makeText(this,
                    "Aktifkan GPS terlebih dahulu",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_REQUEST_CODE
            );
            return;
        }

        fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
        ).addOnSuccessListener(location -> {

            if (location != null) {

                double lat = location.getLatitude();
                double lng = location.getLongitude();

                koordinat = lat + ", " + lng;
                etKoordinat.setText(koordinat);

                // 🔥 AUTO AMBIL ALAMAT
                getAlamatFromLocation(lat, lng);

                Toast.makeText(this,
                        "Lokasi berhasil didapat",
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this,
                        "Lokasi tidak ditemukan. Coba aktifkan GPS.",
                        Toast.LENGTH_LONG).show();
            }

        }).addOnFailureListener(e ->
                Toast.makeText(this,
                        "Gagal mendapatkan lokasi",
                        Toast.LENGTH_LONG).show()
        );
    }

    // ================= REVERSE GEOCODING =================
    private void getAlamatFromLocation(double lat, double lng) {

        try {
            Geocoder geocoder =
                    new Geocoder(this, Locale.getDefault());

            List<Address> addresses =
                    geocoder.getFromLocation(lat, lng, 1);

            if (addresses != null && !addresses.isEmpty()) {

                Address address = addresses.get(0);

                String fullAddress =
                        address.getAddressLine(0);

                etAlamat.setText(fullAddress);
                alamat = fullAddress;

            } else {
                etAlamat.setText("Alamat tidak ditemukan");
            }

        } catch (Exception e) {
            etAlamat.setText("Gagal mengambil alamat");
        }
    }

    // ================= PERMISSION RESULT =================
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getLocation();

            } else {
                Toast.makeText(this,
                        "Izin lokasi ditolak",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ================= NEXT =================
    private void lanjut() {

        alamat = etAlamat.getText().toString().trim();
        koordinat = etKoordinat.getText().toString().trim();

        if (alamat.isEmpty() || koordinat.isEmpty()) {
            Toast.makeText(this,
                    "Lengkapi lokasi terlebih dahulu",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent =
                new Intent(this, SubmitLaporanActivity.class);

        intent.putExtra("judul", judul);
        intent.putExtra("konten", konten);
        intent.putExtra("fotoUri", fotoUri);
        intent.putExtra("alamat", alamat);
        intent.putExtra("koordinat", koordinat);

        startActivity(intent);
    }
}