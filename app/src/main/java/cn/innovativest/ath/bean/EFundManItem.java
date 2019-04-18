package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EFundManItem {

    private int total;
    private int per_page;
    private int current_page;
    private int last_page;
    @SerializedName("data")
    private List<FundManItem> lstFundManItems;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPer_page() {
        return per_page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getLast_page() {
        return last_page;
    }

    public void setLast_page(int last_page) {
        this.last_page = last_page;
    }

    public List<FundManItem> getLstFundManItems() {
        return lstFundManItems;
    }

    public void setLstFundManItems(List<FundManItem> lstFundManItems) {
        this.lstFundManItems = lstFundManItems;
    }
}
