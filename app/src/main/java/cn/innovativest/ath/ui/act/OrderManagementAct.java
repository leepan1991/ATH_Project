package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import cn.innovativest.ath.adapter.OrderAdapter;
import cn.innovativest.ath.bean.OrderBean;
import cn.innovativest.ath.bean.OrderItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.OrderListResponse;
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

public class OrderManagementAct extends BaseAct implements RadioGroup.OnCheckedChangeListener, OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.order_swipe_refresh)
    RefreshLayout order_swipe_refresh;

    @BindView(R.id.order_listview)
    ListView order_listview;

    @BindView(R.id.rgTrade)
    RadioGroup rgTrade;

    @BindView(R.id.btnAll)
    RadioButton btnAll;

    @BindView(R.id.btnCancel)
    RadioButton btnCancel;

    @BindView(R.id.btnNotPay)
    RadioButton btnNotPay;

    @BindView(R.id.btnPayed)
    RadioButton btnPayed;

    @BindView(R.id.btnComplete)
    RadioButton btnComplete;

    private OrderAdapter orderAdapter;
    private List<OrderItem> lstOrderItems;

    int pi = 1;
    OrderBean orderBean;

    int type = 0;

    int requestCode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_management_act);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        if (type == 0) {
            finish();
            return;
        }
        initView();
    }

    private void initView() {

        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        tvwTitle.setText("订单管理");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lstOrderItems = new ArrayList<OrderItem>();
        orderAdapter = new OrderAdapter(this, lstOrderItems,"");
        order_listview.setAdapter(orderAdapter);
        order_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                    return;
                }

                if (btnAll.isChecked()) { // 切换到全部
                    requestCode = 100;
                } else if (btnCancel.isChecked()) { // 切换到已取消
                    requestCode = 101;
                } else if (btnNotPay.isChecked()) { // 切换到待付款
                    requestCode = 102;
                } else if (btnPayed.isChecked()) { // 切换到已付款
                    requestCode = 103;
                } else if (btnComplete.isChecked()) { // 切换到已完成
                    requestCode = 104;
                }

                UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
                if (lstOrderItems.get(i).getBuy_userid().equals(userInfo.id + "")) {
                    startActivityForResult(new Intent(OrderManagementAct.this, TradeDetailAct.class).putExtra("state", lstOrderItems.get(i).getState()).putExtra("isBuy", "true").putExtra("order_number", lstOrderItems.get(i).getOrder_number()), requestCode);
                } else if (lstOrderItems.get(i).getSell_userid().equals(userInfo.id + "")) {
                    startActivityForResult(new Intent(OrderManagementAct.this, TradeDetailAct.class).putExtra("state", lstOrderItems.get(i).getState()).putExtra("isBuy", "false").putExtra("order_number", lstOrderItems.get(i).getOrder_number()), requestCode);
                }

