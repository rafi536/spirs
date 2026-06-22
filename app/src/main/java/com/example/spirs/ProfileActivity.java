package com.example.spirs;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvNamaUser;
    private EditText etAlamat;

    private ImageButton btnBack;
    private Button btnLogout;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvNamaUser = findViewById(R.id.tvNamaUser);
        etAlamat = findViewById(R.id.etAlamat);

        btnBack = findViewById(R.id.btnBack);
        btnLogout = findViewById(R.id.btnLogout);

        databaseHelper = new DatabaseHelper(this);

        loadProfile();

        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {

            SessionManager.logout(ProfileActivity.this);

            Intent intent =
                    new Intent(
                            ProfileActivity.this,
                            LoginActivity.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {

        String email =
                SessionManager.getEmail(this);

        Cursor cursor =
                databaseHelper.getUser(email);

        if (cursor != null && cursor.moveToFirst()) {

            String nama =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_NAMA
                            )
                    );

            String alamat =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    DatabaseHelper.COL_ALAMAT
                            )
                    );

            tvNamaUser.setText(nama);
            etAlamat.setText(alamat);
        }

        if (cursor != null) {
            cursor.close();
        }
    }
}