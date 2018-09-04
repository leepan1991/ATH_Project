package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.TradeItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
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

public class StartTradeAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.spBuyAndSale)
    Spinner spBuyAndSale;

    @BindView(R.id.tvMethod)
    TextView tvMethod;

    @BindView(R.id.tvPrice)
    TextView tvPrice;

    @BindView(R.id.etPrice)
    EditText etPrice;

    @BindView(R.id.tvNumber)
    TextView tvNumber;

    @BindView(R.id.etNumber)
    EditText etNumber;

    @BindView(R.id.tvSaleAll)
    TextView tvSaleAll;

    @BindView(R.id.tvLimit)
    TextView tvLimit;

    @BindView(R.id.etLimit)
    EditText etLimit;

    @BindView(R.id.etLimitMax)
    EditText etLimitMax;

    @BindView(R.id.tvPasswd)
    TextView tvPasswd;

    @BindView(R.id.etPasswd)
    EditText etPasswd;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    @BindView(R.id.tvwSaleInfo)
    TextView tvwSaleInfo;

    @BindView(R.id.tvwBuyInfo)
    TextView tvwBuyInfo;

    private int flag = 0;

    TradeItem tradeItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_trade_act);
        ButterKnife.bind(this);
        flag = getIntent().getIntExtra("flag", 0);
        tradeItem = (TradeItem) getIntent().getSerializableExtra("tradeItem");
        initView();
        addListener();

    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("发起交易");

        if (flag == 0) {//买
            spBuyAndSale.setSelection(0);
            tvSaleAll.setVisibility(View.GONE);
            tvMethod.setText("买入");
            tvPrice.setText("买入单价:");
            tvNumber.setText("购买数量:");
            tvwBuyInfo.setVisibility(View.VISIBLE);
            tvwSaleInfo.setVisibility(View.GONE);
            tvwBuyInfo.setLineSpacing(0, 1.5f);

        } else if (flag == 1) {//卖
            spBuyAndSale.setSelection(1);
            tvSaleAll.setVisibility(View.VISIBLE);
            tvMethod.setText("卖出");
            tvPrice.setText("卖出单价:");
            tvNumber.setText("卖出数量:");
            tvwBuyInfo.setVisibility(View.GONE);
            tvwSaleInfo.setVisibility(View.VISIBLE);
            tvwSaleInfo.setLineSpacing(0, 1.5f);
        }

        initTradeItem();

    }

    private void initTradeItem() {
        if (tradeItem != null) {
            etPrice.setText(String.format("%.2f", Float.valueOf(tradeItem.getCny())));
            etNumber.setText(String.format("%.2f", Float.valueOf(tradeItem.getNumber())));
            String v[] = tradeItem.getQuota().split(",");
            if (v.length == 2) {
                etLimit.setText(v[0]);
                etLimitMax.setText(v[1]);
                etLimitMax.setSelection(v[1].length());
                etLimitMax.requestFocus();
            }
            btnCancel.setText("下架");
            spBuyAndSale.setEnabled(false);
        } else {
            btnCancel.setText("取消");
            spBuyAndSale.setEnabled(true);
        }
    }

    private void addListener() {
        btnBack.setOnClickListener(this);
        tvSaleAll.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String price = etPrice.getText().toString();
                if (!CUtils.isEmpty(price)) {
                    if (flag == 0) {//买
                        tvwBuyInfo.setText("1.为了好买入，建议单价大于（" + price + "）\n2.最低限额在0-（数量*" + price + "）之间\n4.最大限额大于最低限额小于（" + price + "）-（数量*" + price + "）之间");
                    } else if (flag == 1) {//卖
                        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                            if (userInfo != null && userInfo.mainPage != null) {
                                tvwSaleInfo.setText("1.单价必须大于等于（" + price + "）\n2.数量在0.000001-（" + userInfo.mainPage.ath + "）之间\n3.最低限额在（" + price + "）-（数量*" + price + "）之间\n4.最大限额在（" + price + "）-（数量*" + price + "）之间");
                            }
                        }

                    }
                } else {
                    tvwBuyInfo.setVisibility(View.GONE);
                    tvwSaleInfo.setVisibility(View.GONE);
                }
            }
        });

        etNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String number = etNumber.getText().toString();
                if (!CUtils.isEmpty(number)) {
                    if (flag == 0) {//买
                        tvwBuyInfo.setText("1.为了好买入，建议单价大于（" + etPrice.getText().toString() + "）\n2.最低限额在0-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(number) + "）之间\n4.最大限额大于最低限额小于（" + etPrice.getText().toString() + "）-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(number) + "）之间");
                    } else if (flag == 1) {//卖
                        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                            if (userInfo != null && userInfo.mainPage != null) {
                                tvwSaleInfo.setText("1.单价必须大于等于（" + etPrice.getText().toString() + "）\n2.数量在0.000001-（" + userInfo.mainPage.ath + "）之间\n3.最低限额在（" + etPrice.getText().toString() + "）-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(number) + "）之间\n4.最大限额在（" + etPrice.getText().toString() + "）-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(number) + "）之间");
                            }
                        }
                    }
                } else {
                    tvwBuyInfo.setVisibility(View.GONE);
                    tvwSaleInfo.setVisibility(View.GONE);
                }
            }
        });

        spBuyAndSale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                flag = i;
                if (flag == 0) {//买
                    tvSaleAll.setVisibility(View.GONE);
                    tvMethod.setText("买入");
                    tvPrice.setText("买入单价:");
                    tvNumber.setText("购买数量:");
                    tvwBuyInfo.setVisibility(View.VISIBLE);
                    tvwSaleInfo.setVisibility(View.GONE);
                    tvwBuyInfo.setLineSpacing(0, 1.5f);
                    if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                        if (userInfo != null && userInfo.mainPage != null) {
                            etNumber.setHint("最多可买" + userInfo.mainPage.ath);
                            if (!CUtils.isEmpty(etPrice.getText().toString()) && !CUtils.isEmpty(etNumber.getText().toString())) {
                                tvwBuyInfo.setText("1.为了好买入，建议单价大于（" + etPrice.getText().toString() + "）\n2.最低限额在0-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(etNumber.getText().toString()) + "）之间\n4.最大限额大于最低限额小于（" + etPrice.getText().toString() + "）-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(etNumber.getText().toString()) + "）之间");
                            } else if (!CUtils.isEmpty(etPrice.getText().toString())) {
                                tvwBuyInfo.setText("1.为了好买入，建议单价大于（" + etPrice.getText().toString() + "）\n2.最低限额在0-（数量*" + etPrice.getText().toString() + "）之间\n4.最大限额大于最低限额小于（" + etPrice.getText().toString() + "）-（数量*" + etPrice.getText().toString() + "）之间");
                            } else {
                                tvwBuyInfo.setText("1.为了好买入，建议单价大于（" + userInfo.mainPage.xishu + "）\n2.最低限额在0-（数量*" + userInfo.mainPage.xishu + "）之间\n4.最大限额大于最低限额小于（" + userInfo.mainPage.xishu + "）-（数量*" + userInfo.mainPage.xishu + "）之间");
                            }
                        }
                    }


                } else if (flag == 1) {//卖
                    tvSaleAll.setVisibility(View.VISIBLE);
                    tvMethod.setText("卖出");
                    tvPrice.setText("卖出单价:");
                    tvNumber.setText("卖出数量:");
                    tvwBuyInfo.setVisibility(View.GONE);
                    tvwSaleInfo.setVisibility(View.VISIBLE);
                    tvwSaleInfo.setLineSpacing(0, 1.5f);
                    if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                        if (userInfo != null && userInfo.mainPage != null) {
                            etNumber.setHint("最多可卖" + userInfo.mainPage.ath);


                            if (!CUtils.isEmpty(etPrice.getText().toString()) && !CUtils.isEmpty(etNumber.getText().toString())) {
                                tvwSaleInfo.setText("1.单价必须大于等于（" + etPrice.getText().toString() + "）\n2.数量在0.000001-（" + userInfo.mainPage.ath + "）之间\n3.最低限额在（" + etPrice.getText().toString() + "）-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(etNumber.getText().toString()) + "）之间\n4.最大限额在（" + etPrice.getText().toString() + "）-（" + Float.valueOf(etPrice.getText().toString()) * Float.valueOf(etNumber.getText().toString()) + "）之间");
                            } else if (!CUtils.isEmpty(etPrice.getText().toString())) {
                                tvwSaleInfo.setText("1.单价必须大于等于（" + etPrice.getText().toString() + "）\n2.数量在0.000001-（" + userInfo.mainPage.ath + "）之间\n3.最低限额在（" + etPrice.getText().toString() + "）-（数量*" + etPrice.getText().toString() + "）之间\n4.最大限额在（" + etPrice.getText().toString() + "）-（数量*" + etPrice.getText().toString() + "）之间");
                            } else {
                                tvwSaleInfo.setText("1.单价必须大于等于（" + userInfo.mainPage.xishu + "）\n2.数量在0.000001-（" + userInfo.mainPage.ath + "）之间\n3.最低限额在（" + userInfo.mainPage.xishu + "）-（数量*" + userInfo.mainPage.xishu + "）之间\n4.最大限额在（" + userInfo.mainPage.xishu + "）-（数量*" + userInfo.mainPage.xishu + "）之间");
                            }
                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void request() {
        String danjia = etPrice.getText().toString().trim();
        String limit = etLimit.getText().toString().trim();
        String limitmax = etLimitMax.getText().toString().trim();
        String number = etNumber.getText().toString().trim();
        String passwd = etPasswd.getText().toString().trim();

        if (flag == 0) {
            if (CUtils.isEmpty(danjia)) {
                App.toast(StartTradeAct.this, "买入单价不能为空");
                return;
            }

            if (CUtils.isEmpty(number)) {
                App.toast(StartTradeAct.this, "购买数量不能为空");
                return;
            }

            if (CUtils.isEmpty(limit)) {
                App.toast(StartTradeAct.this, "最低限额不能为空");
                return;
            }

            if (CUtils.isEmpty(passwd)) {
                App.toast(StartTradeAct.this, "交易密码不能为空");
                return;
            }

            if (passwd.length() != 6) {
                App.toast(StartTradeAct.this, "请输入6位交易密码");
                return;
            }

            if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                if (CUtils.isEmpty(userInfo.check_pay_password)) {
                    App.toast(this, "请设置交易密码");
                    return;
                }
                if (!userInfo.check_pay_password.equals(MD5Utils.MD5(etPasswd.getText().toString().trim() + "_#HYAK!"))) {
                    App.toast(this, "交易密码不正确");
                    return;
                }

                if (Float.valueOf(number) > Float.valueOf(userInfo.mainPage.ath)) {
                    App.toast(StartTradeAct.this, "数量输入有误");
                    return;
                }
            } else {
                App.toast(StartTradeAct.this, "请设置交易密码");
                return;
            }
            releaseSellOrder(danjia, limit, limitmax, number, "2");

        } else if (flag == 1) {
            if (CUtils.isEmpty(danjia)) {
                App.toast(StartTradeAct.this, "卖出单价不能为空");
                return;
            }

            if (CUtils.isEmpty(number)) {
                App.toast(StartTradeAct.this, "卖出数量不能为空");
                return;
            }

            if (CUtils.isEmpty(limit)) {
                App.toast(StartTradeAct.this, "最低限额不能为空");
                return;
            }

            if (CUtils.isEmpty(passwd)) {
                App.toast(StartTradeAct.this, "交易密码不能为空");
                return;
            }

            if (passwd.length() != 6) {
                App.toast(StartTradeAct.this, "请输入6位交易密码");
                return;
            }
            if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                if (CUtils.isEmpty(userInfo.check_pay_password)) {
                    App.toast(this, "请设置交易密码");
                    return;
                }
                if (!userInfo.check_pay_password.equals(MD5Utils.MD5(etPasswd.getText().toString().trim() + "_#HYAK!"))) {
                    App.toast(this, "交易密码不正确");
                    return;
                }
            } else {
                App.toast(StartTradeAct.this, "请设置交易密码");
                return;
            }
            releaseSellOrder(danjia, limit, limitmax, number, "1");
        }


    }


    private void releaseSellOrder(String danjia, String quota_min, String quota_max, String number, String type) {
        AthService service = App.get().getAthService();
        service.releaseSellOrder(danjia, quota_min, quota_max, number, type).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                App.toast(StartTradeAct.this, baseResponse.message);
                if (baseResponse.status == 1) {
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
        MobclickAgent.onPageStart("StartTradeAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StartTradeAct");
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
            case R.id.btnCancel:
                if (tradeItem != null) {//调用产品下架接口

                } else {
                    finish();
                }
                break;
            case R.id.btnConfirm:
                request();
                break;
            case R.id.tvSaleAll:
                if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                    if (userInfo != null && userInfo.mainPage != null) {
                        etNumber.setText(userInfo.mainPage.ath);
                        etNumber.setSelection(userInfo.mainPage.ath.length());
                    }
                } else {
                    App.toast(StartTradeAct.this, "请先登录");
                }
                break;
        }
    }
}
