package com.example.spirs;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "SPIRS_SESSION";

    private static final String KEY_LOGIN = "is_login";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAMA = "nama";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";

    // ================= SAVE LOGIN =================
    public static void saveLogin(
            Context context,
            int userId,
            String nama,
            String email,
            String role
    ) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(KEY_LOGIN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_NAMA, nama != null ? nama : "");
        editor.putString(KEY_EMAIL, email != null ? email : "");
        editor.putString(KEY_ROLE, role != null ? role : "user");

        // lebih aman untuk UI Android
        editor.apply();
    }

    // ================= CHECK LOGIN =================
    public static boolean isLogin(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        return sp.getBoolean(KEY_LOGIN, false);
    }

    // ================= GET USER ID =================
    public static int getUserId(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        return sp.getInt(KEY_USER_ID, -1);
    }

    // ================= GET NAMA =================
    public static String getNama(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        return sp.getString(KEY_NAMA, "");
    }

    // ================= GET EMAIL =================
    public static String getEmail(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        return sp.getString(KEY_EMAIL, "");
    }

    // ================= GET ROLE (FIXED SAFE) =================
    public static String getRole(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        String role = sp.getString(KEY_ROLE, "user");

        // safety cleanup
        if (role == null || role.trim().isEmpty()) {
            return "user";
        }

        return role;
    }

    // ================= LOGOUT =================
    public static void logout(Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }
}