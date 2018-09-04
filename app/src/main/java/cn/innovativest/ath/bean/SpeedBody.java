package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class SpeedBody {

    @SerializedName("primary")
    public SpeedPrimary speedPrimary;

    @SerializedName("senior")
    public SpeedSenior speedSenior;
}
