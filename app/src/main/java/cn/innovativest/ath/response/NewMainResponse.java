package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.ENewMain;

public class NewMainResponse extends BaseResponse {

    @SerializedName("data")
    public ENewMain eNewMain;

}
