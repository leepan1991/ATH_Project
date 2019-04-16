package cn.innovativest.ath.response;
import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * Auto-generated: 2019-04-16 21:39:29
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class Data {

    private int wancheng;
    private int weiwancheng;
    private int rmb;
    private int commonweal;
    private List<Hot> hot;
    @SerializedName("crowd_funding_type")
    private List<CrowdFundingType> crowdFundingType;
    @SerializedName("crowd_funding_list")
    private CrowdFundingList crowdFundingList;
    public void setWancheng(int wancheng) {
         this.wancheng = wancheng;
     }
     public int getWancheng() {
         return wancheng;
     }

    public void setWeiwancheng(int weiwancheng) {
         this.weiwancheng = weiwancheng;
     }
     public int getWeiwancheng() {
         return weiwancheng;
     }

    public void setRmb(int rmb) {
         this.rmb = rmb;
     }
     public int getRmb() {
         return rmb;
     }

    public void setCommonweal(int commonweal) {
         this.commonweal = commonweal;
     }
     public int getCommonweal() {
         return commonweal;
     }

    public void setHot(List<Hot> hot) {
         this.hot = hot;
     }
     public List<Hot> getHot() {
         return hot;
     }

    public void setCrowdFundingType(List<CrowdFundingType> crowdFundingType) {
         this.crowdFundingType = crowdFundingType;
     }
     public List<CrowdFundingType> getCrowdFundingType() {
         return crowdFundingType;
     }

    public void setCrowdFundingList(CrowdFundingList crowdFundingList) {
         this.crowdFundingList = crowdFundingList;
     }
     public CrowdFundingList getCrowdFundingList() {
         return crowdFundingList;
     }

}