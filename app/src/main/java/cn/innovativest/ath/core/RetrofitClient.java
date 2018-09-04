package cn.innovativest.ath.core;

import android.content.Context;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import cn.innovativest.ath.BuildConfig;
import cn.innovativest.ath.utils.HeaderUtil;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static String baseUrl;

    public static String random = "";

    public static AthService getService(final Context mCtx) {

        OkHttpClient.Builder client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request orgRequest = chain.request();
                final Request newRequest = orgRequest.newBuilder()
                        .addHeader("sign", HeaderUtil.getSign(mCtx))
                        .addHeader("did", HeaderUtil.getDid(mCtx))
                        .addHeader("random", random)
                        .addHeader("token", HeaderUtil.getToken())
                        .addHeader("app-type", HeaderUtil.app_type)
                        .addHeader("model", HeaderUtil.getModel())
                        .addHeader("version", BuildConfig.VERSION_CODE + "")
                        .addHeader("version-code", BuildConfig.VERSION_NAME)
                        .build();

                Response response = chain.proceed(newRequest);
                return response;
            }
        })
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);

        if (!BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(interceptor);
            baseUrl = BuildConfig.HTTP_TEST;
        } else {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(interceptor);
            baseUrl = BuildConfig.HTTP_PROD;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client.build())
                .build();

        return retrofit.create(AthService.class);
    }

    public static RongService getRongService(final Context mCtx) throws NoSuchAlgorithmException {

        final String rom = String.valueOf(HeaderUtil.generateRandomByScope(1000000, 1000000));
        final String time = System.currentTimeMillis() + "";
        String secret = "eb7soAuIvUU";
        final String sign = HeaderUtil.sha1(secret + rom + time);

        OkHttpClient.Builder client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request orgRequest = chain.request();
                final Request newRequest = orgRequest.newBuilder()
                        .addHeader("App-Key", "z3v5yqkbz1n40")
                        .addHeader("Nonce", rom)
                        .addHeader("Timestamp", time)
                        .addHeader("Signature", sign)
                        .build();

                Response response = chain.proceed(newRequest);
                return response;
            }
        })
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS);

        if (!BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(interceptor);
        } else {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client.addInterceptor(interceptor);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.cn.ronghub.com/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client.build())
                .build();

        return retrofit.create(RongService.class);
    }

    public static final void main(String[] args) {

//        RetrofitClient client = new RetrofitClient();
//        client.baseUrl = "https://api.internationalsos.com";
//        client.authorization = "57260aee5f0391000100000f33a2e402e36d4580619865c1704db6f6";
//
//        try {
//            AlertsResponse response = client.getService().getAlerts().execute().body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
