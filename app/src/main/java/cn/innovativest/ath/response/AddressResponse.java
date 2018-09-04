package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import cn.innovativest.ath.bean.Address;

public class AddressResponse extends BaseResponse {

    @SerializedName("data")
    public ArrayList<Address> lstAddress;

}
