package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.MainPage;

public class MainPageResponse extends BaseResponse {

    @SerializedName("data")
    public MainPage mainPage;

}
