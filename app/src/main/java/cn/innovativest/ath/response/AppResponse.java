package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import cn.innovativest.ath.bean.Address;
import cn.innovativest.ath.bean.AppItem;

public class AppResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<AppItem> lstApps;

}
