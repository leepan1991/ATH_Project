package cn.innovativest.ath.response;
import com.google.gson.annotations.SerializedName;
/**
 * Auto-generated: 2019-04-16 21:39:29
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class GetCrowdFundingText {

    private String title;
    private String rmb;
    @SerializedName("img_link")
    private String imgLink;
    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setImgLink(String imgLink) {
         this.imgLink = imgLink;
     }
     public String getImgLink() {
         return imgLink;
     }

    public String getRmb() {
        return rmb;
    }

    public void setRmb(String rmb) {
        this.rmb = rmb;
    }
}