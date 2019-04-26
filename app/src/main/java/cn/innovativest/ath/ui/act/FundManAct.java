package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.FundManAdapter;
import cn.innovativest.ath.adapter.FundPartAdapter;
import cn.innovativest.ath.bean.EFundManItem;
import cn.innovativest.ath.bean.EFundPartItem;
import cn.innovativest.ath.bean.FundManItem;
import cn.innovativest.ath.bean.FundPart;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.FundManResponse;
import cn.innovativest.ath.response.FundPartResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.RealNameDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 20/03/2018.
 */

public class FundManAct extends BaseAct implements RadioGroup.OnCheckedChangeListener, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.rltRightFun)
    RelativeLayout rltRightFun;

    @BindView(R.id.rltCheck)
    RelativeLayout rltCheck;

    @BindView(R.id.trade_swipe_refresh)
    RefreshLayout trade_swipe_refresh;

    @BindView(R.id.trade_listview)
    ListView trade_listview;

    @BindView(R.id.rgTrade)
    RadioGroup rgTrade;

    @BindView(R.id.btnManPro)
    RadioButton btnManPro;

    @BindView(R.id.btnManPic)
    RadioButton btnManPic;

    private FundManAdapter fundManAdapter;
    private List<FundManItem> lstTradeItems;

    private FundPartAdapter fundPartAdapter;
    private List<FundPart> lstFundParts;

    private RealNameDialog realNameDialog;
//    private TradeBuyDialog tradeBuyDialog;

    private CustomDialog customDialog;

    int pi;
    EFundManItem releaseBean;

    EFundPartItem eFundPartItem;

    int requestCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_man_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setVisibility(View.VISIBLE);
        rltRightFun.setVisibility(View.GONE);
        tvwTitle.setText("众筹管理");

        lstTradeItems = new ArrayList<FundManItem>();
        fundManAdapter = new FundManAdapter(this, lstTradeItems);

//        trade_listview.setAdapter(fundManAdapter);

        lstFundParts = new ArrayList<FundPart>();
        fundPartAdapter = new FundPartAdapter(this, lstFundParts);

        trade_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        realNameDialog = new RealNameDialog(mCtx);
