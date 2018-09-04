package cn.innovativest.ath.ui.act;

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
import cn.innovativest.ath.entities.RegisterBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.LoginResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class RegisterAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @BindView(R.id.etPassword)
    EditText etPassword;

    @BindView(R.id.etPassAgain)
    EditText etPassAgain;

    @BindView(R.id.edtUCode)
    EditText edtUCode;

    @BindView(R.id.btnCode)
    Button btnCode;

    @BindView(R.id.etRecommendCode)
    EditText etRecommendCode;

    @BindView(R.id.btnRegister)
    Button btnRegister;

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
        setContentView(R.layout.register_act);
        ButterKnife.bind(this);
        initView();
        addListener();
    }

    private void initView() {
        checkCountDownTimer = new CheckCountDownTimer(totalCount * 1000, 1000);
        btnBack.setOnClickListener(this);
        btnCode.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void addListener() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("注册");
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
        etPhoneNumber.setEnabled(enabled);
        etPassword.setEnabled(enabled);
        etPassAgain.setEnabled(enabled);
        edtUCode.setEnabled(enabled);
        etRecommendCode.setEnabled(enabled);
    }

    private void register() {
        String phone = etPhoneNumber.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();
        String pwdagain = etPassAgain.getText().toString().trim();
        String code = edtUCode.getText().toString().trim();
        String rcode = etRecommendCode.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            App.toast(this, "请输入手机号");
            return;
        }
        if (!AppUtils.isMobileNO(phone)) {
            App.toast(RegisterAct.this, "请输入正确的手机号");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            App.toast(this, "请输入验证码");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            App.toast(this, "请输入密码");
            return;
        }
        if (!AppUtils.isPassword(pwd)) {
            App.toast(RegisterAct.this, "请输入6-20位数字或字母");
            return;
        }

        if (TextUtils.isEmpty(pwdagain)) {
            App.toast(this, "请再次输入密码");
            return;
        }
        if (!AppUtils.isPassword(pwdagain)) {
            App.toast(RegisterAct.this, "请输入6-20位数字或字母");
            return;
        }

        if (!pwd.equals(pwdagain)) {
            App.toast(RegisterAct.this, "密码输入不一致");
            return;
        }

        if (TextUtils.isEmpty(rcode)) {
            App.toast(this, "请输入推荐码");
            return;
        }

        request(phone, pwd, pwdagain, code, rcode);
    }

    public static boolean checkPhoneNumber(String phone) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private void phoneCheck(String phone) {
        if (TextUtils.isEmpty(phone)) {
            App.toast(getApplicationContext(), "请输入手机号码");
            return;
        }
        if (!checkPhoneNumber(phone)) {
            App.toast(getApplicationContext(), "请输入正确的手机号码");
            return;
        }
        getCode(phone);
    }

    private void request(final String phone, final String pwd, final String pwdagain, final String code, final String rcode) {

        RegisterBody registerBody = new RegisterBody();
        registerBody.phone = phone;
        registerBody.password = pwd;
        registerBody.password2 = pwdagain;
        registerBody.yzm = code;
        registerBody.parent_code = rcode;
        LoadingUtils.getInstance().dialogShow(this, "注册中...");

        AthService service = App.get().getAthService();
        service.register(registerBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<LoginResponse>() {
            @Override
            public void call(LoginResponse loginResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (loginResponse.status == 1) {
                    //注册成功
                    App.toast(RegisterAct.this, "注册成功");
                    PrefsManager.get().save("isRemember", false);
                    PrefsManager.get().save("phone", phone);
                    PrefsManager.get().save("password", "");
                    onBackPressed();
                } else {
                    App.toast(RegisterAct.this, loginResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(RegisterAct.this, "注册失败");
            }
        });

    }

    private void getCode(String phone) {
//        CodeBody codeBody = new CodeBody();
//        codeBody.phone = phone;
//        codeBody.statu = AESUtils.encryptData("ath_yzm" + String.valueOf(System.currentTimeMillis()));

        AthService service = App.get().getAthService();
        service.register_yzm(phone, AESUtils.encryptData("ath_yzm" + String.valueOf(System.currentTimeMillis()))).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse.status == 1) {
                    App.toast(
                            RegisterAct.this,
                            "验证码已发送，请注意查收");
                    checkCountDownTimer.start();
                } else {
                    showGetRegCodeRetry();
                    if (!CUtils.isEmpty(baseResponse.message)) {
                        App.toast(
                                RegisterAct.this,
                                baseResponse.message);
                    } else {
                        App.toast(
                                RegisterAct.this,
                                "获取失败,请重新获取");
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showGetRegCodeRetry();
                App.toast(
                        RegisterAct.this,
                        "获取失败,请重新获取");
            }
        });

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

    // 60S倒计时
    private void showGetCodeWaiting(int spaceCount, int totalCount) {
        Message msg = Message.obtain();
        msg.arg1 = spaceCount;
        msg.arg2 = totalCount;
        msg.what = CODE_WAITING;
        mHandler.sendMessage(msg);
    }

    private void requestingView() {
        btnRegister.setEnabled(false);
        btnRegister.setText("正在注册...");
    }

    private void requestendView() {
        btnRegister.setEnabled(true);
        btnRegister.setText("注册");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountDownTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RegisterAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RegisterAct");
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
                String phone = etPhoneNumber.getText().toString().trim();
                phoneCheck(phone);
                break;
            case R.id.btnRegister:
                register();
                break;
        }
    }
}
