//package cn.innovativest.ath.core;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Handler;
//
//import org.json.JSONObject;
//import org.xutils.common.Callback.Cancelable;
//import org.xutils.common.Callback.CommonCallback;
//import org.xutils.common.util.LogUtil;
//import org.xutils.http.HttpMethod;
//import org.xutils.http.RequestParams;
//import org.xutils.x;
//
//import java.io.File;
//import java.util.ArrayList;
//
//import cn.innovativest.ath.App;
//import cn.innovativest.ath.utils.AppUtils;
//import cn.innovativest.ath.utils.CUtils;
//import cn.innovativest.ath.utils.HeaderUtil;
//import cn.innovativest.ath.utils.LoadingUtils;
//import cn.innovativest.ath.utils.LogUtils;
//
//
//public class HttpTask {
//
//    public static final int CODE_SUCCESSFUL = 1;
//
//    public static final int CODE_NETWORK_ERROR = -1;
//    public static final int CODE_NETWORK_NO = -2;
//    public static final int CODE_APP_EXCEPTION = -3;
//
//    // =================================================================================
//    private Context mCtx;
//    private String mUrl;
//    private ArrayList<ResponseListener> mResponseListeners;
//    //	private ProgressDialog mPd;
//    // =================================================================================
//    private boolean isShowFailedMsg = true, isShowPd = true, isShowSuccessMsg = true, isCancelPd = true;
//    private int timeOutMs = 20000, maxNumRetries = 0;
//    private String pdMsg = "请求中...";
//    private HttpMethod method = HttpMethod.POST;
//    private RequestParams params;
//
//
//    private static String getErrorMsg(int code) {
//        String msg = "未知错误";
//        switch (code) {
//            case CODE_NETWORK_ERROR:
//                msg = "网络出错";
//                break;
//            case CODE_NETWORK_NO:
//                msg = "没有网络连接哦~~";
//                break;
//            case CODE_APP_EXCEPTION:
//                msg = "APP解析出错";
//                break;
//        }
//        return msg;
//    }
//
//    // ----------------------------------------------------------
//
//    public HttpTask(Context context, String url) {
//        mCtx = context;
//        mUrl = url;
//        params = new RequestParams(mUrl);
//        mResponseListeners = new ArrayList<ResponseListener>();
//    }
//
//
//    public Cancelable start() {
//        return start(null);
//    }
//
//    public Cancelable start(String ContentType) {
//        if (!AppUtils.isNetworkAvailable(mCtx)) {
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    dealResponse(CODE_NETWORK_NO, null, null);
//                }
//            }, 100);
//
//            return null;
//        }
//
//        if (params == null) {
//            throw new RuntimeException("RequestParams == null");
//        }
//
//        showProgress();
//
//        LogUtil.i(params.getUri());
//
////        if (App.get().user != null) {
////            String token = App.get().user.token;
////            if (!CUtil.isEmpty(token)) {
////                params.addHeader("Token", token);
////                LogUtil.i("token=" + token);
////            }
////        }
//
//        // 注意，不能调用getBodyParams方法，不然会清空bodyParams
//
//        params.setConnectTimeout(timeOutMs);
//        params.setMaxRetryCount(maxNumRetries);
//        params.setCharset("utf-8");
//        params.setCacheSize(0);
//        params.setCacheMaxAge(0);
//
//        params.addHeader("sign", HeaderUtil.getSign(mCtx));
//        params.addHeader("did", HeaderUtil.getDid(mCtx));
//        params.addHeader("time", HeaderUtil.getTime());
//        params.addHeader("token", HeaderUtil.getToken());
//        params.addHeader("app-type", HeaderUtil.app_type);
//        params.addHeader("model", HeaderUtil.getModel());
//        params.addHeader("version", HeaderUtil.version);
//        params.addHeader("version-code", HeaderUtil.version_code);
//
//        LogUtils.e(HeaderUtil.getSign(mCtx) + " 1  " + HeaderUtil.getDid(mCtx) + "  2 " + HeaderUtil.getToken() + "  3 " + HeaderUtil.getModel());
//
//        return x.http().request(method, params, new CommonCallback<String>() {
//
//            @Override
//            public void onCancelled(CancelledException arg0) {
//            }
//
//            @Override
//            public void onError(Throwable arg0, boolean arg1) {
//                LogUtils.e(arg0.getMessage());
//                dealResponse(CODE_NETWORK_ERROR, null, null);
//            }
//
//            @Override
//            public void onFinished() {
//            }
//
//            @Override
//            public void onSuccess(String arg0) {
//                LogUtils.e(arg0);
//                parseResponseData(arg0);
//
//            }
//        });
//    }
//
//    private void parseResponseData(String response) {
//        JSONObject o;
//        try {
//            o = new JSONObject(response);
//            int code = o.getInt("status");
//            Object data = o.opt("data");
//            String msg = o.optString("message", code == CODE_SUCCESSFUL ? "成功" : "");
//            if (code != CODE_SUCCESSFUL) {
//                if (CUtils.isEmpty(msg)) {
//                    msg = getErrorMsg(code);
//                }
//
//            }
//            dealResponse(code, data, msg);
//        } catch (Exception e) {
//            e.printStackTrace();
//            dealResponse(CODE_APP_EXCEPTION, null, null);
//        }
//    }
//
//    private void dealResponse(int code, Object data, String msg) {
//        hideProgress();
//        if (code != CODE_SUCCESSFUL) {
//            if (CUtils.isEmpty(msg)) {
//                msg = getErrorMsg(code);
//            }
//            showFailedMsg(msg);
////            if (code == 9999) {
////                App.get().user = null;
////                PrefsManager.get().save("user", "");
////                PrefsManager.get().save("lastLoginPhone", "");
////                PrefsManager.get().save("address", "");
////                AppUtils.startActivity(mCtx, LoginAct.class,
////                        Intent.FLAG_ACTIVITY_CLEAR_TASK
////                                | Intent.FLAG_ACTIVITY_NEW_TASK);
////                if (mCtx instanceof Activity) {
////                    Activity act = (Activity) mCtx;// 确保ctx是activity
////                    act.finish();
////                }
////            }
//            if (code != CODE_SUCCESSFUL && isShowFailedMsg) {
//                return;
//            }
//            for (ResponseListener l : mResponseListeners) {
//                l.onFailed(code, msg);
//            }
//        } else {
////            showSuccessMsg(msg);
//            for (ResponseListener l : mResponseListeners) {
//                try {
//                    l.onSuccess(data, msg);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    showFailedMsg(getErrorMsg(CODE_APP_EXCEPTION));
//                }
//            }
//        }
//
//    }
//
//    private void showFailedMsg(String msg) {
//        if (isShowFailedMsg) {
//            if (msg != null) {
//                App.toast(mCtx, "" + msg.toString());
//            }
//        }
//    }
//
//    private void showSuccessMsg(String msg) {
//        if (isShowSuccessMsg) {
//            if (msg != null) {
//                App.toast(mCtx, "" + msg.toString());
//            }
//        }
//    }
//
//    private void showProgress() {
//        if (isShowPd) {
//            try {
//                if (mCtx != null) {
//                    Activity act = (Activity) mCtx;// 确保ctx是activity
//                    LoadingUtils.getInstance().dialogShow(mCtx, pdMsg, isCancelPd);
//                }
//            } catch (Exception e) {
//            }
//        }
//
//    }
//
//    private void hideProgress() {
//        LoadingUtils.getInstance().dialogDismiss();
//    }
//
//    // ======================================================================================
//
//    public HttpTask addResponseListener(ResponseListener listener) {
//        mResponseListeners.add(listener);
//        return this;
//    }
//
//    public HttpTask setShowFailedMsg(boolean is) {
//        this.isShowFailedMsg = is;
//        return this;
//    }
//
//    public HttpTask setShowSuccessMsg(boolean is) {
//        this.isShowSuccessMsg = is;
//        return this;
//    }
//
//    public HttpTask setShowPd(boolean isShowPd) {
//        this.isShowPd = isShowPd;
//        return this;
//    }
//
//    public HttpTask setCancelPd(boolean isCancelPd) {
//        this.isCancelPd = isCancelPd;
//        return this;
//    }
//
//    public HttpTask setMethod(HttpMethod method) {
//        this.method = method;
//        return this;
//    }
//
//    public HttpTask setTimeOutMs(int timeOutMs) {
//        this.timeOutMs = timeOutMs;
//        return this;
//    }
//
//    public HttpTask setMaxNumRetries(int maxNumRetries) {
//        this.maxNumRetries = maxNumRetries;
//        return this;
//    }
//
//    public HttpTask setPdMsg(String pdMsg) {
//        this.pdMsg = pdMsg;
//        return this;
//    }
//
//    public HttpTask addQueryStringParameter(String key, String value) {
//        params.addQueryStringParameter(key, value);
//        return this;
//    }
//
//    public HttpTask addBodyParameter(String key, String value) {
//        params.addBodyParameter(key, value);
//        return this;
//    }
//
//    public HttpTask addBodyParameter(String key, File value) {
//        params.addBodyParameter(key, value);
//        return this;
//    }
//
//    public interface ResponseListener {
//        void onSuccess(Object data, String msg) throws Exception;
//
//        void onFailed(int code, String msg);
//    }
//}
