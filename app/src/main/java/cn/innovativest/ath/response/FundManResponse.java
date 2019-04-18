package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.EFundManItem;

public class FundManResponse extends BaseResponse {

    @SerializedName("data")
    public EFundManItem eFundManItem;
}