//                if (lstOrderItems.get(i).getType() == 1) {//买入
//                    startActivity(new Intent(OrderManagementAct.this, TradeDetailAct.class).putExtra("state", lstOrderItems.get(i).getState()).putExtra("isBuy", true).putExtra("order_number", lstOrderItems.get(i).getOrder_number()));
//                } else if (lstOrderItems.get(i).getType() == 2) {//卖出
//                    startActivity(new Intent(OrderManagementAct.this, TradeDetailAct.class).putExtra("state", lstOrderItems.get(i).getState()).putExtra("isBuy", false).putExtra("order_number", lstOrderItems.get(i).getOrder_number()));
//                }
            }
        });

        rgTrade.setOnCheckedChangeListener(this);
        order_swipe_refresh.setOnRefreshListener(this);
        order_swipe_refresh.setOnLoadMoreListener(this);

    }

    private void init(boolean isAll, boolean isCancel, boolean isNotPay, boolean isPayed, boolean isComplete) {

        order_listview.setVisibility(View.VISIBLE);
        pi = 1;
        if (isAll) {
            btnAll.setChecked(true);
            btnCancel.setChecked(false);
            btnNotPay.setChecked(false);
            btnPayed.setChecked(false);
            btnComplete.setChecked(false);
            request("", pi, true);
        }
        if (isCancel) {
            btnAll.setChecked(false);
            btnCancel.setChecked(true);
            btnNotPay.setChecked(false);
            btnPayed.setChecked(false);
            btnComplete.setChecked(false);
            request("0", pi, false);
        }

        if (isNotPay) {
            btnAll.setChecked(false);
            btnCancel.setChecked(false);
            btnNotPay.setChecked(true);
            btnPayed.setChecked(false);
            btnComplete.setChecked(false);
            request("1", pi, false);
        }

        if (isPayed) {
            btnAll.setChecked(false);
            btnCancel.setChecked(false);
            btnNotPay.setChecked(false);
            btnPayed.setChecked(true);
            btnComplete.setChecked(false);
            request("2", pi, false);
        }

        if (isComplete) {
            btnAll.setChecked(false);
            btnCancel.setChecked(false);
            btnNotPay.setChecked(false);
            btnPayed.setChecked(false);
            btnComplete.setChecked(true);
            request("3", pi, false);
        }

    }

    private void request(String status, int page, boolean isAll) {
        getOrderData(status, page, isAll);
    }

    private void initDataToView(List<OrderItem> orderItems) {
        if (pi == 1) {
            lstOrderItems.clear();
        }
        lstOrderItems.addAll(orderItems);
        orderAdapter.notifyDataSetChanged();
    }

    private void getOrderData(String status, int page, boolean isAll) {

        HashMap<String, String> map = new HashMap<String, String>();
        if (!isAll) {
            map.put("state", status);
        }
        map.put("type", type + "");
        map.put("page", page + "");
        AthService service = App.get().getAthService();
        service.order_list(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<OrderListResponse>() {
            @Override
            public void call(OrderListResponse orderListResponse) {
                if (orderListResponse != null) {
                    if (orderListResponse.orderBean.orderItems != null && orderListResponse.orderBean.orderItems.size() > 0) {
                        orderBean = orderListResponse.orderBean;
                        initDataToView(orderListResponse.orderBean.orderItems);
                    } else {
                        if (pi == 1) {
                            lstOrderItems.clear();
                        }
                        orderAdapter.notifyDataSetChanged();
//                        App.toast(OrderManagementAct.this, orderListResponse.message);
                    }
                } else {
                    App.toast(OrderManagementAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(OrderManagementAct.this, "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
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
        MobclickAgent.onPageStart("OrderManagementAct");
        MobclickAgent.onResume(this);
        if (requestCode == 0 || requestCode == 100) {
            init(true, false, false, false, false);
        } else if (requestCode == 101) {
            init(false, true, false, false, false);
        } else if (requestCode == 102) {
            init(false, false, true, false, false);
        } else if (requestCode == 103) {
            init(false, false, false, true, false);
        } else if (requestCode == 104) {
            init(false, false, false, false, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("OrderManagementAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        pi = 1;
        if (i == btnAll.getId()) { // 切换到全部
            request("", pi, true);
        } else if (i == btnCancel.getId()) { // 切换到已取消
            request("0", pi, false);
        } else if (i == btnNotPay.getId()) { // 切换到待付款
            request("1", pi, false);
        } else if (i == btnPayed.getId()) { // 切换到已付款
            request("2", pi, false);
        } else if (i == btnComplete.getId()) { // 切换到已完成
            request("3", pi, false);
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        if (orderBean != null) {
            if (pi <= orderBean.page) {
                if (btnAll.isChecked()) { // 切换到全部
                    request("", pi, true);
                } else if (btnCancel.isChecked()) { // 切换到已取消
                    request("0", pi, false);
                } else if (btnNotPay.isChecked()) { // 切换到待付款
                    request("1", pi, false);
                } else if (btnPayed.isChecked()) { // 切换到已付款
                    request("2", pi, false);
                } else if (btnComplete.isChecked()) { // 切换到已完成
                    request("3", pi, false);
                }
            } else {
                refreshLayout.setNoMoreData(true);
                refreshLayout.setEnableLoadMore(false);
                App.toast(this, "没有更多内容了！");
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
        if (btnAll.isChecked()) { // 切换到全部
            request("", pi, true);
        } else if (btnCancel.isChecked()) { // 切换到已取消
            request("0", pi, false);
        } else if (btnNotPay.isChecked()) { // 切换到待付款
            request("1", pi, false);
        } else if (btnPayed.isChecked()) { // 切换到已付款
            request("2", pi, false);
        } else if (btnComplete.isChecked()) { // 切换到已完成
            request("3", pi, false);
        }
    }
}
