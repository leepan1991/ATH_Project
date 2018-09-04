package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.OrderBean;

public class OrderListResponse extends BaseResponse {

    @SerializedName("data")
    public OrderBean orderBean;

}
