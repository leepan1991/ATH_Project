package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.CodeBody;
import cn.innovativest.ath.entities.ModifyPhoneBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class MobileBindingAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etSinglePasswd)
    EditText etSinglePasswd;

    @BindView(R.id.etMobileNumber)
    EditText etMobileNumber;

    @BindView(R.id.edtUCode)
    EditText edtUCode;

    @BindView(R.id.btnCode)
    Button btnCode;

    @BindView(R.id.btnBinding)
    Button btnBinding;

    // 计时器
    private CheckCountDownTimer checkCountDownTimer;

    private int totalCount = 60;
    private static final int MSG_WHAT_REQUEST_ING = 1;
    private static final int MSG_WHAT_REQUEST_END = 0;

    private static final int CODE_WAITING = 100;// 获取验证码等待
    private static final int CODE_RETRY = 101;// 重新获取验证码
    private static final int CODE_REQUESTING = 102;
    private static final int CODE_GET = 103;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_mobile_binding_act);
        ButterKnife.bind(this);
        initView();
        addListener();
    }

    private void addListener() {
        checkCountDownTimer = new CheckCountDownTimer(totalCount * 1000, 1000);
        btnBack.setOnClickListener(this);
        btnCode.setOnClickListener(this);
        btnBinding.setOnClickListener(this);
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("手机绑定");
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_WAITING:
                    btnCode.setEnabled(false);
                    btnCode.setText(String.format(
                            getString(R.string.regist_code_wait), msg.arg1 + ""));
                    break;
                case CODE_RETRY:
                    btnCode.setEnabled(true);
                    btnCode.setText(R.string.regist_code_get_retry);
                    // TODO 暂无背景资源
                    break;
                case CODE_REQUESTING:
                    btnCode.setEnabled(false);
                    btnCode.setText("获取中");
                    break;
                case CODE_GET:
                    btnCode.setEnabled(true);
                    btnCode.setText("获取验证码");
                    break;
                case MSG_WHAT_REQUEST_END:
                    requestendView();
                    notifyView(true);
                    break;
                case MSG_WHAT_REQUEST_ING:
                    requestingView();
                    notifyView(false);
                    break;
            }

        }

        ;
    };

    private void notifyView(boolean enabled) {
        etSinglePasswd.setEnabled(enabled);
        etMobileNumber.setEnabled(enabled);
        edtUCode.setEnabled(enabled);
    }

    private void binding() {
        String pw1 = etSinglePasswd.getText().toString().trim();
        String phone = etMobileNumber.getText().toString().trim();
        String code = edtUCode.getText().toString().trim();

        if (TextUtils.isEmpty(pw1)) {
            App.toast(this, "请输入交易密码");
            return;
        }

        if (pw1.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            App.toast(this, "请输入手机号");
            return;
        }
        if (!AppUtils.isMobileNO(phone)) {
            App.toast(MobileBindingAct.this, "请输入正确的手机号");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            App.toast(this, "请输入验证码");
            return;
        }

        request(pw1, phone, code);
    }

    public static boolean checkPhoneNumber(String phone) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private void phoneCheck() {
        String pw1 = etSinglePasswd.getText().toString().trim();
        String phone = etMobileNumber.getText().toString().trim();

        if (TextUtils.isEmpty(pw1)) {
            App.toast(this, "请输入交易密码");
            return;
        }

        if (pw1.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            App.toast(this, "请输入手机号");
            return;
        }
        if (!AppUtils.isMobileNO(phone)) {
            App.toast(MobileBindingAct.this, "请输入正确的手机号");
            return;
        }
        getCode();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void request(final String pw, final String phone, final String code) {

        ModifyPhoneBody modifyPhoneBody = new ModifyPhoneBody();
        modifyPhoneBody.pay_password = pw;
        modifyPhoneBody.phone = phone;
        modifyPhoneBody.yzm = code;

        AthService service = App.get().getAthService();
        service.edit_phone(modifyPhoneBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse.status == 1) {
                    App.get().user = null;
                    PrefsManager.get().save("user", "");
                    PrefsManager.get().save("isRemember", false);
                    PrefsManager.get().save("phone", "");
                    PrefsManager.get().save("password", "");
                    PrefsManager.get().save("userinfo", "");
                    if (App.selectAddress != null) {
                        App.selectAddress = null;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("flag", "1");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mCtx, LoginAct.class);
                    startActivity(intent);
                    finish();
                } else {
                    App.toast(MobileBindingAct.this, baseResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                App.toast(MobileBindingAct.this, "绑定失败");
            }
        });

    }

    private void getCode() {
        CodeBody codeBody = new CodeBody();
        codeBody.statu = AESUtils.encryptData("ath_yzm" + String.valueOf(System.currentTimeMillis()));

        AthService service = App.get().getAthService();
        service.edit_phone_password_yzm(codeBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse.status == 1) {
                    App.toast(
                            MobileBindingAct.this,
                            "验证码已发送，请注意查收");
                    checkCountDownTimer.start();
                } else {
                    showGetRegCodeRetry();
                    if (!CUtils.isEmpty(baseResponse.message)) {
                        App.toast(
                                MobileBindingAct.this,
                                baseResponse.message);
                    } else {
                        App.toast(
                                MobileBindingAct.this,
                                "获取失败,请重新获取");
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showGetRegCodeRetry();
                App.toast(
                        MobileBindingAct.this,
                        "获取失败,请重新获取");
            }
        });

    }

    class CheckCountDownTimer extends CountDownTimer {

        public CheckCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            showGetRegCodeRetry();// 倒计时之后显示重新获取
        }

        @Override
        public void onTick(long arg0) {
            showGetCodeWaiting((int) arg0 / 1000, totalCount);
        }

    }

    private void cancelCountDownTimer() {
        if (checkCountDownTimer != null) {
            checkCountDownTimer.cancel();
        }
    }

    private void showRequestView() {
        if (mHandler.hasMessages(MSG_WHAT_REQUEST_END)) {
            mHandler.removeMessages(MSG_WHAT_REQUEST_END);
        }
        mHandler.sendEmptyMessage(MSG_WHAT_REQUEST_ING);
    }

    private void hideRequestView() {
        if (mHandler.hasMessages(MSG_WHAT_REQUEST_ING)) {
            mHandler.removeMessages(MSG_WHAT_REQUEST_ING);
        }
        mHandler.sendEmptyMessage(MSG_WHAT_REQUEST_END);
    }

    private void showGetRegCodeRetry() {
        mHandler.sendEmptyMessage(CODE_RETRY);
    }

    private void showGetBindCodeRequesting() {
        mHandler.sendEmptyMessage(CODE_REQUESTING);
    }

    // 60S倒计时
    private void showGetCodeWaiting(int spaceCount, int totalCount) {
        Message msg = Message.obtain();
        msg.arg1 = spaceCount;
        msg.arg2 = totalCount;
        msg.what = CODE_WAITING;
        mHandler.sendMessage(msg);
    }

    private void requestingView() {
        btnBinding.setEnabled(false);
        btnBinding.setText("正在绑定...");
    }

    private void requestendView() {
        btnBinding.setEnabled(true);
        btnBinding.setText("立即绑定");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountDownTimer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MobileBindingAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MobileBindingAct");
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
            case R.id.btnCode:
                phoneCheck();
                break;
            case R.id.btnBinding:
                binding();
                break;
        }
    }
}
