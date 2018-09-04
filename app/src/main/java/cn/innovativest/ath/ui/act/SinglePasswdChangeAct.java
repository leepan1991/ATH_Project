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

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.CodeBody;
import cn.innovativest.ath.entities.ModifySinglePasswordBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class SinglePasswdChangeAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etOldSinglePasswd)
    EditText etOldSinglePasswd;

    @BindView(R.id.etConfirmNewSinglePasswd)
    EditText etConfirmNewSinglePasswd;

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
        setContentView(R.layout.change_single_passwd_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        checkCountDownTimer = new CheckCountDownTimer(totalCount * 1000, 1000);
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("交易密码更改");

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
        etOldSinglePasswd.setEnabled(enabled);
        etConfirmNewSinglePasswd.setEnabled(enabled);
        edtUCode.setEnabled(enabled);
    }

    private void modify() {
        String pw1 = etOldSinglePasswd.getText().toString().trim();
        String pw2 = etConfirmNewSinglePasswd.getText().toString().trim();
        String code = edtUCode.getText().toString().trim();

        if (TextUtils.isEmpty(pw1)) {
            App.toast(this, "请输入交易密码");
            return;
        }

        if (pw1.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }
        if (TextUtils.isEmpty(pw2)) {
            App.toast(this, "请确认交易密码");
            return;
        }
        if (pw2.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }

        if (!pw1.equals(pw2)) {
            App.toast(this, "交易密码输入不一致");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            App.toast(this, "请输入验证码");
            return;
        }

        request(pw1, pw2, code);
    }


    private void request(final String pwd1, final String pwd2, final String code) {

        ModifySinglePasswordBody modifySinglePasswordBody = new ModifySinglePasswordBody();
        modifySinglePasswordBody.pay_password1 = pwd1;
        modifySinglePasswordBody.pay_password2 = pwd2;
        modifySinglePasswordBody.yzm = code;
        LoadingUtils.getInstance().dialogShow(this, "修改中...");

        AthService service = App.get().getAthService();
        service.edit_pay_password(modifySinglePasswordBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (baseResponse.status == 1) {
                    onBackPressed();
                } else {
                    App.toast(SinglePasswdChangeAct.this, baseResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(SinglePasswdChangeAct.this, "注册失败");
            }
        });

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    private void phoneCheck() {
        String pw1 = etOldSinglePasswd.getText().toString().trim();
        String pw2 = etConfirmNewSinglePasswd.getText().toString().trim();

        if (TextUtils.isEmpty(pw1)) {
            App.toast(this, "请输入交易密码");
            return;
        }

        if (pw1.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }
        if (TextUtils.isEmpty(pw2)) {
            App.toast(this, "请确认交易密码");
            return;
        }
        if (pw2.length() != 6) {
            App.toast(this, "请输入6位交易密码");
            return;
        }

        if (!pw1.equals(pw2)) {
            App.toast(this, "交易密码输入不一致");
            return;
        }
        getCode();
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
                            SinglePasswdChangeAct.this,
                            "验证码已发送，请注意查收");
                    checkCountDownTimer.start();
                } else {
                    showGetRegCodeRetry();
                    if (!CUtils.isEmpty(baseResponse.message)) {
                        App.toast(
                                SinglePasswdChangeAct.this,
                                baseResponse.message);
                    } else {
                        App.toast(
                                SinglePasswdChangeAct.this,
                                "获取失败,请重新获取");
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showGetRegCodeRetry();
                App.toast(
                        SinglePasswdChangeAct.this,
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
        MobclickAgent.onPageStart("SinglePasswdChangeAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SinglePasswdChangeAct");
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
