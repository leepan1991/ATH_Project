package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.ReleaseAdapter;
import cn.innovativest.ath.bean.ReleaseBean;
import cn.innovativest.ath.bean.ReleaseItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.BuySellResponse;
import cn.innovativest.ath.response.ReleaseResponse;
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

public class MyTradeAct extends BaseAct implements RadioGroup.OnCheckedChangeListener, OnRefreshListener, OnLoadMoreListener {

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

    private ReleaseAdapter tradeAdapter;
    private List<ReleaseItem> lstTradeItems;

    private RealNameDialog realNameDialog;
//    private TradeBuyDialog tradeBuyDialog;

    private CustomDialog customDialog;

    int pi;
    ReleaseBean releaseBean;

    int requestCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_trade_act);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setVisibility(View.VISIBLE);
        rltRightFun.setVisibility(View.GONE);
        tvwTitle.setText("我的交易中心");

        lstTradeItems = new ArrayList<ReleaseItem>();
        tradeAdapter = new ReleaseAdapter(this, lstTradeItems);
        trade_listview.setAdapter(tradeAdapter);
        trade_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //1. id和pay_password 都没设置   按钮：下一步
                //2. id已设置，pay_password未设置   按钮：直接跳转到pay_password设置页面
                //3. id未设置,pay_password已设置    按钮：完成
                //4. id和pay_password 都已设置      按钮：完成

                if (btnManPro.isChecked()) { // 切换到全部
                    requestCode = 300;
                } else if (btnManPic.isChecked()) { // 切换到已取消
                    requestCode = 301;
                }

                if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                    if (CUtils.isEmpty(userInfo.real_name) && CUtils.isEmpty(userInfo.id_number) && CUtils.isEmpty(userInfo.check_pay_password)) {
                        realNameDialog.setMRightBt("去认证").setIsCancelable(true).setMsg("为了您的资金安全，进行交易前请完成实名认证！")
                                .setChooseListener(new RealNameDialog.ChooseListener() {

                                    @Override
                                    public void onChoose(int which) {
                                        if (which == WHICH_RIGHT) {
                                            startActivityForResult(new Intent(MyTradeAct.this, SettingUserInfoOneAct.class), 101);
                                        }
                                    }
                                }).show();
                    }
                    if (!CUtils.isEmpty(userInfo.real_name) && !CUtils.isEmpty(userInfo.id_number) && CUtils.isEmpty(userInfo.check_pay_password)) {
                        realNameDialog.setMRightBt("去认证").setIsCancelable(true).setMsg("为了您的资金安全，进行交易前请完成实名认证！")
                                .setChooseListener(new RealNameDialog.ChooseListener() {

                                    @Override
                                    public void onChoose(int which) {
                                        if (which == WHICH_RIGHT) {
                                            startActivityForResult(new Intent(MyTradeAct.this, SettingUserInfoTwoAct.class), 101);
                                        }
                                    }
                                }).show();
                    }

                    if (CUtils.isEmpty(userInfo.real_name) && CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.check_pay_password)) {
                        realNameDialog.setMRightBt("去认证").setIsCancelable(true).setMsg("为了您的资金安全，进行交易前请完成实名认证！")
                                .setChooseListener(new RealNameDialog.ChooseListener() {

                                    @Override
                                    public void onChoose(int which) {
                                        if (which == WHICH_RIGHT) {
                                            startActivityForResult(new Intent(MyTradeAct.this, SettingUserInfoOneAct.class), 101);
                                        }
                                    }
                                }).show();
                    }


                    if (!CUtils.isEmpty(userInfo.real_name) && !CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.check_pay_password)) {
                        final ReleaseItem tradeItem = lstTradeItems.get(i);
                        String v[] = tradeItem.getQuota().split(",");

                        if (btnManPro.isChecked()) {//我要买

                            if (tradeItem.getStatus().equals("0")) {
                                customDialog.setMRightBt("查看").setMLeftBtt("下架").setMsg("请选择操作类型").setIsCancelable(true)
                                        .setChooseListener(new CustomDialog.ChooseListener() {

                                            @Override
                                            public void onChoose(int which) {
                                                if (which == WHICH_RIGHT) {//查看消息列表
                                                    startActivityForResult(new Intent(MyTradeAct.this, SaleAndBuyInfoAct.class).putExtra("isBuy", "true").putExtra("order_number", tradeItem.getOrder_number()), 100);
                                                } else if (which == WHICH_LEFT) {//下架
                                                    releaseEdit(tradeItem.getId());
                                                }
                                            }
                                        }).show();
                            } else if (tradeItem.getStatus().equals("1")) {
                                startActivityForResult(new Intent(MyTradeAct.this, SaleAndBuyInfoAct.class).putExtra("isBuy", "true").putExtra("order_number", tradeItem.getOrder_number()), 100);
                            }
                        } else if (btnManPic.isChecked()) {//我要卖
                            if (tradeItem.getStatus().equals("0")) {
                                customDialog.setMRightBt("查看").setMLeftBtt("下架").setMsg("请选择操作类型").setIsCancelable(true)
                                        .setChooseListener(new CustomDialog.ChooseListener() {

                                            @Override
                                            public void onChoose(int which) {
                                                if (which == WHICH_RIGHT) {//查看消息列表
                                                    startActivityForResult(new Intent(MyTradeAct.this, SaleAndBuyInfoAct.class).putExtra("isBuy", "false").putExtra("order_number", tradeItem.getOrder_number()), 100);
                                                } else if (which == WHICH_LEFT) {//修改
                                                    releaseEdit(tradeItem.getId());
                                                }
                                            }
                                        }).show();
                            } else if (tradeItem.getStatus().equals("1")) {
                                startActivityForResult(new Intent(MyTradeAct.this, SaleAndBuyInfoAct.class).putExtra("isBuy", "false").putExtra("order_number", tradeItem.getOrder_number()), 100);
                            }
                        }


                    }


                } else {
                    realNameDialog.setMRightBt("去认证").setIsCancelable(true).setMsg("为了您的资金安全，进行交易前请完成实名认证！")
                            .setChooseListener(new RealNameDialog.ChooseListener() {

                                @Override
                                public void onChoose(int which) {
                                    if (which == WHICH_RIGHT) {
                                        startActivityForResult(new Intent(MyTradeAct.this, SettingUserInfoOneAct.class), 101);
                                    }
                                }
                            }).show();
                }
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
        MobclickAgent.onPageStart("MyTradeAct");
        MobclickAgent.onResume(this);
        if (App.get().user == null) {
            customDialog.setMRightBt("去登录").setMsg("登录后查看我的交易中心").setIsCancelable(true)
                    .setChooseListener(new CustomDialog.ChooseListener() {

                        @Override
                        public void onChoose(int which) {
                            if (which == WHICH_RIGHT) {
                                startActivityForResult(new Intent(MyTradeAct.this, LoginAct.class), 100);
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
        } else {
            btnManPro.setChecked(false);
            btnManPic.setChecked(true);
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
            request("2", pi);
        } else if (requestCode == 301) {
            initTop(false);
            request("1", pi);
        }
    }

    private void request(String status, int pi) {
        //获取数据
        getTradeData(status, pi);
    }

    private void initDataToView(List<ReleaseItem> releaseItems) {
        if (pi == 1) {
            lstTradeItems.clear();
        }
        lstTradeItems.addAll(releaseItems);
        tradeAdapter.notifyDataSetChanged();
    }

    private void buySell(final String order_number, String type, String number, final int flag) {
        AthService service = App.get().getAthService();
        service.buysell(order_number, type, number).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BuySellResponse>() {
            @Override
            public void call(BuySellResponse buySellResponse) {
                App.toast(MyTradeAct.this, buySellResponse.message);
                if (buySellResponse.status == 1) {
                    if (flag == 1) {//买
                        startActivity(new Intent(MyTradeAct.this, TradeDetailAct.class).putExtra("state", "1").putExtra("isBuy", true).putExtra("order_number", buySellResponse.buySell.order_number));
                    } else if (flag == 2) {//卖
                        startActivity(new Intent(MyTradeAct.this, TradeDetailAct.class).putExtra("state", "1").putExtra("isBuy", false).putExtra("order_number", buySellResponse.buySell.order_number));
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
            }
        });
    }

    private void getTradeData(String status, int page) {
//        MainPageBody mainPageBody = new MainPageBody();
//        mainPageBody.status = status;
//        mainPageBody.page = page;

        AthService service = App.get().getAthService();
        service.release_list(status, page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<ReleaseResponse>() {
            @Override
            public void call(ReleaseResponse releaseResponse) {
                if (releaseResponse != null) {
                    if (releaseResponse.releaseBean != null && releaseResponse.releaseBean.releaseItems.size() > 0) {
                        releaseBean = releaseResponse.releaseBean;
                        initDataToView(releaseResponse.releaseBean.releaseItems);
                    } else {
                        if (pi == 1) {
                            lstTradeItems.clear();
                        }
                        tradeAdapter.notifyDataSetChanged();
//                        App.toast(getActivity(), tradeResponse.message);
                    }
                } else {
                    App.toast(MyTradeAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(MyTradeAct.this, "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
            }
        });

    }

    private void releaseEdit(String id) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("id", id);

        AthService service = App.get().getAthService();
        service.release_edit(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    App.toast(MyTradeAct.this, baseResponse.message);
                    if (baseResponse.status == 1) {
                        pi = 1;
                        if (btnManPro.isChecked()) {
                            request("1", pi);
                        } else if (btnManPic.isChecked()) {
                            request("2", pi);
                        }
                    }
                } else {
                    App.toast(MyTradeAct.this, "下架失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(MyTradeAct.this, "下架失败");
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
            request("2", pi);
        } else if (btnManPic.isChecked()) {
            request("1", pi);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        if (releaseBean != null) {
            if (pi <= releaseBean.page) {
                if (btnManPro.isChecked()) {
                    request("2", pi);
                } else if (btnManPic.isChecked()) {
                    request("1", pi);
                }
            } else {
                refreshLayout.setNoMoreData(true);
                refreshLayout.setEnableLoadMore(false);
                App.toast(MyTradeAct.this, "没有更多内容了！");
                pi--;
            }
        } else {
            pi--;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        pi = 1;
        if (i == btnManPro.getId()) { // 切换到我要买
            request("2", pi);
        } else if (i == btnManPic.getId()) { // 切换到我要卖
            request("1", pi);
        }
    }
}
