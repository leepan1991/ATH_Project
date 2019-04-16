package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

/**
 * Auto-generated: 2019-04-16 21:39:29
 *
 * @author www.jsons.cn
 * @website http://www.jsons.cn/json2java/
 */
public class Hot {

    private int id;
    @SerializedName("get_crowd_funding_text")
    private GetCrowdFundingText getCrowdFundingText;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setGetCrowdFundingText(GetCrowdFundingText getCrowdFundingText) {
        this.getCrowdFundingText = getCrowdFundingText;
    }

    public GetCrowdFundingText getGetCrowdFundingText() {
        return getCrowdFundingText;
    }

}