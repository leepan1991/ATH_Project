package cn.innovativest.ath.bean;

/**
 * Created by leepan on 26/03/2018.
 */

public class CoinTradeItem {


    private String order_id;
    private String img;
    private String title;
    private String danwei;
    private String description;
    private long ctime;
    private String name;
    private String dak;
    private String site;
    private String need_integral;
    private int status;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDanwei() {
        return danwei;
    }

    public void setDanwei(String danwei) {
        this.danwei = danwei;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDak() {
        return dak;
    }

    public void setDak(String dak) {
        this.dak = dak;
    }

    public String getSite() {
        return site;
    }

    public String getNeed_integral() {
        return need_integral;
    }

    public void setNeed_integral(String need_integral) {
        this.need_integral = need_integral;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
