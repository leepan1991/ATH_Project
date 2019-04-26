package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EComment {
    public int total;
    public int per_page;
    public int current_page;
    public int last_page;
    @SerializedName("data")
    private List<Comment> lstComments;

    public List<Comment> getLstComments() {
        return lstComments;
    }

    public void setLstComments(List<Comment> lstComments) {
        this.lstComments = lstComments;
    }
}
