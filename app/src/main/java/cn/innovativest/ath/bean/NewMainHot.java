package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class NewMainHot {
    public String id;
    public String persons;
    public String target_rmb;
    public String reach_rmb;
    public String status;
    public int bai_fen_bi;
    public String stop_time;
    @SerializedName("get_crowd_funding_text")
    public NewMainGetFundingText newMainGetFundingText;

}
