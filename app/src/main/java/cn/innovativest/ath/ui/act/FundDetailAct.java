package cn.innovativest.ath.ui.act;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alipay.sdk.app.PayTask;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.DetailFund;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.FundDetailResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.AppUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PayResult;
import cn.innovativest.ath.widget.CustomDialog;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class FundDetailAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.ivFundImg1)
    ImageView ivFundImg1;

    @BindView(R.id.ivPlayIcon)
    ImageView ivPlayIcon;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.tvATH)
    TextView tvATH;

    @BindView(R.id.tvHelp)
    TextView tvHelp;

    @BindView(R.id.tvScore)
    TextView tvScore;

    @BindView(R.id.btnPubName)
    Button btnPubName;

    @BindView(R.id.tvwPubName)
    TextView tvwPubName;

    @BindView(R.id.tvwStatus)
    TextView tvwStatus;

    @BindView(R.id.tvwDesc)
    TextView tvwDesc;

    @BindView(R.id.tvRegisterNum)
    TextView tvRegisterNum;

    @BindView(R.id.tvFundNum)
    TextView tvFundNum;

    @BindView(R.id.tvShouyi)
    TextView tvShouyi;

//    @BindView(R.id.ivImg1)
//    ImageView ivImg1;
//
//    @BindView(R.id.ivImg2)
//    ImageView ivImg2;

    @BindView(R.id.lltImgs)
    LinearLayout lltImgs;

    @BindView(R.id.btnShare)
    Button btnShare;

    @BindView(R.id.btnStartZc)
    Button btnStartZc;

    private String id;

    DetailFund detailFund;

    private CustomDialog customDialog;

    String rmb;

    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2018060560268737";

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /**
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE = "MIIEogIBAAKCAQEAnL1jiVr2zW2CoWswu6XNu2YHLpxg/fgKfPI3kPhLaFOjd1zYBH2TZMJytTZTO2WwBcIzpDPn7IKp8XAbX7mBkWLuDa8zlKr8RUCuFwtyAfLHU3DDWcsY5zWNbqyMp/JIaCgMqtDntqloSFS2qnJO8dea6PqY84goo+kdYRZ3Kzl7Ul3EZObEO6SPZ5bU5yH4jphRgCmmH89GYlo1p4+CQ5N0JVKY0r/E2GoPAXqi06WPgOMbGpPAaH0kGqzSVyi6vRBlFUqiEXLfuzvLSApZoGph6lflROHenUh8JndaUb2hNcfQTuft4j/JLR4DE9UyTMHteApapJRk6STmrAkOawIDAQABAoIBAG6tWQzTm7TBYF2lOBs44AY07Ftgdyi+roE99Di861p2vNX7TFoXZi3fFGqbOriVfG8Ei7ymHl2mgmQHOn0km7ZSujAViAGxn0MzgfqpzU5M5a0o0fik7ifNa9o7o3KwJarOpOs1anlUNFvm3bmLz+z7xto+oiRNAA2F/YXh/DIbYMgdUQWF52l7MgsGhi7MCFtRlno8HRSY6b8LE2QuNr64/J0/5IESXo0JI9Ek7s4TcCZ8YnGT1v42RNQ4RGvQXl/BiW1l3VfCNc92yKnILuKknUvID6TkKyevUfL0DL6bmZYTQXcDiU+JmQf5Xben9wtUhhVZSxydR9OO+bqR93kCgYEAzzDBFHcC0/Q/ZYnZjfs8QVgUQyZbFh5zip1PnC3iiK2pqjqL9gePkPKJ2cKWW7B3gdvd/Z2KBhwo58Y+c9/Z2fm1SGUKJIGoxTk5g1X99chAQAX4fHEnWukmL87phU7oSbWTvl66EuijPvydQX5qVXQkzhActD4f6maEfXG4f58CgYEAwaoOVBKmer7yheCL4xRx68UHc3VbqFYS7+4mZRpzntzZsGQ+IcjJw9X9aWeJN6uP6VVm3rDBFF+3iHwfPLF9A1Y6frQBp62b4Z6ZnHQLIthwO6hSZtBPloePj5eE+5ICHEXBEHWuO4XicBTMBmaDoAmtuif9DFv5s3Ti7j1MTbUCgYASzQ7IR6BnEWPrV935B1JJb6+vBD0BvdOoQWwm9Pb4hiG+Q7/NnJQHiCrAKusv+MxvaT80s2YB9e40UgX6x9Zh9Enh/uEzvNxOwUmZxGTeN8S0ypXo3O/ATSXc8r64DRgBEEwO21OxQZEGty+h8NG/XWG1nTqtlHGa+KCPLZGbawKBgFudchfNltn8WMiCeEqdUmMhmyvAefLBfUXpmFo90DJ38bdjRI1A6kntgmsJor0mOPc+AmMYpM5ZlX5IkZJpuGUKtrNXvmyvUU3DdJGxx87dKwLd1tVyeCQSzxQzrqI/6SWsze9WbG0WIg+5lub0OhJMYdXtsuTU4eRGSFBByUX9AoGAVaMXJaJ4sMuA3lOjE1VijAZVfFgj/4dPVWEy159XNKIX7BWsiCQPrHBTsOXrCdmR8HqMS/SaJBXAY7DxUNALiF07DAX8BqFdurJhDMJLtLe8GzMYoPzw9638B2J23wQIVpM/Nc8O7bR2buPmibAT1YZ+po4AarIkwNA2P/pQqMA=";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_detail_act);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");

        initView();

    }

    private void initView() {
        customDialog = new CustomDialog(mCtx);
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("众筹详情");
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnStartZc.setOnClickListener(this);
        getDetail(id);
    }

    private void initDataToView(DetailFund detailFund) {

        tvName.setText(detailFund.detailFundText.title);
        tvATH.setText(detailFund.persons + "");
        tvHelp.setText("￥" + detailFund.reach_rmb + " 万元");
        tvScore.setText(detailFund.bai_fen_bi + " %");
        tvwPubName.setText(detailFund.detailFundUser.name);
        tvwStatus.setText(detailFund.status + ".剩余" + AppUtils.differentDaysByMillisecond(detailFund.stop_time, detailFund.ctime) + "天");
        tvwDesc.setText(detailFund.detailFundText.text);
        tvRegisterNum.setText("￥" + detailFund.target_rmb + " 万元");
        tvFundNum.setText(AppUtils.differentDaysByMillisecond(System.currentTimeMillis() / 1000, detailFund.ctime) + "天");
        tvShouyi.setText(detailFund.bai_fen_bi + "%");

        if (!TextUtils.isEmpty(detailFund.detailFundText.video_screenshot)) {
            ivPlayIcon.setVisibility(View.VISIBLE);
            GlideApp.with(this).load(detailFund.detailFundText.video_screenshot).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(ivFundImg1);
        } else {
            ivPlayIcon.setVisibility(View.GONE);
            if (detailFund.detailFundText.img_link.contains("|")) {
                String[] imgs = detailFund.detailFundText.img_link.split("\\|");
                if (imgs.length > 0) {
                    GlideApp.with(this).load(imgs[0]).error(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).into(ivFundImg1);
                }
            }
        }

        if (detailFund.detailFundText.img_link.contains("|")) {
            String[] imgs = detailFund.detailFundText.img_link.split("\\|");
            for (int i = 0; i < imgs.length; i++) {
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));  //设置图片宽高
                GlideApp.with(this).load(imgs[i]).into(imageView);
                lltImgs.addView(imageView);
            }
        }

        if (detailFund.detailFundText.aptitude_img_link.contains("|")) {
            String[] imgs = detailFund.detailFundText.aptitude_img_link.split("\\|");
            for (int i = 0; i < imgs.length; i++) {
                ImageView imageView = new ImageView(this);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));  //设置图片宽高
                GlideApp.with(this).load(imgs[i]).into(imageView);
                lltImgs.addView(imageView);
            }
        }

        if (detailFund.detailFundText.rmb.contains("|")) {
            String str = detailFund.detailFundText.rmb;
            rmb = str.substring(0, str.indexOf("|"));
            btnStartZc.setText("立即众筹(" + rmb + "起投)");
        }

    }

    private void getDetail(String id) {
        AthService service = App.get().getAthService();
        service.crowd_funding_details(id).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<FundDetailResponse>() {
            @Override
            public void call(FundDetailResponse fundDetailResponse) {
                if (fundDetailResponse != null) {
                    if (fundDetailResponse.detailFund != null) {
                        detailFund = fundDetailResponse.detailFund;
                        initDataToView(fundDetailResponse.detailFund);
                    } else {
                        App.toast(FundDetailAct.this, fundDetailResponse.message);
                    }
                } else {
                    App.toast(FundDetailAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("数据获取失败");
            }
        });
    }

    private void pay(final int id, String amount) {
        customDialog.setMLeftBtt("ATH支付").setMRightBt("支付宝支付").setMsg("请选择支付方式").setIsCancelable(true)
                .setChooseListener(new CustomDialog.ChooseListener() {
                    @Override
                    public void onChoose(int which) {
                        if (which == WHICH_RIGHT) {
                            getOrderInfo(id, amount);
                        } else if (which == WHICH_LEFT) {
                            getOrderATHInfo(id, amount);
                        }
                    }
                }).show();
    }

    private void getOrderInfo(int id, String mount) {
        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("list_id", id + "");
        map.put("rmb", mount + "");
        service.crowd_funding_pay(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                LogUtils.e(userInfoResponse.message);
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        payV2(AESUtils.decryptData(userInfoResponse.data));
                    } else {
                        App.toast(FundDetailAct.this, "支付失败");
                    }
                } else {
                    App.toast(FundDetailAct.this, userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }


    private void getOrderATHInfo(int id, String mount) {
        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("list_id", id + "");
        map.put("rmb", mount + "");
        map.put("type", "ATH");
        service.crowd_funding_pay(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                LogUtils.e(userInfoResponse.message);
                if (userInfoResponse.status == 1) {
                    App.toast(FundDetailAct.this, userInfoResponse.message);
                    finish();
                } else {
                    App.toast(FundDetailAct.this, userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    /**
     * 支付宝支付业务
     * <p>
     * //     * @param v
     */
    public void payV2(String orderInfoParam) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            new AlertDialog.Builder(mCtx).setTitle("警告").setMessage("数据有误")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
                            finish();
                        }
                    }).show();
            return;
        }

        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
//        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
//        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
//
//        String orderParam = orderInfoParam;
//
//        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
//        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
//        final String orderInfo = orderParam + "&" + sign;
        final String orderInfo = orderInfoParam;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(FundDetailAct.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(FundDetailAct.this, "支付成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(FundDetailAct.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnShare:
                break;
            case R.id.btnStartZc:
                pay(Integer.valueOf(id), rmb);
                break;
        }
    }
}
