package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TradeBean {
    @SerializedName("list")
    public ArrayList<TradeItem> tradeItems;
    public int page;
}
