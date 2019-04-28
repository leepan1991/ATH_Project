package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.EFundDataId;


public class FundIdResponse extends BaseResponse {

    @SerializedName("data")
    public EFundDataId eFundDataId;

}