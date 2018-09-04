package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.ECoinDetail;

public class CoinDetailResponse extends BaseResponse {

    @SerializedName("data")
    public ECoinDetail eCoinDetail;

}
