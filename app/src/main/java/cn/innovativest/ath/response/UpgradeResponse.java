package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Upgrade;

public class UpgradeResponse extends BaseResponse {

    @SerializedName("data")
    public Upgrade upgrade;

}
