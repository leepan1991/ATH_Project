package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.CoinTradeAdapter;
import cn.innovativest.ath.bean.CoinTradeItem;
import cn.innovativest.ath.bean.EManCoin;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.ManCoinResponse;
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

public class CoinTradeManagementAct extends BaseAct implements OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.trade_swipe_refresh)
    RefreshLayout trade_swipe_refresh;

    @BindView(R.id.coin_trade_management_listview)
    ListView coin_trade_management_listview;

    private CoinTradeAdapter coinTradeAdapter;
    private List<CoinTradeItem> lstCoinTradeItems;


    EManCoin eManCoin;
    int pi = 1;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coin_trade_management_act);
        ButterKnife.bind(this);
        initView();
        pi = 1;
        request(pi);
    }

    private void initView() {

        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("积分兑换管理");
        btnBack.setOnClickListener(this);

        View header_view = mLayoutInft.inflate(R.layout.coin_trade_management_header, null);

        ImageView ivLoginAvatar = header_view.findViewById(R.id.ivLoginAvatar);
        ImageView ivRealed = header_view.findViewById(R.id.ivRealed);
        TextView tvName = header_view.findViewById(R.id.tvName);
        TextView tvwCoinValue = header_view.findViewById(R.id.tvwCoinValue);

        lstCoinTradeItems = new ArrayList<CoinTradeItem>();
        coin_trade_management_listview.addHeaderView(header_view);
        coinTradeAdapter = new CoinTradeAdapter(this, lstCoinTradeItems);
        coin_trade_management_listview.setAdapter(coinTradeAdapter);

        trade_swipe_refresh.setOnRefreshListener(this);
        trade_swipe_refresh.setOnLoadMoreListener(this);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + userInfo.head_img_link).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(ivLoginAvatar);
            if (userInfo.state == 1) {
                ivRealed.setVisibility(View.VISIBLE);
            } else {
                ivRealed.setVisibility(View.INVISIBLE);
            }
            tvName.setText(userInfo.name);
            if (userInfo.mainPage != null) {
                tvwCoinValue.setText("总积分：" + String.format("%.2f", Float.valueOf((CUtils.isEmpty(userInfo.mainPage.integral) ? "0" : userInfo.mainPage.integral + ""))));
            }
        }

    }

    private void request(int page) {
        //获取数据
        getCoinData(page);
    }

    private void initDataToView(List<CoinTradeItem> coinItems) {
        if (pi == 1) {
            lstCoinTradeItems.clear();
        }
        lstCoinTradeItems.addAll(coinItems);
        coinTradeAdapter.notifyDataSetChanged();
    }

    private void getCoinData(int page) {

        AthService service = App.get().getAthService();
        service.good_checkout_success().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<ManCoinResponse>() {
            @Override
            public void call(ManCoinResponse manCoinResponse) {
                if (manCoinResponse != null) {
                    if (manCoinResponse.status == 1) {
                        if (manCoinResponse.eManCoin != null) {
                            if (manCoinResponse.eManCoin.list != null && manCoinResponse.eManCoin.list.size() > 0) {
                                eManCoin = manCoinResponse.eManCoin;
                                initDataToView(manCoinResponse.eManCoin.list);
                            }
                        } else {
                            App.toast(CoinTradeManagementAct.this, manCoinResponse.message);
                        }

                    } else {
                        App.toast(CoinTradeManagementAct.this, manCoinResponse.message);
                    }
                } else {
                    App.toast(CoinTradeManagementAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
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
        MobclickAgent.onPageStart("CoinTradeManagementAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CoinTradeManagementAct");
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
                onBackPressed();
                break;
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        if (eManCoin != null) {
            if (pi <= eManCoin.page) {
                request(pi);
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
        request(pi);
    }
}
