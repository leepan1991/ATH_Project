package cn.innovativest.ath.utils;

import android.content.Context;

import cn.innovativest.ath.R;
import cn.innovativest.ath.widget.LoadingDialog;


public class LoadingUtils {

    private static LoadingUtils loadingUtils;

    public static LoadingUtils getInstance() {
        if (loadingUtils == null) {
            loadingUtils = new LoadingUtils();
        }
        return loadingUtils;
    }

    private LoadingDialog loadingDia;

    /**
     * @param context 用于取消网络请求
     */
    public void dialogShow(Context context, String msg, boolean canCencel) {
        loadingDia = new LoadingDialog(context, R.layout.view_tips_loading, msg);
        loadingDia.setCanceledOnTouchOutside(false);
        loadingDia.setCancelable(canCencel);
        loadingDia.show();
    }

    /**
     * @param context 用于取消网络请求
     */
    public void dialogShow(Context context, String msg) {
        loadingDia = new LoadingDialog(context, R.layout.view_tips_loading, msg);
        loadingDia.setCancelable(false);
        loadingDia.show();
    }

    public void dialogDismiss() {
        if (loadingDia != null && loadingDia.isShowing()) {
            loadingDia.dismiss();
            loadingDia = null;
        }
    }
}
