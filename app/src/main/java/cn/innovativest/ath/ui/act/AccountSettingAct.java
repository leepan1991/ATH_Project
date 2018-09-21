package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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

public class AccountSettingAct extends BaseAct {

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

    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;

    @BindView(R.id.rltAlipay)
    RelativeLayout rltAlipay;

    @BindView(R.id.tvAlipayAccount)
    TextView tvAlipayAccount;

    @BindView(R.id.tbAlipaySwitch)
    ToggleButton tbAlipaySwitch;

    @BindView(R.id.rltBank)
    RelativeLayout rltBank;

    @BindView(R.id.tvBankAccount)
    TextView tvBankAccount;

    @BindView(R.id.tbBankSwitch)
    ToggleButton tbBankSwitch;

    @BindView(R.id.rltWechat)
    RelativeLayout rltWechat;

    @BindView(R.id.tvWechatAccount)
    TextView tvWechatAccount;

    @BindView(R.id.tbWechatSwitch)
    ToggleButton tbWechatSwitch;

    @BindView(R.id.lltPay)
    LinearLayout lltPay;

    @BindView(R.id.tvwMethod)
    TextView tvwMethod;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_setting_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("账户设置");

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        } else {
            rltAlipay.setVisibility(View.GONE);
            rltBank.setVisibility(View.GONE);
            rltWechat.setVisibility(View.GONE);
        }

//        if (count >= 0 && count < 3) {
//            lltPay.setVisibility(View.VISIBLE);
//        } else {
//            lltPay.setVisibility(View.GONE);
//        }
        if (count == 3) {
            tvwMethod.setText("添加或修改收款方式");
        } else {
            tvwMethod.setText("添加或修改收款方式");
        }

        btnBack.setOnClickListener(this);
        lltPay.setOnClickListener(this);
    }

    private void initDataToView(UserInfo userInfo) {
        if (userInfo != null) {
            tvName.setText(userInfo.name);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + userInfo.head_img_link).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(ivLoginAvatar);
            if (userInfo.state == 1) {
                ivRealed.setVisibility(View.VISIBLE);
            } else {
                ivRealed.setVisibility(View.INVISIBLE);
            }
            tvPhoneNumber.setText("手机号：" + userInfo.phone);
            if (!CUtils.isEmpty(userInfo.bank)) {
                rltBank.setVisibility(View.VISIBLE);
                String[] strbank = userInfo.bank.split("\\|");
                if (strbank.length == 3) {
                    count++;
                    tvBankAccount.setText(strbank[2]);
                } else {
                    rltBank.setVisibility(View.GONE);
                }

            } else {
                rltBank.setVisibility(View.GONE);
            }

            if (!CUtils.isEmpty(userInfo.alipay)) {
                rltAlipay.setVisibility(View.VISIBLE);
                String[] stralipay = userInfo.alipay.split("\\|");
                if (stralipay.length == 2) {
                    count++;
                    tvAlipayAccount.setText(stralipay[1]);
                } else {
                    rltAlipay.setVisibility(View.GONE);
                }
            } else {
                rltAlipay.setVisibility(View.GONE);
            }

            if (!CUtils.isEmpty(userInfo.wechat)) {
                rltWechat.setVisibility(View.VISIBLE);
                String[] strwechat = userInfo.wechat.split("\\|");
                if (strwechat.length == 2) {
                    count++;
                    tvWechatAccount.setText(strwechat[1]);
                } else {
                    rltWechat.setVisibility(View.GONE);
                }
            } else {
                rltWechat.setVisibility(View.GONE);
            }
        } else {
            rltAlipay.setVisibility(View.GONE);
            rltBank.setVisibility(View.GONE);
            rltWechat.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AccountSettingAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AccountSettingAct");
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
            case 100:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.lltPay:
                startActivityForResult(new Intent(this, PayMethodAct.class), 100);
                break;
        }
    }
}
