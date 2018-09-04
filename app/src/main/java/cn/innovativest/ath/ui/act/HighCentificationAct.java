package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class HighCentificationAct extends BaseAct {


    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etId)
    EditText etId;

    @BindView(R.id.btnUpload)
    Button btnUpload;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.high_certification_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("高级认证");
        btnBack.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnConfirm.setVisibility(View.INVISIBLE);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        }
    }

    private void initDataToView(UserInfo userInfo) {
        if (userInfo != null) {
            etName.setText(userInfo.real_name);
            etId.setText(userInfo.id_number);
            etName.setSelection(userInfo.real_name.length());
            etId.setSelection(userInfo.id_number.length());
            etName.setEnabled(false);
            etId.setEnabled(false);
            if (userInfo.state == 1 && !CUtils.isEmpty(userInfo.id_img)) {
                btnUpload.setText("查看");
            } else if (userInfo.state == 0 && !CUtils.isEmpty(userInfo.id_img)) {
                btnUpload.setText("待审核");
            } else if (userInfo.state == 2 && !CUtils.isEmpty(userInfo.id_img)) {
                btnUpload.setText("审核失败");
            } else {
                btnUpload.setText("立即上传");
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
        MobclickAgent.onPageStart("HighCentificationAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HighCentificationAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void requestUserInfo() {

        AthService service = App.get().getAthService();
        service.userInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        LogUtils.e(AESUtils.decryptData(userInfoResponse.data));
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(userInfoResponse.data), UserInfo.class);
                        if (userInfo != null) {
                            PrefsManager.get().save("userinfo", userInfoResponse.data);
                            initDataToView(userInfo);
                        } else {
                            LogUtils.e("userInfo is null");
                        }
                    } else {
                        LogUtils.e("userInfoResponse.data is null");
                    }

                } else {
                    LogUtils.e(userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("获取失败");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnUpload:
                if (userInfo != null) {
                    if (userInfo.state == 3) {
                        startActivityForResult(new Intent(this, UploadIdAct.class), 100);
                    } else {
                        startActivity(new Intent(this, CheckIdStatusAct.class));
                    }
                } else {
                    startActivity(new Intent(this, UploadIdAct.class));
                }
                break;
            case R.id.btnConfirm:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
