package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderBean {

    @SerializedName("list")
    public ArrayList<OrderItem> orderItems;
    public int page;
}
