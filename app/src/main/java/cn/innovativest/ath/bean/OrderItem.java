package cn.innovativest.ath.bean;

/**
 * Created by leepan on 26/03/2018.
 */

public class OrderItem {

    private String name;
    private String order_number;
    private String ath_number;
    private String ath_price;
    private String state;
    private long creatime;
    private String sell_userid;
    private String buy_userid;
    private int type;
    private String head_img_link;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getAth_number() {
        return ath_number;
    }

    public void setAth_number(String ath_number) {
        this.ath_number = ath_number;
    }

    public String getAth_price() {
        return ath_price;
    }

    public void setAth_price(String ath_price) {
        this.ath_price = ath_price;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getCreatime() {
        return creatime;
    }

    public void setCreatime(long creatime) {
        this.creatime = creatime;
    }

    public String getSell_userid() {
        return sell_userid;
    }

    public void setSell_userid(String sell_userid) {
        this.sell_userid = sell_userid;
    }

    public String getBuy_userid() {
        return buy_userid;
    }

    public void setBuy_userid(String buy_userid) {
        this.buy_userid = buy_userid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHead_img_link() {
        return head_img_link;
    }

    public void setHead_img_link(String head_img_link) {
        this.head_img_link = head_img_link;
    }
}
