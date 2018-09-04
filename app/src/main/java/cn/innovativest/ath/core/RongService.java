package cn.innovativest.ath.core;

import java.util.Map;

import cn.innovativest.ath.entities.RongLoginBody;
import cn.innovativest.ath.response.RongLoginResponse;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

public interface RongService {

    @Multipart
    @POST("user/getToken.json")
    Observable<RongLoginResponse> getToken(@PartMap Map<String, RequestBody> requestBodyMap);

    @POST("user/refresh.json")
    Observable<RongLoginResponse> refresh(@Body RongLoginBody body);
}
