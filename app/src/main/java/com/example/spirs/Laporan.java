package com.example.spirs;

import java.io.Serializable;

public class Laporan implements Serializable {

    private int id;

    private String judul;
    private String deskripsi;
    private String lokasi;
    private String koordinat;

    // Foto
    private String fotoUri;
    private String imageBase64;

    private String status;
    private String namaUser;
    private String tanggal;

    // =========================
    // CONSTRUCTOR DEFAULT
    // =========================

    public Laporan() {
    }

    // =========================
    // CONSTRUCTOR DENGAN ID
    // =========================

    public Laporan(
            int id,
            String judul,
            String deskripsi,
            String lokasi,
            String koordinat,
            String fotoUri,
            String status,
            String namaUser,
            String tanggal
    ) {
        this.id = id;
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.koordinat = koordinat;
        this.fotoUri = fotoUri;
        this.status = status;
        this.namaUser = namaUser;
        this.tanggal = tanggal;
    }

    // =========================
    // CONSTRUCTOR TANPA ID
    // =========================

    public Laporan(
            String judul,
            String deskripsi,
            String lokasi,
            String koordinat,
            String fotoUri,
            String status,
            String namaUser,
            String tanggal
    ) {
        this.judul = judul;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.koordinat = koordinat;
        this.fotoUri = fotoUri;
        this.status = status;
        this.namaUser = namaUser;
        this.tanggal = tanggal;
    }

    // =========================
    // GETTER & SETTER
    // =========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getKoordinat() {
        return koordinat;
    }

    public void setKoordinat(String koordinat) {
        this.koordinat = koordinat;
    }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    // =========================
    // ALIAS UNTUK MYSQL API
    // =========================
    // Database menggunakan:
    // konten, alamat, foto

    public String getKonten() {
        return deskripsi;
    }

    public void setKonten(String konten) {
        this.deskripsi = konten;
    }

    public String getAlamat() {
        return lokasi;
    }

    public void setAlamat(String alamat) {
        this.lokasi = alamat;
    }

    public String getFoto() {
        return fotoUri;
    }

    public void setFoto(String foto) {
        this.fotoUri = foto;
    }

    // =========================
    // SAFE IMAGE
    // =========================

    public String getSafeImage() {

        if (fotoUri != null && !fotoUri.trim().isEmpty()) {
            return fotoUri;
        }

        if (imageBase64 != null && !imageBase64.trim().isEmpty()) {
            return imageBase64;
        }

        return "";
    }

    public String getSafeFoto() {
        return getSafeImage();
    }

    // =========================
    // DEBUG
    // =========================

    @Override
    public String toString() {
        return "Laporan{" +
                "id=" + id +
                ", judul='" + judul + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                ", lokasi='" + lokasi + '\'' +
                ", koordinat='" + koordinat + '\'' +
                ", fotoUri='" + fotoUri + '\'' +
                ", imageBase64='" + imageBase64 + '\'' +
                ", status='" + status + '\'' +
                ", namaUser='" + namaUser + '\'' +
                ", tanggal='" + tanggal + '\'' +
                '}';
    }
}