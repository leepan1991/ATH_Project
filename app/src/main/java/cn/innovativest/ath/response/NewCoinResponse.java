package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Binding;
import cn.innovativest.ath.bean.ECoinItem;
import cn.innovativest.ath.bean.ENewCoinItem;

public class NewCoinResponse extends BaseResponse {

    @SerializedName("data")
    public ENewCoinItem eNewCoinItem;
}
