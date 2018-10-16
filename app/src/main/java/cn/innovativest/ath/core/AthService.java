package cn.innovativest.ath.core;

import java.util.HashMap;
import java.util.Map;

import cn.innovativest.ath.entities.AddressBody;
import cn.innovativest.ath.entities.BindingBody;
import cn.innovativest.ath.entities.CodeBody;
import cn.innovativest.ath.entities.CoinBody;
import cn.innovativest.ath.entities.LoginBody;
import cn.innovativest.ath.entities.MainPageBody;
import cn.innovativest.ath.entities.MiningBody;
import cn.innovativest.ath.entities.ModifyLoginBody;
import cn.innovativest.ath.entities.ModifyLoginYzmBody;
import cn.innovativest.ath.entities.ModifyPhoneBody;
import cn.innovativest.ath.entities.ModifySinglePasswordBody;
import cn.innovativest.ath.entities.OrderStatusBody;
import cn.innovativest.ath.entities.RegisterBody;
import cn.innovativest.ath.entities.SinglePasswordBody;
import cn.innovativest.ath.response.AddressResponse;
import cn.innovativest.ath.response.ApplyResponse;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.BuyListResponse;
import cn.innovativest.ath.response.BuySellResponse;
import cn.innovativest.ath.response.CoinDetailResponse;
import cn.innovativest.ath.response.CoinResponse;
import cn.innovativest.ath.response.CommonResponse;
import cn.innovativest.ath.response.FriendListResponse;
import cn.innovativest.ath.response.GiftResponse;
import cn.innovativest.ath.response.LoginResponse;
import cn.innovativest.ath.response.MainPageResponse;
import cn.innovativest.ath.response.ManCoinResponse;
import cn.innovativest.ath.response.MiningResponse;
import cn.innovativest.ath.response.ModifyUserInfoResponse;
import cn.innovativest.ath.response.NoticeListResponse;
import cn.innovativest.ath.response.OrderDetailResponse;
import cn.innovativest.ath.response.OrderListResponse;
import cn.innovativest.ath.response.PitunlockResponse;
import cn.innovativest.ath.response.ReleaseResponse;
import cn.innovativest.ath.response.SpeedMineResponse;
import cn.innovativest.ath.response.SpeedResponse;
import cn.innovativest.ath.response.StartImgResponse;
import cn.innovativest.ath.response.TradeResponse;
import cn.innovativest.ath.response.UpgradeResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

public interface AthService {

    @POST("login")
    Observable<LoginResponse> login(@Body LoginBody body);

    @POST("index")
    Observable<MainPageResponse> index(@Body MainPageBody body);

    @GET("register_yzm")
    Observable<BaseResponse> register_yzm(@Query("phone") String phone, @Query("statu") String statu);

    @POST("register")
    Observable<LoginResponse> register(@Body RegisterBody body);

    @GET("user")
    Observable<UserInfoResponse> userInfo();

    @POST("trade")
    Observable<TradeResponse> trade(@Body MainPageBody body);

    @GET("xishuAndStatic")
    Observable<CommonResponse> commonInfo(@Query("id") int id);

    @GET("goods_list")
    Observable<CoinResponse> goods_list(@Query("page") int page);

    @POST("site_add")
    Observable<BaseResponse> site_add(@Body AddressBody body);

    @POST("site_edit")
    Observable<BaseResponse> site_edit(@Body AddressBody body);

    @GET("site")
    Observable<AddressResponse> site();

    @GET("site_del")
    Observable<BaseResponse> site_del(@Query("id") String id);

    @GET("good_info")
    Observable<CoinDetailResponse> good_info(@Query("id") String id);

    @POST("submit_order")
    Observable<BaseResponse> submit_order(@Body CoinBody body);

    @GET("good_checkout_success")
    Observable<ManCoinResponse> good_checkout_success();

    @GET("IdAuthentication")
    Observable<BaseResponse> IdAuthentication(@Query("name") String name, @Query("id") String id);

