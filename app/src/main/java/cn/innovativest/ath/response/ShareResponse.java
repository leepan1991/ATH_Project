package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.ECoinItem;
import cn.innovativest.ath.bean.ShareItem;

public class ShareResponse extends BaseResponse {

    @SerializedName("data")
    public ShareItem shareItem;

}
