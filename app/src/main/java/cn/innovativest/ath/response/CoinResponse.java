package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.ECoinItem;

public class CoinResponse extends BaseResponse {

    @SerializedName("data")
    public ECoinItem eCoinItem;

}
