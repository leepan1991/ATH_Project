package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.DetailFund;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.FundDetailResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class FundDetailAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.ivFundImg1)
    ImageView ivFundImg1;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvATH)
    TextView tvATH;

    @BindView(R.id.tvHelp)
    TextView tvHelp;

    @BindView(R.id.tvScore)
    TextView tvScore;

    @BindView(R.id.btnPubName)
    Button btnPubName;

    @BindView(R.id.tvwPubName)
    TextView tvwPubName;

    @BindView(R.id.tvwStatus)
    TextView tvwStatus;

    @BindView(R.id.tvwDesc)
    TextView tvwDesc;

    @BindView(R.id.tvRegisterNum)
    TextView tvRegisterNum;

    @BindView(R.id.tvFundNum)
    TextView tvFundNum;

    @BindView(R.id.tvShouyi)
    TextView tvShouyi;

    @BindView(R.id.ivImg1)
    ImageView ivImg1;

    @BindView(R.id.ivImg2)
    ImageView ivImg2;

    @BindView(R.id.btnShare)
    Button btnShare;

    @BindView(R.id.btnStartZc)
    Button btnStartZc;

    private String id;

    DetailFund detailFund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_detail_act);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");

        initView();

    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("众筹详情");
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnStartZc.setOnClickListener(this);
        getDetail(id);
    }

    private void initDataToView(DetailFund detailFund) {
        tvName.setText(detailFund.detailFundText.title);
        tvATH.setText(detailFund.persons + "");
        tvHelp.setText("￥" + detailFund.reach_rmb + " 万元");
        tvScore.setText(detailFund.bai_fen_bi + " %");
        tvwPubName.setText(detailFund.detailFundUser.name);
        tvwStatus.setText(detailFund.status + ".剩余" + AppUtils.differentDaysByMillisecond(detailFund.stop_time, detailFund.ctime) + "天");
        tvwDesc.setText(detailFund.detailFundText.text);
        tvRegisterNum.setText("￥" + detailFund.target_rmb + " 万元");
        tvFundNum.setText(AppUtils.differentDaysByMillisecond(System.currentTimeMillis() / 1000, detailFund.ctime) + "天");
        tvShouyi.setText(detailFund.bai_fen_bi + "%");

        if (detailFund.detailFundText.img_link.contains("|")) {
            String[] imgs = detailFund.detailFundText.img_link.split("\\|");
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + imgs[0]).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).into(ivImg1);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + imgs[1]).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).into(ivImg2);
        }

    }

    private void getDetail(String id) {
        AthService service = App.get().getAthService();
        service.crowd_funding_details(id).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<FundDetailResponse>() {
            @Override
            public void call(FundDetailResponse fundDetailResponse) {
                if (fundDetailResponse != null) {
                    if (fundDetailResponse.detailFund != null) {
                        detailFund = fundDetailResponse.detailFund;
                        initDataToView(fundDetailResponse.detailFund);
                    } else {
                        App.toast(FundDetailAct.this, fundDetailResponse.message);
                    }
                } else {
                    App.toast(FundDetailAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("数据获取失败");
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
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnShare:
                break;
            case R.id.btnStartZc:
                break;
        }
    }
}