    @POST("pay_password")
    Observable<BaseResponse> pay_password(@Body SinglePasswordBody body);

//    @POST("edit_pay_password_yzm")
//    Observable<BaseResponse> edit_pay_password_yzm(@Body CodeBody body);

    @POST("edit_pay_password")
    Observable<BaseResponse> edit_pay_password(@Body ModifySinglePasswordBody body);

    @POST("edit_phone_password_yzm")
    Observable<BaseResponse> edit_phone_password_yzm(@Body CodeBody body);

    @POST("edit_phone")
    Observable<BaseResponse> edit_phone(@Body ModifyPhoneBody body);

    @POST("forget_password_yzm")
    Observable<BaseResponse> forget_password_yzm(@Body ModifyLoginYzmBody body);

    @POST("forget_password")
    Observable<BaseResponse> forget_password(@Body ModifyLoginBody body);

    @GET("sign_in")
    Observable<BaseResponse> sign_in();

    @GET("pit_open")
    Observable<BaseResponse> pit_open();

    @POST("mining")
    Observable<MiningResponse> mining(@Body MiningBody body);

    @POST("pit_unlock")
    Observable<PitunlockResponse> pit_unlock(@Body HashMap<String, String> map);

    @GET("buysell")
    Observable<BuySellResponse> buysell(@Query("order_number") String order_number, @Query("type") String type, @Query("number") String number);

    @GET("order")
    Observable<OrderDetailResponse> order(@Query("order_number") String order_number);

    @GET("releaseSellOrder")
    Observable<BaseResponse> releaseSellOrder(@Query("danjia") String danjia, @Query("quota_min") String quota_min, @Query("quota_max") String quota_max, @Query("number") String number, @Query("type") String type);

    @POST("binding")
    Observable<BaseResponse> binding(@Body BindingBody body);

    @POST("binding")
    Observable<BaseResponse> bindingAlipay(@Body MultipartBody body);

    @GET("order_list")
    Observable<OrderListResponse> order_list(@QueryMap Map<String, String> param);

    @POST("alter_info")
    Observable<ModifyUserInfoResponse> alter_info(@Body MultipartBody body);

    @POST("senior_IdAuthentication")
    Observable<BaseResponse> senior_IdAuthentication(@Body MultipartBody body);

    @POST("transaction")
    Observable<BaseResponse> transaction(@Body OrderStatusBody body);

    @GET("person_list")
    Observable<FriendListResponse> person_list();

    @POST("loginOut")
    Observable<BaseResponse> loginOut();

    @GET("init")
    Observable<UpgradeResponse> init();

    @GET("invite")
    Observable<GiftResponse> invite(@Query("type") String type);

    @POST("draw")
    Observable<BaseResponse> draw(@Body HashMap<String, String> map);

    @GET("caifujiasu")
    Observable<SpeedResponse> caifujiasu();

    @GET("sel_complain")
    Observable<ApplyResponse> sel_complain(@Query("order_number") String order_number, @Query("state") String state);

    @POST("complain")
    Observable<BaseResponse> complain(@Body MultipartBody body);

    @POST("payOrder")
    Observable<UserInfoResponse> payOrder(@Body HashMap<String, String> map);

    @GET("release_list")
    Observable<ReleaseResponse> release_list(@Query("type") String type, @Query("page") int page);

    @POST("release_edit")
    Observable<BaseResponse> release_edit(@Body HashMap<String, String> map);

    @GET("child_list")
    Observable<BuyListResponse> child_list(@Query("order_number") String order_number, @Query("page") int page);

    @GET("android")
    Observable<BaseResponse> androidInfo();

    @GET("pit")
    Observable<SpeedMineResponse> pit();

    @GET("qidong")
    Observable<StartImgResponse> qidong();

    @GET("get_user_info")
    Observable<UserInfoResponse> get_user_info(@Query("user_id") String user_id);

    @GET("noticeList")
    Observable<NoticeListResponse> noticeList(@Query("page") int page);
}
