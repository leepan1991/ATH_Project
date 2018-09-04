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

public class ApplyAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.rltMineApply)
    RelativeLayout rltMineApply;

    @BindView(R.id.rltOtherApply)
    RelativeLayout rltOtherApply;

    String order_number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_act);
        ButterKnife.bind(this);
        order_number = getIntent().getStringExtra("order_number");
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("查看申诉");
        btnBack.setOnClickListener(this);

        rltMineApply.setOnClickListener(this);
        rltOtherApply.setOnClickListener(this);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ApplyAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ApplyAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.rltMineApply:
                startActivity(new Intent(ApplyAct.this, ApplyDetailAct.class).putExtra("order_number", order_number).putExtra("flag", 1));
                break;
            case R.id.rltOtherApply:
                startActivity(new Intent(ApplyAct.this, ApplyDetailAct.class).putExtra("order_number", order_number).putExtra("flag", 2));
                break;
        }
    }
}
