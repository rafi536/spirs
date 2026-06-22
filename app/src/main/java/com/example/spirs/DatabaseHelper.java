package com.example.spirs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SPIRS_DB.db";
    public static final int DATABASE_VERSION = 2;

    // =========================
    // USERS TABLE
    // =========================
    public static final String TABLE_USERS = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAMA = "nama";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ALAMAT = "alamat";

    // =========================
    // LAPORAN TABLE
    // =========================
    public static final String TABLE_LAPORAN = "laporan";
    public static final String LAP_ID = "lap_id";
    public static final String LAP_JUDUL = "judul";
    public static final String LAP_DESKRIPSI = "deskripsi";
    public static final String LAP_LOKASI = "lokasi";
    public static final String LAP_KOORDINAT = "koordinat";
    public static final String LAP_FOTO = "foto_uri";
    public static final String LAP_STATUS = "status";
    public static final String LAP_USER = "nama_user";
    public static final String LAP_TANGGAL = "tanggal";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COL_NAMA + " TEXT NOT NULL," +
                        COL_EMAIL + " TEXT UNIQUE NOT NULL," +
                        COL_PASSWORD + " TEXT NOT NULL," +
                        COL_ALAMAT + " TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE " + TABLE_LAPORAN + " (" +
                        LAP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        LAP_JUDUL + " TEXT," +
                        LAP_DESKRIPSI + " TEXT," +
                        LAP_LOKASI + " TEXT," +
                        LAP_KOORDINAT + " TEXT," +
                        LAP_FOTO + " TEXT," +
                        LAP_STATUS + " TEXT," +
                        LAP_USER + " TEXT," +
                        LAP_TANGGAL + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LAPORAN);
        onCreate(db);
    }

    // =====================================================
    // 🔥 USER SYSTEM
    // =====================================================

    public boolean registerUser(String nama, String email, String password, String alamat) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_NAMA, nama);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_ALAMAT, alamat);

        return db.insert(TABLE_USERS, null, values) != -1;
    }

    // =========================
    // 🔥 FIX: EMAIL CHECK (YANG KAMU ERROR)
    // =========================
    public boolean isEmailExists(String email) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE email=?",
                new String[]{email}
        );

        boolean exists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) cursor.close();

        return exists;
    }

    // =========================
    // LOGIN USER
    // =========================
    public boolean loginUser(String email, String password) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE email=? AND password=?",
                new String[]{email, password}
        );

        boolean success = cursor != null && cursor.getCount() > 0;

        if (cursor != null) cursor.close();

        return success;
    }

    // =========================
    // GET USER
    // =========================
    public Cursor getUser(String email) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_USERS + " WHERE email=?",
                new String[]{email}
        );
    }
    // =========================
// SAVE / UPDATE USER
// =========================
    public boolean saveOrUpdateUser(
            String nama,
            String email,
            String password,
            String alamat
    ) {

        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                        " WHERE email=?",
                new String[]{email}
        );

        ContentValues values = new ContentValues();
        values.put(COL_NAMA, nama);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        values.put(COL_ALAMAT, alamat);

        boolean result;

        if (cursor != null && cursor.moveToFirst()) {

            result = db.update(
                    TABLE_USERS,
                    values,
                    COL_EMAIL + "=?",
                    new String[]{email}
            ) > 0;

        } else {

            result = db.insert(
                    TABLE_USERS,
                    null,
                    values
            ) != -1;
        }

        if (cursor != null) {
            cursor.close();
        }

        return result;
    }

    // =====================================================
    // LAPORAN SYSTEM
    // =====================================================

    public boolean insertLaporan(String judul, String deskripsi, String lokasi,
                                 String koordinat, String fotoUri,
                                 String status, String namaUser, String tanggal) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LAP_JUDUL, judul);
        values.put(LAP_DESKRIPSI, deskripsi);
        values.put(LAP_LOKASI, lokasi);
        values.put(LAP_KOORDINAT, koordinat);
        values.put(LAP_FOTO, fotoUri == null ? "" : fotoUri);
        values.put(LAP_STATUS, status);
        values.put(LAP_USER, namaUser);
        values.put(LAP_TANGGAL, tanggal);

        return db.insert(TABLE_LAPORAN, null, values) != -1;
    }

    // =========================
    // GET ALL LAPORAN
    // =========================
    public ArrayList<Laporan> getAllLaporanList() {

        ArrayList<Laporan> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_LAPORAN + " ORDER BY " + LAP_ID + " DESC",
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {

                Laporan l = new Laporan();

                l.setId(cursor.getInt(cursor.getColumnIndexOrThrow(LAP_ID)));
                l.setJudul(cursor.getString(cursor.getColumnIndexOrThrow(LAP_JUDUL)));
                l.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow(LAP_DESKRIPSI)));
                l.setLokasi(cursor.getString(cursor.getColumnIndexOrThrow(LAP_LOKASI)));
                l.setKoordinat(cursor.getString(cursor.getColumnIndexOrThrow(LAP_KOORDINAT)));
                l.setFotoUri(cursor.getString(cursor.getColumnIndexOrThrow(LAP_FOTO)));
                l.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(LAP_STATUS)));
                l.setNamaUser(cursor.getString(cursor.getColumnIndexOrThrow(LAP_USER)));
                l.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(LAP_TANGGAL)));

                list.add(l);

            } while (cursor.moveToNext());
        }

        if (cursor != null) cursor.close();

        return list;
    }

    // =========================
    // GET BY ID
    // =========================
    public Laporan getLaporanObjectById(int id) {

        SQLiteDatabase db = getReadableDatabase();
        Laporan l = null;

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_LAPORAN + " WHERE " + LAP_ID + "=?",
                new String[]{String.valueOf(id)}
        );

        if (cursor != null && cursor.moveToFirst()) {

            l = new Laporan();

            l.setId(cursor.getInt(cursor.getColumnIndexOrThrow(LAP_ID)));
            l.setJudul(cursor.getString(cursor.getColumnIndexOrThrow(LAP_JUDUL)));
            l.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow(LAP_DESKRIPSI)));
            l.setLokasi(cursor.getString(cursor.getColumnIndexOrThrow(LAP_LOKASI)));
            l.setKoordinat(cursor.getString(cursor.getColumnIndexOrThrow(LAP_KOORDINAT)));
            l.setFotoUri(cursor.getString(cursor.getColumnIndexOrThrow(LAP_FOTO)));
            l.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(LAP_STATUS)));
            l.setNamaUser(cursor.getString(cursor.getColumnIndexOrThrow(LAP_USER)));
            l.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(LAP_TANGGAL)));
        }

        if (cursor != null) cursor.close();

        return l;
    }

    // =========================
    // UPDATE STATUS
    // =========================
    public boolean updateStatusLaporan(int id, String status) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LAP_STATUS, status);

        return db.update(
                TABLE_LAPORAN,
                values,
                LAP_ID + "=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // =========================
    // DELETE LAPORAN
    // =========================
    public boolean deleteLaporan(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_LAPORAN,
                LAP_ID + "=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
}