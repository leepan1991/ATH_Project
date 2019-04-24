package cn.innovativest.ath.ui.act;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.GetUserInfo;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.core.RongService;
import cn.innovativest.ath.entities.LoginBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.LoginResponse;
import cn.innovativest.ath.response.RongLoginResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class LoginAct extends BaseAct  {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.edtUBname)
    EditText edtUBname;

    @BindView(R.id.edtUPassword)
    EditText edtUPassword;

    @BindView(R.id.cbPassword)
    CheckBox cbPassword;

    @BindView(R.id.tvwForget)
    TextView tvwForget;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.tvwRegister)
    TextView tvwRegister;

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);
        ButterKnife.bind(this);
        flag = getIntent().getStringExtra("flag");
        addListener();
        initView();
    }

    private void addListener() {
        btnBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvwRegister.setOnClickListener(this);
        tvwForget.setOnClickListener(this);
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("登录");

        String phone = PrefsManager.get().getString("phone");
        if (!CUtils.isEmpty(phone)) {
            edtUBname.setText(phone);
            edtUBname.setSelection(phone.length());
        }

        if (PrefsManager.get().getBoolean("isRemember")) {
            cbPassword.setChecked(true);
            String password = PrefsManager.get().getString("password");
            if (!CUtils.isEmpty(password)) {
                edtUPassword.setText(password);
                edtUPassword.setSelection(password.length());
            } else {
                cbPassword.setChecked(false);
                edtUPassword.setText("");
            }
        } else {
            cbPassword.setChecked(false);
            edtUPassword.setText("");
        }
//        edtUPassword.requestFocus();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LoginAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LoginAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void login() {

        final String phone = edtUBname.getText().toString();
        final String password = edtUPassword.getText().toString();

        if (CUtils.isEmpty(phone)) {
            App.toast(LoginAct.this, "请输入手机号");
            return;
        }
        if (!AppUtils.isMobileNO(phone)) {
            App.toast(LoginAct.this, "请输入正确的手机号");
            return;
        }
        if (CUtils.isEmpty(password)) {
            App.toast(LoginAct.this, "请输入密码");
            return;
        }
        if (!AppUtils.isPassword(password)) {
            App.toast(LoginAct.this, "请输入6-20位数字或字母");
            return;
        }


        LoadingUtils.getInstance().dialogShow(this, "登录中...");

        LoginBody loginBody = new LoginBody();
        loginBody.phone = phone;
        loginBody.password = AESUtils.encryptData(password.trim());

        AthService service = App.get().getAthService();
        service.login(loginBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse loginResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(LoginAct.this, loginResponse.message);
                if (loginResponse.status == 1) {
                    //登录成功
//                    App.toast(LoginAct.this, "登录成功");
                    pitOpen();
                    App.get().user = loginResponse.user;
                    PrefsManager.get().save("user", loginResponse.user.getToken());
                    if (cbPassword.isChecked()) {
                        PrefsManager.get().save("isRemember", true);
                        PrefsManager.get().save("phone", phone);
                        PrefsManager.get().save("password", password);
                    } else {
                        PrefsManager.get().save("isRemember", false);
                        PrefsManager.get().save("phone", phone);
                        PrefsManager.get().save("password", "");
                    }
                    if (!TextUtils.isEmpty(flag) && flag.equals("1")) {
                        requestUserInfo();
                    } else {
                        setResult(RESULT_OK);
                        onBackPressed();
                    }
                }


            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(LoginAct.this, "登录失败");
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
//                            startActivity(new Intent(LoginAct.this, MainAct.class));
                            startActivity(new Intent(LoginAct.this, NewMainAct.class));
                            onBackPressed();
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
//        rongLoginBody.userId = userInfo.id + "";
//        rongLoginBody.name = userInfo.name;
//        rongLoginBody.portraitUri = AppConfig.ATH_APP_URL + userInfo.head_img_link;
        HashMap<String, String> mp = new HashMap<>();
        mp.put("userId", userInfo.id + "");
        mp.put("name", userInfo.name);
        mp.put("portraitUri", AppConfig.ATH_APP_URL + userInfo.head_img_link);
        service.getToken(App.get().generateRequestBody(mp)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<RongLoginResponse>() {
            @Override
            public void call(RongLoginResponse rongLoginResponse) {
                if (rongLoginResponse != null && rongLoginResponse.code == 200) {
                    if (!CUtils.isEmpty(rongLoginResponse.token)) {
                        PrefsManager.get().save("rongToken", rongLoginResponse.token);
//                        connect(rongLoginResponse.token);
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
                LogUtils.e("获取融云用户信息失败");
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
//                            io.rong.imlib.model.UserInfo userInfo = new io.rong.imlib.model.UserInfo(getUserInfo.id + "", getUserInfo.name, Uri.parse(AppConfig.ATH_APP_URL + getUserInfo.head_img_link));
//                            RongIM.getInstance().refreshUserInfoCache(userInfo);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnLogin:
                login();
                break;
            case R.id.tvwRegister:
                startActivity(new Intent(LoginAct.this, RegisterAct.class));
                break;
            case R.id.tvwForget:
                startActivity(new Intent(LoginAct.this, ChangeLoginPasswdAct.class));
                break;
        }
    }

}
