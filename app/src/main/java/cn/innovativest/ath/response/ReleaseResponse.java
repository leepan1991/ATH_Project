package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.ReleaseBean;

public class ReleaseResponse extends BaseResponse {

    @SerializedName("data")
    public ReleaseBean releaseBean;

}
