package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import cn.innovativest.ath.bean.Gift;

public class GiftResponse extends BaseResponse {

    @SerializedName("data")
    public List<Gift> lstGifts;

}
