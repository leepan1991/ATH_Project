package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

public class FundItem {
    private int id;
    @SerializedName("target_rmb")
    private String targetRmb;
    @SerializedName("reach_rmb")
    private String reachRmb;
    private String status;
    @SerializedName("bai_fen_bi")
    private int baiFenBi;
    @SerializedName("get_crowd_funding_text")
    private GetCrowdFundingText getCrowdFundingText;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTargetRmb(String targetRmb) {
        this.targetRmb = targetRmb;
    }

    public String getTargetRmb() {
        return targetRmb;
    }

    public void setReachRmb(String reachRmb) {
        this.reachRmb = reachRmb;
    }

    public String getReachRmb() {
        return reachRmb;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setBaiFenBi(int baiFenBi) {
        this.baiFenBi = baiFenBi;
    }

    public int getBaiFenBi() {
        return baiFenBi;
    }

    public void setGetCrowdFundingText(GetCrowdFundingText getCrowdFundingText) {
        this.getCrowdFundingText = getCrowdFundingText;
    }

    public GetCrowdFundingText getGetCrowdFundingText() {
        return getCrowdFundingText;
    }
}
