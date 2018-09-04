package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import cn.innovativest.ath.bean.SpeedMine;

public class SpeedMineResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<SpeedMine> speedMines;

}
