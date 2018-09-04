package cn.innovativest.ath.bean;

import java.io.Serializable;

/**
 * Created by leepan on 26/03/2018.
 */

public class ReleaseItem implements Serializable {


    private String status;
    private String id;
    private String quota;
    private String number;
    private String cny;
    private String order_number;
    private String name;
    private String phone;
    private String successful_order_number;
    private String head_img_link;
    private String alipay;
    private String wechat;
    private String bank;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCny() {
        return cny;
    }

    public void setCny(String cny) {
        this.cny = cny;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSuccessful_order_number() {
        return successful_order_number;
    }

    public void setSuccessful_order_number(String successful_order_number) {
        this.successful_order_number = successful_order_number;
    }

    public String getHead_img_link() {
        return head_img_link;
    }

    public void setHead_img_link(String head_img_link) {
        this.head_img_link = head_img_link;
    }
    
    public String getAlipay() {
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }
}
