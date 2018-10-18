package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

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
import cn.innovativest.ath.adapter.OrderAdapter;
import cn.innovativest.ath.bean.BuyBean;
import cn.innovativest.ath.bean.OrderItem;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BuyListResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SaleAndBuyInfoAct extends BaseAct implements OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.order_swipe_refresh)
    RefreshLayout order_swipe_refresh;

    @BindView(R.id.order_listview)
    ListView order_listview;

    private OrderAdapter orderAdapter;
    private List<OrderItem> lstOrderItems;

    TextView tvOrderId;
    TextView tvwPubNumber;
    TextView tvOrderSaleOrBuy;
    TextView tvTradePrice;
    TextView tvTradeAmount;
    TextView tvOrderStatus;
    TextView tvLimitMin;
    TextView tvLimitMax;

    int pi = 1;
    BuyBean orderBean;

    String isBuy = "false";

    private String order_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_and_buy_act);
        ButterKnife.bind(this);
        order_number = getIntent().getStringExtra("order_number");
        isBuy = getIntent().getStringExtra("isBuy");
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


        View header_view = mLayoutInft.inflate(R.layout.sale_and_buy_header, null);

        tvOrderId = header_view.findViewById(R.id.tvOrderId);
        tvwPubNumber = header_view.findViewById(R.id.tvwPubNumber);
        tvOrderSaleOrBuy = header_view.findViewById(R.id.tvOrderSaleOrBuy);
        tvTradePrice = header_view.findViewById(R.id.tvTradePrice);
        tvTradeAmount = header_view.findViewById(R.id.tvTradeAmount);
        tvOrderStatus = header_view.findViewById(R.id.tvOrderStatus);
        tvLimitMin = header_view.findViewById(R.id.tvLimitMin);
        tvLimitMax = header_view.findViewById(R.id.tvLimitMax);

        lstOrderItems = new ArrayList<OrderItem>();
        order_listview.addHeaderView(header_view);
        orderAdapter = new OrderAdapter(this, lstOrderItems,isBuy);
        order_listview.setAdapter(orderAdapter);
        order_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (lstOrderItems.size() > 0 && i >= 1) {
                    OrderItem orderItem = lstOrderItems.get(i - 1);

                    if (isBuy != null && isBuy.equals("true")) {
                        startActivity(new Intent(SaleAndBuyInfoAct.this, TradeDetailAct.class).putExtra("state", orderItem.getState()).putExtra("isBuy", "true").putExtra("order_number", orderItem.getOrder_number()));
                    } else {
                        startActivity(new Intent(SaleAndBuyInfoAct.this, TradeDetailAct.class).putExtra("state", orderItem.getState()).putExtra("isBuy", "false").putExtra("order_number", orderItem.getOrder_number()));
                    }

//                    if (orderItem.getType() == 1) {//买入
//                        startActivity(new Intent(SaleAndBuyInfoAct.this, TradeDetailAct.class).putExtra("state", orderItem.getState()).putExtra("isBuy", true).putExtra("order_number", orderItem.getOrder_number()));
//                    } else if (orderItem.getType() == 2) {//卖出
//                        startActivity(new Intent(SaleAndBuyInfoAct.this, TradeDetailAct.class).putExtra("state", orderItem.getState()).putExtra("isBuy", false).putExtra("order_number", orderItem.getOrder_number()));
//                    }
                }
            }
        });

        init();

    }

    private void init() {

        order_listview.setVisibility(View.VISIBLE);
        order_swipe_refresh.setOnRefreshListener(this);
        order_swipe_refresh.setOnLoadMoreListener(this);
        pi = 1;
        request(order_number, pi);
    }

    private void request(String order_number, int page) {
        getOrderData(order_number, page);
    }

    private void initDataToView(BuyBean buyBean) {
        if (pi == 1) {
            lstOrderItems.clear();
        }
        if (buyBean.buyHeader != null) {
            tvOrderId.setText("#" + buyBean.buyHeader.bianhao);
            tvwPubNumber.setText(buyBean.buyHeader.shuliang + "ATH");
            if (buyBean.buyHeader.type.equals("1")) {//买入
                tvOrderSaleOrBuy.setText("买入：ATH");
            } else if (buyBean.buyHeader.status.equals("2")) {//卖出
                tvOrderSaleOrBuy.setText("卖出：ATH");
            }
            tvTradePrice.setText("交易单价 " + String.format("%.2f", Float.parseFloat(buyBean.buyHeader.danjia)));
            tvTradeAmount.setText("交易总额 " + buyBean.buyHeader.zong);
            if (buyBean.buyHeader.status.equals("0")) {
                tvOrderStatus.setText("交易中");
            } else if (buyBean.buyHeader.status.equals("1")) {
                tvOrderStatus.setText("已下架");
            }
            tvLimitMin.setText("最低限额 " + String.format("%.2f", Float.parseFloat(buyBean.buyHeader.min)));
            tvLimitMax.setText("最大限额 " + String.format("%.2f", Float.parseFloat(buyBean.buyHeader.max)));
        }
        if (buyBean.orderItems != null && buyBean.orderItems.size() > 0) {
            lstOrderItems.addAll(buyBean.orderItems);
            orderAdapter.notifyDataSetChanged();
        }
    }

    private void getOrderData(String order_number, int page) {

        AthService service = App.get().getAthService();
        service.child_list(order_number, page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BuyListResponse>() {
            @Override
            public void call(BuyListResponse buyListResponse) {
                if (buyListResponse != null) {
                    if (buyListResponse.status == 1) {
                        if (buyListResponse.buyBean != null) {
                            orderBean = buyListResponse.buyBean;
                            initDataToView(buyListResponse.buyBean);
                        } else {
                            if (pi == 1) {
                                lstOrderItems.clear();
                            }
                            orderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        App.toast(SaleAndBuyInfoAct.this, buyListResponse.message);
                    }
                } else {
                    App.toast(SaleAndBuyInfoAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(SaleAndBuyInfoAct.this, "数据获取失败");
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
        MobclickAgent.onPageStart("SaleAndBuyInfoAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SaleAndBuyInfoAct");
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
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        if (orderBean != null) {
            if (pi <= orderBean.page) {
                request(order_number, pi);
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
        request(order_number, pi);
    }
}
