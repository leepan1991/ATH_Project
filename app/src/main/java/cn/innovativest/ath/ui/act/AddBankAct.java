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
import cn.innovativest.ath.entities.BindingBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.MD5Utils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class AddBankAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etAccount)
    EditText etAccount;

    @BindView(R.id.etAccountBranch)
    EditText etAccountBranch;

    @BindView(R.id.etBankId)
    EditText etBankId;

    @BindView(R.id.etBankIdAgain)
    EditText etBankIdAgain;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.btnSave)
    Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bank_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("添加银行卡");
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        }
    }

    private void initDataToView(UserInfo userInfo) {
        etName.setText(userInfo.real_name);
    }

    private void request() {
        String name = etName.getText().toString().trim();
        String bankName = etAccount.getText().toString().trim();
        String bankBranchName = etAccountBranch.getText().toString().trim();
        String bankId = etBankId.getText().toString().trim();
        String bankIdAgain = etBankIdAgain.getText().toString().trim();
        String passwd = etPassword.getText().toString().trim();

        if (CUtils.isEmpty(name)) {
            App.toast(this, "姓名不能为空");
            return;
        }

        if (CUtils.isEmpty(bankName)) {
            App.toast(this, "开户银行不能为空");
            return;
        }

        if (CUtils.isEmpty(bankBranchName)) {
            App.toast(this, "开户支行不能为空");
            return;
        }

        if (CUtils.isEmpty(bankId)) {
            App.toast(this, "银行卡号不能为空");
            return;
        }

        if (bankId.length() < 13 || bankId.length() > 19) {
            App.toast(this, "请输入13或19位的银行卡号");
            return;
        }

        if (CUtils.isEmpty(bankIdAgain)) {
            App.toast(this, "银行卡号不能为空");
            return;
        }

        if (bankIdAgain.length() < 13 || bankIdAgain.length() > 19) {
            App.toast(this, "请输入13或19位的银行卡号");
            return;
        }

        if (!bankId.equals(bankIdAgain)) {
            App.toast(this, "银行卡号输入不一致");
            return;
        }

        if (CUtils.isEmpty(passwd)) {
            App.toast(this, "交易密码不能为空");
            return;
        }

        if (passwd.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            if (CUtils.isEmpty(userInfo.check_pay_password)) {
                App.toast(this, "请设置交易密码");
                return;
            }
            if (!userInfo.check_pay_password.equals(MD5Utils.MD5(passwd + "_#HYAK!"))) {
                App.toast(this, "交易密码不正确");
                return;
            }
        } else {
            App.toast(AddBankAct.this, "请设置交易密码");
            return;
        }

        addBank(bankName, bankBranchName, bankId);
    }


    private void addBank(String bankName, String bankBranchName, String bankId) {
        BindingBody bindingBody = new BindingBody();
        bindingBody.status = "3";
        bindingBody.bank_name = bankName;
        bindingBody.bank_location = bankBranchName;
        bindingBody.bank_number = bankId;

        AthService service = App.get().getAthService();
        service.binding(bindingBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                App.toast(AddBankAct.this, baseResponse.message);
                if (baseResponse.status == 1) {
                    setResult(RESULT_OK);
                    finish();

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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddBankAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddBankAct");
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
                finish();
                break;
            case R.id.btnSave:
                request();
                break;
        }
    }
}
