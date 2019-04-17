package cn.innovativest.ath;

import android.app.ActivityManager;
import android.content.Context;
import android.os.StrictMode;
import androidx.multidex.MultiDex;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mob.MobApplication;
import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import cn.innovativest.ath.bean.Address;
import cn.innovativest.ath.bean.User;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.core.RetrofitClient;
import cn.innovativest.ath.core.RongService;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import io.rong.imkit.RongIM;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Scheduler;
import rx.schedulers.Schedulers;

public class App extends MobApplication {
    private static App my;
    //    public DbManager dbManager;
    public static boolean DEBUG;
    public Gson gson;
    public User user;
    private AthService athService;
    private RongService rongService;
    private Scheduler defaultSubscribeScheduler;
    public static Address selectAddress = null;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "ab6f5e0feb", BuildConfig.DEBUG);
        my = this;
        MobSDK.init(this);
        initPush();
        initStaticMethods();
//        x.Ext.init(my);
//        x.Ext.setDebug(false);
//        dbManager = x.getDb(Config.daoConfig);
        gson = new Gson();
        PrefsManager.init(my);
//        ImageLoader.getInstance().init(
//                new ImageLoaderConfiguration.Builder(my).denyCacheImageMultipleSizesInMemory().memoryCache(new WeakMemoryCache()).memoryCacheSize(25 * 1024 * 1024)
//                        .diskCacheSize(80 * 1024 * 1024).diskCacheFileCount(100).build());
        LogUtils.init(BuildConfig.DEBUG);
        String saddress = PrefsManager.get().getString("address");
        if (!CUtils.isEmpty(saddress)) {
            selectAddress = App.get().gson.fromJson(saddress, Address.class);
        }
    }

    private void initPush() {

        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            RongIM.init(this);
        }

        //注册友盟
        UMConfigure.init(getApplicationContext(), "5b6bb8f08f4a9d25e70000be", "Channel", UMConfigure.DEVICE_TYPE_PHONE, "6fb60a87b9b54205dc1f49bce93ec9e2");

        PushAgent mPushAgent = PushAgent.getInstance(this);
        // 通知声音由服务端控制
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SERVER);
//注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                LogUtils.i("device token: " + deviceToken);
                //注册成功会返回device token
                PrefsManager.get().save("deviceToken", deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    /**
     * initialize app static data
     */
    private void initStaticMethods() {
        DEBUG = BuildConfig.DEBUG;
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public AthService getAthService() {
        if (athService == null) {
            athService = RetrofitClient.getService(this);
        }
        return athService;
    }

    public void setAthService(AthService athService) {
        this.athService = athService;
    }

    public RongService getRongService() {
        if (rongService == null) {
            try {
                rongService = RetrofitClient.getRongService(this);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return rongService;
    }

    public void setRongService(RongService rongService) {
        this.rongService = rongService;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    //User to change scheduler from tests
    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static App get() {
        return my;
    }

    public static void toast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    public Map<String, RequestBody> generateRequestBody(Map<String, String> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                    requestDataMap.get(key) == null ? "" : requestDataMap.get(key));
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }

    public String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
