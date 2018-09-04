package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendHelpBean {

    @SerializedName("list")
    public List<FriendHelpItem> lstFriendHelpItems;
    public String link;
    public String code;
    public String barcodeurl;

}
