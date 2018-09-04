package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.TradeOrderDetail;

public class OrderDetailResponse extends BaseResponse {

    @SerializedName("data")
    public TradeOrderDetail tradeOrderDetail;

}
