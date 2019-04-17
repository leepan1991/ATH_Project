package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageButton;
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
import cn.innovativest.ath.adapter.NoticeAdapter;
import cn.innovativest.ath.bean.Notice;
import cn.innovativest.ath.bean.NoticeBean;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.NoticeListResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.widget.VpSwipeRefreshLayout;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class NoticeListAct extends BaseAct implements OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.swipeRefresh)
    VpSwipeRefreshLayout swipeRefresh;

    @BindView(R.id.xlvCoin)
    XListView xlvCoin;

    private NoticeAdapter noticeAdapter;
    private List<Notice> noticeList;

    int pi;
    NoticeBean noticeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_notice);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        tvwTitle.setText("系统公告");
        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        noticeList = new ArrayList<Notice>();
        noticeAdapter = new NoticeAdapter(this, noticeList);
        xlvCoin.setAdapter(noticeAdapter);

        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setOnLoadMoreListener(this);
    }

    private void init() {

        swipeRefresh.setVisibility(View.VISIBLE);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setOnLoadMoreListener(this);
        pi = 1;
        request(pi);
    }

    private void request(int page) {
        //获取数据
        LogUtils.e("======" + page);
        getTradeData(page);
    }

    private void initDataToView(List<Notice> lstNotices) {
        if (pi == 1) {
            noticeList.clear();
        }
        noticeList.addAll(lstNotices);
        noticeAdapter.notifyDataSetChanged();
    }

    private void getTradeData(int page) {

        AthService service = App.get().getAthService();
        service.noticeList(page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<NoticeListResponse>() {
            @Override
            public void call(NoticeListResponse noticeListResponse) {
                if (noticeListResponse != null) {
                    if (noticeListResponse.noticeBean != null) {
                        if (noticeListResponse.noticeBean.noticeList != null && noticeListResponse.noticeBean.noticeList.size() > 0) {
                            noticeBean = noticeListResponse.noticeBean;
                            initDataToView(noticeListResponse.noticeBean.noticeList);
                        }
                    } else {
                        App.toast(NoticeListAct.this, noticeListResponse.message);
                    }
                } else {
                    App.toast(NoticeListAct.this, "数据获取失败");
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
        MobclickAgent.onPageStart("NoticeListAct");
        MobclickAgent.onResume(this);
        pi = 1;
        init();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoticeListAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        LogUtils.e("&&&&&&&&&& " + pi);
        if (noticeBean != null) {
            if (pi <= noticeBean.page) {
                request(pi);
            } else {
                refreshLayout.setNoMoreData(true);
                refreshLayout.setEnableLoadMore(false);
                App.toast(NoticeListAct.this, "没有更多内容了！");
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
