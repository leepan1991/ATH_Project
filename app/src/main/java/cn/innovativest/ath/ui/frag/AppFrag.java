package cn.innovativest.ath.ui.frag;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

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
import cn.innovativest.ath.ui.BaseFrag;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AppFrag extends BaseFrag implements AdapterView.OnItemClickListener, OnRefreshListener {

    private View contentView;

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.rltRightFun)
    RelativeLayout rltRightFun;

    @BindView(R.id.app_refresh)
    SmartRefreshLayout app_refresh;

    @BindView(R.id.appGird)
    GridView appGird;

    private List<AppItem> lstAppItems;
    private AppAdapter appAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.app_frag, container,
                    false);
            ButterKnife.bind(this, contentView);
            initView();
            getData();
        }

        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("生态圈应用");
        btnBack.setVisibility(View.GONE);
        rltRightFun.setVisibility(View.GONE);

        lstAppItems = new ArrayList<>();
        appAdapter = new AppAdapter(getActivity(), lstAppItems);
        appGird.setAdapter(appAdapter);
        appGird.setOnItemClickListener(this);

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

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppItem appItem = lstAppItems.get(i);
        openApp(appItem);
    }

    private void openApp(AppItem appItem) {
        PackageManager packageManager = getActivity().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(appItem.schemes);
        if (intent == null) {
            Uri uri = Uri.parse(appItem.url);
            if (uri != null) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent1);
                } catch (Exception e) {
                    e.printStackTrace();
                    App.toast(getActivity(), "未知应用");
                }

            } else {
                App.toast(getActivity(), "app下载地址有误");
            }
        } else {
            startActivity(intent);
        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        getData();
    }
}
