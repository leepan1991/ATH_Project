package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.response.CrowdFundingList;

public class EFundDataId {

    @SerializedName("crowd_funding_list")
    private CrowdFundingList crowdFundingList;

    public CrowdFundingList getCrowdFundingList() {
        return crowdFundingList;
    }

    public void setCrowdFundingList(CrowdFundingList crowdFundingList) {
        this.crowdFundingList = crowdFundingList;
    }
}
