package cn.innovativest.ath.ui.frag;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.FundGallerAdapter;
import cn.innovativest.ath.bean.FundGallery;
import cn.innovativest.ath.ui.BaseFrag;

public class FundFrag extends BaseFrag {

    private View contentView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    LinearLayout linear;
    //    private GridView grid;
//    private FundGallerAdapter fundGallerAdapter;
    private List<FundGallery> lstFundGallerys;

    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View view1, view2, view3, view4, view5;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    private List<String> listTitles;
    private List<Fragment> fragments;
    private List<TextView> listTextViews;

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
//            ButterKnife.bind(this, contentView);
            initView();
            inintent();
        }

        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;

    }

    private void initView() {
        mViewPager = (ViewPager) contentView.findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) contentView.findViewById(R.id.tablayout);
//        grid = (GridView) contentView.findViewById(R.id.grid);
        initData();
    }

    private void inintent() {

        lstFundGallerys = new ArrayList<>();
        FundGallery fundGallery1 = new FundGallery();
        fundGallery1.setName("第一个众筹");
        lstFundGallerys.add(fundGallery1);
        lstFundGallerys.add(fundGallery1);
        lstFundGallerys.add(fundGallery1);
        lstFundGallerys.add(fundGallery1);
        lstFundGallerys.add(fundGallery1);
//        fundGallerAdapter = new FundGallerAdapter(getActivity(), lstFundGallerys);
//        grid.setAdapter(fundGallerAdapter);
//        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                App.toast(getActivity(), lstFundGallerys.get(position).getName());
//            }
//        });


        linear = (LinearLayout) contentView.findViewById(R.id.linear);
        //开始添加数据
        for (int x = 0; x < lstFundGallerys.size(); x++) {
            //寻找行布局，第一个参数为行布局ID，第二个参数为这个行布局需要放到那个容器上
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.fund_item, linear, false);
            //通过View寻找ID实例化控件
//            ImageView img = (ImageView) view.findViewById(R.id.imageView);
            //实例化TextView控件
            TextView tv = (TextView) view.findViewById(R.id.tvwName);

            //给TextView添加文字
            tv.setText(lstFundGallerys.get(x).getName());
            //把行布局放到linear里
            linear.addView(view);
        }
    }

    private void initData() {


        listTitles = new ArrayList<>();
        fragments = new ArrayList<>();
        listTextViews = new ArrayList<>();

        listTitles.add("面铺众筹");
        listTitles.add("回馈众筹");
        listTitles.add("公益众筹");
        listTitles.add("债权众筹");
        listTitles.add("产品众筹");
        listTitles.add("科技众筹");

        for (int i = 0; i < listTitles.size(); i++) {
            ContentFragment fragment = ContentFragment.newInstance(listTitles.get(i));
            fragments.add(fragment);

        }
        //mTabLayout.setTabMode(TabLayout.SCROLL_AXIS_HORIZONTAL);//设置tab模式，当前为系统默认模式
        for (int i = 0; i < listTitles.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(listTitles.get(i)));//添加tab选项
        }

        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }

            //ViewPager与TabLayout绑定后，这里获取到PageTitle就是Tab的Text
            @Override
            public CharSequence getPageTitle(int position) {
                return listTitles.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);

        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器

    }

    @Override
    public void onClick(View v) {

    }
}
