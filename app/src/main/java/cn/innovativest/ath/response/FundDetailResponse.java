package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.DetailFund;

public class FundDetailResponse extends BaseResponse {

    @SerializedName("data")
    public DetailFund detailFund;

}
