package cn.innovativest.ath.ui.act;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.PropertyAdapter;
import cn.innovativest.ath.bean.EProperty;
import cn.innovativest.ath.bean.PropertyItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.PropertyResponse;
import cn.innovativest.ath.response.RechargeResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.PropertyDialog;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class PropertyManagementAct extends BaseAct implements AdapterView.OnItemClickListener, OnRefreshListener, OnLoadMoreListener {

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

    @BindView(R.id.btnWallet)
    Button btnWallet;

    @BindView(R.id.xlvProperty)
    XListView xlvProperty;

    @BindView(R.id.property_refresh)
    RefreshLayout property_refresh;

    private PropertyAdapter propertyAdapter;
    private List<PropertyItem> lstPropertyItems;

    int pi;
    private EProperty eProperty;

    private PropertyDialog propertyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_management_act);
        ButterKnife.bind(this);
        initView();
        pi = 1;
        getData(pi);
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
        lstPropertyItems = new ArrayList<PropertyItem>();
        propertyAdapter = new PropertyAdapter(this, lstPropertyItems);
        xlvProperty.setAdapter(propertyAdapter);
        property_refresh.setOnRefreshListener(this);
        property_refresh.setOnLoadMoreListener(this);
        xlvProperty.setOnItemClickListener(this);
        btnWallet.setOnClickListener(this);
        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            initDataToView(userInfo);
        }
        propertyDialog = new PropertyDialog(mCtx);
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

    private void initData(List<PropertyItem> lstItems) {
        if (pi == 1) {
            lstPropertyItems.clear();
        }
        lstPropertyItems.addAll(lstItems);
        propertyAdapter.notifyDataSetChanged();
    }

    private void getData(int page) {

        LoadingUtils.getInstance().dialogDismiss();
        LoadingUtils.getInstance().dialogShow(this, "请求中。。。", false);
        AthService service = App.get().getAthService();
        service.ath_recharge_list(page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<PropertyResponse>() {
            @Override
            public void call(PropertyResponse propertyResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (propertyResponse != null) {
                    if (propertyResponse.eProperty != null) {
                        eProperty = propertyResponse.eProperty;
                        initData(propertyResponse.eProperty.list);
                    } else {
                        if (pi == 1) {
                            lstPropertyItems.clear();
                        }
                        propertyAdapter.notifyDataSetChanged();
                    }
                } else {
                    App.toast(PropertyManagementAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                LogUtils.e(throwable.getMessage());
                App.toast(PropertyManagementAct.this, "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
            }
        });

    }

    private void generateWallet(final String ath, final String score, final String code) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ath_number", ath);
        map.put("integral_number", score);
        map.put("yzm", code);
        LoadingUtils.getInstance().dialogShow(this, "钱包生成中...");

        AthService service = App.get().getAthService();
        service.ath_recharge(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<RechargeResponse>() {
            @Override
            public void call(RechargeResponse rechargeResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (rechargeResponse.status == 1) {
                    //deal
                    pi = 1;
                    getData(pi);
                } else {
                    App.toast(PropertyManagementAct.this, rechargeResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(PropertyManagementAct.this, "生成失败");
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
        switch (v.getId()) {
            case R.id.btnWallet:
                propertyDialog.setIsCancelable(true).setMsg("钱包生成")
                        .setChooseListener(new PropertyDialog.ChooseListener() {

                            @Override
                            public void onChoose(int which) {
                                if (which == WHICH_RIGHT) {
                                    generateWallet(propertyDialog.getEdtSDSDText(), propertyDialog.getEdtScore(), propertyDialog.getEdtUCode());
                                }
                            }
                        }).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        popDialog(lstPropertyItems.get(position).recharge);
    }

    private void popDialog(final String address) {
        final AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("提示")//标题
                .setMessage("钱包地址：" + address)//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .setPositiveButton("一键复制", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        copy(address);
                    }
                })
                .create();
        alertDialog1.show();
    }

    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        App.toast(this, "复制成功");
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        if (eProperty != null) {
            if (pi <= eProperty.page) {
                getData(pi);
            } else {
                refreshLayout.setNoMoreData(true);
                refreshLayout.setEnableLoadMore(false);
                App.toast(PropertyManagementAct.this, "没有更多内容了！");
                pi--;
            }
        } else {
            pi--;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        refreshLayout.setNoMoreData(false);
        refreshLayout.setEnableLoadMore(true);
        pi = 1;
        getData(pi);
    }
}
