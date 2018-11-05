package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.AppAdapter;
import cn.innovativest.ath.bean.AppItem;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.AppResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class AppAct extends BaseAct implements AdapterView.OnItemClickListener, OnRefreshListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.app_refresh)
    SmartRefreshLayout app_refresh;

    @BindView(R.id.appGird)
    GridView appGird;

    private List<AppItem> lstAppItems;
    private AppAdapter appAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_act);
        ButterKnife.bind(this);
        initView();
        getData();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("生态圈子应用");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lstAppItems = new ArrayList<>();
        appAdapter = new AppAdapter(this, lstAppItems);
        appGird.setAdapter(appAdapter);

        app_refresh.setOnRefreshListener(this);
        app_refresh.setNoMoreData(true);
        app_refresh.setEnableLoadMore(false);


    }

    private void initDataToView(List<AppItem> appItems) {
        lstAppItems.clear();
        lstAppItems.addAll(appItems);
        appAdapter.notifyDataSetChanged();
    }

    private void getData() {

        AthService service = App.get().getAthService();
        service.apps().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<AppResponse>() {
            @Override
            public void call(AppResponse appResponse) {
                if (appResponse != null) {
                    if (appResponse.lstApps != null && appResponse.lstApps.size() > 0) {
                        initDataToView(appResponse.lstApps);
                    } else {
                        appAdapter.notifyDataSetChanged();
//                        App.toast(AppAct.this, appResponse.message);
                    }
                } else {
                    App.toast(AppAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(AppAct.this, "数据获取失败");
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
        MobclickAgent.onPageStart("AboutUsAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutUsAct");
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        getData();
    }
}
