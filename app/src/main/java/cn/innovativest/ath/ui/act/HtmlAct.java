package cn.innovativest.ath.ui.act;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.CommonResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class HtmlAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.wvDesc)
    WebView wvDesc;

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_act);
        ButterKnife.bind(this);
        url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        initView();

    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("");
        tvwTitle.setVisibility(View.INVISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        wvDesc.loadUrl(url);

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
        MobclickAgent.onPageStart("HtmlAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HtmlAct");
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
