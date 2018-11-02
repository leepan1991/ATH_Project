package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ShareItem {
    @SerializedName("imgs")
    public ArrayList<ImgsItem> lstImgs;

    @SerializedName("text")
    public ArrayList<ImgsItem> lstTexts;
}
