package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Speed;

public class SpeedResponse extends BaseResponse {

    @SerializedName("data")
    public Speed speed;

}
