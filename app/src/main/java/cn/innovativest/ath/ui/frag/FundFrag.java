package cn.innovativest.ath.ui.frag;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.tabs.TabLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.FundAdapter;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.CrowdFundingType;
import cn.innovativest.ath.response.FundItem;
import cn.innovativest.ath.response.FundResponse;
import cn.innovativest.ath.response.Hot;
import cn.innovativest.ath.ui.BaseFrag;
import cn.innovativest.ath.ui.act.AddFundAct;
import cn.innovativest.ath.ui.act.FundDetailAct;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class FundFrag extends BaseFrag implements OnRefreshListener, OnLoadMoreListener {

    private View contentView;
    @BindView(R.id.tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.tvFundComplete)
    TextView tvFundComplete;

    @BindView(R.id.tvRegisterNum)
    TextView tvRegisterNum;

    @BindView(R.id.tvFundAmount)
    TextView tvFundAmount;

    @BindView(R.id.tvFundNum)
    TextView tvFundNum;

    @BindView(R.id.tvwAction)
    ImageButton tvwAction;

    LinearLayout linear;
    //    private GridView grid;
//    private FundGallerAdapter fundGallerAdapter;
    private List<Hot> lstFundGallerys;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View view1, view2, view3, view4, view5;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private List<CrowdFundingType> listTitles;
//    private List<Fragment> fragments;
//    private List<TextView> listTextViews;

    @BindView(R.id.fund_listview)
    XListView fund_listview;
    private FundAdapter fundAdapter;
    private List<FundItem> lstFundItems;
    int pi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.fund_frag, container,
                    false);
            ButterKnife.bind(this, contentView);
            initView();
            getFundData(1, 1);
        }

        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;

    }

    private void initView() {
        tvwAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddFundAct.class));
            }
        });
        lstFundItems = new ArrayList<>();
        listTitles = new ArrayList<>();
        fundAdapter = new FundAdapter(getActivity(), lstFundItems);
        fund_listview.setAdapter(fundAdapter);
        fund_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
//        initData();
    }

    private void inintent(List<Hot> hots) {

        lstFundGallerys = new ArrayList<>();
        lstFundGallerys.clear();
        lstFundGallerys.addAll(hots);

        linear = (LinearLayout) contentView.findViewById(R.id.linear);
        //开始添加数据
        for (int x = 0; x < lstFundGallerys.size(); x++) {
            //寻找行布局，第一个参数为行布局ID，第二个参数为这个行布局需要放到那个容器上
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fund_item, linear, false);
            //通过View寻找ID实例化控件
//            ImageView img = (ImageView) view.findViewById(R.id.imageView);
            //实例化TextView控件
            TextView tv = (TextView) view.findViewById(R.id.tvwName);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_logo);
            if (lstFundGallerys.get(x).getGetCrowdFundingText().getImgLink().contains("|")) {
                GlideApp.with(getActivity()).load(lstFundGallerys.get(x).getGetCrowdFundingText().getImgLink().substring(lstFundGallerys.get(x).getGetCrowdFundingText().getImgLink().indexOf("|"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv);
            } else {
                GlideApp.with(getActivity()).load(lstFundGallerys.get(x).getGetCrowdFundingText().getImgLink()).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(iv);
            }
            //给TextView添加文字
            tv.setText(lstFundGallerys.get(x).getGetCrowdFundingText().getTitle());
            view.setTag(lstFundGallerys.get(x));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Hot hot = (Hot) view.getTag();
                    startActivity(new Intent(getActivity(), FundDetailAct.class).putExtra("id", hot.getId()));
                }
            });
            //把行布局放到linear里
            linear.addView(view);
        }
    }

    private void initData(List<CrowdFundingType> crowdFundingTypes) {


        listTitles.clear();
        listTitles.addAll(crowdFundingTypes);
//        fragments = new ArrayList<>();


//        for (int i = 0; i < listTitles.size(); i++) {
//            ContentFragment fragment = ContentFragment.newInstance(listTitles.get(i).getTitle());
//            fragments.add(fragment);
//
//        }
        //mTabLayout.setTabMode(TabLayout.SCROLL_AXIS_HORIZONTAL);//设置tab模式，当前为系统默认模式
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("TAG", "tab position:" + tab.getPosition());
                pi = 1;
//                getFundData(listTitles.get(tab.getPosition()).getId(), pi);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < listTitles.size(); i++) {
            mTabLayout.removeAllTabs();
            mTabLayout.addTab(mTabLayout.newTab().setText(listTitles.get(i).getTitle()));//添加tab选项
        }

//        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
//            @Override
//            public Fragment getItem(int position) {
//                return fragments.get(position);
//            }
//
//            @Override
//            public int getCount() {
//                return fragments.size();
//            }
//
//            //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return listTitles.get(position).getTitle();
//            }
//        };
//        mViewPager.setAdapter(mAdapter);
//
//        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
//        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
    }

    private void initDataToView(List<FundItem> fundItems) {
        if (pi == 1) {
            lstFundItems.clear();
        }
        lstFundItems.addAll(fundItems);
        fundAdapter.notifyDataSetChanged();
    }

    private void getFundData(int id, int page) {

        LoadingUtils.getInstance().dialogDismiss();
        LoadingUtils.getInstance().dialogShow(getActivity(), "请求中。。。", false);
        AthService service = App.get().getAthService();
        service.crowd_funding_list(page, id + "").observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<FundResponse>() {
            @Override
            public void call(FundResponse fundResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (fundResponse != null) {
                    if (fundResponse.getData() != null) {
                        tvFundComplete.setText("已完成： " + fundResponse.getData().getWancheng() + " 个项目");
                        tvRegisterNum.setText("未完成： " + fundResponse.getData().getWeiwancheng() + " 个项目");
                        tvFundAmount.setText("已筹款： " + fundResponse.getData().getRmb() + " 元");
                        tvFundNum.setText("公益基金： " + fundResponse.getData().getCommonweal() + " 元");
                    }
                    if (fundResponse.getData() != null && fundResponse.getData().getHot().size() > 0) {
                        inintent(fundResponse.getData().getHot());
                    }
                    if (fundResponse.getData() != null && fundResponse.getData().getCrowdFundingType().size() > 0) {
                        initData(fundResponse.getData().getCrowdFundingType());
                    }

                    if (fundResponse.getData() != null && fundResponse.getData().getCrowdFundingList().getData().size() > 0) {
                        initDataToView(fundResponse.getData().getCrowdFundingList().getData());
                    } else {
                        if (pi == 1) {
                            lstFundItems.clear();
                        }
                        fundAdapter.notifyDataSetChanged();
//                        App.toast(getActivity(), tradeResponse.message);
                    }
                } else {
                    App.toast(getActivity(), "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                LogUtils.e(throwable.getMessage());
                App.toast(getActivity(), "数据获取失败");
                if (pi > 1) {
                    pi--;
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        LogUtils.e("&&&&&&&&&& " + pi);
//        if (eComment != null) {
//            if (pi <= eComment.total) {
//                request(id, pi);
//            } else {
//                refreshLayout.setNoMoreData(true);
//                refreshLayout.setEnableLoadMore(false);
//                App.toast(getActivity(), "没有更多内容了！");
//                pi--;
//            }
//        } else {
//            pi--;
//        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        refreshLayout.setNoMoreData(false);
        refreshLayout.setEnableLoadMore(true);
        pi = 1;
        getFundData(1,pi);
    }
}
