package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Auto-generated: 2019-04-16 21:39:29
 *
 * @author www.jsons.cn
 * @website http://www.jsons.cn/json2java/
 */
public class CrowdFundingList {

    private int total;
    @SerializedName("per_page")
    private int perPage;
    @SerializedName("current_page")
    private int currentPage;
    @SerializedName("last_page")
    private int lastPage;
    private List<FundItem> data;

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setData(List<FundItem> data) {
        this.data = data;
    }

    public List<FundItem> getData() {
        return data;
    }

}