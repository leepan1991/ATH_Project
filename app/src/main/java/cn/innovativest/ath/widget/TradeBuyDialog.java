package cn.innovativest.ath.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.TradeItem;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;


public class TradeBuyDialog extends Dialog implements View.OnClickListener {
    public static int TYPE_CONFIRM = 1;
    public static int TYPE_TIP = 2;
    @BindView(R.id.dialog_msg_tv)
    TextView mMsgTv;

    @BindView(R.id.dialog_left_bt)
    Button mLeftBt;

    @BindView(R.id.dialog_right_bt)
    Button mRightBt;

    @BindView(R.id.dgAmount)
    TextView dgAmount;

    @BindView(R.id.dgLimit)
    TextView dgLimit;

    @BindView(R.id.edtSDSD)
    EditText edtSDSD;

    @BindView(R.id.dgAllBuy)
    TextView dgAllBuy;

    @BindView(R.id.dgMoney)
    TextView dgMoney;

    TradeItem tradeItem;

    ChooseTradeListener mListener;

    public TradeBuyDialog(final Context context) {
        super(context, R.style.mDialog);
        setContentView(R.layout.trade_buy_dialog);
        ButterKnife.bind(this);

        mLeftBt.setOnClickListener(this);
        mRightBt.setOnClickListener(this);
        dgAllBuy.setOnClickListener(this);
        edtSDSD.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = edtSDSD.getText().toString();
                if (!CUtils.isEmpty(number)) {
                    if (tradeItem != null) {
                        dgMoney.setVisibility(View.VISIBLE);
                        dgMoney.setText("￥" + String.format("%.2f", Float.parseFloat(tradeItem.getCny()) * Float.parseFloat(number)));
                    } else {
                        dgMoney.setVisibility(View.INVISIBLE);
                    }
                } else {
                    dgMoney.setVisibility(View.INVISIBLE);
//                    App.toast(context, "请输入正确购买数量");
//                    edtSDSD.setText("");
                }
            }
        });
    }

    public TradeBuyDialog isCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    public TradeBuyDialog setMsg(String msg) {

        mMsgTv.setText(msg);
        return this;
    }

    public void setTradeItem(TradeItem tradeItem) {
        this.tradeItem = tradeItem;
        if (!CUtils.isEmpty(tradeItem.getQuota()) && tradeItem.getQuota().contains(",")) {
            dgLimit.setText(tradeItem.getQuota().replace(",", " - ") + "元");

            String str[] = tradeItem.getQuota().split(",");
            if (str.length == 2) {
                edtSDSD.setText("");
                edtSDSD.setHint(AppUtils.floatToStringByTruncate(Double.parseDouble(str[0]) / Double.parseDouble(tradeItem.getCny()), 6) + " - " + AppUtils.floatToStringByTruncate(Double.parseDouble(str[1]) / Double.parseDouble(tradeItem.getCny()), 6));
            }
        }
        dgAmount.setText(String.format("%.2f", Float.parseFloat(tradeItem.getCny())) + " CNY");

    }

    public TradeBuyDialog setMRightBt(String mRightBtTxt) {

        mRightBt.setText(mRightBtTxt);
        return this;
    }

    public TradeBuyDialog setMLeftBtt(String mLeftBtTxt) {

        mLeftBt.setText(mLeftBtTxt);
        return this;
    }

    public TradeBuyDialog setDgAllBuy(String mDgAllButText) {
        dgAllBuy.setText(mDgAllButText);
        return this;
    }

    public TradeBuyDialog setEdtSDSDHint(String edtSDSDText) {
        edtSDSD.setHint(edtSDSDText);
        return this;
    }

    public String getEdtSDSDText() {
        return edtSDSD.getText().toString();
    }

    public TradeBuyDialog setIsCancelable(boolean flag) {

        setCancelable(flag);
        return this;
    }

    public TradeBuyDialog setType(int type) {
        if (type == TYPE_TIP) {
            findViewById(R.id.dialog_line).setVisibility(View.GONE);
            findViewById(R.id.dialog_left_bt).setVisibility(View.GONE);
        }
        return this;
    }

    public TradeBuyDialog setBtStr(String... BtStr) {

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

    public TradeBuyDialog setChooseTradeListener(ChooseTradeListener l) {
        mListener = l;
        return this;
    }

    @Override
    public void onClick(View v) {
        String vv[] = tradeItem.getQuota().split(",");
        switch (v.getId()) {
            case R.id.dialog_left_bt:
                dismiss();
                if (mListener != null) {
                    mListener.onChoose(ChooseTradeListener.WHICH_LEFT);
                }
                break;

            case R.id.dialog_right_bt:
                if (!CUtils.isEmpty(edtSDSD.getText().toString())) {

                    if (Float.parseFloat(edtSDSD.getText().toString()) <= 0) {
                        App.toast(getContext(), "输入数量不能小于等于0");
                        return;
                    }

                    if (!CUtils.isEmpty(tradeItem.getQuota()) && tradeItem.getQuota().contains(",")) {
                        String str[] = tradeItem.getQuota().split(",");
                        if (str.length == 2) {
                            if (Double.parseDouble(edtSDSD.getText().toString()) > Double.parseDouble(str[1]) / Double.parseDouble(tradeItem.getCny()) || Double.parseDouble(edtSDSD.getText().toString()) < Double.parseDouble(str[0]) / Double.parseDouble(tradeItem.getCny())) {
                                App.toast(getContext(), "输入数量不能小于最小限额或大于最大限额");
                                return;
                            }
                        }
                    }

                } else {
                    App.toast(getContext(), "请输入购买量");
                    return;
                }
                dismiss();
                if (mListener != null) {
                    mListener.onChoose(ChooseTradeListener.WHICH_RIGHT);
                }
                break;
            case R.id.dgAllBuy:
                if (!CUtils.isEmpty(tradeItem.getQuota()) && tradeItem.getQuota().contains(",")) {
                    String str[] = tradeItem.getQuota().split(",");
                    if (str.length == 2) {
//                        edtSDSD.setText(String.format("%.6f", Float.parseFloat(str[1]) / Float.parseFloat(tradeItem.getCny())));
//                        edtSDSD.setSelection(String.format("%.6f", Float.parseFloat(str[1]) / Float.parseFloat(tradeItem.getCny())).length());
                        edtSDSD.setText(AppUtils.floatToStringByTruncate(Double.parseDouble(str[1]) / Double.parseDouble(tradeItem.getCny()), 6));
                        edtSDSD.setSelection(AppUtils.floatToStringByTruncate(Double.parseDouble(str[1]) / Double.parseDouble(tradeItem.getCny()), 6).length());
                    }
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
