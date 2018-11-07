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
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.PrefsManager;

/**
 * Created by leepan on 21/03/2018.
 */

public class About11Act extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.wvDesc)
    WebView wvDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_11_act);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("ATH活动");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            wvDesc.loadUrl("http://ath.pub/home/actives/index.html?phone=" + userInfo.phone + "&name=" + userInfo.name);
        } else {
            finish();
            return;
        }

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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AboutAthAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutAthAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }
}
