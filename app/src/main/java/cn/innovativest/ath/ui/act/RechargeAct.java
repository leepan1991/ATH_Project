package cn.innovativest.ath.ui.act;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.HashMap;
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
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PayResult;
import cn.innovativest.ath.utils.SoundPoolUtil;
import cn.innovativest.ath.widget.CustomDialog;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RechargeAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.tvwBtn1)
    TextView tvwBtn1;

    @BindView(R.id.tvwBtn2)
    TextView tvwBtn2;

    @BindView(R.id.tvwBtn3)
    TextView tvwBtn3;

    @BindView(R.id.tvwBtn4)
    TextView tvwBtn4;

    @BindView(R.id.tvwBtn5)
    TextView tvwBtn5;

    @BindView(R.id.tvwBtn6)
    TextView tvwBtn6;

    @BindView(R.id.tv1)
    TextView tv1;

    @BindView(R.id.tv2)
    TextView tv2;

    @BindView(R.id.tv3)
    TextView tv3;

    @BindView(R.id.tv4)
    TextView tv4;

    @BindView(R.id.tv5)
    TextView tv5;

    @BindView(R.id.tv6)
    TextView tv6;

    @BindView(R.id.tvx1)
    TextView tvx1;

    @BindView(R.id.tvx2)
    TextView tvx2;

    @BindView(R.id.tvx3)
    TextView tvx3;

    @BindView(R.id.tvx4)
    TextView tvx4;

    @BindView(R.id.tvx5)
    TextView tvx5;

    @BindView(R.id.tvx6)
    TextView tvx6;

    @BindView(R.id.tvTime1)
    TextView tvTime1;

    @BindView(R.id.tvTime2)
    TextView tvTime2;

    @BindView(R.id.tvTime3)
    TextView tvTime3;

    @BindView(R.id.tvTime4)
    TextView tvTime4;

    @BindView(R.id.tvTime5)
    TextView tvTime5;

    @BindView(R.id.tvTime6)
    TextView tvTime6;

    private Subscription subscription1;
    private Subscription subscription2;
    private Subscription subscription3;
    private Subscription subscription4;
    private Subscription subscription5;
    private Subscription subscription6;

    private CustomDialog customDialog;

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

    private int flag = 0;

    private String mPit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charge_unlock_act);
        ButterKnife.bind(this);
        flag = getIntent().getIntExtra("flag", 0);
        if (flag == 0) {
            finish();
        }
        initView();
        getMainPageData("1.0");
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("充值解锁");
        btnBack.setOnClickListener(this);

        tvwBtn1.setOnClickListener(this);
        tvwBtn2.setOnClickListener(this);
        tvwBtn3.setOnClickListener(this);
        tvwBtn4.setOnClickListener(this);
        tvwBtn5.setOnClickListener(this);
        tvwBtn6.setOnClickListener(this);

        customDialog = new CustomDialog(mCtx);

        if (flag == 1) {
            tv1.setText("4号高级矿机");
            tv2.setText("5号高级矿机");
            tv3.setText("6号高级矿机");
            tv4.setText("7号高级矿机");
            tv5.setText("8号高级矿机");
            tv6.setText("9号高级矿机");

            tvx1.setText("1X");
            tvx2.setText("1.2X");
            tvx3.setText("3X");
            tvx4.setText("3X");
            tvx5.setText("3X");
            tvx6.setText("3X");
        } else if (flag == 2) {
            tv1.setText("10号高级矿机");
            tv2.setText("11号高级矿机");
            tv3.setText("12号高级矿机");
            tv4.setText("13号高级矿机");
            tv5.setText("14号高级矿机");
            tv6.setText("15号高级矿机");

            tvx1.setText("3X");
            tvx2.setText("3X");
            tvx3.setText("3X");
            tvx4.setText("3X");
            tvx5.setText("3X");
            tvx6.setText("3X");
        } else if (flag == 3) {
            tv1.setText("16号高级矿机");
            tv2.setText("17号高级矿机");
            tv3.setText("18号高级矿机");
            tv4.setText("19号高级矿机");
            tv5.setText("20号高级矿机");
            tv6.setText("21号高级矿机");

            tvx1.setText("3X");
            tvx2.setText("3X");
            tvx3.setText("3X");
            tvx4.setText("10X");
            tvx5.setText("10X");
            tvx6.setText("10X");
        }

        tvTime1.setVisibility(View.INVISIBLE);
        tvTime2.setVisibility(View.INVISIBLE);
        tvTime3.setVisibility(View.INVISIBLE);
        tvTime4.setVisibility(View.INVISIBLE);
        tvTime5.setVisibility(View.INVISIBLE);
        tvTime6.setVisibility(View.INVISIBLE);
    }

    private void getMainPageData(String status) {
        MainPageBody mainPageBody = new MainPageBody();
        mainPageBody.status = status;

        AthService service = App.get().getAthService();
        service.index(mainPageBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<MainPageResponse>() {
            @Override
            public void call(MainPageResponse mainPageResponse) {
                if (mainPageResponse != null) {
                    if (mainPageResponse.mainPage != null) {
                        initDataToView(mainPageResponse.mainPage);
                    } else {
                        App.toast(RechargeAct.this, mainPageResponse.message);
                    }
                } else {
                    App.toast(RechargeAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(RechargeAct.this, "数据获取失败");
            }
        });

    }

    /**
     * RxJava 方式实现
     */
    private void rxJava1(final TextView tv, long allTime) {

        if (subscription1 != null && !subscription1.isUnsubscribed()) {
            subscription1.unsubscribe();
        }

        if (allTime > 172800) {
            allTime = 172800;
        }
        final long count = allTime;
        subscription1 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (172800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
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
                        tv.setText("已达到饱和");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong >= 28800) {
                            tvwBtn1.setText("获取");
                        } else {
                            tvwBtn1.setText("开采中");
                        }
                    }
                });
    }

    private void rxJava2(final TextView tv, long allTime) {

        if (subscription2 != null && !subscription2.isUnsubscribed()) {
            subscription2.unsubscribe();
        }

        if (allTime > 172800) {
            allTime = 172800;
        }
        final long count = allTime;
        subscription2 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (172800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
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
                        tv.setText("已达到饱和");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong >= 28800) {
                            tvwBtn2.setText("获取");
                        } else {
                            tvwBtn2.setText("开采中");
                        }
                    }
                });
    }

    private void rxJava3(final TextView tv, long allTime) {

        if (subscription3 != null && !subscription3.isUnsubscribed()) {
            subscription3.unsubscribe();
        }

        if (allTime > 172800) {
            allTime = 172800;
        }
        final long count = allTime;
        subscription3 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (172800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
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
                        tv.setText("已达到饱和");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong >= 28800) {
                            tvwBtn3.setText("获取");
                        } else {
                            tvwBtn3.setText("开采中");
                        }
                    }
                });
    }

    private void rxJava4(final TextView tv, long allTime) {

        if (subscription4 != null && !subscription4.isUnsubscribed()) {
            subscription4.unsubscribe();
        }

        if (allTime > 172800) {
            allTime = 172800;
        }
        final long count = allTime;
        subscription4 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (172800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
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
                        tv.setText("已达到饱和");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong >= 28800) {
                            tvwBtn4.setText("获取");
                        } else {
                            tvwBtn4.setText("开采中");
                        }
                    }
                });
    }

    private void rxJava5(final TextView tv, long allTime) {

        if (subscription5 != null && !subscription5.isUnsubscribed()) {
            subscription5.unsubscribe();
        }

        if (allTime > 172800) {
            allTime = 172800;
        }
        final long count = allTime;
        subscription5 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (172800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
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
                        tv.setText("已达到饱和");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong >= 28800) {
                            tvwBtn5.setText("获取");
                        } else {
                            tvwBtn5.setText("开采中");
                        }
                    }
                });
    }

    private void rxJava6(final TextView tv, long allTime) {

        if (subscription6 != null && !subscription6.isUnsubscribed()) {
            subscription6.unsubscribe();
        }

        if (allTime > 172800) {
            allTime = 172800;
        }
        final long count = allTime;
        subscription6 = Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (172800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
                        return count + aLong;
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
                        tv.setText("已达到饱和");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        tv.setText(secToTime(aLong));
                        if (aLong >= 28800) {
                            tvwBtn6.setText("获取");
                        } else {
                            tvwBtn6.setText("开采中");
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
                if (hour > 48)
                    return "可以进行开采";
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
        if (!CUtils.isEmpty(mainPage.pit)) {

            String[] arrStr = mainPage.pit.split("\\|");

            if (CUtils.isEmpty(mainPage.update_time)) {
                return;
            }

            String[] timeStr = mainPage.update_time.split("\\|");

            if (flag == 1) {
                if (useList(arrStr, "4")) {
                    tvwBtn1.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn1.setText("开采中");
                    tvTime1.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "4");
                    if (pos >= 0) {
                        LogUtils.e(Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                        rxJava1(tvTime1, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime1.setVisibility(View.INVISIBLE);
                    }

                } else {
                    tvwBtn1.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn1.setText("立即解锁");
                    tvTime1.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "5")) {
                    tvwBtn2.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn2.setText("开采中");
                    tvTime2.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "5");
                    if (pos >= 0) {
                        rxJava2(tvTime2, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn2.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn2.setText("立即解锁");
                    tvTime2.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "6")) {
                    tvwBtn3.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn3.setText("开采中");
                    tvTime3.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "6");
                    if (pos >= 0) {
                        rxJava3(tvTime3, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime3.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn3.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn3.setText("立即解锁");
                    tvTime3.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "7")) {
                    tvwBtn4.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn4.setText("开采中");
                    tvTime4.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "7");
                    if (pos >= 0) {
                        rxJava4(tvTime4, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime4.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn4.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn4.setText("立即解锁");
                    tvTime4.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "8")) {
                    tvwBtn5.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn5.setText("开采中");
                    tvTime5.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "8");
                    if (pos >= 0) {
                        rxJava5(tvTime5, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime5.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn5.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn5.setText("立即解锁");
                    tvTime5.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "9")) {
                    tvwBtn6.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn6.setText("开采中");
                    tvTime6.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "9");
                    if (pos >= 0) {
                        rxJava6(tvTime6, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime6.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn6.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn6.setText("立即解锁");
                    tvTime6.setVisibility(View.INVISIBLE);
                }

            } else if (flag == 2) {
                if (useList(arrStr, "10")) {
                    tvwBtn1.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn1.setText("开采中");
                    tvTime1.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "10");
                    if (pos >= 0) {
                        rxJava1(tvTime1, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime1.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn1.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn1.setText("立即解锁");
                    tvTime1.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "11")) {
                    tvwBtn2.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn2.setText("开采中");
                    tvTime2.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "11");
                    if (pos >= 0) {
                        rxJava2(tvTime2, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn2.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn2.setText("立即解锁");
                    tvTime2.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "12")) {
                    tvwBtn3.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn3.setText("开采中");
                    tvTime3.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "12");
                    if (pos >= 0) {
                        rxJava3(tvTime3, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime3.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn3.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn3.setText("立即解锁");
                    tvTime3.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "13")) {
                    tvwBtn4.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn4.setText("开采中");
                    tvTime4.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "13");
                    if (pos >= 0) {
                        rxJava4(tvTime4, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime4.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn4.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn4.setText("立即解锁");
                    tvTime4.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "14")) {
                    tvwBtn5.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn5.setText("开采中");
                    tvTime5.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "14");
                    if (pos >= 0) {
                        rxJava5(tvTime5, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime5.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn5.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn5.setText("立即解锁");
                    tvTime5.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "15")) {
                    tvwBtn6.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn6.setText("开采中");
                    tvTime6.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "15");
                    if (pos >= 0) {
                        rxJava6(tvTime6, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime6.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn6.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn6.setText("立即解锁");
                    tvTime6.setVisibility(View.INVISIBLE);
                }
            } else if (flag == 3) {
                if (useList(arrStr, "16")) {
                    tvwBtn1.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn1.setText("开采中");
                    tvTime1.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "16");
                    if (pos >= 0) {
                        rxJava1(tvTime1, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime1.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn1.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn1.setText("立即解锁");
                    tvTime1.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "17")) {
                    tvwBtn2.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn2.setText("开采中");
                    tvTime2.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "17");
                    if (pos >= 0) {
                        rxJava2(tvTime2, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime2.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn2.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn2.setText("立即解锁");
                    tvTime2.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "18")) {
                    tvwBtn3.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn3.setText("开采中");
                    tvTime3.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "18");
                    if (pos >= 0) {
                        rxJava3(tvTime3, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime3.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn3.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn3.setText("立即解锁");
                    tvTime3.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "19")) {
                    tvwBtn4.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn4.setText("开采中");
                    tvTime4.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "19");
                    if (pos >= 0) {
                        rxJava4(tvTime4, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime4.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn4.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn4.setText("立即解锁");
                    tvTime4.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "20")) {
                    tvwBtn5.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn5.setText("开采中");
                    tvTime5.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "20");
                    if (pos >= 0) {
                        rxJava5(tvTime5, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime5.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn5.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn5.setText("立即解锁");
                    tvTime5.setVisibility(View.INVISIBLE);
                }

                if (useList(arrStr, "21")) {
                    tvwBtn6.setBackgroundResource(R.drawable.charge_mining);
                    tvwBtn6.setText("开采中");
                    tvTime6.setVisibility(View.VISIBLE);
                    int pos = printArray(arrStr, "21");
                    if (pos >= 0) {
                        rxJava6(tvTime6, Long.valueOf(mainPage.time - Long.valueOf(timeStr[pos])));
                    } else {
                        tvTime6.setVisibility(View.INVISIBLE);
                    }
                } else {
                    tvwBtn6.setBackgroundResource(R.drawable.charge_unlock);
                    tvwBtn6.setText("立即解锁");
                    tvTime6.setVisibility(View.INVISIBLE);
                }
            }

        } else {
            tvwBtn1.setBackgroundResource(R.drawable.charge_unlock);
            tvwBtn1.setText("立即解锁");

            tvwBtn2.setBackgroundResource(R.drawable.charge_unlock);
            tvwBtn2.setText("立即解锁");

            tvwBtn3.setBackgroundResource(R.drawable.charge_unlock);
            tvwBtn3.setText("立即解锁");

            tvwBtn4.setBackgroundResource(R.drawable.charge_unlock);
            tvwBtn4.setText("立即解锁");

            tvwBtn5.setBackgroundResource(R.drawable.charge_unlock);
            tvwBtn5.setText("立即解锁");

            tvwBtn6.setBackgroundResource(R.drawable.charge_unlock);
            tvwBtn6.setText("立即解锁");
        }
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
                        Toast.makeText(RechargeAct.this, "支付成功", Toast.LENGTH_SHORT).show();

                        if (!CUtils.isEmpty(mPit)) {
                            if (flag == 1) {
                                if (mPit.equals("4")) {
                                    tvwBtn1.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn1.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("5")) {
                                    tvwBtn2.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn2.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("6")) {
                                    tvwBtn3.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn3.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("7")) {
                                    tvwBtn4.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn4.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("8")) {
                                    tvwBtn5.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn5.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("9")) {
                                    tvwBtn6.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn6.setText("开采中");
                                    getMainPageData("1.0");
                                }

                            } else if (flag == 2) {
                                if (mPit.equals("10")) {
                                    tvwBtn1.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn1.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("11")) {
                                    tvwBtn2.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn2.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("12")) {
                                    tvwBtn3.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn3.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("13")) {
                                    tvwBtn4.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn4.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("14")) {
                                    tvwBtn5.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn5.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("15")) {
                                    tvwBtn6.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn6.setText("开采中");
                                    getMainPageData("1.0");
                                }
                            } else if (flag == 3) {
                                if (mPit.equals("16")) {
                                    tvwBtn1.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn1.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("17")) {
                                    tvwBtn2.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn2.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("18")) {
                                    tvwBtn3.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn3.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("19")) {
                                    tvwBtn4.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn4.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("20")) {
                                    tvwBtn5.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn5.setText("开采中");
                                    getMainPageData("1.0");
                                }

                                if (mPit.equals("21")) {
                                    tvwBtn6.setBackgroundResource(R.drawable.charge_mining);
                                    tvwBtn6.setText("开采中");
                                    getMainPageData("1.0");
                                }
                            }
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(RechargeAct.this, "支付失败", Toast.LENGTH_SHORT).show();
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
            new AlertDialog.Builder(this).setTitle("警告").setMessage("数据有误")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
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
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
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
                PayTask alipay = new PayTask(RechargeAct.this);
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

    private void getOrderInfo(String pit) {
        mPit = pit;
        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pit", pit);
        map.put("type", "3");
        service.payOrder(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                LogUtils.e(userInfoResponse.message);
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        payV2(AESUtils.decryptData(userInfoResponse.data));
                    } else {
                        App.toast(RechargeAct.this, "支付失败");
                    }
                } else {
                    App.toast(RechargeAct.this, userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    private void getOrderATHInfo(String pit) {
        mPit = pit;
        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pit", pit);
        map.put("type", "ATH");
        service.payOrder(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                LogUtils.e(userInfoResponse.message);
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        App.toast(RechargeAct.this, userInfoResponse.data);
                    } else {
                        App.toast(RechargeAct.this, "支付失败");
                    }
                } else {
                    App.toast(RechargeAct.this, userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    private void recharge(final String pit) {
        customDialog.setMLeftBtt("ATH购买").setMRightBt("支付宝购买").setMsg("充值解锁高级矿机").setIsCancelable(true)
                .setChooseListener(new CustomDialog.ChooseListener() {
                    @Override
                    public void onChoose(int which) {
                        if (which == WHICH_RIGHT) {
                            getOrderInfo(pit);
                        } else if (which == WHICH_LEFT) {
                            getOrderATHInfo(pit);
                        }
                    }
                }).show();
    }

    private void mining(final String pit_number) {

        MiningBody miningBody = new MiningBody();
        miningBody.pit_number = pit_number;

        AthService service = App.get().getAthService();
        service.mining(miningBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<MiningResponse>() {
            @Override
            public void call(MiningResponse miningResponse) {
                LogUtils.e(miningResponse.message);
                App.toast(RechargeAct.this, miningResponse.message);
                if (miningResponse.status == 1) {
                    if (flag == 1) {
                        if (pit_number.equals("4")) {
                            if (subscription1 != null && !subscription1.isUnsubscribed()) {
                                subscription1.unsubscribe();
                            }
                        } else if (pit_number.equals("5")) {
                            if (subscription2 != null && !subscription2.isUnsubscribed()) {
                                subscription2.unsubscribe();
                            }
                        } else if (pit_number.equals("6")) {
                            if (subscription3 != null && !subscription3.isUnsubscribed()) {
                                subscription3.unsubscribe();
                            }
                        } else if (pit_number.equals("7")) {
                            if (subscription4 != null && !subscription4.isUnsubscribed()) {
                                subscription4.unsubscribe();
                            }
                        } else if (pit_number.equals("8")) {
                            if (subscription5 != null && !subscription5.isUnsubscribed()) {
                                subscription5.unsubscribe();
                            }
                        } else if (pit_number.equals("9")) {
                            if (subscription6 != null && !subscription6.isUnsubscribed()) {
                                subscription6.unsubscribe();
                            }
                        }
                    } else if (flag == 2) {
                        if (pit_number.equals("10")) {
                            if (subscription1 != null && !subscription1.isUnsubscribed()) {
                                subscription1.unsubscribe();
                            }
                        } else if (pit_number.equals("11")) {
                            if (subscription2 != null && !subscription2.isUnsubscribed()) {
                                subscription2.unsubscribe();
                            }
                        } else if (pit_number.equals("12")) {
                            if (subscription3 != null && !subscription3.isUnsubscribed()) {
                                subscription3.unsubscribe();
                            }
                        } else if (pit_number.equals("13")) {
                            if (subscription4 != null && !subscription4.isUnsubscribed()) {
                                subscription4.unsubscribe();
                            }
                        } else if (pit_number.equals("14")) {
                            if (subscription5 != null && !subscription5.isUnsubscribed()) {
                                subscription5.unsubscribe();
                            }
                        } else if (pit_number.equals("15")) {
                            if (subscription6 != null && !subscription6.isUnsubscribed()) {
                                subscription6.unsubscribe();
                            }
                        }
                    } else if (flag == 3) {
                        if (pit_number.equals("16")) {
                            if (subscription1 != null && !subscription1.isUnsubscribed()) {
                                subscription1.unsubscribe();
                            }
                        } else if (pit_number.equals("17")) {
                            if (subscription2 != null && !subscription2.isUnsubscribed()) {
                                subscription2.unsubscribe();
                            }
                        } else if (pit_number.equals("18")) {
                            if (subscription3 != null && !subscription3.isUnsubscribed()) {
                                subscription3.unsubscribe();
                            }
                        } else if (pit_number.equals("19")) {
                            if (subscription4 != null && !subscription4.isUnsubscribed()) {
                                subscription4.unsubscribe();
                            }
                        } else if (pit_number.equals("20")) {
                            if (subscription5 != null && !subscription5.isUnsubscribed()) {
                                subscription5.unsubscribe();
                            }
                        } else if (pit_number.equals("21")) {
                            if (subscription6 != null && !subscription6.isUnsubscribed()) {
                                subscription6.unsubscribe();
                            }
                        }
                    }
                    getMainPageData("1.0");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
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
        MobclickAgent.onPageStart("RechargeAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RechargeAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.tvwBtn1:
                if (flag == 1) {
                    if (tvwBtn1.getText().toString().equals("立即解锁")) {
                        recharge("4");
                    } else if (tvwBtn1.getText().toString().equals("开采中") || tvwBtn1.getText().toString().equals("获取")) {
                        if (tvwBtn1.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("4");
                    }

                } else if (flag == 2) {
                    if (tvwBtn1.getText().toString().equals("立即解锁")) {
                        recharge("10");
                    } else if (tvwBtn1.getText().toString().equals("开采中") || tvwBtn1.getText().toString().equals("获取")) {
                        if (tvwBtn1.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("10");
                    }

                } else if (flag == 3) {
                    if (tvwBtn1.getText().toString().equals("立即解锁")) {
                        recharge("16");
                    } else if (tvwBtn1.getText().toString().equals("开采中") || tvwBtn1.getText().toString().equals("获取")) {
                        if (tvwBtn1.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("16");
                    }

                }
                break;
            case R.id.tvwBtn2:
                if (flag == 1) {

                    if (tvwBtn2.getText().toString().equals("立即解锁")) {
                        recharge("5");
                    } else if (tvwBtn2.getText().toString().equals("开采中") || tvwBtn2.getText().toString().equals("获取")) {
                        if (tvwBtn2.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("5");
                    }
                } else if (flag == 2) {

                    if (tvwBtn2.getText().toString().equals("立即解锁")) {
                        recharge("11");
                    } else if (tvwBtn2.getText().toString().equals("开采中") || tvwBtn2.getText().toString().equals("获取")) {
                        if (tvwBtn2.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("11");
                    }
                } else if (flag == 3) {

                    if (tvwBtn2.getText().toString().equals("立即解锁")) {
                        recharge("17");
                    } else if (tvwBtn2.getText().toString().equals("开采中") || tvwBtn2.getText().toString().equals("获取")) {
                        if (tvwBtn2.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("17");
                    }
                }
                break;
            case R.id.tvwBtn3:
                if (flag == 1) {

                    if (tvwBtn3.getText().toString().equals("立即解锁")) {
                        recharge("6");
                    } else if (tvwBtn3.getText().toString().equals("开采中") || tvwBtn3.getText().toString().equals("获取")) {
                        if (tvwBtn3.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("6");
                    }
                } else if (flag == 2) {

                    if (tvwBtn3.getText().toString().equals("立即解锁")) {
                        recharge("12");
                    } else if (tvwBtn3.getText().toString().equals("开采中") || tvwBtn3.getText().toString().equals("获取")) {
                        if (tvwBtn3.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("12");
                    }
                } else if (flag == 3) {

                    if (tvwBtn3.getText().toString().equals("立即解锁")) {
                        recharge("18");
                    } else if (tvwBtn3.getText().toString().equals("开采中") || tvwBtn3.getText().toString().equals("获取")) {
                        if (tvwBtn3.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("18");
                    }
                }
                break;
            case R.id.tvwBtn4:
                if (flag == 1) {

                    if (tvwBtn4.getText().toString().equals("立即解锁")) {
                        recharge("7");
                    } else if (tvwBtn4.getText().toString().equals("开采中") || tvwBtn4.getText().toString().equals("获取")) {
                        if (tvwBtn4.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("7");
                    }
                } else if (flag == 2) {
                    if (tvwBtn4.getText().toString().equals("立即解锁")) {
                        recharge("13");
                    } else if (tvwBtn4.getText().toString().equals("开采中") || tvwBtn4.getText().toString().equals("获取")) {
                        if (tvwBtn4.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("13");
                    }
                } else if (flag == 3) {
                    if (tvwBtn4.getText().toString().equals("立即解锁")) {
                        recharge("19");
                    } else if (tvwBtn4.getText().toString().equals("开采中") || tvwBtn4.getText().toString().equals("获取")) {
                        if (tvwBtn4.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("19");
                    }
                }
                break;
            case R.id.tvwBtn5:
                if (flag == 1) {
                    if (tvwBtn5.getText().toString().equals("立即解锁")) {
                        recharge("8");
                    } else if (tvwBtn5.getText().toString().equals("开采中") || tvwBtn5.getText().toString().equals("获取")) {
                        if (tvwBtn5.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("8");
                    }
                } else if (flag == 2) {
                    if (tvwBtn5.getText().toString().equals("立即解锁")) {
                        recharge("14");
                    } else if (tvwBtn5.getText().toString().equals("开采中") || tvwBtn5.getText().toString().equals("获取")) {
                        if (tvwBtn5.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("14");
                    }
                } else if (flag == 3) {
                    if (tvwBtn5.getText().toString().equals("立即解锁")) {
                        recharge("20");
                    } else if (tvwBtn5.getText().toString().equals("开采中") || tvwBtn5.getText().toString().equals("获取")) {
                        if (tvwBtn5.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("20");
                    }
                }
                break;
            case R.id.tvwBtn6:
                if (flag == 1) {
                    if (tvwBtn6.getText().toString().equals("立即解锁")) {
                        recharge("9");
                    } else if (tvwBtn6.getText().toString().equals("开采中") || tvwBtn6.getText().toString().equals("获取")) {
                        if (tvwBtn6.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("9");
                    }
                } else if (flag == 2) {
                    if (tvwBtn6.getText().toString().equals("立即解锁")) {
                        recharge("15");
                    } else if (tvwBtn6.getText().toString().equals("开采中") || tvwBtn6.getText().toString().equals("获取")) {
                        if (tvwBtn6.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("15");
                    }
                } else if (flag == 3) {
                    if (tvwBtn6.getText().toString().equals("立即解锁")) {
                        recharge("21");
                    } else if (tvwBtn6.getText().toString().equals("开采中") || tvwBtn6.getText().toString().equals("获取")) {
                        if (tvwBtn6.getText().toString().equals("获取")) {
                            SoundPoolUtil.getInstance(this).play();
                        }
                        mining("21");
                    }
                }
                break;


        }
    }
}
