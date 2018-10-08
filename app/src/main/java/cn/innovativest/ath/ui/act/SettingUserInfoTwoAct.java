package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.SinglePasswordBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class SettingUserInfoTwoAct extends BaseAct {


    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etPass)
    EditText etPass;

    @BindView(R.id.etConfirmPass)
    EditText etConfirmPass;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_user_info_two_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("设置用户信息");
        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
        if (userInfo != null && !CUtils.isEmpty(userInfo.name)) {
            etName.setText(userInfo.name);
            etName.setSelection(userInfo.name.length());
        }
    }

    private void auth() {

        String name = etName.getText().toString();
        String pw1 = etPass.getText().toString().trim();
        String pw2 = etConfirmPass.getText().toString().trim();
        if (CUtils.isEmpty(pw1)) {
            App.toast(SettingUserInfoTwoAct.this, "请设置交易密码");
            return;
        }

        if (pw1.length() != 6) {
            App.toast(SettingUserInfoTwoAct.this, "请输入6位交易密码");
            return;
        }

        if (CUtils.isEmpty(pw2)) {
            App.toast(SettingUserInfoTwoAct.this, "请确认交易密码");
            return;
        }

        if (pw2.length() != 6) {
            App.toast(SettingUserInfoTwoAct.this, "请确认输入6位交易密码");
            return;
        }

        if (!pw1.equals(pw2)) {
            App.toast(SettingUserInfoTwoAct.this, "交易密码输入不一致");
            return;
        }

        request(name, pw1, pw2);

    }

    private void request(String name, String pw1, String pw2) {
        SinglePasswordBody singlePasswordBody = new SinglePasswordBody();
        singlePasswordBody.name = name;
        singlePasswordBody.pay_password1 = pw1;
        singlePasswordBody.pay_password2 = pw2;
        AthService service = App.get().getAthService();
        LoadingUtils.getInstance().dialogShow(this, "设置中...");
        service.pay_password(singlePasswordBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (baseResponse.status == 1) {
                    App.toast(SettingUserInfoTwoAct.this, "设置交易密码成功");
                    setResult(RESULT_OK);
                    finish();
                } else {
                    App.toast(SettingUserInfoTwoAct.this, baseResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(SettingUserInfoTwoAct.this, "设置交易密码失败");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingUserInfoTwoAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingUserInfoTwoAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnConfirm:
                auth();
                break;
        }
    }
}
