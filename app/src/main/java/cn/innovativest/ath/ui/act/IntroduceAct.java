package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import java.util.ArrayList;
import java.util.List;

import cn.innovativest.ath.R;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AppUtils;

/**
 * 介绍界面
 */
public class IntroduceAct extends BaseAct {
    private ViewPager mViewPaper;
    //	private Button mEnterBt;
//	ImageView[] pointImgVIews;
    private List<View> mViewList;
    int currentIndex = 0;
    int[] picIds = new int[]{R.drawable.ic_bg_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduce_act);
        mViewPaper = (ViewPager) findViewById(R.id.introduce_viewpaper);
//        mEnterBt = (Button) findViewById(R.id.introduce_bt);
//        mEnterBt.setVisibility(View.INVISIBLE);
//        mEnterBt.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PrefsManager.get().saveIsIntroduce(true);
//                if (PrefsManager.get().getIsAutoLogin()) {
//                    AppData.user = PrefsManager.get().getLastLoginUser();
//                    if (AppData.user == null) {
//                        AppUtil.startActivity(mCtx, LoginAct.class);
//                    } else {
//                        AppUtil.startActivity(mCtx, MainAct.class,
//                                Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                        | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    }
//                } else {
//                    AppUtil.startActivity(mCtx, LoginAct.class);
//                }
//                AppUtil.finish(mCtx);
//            }
//        });

        mViewList = new ArrayList<View>();
        for (int i : picIds) {
            ImageView iv = new ImageView(this);
            iv.setScaleType(ScaleType.FIT_XY);
            iv.setImageResource(i);
            mViewList.add(iv);
        }
        mViewPaper.setAdapter(new IPagerAdapter());
        mViewPaper.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                if (position != currentIndex) {
//                    pointImgVIews[currentIndex]
//                            .setBackgroundResource(R.drawable.common_point_nor);
//                    pointImgVIews[position]
//                            .setBackgroundResource(R.drawable.common_point_hor);
                    currentIndex = position;
                }
//                if (position == picIds.length - 1) {
//                    mEnterBt.setVisibility(View.VISIBLE);
//                } else {
//                    mEnterBt.setVisibility(View.INVISIBLE);
//                }
            }
        });
        mViewPaper.setCurrentItem(currentIndex);

//        LinearLayout pointLay = (LinearLayout) findViewById(R.id.introduce_point_lay);
//        pointImgVIews = new ImageView[picIds.length];
//        for (int i = 0; i < picIds.length; i++) {
//            pointImgVIews[i] = new ImageView(this);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//            lp.leftMargin = 10;
//            pointImgVIews[i].setLayoutParams(lp);
//            pointImgVIews[i].setScaleType(ScaleType.CENTER_CROP);
//            if (i == 0) {
//                pointImgVIews[i]
//                        .setBackgroundResource(R.drawable.common_point_hor);
//            } else {
//                pointImgVIews[i]
//                        .setBackgroundResource(R.drawable.common_point_nor);
//            }
//            pointLay.addView(pointImgVIews[i]);
//        }
//        AppUtil.setStatusPadding(mCtx, R.id.header_lay);
        handler.sendEmptyMessageDelayed(1, 2000);
    }

    // 让图片自动播放,无限循环
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (currentIndex == picIds.length - 1) {
                AppUtils.startActivity(IntroduceAct.this, MainAct.class);
                AppUtils.finish(IntroduceAct.this);
            }
        }
    };


    class IPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mViewList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            if (mViewList == null || mViewList.size() == 0) {
                return null;
            }
            View v = mViewList.get(position);
            ViewGroup p = (ViewGroup) v.getParent();
            if (p != null) {
                p.removeView(v);
            }
            container.addView(v);
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position == picIds.length - 1) {
//                        PrefsManager.get().save("isIntroduce", true);
//                        if (App.get().user == null) {
//                            AppUtils.startActivity(IntroduceAct.this, LoginAct.class);
//                        } else {
//                            AppUtils.startActivity(IntroduceAct.this, MainAct.class,
//                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                            | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        AppUtils.startActivity(IntroduceAct.this, MainAct.class);
////                        }
//                        AppUtils.finish(IntroduceAct.this);
                    }
                }
            });
            return v;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}
