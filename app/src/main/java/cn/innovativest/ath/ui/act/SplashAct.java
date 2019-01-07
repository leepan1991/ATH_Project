package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.R;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.MD5Utils;
import cn.innovativest.ath.utils.PrefsManager;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 闪图
 *
 * @author leepan
 */
public class SplashAct extends BaseAct {

    private final String mPageName = "SplashAct";

    @BindView(R.id.ivImg)
    ImageView ivImg;

    @BindView(R.id.lltJump)
    LinearLayout lltJump;

    @BindView(R.id.tvwTime)
    TextView tvwTime;

    //    ImageView imageView;
//    private long delay = 2000l;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lltJump:
                toHome();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_act);
        ButterKnife.bind(this);
        lltJump.setOnClickListener(this);
        String urlSplash = PrefsManager.get().getString("lastSplashUrl");
        if (TextUtils.isEmpty(urlSplash) || !AppUtils.existsSplash(urlSplash)) {
            toHome();
        } else {
            Drawable mBitmap = BitmapDrawable
                    .createFromPath(AppConfig.ATH_CACHE_PATH
                            + MD5Utils.MD5(urlSplash));
            if (mBitmap == null) {
                toHome();
                return;
            }
//            imageView = new ImageView(this);
//            imageView.setBackgroundDrawable(mBitmap);
//            setContentView(imageView);
            ivImg.setBackground(mBitmap);

            int count_time = PrefsManager.get().getInt("time");

            Observable.interval(0, 1, TimeUnit.SECONDS) //0延迟  每隔1秒触发
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                    .take(count_time) //设置循环次数
                    .map(aLong -> count_time - aLong)
                    .subscribe(new Observer<Long>() {
                        @Override
                        public void onCompleted() {//循环结束调用此方法
                            toHome();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Long aLong) {//每隔一秒执行
                            tvwTime.setText(aLong + "");
                        }
                    });


//            new Timer().schedule(new TimerTask() {
//
//                @Override
//                public void run() {
//                    toHome();
//                }
//            }, delay);
//            imageView = new ImageView(this);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            setContentView(imageView);
//            GlideApp.with(this).load("file://"
//                    + AppConfig.ATH_CACHE_PATH + MD5Utils.MD5(urlSplash)).listener(new RequestListener<Drawable>() {
//                @Override
//                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                    toHome();
//                    return true;
//                }
//
//                @Override
//                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    imageView.setBackground(resource);
//                    new Timer().schedule(new TimerTask() {
//
//                        @Override
//                        public void run() {
//                            toHome();
//                        }
//                    }, delay);
//                    return true;
//                }
//            });
        }
    }

    private void toHome() {
//        startActivity(new Intent(SplashAct.this, MainAct.class));
        if (!PrefsManager.get().getBoolean("athIntroduce")) {
            AppUtils.startActivity(SplashAct.this, AboutAthAct.class);
        } else {
            startActivity(new Intent(SplashAct.this, NewMainAct.class));
        }
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // do nothing
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}