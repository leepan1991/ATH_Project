package cn.innovativest.ath.ui.frag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.CommonResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseFrag;
import cn.innovativest.ath.ui.act.AboutUsAct;
import cn.innovativest.ath.ui.act.AccountSettingAct;
import cn.innovativest.ath.ui.act.CoinTradeManagementAct;
import cn.innovativest.ath.ui.act.LoginAct;
import cn.innovativest.ath.ui.act.MyTradeAct;
import cn.innovativest.ath.ui.act.OrderManAct;
import cn.innovativest.ath.ui.act.PropertyManagementAct;
import cn.innovativest.ath.ui.act.RealCentificationAct;
import cn.innovativest.ath.ui.act.SafetyManagementAct;
import cn.innovativest.ath.ui.act.SettingAct;
import cn.innovativest.ath.ui.act.SettingUserInfoOneAct;
import cn.innovativest.ath.ui.act.SettingUserInfoTwoAct;
import cn.innovativest.ath.ui.act.StartTradeAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.ScrollTextView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;

/**
 * Created by leepan on 20/03/2018.
 */

public class MineFrag extends BaseFrag {
    private View contentView;

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.lltAction)
    LinearLayout lltAction;

    @BindView(R.id.btnAction)
    ImageButton btnAction;

    @BindView(R.id.rltNotLogin)
    RelativeLayout rltNotLogin;

    @BindView(R.id.btnNotLogin)
    Button btnNotLogin;

    @BindView(R.id.rltLogined)
    RelativeLayout rltLogined;

    @BindView(R.id.ivLoginAvatar)
    ImageView ivLoginAvatar;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ivRealed)
    ImageView ivRealed;

    @BindView(R.id.tvRealNamed)
    TextView tvRealNamed;

    @BindView(R.id.tvwNotice)
    ScrollTextView tvwNotice;

    @BindView(R.id.btnSign)
    Button btnSign;

    @BindView(R.id.rltOrderManagement)
    RelativeLayout rltOrderManagement;

    @BindView(R.id.rltCoinManagement)
    RelativeLayout rltCoinManagement;

    @BindView(R.id.rltPubManagement)
    RelativeLayout rltPubManagement;

    @BindView(R.id.rltAdPubManagement)
    RelativeLayout rltAdPubManagement;

    @BindView(R.id.rltBuyAndSale)
    RelativeLayout rltBuyAndSale;

    @BindView(R.id.rltSafetyManagement)
    RelativeLayout rltSafetyManagement;

    @BindView(R.id.rltPropertyManagement)
    RelativeLayout rltPropertyManagement;

    @BindView(R.id.rltRealName)
    RelativeLayout rltRealName;

    @BindView(R.id.rltAccountSetting)
    RelativeLayout rltAccountSetting;

    @BindView(R.id.rltAboutUs)
    RelativeLayout rltAboutUs;

    @BindView(R.id.lltBuy)
    LinearLayout lltBuy;

    @BindView(R.id.lltSale)
    LinearLayout lltSale;

    private boolean isOpen = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.mine_frag, container,
                    false);
            ButterKnife.bind(this, contentView);
            addListener();
            initView();
        }

        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;

    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);

        btnBack.setVisibility(View.INVISIBLE);
        btnAction.setImageResource(R.drawable.mine_setting);
        btnAction.setVisibility(View.VISIBLE);
        lltAction.setOnClickListener(this);
        lltAction.setVisibility(View.VISIBLE);
        tvwTitle.setText("个人中心");
