package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class Speed {
    @SerializedName("head")
    public SpeedHead speedHead;

    @SerializedName("body")
    public SpeedBody speedBody;
}