//        tradeBuyDialog = new TradeBuyDialog(mCtx);
        customDialog = new CustomDialog(mCtx);
        rltCheck.setVisibility(View.INVISIBLE);
        trade_listview.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("FundManAct");
        MobclickAgent.onResume(this);
        if (App.get().user == null) {
            customDialog.setMRightBt("去登录").setMsg("登录后查看我的交易中心").setIsCancelable(true)
                    .setChooseListener(new CustomDialog.ChooseListener() {

                        @Override
                        public void onChoose(int which) {
                            if (which == WHICH_RIGHT) {
                                startActivityForResult(new Intent(FundManAct.this, LoginAct.class), 100);
                            }
                        }
                    }).show();
        } else {
            pi = 1;
            init();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MyTradeAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                    init();
                }
                break;
            case 101:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                }
                break;
            case 102:
                if (resultCode == RESULT_OK) {
                    requestUserInfo();
                    init();
                }
                break;
        }
    }

    private void initTop(boolean isBuy) {
        if (isBuy) {
            btnManPro.setChecked(true);
            btnManPic.setChecked(false);
            trade_listview.setAdapter(fundManAdapter);
        } else {
            btnManPro.setChecked(false);
            btnManPic.setChecked(true);
            trade_listview.setAdapter(fundPartAdapter);
        }
    }

    private void init() {

        rltCheck.setVisibility(View.VISIBLE);
        trade_listview.setVisibility(View.VISIBLE);
        rgTrade.setOnCheckedChangeListener(this);
        btnBack.setOnClickListener(this);
        trade_swipe_refresh.setOnRefreshListener(this);
        trade_swipe_refresh.setOnLoadMoreListener(this);

        if (requestCode == 0 || requestCode == 300) {
            initTop(true);
            request("1", pi);
        } else if (requestCode == 301) {
            initTop(false);
            request("2", pi);
        }
    }

    private void request(String status, int pi) {
        //获取数据
        if (status.equalsIgnoreCase("1")) {
            getTradeData(pi);
        } else {
            getFundPartData(pi);
        }
    }

    private void initDataToView(List<FundManItem> releaseItems) {
        if (pi == 1) {
            lstTradeItems.clear();
            lstFundParts.clear();
            fundPartAdapter.notifyDataSetChanged();
        }
        lstTradeItems.addAll(releaseItems);
        fundManAdapter.notifyDataSetChanged();
    }

    private void initDataToFundView(List<FundPart> releaseItems) {
        if (pi == 1) {
            lstTradeItems.clear();
            lstFundParts.clear();
            fundManAdapter.notifyDataSetChanged();
        }
        lstFundParts.addAll(releaseItems);
        fundPartAdapter.notifyDataSetChanged();
    }


    private void getTradeData(int page) {

        AthService service = App.get().getAthService();
        service.self_crowd_funding_list(page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<FundManResponse>() {
            @Override
            public void call(FundManResponse releaseResponse) {
                if (releaseResponse != null) {
                    if (releaseResponse.eFundManItem != null && releaseResponse.eFundManItem.getLstFundManItems().size() > 0) {
                        releaseBean = releaseResponse.eFundManItem;
                        initDataToView(releaseResponse.eFundManItem.getLstFundManItems());
                    } else {
                        if (pi == 1) {
                            lstTradeItems.clear();
                        }
                        fundManAdapter.notifyDataSetChanged();
//                        App.toast(getActivity(), tradeResponse.message);
                    }
                } else {
                    App.toast(FundManAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(FundManAct.this, "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
            }
        });

    }

    private void getFundPartData(int page) {

        AthService service = App.get().getAthService();
        service.join_crowd_funding_list(page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<FundPartResponse>() {
            @Override
            public void call(FundPartResponse releaseResponse) {
                if (releaseResponse != null) {
                    if (releaseResponse.eFundPartItem != null && releaseResponse.eFundPartItem.getLstFundParts().size() > 0) {
                        eFundPartItem = releaseResponse.eFundPartItem;
                        initDataToFundView(releaseResponse.eFundPartItem.getLstFundParts());
                    } else {
                        if (pi == 1) {
                            lstFundParts.clear();
                        }
                        fundPartAdapter.notifyDataSetChanged();
                    }
                } else {
                    App.toast(FundManAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(FundManAct.this, "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
            }
        });

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
                LogUtils.e("获取用户信息失败");
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        refreshLayout.setNoMoreData(false);
        refreshLayout.setEnableLoadMore(true);
        pi = 1;
        if (btnManPro.isChecked()) {
            request("1", pi);
        } else if (btnManPic.isChecked()) {
            request("2", pi);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        if (btnManPro.isChecked()) {
            if (releaseBean != null) {
                if (pi <= releaseBean.getTotal()) {
                    if (btnManPro.isChecked()) {
                        request("1", pi);
                    } else if (btnManPic.isChecked()) {
                        request("2", pi);
                    }
                } else {
                    refreshLayout.setNoMoreData(true);
                    refreshLayout.setEnableLoadMore(false);
                    App.toast(FundManAct.this, "没有更多内容了！");
                    pi--;
                }
            } else {
                pi--;
            }
        } else if (btnManPic.isChecked()) {
            if (eFundPartItem != null) {
                if (pi <= eFundPartItem.getTotal()) {
                    if (btnManPro.isChecked()) {
                        request("1", pi);
                    } else if (btnManPic.isChecked()) {
                        request("2", pi);
                    }
                } else {
                    refreshLayout.setNoMoreData(true);
                    refreshLayout.setEnableLoadMore(false);
                    App.toast(FundManAct.this, "没有更多内容了！");
                    pi--;
                }
            } else {
                pi--;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        pi = 1;
        if (i == btnManPro.getId()) { // 切换到我要买
            initTop(true);
            request("1", pi);
        } else if (i == btnManPic.getId()) { // 切换到我要卖
            initTop(false);
            request("2", pi);
        }
    }
}
