package cn.rongcloud.im.utils;

import android.content.SharedPreferences;

/**
 * Created by nakisaRen
 * on 16/9/8.
 */
public class SPUtils {
    private static SharedPreferences mSp;


    public static void init(SharedPreferences sharedPreferences) {
        mSp = sharedPreferences;
    }


    public static void updateOrSave(String key, String json) {
        mSp.edit().putString(key, json).apply();
    }
    public static void updateOrSave(String key, boolean flag) {
        mSp.edit().putBoolean(key, flag).apply();
    }

    public static boolean findBoolean(String key) {

        return mSp.getBoolean(key, true);
    }

    public static void updateOrSaveInt(String key, int json) {
        mSp.edit().putInt(key,json).apply();
    }


    public static String find(String key) {
        return mSp.getString(key, "zh");
    }


    public static int findInt(String key) {
        return mSp.getInt(key, 0);
    }


    public static void delete(String key) {
        mSp.edit().remove(key).apply();
    }


    public static void clearAll() {
        mSp.edit().clear().apply();
    }
}
