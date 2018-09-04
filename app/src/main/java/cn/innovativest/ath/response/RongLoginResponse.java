package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

public class RongLoginResponse {

    @SerializedName("code")
    public int code;
    @SerializedName("userId")
    public String userId;
    @SerializedName("token")
    public String token;

}
