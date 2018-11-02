package cn.innovativest.ath.common;

import java.io.File;

import cn.innovativest.ath.BuildConfig;

/**
 * 程序的一些配置信息
 *
 * @author leepan
 */
public class AppConfig {

    public static final String ATH_APP_URL = !BuildConfig.DEBUG ? "http://ath.wuhao.link" : "http://ath.pub";

    /**
     * APP INFO
     */
    public static final String ATH_APP_INFO = "ATH_APP_INFO";

    /**
     * 不再提醒版本升级
     */
    public static final String ATH_NO_REMIND_UPDATE_VERSION_KEY = "ATH_NO_REMIND_UPDATE_VERSION_KEY";

    /**
     * APP PUSH USER INFO
     */
    public static final String ATH_APP_PUSH_KEY = "ATH_APP_PUSH_KEY";

    /**
     * 页容量
     */
    public static final int PAGE_SIZE = 12;

    /**
     * APP VERSION UPDATE INFO
     */
    public static final String ATH_VERSION_UPDATE_KEY = "ATH_VERSION_UPDATE_KEY";
    /**
     * 设备信息
     */
    public final static String DEFAULT_DID = "000000000000000";
    public final static int NET_TYPE_UNKNOWN = -1;
    public final static String NET_TYPE_UNKNOWN_NAME = "无网络";
    public final static String ATH_DEVICE_INFO_KEY = "ATH_DEVICE_INFO_KEY";
    /**
     * 当SDCard 只剩下50M时，不可用
     */
    public final static long SDCARD_BLOCK_SIZE = 50 * 1024 * 1024;
    /**
     * 缓存等主目录
     */
    public final static String ATH_HOME_PATH = File.separator + "sdcard"
            + File.separator + "ath" + File.separator;
    // 缓存
    public final static String ATH_CACHE_PATH = AppConfig.ATH_HOME_PATH
            + "cache" + File.separator;
    // 下载文件
    public final static String ATH_DOWNLOAD_PATH = AppConfig.ATH_HOME_PATH
            + "download" + File.separator;
    // 配置
    public final static String ATH_CONFIG_PATH = AppConfig.ATH_HOME_PATH
            + "config" + File.separator;
    //图片
    public final static String ATH_CACHE_PIC_PATH = AppConfig.ATH_HOME_PATH
            + "image" + File.separator;

    /**
     * CACHE过期时间 24小时=86400000毫秒
     */
    public static final long CACHE_EXPIRED_MILLISECONDS = 30 * 60 * 1000;// 24
    // *
    // 60
    // *
    // 60
    // *
    // 1000;

    // 保存在SP中的是否在运营商网络下观看的key
    public static final String ATH_SP_PLAY_IN_3G = "ATH_SP_PLAY_IN_3G";
    // 是否接收推送
    public static final String ATH_SP_RECEIVE_PUSH = "ATH_SP_RECEIVE_PUSH";

    // 保存帐号密码
    public static final String ATH_SP_USER_NAME = "ATH_SP_USER_NAME";
    public static final String ATH_SP_USER_PWD = "ATH_SP_USER_PWD";
    public static final String ATH_SP_USER_JSON = "ATH_SP_USER_JSON";
    public static final String ATH_SP_USER_ADDRESS_JSON = "ATH_SP_USER_ADDRESS_JSON";
    public static final String ATH_SP_USER_CARD_JSON = "ATH_SP_USER_CARD_JSON";
    public static final String ATH_SP_USER_ADDRESS = "ATH_SP_USER_ADDRESS";
    public static final String ATH_SP_USER_ZIPCODE = "ATH_SP_USER_ZIPCODE";
    public static final String ATH_SP_USER_REMEMBER = "ATH_SP_USER_REMEMBER";

    public static final String ATH_SP_APP_ID = "ATH_SP_APP_ID";

}
