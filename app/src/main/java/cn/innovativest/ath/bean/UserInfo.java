package cn.innovativest.ath.bean;

import com.google.gson.annotations.SerializedName;

public class UserInfo {

    public int id;
    public String name;
    public String phone;
    public String head_img_link;
    public String id_number;
    public String real_name;
    public int sex;
    public String code;
    public String parent_code;
    public String bank;
    public String wechat;
    public String alipay;
    public String id_img;
    public int is_sign_in;//0已签到，1没有签到
    public int state;
    public String check_pay_password;
    @SerializedName("values")
    public UserMainPage mainPage;
}
