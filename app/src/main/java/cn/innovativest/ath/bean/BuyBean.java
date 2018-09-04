package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class BuyBean {
    @SerializedName("head")
    public BuyHeader buyHeader;
    @SerializedName("list")
    public ArrayList<OrderItem> orderItems;
    public int page;
}
