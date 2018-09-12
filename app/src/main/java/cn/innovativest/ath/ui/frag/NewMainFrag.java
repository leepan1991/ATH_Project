package cn.innovativest.ath.ui.frag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.MainPage;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.entities.MainPageBody;
import cn.innovativest.ath.entities.MiningBody;
import cn.innovativest.ath.response.MainPageResponse;
import cn.innovativest.ath.response.MiningResponse;
import cn.innovativest.ath.response.PitunlockResponse;
import cn.innovativest.ath.ui.BaseFrag;
import cn.innovativest.ath.ui.act.FriendHelpAct;
import cn.innovativest.ath.ui.act.LoginAct;
import cn.innovativest.ath.ui.act.RechargeAct;
import cn.innovativest.ath.ui.act.SpeedValueAct;
import cn.innovativest.ath.ui.act.StoryAct;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PayResult;
import cn.innovativest.ath.utils.SoundPoolUtil;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.MyScrollView;
import cn.innovativest.ath.widget.VpSwipeRefreshLayout;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by leepan on 20/03/2018.
 */

public class NewMainFrag extends BaseFrag implements OnRefreshListener {

    private View contentView;

    @BindView(R.id.mSwipeLayout)
    VpSwipeRefreshLayout mSwipeLayout;

    @BindView(R.id.scrollView)
    MyScrollView scrollView;

    @BindView(R.id.tvwCollectValue)
    TextView tvwCollectValue;

    @BindView(R.id.tvwHelpValue)
    TextView tvwHelpValue;

    @BindView(R.id.tvwCoinValue)
    TextView tvwCoinValue;

    @BindView(R.id.rltCollecting)
    LinearLayout rltCollecting;

    @BindView(R.id.tvwCollecting)
    TextView tvwCollecting;

    @BindView(R.id.ivCollectingBg)
    ImageView ivCollectingBg;

    @BindView(R.id.ivCollecting)
    ImageView ivCollecting;

    @BindView(R.id.btnCollecting)
    Button btnCollecting;

    @BindView(R.id.rltUnlockOne)
    LinearLayout rltUnlockOne;

    @BindView(R.id.tvwUnlockOne)
    TextView tvwUnlockOne;

    @BindView(R.id.ivUnlockOneBg)
    ImageView ivUnlockOneBg;

    @BindView(R.id.ivUnlockOne)
    ImageView ivUnlockOne;

    @BindView(R.id.btnUnlockOne)
    Button btnUnlockOne;

    @BindView(R.id.rltUnlockTwo)
    LinearLayout rltUnlockTwo;

    @BindView(R.id.tvwUnlockTwo)
    TextView tvwUnlockTwo;

    @BindView(R.id.ivUnlockTwoBg)
    ImageView ivUnlockTwoBg;

    @BindView(R.id.ivUnlockTwo)
    ImageView ivUnlockTwo;

    @BindView(R.id.btnUnlockTwo)
    Button btnUnlockTwo;

    @BindView(R.id.btnLockedOne)
    ImageButton btnLockedOne;

    @BindView(R.id.btnLockedTwo)
    ImageButton btnLockedTwo;

    @BindView(R.id.btnLockedThree)
    ImageButton btnLockedThree;

    @BindView(R.id.ivCaifu)
    ImageView lltValueSpeed;

    @BindView(R.id.ivYqoqing)
    ImageView lltGetFriend;

    @BindView(R.id.ivMiji)
    ImageView lltProjectWhitePage;

    @BindView(R.id.tvwATHAllValue)
    TextView tvwATHAllValue;

