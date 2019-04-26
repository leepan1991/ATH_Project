package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class Comment {

    public String id;
    public String text;
    public long time;
    @SerializedName("user")
    public CommentUser commentUser;
}
