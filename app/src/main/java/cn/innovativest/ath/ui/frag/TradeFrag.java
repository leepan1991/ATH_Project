package cn.innovativest.ath.ui.frag;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.TradeAdapter;
import cn.innovativest.ath.bean.TradeBean;
import cn.innovativest.ath.bean.TradeItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.MainPageBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.BuySellResponse;
import cn.innovativest.ath.response.CommonResponse;
import cn.innovativest.ath.response.TradeResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseFrag;
import cn.innovativest.ath.ui.act.AccountSettingAct;
import cn.innovativest.ath.ui.act.LoginAct;
import cn.innovativest.ath.ui.act.SettingUserInfoOneAct;
import cn.innovativest.ath.ui.act.SettingUserInfoTwoAct;
import cn.innovativest.ath.ui.act.TradeDetailAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.RealNameDialog;
import cn.innovativest.ath.widget.ScrollTextView;
import cn.innovativest.ath.widget.TradeBuyDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import static android.app.Activity.RESULT_OK;

/**
 * Created by leepan on 20/03/2018.
 */

public class TradeFrag extends BaseFrag implements RadioGroup.OnCheckedChangeListener, OnRefreshListener, OnLoadMoreListener {
    private View contentView;

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    ScrollTextView tvwTitle;

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

    private TradeAdapter tradeAdapter;
    private List<TradeItem> lstTradeItems;

    private RealNameDialog realNameDialog;
    private TradeBuyDialog tradeBuyDialog;

    private CustomDialog customDialog;

    int pi;
    TradeBean tradeBean;