    @BindView(R.id.tvwMarketValue)
    TextView tvwMarketValue;

    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2018060560268737";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /**
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEogIBAAKCAQEAnL1jiVr2zW2CoWswu6XNu2YHLpxg/fgKfPI3kPhLaFOjd1zYBH2TZMJytTZTO2WwBcIzpDPn7IKp8XAbX7mBkWLuDa8zlKr8RUCuFwtyAfLHU3DDWcsY5zWNbqyMp/JIaCgMqtDntqloSFS2qnJO8dea6PqY84goo+kdYRZ3Kzl7Ul3EZObEO6SPZ5bU5yH4jphRgCmmH89GYlo1p4+CQ5N0JVKY0r/E2GoPAXqi06WPgOMbGpPAaH0kGqzSVyi6vRBlFUqiEXLfuzvLSApZoGph6lflROHenUh8JndaUb2hNcfQTuft4j/JLR4DE9UyTMHteApapJRk6STmrAkOawIDAQABAoIBAG6tWQzTm7TBYF2lOBs44AY07Ftgdyi+roE99Di861p2vNX7TFoXZi3fFGqbOriVfG8Ei7ymHl2mgmQHOn0km7ZSujAViAGxn0MzgfqpzU5M5a0o0fik7ifNa9o7o3KwJarOpOs1anlUNFvm3bmLz+z7xto+oiRNAA2F/YXh/DIbYMgdUQWF52l7MgsGhi7MCFtRlno8HRSY6b8LE2QuNr64/J0/5IESXo0JI9Ek7s4TcCZ8YnGT1v42RNQ4RGvQXl/BiW1l3VfCNc92yKnILuKknUvID6TkKyevUfL0DL6bmZYTQXcDiU+JmQf5Xben9wtUhhVZSxydR9OO+bqR93kCgYEAzzDBFHcC0/Q/ZYnZjfs8QVgUQyZbFh5zip1PnC3iiK2pqjqL9gePkPKJ2cKWW7B3gdvd/Z2KBhwo58Y+c9/Z2fm1SGUKJIGoxTk5g1X99chAQAX4fHEnWukmL87phU7oSbWTvl66EuijPvydQX5qVXQkzhActD4f6maEfXG4f58CgYEAwaoOVBKmer7yheCL4xRx68UHc3VbqFYS7+4mZRpzntzZsGQ+IcjJw9X9aWeJN6uP6VVm3rDBFF+3iHwfPLF9A1Y6frQBp62b4Z6ZnHQLIthwO6hSZtBPloePj5eE+5ICHEXBEHWuO4XicBTMBmaDoAmtuif9DFv5s3Ti7j1MTbUCgYASzQ7IR6BnEWPrV935B1JJb6+vBD0BvdOoQWwm9Pb4hiG+Q7/NnJQHiCrAKusv+MxvaT80s2YB9e40UgX6x9Zh9Enh/uEzvNxOwUmZxGTeN8S0ypXo3O/ATSXc8r64DRgBEEwO21OxQZEGty+h8NG/XWG1nTqtlHGa+KCPLZGbawKBgFudchfNltn8WMiCeEqdUmMhmyvAefLBfUXpmFo90DJ38bdjRI1A6kntgmsJor0mOPc+AmMYpM5ZlX5IkZJpuGUKtrNXvmyvUU3DdJGxx87dKwLd1tVyeCQSzxQzrqI/6SWsze9WbG0WIg+5lub0OhJMYdXtsuTU4eRGSFBByUX9AoGAVaMXJaJ4sMuA3lOjE1VijAZVfFgj/4dPVWEy159XNKIX7BWsiCQPrHBTsOXrCdmR8HqMS/SaJBXAY7DxUNALiF07DAX8BqFdurJhDMJLtLe8GzMYoPzw9638B2J23wQIVpM/Nc8O7bR2buPmibAT1YZ+po4AarIkwNA2P/pQqMA=";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;

    private Subscription subscription1;
    private Subscription subscription2;
    private Subscription subscription3;

    private CustomDialog customDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.new_main_frag, container,
                    false);
            ButterKnife.bind(this, contentView);
            initView();
            addListener();
