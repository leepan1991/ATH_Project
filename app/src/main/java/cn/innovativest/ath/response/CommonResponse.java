package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.CommonItem;

public class CommonResponse extends BaseResponse {

//    @SerializedName("data")
//    public ArrayList<CommonItem> commonItems;

    @SerializedName("data")
    public CommonItem commonItem;

}
