package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Mining;

public class MiningResponse extends BaseResponse {

    @SerializedName("data")
    public Mining mining;

}