    int requestCode = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.trade_frag, container,
                    false);
            ButterKnife.bind(this, contentView);
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
        btnBack.setVisibility(View.GONE);
        rltRightFun.setVisibility(View.GONE);
        tvwTitle.setBackgroundResource(R.color.trade_top_tv_bg);
        tvwTitle.setText("交易中心1.交易中心9月份开放，敬请期待！！  2.苹果app即将上线。");

        lstTradeItems = new ArrayList<TradeItem>();
        tradeAdapter = new TradeAdapter(getActivity(), lstTradeItems);
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
                                            startActivityForResult(new Intent(getActivity(), SettingUserInfoOneAct.class), 101);
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
                                            startActivityForResult(new Intent(getActivity(), SettingUserInfoTwoAct.class), 101);
                                        }
                                    }
                                }).show();
                    }

                    if ((CUtils.isEmpty(userInfo.real_name) || CUtils.isEmpty(userInfo.id_number)) && !CUtils.isEmpty(userInfo.check_pay_password)) {
                        realNameDialog.setMRightBt("去认证").setIsCancelable(true).setMsg("为了您的资金安全，进行交易前请完成实名认证！")
                                .setChooseListener(new RealNameDialog.ChooseListener() {

                                    @Override
                                    public void onChoose(int which) {
                                        if (which == WHICH_RIGHT) {
                                            startActivityForResult(new Intent(getActivity(), SettingUserInfoOneAct.class), 101);
                                        }
                                    }
                                }).show();
                    }


                    if (!CUtils.isEmpty(userInfo.real_name) && !CUtils.isEmpty(userInfo.id_number) && !CUtils.isEmpty(userInfo.check_pay_password)) {
                        final TradeItem tradeItem = lstTradeItems.get(i);
                        if (CUtils.isEmpty(userInfo.bank) && CUtils.isEmpty(userInfo.alipay) && CUtils.isEmpty(userInfo.wechat)) {
                            startActivity(new Intent(getActivity(), AccountSettingAct.class));
                        } else {

                            String v[] = tradeItem.getQuota().split(",");
                            if (btnManPro.isChecked()) {
                                tradeBuyDialog.setTradeItem(tradeItem);
                                tradeBuyDialog.setIsCancelable(true).setMsg("买入ATH").setDgAllBuy("全部买入").setEdtSDSDHint("最大可买" + String.format("%.2f", Float.parseFloat(tradeItem.getNumber())))
                                        .setChooseTradeListener(new TradeBuyDialog.ChooseTradeListener() {

                                            @Override
                                            public void onChoose(int which) {
                                                if (which == WHICH_RIGHT) {
                                                    buySell(tradeItem.getOrder_number(), "2", tradeBuyDialog.getEdtSDSDText(), 1);
//                                                startActivity(new Intent(getActivity(), TradeDetailAct.class).putExtra("state", "1").putExtra("isBuy", true).putExtra("order_number", tradeItem.getOrder_number()));
                                                }
                                            }
                                        }).show();
                            } else if (btnManPic.isChecked()) {
                                tradeBuyDialog.setTradeItem(tradeItem);
                                tradeBuyDialog.setIsCancelable(true).setMsg("卖出ATH").setDgAllBuy("一键卖出").setEdtSDSDHint("最大可卖" + String.format("%.2f", Float.parseFloat(tradeItem.getNumber())))
                                        .setChooseTradeListener(new TradeBuyDialog.ChooseTradeListener() {

                                            @Override
                                            public void onChoose(int which) {
                                                if (which == WHICH_RIGHT) {
                                                    buySell(tradeItem.getOrder_number(), "1", tradeBuyDialog.getEdtSDSDText(), 2);
                                                }
                                            }
                                        }).show();
                            }
                        }


                    }


                } else {
                    realNameDialog.setMRightBt("去认证").setIsCancelable(true).setMsg("为了您的资金安全，进行交易前请完成实名认证！")
                            .setChooseListener(new RealNameDialog.ChooseListener() {

                                @Override
                                public void onChoose(int which) {
                                    if (which == WHICH_RIGHT) {
                                        startActivityForResult(new Intent(getActivity(), SettingUserInfoOneAct.class), 101);
                                    }
                                }
                            }).show();
                }
            }
        });

        realNameDialog = new RealNameDialog(mCtx);
        tradeBuyDialog = new TradeBuyDialog(mCtx);
        customDialog = new CustomDialog(mCtx);
        rltCheck.setVisibility(View.VISIBLE);
        trade_listview.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainAct");
        if (App.get().user == null) {
            getCommonData();
            customDialog.setMRightBt("去登录").setMsg("登录后查看交易中心").setIsCancelable(true)
                    .setChooseListener(new CustomDialog.ChooseListener() {

                        @Override
                        public void onChoose(int which) {
                            if (which == WHICH_RIGHT) {
                                startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                            }
                        }
                    }).show();
        } else {
            androidInfo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainAct");
    }

    private void initData() {
        pi = 1;
        init();
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
        getTradeData(status, pi);

    }

    private void initDataToView(List<TradeItem> tradeItems) {
        if (pi == 1) {
            lstTradeItems.clear();
        }
        lstTradeItems.addAll(tradeItems);
        tradeAdapter.notifyDataSetChanged();
    }

    private void buySell(final String order_number, String type, String number, final int flag) {
        AthService service = App.get().getAthService();
        service.buysell(order_number, type, number).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BuySellResponse>() {
            @Override
            public void call(BuySellResponse buySellResponse) {
                App.toast(getActivity(), buySellResponse.message);
                if (buySellResponse.status == 1) {
                    if (flag == 1) {//买
                        startActivity(new Intent(getActivity(), TradeDetailAct.class).putExtra("state", "1").putExtra("isBuy", true).putExtra("order_number", buySellResponse.buySell.order_number));
                    } else if (flag == 2) {//卖
                        startActivity(new Intent(getActivity(), TradeDetailAct.class).putExtra("state", "1").putExtra("isBuy", false).putExtra("order_number", buySellResponse.buySell.order_number));
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
        MainPageBody mainPageBody = new MainPageBody();
        mainPageBody.status = status;
        mainPageBody.page = page;

        AthService service = App.get().getAthService();
        service.trade(mainPageBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<TradeResponse>() {
            @Override
            public void call(TradeResponse tradeResponse) {
                getCommonData();
                if (tradeResponse != null) {
                    if (tradeResponse.tradeBean != null && tradeResponse.tradeBean.tradeItems.size() > 0) {
                        tradeBean = tradeResponse.tradeBean;
                        initDataToView(tradeResponse.tradeBean.tradeItems);
                    } else {
                        if (pi == 1) {
                            lstTradeItems.clear();
                        }
                        tradeAdapter.notifyDataSetChanged();
//                        App.toast(getActivity(), tradeResponse.message);
                    }
                } else {
                    App.toast(getActivity(), "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                getCommonData();
                LogUtils.e(throwable.getMessage());
                App.toast(getActivity(), "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
            }
        });

    }


    private void getCommonData() {

        AthService service = App.get().getAthService();
        service.commonInfo(7).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<CommonResponse>() {
            @Override
            public void call(CommonResponse commonResponse) {
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
                            tvwTitle.setText(commonResponse.commonItem.exchange);
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
                App.toast(getActivity(), "数据获取失败");
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

    private void androidInfo() {
        AthService service = App.get().getAthService();
        service.androidInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (baseResponse.status == 1) {
                        trade_swipe_refresh.setEnableLoadMore(false);
                        trade_swipe_refresh.setEnableRefresh(false);
                        getCommonData();
                        popDialog();
                    } else {
                        initData();
                    }
                } else {
                    initData();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                initData();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

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
        if (tradeBean != null) {
            if (pi <= tradeBean.page) {
                if (btnManPro.isChecked()) {
                    request("1", pi);
                } else if (btnManPic.isChecked()) {
                    request("2", pi);
                }
            } else {
                refreshLayout.setNoMoreData(true);
                refreshLayout.setEnableLoadMore(false);
                App.toast(getActivity(), "没有更多内容了！");
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
            requestCode = 300;
            request("1", pi);
        } else if (i == btnManPic.getId()) { // 切换到我要卖
            requestCode = 301;
            request("2", pi);
        }
    }
}