//            request("1.0");

        }

        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainAct");
        request("1.0");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainAct");
    }

    private void initView() {
        customDialog = new CustomDialog(mCtx);
        rltCollecting.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                int dd = (AppUtils.getDisplayWidth(getActivity()) - AppUtils.dip2px(getActivity(), 30) - rltCollecting.getMeasuredWidth() * 3) / 2;

                LinearLayout.LayoutParams layout11 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout11.setMargins(0, 0, 0, 0);
                rltCollecting.setLayoutParams(layout11);

                LinearLayout.LayoutParams layout22 = new LinearLayout.LayoutParams(//15,15,20,20,77,77,77
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layout22.setMargins(dd, 0, 0, 0);
                rltUnlockOne.setLayoutParams(layout22);

                LinearLayout.LayoutParams layout33 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout33.setMargins(dd, 0, 0, 0);
                rltUnlockTwo.setLayoutParams(layout33);
                return true;
            }
        });

        btnLockedOne.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                int d = (AppUtils.getDisplayWidth(getActivity()) - AppUtils.dip2px(getActivity(), 40) - btnLockedOne.getMeasuredWidth() * 3) / 2;

                LinearLayout.LayoutParams layout1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout1.setMargins(0, 0, 0, 0);
                btnLockedOne.setLayoutParams(layout1);

                LinearLayout.LayoutParams layout2 = new LinearLayout.LayoutParams(//15,15,20,20,77,77,77
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout2.setMargins(d, 0, 0, 0);
                btnLockedTwo.setLayoutParams(layout2);

                LinearLayout.LayoutParams layout3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout3.setMargins(d, 0, 0, 0);
                btnLockedThree.setLayoutParams(layout3);
                return true;
            }
        });

        lltValueSpeed.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {

                int dd = (AppUtils.getDisplayWidth(getActivity()) - AppUtils.dip2px(getActivity(), 30) - lltValueSpeed.getMeasuredWidth() * 3) / 2;

                LinearLayout.LayoutParams layout11 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout11.setMargins(0, 0, 0, 0);
                lltValueSpeed.setLayoutParams(layout11);

                LinearLayout.LayoutParams layout22 = new LinearLayout.LayoutParams(//15,15,20,20,77,77,77
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layout22.setMargins(dd, 0, 0, 0);
                lltGetFriend.setLayoutParams(layout22);

                LinearLayout.LayoutParams layout33 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layout33.setMargins(dd, 0, 0, 0);
                lltProjectWhitePage.setLayoutParams(layout33);
                return true;
            }
        });
        if (App.get().user == null) {

            btnCollecting.setText("解锁");
            btnUnlockOne.setText("解锁");
            btnUnlockTwo.setText("解锁");

            tvwCollecting.setVisibility(View.INVISIBLE);
            tvwUnlockOne.setVisibility(View.INVISIBLE);
            tvwUnlockTwo.setVisibility(View.INVISIBLE);

            ivCollectingBg.setVisibility(View.VISIBLE);
            ivCollecting.setBackgroundResource(R.drawable.new_main_jiesuo);
            Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivCollectingBg);

            ivUnlockOneBg.setVisibility(View.VISIBLE);
            ivUnlockOne.setBackgroundResource(R.drawable.new_main_jiesuo);
            Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockOneBg);

            ivUnlockTwoBg.setVisibility(View.VISIBLE);
            ivUnlockTwo.setBackgroundResource(R.drawable.new_main_jiesuo);
            Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockTwoBg);

            btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji0);
            Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);

            btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji0);
            Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);

            btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji0);
            Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);


