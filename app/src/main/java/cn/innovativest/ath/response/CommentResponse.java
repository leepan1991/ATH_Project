package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.EComment;

public class CommentResponse extends BaseResponse {

    @SerializedName("data")
    public EComment eComment;

}
