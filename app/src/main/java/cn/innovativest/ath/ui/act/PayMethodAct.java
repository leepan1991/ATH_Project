package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.R;
import cn.innovativest.ath.ui.BaseAct;

/**
 * Created by leepan on 21/03/2018.
 */

public class PayMethodAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.rltBank)
    RelativeLayout rltBank;

    @BindView(R.id.rltAlipay)
    RelativeLayout rltAlipay;

    @BindView(R.id.rltWechat)
    RelativeLayout rltWechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_pay_method_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setOnClickListener(this);
        tvwTitle.setText("选择支付方式");
        rltBank.setOnClickListener(this);
        rltAlipay.setOnClickListener(this);
        rltWechat.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PayMethodAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PayMethodAct");
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
            case 100:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                }
                finish();
                break;
            case 101:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                }
                finish();
                break;
            case 102:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, data);
                }
                finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.rltBank:
                startActivityForResult(new Intent(PayMethodAct.this, AddBankAct.class), 100);
                break;
            case R.id.rltAlipay:
                startActivityForResult(new Intent(PayMethodAct.this, AddAlipayAct.class), 101);
                break;
            case R.id.rltWechat:
                startActivityForResult(new Intent(PayMethodAct.this, AddWechatAct.class), 102);
                break;
        }
    }
}
