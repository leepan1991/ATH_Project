package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.TradeBean;

public class TradeResponse extends BaseResponse {

    @SerializedName("data")
    public TradeBean tradeBean;

}
