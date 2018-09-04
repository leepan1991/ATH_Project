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
import cn.innovativest.ath.entities.ModifyLoginBody;
import cn.innovativest.ath.entities.ModifyLoginYzmBody;
import cn.innovativest.ath.response.BaseResponse;
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

public class ChangeLoginPasswdAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etNewSinglePasswd)
    EditText etNewSinglePasswd;

    @BindView(R.id.etConfirmNewSinglePasswd)
    EditText etConfirmNewSinglePasswd;

    @BindView(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @BindView(R.id.edtUCode)
    EditText edtUCode;

    @BindView(R.id.btnCode)
    Button btnCode;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

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
        setContentView(R.layout.change_login_passwd_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        checkCountDownTimer = new CheckCountDownTimer(totalCount * 1000, 1000);
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("登录密码修改");

        btnBack.setOnClickListener(this);
        btnCode.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
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
        etNewSinglePasswd.setEnabled(enabled);
        etConfirmNewSinglePasswd.setEnabled(enabled);
        etPhoneNumber.setEnabled(enabled);
        edtUCode.setEnabled(enabled);
    }

    private void modify() {
        String pw1 = etNewSinglePasswd.getText().toString().trim();
        String pw2 = etConfirmNewSinglePasswd.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();
        String code = edtUCode.getText().toString().trim();

        if (TextUtils.isEmpty(pw1)) {
            App.toast(this, "请输入登录密码");
            return;
        }

        if (!AppUtils.isPassword(pw1)) {
            App.toast(this, "请输入6-20位数字或字母");
            return;
        }

        if (TextUtils.isEmpty(pw2)) {
            App.toast(this, "请确认登录密码");
            return;
        }

        if (!AppUtils.isPassword(pw2)) {
            App.toast(this, "请输入6-20位数字或字母");
            return;
        }

        if (!pw1.equals(pw2)) {
            App.toast(this, "登录密码输入不一致");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            App.toast(this, "请输入手机号");
            return;
        }

        if (!checkPhoneNumber(phone)) {
            App.toast(getApplicationContext(), "请输入正确的手机号码");
            return;
        }


        if (TextUtils.isEmpty(code)) {
            App.toast(this, "请输入验证码");
            return;
        }

        request(phone, pw1, pw2, code);
    }

    public static boolean checkPhoneNumber(String phone) {
        Pattern p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private void request(String phone, String pwd1, String pwd2, String code) {

        ModifyLoginBody modifyLoginBody = new ModifyLoginBody();
        modifyLoginBody.phone = phone;
        modifyLoginBody.password1 = pwd1;
        modifyLoginBody.password2 = pwd2;
        modifyLoginBody.yzm = code;
        LoadingUtils.getInstance().dialogShow(this, "修改中...");

        AthService service = App.get().getAthService();
        service.forget_password(modifyLoginBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
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
                }
                App.toast(ChangeLoginPasswdAct.this, baseResponse.message);

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(ChangeLoginPasswdAct.this, "修改失败");
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    private void phoneCheck() {

        String pw1 = etNewSinglePasswd.getText().toString().trim();
        String pw2 = etConfirmNewSinglePasswd.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();

        if (TextUtils.isEmpty(pw1)) {
            App.toast(this, "请输入登录密码");
            return;
        }

        if (!AppUtils.isPassword(pw1)) {
            App.toast(this, "请输入6-20位数字或字母");
            return;
        }

        if (TextUtils.isEmpty(pw2)) {
            App.toast(this, "请确认登录密码");
            return;
        }

        if (!AppUtils.isPassword(pw2)) {
            App.toast(this, "请输入6-20位数字或字母");
            return;
        }

        if (!pw1.equals(pw2)) {
            App.toast(this, "登录密码输入不一致");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            App.toast(this, "请输入手机号");
            return;
        }

        if (!checkPhoneNumber(phone)) {
            App.toast(getApplicationContext(), "请输入正确的手机号码");
            return;
        }
        getCode(phone, pw1, pw2);
    }


    private void getCode(String phone, String pw1, String pw2) {
        ModifyLoginYzmBody modifyLoginYzmBody = new ModifyLoginYzmBody();
        modifyLoginYzmBody.statu = AESUtils.encryptData("ath_yzm" + String.valueOf(System.currentTimeMillis()));
        modifyLoginYzmBody.phone = phone;
        modifyLoginYzmBody.password1 = pw1;
        modifyLoginYzmBody.password2 = pw2;

        AthService service = App.get().getAthService();
        service.forget_password_yzm(modifyLoginYzmBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse.status == 1) {
                    App.toast(
                            ChangeLoginPasswdAct.this,
                            "验证码已发送，请注意查收");
                    checkCountDownTimer.start();
                } else {
                    showGetRegCodeRetry();
                    if (!CUtils.isEmpty(baseResponse.message)) {
                        App.toast(
                                ChangeLoginPasswdAct.this,
                                baseResponse.message);
                    } else {
                        App.toast(
                                ChangeLoginPasswdAct.this,
                                "获取失败,请重新获取");
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showGetRegCodeRetry();
                App.toast(
                        ChangeLoginPasswdAct.this,
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
        btnConfirm.setEnabled(false);
        btnConfirm.setText("正在修改...");
    }

    private void requestendView() {
        btnConfirm.setEnabled(true);
        btnConfirm.setText("确认");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelCountDownTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChangeLoginPasswdAct");
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChangeLoginPasswdAct");
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
            case R.id.btnConfirm:
                modify();
                break;
        }
    }
}
