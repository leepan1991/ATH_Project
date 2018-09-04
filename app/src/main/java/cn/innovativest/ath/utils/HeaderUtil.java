package cn.innovativest.ath.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cn.innovativest.ath.App;
import cn.innovativest.ath.core.RetrofitClient;

public class HeaderUtil {

    public static String app_type = "android";

    public static String random;

//    public static String getTime() {
//        LogUtils.e(time);
//        return time;
//    }

    public static String getDid(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            return tm.getDeviceId().trim();
        }
        return null;
    }

    public static String getModel() {
        return android.os.Build.MODEL;
    }

    public static String getToken() {
        return App.get().user == null ? "" : AESUtils.encryptData(AESUtils.decryptData(App.get().user.getToken()).substring(0, AESUtils.decryptData(App.get().user.getToken()).indexOf("_#HYAK!")) + String.valueOf(System.currentTimeMillis()));
    }

    //产生 一个范围的内的数
    public static int generateRandomByScope(int small, int bignum) {
        int num = -1;
        Random random = new Random();
        num = random.nextInt(bignum) + small;  //产生幸运数
        return num;
    }

    public static String getSign(Context ctx) {
        //产生一个1000000到2000000之间的随机数
        random = String.valueOf(generateRandomByScope(1000000, 1000000));

        RetrofitClient.random = random;

//        String sign = "did=" + "4294967295" + "&random=" + random + "&app-type=" + app_type + "&time=" + String.valueOf(System.currentTimeMillis());
        String sign = "did=" + getDid(ctx) + "&random=" + RetrofitClient.random + "&app-type=" + app_type + "&time=" + String.valueOf(System.currentTimeMillis());
        LogUtils.e(AESUtils.encryptData(sign.trim()));
        return AESUtils.encryptData(sign.trim());

    }

    public static String sha1(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for (int i = 0; i < bits.length; i++) {
            int a = bits[i];
            if (a < 0) a += 256;
            if (a < 16) buf.append("0");
            buf.append(Integer.toHexString(a));
        }
        return buf.toString();
    }

}
