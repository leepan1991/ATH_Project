package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.PrefsManager;

/**
 * Created by leepan on 21/03/2018.
 */

public class PropertyManagementAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.ivLoginAvatar)
    ImageView ivLoginAvatar;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ivRealed)
    ImageView ivRealed;

    @BindView(R.id.tvRealNamed)
    TextView tvRealNamed;

    @BindView(R.id.tvATHAll)
    TextView tvATHAll;

    @BindView(R.id.tvMarketAll)
    TextView tvMarketAll;

    @BindView(R.id.tvDirectInvitePeople)
    TextView tvDirectInvitePeople;

    @BindView(R.id.tvCoinAll)
    TextView tvCoinAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_management_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("资产管理");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        }
    }

    private void initDataToView(UserInfo userInfo) {
        tvName.setText(userInfo.name);
        GlideApp.with(this).load(AppConfig.ATH_APP_URL + userInfo.head_img_link).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(ivLoginAvatar);
        if (userInfo.state == 1) {
            ivRealed.setVisibility(View.VISIBLE);
            tvRealNamed.setVisibility(View.VISIBLE);
        } else {
            ivRealed.setVisibility(View.INVISIBLE);
            tvRealNamed.setVisibility(View.INVISIBLE);
        }
        if (userInfo.mainPage != null) {
            tvATHAll.setText("ATH总量：" + (!CUtils.isEmpty(userInfo.mainPage.ath) ? String.format("%.6f", Float.parseFloat(userInfo.mainPage.ath)) : 0.000000));
            tvMarketAll.setText("市场估值：" + (!CUtils.isEmpty(userInfo.mainPage.exchange) ? String.format("%.2f", Float.parseFloat(userInfo.mainPage.exchange)) : 0.00));
            tvDirectInvitePeople.setText((!CUtils.isEmpty(userInfo.mainPage.help_value) ? String.format("%.2f", Float.parseFloat(userInfo.mainPage.help_value)) : 0.00 + ""));
            tvCoinAll.setText((!CUtils.isEmpty(userInfo.mainPage.integral) ? String.format("%.2f", Float.parseFloat(userInfo.mainPage.integral)) : 0.00 + ""));
        } else {
            tvATHAll.setText("ATH总量：0.000000");
            tvMarketAll.setText("市场估值：0.00");
            tvDirectInvitePeople.setText("0.00");
            tvCoinAll.setText("0.00");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PropertyManagementAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PropertyManagementAct");
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
