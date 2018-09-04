package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.User;

public class LoginResponse extends BaseResponse {

    @SerializedName("data")
    public User user;

}
