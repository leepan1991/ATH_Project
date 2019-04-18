package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.EFundPartItem;

public class FundPartResponse extends BaseResponse {

    @SerializedName("data")
    public EFundPartItem eFundPartItem;
}
