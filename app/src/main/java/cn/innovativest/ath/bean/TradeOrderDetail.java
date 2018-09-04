package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class TradeOrderDetail {

    @SerializedName("head")
    public OrderDetailHead orderDetailHead;
    @SerializedName("body")
    public OrderDetailBody orderDetailBody;
    @SerializedName("foot")
    public OrderDetailFoot orderDetailFoot;

}
