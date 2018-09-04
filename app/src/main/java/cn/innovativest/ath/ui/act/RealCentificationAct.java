package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
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

public class RealCentificationAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.rltRealCertification)
    RelativeLayout rltRealCertification;

    @BindView(R.id.tvCertificationStatus)
    TextView tvCertificationStatus;

    @BindView(R.id.rltHighCertification)
    RelativeLayout rltHighCertification;

    @BindView(R.id.tvHighCertificationStatus)
    TextView tvHighCertificationStatus;

    UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.real_certification_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("实名认证");
        btnBack.setOnClickListener(this);
        rltRealCertification.setOnClickListener(this);
        rltHighCertification.setOnClickListener(this);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            if (!CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.real_name)) {
                tvCertificationStatus.setVisibility(View.VISIBLE);
            } else {
                tvCertificationStatus.setVisibility(View.INVISIBLE);
            }

            if (userInfo.state == 1) {
                tvHighCertificationStatus.setVisibility(View.VISIBLE);
            } else {
                tvHighCertificationStatus.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RealCentificationAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RealCentificationAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void requestUserInfo() {

        AthService service = App.get().getAthService();
        service.userInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        LogUtils.e(AESUtils.decryptData(userInfoResponse.data));
                        userInfo = new Gson().fromJson(AESUtils.decryptData(userInfoResponse.data), UserInfo.class);
                        if (userInfo != null) {
                            PrefsManager.get().save("userinfo", userInfoResponse.data);
                            if (!CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.real_name)) {
                                tvCertificationStatus.setVisibility(View.VISIBLE);
                            } else {
                                tvCertificationStatus.setVisibility(View.INVISIBLE);
                            }

                            if (userInfo.state == 1) {
                                tvHighCertificationStatus.setVisibility(View.VISIBLE);
                            } else {
                                tvHighCertificationStatus.setVisibility(View.INVISIBLE);
                            }
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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.rltRealCertification:
                startActivityForResult(new Intent(this, SettingUserInfoOneAct.class), 101);
                break;
            case R.id.rltHighCertification:
                if (!CUtils.isEmpty(userInfo.real_name) && !CUtils.isEmpty(userInfo.id_number)) {
                    startActivityForResult(new Intent(this, HighCentificationAct.class), 101);
                } else {
                    startActivityForResult(new Intent(this, SettingUserInfoOneAct.class), 101);
                }
                break;
        }
    }
}
