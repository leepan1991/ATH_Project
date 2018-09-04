package cn.innovativest.ath.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cn.innovativest.ath.R;


public class CustomDialog extends Dialog implements View.OnClickListener {
    public static int TYPE_CONFIRM = 1;
    public static int TYPE_TIP = 2;
    TextView mMsgTv;
    Button mLeftBt, mRightBt;
    ChooseListener mListener;

    public CustomDialog(Context context) {
        super(context, R.style.mDialog);
        setContentView(R.layout.custom_dialog);
        mMsgTv = (TextView) findViewById(R.id.dialog_msg_tv);
        mLeftBt = (Button) findViewById(R.id.dialog_left_bt);
        mRightBt = (Button) findViewById(R.id.dialog_right_bt);

        mLeftBt.setOnClickListener(this);
        mRightBt.setOnClickListener(this);
    }

    public CustomDialog isCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    public CustomDialog setMsg(String msg) {

        mMsgTv.setText(msg);
        return this;
    }

    public CustomDialog setMRightBt(String mRightBtTxt) {

        mRightBt.setText(mRightBtTxt);
        return this;
    }

    public CustomDialog setMLeftBtt(String mLeftBtTxt) {

        mLeftBt.setText(mLeftBtTxt);
        return this;
    }

    public CustomDialog setIsCancelable(boolean flag) {

        setCancelable(flag);
        return this;
    }

    public CustomDialog setType(int type) {
        if (type == TYPE_TIP) {
            findViewById(R.id.dialog_line).setVisibility(View.GONE);
            findViewById(R.id.dialog_left_bt).setVisibility(View.GONE);
        }
        return this;
    }

    public CustomDialog setBtStr(String... BtStr) {

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

    public CustomDialog setChooseListener(ChooseListener l) {
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
                dismiss();
                if (mListener != null) {
                    mListener.onChoose(ChooseListener.WHICH_RIGHT);
                }
                break;
        }
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
