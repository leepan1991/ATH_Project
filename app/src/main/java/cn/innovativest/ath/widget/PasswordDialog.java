package cn.innovativest.ath.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.MD5Utils;
import cn.innovativest.ath.utils.PrefsManager;


public class PasswordDialog extends Dialog implements View.OnClickListener {
    public static int TYPE_CONFIRM = 1;
    public static int TYPE_TIP = 2;
    @BindView(R.id.dialog_msg_tv)
    TextView mMsgTv;

    @BindView(R.id.dialog_left_bt)
    Button mLeftBt;

    @BindView(R.id.dialog_right_bt)
    Button mRightBt;

    @BindView(R.id.edtPassword)
    EditText edtPassword;

    ChooseTradeListener mListener;

    public PasswordDialog(Context context) {
        super(context, R.style.mDialog);
        setContentView(R.layout.password_dialog);
        ButterKnife.bind(this);

        mLeftBt.setOnClickListener(this);
        mRightBt.setOnClickListener(this);
    }

    public PasswordDialog isCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    public EditText getEdtPassword() {
        return edtPassword;
    }

    public void setPassword() {
        edtPassword.setText("");
    }

    public PasswordDialog setMsg(String msg) {

        mMsgTv.setText(msg);
        return this;
    }

    public PasswordDialog setMRightBt(String mRightBtTxt) {

        mRightBt.setText(mRightBtTxt);
        return this;
    }

    public PasswordDialog setMLeftBtt(String mLeftBtTxt) {

        mLeftBt.setText(mLeftBtTxt);
        return this;
    }

    public PasswordDialog setIsCancelable(boolean flag) {

        setCancelable(flag);
        return this;
    }

    public PasswordDialog setType(int type) {
        if (type == TYPE_TIP) {
            findViewById(R.id.dialog_line).setVisibility(View.GONE);
            findViewById(R.id.dialog_left_bt).setVisibility(View.GONE);
        }
        return this;
    }

    public PasswordDialog setBtStr(String... BtStr) {

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

    public PasswordDialog setChooseTradeListener(ChooseTradeListener l) {
        mListener = l;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_left_bt:
                dismiss();
                if (mListener != null) {
                    mListener.onChoose(ChooseTradeListener.WHICH_LEFT);
                }
                break;

            case R.id.dialog_right_bt:
                if (CUtils.isEmpty(edtPassword.getText().toString().trim())) {
                    App.toast(getContext(), "请输入交易密码");
                    return;
                }

                if (CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    App.toast(getContext(), "请设置交易密码");
                    return;
                }
                if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                    if (CUtils.isEmpty(userInfo.check_pay_password)) {
                        App.toast(getContext(), "请设置交易密码");
                        return;
                    }
                    if (!userInfo.check_pay_password.equals(MD5Utils.MD5(edtPassword.getText().toString().trim() + "_#HYAK!"))) {
                        App.toast(getContext(), "交易密码不正确");
                        return;
                    }
                }

                dismiss();
                if (mListener != null) {
                    mListener.onChoose(ChooseTradeListener.WHICH_RIGHT);
                }
                break;
        }
    }

    public interface ChooseTradeListener {
        public final static int WHICH_LEFT = 1;
        public final static int WHICH_RIGHT = 2;

        /**
         * which=1,左button。 which=2,右button。
         */
        void onChoose(int which);
    }

}
