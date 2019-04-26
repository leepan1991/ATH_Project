package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CrowdFunding {

    public int total;
    public int per_page;
    public int current_page;
    public int last_page;
    @SerializedName("data")
    private List<NewMainHot> lstNewMainHots;

    public List<NewMainHot> getLstNewMainHots() {
        return lstNewMainHots;
    }

    public void setLstNewMainHots(List<NewMainHot> lstNewMainHots) {
        this.lstNewMainHots = lstNewMainHots;
    }
}
