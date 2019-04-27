package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class DetailFund {
    public String id;
    public String target_rmb;
    public String reach_rmb;
    public String bai_fen_bi;
    public String status;
    public String error;
    public long stop_time;
    public long ctime;
    public int persons;

    @SerializedName("get_crowd_funding_text")
    public DetailFundText detailFundText;

    @SerializedName("user")
    public DetailFundUser detailFundUser;
}