//            btnLockedOne.setBackgroundResource(R.drawable.main_null);
//            btnLockedTwo.setBackgroundResource(R.drawable.main_null);
//            btnLockedThree.setBackgroundResource(R.drawable.main_null);

            tvwCollectValue.setText("宝藏(ATH): 0.000000");
            tvwHelpValue.setText("助力值: 0.00");
            tvwCoinValue.setText("积分: 0.00");
            tvwATHAllValue.setText("0.000000");
            tvwMarketValue.setText("0.00CNY");

        }

    }

    private void addListener() {
        btnCollecting.setOnClickListener(this);
        btnUnlockOne.setOnClickListener(this);
        btnUnlockTwo.setOnClickListener(this);
        btnLockedOne.setOnClickListener(this);
        btnLockedTwo.setOnClickListener(this);
        btnLockedThree.setOnClickListener(this);
        lltValueSpeed.setOnClickListener(this);
        lltGetFriend.setOnClickListener(this);
        lltProjectWhitePage.setOnClickListener(this);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setEnableLoadMore(false);
    }

    /**
     * RxJava 方式实现
     */
    private void rxJava1(final TextView tv,final Button btn,final ImageView imv, long allTime) {

        if (subscription1 != null && !subscription1.isUnsubscribed()) {
            subscription1.unsubscribe();
        }

        if (allTime > 28800) {
            allTime = 28800;
        }
        final long count = allTime;
        subscription1 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (28800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
//                        return count - aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
                //执行计时任务前先将 button 设置为不可点击
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onCompleted() {
                        tv.setVisibility(View.INVISIBLE);
                        btn.setText("领取");
                        Glide.with(getActivity()).asGif().load(R.drawable.dot1).into(imv);
                        imv.setBackgroundResource(R.drawable.new_main_linqu);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong > 28800) {
                            tv.setVisibility(View.INVISIBLE);
                            btn.setText("领取");
                            Glide.with(getActivity()).asGif().load(R.drawable.dot1).into(imv);
                            imv.setBackgroundResource(R.drawable.new_main_linqu);

                        } else {
                            tv.setVisibility(View.VISIBLE);
                            btn.setText("休眠中");
                            imv.setBackgroundResource(R.drawable.new_main_xiumian);
                            Glide.with(getActivity()).asGif().load(R.drawable.new_main_kaikuang).into(imv);
                        }
                    }
                });
    }

    private void rxJava2(final TextView tv,final Button btn,final ImageView imv, long allTime) {

        if (subscription2 != null && !subscription2.isUnsubscribed()) {
            subscription2.unsubscribe();
        }

        if (allTime > 28800) {
            allTime = 28800;
        }
        final long count = allTime;
        subscription2 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (28800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
//                        return count - aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
                //执行计时任务前先将 button 设置为不可点击
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onCompleted() {
                        tv.setVisibility(View.INVISIBLE);
                        btn.setText("领取");
                        Glide.with(getActivity()).asGif().load(R.drawable.dot1).into(imv);
                        imv.setBackgroundResource(R.drawable.new_main_linqu);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong > 28800) {
                            tv.setVisibility(View.INVISIBLE);
                            btn.setText("领取");
                            Glide.with(getActivity()).asGif().load(R.drawable.dot1).into(imv);
                            imv.setBackgroundResource(R.drawable.new_main_linqu);

                        } else {
                            tv.setVisibility(View.VISIBLE);
                            btn.setText("休眠中");
                            imv.setBackgroundResource(R.drawable.new_main_xiumian);
                            Glide.with(getActivity()).asGif().load(R.drawable.new_main_kaikuang).into(imv);
                        }
                    }
                });
    }

    private void rxJava3(final TextView tv,final Button btn,final ImageView imv, long allTime) {

        if (subscription3 != null && !subscription3.isUnsubscribed()) {
            subscription3.unsubscribe();
        }

        if (allTime > 28800) {
            allTime = 28800;
        }
        final long count = allTime;
        subscription3 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (28800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
//                        return count - aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
                //执行计时任务前先将 button 设置为不可点击
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Subscriber<Long>() {

                    @Override
                    public void onCompleted() {
                        tv.setVisibility(View.INVISIBLE);
                        btn.setText("领取");
                        Glide.with(getActivity()).asGif().load(R.drawable.dot1).into(imv);
                        imv.setBackgroundResource(R.drawable.new_main_linqu);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong > 28800) {
                            tv.setVisibility(View.INVISIBLE);
                            btn.setText("领取");
                            Glide.with(getActivity()).asGif().load(R.drawable.dot1).into(imv);
                            Glide.with(getActivity()).asGif().into(imv);

                        } else {
                            tv.setVisibility(View.VISIBLE);
                            btn.setText("休眠中");
                            imv.setBackgroundResource(R.drawable.new_main_xiumian);
                            Glide.with(getActivity()).asGif().load(R.drawable.new_main_kaikuang).into(imv);
                        }
                    }
                });
    }

    public static String secToTime(long time) {
        String timeStr = null;
        long hour = 0;
        long minute = 0;
        long second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
//                if (hour > 48)
//                    return "可以进行开采";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(long i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Long.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    private void request(String status) {
        //获取数据
        getMainPageData("1.0");
    }

    public boolean useList(String[] arr, String targetValue) {
        return Arrays.asList(arr).contains(targetValue);
    }

    //遍历数组
    public static int printArray(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;//当if条件不成立时，默认返回一个负数值-1
    }

    private void initDataToView(MainPage mainPage) {
        tvwCollectValue.setText("宝藏(ATH): " + (!CUtils.isEmpty(mainPage.ath) ? String.format("%.6f", Float.parseFloat(mainPage.ath)) : 0.000000));
        tvwHelpValue.setText("助力值: " + (!CUtils.isEmpty(mainPage.help_value) ? String.format("%.2f", Float.parseFloat(mainPage.help_value)) : 0.00));
        tvwCoinValue.setText("积分: " + (!CUtils.isEmpty(mainPage.integral) ? String.format("%.2f", Float.parseFloat(mainPage.integral)) : 0.00));

        tvwATHAllValue.setText(!CUtils.isEmpty(mainPage.zongath) ? mainPage.zongath : "0.000000");
        tvwMarketValue.setText(!CUtils.isEmpty(mainPage.zongath) ? String.format(Locale.CHINA, "%.2f", Float.valueOf(mainPage.zongath) * Float.valueOf(mainPage.exchange_rate)) + " CNY" : 0.00 + " CNY");

        if (!CUtils.isEmpty(mainPage.pit)) {

            String[] arrStr = mainPage.pit.split("\\|");

            if (CUtils.isEmpty(mainPage.update_time)) {
                return;
            }
            String[] timeStr = mainPage.update_time.split("\\|");
            if (useList(arrStr, "1")) {
                ivCollectingBg.setVisibility(View.INVISIBLE);
                int pos = printArray(arrStr, "1");
                if (pos >= 0) {
                    LogUtils.e(Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    rxJava1(tvwCollecting,btnCollecting,ivCollecting,Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                } else {
                    btnCollecting.setText("解锁");
                    ivCollecting.setBackgroundResource(R.drawable.new_main_jiesuo);
                    ivCollectingBg.setVisibility(View.VISIBLE);
                    Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivCollectingBg);
                }
            } else {
                btnCollecting.setText("解锁");
                ivCollecting.setBackgroundResource(R.drawable.new_main_jiesuo);
                ivCollectingBg.setVisibility(View.VISIBLE);
                Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivCollectingBg);
//                btnCollecting.setBackgroundResource(R.drawable.main_locking_cell);
            }
            if (useList(arrStr, "2")) {
                ivUnlockOneBg.setVisibility(View.INVISIBLE);
                int pos = printArray(arrStr, "2");
                if (pos >= 0) {
                    LogUtils.e(Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    rxJava2(tvwUnlockOne,btnUnlockOne,ivUnlockOne,Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                } else {
                    btnUnlockOne.setText("解锁");
                    ivUnlockOne.setBackgroundResource(R.drawable.new_main_jiesuo);
                    ivUnlockOneBg.setVisibility(View.VISIBLE);
                    Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockOneBg);
                }
            } else {
                btnUnlockOne.setText("解锁");
                ivUnlockOne.setBackgroundResource(R.drawable.new_main_jiesuo);
                ivUnlockOneBg.setVisibility(View.VISIBLE);
                Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockOneBg);
            }
            if (useList(arrStr, "3")) {
                ivUnlockTwoBg.setVisibility(View.INVISIBLE);
                int pos = printArray(arrStr, "3");
                if (pos >= 0) {
                    LogUtils.e(Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    rxJava3(tvwUnlockTwo,btnUnlockTwo,ivUnlockTwo,Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                } else {
                    btnUnlockTwo.setText("解锁");
                    ivUnlockTwo.setBackgroundResource(R.drawable.new_main_jiesuo);
                    ivUnlockTwoBg.setVisibility(View.VISIBLE);
                    Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockTwoBg);
                }
            } else {
                btnUnlockTwo.setText("解锁");
                ivUnlockTwo.setBackgroundResource(R.drawable.new_main_jiesuo);
                ivUnlockTwoBg.setVisibility(View.VISIBLE);
                Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockTwoBg);
            }

            if (useList(arrStr, "9")) {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji6);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            } else if (useList(arrStr, "8")) {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji5);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            } else if (useList(arrStr, "7")) {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji4);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            } else if (useList(arrStr, "6")) {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji3);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            } else if (useList(arrStr, "5")) {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji2);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            } else if (useList(arrStr, "4")) {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji1);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            } else {
                btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji0);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);
            }

            if (useList(arrStr, "15")) {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji6);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            } else if (useList(arrStr, "14")) {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji5);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            } else if (useList(arrStr, "13")) {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji4);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            } else if (useList(arrStr, "12")) {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji3);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            } else if (useList(arrStr, "11")) {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji2);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            } else if (useList(arrStr, "10")) {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji1);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            } else {
                btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji0);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);
            }

            if (useList(arrStr, "21")) {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji6);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            } else if (useList(arrStr, "20")) {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji5);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            } else if (useList(arrStr, "19")) {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji4);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            } else if (useList(arrStr, "18")) {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji3);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            } else if (useList(arrStr, "17")) {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji2);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            } else if (useList(arrStr, "16")) {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji1);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            } else {
                btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji0);
                Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
            }

