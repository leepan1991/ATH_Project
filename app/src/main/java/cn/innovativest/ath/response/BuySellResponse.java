package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.BuySell;

public class BuySellResponse extends BaseResponse {

    @SerializedName("data")
    public BuySell buySell;

}
