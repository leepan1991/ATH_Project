package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class SafetyManagementAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.ivLoginAvatar)
    ImageView ivLoginAvatar;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ivRealed)
    ImageView ivRealed;

    @BindView(R.id.tvwCoinValue)
    TextView tvwCoinValue;

    @BindView(R.id.rltRealCertification)
    RelativeLayout rltRealCertification;

    @BindView(R.id.tvCertificationStatus)
    TextView tvCertificationStatus;

    @BindView(R.id.rltMobileBinding)
    RelativeLayout rltMobileBinding;

    @BindView(R.id.tvMobileNumber)
    TextView tvMobileNumber;

    @BindView(R.id.rltSinglePasswd)
    RelativeLayout rltSinglePasswd;

    @BindView(R.id.rltLoginPasswd)
    RelativeLayout rltLoginPasswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safety_management_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("安全管理");

        btnBack.setOnClickListener(this);
        rltRealCertification.setOnClickListener(this);
        rltMobileBinding.setOnClickListener(this);
        rltSinglePasswd.setOnClickListener(this);
        rltLoginPasswd.setOnClickListener(this);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        }

    }

    private void initDataToView(UserInfo userInfo) {
        tvName.setText(userInfo.name);
        GlideApp.with(this).load(AppConfig.ATH_APP_URL + userInfo.head_img_link).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(ivLoginAvatar);
        if (userInfo.state == 1) {
            ivRealed.setVisibility(View.VISIBLE);
            tvCertificationStatus.setVisibility(View.VISIBLE);
        } else {
            ivRealed.setVisibility(View.INVISIBLE);
            tvCertificationStatus.setVisibility(View.INVISIBLE);
        }
        if (userInfo.mainPage != null) {
            tvwCoinValue.setText("总积分：" + String.format("%.2f", Float.valueOf((CUtils.isEmpty(userInfo.mainPage.integral) ? "0" : userInfo.mainPage.integral + ""))));
        }
        if (!CUtils.isEmpty(userInfo.phone)) {
            tvMobileNumber.setVisibility(View.VISIBLE);
            tvMobileNumber.setText(userInfo.phone);
        } else {
            tvMobileNumber.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SafetyManagementAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SafetyManagementAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                }
                break;
        }
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
//                            if (userInfo.state == 1) {
//                                ivRealed.setVisibility(View.VISIBLE);
//                                tvCertificationStatus.setVisibility(View.VISIBLE);
//                            } else if (userInfo.state == 0) {
//                                ivRealed.setVisibility(View.INVISIBLE);
//                                tvCertificationStatus.setVisibility(View.INVISIBLE);
//                            }
//                            if (!CUtils.isEmpty(userInfo.phone)) {
//                                tvMobileNumber.setVisibility(View.VISIBLE);
//                                tvMobileNumber.setText(userInfo.phone);
//                            } else {
//                                tvMobileNumber.setVisibility(View.INVISIBLE);
//                            }
                            initDataToView(userInfo);
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
            case R.id.rltRealCertification:
                startActivityForResult(new Intent(this, RealCentificationAct.class), 101);
                break;
            case R.id.rltMobileBinding:
                startActivity(new Intent(this, MobileBindingAct.class));
                break;
            case R.id.rltSinglePasswd:
                if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                    if (userInfo != null && !CUtils.isEmpty(userInfo.check_pay_password)) {
                        startActivityForResult(new Intent(this, SinglePasswdChangeAct.class), 101);
                    } else {
                        startActivityForResult(new Intent(this, SettingUserInfoTwoAct.class), 101);
                    }
                } else {
                    startActivityForResult(new Intent(this, SettingUserInfoTwoAct.class), 101);
                }
                break;
            case R.id.rltLoginPasswd:
                startActivity(new Intent(this, ChangeLoginPasswdAct.class));
                break;
//            case R.id.rltSettingPwd:
//                startActivity(new Intent(this, SettingPasswdProtectAct.class));
//                break;
        }

    }
}