//            if (useList(arrStr, "4")) {
//                btnLockedOne.setText("矿机");
//                btnLockedOne.setBackgroundResource(R.drawable.main_unlocked);
//            } else {
//                btnLockedOne.setText("矿机");
//                btnLockedOne.setBackgroundResource(R.drawable.main_locking_circle);
//            }
//            if (useList(arrStr, "10")) {
//                btnLockedTwo.setText("矿机");
//                btnLockedTwo.setBackgroundResource(R.drawable.main_unlocked);
//            } else {
//                btnLockedTwo.setText("矿机");
//                btnLockedTwo.setBackgroundResource(R.drawable.main_locking_circle);
//            }
//            if (useList(arrStr, "16")) {
//                btnLockedThree.setText("矿机");
//                btnLockedThree.setBackgroundResource(R.drawable.main_unlocked);
//            } else {
//                btnLockedThree.setText("矿机");
//                btnLockedThree.setBackgroundResource(R.drawable.main_locking_circle);
//            }
        } else {
            btnCollecting.setText("解锁");
            btnUnlockOne.setText("解锁");
            btnUnlockTwo.setText("解锁");

            tvwCollecting.setVisibility(View.INVISIBLE);
            tvwUnlockOne.setVisibility(View.INVISIBLE);
            tvwUnlockTwo.setVisibility(View.INVISIBLE);

            ivCollectingBg.setVisibility(View.VISIBLE);
            ivCollecting.setBackgroundResource(R.drawable.new_main_jiesuo);
            Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivCollectingBg);

            ivUnlockOneBg.setVisibility(View.VISIBLE);
            ivUnlockOne.setBackgroundResource(R.drawable.new_main_jiesuo);
            Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockOneBg);

            ivUnlockTwoBg.setVisibility(View.VISIBLE);
            ivUnlockTwo.setBackgroundResource(R.drawable.new_main_jiesuo);
            Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivUnlockTwoBg);

