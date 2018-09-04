package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.Binding;

public class BindingResponse extends BaseResponse {

    @SerializedName("data")
    public Binding binding;

}
