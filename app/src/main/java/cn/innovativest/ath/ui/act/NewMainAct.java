package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.ui.frag.CoinFrag;
import cn.innovativest.ath.ui.frag.MainFrag;
import cn.innovativest.ath.ui.frag.MineFrag;
import cn.innovativest.ath.ui.frag.NewMainFrag;
import cn.innovativest.ath.ui.frag.TradeFrag;
import io.rong.imkit.RongIM;


public class NewMainAct extends BaseAct {

    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //    private int[] mImageViewArray = {R.drawable.main_logo_not_select,
//            R.drawable.main_trade_not_select, R.drawable.main_coin_not_select,
//            R.drawable.main_account_not_select};
    private int[] mBgArray = {R.drawable.new_tab_main_logo,
            R.drawable.new_tab_trade_logo,
            R.drawable.new_tab_coin_logo, R.drawable.new_tab_mine_logo};
    private String[] mTextviewArray;
    private Class[] fragmentArray = {NewMainFrag.class,
            TradeFrag.class, CoinFrag.class, MineFrag.class};

    private long mExitTime;

//    private int tag = 0;

//    public static int tag = 7;

    public FragmentTabHost getmTabHost() {
        return mTabHost;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_main_act);
        mTextviewArray = new String[4];
        mTextviewArray[0] = getString(R.string.tab_main);
        mTextviewArray[1] = getString(R.string.tab_purchase);
        mTextviewArray[2] = getString(R.string.tab_coin);
        mTextviewArray[3] = getString(R.string.tab_mine);
        initView();
    }

    private void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(NewMainAct.this, getSupportFragmentManager(), R.id.content);
//        mTabHost.getTabWidget().setDividerDrawable(R.drawable.main_ver_line);
        // 得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i])
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.main_tab_item,
                null);
        LinearLayout lltbg = (LinearLayout) view.findViewById(R.id.lltbg);
//        lltbg.setBackgroundResource(mBgArray[index]);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mBgArray[index]);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if ((System.currentTimeMillis() - mExitTime) > 2000) {// 如果两次按键时间间隔大于2000毫秒，则不退出
                        App.toast(NewMainAct.this, "再按一次退出");
                        mExitTime = System.currentTimeMillis();// 更新mExitTime
                    } else {
                        // 否则退出程序
//                        App.get().user = null;
                        if (RongIM.getInstance() != null)
                            RongIM.getInstance().disconnect();
                        android.os.Process.killProcess(android.os.Process.myPid());
                        NewMainAct.this.finish();
                    }
                    return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainAct");
        MobclickAgent.onResume(this);
//        if (tag != 7) {
//            mTabHost.setCurrentTab(tag);
//            tag = 7;
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {

    }

}
