package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.Address;
import cn.innovativest.ath.bean.CoinItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.CoinBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.CoinDetailResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.MD5Utils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.PasswordDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class CoinDetailAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.tvwAction)
    TextView tvwAction;

    @BindView(R.id.rltAddress)
    RelativeLayout rltAddress;

    @BindView(R.id.tvwAddress)
    TextView tvwAddress;

    @BindView(R.id.iv_avatar)
    ImageView iv_avatar;

    @BindView(R.id.tvwKind)
    TextView tvwKind;

    @BindView(R.id.tvwDanwei)
    TextView tvwDanwei;

    @BindView(R.id.wvDesc)
    WebView wvDesc;

    private PasswordDialog passwordDialog;

//    @BindView(R.id.tvType)
//    TextView tvType;
//
//    @BindView(R.id.btnSubmit)
//    Button btnSubmit;

    private String id;
    CoinItem coinItem;
//    private String pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_detail_act);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        initView();
        request(id);
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("确认订单");
        btnBack.setOnClickListener(this);
        rltAddress.setOnClickListener(this);
        tvwAction.setText("提交");
        tvwAction.setVisibility(View.VISIBLE);
        tvwAction.setOnClickListener(this);
        passwordDialog = new PasswordDialog(mCtx);
//        btnSubmit.setOnClickListener(this);

        wvDesc.getSettings().setJavaScriptEnabled(true);
        wvDesc.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvDesc.getSettings().setSupportZoom(true);
        wvDesc.getSettings().setLoadWithOverviewMode(true);
        wvDesc.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wvDesc.getSettings().setDefaultTextEncodingName("UTF-8");
        wvDesc.getSettings().setDomStorageEnabled(true);
        wvDesc.getSettings().setUseWideViewPort(true);
        wvDesc.getSettings().setLoadWithOverviewMode(true);

        wvDesc.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        wvDesc.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。
                // view.loadUrl(url);
                return false;
            }
        });
    }

    private void request(String id) {
        LoadingUtils.getInstance().dialogShow(this, "请求中...");
        AthService service = App.get().getAthService();
        service.good_info(id).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<CoinDetailResponse>() {
            @Override
            public void call(CoinDetailResponse coinDetailResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (coinDetailResponse.status == 1) {

                    if (coinDetailResponse.eCoinDetail != null) {
                        if (!CUtils.isEmpty(coinDetailResponse.eCoinDetail.default_site)) {
                            App.selectAddress = coinDetailResponse.eCoinDetail.default_site;
                            tvwAddress.setText(coinDetailResponse.eCoinDetail.default_site.site);
                        } else {
                            tvwAddress.setText("请选择一个收获地址");
                        }
                        if (coinDetailResponse.eCoinDetail.goods != null) {
                            coinItem = coinDetailResponse.eCoinDetail.goods;
                            GlideApp.with(CoinDetailAct.this).load(AppConfig.ATH_APP_URL + coinDetailResponse.eCoinDetail.goods.img).into(iv_avatar);
                            tvwKind.setText(coinDetailResponse.eCoinDetail.goods.title);
                            tvwDanwei.setText("X1  " + coinDetailResponse.eCoinDetail.goods.danwei);
                            if (!CUtils.isEmpty(coinDetailResponse.eCoinDetail.goods.description)) {
                                wvDesc.setVisibility(View.VISIBLE);
                                wvDesc.loadDataWithBaseURL(null, coinDetailResponse.eCoinDetail.goods.description, "text/html", "utf-8", null);
                            } else {
                                wvDesc.setVisibility(View.INVISIBLE);
                            }

                        }
                    }

                }
                App.toast(CoinDetailAct.this, coinDetailResponse.message);

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(CoinDetailAct.this, "获取数据失败");
            }
        });
    }

    private void submitOrder(String pw) {

        if (App.selectAddress == null) {
            App.toast(this, "请选择收货地址");
            return;
        }

        CoinBody coinBody = new CoinBody();
        coinBody.good_id = id;
        coinBody.site_id = App.selectAddress.id + "";
        coinBody.pay_password = MD5Utils.MD5(pw + "_#HYAK!");
        AthService service = App.get().getAthService();
        service.submit_order(coinBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (baseResponse.status == 1) {
                    App.toast(CoinDetailAct.this, "积分兑换成功");
                    onBackPressed();
                } else {
                    App.toast(CoinDetailAct.this, baseResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(CoinDetailAct.this, "积分兑换失败");
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CoinDetailAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CoinDetailAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 101:
                if (resultCode == RESULT_OK) {
                    if (data != null && data.hasExtra("address")) {
                        App.selectAddress = (Address) data.getSerializableExtra("address");
                        tvwAddress.setText(App.selectAddress.site);
                    }
                }
                break;
        }
    }

    private void check() {
        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo")) && coinItem != null) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            if (userInfo != null) {
                if (Float.parseFloat(coinItem.need_integral) > Float.parseFloat(userInfo.mainPage.integral)) {
                    App.toast(CoinDetailAct.this, "当前积分不够，不能进行兑换");
                    return;
                }
            } else {
                App.toast(CoinDetailAct.this, "请登录再进行积分兑换");
            }
        } else {
            App.toast(CoinDetailAct.this, "请登录再进行积分兑换");
        }
        passwordDialog.setPassword();
        passwordDialog.setMRightBt("验证").setMsg("交易密码").setIsCancelable(true)
                .setChooseTradeListener(new PasswordDialog.ChooseTradeListener() {

                    @Override
                    public void onChoose(int which) {
                        if (which == WHICH_RIGHT) {
                            AppUtils.hideImm(CoinDetailAct.this);
                            submitOrder(passwordDialog.getEdtPassword().getText().toString());
//                            startActivity(new Intent(CoinDetailAct.this, CoinDetailAct.class).putExtra("pw", passwordDialog.getEdtPassword().getText().toString()).putExtra("id", coinItem.id + ""));
                        }
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.rltAddress:
                startActivityForResult(new Intent(CoinDetailAct.this, AddressListAct.class).putExtra("type", 1), 101);
                break;
            case R.id.tvwAction:
                check();
                break;
        }
    }
}
