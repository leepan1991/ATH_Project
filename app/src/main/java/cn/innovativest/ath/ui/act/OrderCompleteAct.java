package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.R;
import cn.innovativest.ath.ui.BaseAct;

/**
 * Created by leepan on 21/03/2018.
 */

public class OrderCompleteAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.tvSaleRole)
    TextView tvSaleRole;

    @BindView(R.id.tvOrderManagement)
    TextView tvOrderManagement;

    @BindView(R.id.tvOrderPrice)
    TextView tvOrderPrice;

    @BindView(R.id.tvTradeNumber)
    TextView tvTradeNumber;

    @BindView(R.id.tvPayCode)
    TextView tvPayCode;

    @BindView(R.id.tvOrderId)
    TextView tvOrderId;

    @BindView(R.id.tvOrderTime)
    TextView tvOrderTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_complete_act);
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setOnClickListener(this);
        tvwTitle.setText("购买订单");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("OrderCompleteAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OrderCompleteAct");
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
        }
    }
}
