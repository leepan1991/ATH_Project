package cn.innovativest.ath.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import cn.innovativest.ath.R;

public class SoundPoolUtil {
    private static SoundPoolUtil soundPoolUtil;
    private SoundPool soundPool;
    private boolean isLoaded = false;

    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        soundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 0);
        //加载音频文件
        soundPool.load(context, R.raw.diamond, 1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                isLoaded = true;
            }
        });
    }

    public void play() {

        if (isLoaded) {
            //播放音频
            soundPool.play(1, 1, 1, 0, 0, 1);
        }
    }
}
