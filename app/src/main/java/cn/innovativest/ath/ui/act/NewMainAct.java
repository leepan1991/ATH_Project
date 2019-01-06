package cn.innovativest.ath.ui.act;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.CommonResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.ui.frag.AppFrag;
import cn.innovativest.ath.ui.frag.MineFrag;
import cn.innovativest.ath.ui.frag.NewCoinFrag;
import cn.innovativest.ath.ui.frag.NewMainFrag;
import cn.innovativest.ath.ui.frag.TradeFrag;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import io.rong.imkit.RongIM;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class NewMainAct extends BaseAct {

    // 定义FragmentTabHost对象
    private FragmentTabHost mTabHost;
    //    private int[] mImageViewArray = {R.drawable.main_logo_not_select,
//            R.drawable.main_trade_not_select, R.drawable.main_coin_not_select,
//            R.drawable.main_account_not_select};
    private int[] mBgArray = {R.drawable.new_tab_main_logo,
            R.drawable.new_tab_trade_logo,
            R.drawable.new_tab_coin_logo,
            R.drawable.new_tab_app_logo, R.drawable.new_tab_mine_logo};
    private String[] mTextviewArray;
    private Class[] fragmentArray = {NewMainFrag.class,
            TradeFrag.class, NewCoinFrag.class, AppFrag.class, MineFrag.class};

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
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("isAboutAth")) {
            popDialog();
        } else {
            getCommonData();
        }
        mTextviewArray = new String[5];
        mTextviewArray[0] = getString(R.string.tab_main);
        mTextviewArray[1] = getString(R.string.tab_purchase);
        mTextviewArray[2] = getString(R.string.tab_coin);
        mTextviewArray[3] = getString(R.string.tab_app);
        mTextviewArray[4] = getString(R.string.tab_mine);
        initView();
    }

    private void popDialog() {
        final AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("提示")//标题
                .setMessage("个人账户->ATH生态圈须知可再次查看")//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getCommonData();
                    }
                })
                .create();
        alertDialog1.show();
    }

    private void popSpecialDialog(String msg) {
        final AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                .setTitle("特殊公告")//标题
                .setMessage(msg)//内容
                .setIcon(R.mipmap.ic_launcher)//图标
                .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
        alertDialog1.show();
    }

    private void getCommonData() {

        AthService service = App.get().getAthService();
        service.commonInfo(24).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<CommonResponse>() {
            @Override
            public void call(CommonResponse commonResponse) {
                if (commonResponse != null) {
                    if (commonResponse.commonItem != null) {
                        if (commonResponse.commonItem.title.equals("特殊公告")) {
                            if (!CUtils.isEmpty(commonResponse.commonItem.exchange)) {
                                popSpecialDialog(commonResponse.commonItem.exchange);
                            }
                        }
                    } else {
                        App.toast(NewMainAct.this, commonResponse.message);
                    }
                } else {
//                    App.toast(NewMainAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
//                App.toast(NewMainAct.this, "数据获取失败");
            }
        });

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
        View view = LayoutInflater.from(this).inflate(R.layout.new_main_tab_item,
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
