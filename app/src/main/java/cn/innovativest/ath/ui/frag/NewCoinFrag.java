package cn.innovativest.ath.ui.frag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.CoinTaskAdapter;
import cn.innovativest.ath.adapter.CoinTeamAdapter;
import cn.innovativest.ath.bean.CoinActive;
import cn.innovativest.ath.bean.CoinBanner;
import cn.innovativest.ath.bean.CoinTop;
import cn.innovativest.ath.bean.ENewCoinItem;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.NewCoinResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseFrag;
import cn.innovativest.ath.ui.act.About11Act;
import cn.innovativest.ath.ui.act.AppAct;
import cn.innovativest.ath.ui.act.LoginAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.ViewPagerScroller;
import cn.innovativest.ath.widget.VpSwipeRefreshLayout;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 20/03/2018.
 */

public class NewCoinFrag extends BaseFrag implements OnRefreshListener, RadioGroup.OnCheckedChangeListener {
    private View contentView;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.swipeRefresh)
    VpSwipeRefreshLayout swipeRefresh;

    @BindView(R.id.vprRecoImage)
    ViewPager vprRecoImage;

    @BindView(R.id.lltSmallDots)
    LinearLayout lltSmallDots;

    @BindView(R.id.rltCheck)
    RelativeLayout rltCheck;

    @BindView(R.id.rgTrade)
    RadioGroup rgTrade;

    @BindView(R.id.btnManPro)
    RadioButton btnManPro;

    @BindView(R.id.btnManPic)
    RadioButton btnManPic;

    @BindView(R.id.xlvCoin)
    XListView xlvCoin;

    @BindView(R.id.xlvTask)
    XListView xlvTask;

    private CoinTeamAdapter coinTeamAdapter;
    private List<CoinTop> lstCoinTops;

    private CoinTaskAdapter coinTaskAdapter;
    private List<CoinActive> lstCoinActives;

    private List<CoinBanner> vprRecommendData;
    private MyPagerAdapter pagerAdapter;
    private ViewPagerScroller viewPagerScroller;
    private int currentPagerIndex = 0;
    ImageView[] points;

    private CustomDialog customDialog;
//    private PasswordDialog passwordDialog;

    //    int pi;
    ENewCoinItem eNewCoinItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.new_coin_frag, container,
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

        tvwTitle.setText("活动中心");

//        View team_header_view = mLayoutInft.inflate(R.layout.coin_team_header, null);

        lstCoinTops = new ArrayList<CoinTop>();
//        xlvCoin.addHeaderView(team_header_view);
        coinTeamAdapter = new CoinTeamAdapter(getActivity(), lstCoinTops);
        xlvCoin.setAdapter(coinTeamAdapter);

        View task_header_view = mLayoutInft.inflate(R.layout.coin_task_header, null);
        lstCoinActives = new ArrayList<CoinActive>();
        xlvTask.addHeaderView(task_header_view);
        coinTaskAdapter = new CoinTaskAdapter(getActivity(), lstCoinActives, handler);
        xlvTask.setAdapter(coinTaskAdapter);

        vprRecommendData = new ArrayList<CoinBanner>();

        pagerAdapter = new MyPagerAdapter(vprRecommendData);

        ViewGroup.LayoutParams layoutParams = vprRecoImage.getLayoutParams();
        layoutParams.width = AppUtils.getDisplayWidth(getActivity());
        vprRecoImage.setLayoutParams(layoutParams);

        viewPagerScroller = new ViewPagerScroller(getActivity());
        viewPagerScroller.initViewPagerScroll(vprRecoImage); // 初始货ViewPager时,反射修改滑动速度
        vprRecoImage.setOnPageChangeListener(changeListener);// 页面切换监听
        vprRecoImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        // isAuto = false;
                        cancelHandler();
                        viewPagerScroller.setScrollDuration(500);
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessageDelayed(1, 5000);
                        break;
                    default:
                        // isAuto = true;
                        handler.sendEmptyMessageDelayed(1, 5000);
                        break;
                }
                return false;
            }
        });
        vprRecoImage.setAdapter(pagerAdapter);// 设置viewpager的adapter

        currentPagerIndex = vprRecoImage.getCurrentItem();

        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setNoMoreData(true);
        swipeRefresh.setEnableLoadMore(false);

        swipeRefresh.setVisibility(View.INVISIBLE);
        customDialog = new CustomDialog(mCtx);
        rltCheck.setVisibility(View.VISIBLE);
