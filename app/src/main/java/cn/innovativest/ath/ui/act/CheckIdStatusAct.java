package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class CheckIdStatusAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.tvIDZ)
    ImageView tvIDZ;

    @BindView(R.id.tvIDB)
    ImageView tvIDB;

    @BindView(R.id.tvCheckAgain)
    TextView tvCheckAgain;

    @BindView(R.id.tvwStatus)
    TextView tvwStatus;

    @BindView(R.id.btnStatus)
    Button btnStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_id_status_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("上传身份证");
        btnBack.setOnClickListener(this);
        tvCheckAgain.setOnClickListener(this);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        }
    }

    private void initDataToView(UserInfo userInfo) {
        if (userInfo != null) {
            if (!CUtils.isEmpty(userInfo.id_img)) {
                String imgs[] = userInfo.id_img.split("\\|");
                if (imgs.length == 2) {
                    GlideApp.with(this).load(AppConfig.ATH_APP_URL + imgs[0]).into(tvIDZ);
                    GlideApp.with(this).load(AppConfig.ATH_APP_URL + imgs[1]).into(tvIDB);
                }
            }

            if (userInfo.state == 0) {
                tvwStatus.setText("请耐心等待，工作人员会在2-5个工作日内完成审核，在审核期间，请不要重复提交资料。");
                tvwStatus.setTextColor(Color.parseColor("#F1A361"));
                btnStatus.setText("审核中");
                btnStatus.setBackgroundResource(R.drawable.high_centification_checking);
                tvCheckAgain.setVisibility(View.INVISIBLE);
            } else if (userInfo.state == 1) {

                tvwStatus.setText("恭喜，您的审核已通过，认证成功！");
                tvwStatus.setTextColor(Color.rgb(32, 116, 180));
                btnStatus.setText("审核通过");
                btnStatus.setBackgroundResource(R.drawable.high_centification_check_pass);
                tvCheckAgain.setVisibility(View.INVISIBLE);
            } else if (userInfo.state == 2) {

                tvwStatus.setText("您的审核未通过，请重新上传您的身份证件，并仔细阅读上传身份证的要求，谢谢！");
                tvwStatus.setTextColor(Color.RED);
                btnStatus.setText("审核失败");
                btnStatus.setBackgroundResource(R.drawable.high_centification_check_failed);
                tvCheckAgain.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CheckIdStatusAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CheckIdStatusAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tvCheckAgain:
                startActivityForResult(new Intent(this, UploadIdAct.class), 100);
                break;
        }
    }
}