//        init();
    }

    private void init() {
        if (App.get().user == null) {
            rltNotLogin.setVisibility(View.VISIBLE);
            rltLogined.setVisibility(View.GONE);
        } else {
            rltNotLogin.setVisibility(View.GONE);
            rltLogined.setVisibility(View.VISIBLE);
            if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                initDataToView(userInfo);
            }
            requestUserInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainAct");
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainAct");
    }

    private void androidInfo() {
        AthService service = App.get().getAthService();
        service.androidInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (baseResponse.status == 1) {
                        isOpen = false;
                    } else {
                        isOpen = true;
                    }
                } else {
                    isOpen = true;
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                isOpen = true;
            }
        });
    }

    private void popDialog() {
        //    通过AlertDialog.Builder这个类来实例化我们的一个AlertDialog的对象
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //    设置Title的内容
        builder.setTitle("消息提示");
        //    设置Content来显示一个信息
        builder.setMessage("9月开放，敬请期待！");
        //    设置一个PositiveButton
        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(MainActivity.this, "positive: " + which, Toast.LENGTH_SHORT).show();
            }
        });
        //    显示出该对话框
        builder.show();
    }

    private void addListener() {
        btnAction.setOnClickListener(this);
        rltNotLogin.setOnClickListener(this);
        btnNotLogin.setOnClickListener(this);
        ivLoginAvatar.setOnClickListener(this);
        btnSign.setOnClickListener(this);
        rltOrderManagement.setOnClickListener(this);
        rltCoinManagement.setOnClickListener(this);
        rltPubManagement.setOnClickListener(this);
        rltAdPubManagement.setOnClickListener(this);
        rltSafetyManagement.setOnClickListener(this);
        rltPropertyManagement.setOnClickListener(this);
        rltRealName.setOnClickListener(this);
        rltAccountSetting.setOnClickListener(this);
        rltAboutUs.setOnClickListener(this);

        lltBuy.setOnClickListener(this);
        lltSale.setOnClickListener(this);

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

        if (userInfo.is_sign_in == 0) {
            btnSign.setText("已签到");
        } else if (userInfo.is_sign_in == 1) {
            btnSign.setText("未签到");
        }
    }


    private void requestUserInfo() {
//        LoadingUtils.getInstance().dialogShow(getContext(), "获取中...");

        AthService service = App.get().getAthService();
        service.userInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
//                LoadingUtils.getInstance().dialogDismiss();
                getCommonData();
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
                    App.toast(getActivity(), userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
//                LoadingUtils.getInstance().dialogDismiss();
                getCommonData();
                App.toast(getActivity(), "获取失败");
            }
        });
    }

    private void getCommonData() {

        AthService service = App.get().getAthService();
        service.commonInfo(7).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<CommonResponse>() {
            @Override
            public void call(CommonResponse commonResponse) {
                androidInfo();
                if (commonResponse != null) {
//                    if (commonResponse.commonItems != null && commonResponse.commonItems.size() > 0) {
////                        initDataToView(tradeResponse.tradeItems);
//                        for (CommonItem commonItem : commonResponse.commonItems) {
//                            if (commonItem.title.equals("系统公告")) {
//                                tvwNotice.setText(commonItem.exchange);
////                                tvwNotice.setTimes(20000);
//                            }
//                        }
//                    }
                    if (commonResponse.commonItem != null) {
//                        initDataToView(tradeResponse.tradeItems);
                        if (commonResponse.commonItem.title.equals("系统公告")) {
                            tvwNotice.setText(commonResponse.commonItem.exchange);
//                                tvwNotice.setTimes(20000);
                        }
                    } else {
                        App.toast(getActivity(), commonResponse.message);
                    }
                } else {
                    App.toast(getActivity(), "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                androidInfo();
//                App.toast(getActivity(), "数据获取失败");
            }
        });

    }

    private void sign() {
        AthService service = App.get().getAthService();
        service.sign_in().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (baseResponse.status == 1) {
                        btnSign.setText("已签到");
                    }
                    App.toast(getActivity(), baseResponse.message);
                } else {
                    App.toast(getActivity(), "签到失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(getActivity(), "签到失败");
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    rltNotLogin.setVisibility(View.GONE);
                    rltLogined.setVisibility(View.VISIBLE);
                    requestUserInfo();
                    getCommonData();
                }
                break;
            case 101:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAction:
                if (App.get().user != null) {
                    startActivityForResult(new Intent(getActivity(), SettingAct.class), 101);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.lltAction:
                if (App.get().user != null) {
                    startActivityForResult(new Intent(getActivity(), SettingAct.class), 101);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnNotLogin:
                startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                break;
            case R.id.rltNotLogin:
                startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                break;
            case R.id.ivLoginAvatar:
                //
                break;
            case R.id.rltOrderManagement:
                if (App.get().user != null) {
                    if (!isOpen) {
                        startActivity(new Intent(getActivity(), OrderManAct.class));
                    } else {
                        popDialog();
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnSign:
                sign();
                break;
            case R.id.rltCoinManagement:
                if (App.get().user != null) {
                    startActivity(new Intent(getActivity(), CoinTradeManagementAct.class));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.rltPubManagement:
                if (App.get().user != null) {
                    if (!isOpen) {
                        startActivity(new Intent(getActivity(), MyTradeAct.class));
                    } else {
                        popDialog();
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.rltAdPubManagement:
                if (!isOpen) {
                    if (rltBuyAndSale.getVisibility() == View.VISIBLE) {
                        rltBuyAndSale.setVisibility(View.GONE);
                    } else {
                        rltBuyAndSale.setVisibility(View.VISIBLE);
                    }
                } else {
                    popDialog();
                }
                break;
            case R.id.rltSafetyManagement:
                if (App.get().user != null) {
                    startActivity(new Intent(getActivity(), SafetyManagementAct.class));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.rltPropertyManagement:
                if (App.get().user != null) {
                    startActivity(new Intent(getActivity(), PropertyManagementAct.class));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.rltRealName:
                if (App.get().user != null) {
                    startActivity(new Intent(getActivity(), RealCentificationAct.class));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.rltAccountSetting:
                if (App.get().user != null) {

                    if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                        if (userInfo != null && !CUtils.isEmpty(userInfo.real_name)) {
                            startActivity(new Intent(getActivity(), AccountSettingAct.class));
                        } else {
                            startActivityForResult(new Intent(getActivity(), SettingUserInfoOneAct.class), 101);
                        }
                    } else {
                        startActivityForResult(new Intent(getActivity(), SettingUserInfoOneAct.class), 101);
                    }

                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }

                break;
            case R.id.lltBuy:
                if (App.get().user != null) {

                    if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                        if (userInfo != null && !CUtils.isEmpty(userInfo.check_pay_password)) {
                            startActivityForResult(new Intent(getActivity(), StartTradeAct.class).putExtra("flag", 0), 101);
                        } else {
                            startActivityForResult(new Intent(getActivity(), SettingUserInfoTwoAct.class), 101);
                        }
                    } else {
                        startActivityForResult(new Intent(getActivity(), SettingUserInfoTwoAct.class), 101);
                    }

                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }


                break;
            case R.id.lltSale:
                if (App.get().user != null) {
//                    startActivity(new Intent(getActivity(), StartTradeAct.class).putExtra("flag", 1));
                    if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                        if (userInfo != null && !CUtils.isEmpty(userInfo.check_pay_password)) {
                            startActivityForResult(new Intent(getActivity(), StartTradeAct.class).putExtra("flag", 1), 101);
                        } else {
                            startActivityForResult(new Intent(getActivity(), SettingUserInfoTwoAct.class), 101);
                        }
                    } else {
                        startActivityForResult(new Intent(getActivity(), SettingUserInfoTwoAct.class), 101);
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.rltAboutUs:
                startActivity(new Intent(getActivity(), AboutUsAct.class));
                break;
        }

    }
}
