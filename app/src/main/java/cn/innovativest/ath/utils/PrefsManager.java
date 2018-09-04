package cn.innovativest.ath.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsManager {

    private static PrefsManager my;
    private Context mCtx;
    private SharedPreferences mPrefs;

    private PrefsManager(Context ctx) {
        this.mCtx = ctx;
        this.mPrefs = this.mCtx.getSharedPreferences("prefsdata", 0);
    }

    public static void init(Context ctx) {
        my = new PrefsManager(ctx);
    }

    public static PrefsManager get() {
        if (my == null) {
            throw new RuntimeException("PrefsManager==null");
        } else {
            return my;
        }
    }

    public void save(String key, String value) {
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void save(String key, Integer value) {
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void save(String key, Long value) {
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void save(String key, Float value) {
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public void save(String key, Boolean value) {
        SharedPreferences.Editor editor = this.mPrefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

//    public static void setLastSplashUrl(Context context, String urlSplash) {
//        if (context == null)
//            return;
//        setAppInfo(context, AppConfig.M1905_APP_SPLASH_KEY, urlSplash);
//    }
//
//    public static String getLastSplashUrl(Context context) {
//        if (context == null)
//            return "";
//        SharedPreferences sp = context.getSharedPreferences(
//                AppConfig.M1905_APP_INFO, Context.MODE_PRIVATE);
//        return sp.getString(AppConfig.M1905_APP_SPLASH_KEY, "");
//}

    public String getString(String key) {
        return this.mPrefs.getString(key, (String) null);
    }

    public int getInt(String key) {
        return this.mPrefs.getInt(key, 0);
    }

    public long getLong(String key) {
        return this.mPrefs.getLong(key, 0L);
    }

    public float getFloat(String key) {
        return this.mPrefs.getFloat(key, 0.0F);
    }

    public boolean getBoolean(String key) {
        return this.mPrefs.getBoolean(key, false);
    }
}
