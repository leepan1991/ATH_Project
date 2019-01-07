package cn.innovativest.ath.ui.act;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.GetUserInfo;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.common.DownloadTask;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.core.RongService;
import cn.innovativest.ath.entities.LoginBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.LoginResponse;
import cn.innovativest.ath.response.RongLoginResponse;
import cn.innovativest.ath.response.StartImgResponse;
import cn.innovativest.ath.response.UpgradeResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.MD5Utils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.utils.SDUtils;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class StartAct extends BaseAct implements RongIM.UserInfoProvider {

    private final int REQUEST_READ_PHONE_STATE = 100;
    private long lastTimeMillis;
    private final long WAIT_TIME = 3000l;

    @Override
    protected void onCreate(@Nullable Bundle arg0) {
        super.onCreate(arg0);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.start_act);
        getPermission();
    }

    private void getPermission() {
        // 申请单个权限。
        AndPermission.with(this)
                .requestCode(REQUEST_READ_PHONE_STATE)
                .permission(Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET)
                .callback(listener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(StartAct.this, rationale)
                                .show();
                    }
                })
                .start();
    }

    private PermissionListener listener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, List<String> grantedPermissions) {
            if (requestCode == REQUEST_READ_PHONE_STATE) {
                // 初始化文件目录
                SDUtils.initAppDirectory();
                // 删除过期缓存
                SDUtils.deleteExpiredCacheFile();
                lastTimeMillis = System.currentTimeMillis();
                RongIM.setUserInfoProvider(StartAct.this, true);
                getVersion();
                qidong();
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

        }
    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    doEnter();
                    break;
            }
        }
    };

    private void doEnter() {
//        if (PrefsManager.get().getBoolean("isIntroduce")) {
//            AppUtils.startActivity(StartAct.this, MainAct.class);
//        } else {
        AppUtils.startActivity(mCtx, SplashAct.class);
//            AppUtils.startActivity(StartAct.this, MainAct.class);
//        }
        AppUtils.finish(mCtx);

    }

    private void getVersion() {
        AthService service = App.get().getAthService();
        service.init().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UpgradeResponse>() {
            @Override
            public void call(UpgradeResponse upgradeResponse) {
                if (upgradeResponse != null) {
                    if (upgradeResponse.upgrade != null) {
                        boolean force = false;
                        if ("1".equals(upgradeResponse.upgrade.is_update)) {
                            dealUpgrade(force, upgradeResponse.upgrade.version_code, upgradeResponse.upgrade.upgrade_point, upgradeResponse.upgrade.apk_url);
                        } else if ("2".equals(upgradeResponse.upgrade.is_update)) {
                            force = true;
                            dealUpgrade(force, upgradeResponse.upgrade.version_code, upgradeResponse.upgrade.upgrade_point, upgradeResponse.upgrade.apk_url);
                        } else {
                            autoLogin();
                        }
                    } else {
                        autoLogin();
                    }
                } else {
                    autoLogin();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                autoLogin();
            }
        });
    }


    private void requestUserInfoByUserId(String userId) {

        AthService service = App.get().getAthService();
        service.get_user_info(userId).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        LogUtils.e(AESUtils.decryptData(userInfoResponse.data));
                        GetUserInfo getUserInfo = new Gson().fromJson(AESUtils.decryptData(userInfoResponse.data), GetUserInfo.class);
                        if (getUserInfo != null) {
                            io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(getUserInfo.id + "", getUserInfo.name, Uri.parse(AppConfig.ATH_APP_URL + getUserInfo.head_img_link));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
                        } else {
                            LogUtils.e("userInfo is null");
                        }
                    } else {
                        LogUtils.e("userInfoResponse.data is null");
                    }

                } else {
                    LogUtils.e(userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("获取用户信息失败");
            }
        });
    }


    private void autoLogin() {


        if (!PrefsManager.get().getBoolean("isRemember")) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(1, calculate());
            }
            return;
        }
        final String strEmail = PrefsManager.get().getString("phone");
        final String strPassword = PrefsManager.get().getString("password");

        // 未登录， 等待后直接转跳
        if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
            if (mHandler != null) {
                mHandler.sendEmptyMessageDelayed(1, calculate());
            }
            return;
        }

        LoginBody loginBody = new LoginBody();
        loginBody.phone = strEmail;
        loginBody.password = AESUtils.encryptData(strPassword.trim());

        AthService service = App.get().getAthService();
        service.login(loginBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse loginResponse) {
                if (loginResponse.status == 1) {
                    //登录成功
//                    App.toast(LoginAct.this, "登录成功");
                    pitOpen();
                    App.get().user = loginResponse.user;
                    PrefsManager.get().save("user", loginResponse.user.getToken());
                    requestUserInfo();
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(1, calculate());
                    }
                } else {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessageDelayed(1, calculate());
                    }
                }


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (mHandler != null) {
                    mHandler.sendEmptyMessageDelayed(1, calculate());
                }
            }
        });

    }

    private void pitOpen() {

        AthService service = App.get().getAthService();
        service.pit_open().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LogUtils.e(baseResponse.message);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
    }

    private void qidong() {

        AthService service = App.get().getAthService();
        service.qidong().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<StartImgResponse>() {
            @Override
            public void call(StartImgResponse startImgResponse) {
                if (startImgResponse != null && startImgResponse.status == 1 && startImgResponse.lstStartImg != null && startImgResponse.lstStartImg.size() == 1) {
                    doDownloadImage(AppConfig.ATH_APP_URL + startImgResponse.lstStartImg.get(0).img);
                    PrefsManager.get().save("time", startImgResponse.lstStartImg.get(0).time);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
            }
        });
    }

    private void requestUserInfo() {

        AthService service = App.get().getAthService();
        service.userInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        LogUtils.e(AESUtils.decryptData(userInfoResponse.data));
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(userInfoResponse.data), UserInfo.class);
                        if (userInfo != null) {
                            PrefsManager.get().save("userinfo", userInfoResponse.data);
                            getToken(userInfo);
//                            startActivity(new Intent(StartAct.this, MainAct.class));
//                            onBackPressed();
                        } else {
                            LogUtils.e("userInfo is null");
                        }
                    } else {
                        LogUtils.e("userInfoResponse.data is null");
                    }

                } else {
                    LogUtils.e(userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("获取用户信息失败");
                if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                    getToken(userInfo);
                }

            }
        });
    }

    private void getToken(UserInfo userInfo) {

        RongService service = App.get().getRongService();
//        RongLoginBody rongLoginBody = new RongLoginBody();
        HashMap<String, String> mp = new HashMap<>();
        mp.put("userId", userInfo.id + "");
        mp.put("name", userInfo.name);
        mp.put("portraitUri", AppConfig.ATH_APP_URL + userInfo.head_img_link);
//        rongLoginBody.userId = userInfo.id + "";
//        rongLoginBody.name = userInfo.name;
//        rongLoginBody.portraitUri = AppConfig.ATH_APP_URL + userInfo.head_img_link;
        service.getToken(App.get().generateRequestBody(mp)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<RongLoginResponse>() {
            @Override
            public void call(RongLoginResponse rongLoginResponse) {
                if (rongLoginResponse != null && rongLoginResponse.code == 200) {
                    if (!CUtils.isEmpty(rongLoginResponse.token)) {
                        PrefsManager.get().save("rongToken", rongLoginResponse.token);
                        connect(rongLoginResponse.token);
                    } else {
                        LogUtils.e("rongLoginResponse.token is null");
                    }

                } else {
                    LogUtils.e("rongLoginResponse.code is not 200");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                LogUtils.e("获取融云用户信息失败");
            }
        });
    }


    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #(Context)} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token 从服务端获取的用户身份令牌（Token）。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.get().getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {
                    requestUserInfo();
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtils.d("--onSuccess" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }


    private void dealUpgrade(final boolean force, String title, String msg, final String url) {

        new AlertDialog.Builder(StartAct.this).setTitle("检测到新版本V" + title + "，是否升级?").setMessage(msg).setCancelable(false).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (force) {
                    finish();
                } else {
                    autoLogin();
                }
            }
        }).setPositiveButton("升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                doDownload(force, url);
            }
        }).show();
    }

    private long calculate() {
        long timeDifference = System.currentTimeMillis() - lastTimeMillis;
        if (timeDifference > WAIT_TIME) {// 时间差大于默认值 直接转跳
            return 0;
        } else {// 时间差不足默认值，继续等待剩余时间后 转跳
            return WAIT_TIME - timeDifference;
        }
    }

    private void doDownload(final boolean force, String url) {
        final ProgressDialog pd = new ProgressDialog(StartAct.this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("下载中...");
        pd.setCancelable(false);
        pd.show();
        String path = AppConfig.ATH_DOWNLOAD_PATH + "update.apk";
        LogUtils.i("url=" + url + "  path=" + url);
        new DownloadTask(StartAct.this, url, path, false, new DownloadTask.DownloadListener() {

            @Override
            public void onFailed(String url) {
                pd.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                        if (force) {
                            finish();
                        } else {
                            if (mHandler != null) {
                                mHandler.sendEmptyMessageDelayed(1, calculate());
                            }
                        }
                    }
                });

            }

            @Override
            public void onDownloading(String url, final long totalSize, final long downSize) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        pd.setMax((int) (totalSize / 1024));
                        pd.setProgress((int) (downSize / 1024));
                        pd.setProgressNumberFormat("%1dkb/%2dkb");
                    }
                });
            }

            @Override
            public void onComplete(String url, String localPath) {
                pd.dismiss();
                LogUtils.i("localPath=" + localPath);
                installApk(StartAct.this, localPath);
                finish();
            }
        }).start();
    }

    private void doDownloadImage(final String urlSplash) {
        if (TextUtils.isEmpty(urlSplash)) {
            return;
        }
        if (AppUtils.existsSplash(urlSplash)) {
            if (!urlSplash.equals(PrefsManager.get().getString("lastSplashUrl"))) {
                PrefsManager.get().save("lastSplashUrl", urlSplash);
            }
            return;
        }
        String fileName = MD5Utils.MD5(urlSplash);
        String path = AppConfig.ATH_CACHE_PATH + fileName;
        LogUtils.i("url=" + urlSplash + "  path=" + path);
        new DownloadTask(StartAct.this, urlSplash, path, false, new DownloadTask.DownloadListener() {

            @Override
            public void onFailed(String url) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        LogUtils.e("闪图下载失败");
                    }
                });

            }

            @Override
            public void onDownloading(String url, final long totalSize, final long downSize) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        LogUtils.i(downSize + "kb/" + totalSize + "kb");
                    }
                });
            }

            @Override
            public void onComplete(String url, String localPath) {
                LogUtils.e("闪图下载成功");
                PrefsManager.get().save("lastSplashUrl", urlSplash);
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
            if (mHandler.hasMessages(1)) {
                mHandler.removeMessages(1);
            }
            mHandler = null;
        }
        super.onDestroy();
    }

    /**
     * 安装APK文件
     */
    public void installApk(Context ctx, String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(this, "cn.innovativest.ath.fileProvider", apkfile);
            i.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        }
        ctx.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("StartAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StartAct");
        MobclickAgent.onPause(this);
    }

    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            if (mHandler != null)
                mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public io.rong.imlib.model.UserInfo getUserInfo(String s) {
        requestUserInfoByUserId(s);
        return null;
    }
}
