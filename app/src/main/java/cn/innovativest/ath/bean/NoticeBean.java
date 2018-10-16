package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoticeBean {

    @SerializedName("list")
    public List<Notice> noticeList;
    public int page;

}
