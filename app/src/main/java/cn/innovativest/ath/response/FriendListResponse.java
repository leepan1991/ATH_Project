package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.FriendHelpBean;

public class FriendListResponse extends BaseResponse {

    @SerializedName("data")
    public FriendHelpBean friendHelpBean;

}
