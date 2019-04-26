package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ENewMain {
    @SerializedName("nav")
    private List<Nav> lstNavs;

    @SerializedName("crowd_funding")
    public CrowdFunding crowdFunding;

    public List<Nav> getLstNavs() {
        return lstNavs;
    }

    public void setLstNavs(List<Nav> lstNavs) {
        this.lstNavs = lstNavs;
    }
}
