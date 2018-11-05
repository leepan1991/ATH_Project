package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.BuyBean;
import cn.innovativest.ath.bean.EProperty;

public class PropertyResponse extends BaseResponse {

    @SerializedName("data")
    public EProperty eProperty;

}
