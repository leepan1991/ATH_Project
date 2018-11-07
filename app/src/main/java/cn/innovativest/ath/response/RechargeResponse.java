package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Binding;
import cn.innovativest.ath.bean.Recharge;

public class RechargeResponse extends BaseResponse {

    @SerializedName("data")
    public Recharge recharge;

}
