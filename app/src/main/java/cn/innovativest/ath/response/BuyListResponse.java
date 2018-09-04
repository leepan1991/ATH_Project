package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.BuyBean;

public class BuyListResponse extends BaseResponse {

    @SerializedName("data")
    public BuyBean buyBean;

}
