package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Apply;

public class ApplyResponse extends BaseResponse {

    @SerializedName("data")
    public Apply apply;

}
