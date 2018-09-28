package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.Speed;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.SpeedResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.SpeedValueDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class SpeedValueAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.tvwChuji)
    TextView tvwChuji;

    @BindView(R.id.tvwGaoji)
    TextView tvwGaoji;

    @BindView(R.id.tvwPerHelpValue)
    TextView tvwPerHelpValue;

    @BindView(R.id.ibChange)
    ImageButton ibChange;

    @BindView(R.id.tvwPerCoin)
    TextView tvwPerCoin;

    @BindView(R.id.ibChange1)
    ImageButton ibChange1;

    @BindView(R.id.tvwBuy)
    TextView tvwBuy;

    @BindView(R.id.ibBuy)
    ImageButton ibBuy;

    Speed speed = null;

    private CustomDialog customDialog;

    private SpeedValueDialog speedValueDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_value_act);
        ButterKnife.bind(this);
        initView();
        getSpeedValueData();
    }


    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("财富加速");

        customDialog = new CustomDialog(mCtx);
        speedValueDialog = new SpeedValueDialog(mCtx, this);
        btnBack.setOnClickListener(this);

        ibChange.setOnClickListener(this);
        ibChange1.setOnClickListener(this);
        ibBuy.setOnClickListener(this);
    }

    private void initDataToView() {
        tvwChuji.setText("x" + speed.speedHead.primary);
        tvwGaoji.setText("x" + speed.speedHead.senior);
        if (speed != null && speed.speedBody != null && speed.speedBody.speedPrimary != null && !CUtils.isEmpty(speed.speedBody.speedPrimary.help_value) && speed.speedBody.speedPrimary.help_value.contains("|")) {
            String[] string = speed.speedBody.speedPrimary.help_value.split("\\|");
            if (string.length == 2) {
                tvwPerHelpValue.setText("2号矿机" + string[0] + "助力值\n" +
                        "3号矿机" + string[1] + "助力值");
            }

        }
        if (speed != null && speed.speedBody != null && speed.speedBody.speedPrimary != null && !CUtils.isEmpty(speed.speedBody.speedPrimary.integral) && speed.speedBody.speedPrimary.integral.contains("|")) {
            String[] string = speed.speedBody.speedPrimary.integral.split("\\|");
            if (string.length == 2) {
                tvwPerCoin.setText("2号矿机" + string[0] + "积分值\n" +
                        "3号矿机" + string[1] + "积分值");
            }
        }

        if (speed != null && speed.speedBody != null && speed.speedBody.speedSenior != null && !CUtils.isEmpty(speed.speedBody.speedSenior.rmb) && speed.speedBody.speedSenior.rmb.contains("|")) {
            String[] string = speed.speedBody.speedSenior.rmb.split("\\|");
            if (string.length == 2) {
                tvwBuy.setText("RMB" + string[0] +
                        " - " + string[1] + "元/台");
            }
        }
    }

    private void pit_unlock(final String pit, String type) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pit", pit);
        map.put("type", type);
        AthService service = App.get().getAthService();
        service.pit_unlock(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LogUtils.e(baseResponse.message);
                App.toast(SpeedValueAct.this, baseResponse.message);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    private void getSpeedValueData() {

        AthService service = App.get().getAthService();
        service.caifujiasu().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<SpeedResponse>() {
            @Override
            public void call(SpeedResponse speedResponse) {
                if (speedResponse != null) {
                    if (speedResponse.speed != null) {
                        speed = speedResponse.speed;
                        initDataToView();
                    }
                } else {
                    App.toast(SpeedValueAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(SpeedValueAct.this, "数据获取失败");
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
        MobclickAgent.onPageStart("SpeedValueAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SpeedValueAct");
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
            case R.id.ibChange:
                if (speed != null && speed.speedBody != null && speed.speedBody.speedPrimary != null && !CUtils.isEmpty(speed.speedBody.speedPrimary.primary_id)) {
                    pit_unlock(speed.speedBody.speedPrimary.primary_id, "2");
                } else {
                    App.toast(SpeedValueAct.this, "数据错误");
                }
                break;
            case R.id.ibChange1:
                if (speed != null && speed.speedBody != null && speed.speedBody.speedPrimary != null && !CUtils.isEmpty(speed.speedBody.speedPrimary.primary_id)) {
                    pit_unlock(speed.speedBody.speedPrimary.primary_id, "1");
                } else {
                    App.toast(SpeedValueAct.this, "数据错误");
                }
                break;
            case R.id.ibBuy:
//                if (speed != null && speed.speedBody != null && !CUtils.isEmpty(speed.speedBody.senior_id)) {
                if (speed != null && speed.speedBody != null) {
//                    customDialog.setMRightBt("去充值").setMsg("支付宝充值解锁矿机").setIsCancelable(true)
//                            .setChooseListener(new CustomDialog.ChooseListener() {
//                                @Override
//                                public void onChoose(int which) {
//                                    if (which == WHICH_RIGHT) {
//                                        getOrderInfo();
//                                    }
//                                }
//                            }).show();
                    speedValueDialog.getPitData();
                    speedValueDialog.isCancelable(true).show();
                    // 将对话框的大小按屏幕大小的百分比设置
                    WindowManager windowManager = getWindowManager();
                    Display display = windowManager.getDefaultDisplay();
                    WindowManager.LayoutParams lp = speedValueDialog.getWindow().getAttributes();
                    lp.width = (int) (display.getWidth() * 0.8); //设置宽度
                    speedValueDialog.getWindow().setAttributes(lp);

                } else {
                    App.toast(SpeedValueAct.this, "数据有误");
                }
                break;

        }
    }
}
