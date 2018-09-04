package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.EManCoin;

public class ManCoinResponse extends BaseResponse {

    @SerializedName("data")
    public EManCoin eManCoin;

}