//        passwordDialog = new PasswordDialog(mCtx);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    requestUserInfo();
                    init();
                }
                break;
        }
    }

    private void initTop() {
        btnManPro.setChecked(true);
        btnManPic.setChecked(false);
        getData();
    }

    private void init() {

        rltCheck.setVisibility(View.VISIBLE);
        rgTrade.setOnCheckedChangeListener(this);
        swipeRefresh.setOnRefreshListener(this);
        initTop();
    }

    private void initDataToTeamView(List<CoinTop> coinTops) {
        lstCoinTops.clear();
        lstCoinTops.addAll(coinTops);
        coinTeamAdapter.notifyDataSetChanged();
    }

    private void initDataToTaskView(List<CoinActive> coinActives) {
        lstCoinActives.clear();
        lstCoinActives.addAll(coinActives);
        coinTaskAdapter.notifyDataSetChanged();
    }

    private void getData() {

        AthService service = App.get().getAthService();
        service.tops().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<NewCoinResponse>() {
            @Override
            public void call(NewCoinResponse newCoinResponse) {
                if (newCoinResponse != null) {
                    if (newCoinResponse.eNewCoinItem != null) {
                        swipeRefresh.setVisibility(View.VISIBLE);
                        eNewCoinItem = newCoinResponse.eNewCoinItem;
                        if (btnManPro.isChecked()) {
                            if (newCoinResponse.eNewCoinItem.month != null && newCoinResponse.eNewCoinItem.month.size() > 0) {
                                initDataToTeamView(newCoinResponse.eNewCoinItem.month);
                            }
                        } else if (btnManPic.isChecked()) {
                            if (newCoinResponse.eNewCoinItem.week != null && newCoinResponse.eNewCoinItem.week.size() > 0) {
                                initDataToTeamView(newCoinResponse.eNewCoinItem.week);
                            }
                        }

                        if (newCoinResponse.eNewCoinItem.active != null && newCoinResponse.eNewCoinItem.active.size() > 0) {
                            initDataToTaskView(newCoinResponse.eNewCoinItem.active);
                        }
                        if (newCoinResponse.eNewCoinItem.banner != null && newCoinResponse.eNewCoinItem.banner.size() > 0) {
                            currentPagerIndex = vprRecoImage.getCurrentItem();
                            cancelHandler();
                            vprRecommendData.clear();
                            vprRecommendData.addAll(newCoinResponse.eNewCoinItem.banner);
                            if (vprRecommendData.size() > 0) {
                                if (currentPagerIndex == 0) {
                                    currentPagerIndex = vprRecommendData.size() * 1000;
                                }
                                pagerAdapter.notifyDataSetChanged();
                                lltSmallDots.removeAllViews();
                                addPoints();
                                vprRecoImage.setCurrentItem(currentPagerIndex,
                                        true);
                                selectPointFromPagerIndex(currentPagerIndex);
                                handler.sendEmptyMessageDelayed(1, 5000);
                            }
                        }
                    } else {
                        App.toast(getActivity(), newCoinResponse.message);
                    }
                } else {
                    App.toast(getActivity(), "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
            }
        });

    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    if (!isSleeping) {
                        viewPagerScroller.setScrollDuration(1500);
                        vprRecoImage.setCurrentItem(
                                vprRecoImage.getCurrentItem() + 1, true);
                        this.sendEmptyMessageDelayed(1, 5000);
                    }
                    break;
                case 2:
                    getData();
                    break;
                default:
                    break;
            }
        }
    };

    private boolean isSleeping = false;

    private void cancelHandler() {
        if (handler.hasMessages(1)) {
            handler.removeMessages(1);
        }
    }

    // 添加圆点
    private void addPoints() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        points = new ImageView[vprRecommendData.size()];
        for (int i = 0; i < points.length; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(layoutParams);
            points[i] = imageView;
            if (i == 0) {
                points[i]
                        .setImageResource(R.drawable.ic_shouye_lunbob);
            } else {
                points[i]
                        .setImageResource(R.drawable.ic_shouye_lunboa);
            }
            lltSmallDots.addView(points[i]);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainAct"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        if (App.get().user == null) {
            customDialog.setMRightBt("去登录").setMsg("登录后进行积分兑换").setIsCancelable(true)
                    .setChooseListener(new CustomDialog.ChooseListener() {

                        @Override
                        public void onChoose(int which) {
                            if (which == WHICH_RIGHT) {
                                startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                            }
                        }
                    }).show();
        } else {
            init();
        }
        if (vprRecommendData.size() > 0) {
            cancelHandler();
            handler.sendEmptyMessageDelayed(1, 5000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainAct");
        cancelHandler();
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
        getData();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (btnManPro.isChecked()) {
            if (eNewCoinItem != null && eNewCoinItem.month != null && eNewCoinItem.month.size() > 0) {
                initDataToTeamView(eNewCoinItem.month);
            }
        } else if (btnManPic.isChecked()) {
            if (eNewCoinItem != null && eNewCoinItem.week != null && eNewCoinItem.week.size() > 0) {
                initDataToTeamView(eNewCoinItem.week);
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        private List<CoinBanner> vprRecommendData;

        public MyPagerAdapter(List<CoinBanner> vprRecommendData) {
            this.vprRecommendData = vprRecommendData;
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(final ViewGroup container,
                                      final int position) {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.item_reco_vpr, null);
            container.addView(view);
            ImageView ivwRecoBigImg = (ImageView) view
                    .findViewById(R.id.ivwRecoBigImg);
            try {

                final CoinBanner recommendData = vprRecommendData
                        .get(position % vprRecommendData.size());
                GlideApp.with(getActivity()).load(AppConfig.ATH_APP_URL + recommendData.img).into(ivwRecoBigImg);
                ivwRecoBigImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
                            startActivity(new Intent(getActivity(), About11Act.class));
                        }
                    }
                });
            } catch (Exception e) {
                // TODO: handle exception
            }

            return view;

        }
    }

    private void selectPointFromPagerIndex(int pagerIndex) {
        int position = pagerIndex % vprRecommendData.size();
        int n = points.length;
        for (int i = 0; i < n; i++) {
            if (i == position) {
                points[i]
                        .setImageResource(R.drawable.ic_shouye_lunbob);
            } else {
                points[i]
                        .setImageResource(R.drawable.ic_shouye_lunboa);
            }

        }
    }

    private ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {
            try {
                selectPointFromPagerIndex(arg0);
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

}
