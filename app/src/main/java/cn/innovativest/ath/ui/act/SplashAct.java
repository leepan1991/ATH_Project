package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.MD5Utils;
import cn.innovativest.ath.utils.PrefsManager;

/**
 * 闪图
 *
 * @author leepan
 */
public class SplashAct extends BaseAct {

    private final String mPageName = "SplashAct";

    ImageView imageView;
    private long delay = 2000l;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {

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
            imageView = new ImageView(this);
            imageView.setBackgroundDrawable(mBitmap);
            setContentView(imageView);
            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    toHome();
                }
            }, delay);
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
        startActivity(new Intent(SplashAct.this, NewMainAct.class));
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