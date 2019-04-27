package cn.innovativest.ath.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Process;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.innovativest.ath.common.AppConfig;

public class AppUtils {
    static final String TAG = "AppUtils";

    // ----------------------------------activity跳转------------------------

    /**
     * 跳转activity
     */
    public static void startActivity(Context context, Class<?> toActivity,
                                     Bundle pBundle, Integer flags, Integer requestCode) {
        Intent intent = new Intent(context, toActivity);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        if (flags != null) {
            intent.setFlags(flags);
        }
        if (requestCode != null) {
            ((AppCompatActivity) context).startActivityForResult(intent, requestCode);
        } else {
            context.startActivity(intent);
        }

    }

    public static String getFloat(float str) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format(str);
    }

    public static void startActivity(Context context, Class<?> toActivity) {
        startActivity(context, toActivity, null, null, null);
    }

    public static void startActivity(Context context, Class<?> toActivity,
                                     Bundle pBundle) {
        startActivity(context, toActivity, pBundle, null, null);
    }

    public static void startActivity(Context context, Class<?> toActivity,
                                     Integer flags) {
        startActivity(context, toActivity, null, flags, null);
    }

    public static void startActivity(Context context, Class<?> toActivity, Bundle pBundle,
                                     Integer flags) {
        startActivity(context, toActivity, pBundle, flags, null);
    }

    public static void finish(Activity a) {
        if (a.getParent() != null) {
            NavUtils.navigateUpFromSameTask(a);
        } else {
            a.finish();
        }
    }

    public static Fragment getVisibleFragment(FragmentActivity act) {
        FragmentManager fragmentManager = act.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                return fragment;
        }
        return null;
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^[1][3,4,5,6,7,8,9][0-9]{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isPassword(String pass) {
        Pattern p = Pattern
                .compile("^[a-zA-Z0-9]{6,20}$");

        Matcher m = p.matcher(pass);
        return m.matches();
    }

    /**
     * 时间转换
     *
     * @param format
     * @param time
     * @return
     */
    public static String formatTimeToFormat(String format, long time) {
        if (TextUtils.isEmpty(format))
            return String.valueOf(time);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (String.valueOf(time).length() < 13) {
            return sdf.format(time * 1000l);
        } else {
            return sdf.format(time);
        }
    }

    // -------------------------------------------------------------
    public static void killMyProcess() {
        Process.killProcess(Process.myPid());
    }

    // -------------------------------------------------------------
    public static int getScreenW(Activity act) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.widthPixels;
    }

    public static int getScreenH(Activity act) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.heightPixels;
    }

    // ----------------------------------network------------------------

    /**
     * check whether network is available
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * check whether wifi is available
     */
    public static boolean isWifiAvailable(Context ctx) {
        ConnectivityManager connMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 打开网络设置界面
     */
    public static void openNetSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    // ----------------------------------local------------------------

    /**
     * Get current local phone number if possible
     */
    public static String getLocalNumber(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        return ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();

    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale - 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((pxValue + 0.5f) / scale);
    }

    public static int getVersionCode(Context ctx) {
        PackageInfo packInfo;
        int versionCode = 0;
        try {
            packInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName(Context ctx) {
        PackageInfo packInfo;
        String versionName = "";
        try {
            packInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            versionName = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 隐藏键盘
     */
    public static void hideImm(Activity context) {
        InputMethodManager imm = (InputMethodManager) context
                .getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (context.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(context.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 打开键盘
     */
    public static void showImm(Activity context) {
        InputMethodManager imm = (InputMethodManager) context
                .getApplicationContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if (context.getCurrentFocus() != null) {
                imm.showSoftInput(context.getCurrentFocus(),
                        InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }


    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = null;
        try {
            dm = context.getResources().getDisplayMetrics();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dm.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 程序是否在前台运行
     */
    public boolean isAppOnForeground(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        String packageName = ctx.getApplicationContext().getPackageName();

        List<RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    /**
     * 安装APK文件
     */
    public static void installApk(Context ctx, String filePath, boolean exit) {
        Log.i(TAG, "installApk  filePath=" + filePath);
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        ctx.startActivity(i);
        if (exit) {
            killMyProcess();
        }
    }

    /**
     * 是否支持该特征
     */
    public static boolean isFeatureSupported(Context context, String feature) {
        boolean support;
        try {
            support = context.getPackageManager().hasSystemFeature(feature);
            return support;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取meta-data
     *
     * @param ctx
     * @return
     */
    public static String getMetaDataValue(Context ctx, String name) {
        String value = "";
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(
                    ctx.getPackageName(), PackageManager.GET_META_DATA);
            Object v = ai.metaData.get(name);
            if (v != null) {
                value = v.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * // 应用内配置语言
     *
     * @param l --Locale.SIMPLIFIED_CHINESE
     */
    public static void changeLocale(Context ctx, Locale l) {
        Resources resources = ctx.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        config.locale = l;
        resources.updateConfiguration(config, dm);
    }


    /**
     * 获取状态栏的高度
     *
     * @param activity
     * @return
     */
    public static int getStatusHeight(AppCompatActivity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 是否存在闪图
     *
     * @param urlSplash
     * @return
     */
    public static boolean existsSplash(String urlSplash) {
        if (TextUtils.isEmpty(urlSplash)) {
            return false;
        }
        String fileName = MD5Utils.MD5(urlSplash);
        File splash = new File(AppConfig.ATH_CACHE_PATH + fileName);
        if (splash != null && splash.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置header view的paddingTop
     *
     * @param act
     * @param
     */
    public static void setStatusPadding(AppCompatActivity act, int layId) {
        if (VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            act.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            LinearLayout lay = (LinearLayout) act.findViewById(layId);
            if (lay != null) {
                int statusBarHeight = AppUtils.getStatusHeight(act);
                lay.setPadding(0, statusBarHeight, 0, 0);
            }
        }
    }

    public static String floatToStringByTruncate(double num, int remainBitNum) {
        String numStr = Double.toString(num);
        BigDecimal bd = new BigDecimal(numStr);
        bd = bd.setScale(remainBitNum, BigDecimal.ROUND_DOWN);
        return bd.toString();
    }

    public static int differentDaysByMillisecond(long date1, long date2) {
        int days = (int) ((date1 - date2) / (3600 * 24));
        return days;
    }

}
