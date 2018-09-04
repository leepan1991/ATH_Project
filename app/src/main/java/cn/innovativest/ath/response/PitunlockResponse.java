package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Pitunlock;

public class PitunlockResponse extends BaseResponse {

    @SerializedName("data")
    public Pitunlock pitunlock;

}
