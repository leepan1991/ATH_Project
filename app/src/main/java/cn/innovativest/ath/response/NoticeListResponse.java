package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.FriendHelpBean;
import cn.innovativest.ath.bean.NoticeBean;

public class NoticeListResponse extends BaseResponse {

    @SerializedName("data")
    public NoticeBean noticeBean;

}
