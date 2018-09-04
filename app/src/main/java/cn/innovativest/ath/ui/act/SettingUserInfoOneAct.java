package cn.innovativest.ath.ui.act;

import android.content.Intent;
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

public class SettingUserInfoOneAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etId)
    EditText etId;

    @BindView(R.id.btnNextStep)
    Button btnNextStep;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_user_info_one_act);
        ButterKnife.bind(this);
        initView();
    }


    //1. id和pay_password 都没设置   按钮：下一步
    //2. id已设置，pay_password未设置   按钮：直接跳转到pay_password设置页面
    //3. id未设置,pay_password已设置    按钮：完成
    //4. id和pay_password 都已设置      按钮：完成
    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("普通认证");
        btnBack.setOnClickListener(this);
        btnNextStep.setOnClickListener(this);
        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);

            if (CUtils.isEmpty(userInfo.real_name) && CUtils.isEmpty(userInfo.id_number) && CUtils.isEmpty(userInfo.check_pay_password)) {
                btnNextStep.setText("下一步");
                flag = 0;
            }

            if (CUtils.isEmpty(userInfo.real_name) && CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.check_pay_password)) {
                btnNextStep.setText("完成");
                flag = 1;
            }

            if (!CUtils.isEmpty(userInfo.real_name) && !CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.check_pay_password)) {
                if (!CUtils.isEmpty(userInfo.real_name)) {
                    etName.setText(userInfo.real_name);
                    etName.setSelection(userInfo.real_name.length());
                    etName.setEnabled(false);
                }
                if (!CUtils.isEmpty(userInfo.id_number)) {
                    etId.setText(userInfo.id_number);
                    etId.setSelection(userInfo.id_number.length());
                    etId.setEnabled(false);
                }
                btnNextStep.setText("完成");
                btnNextStep.setVisibility(View.INVISIBLE);
                flag = 2;
            }

        } else {
            btnNextStep.setText("下一步");
            flag = 0;
        }

    }

    private void idAuth() {
        if (CUtils.isEmpty(etName.getText().toString().trim())) {
            App.toast(SettingUserInfoOneAct.this, "请输入真实姓名");
            return;
        }
        if (CUtils.isEmpty(etId.getText().toString().trim())) {
            App.toast(SettingUserInfoOneAct.this, "请输入身份证号");
            return;
        }
        request(etName.getText().toString(), etId.getText().toString().trim());
    }

    private void request(String name, String id) {
        AthService service = App.get().getAthService();
        service.IdAuthentication(name, id).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (baseResponse.status == 1) {
                    if (flag == 0) {
                        startActivityForResult(new Intent(SettingUserInfoOneAct.this, SettingUserInfoTwoAct.class), 101);
                    } else if (flag == 1) {
                        setResult(RESULT_OK);
                        finish();
                    }
                } else {
                    App.toast(SettingUserInfoOneAct.this, baseResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(SettingUserInfoOneAct.this, "提交信息失败");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingUserInfoOneAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingUserInfoOneAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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
            case R.id.btnNextStep:
                if (flag == 0) {
                    idAuth();
                } else if (flag == 1) {
                    idAuth();
                } else if (flag == 2) {
                    onBackPressed();
                }
                break;
        }
    }
}