//            btnCollecting.setBackgroundResource(R.drawable.main_locking_cell);
//            btnUnlockOne.setBackgroundResource(R.drawable.main_locking_cell);
//            btnUnlockTwo.setBackgroundResource(R.drawable.main_locking_cell);
//            btnLockedOne.setBackgroundResource(R.drawable.main_null);
//            btnLockedTwo.setBackgroundResource(R.drawable.main_null);
//            btnLockedThree.setBackgroundResource(R.drawable.main_null);
            btnLockedOne.setBackgroundResource(R.drawable.new_main_kaungji0);
            Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedOne);

            btnLockedTwo.setBackgroundResource(R.drawable.new_main_kaungji0);
            Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedTwo);

            btnLockedThree.setBackgroundResource(R.drawable.new_main_kaungji0);
            Glide.with(this).asGif().load(R.drawable.new_main_kuangji).into(btnLockedThree);
        }
    }


    private void mining(final String pit_number) {

        MiningBody miningBody = new MiningBody();
        miningBody.pit_number = pit_number;

        AthService service = App.get().getAthService();
        service.mining(miningBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<MiningResponse>() {
            @Override
            public void call(MiningResponse miningResponse) {
                LogUtils.e(miningResponse.message);
                App.toast(getActivity(), miningResponse.message);
                if (miningResponse.status == 1) {
                    if (miningResponse.mining != null) {
                        if (!CUtils.isEmpty(miningResponse.mining.ath)) {
//                            tvwCollectValue.setText("宝藏(ATH): " + String.format("%.6f", Float.parseFloat(tvwCollectValue.getText().toString()) + Float.parseFloat(miningResponse.mining.ath)));
                            tvwCollectValue.setText("宝藏(ATH): " + String.format("%.6f", Float.parseFloat(miningResponse.mining.ath)));
                        }

                        if (!CUtils.isEmpty(miningResponse.mining.score)) {
//                            tvwCoinValue.setText("积分: " + String.format("%.2f", Float.parseFloat(tvwCoinValue.getText().toString()) + Float.parseFloat(miningResponse.mining.score)));
                            tvwCoinValue.setText("积分: " + String.format("%.2f", Float.parseFloat(miningResponse.mining.score)));
                        }
                    }
                    if(pit_number.equals("1")){
                        if (subscription1 != null && !subscription1.isUnsubscribed()) {
                            subscription1.unsubscribe();
                        }
                    }else if(pit_number.equals("2")){
                        if (subscription2 != null && !subscription2.isUnsubscribed()) {
                            subscription2.unsubscribe();
                        }
                    }else if(pit_number.equals("3")){
                        if (subscription3 != null && !subscription3.isUnsubscribed()) {
                            subscription3.unsubscribe();
                        }
                    }
                    request("1.0");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    private void pit_unlock(final String pit, String type) {

        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pit", pit);
        map.put("type", type);
        service.pit_unlock(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<PitunlockResponse>() {
            @Override
            public void call(PitunlockResponse pitunlockResponse) {
                LogUtils.e(pitunlockResponse.message);
                App.toast(getActivity(), pitunlockResponse.message);
                if (pitunlockResponse.status == 1) {
                    if (pit.equals("1")) {
                        btnCollecting.setText("休眠中");
                        btnCollecting.setBackgroundResource(R.drawable.main_doing);

                        ivCollecting.setBackgroundResource(R.drawable.new_main_xiumian);
//                        Glide.with(this).asGif().load(R.drawable.new_main_jiantou).into(ivCollecting);
                    }
                    if (pit.equals("2")) {
                        btnUnlockOne.setText("休眠中");
//                        btnUnlockOne.setBackgroundResource(R.drawable.main_doing);
                        ivUnlockOne.setBackgroundResource(R.drawable.new_main_xiumian);
                    }
                    if (pit.equals("3")) {
                        btnUnlockTwo.setText("休眠中");
//                        btnUnlockTwo.setBackgroundResource(R.drawable.main_doing);
                        ivUnlockTwo.setBackgroundResource(R.drawable.new_main_xiumian);
                    }


                    if (pitunlockResponse.pitunlock != null) {
                        if (!CUtils.isEmpty(pitunlockResponse.pitunlock.help_value)) {
                            tvwHelpValue.setText("助力值: " + (!CUtils.isEmpty(pitunlockResponse.pitunlock.help_value) ? String.format("%.2f", Float.parseFloat(pitunlockResponse.pitunlock.help_value)) : 0.00));
                        }

                        if (!CUtils.isEmpty(pitunlockResponse.pitunlock.integral)) {
                            tvwCoinValue.setText("积分: " + (!CUtils.isEmpty(pitunlockResponse.pitunlock.integral) ? String.format("%.2f", Float.parseFloat(pitunlockResponse.pitunlock.integral)) : 0.00));
                        }
                    }
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    private void getMainPageData(String status) {
        MainPageBody mainPageBody = new MainPageBody();
        mainPageBody.status = status;

        AthService service = App.get().getAthService();
        service.index(mainPageBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<MainPageResponse>() {
            @Override
            public void call(MainPageResponse mainPageResponse) {
                if (mainPageResponse != null) {
                    if (mainPageResponse.status == 1) {
                        if (mainPageResponse.mainPage != null) {
                            initDataToView(mainPageResponse.mainPage);
                        } else {
                            App.toast(getActivity(), mainPageResponse.message);
                        }
                    } else {
                        App.toast(getActivity(), mainPageResponse.message);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    request("1.0");
                }
                break;
        }
    }

    String type = "1";

    private void popDialog(final String pit) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("请选择解锁方式");
//        final String[] sex = {"积分解锁", "助力值解锁"};
//
//        //    设置一个单项选择下拉框
//        /**
//         * 第一个参数指定我们要显示的一组下拉单选框的数据集合
//         * 第二个参数代表索引，指定默认哪一个单选框被勾选
//         * 第三个参数给每一个单选项绑定一个监听器
//         */
//        builder.setSingleChoiceItems(sex, 0, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                type = which + 1 + "";
//            }
//        });
//        builder.setPositiveButton("解锁", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                pit_unlock(pit, type);
//                dialog.dismiss();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();


        customDialog.setMLeftBtt("积分解锁").setMRightBt("助力解锁").setMsg("解锁初级矿机").setIsCancelable(true)
                .setChooseListener(new CustomDialog.ChooseListener() {
                    @Override
                    public void onChoose(int which) {
                        if (which == WHICH_LEFT) {
                            pit_unlock(pit, "1");
                        } else if (which == WHICH_RIGHT) {
                            pit_unlock(pit, "2");
                        }
                    }
                }).show();

    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
//                        if (!CUtils.isEmpty(mPit)) {
//                            if (mPit.equals("1")) {
//                                btnCollecting.setText("开采");
//                                btnCollecting.setBackgroundResource(R.drawable.main_doing);
//                            }
//                            if (mPit.equals("2")) {
//                                btnUnlockOne.setText("开采");
//                                btnUnlockOne.setBackgroundResource(R.drawable.main_doing);
//                            }
//                            if (mPit.equals("3")) {
//                                btnUnlockTwo.setText("开采");
//                                btnUnlockTwo.setBackgroundResource(R.drawable.main_doing);
//                            }
//                            if (mPit.equals("4")) {
//                                btnLockedOne.setText("矿机");
//                                btnLockedOne.setBackgroundResource(R.drawable.main_unlocked);
//                            }
//                            if (mPit.equals("5")) {
//                                btnLockedTwo.setText("矿机");
//                                btnLockedTwo.setBackgroundResource(R.drawable.main_unlocked);
//                            }
//                            if (mPit.equals("6")) {
//                                btnLockedThree.setText("矿机");
//                                btnLockedThree.setBackgroundResource(R.drawable.main_unlocked);
//                            }
//                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 支付宝支付业务
     * <p>
     * //     * @param v
     */
    public void payV2(String orderInfoParam) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(getActivity()).setTitle("警告").setMessage("数据有误")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
//                            finish();
                            App.toast(getActivity(), "数据有误");
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
//        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//        String orderParam = orderInfoParam;
//
//        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
        final String orderInfo = orderInfoParam;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCollecting:
                if (App.get().user != null) {
                    if (btnCollecting.getText().toString().equals("解锁")) {
                        popDialog("1");
                    } else if (btnCollecting.getText().toString().equals("领取")) {
                        SoundPoolUtil.getInstance(getActivity()).play();
                        mining("1");
                    }else if(btnCollecting.getText().toString().equals("休眠中")){
                        App.toast(getActivity(),"正在挖矿中，请稍候再试。");
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnUnlockOne:
                if (App.get().user != null) {
                    if (btnUnlockOne.getText().toString().equals("解锁")) {
                        popDialog("2");
                    } else if (btnUnlockOne.getText().toString().equals("领取")) {
                        SoundPoolUtil.getInstance(getActivity()).play();
                        mining("2");
                    }else if(btnUnlockOne.getText().toString().equals("休眠中")){
                        App.toast(getActivity(),"正在挖矿中，请稍候再试。");
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnUnlockTwo:
                if (App.get().user != null) {
                    if (btnUnlockTwo.getText().toString().equals("解锁")) {
                        popDialog("3");
                    } else if (btnUnlockTwo.getText().toString().equals("领取")) {
                        SoundPoolUtil.getInstance(getActivity()).play();
                        mining("3");
                    }else if(btnUnlockTwo.getText().toString().equals("休眠中")){
                        App.toast(getActivity(),"正在挖矿中，请稍候再试。");
                    }
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnLockedOne:
                if (App.get().user != null) {
//                    if (btnLockedOne.getText().toString().equals("锁定")) {
//                        getOrderInfo("4");
//                    } else if (btnLockedOne.getText().toString().equals("开采")) {
//                        mining("4");
//                    }
                    startActivity(new Intent(getActivity(), RechargeAct.class).putExtra("flag", 1));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnLockedTwo:
                if (App.get().user != null) {
//                    if (btnLockedTwo.getText().toString().equals("锁定")) {
//                        getOrderInfo("5");
//                    } else if (btnLockedTwo.getText().toString().equals("开采")) {
//                        mining("5");
//                    }
                    startActivity(new Intent(getActivity(), RechargeAct.class).putExtra("flag", 2));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.btnLockedThree:
                if (App.get().user != null) {
//                    if (btnLockedThree.getText().toString().equals("锁定")) {
//                        getOrderInfo("6");
//                    } else if (btnLockedThree.getText().toString().equals("开采")) {
//                        mining("6");
//                    }
                    startActivity(new Intent(getActivity(), RechargeAct.class).putExtra("flag", 3));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.ivCaifu:
                if (App.get().user != null) {
                    startActivityForResult(new Intent(getActivity(), SpeedValueAct.class), 100);
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;
            case R.id.ivMiji:
                startActivityForResult(new Intent(getActivity(), StoryAct.class), 100);
                break;
            case R.id.ivYqoqing:
                if (App.get().user != null) {
                    startActivity(new Intent(getActivity(), FriendHelpAct.class));
                } else {
                    startActivityForResult(new Intent(getActivity(), LoginAct.class), 100);
                }
                break;

        }

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        request("1.0");
    }
}
