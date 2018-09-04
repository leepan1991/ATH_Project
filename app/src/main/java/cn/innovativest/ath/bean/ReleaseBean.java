package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReleaseBean {
    @SerializedName("list")
    public ArrayList<ReleaseItem> releaseItems;
    public int page;
}
