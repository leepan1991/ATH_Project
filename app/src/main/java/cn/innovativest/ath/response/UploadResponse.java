package cn.innovativest.ath.response;

import com.google.gson.annotations.SerializedName;

import cn.innovativest.ath.bean.UploadBean;

public class UploadResponse extends BaseResponse {

    @SerializedName("data")
    public UploadBean uploadBean;
}
