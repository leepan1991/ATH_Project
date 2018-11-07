package cn.innovativest.ath.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.utils.CUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class PropertyDialog extends Dialog implements View.OnClickListener {
    public static int TYPE_CONFIRM = 1;
    public static int TYPE_TIP = 2;
    @BindView(R.id.dialog_msg_tv)
    TextView mMsgTv;

    @BindView(R.id.dialog_left_bt)
    Button mLeftBt;

    @BindView(R.id.dialog_right_bt)
    Button mRightBt;

    @BindView(R.id.edtSDSD)
    EditText edtSDSD;

    @BindView(R.id.edtScore)
    EditText edtScore;

    @BindView(R.id.edtUCode)
    EditText edtUCode;

    @BindView(R.id.btnCode)
    Button btnCode;

    ChooseListener mListener;

    // 计时器
    private CheckCountDownTimer checkCountDownTimer;

    private int totalCount = 60;
    private static final int MSG_WHAT_REQUEST_ING = 1;
    private static final int MSG_WHAT_REQUEST_END = 0;

    private static final int CODE_WAITING = 100;// 获取验证码等待
    private static final int CODE_RETRY = 101;// 重新获取验证码
    private static final int CODE_REQUESTING = 102;
    private static final int CODE_GET = 103;

    public PropertyDialog(final Context context) {
        super(context, R.style.mDialog);
        setContentView(R.layout.property_dialog);
        ButterKnife.bind(this);

        checkCountDownTimer = new CheckCountDownTimer(totalCount * 1000, 1000);
        mLeftBt.setOnClickListener(this);
        mRightBt.setOnClickListener(this);
        btnCode.setOnClickListener(this);

//        edtSDSD.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                String number = edtSDSD.getText().toString();
//                if (!CUtils.isEmpty(number)) {
//                    if (tradeItem != null) {
//                        dgMoney.setVisibility(View.VISIBLE);
//                        dgMoney.setText("￥" + String.format("%.2f", Float.parseFloat(tradeItem.getCny()) * Float.parseFloat(number)));
//                    } else {
//                        dgMoney.setVisibility(View.INVISIBLE);
//                    }
//                } else {
//                    dgMoney.setVisibility(View.INVISIBLE);
////                    App.toast(context, "请输入正确购买数量");
////                    edtSDSD.setText("");
//                }
//            }
//        });
    }

    public PropertyDialog isCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    public PropertyDialog setMsg(String msg) {
        mMsgTv.setText(msg);
        edtSDSD.setText("");
        edtScore.setText("");
        edtUCode.setText("");
        btnCode.setEnabled(true);
        btnCode.setText("获取验证码");
        edtSDSD.requestFocus();
        return this;
    }


    public PropertyDialog setMRightBt(String mRightBtTxt) {

        mRightBt.setText(mRightBtTxt);
        return this;
    }

    public PropertyDialog setMLeftBtt(String mLeftBtTxt) {

        mLeftBt.setText(mLeftBtTxt);
        return this;
    }

    public PropertyDialog setEdtSDSDHint(String edtSDSDText) {
        edtSDSD.setHint(edtSDSDText);
        return this;
    }

    public String getEdtSDSDText() {
        return edtSDSD.getText().toString();
    }

    public String getEdtScore() {
        return edtScore.getText().toString();
    }

    public void setEdtScore(EditText edtScore) {
        this.edtScore = edtScore;
    }

    public String getEdtUCode() {
        return edtUCode.getText().toString();
    }

    public void setEdtUCode(EditText edtUCode) {
        this.edtUCode = edtUCode;
    }

    public PropertyDialog setIsCancelable(boolean flag) {

        setCancelable(flag);
        return this;
    }

    public PropertyDialog setType(int type) {
        if (type == TYPE_TIP) {
            findViewById(R.id.dialog_line).setVisibility(View.GONE);
            findViewById(R.id.dialog_left_bt).setVisibility(View.GONE);
        }
        return this;
    }

    public PropertyDialog setBtStr(String... BtStr) {

        if (BtStr != null) {
            if (BtStr.length >= 1) {
                mLeftBt.setText(BtStr[0]);
            }
            if (BtStr.length >= 2) {
                mRightBt.setText(BtStr[1]);
            }

        }
        return this;
    }

    public PropertyDialog setChooseListener(ChooseListener l) {
        mListener = l;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_left_bt:
                dismiss();
                if (mListener != null) {
                    mListener.onChoose(ChooseListener.WHICH_LEFT);
                }
                break;
            case R.id.dialog_right_bt:
                wallet();
                break;
            case R.id.btnCode:
                code();
                break;
        }
    }

    private void wallet() {
        if (TextUtils.isEmpty(edtSDSD.getText().toString())) {
            App.toast(getContext(), "请输入ATH数量");
            return;
        }
        if (TextUtils.isEmpty(edtScore.getText().toString())) {
            App.toast(getContext(), "请输入积分数量");
            return;
        }
        if (TextUtils.isEmpty(edtUCode.getText().toString())) {
            App.toast(getContext(), "请输入验证码");
            return;
        }
        dismiss();
        if (mListener != null) {
            mListener.onChoose(ChooseListener.WHICH_RIGHT);
        }
    }


    private void code() {
        if (TextUtils.isEmpty(edtSDSD.getText().toString())) {
            App.toast(getContext(), "请输入ATH数量");
            return;
        }
        if (TextUtils.isEmpty(edtScore.getText().toString())) {
            App.toast(getContext(), "请输入积分数量");
            return;
        }
        getCode();
    }


    private void getCode() {

        AthService service = App.get().getAthService();
        service.ath_recharge_yzm().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse.status == 1) {
                    App.toast(
                            getContext(),
                            "验证码已发送，请注意查收");
                    checkCountDownTimer.start();
                } else {
                    showGetRegCodeRetry();
                    if (!CUtils.isEmpty(baseResponse.message)) {
                        App.toast(
                                getContext(),
                                baseResponse.message);
                    } else {
                        App.toast(
                                getContext(),
                                "获取失败,请重新获取");
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                showGetRegCodeRetry();
                App.toast(
                        getContext(),
                        "获取失败,请重新获取");
            }
        });

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_WAITING:
                    btnCode.setEnabled(false);
                    btnCode.setText(String.format(
                            getContext().getString(R.string.regist_code_wait), msg.arg1 + ""));
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
//                    requestendView();
                    notifyView(true);
                    break;
                case MSG_WHAT_REQUEST_ING:
//                    requestingView();
                    notifyView(false);
                    break;
            }

        }

    };

    private void notifyView(boolean enabled) {
        edtSDSD.setEnabled(enabled);
        edtScore.setEnabled(enabled);
        edtUCode.setEnabled(enabled);
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

    public interface ChooseListener {
        public final static int WHICH_LEFT = 1;
        public final static int WHICH_RIGHT = 2;

        /**
         * which=1,左button。 which=2,右button。
         */
        void onChoose(int which);
    }

}
