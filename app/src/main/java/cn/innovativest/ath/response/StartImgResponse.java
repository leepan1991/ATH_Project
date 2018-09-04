package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import cn.innovativest.ath.bean.StartImg;

public class StartImgResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<StartImg> lstStartImg;

}
