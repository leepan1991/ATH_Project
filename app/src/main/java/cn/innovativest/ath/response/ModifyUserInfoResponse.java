package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.ModifyUserInfo;

public class ModifyUserInfoResponse extends BaseResponse {

    @SerializedName("data")
    public ModifyUserInfo modifyUserInfo;

}
