package cn.innovativest.ath.ui.act;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.TradeOrderDetail;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.core.RongService;
import cn.innovativest.ath.entities.OrderStatusBody;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.OrderDetailResponse;
import cn.innovativest.ath.response.RongLoginResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.ViewImgDialog;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by leepan on 21/03/2018.
 */

public class TradeDetailAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.btnAction)
    ImageButton btnAction;

    @BindView(R.id.ivLoginAvatar)
    ImageView ivLoginAvatar;

    @BindView(R.id.tvName)
    TextView tvName;

    @BindView(R.id.ivRealed)
    ImageView ivRealed;

    @BindView(R.id.tvTradeThirdNumber)
    TextView tvTradeThirdNumber;

    @BindView(R.id.tvApplyNumber)
    TextView tvApplyNumber;

    @BindView(R.id.tvApplySuccessNumber)
    TextView tvApplySuccessNumber;

    @BindView(R.id.tvOrderId)
    TextView tvOrderId;

    @BindView(R.id.tvTradeAmount)
    TextView tvTradeAmount;

    @BindView(R.id.tvOrderStatus)
    TextView tvOrderStatus;

    @BindView(R.id.tvOrderInfo)
    TextView tvOrderInfo;

    @BindView(R.id.tvOrderTime)
    TextView tvOrderTime;

    @BindView(R.id.tvTradePrice)
    TextView tvTradePrice;

    @BindView(R.id.tvTradeNumber)
    TextView tvTradeNumber;

    @BindView(R.id.rltBank)
    RelativeLayout rltBank;

    @BindView(R.id.tvBankName)
    TextView tvBankName;

    @BindView(R.id.tvBankNumber)
    TextView tvBankNumber;

    @BindView(R.id.tvBankAddress)
    TextView tvBankAddress;

    @BindView(R.id.rltAlipay)
    RelativeLayout rltAlipay;

    @BindView(R.id.tvAlipayName)
    TextView tvAlipayName;

    @BindView(R.id.tvAlipayNumber)
    TextView tvAlipayNumber;

    @BindView(R.id.rltWechat)
    RelativeLayout rltWechat;

    @BindView(R.id.tvWechatName)
    TextView tvWechatName;

    @BindView(R.id.tvWechatNumber)
    TextView tvWechatNumber;

    @BindView(R.id.tvPayNumber)
    TextView tvPayNumber;

    @BindView(R.id.lltBottom)
    LinearLayout lltBottom;

    @BindView(R.id.rltCancel)
    LinearLayout rltCancel;

    @BindView(R.id.btnPayed)
    Button btnPayed;

    @BindView(R.id.btnCancel)
    Button btnCancel;

    @BindView(R.id.btnContact)
    Button btnContact;

//    @BindView(R.id.lltCart)
//    LinearLayout lltCart;

    @BindView(R.id.rltWaitCancel)
    LinearLayout rltWaitCancel;

    @BindView(R.id.btnWaitContact)
    Button btnWaitContact;

    @BindView(R.id.btnApply)
    Button btnApply;

    @BindView(R.id.btnWaitCancel)
    Button btnWaitCancel;

//    @BindView(R.id.lltContact)
//    LinearLayout lltContact;

//    @BindView(R.id.lltCallPhone)
//    LinearLayout lltCallPhone;

    private String copyContent;

    String order_number;

    private CustomDialog mDialog;

    private ViewImgDialog viewImgDialog;

    private String isBuy = "false";

    private String isFromPush = "false";

    private String state;

    private String wurl;
    private String aurl;

    private TradeOrderDetail tradeOrderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_detail_act);
        ButterKnife.bind(this);
        isFromPush = getIntent().getStringExtra("isFromPush");
        order_number = getIntent().getStringExtra("order_number");
        isBuy = getIntent().getStringExtra("isBuy");
        state = getIntent().getStringExtra("state");
        if (CUtils.isEmpty(order_number)) {
            finish();
            return;
        }
        if (App.get().user == null) {
            startActivityForResult(new Intent(TradeDetailAct.this, LoginAct.class), 100);
            return;
        }
        initView();
        addListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("TradeDetailAct");
        MobclickAgent.onResume(this);

        if (isFromPush != null && isFromPush.equals("true")) {
            isFromPush = getIntent().getStringExtra("isFromPush");
            order_number = getIntent().getStringExtra("order_number");
            isBuy = getIntent().getStringExtra("isBuy");
            state = getIntent().getStringExtra("state");
        }
        if (CUtils.isEmpty(order_number)) {
            finish();
            return;
        }
        orderDetail();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            isFromPush = intent.getStringExtra("isFromPush");
            order_number = intent.getStringExtra("order_number");
            isBuy = intent.getStringExtra("isBuy");
            state = intent.getStringExtra("state");
            if (CUtils.isEmpty(order_number)) {
                finish();
                return;
            }
            if (App.get().user == null) {
                startActivityForResult(new Intent(TradeDetailAct.this, LoginAct.class), 100);
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    initView();
                    addListener();
                    requestUserInfo();
                }
                break;
        }
    }

    private void requestUserInfo() {

        AthService service = App.get().getAthService();
        service.userInfo().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        LogUtils.e(AESUtils.decryptData(userInfoResponse.data));
                        UserInfo userInfo = new Gson().fromJson(AESUtils.decryptData(userInfoResponse.data), UserInfo.class);
                        if (userInfo != null) {
                            PrefsManager.get().save("userinfo", userInfoResponse.data);
                            getToken(userInfo);
//                            orderDetail();
                        } else {
                            LogUtils.e("userInfo is null");
                        }
                    } else {
                        LogUtils.e("userInfoResponse.data is null");
                    }

                } else {
                    LogUtils.e(userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("获取用户信息失败");
            }
        });
    }

    private void getToken(UserInfo userInfo) {

        RongService service = App.get().getRongService();
//        RongLoginBody rongLoginBody = new RongLoginBody();
//        rongLoginBody.userId = userInfo.id + "";
//        rongLoginBody.name = userInfo.name;
//        rongLoginBody.portraitUri = AppConfig.ATH_APP_URL + userInfo.head_img_link;
        HashMap<String, String> mp = new HashMap<>();
        mp.put("userId", userInfo.id + "");
        mp.put("name", userInfo.name);
        mp.put("portraitUri", AppConfig.ATH_APP_URL + userInfo.head_img_link);
        service.getToken(App.get().generateRequestBody(mp)).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<RongLoginResponse>() {
            @Override
            public void call(RongLoginResponse rongLoginResponse) {
                if (rongLoginResponse != null && rongLoginResponse.code == 200) {
                    if (!CUtils.isEmpty(rongLoginResponse.token)) {
                        PrefsManager.get().save("rongToken", rongLoginResponse.token);
                        connect(rongLoginResponse.token);
                    } else {
                        LogUtils.e("rongLoginResponse.token is null");
                    }

                } else {
                    LogUtils.e("rongLoginResponse.code is not 200");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("获取融云用户信息失败");
            }
        });
    }

    /**
     * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #( Context )} 之后调用。</p>
     * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
     * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
     *
     * @param token 从服务端获取的用户身份令牌（Token）。
     * @return RongIM  客户端核心类的实例。
     */
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.get().getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                /**
                 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
                 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
                 */
                @Override
                public void onTokenIncorrect() {

                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token 对应的用户 id
                 */
                @Override
                public void onSuccess(String userid) {
                    LogUtils.d("--onSuccess" + userid);
//                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                    finish();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("订单详情");
        btnAction.setVisibility(View.VISIBLE);
        btnAction.setImageResource(R.drawable.order_buy_have_message);
        mDialog = new CustomDialog(mCtx);
        viewImgDialog = new ViewImgDialog(mCtx);

        if (isBuy.equals("true")) {//买家查看
            rltCancel.setVisibility(View.VISIBLE);
            rltWaitCancel.setVisibility(View.GONE);

        } else {//卖家查看
            rltCancel.setVisibility(View.GONE);
            rltWaitCancel.setVisibility(View.VISIBLE);
        }
    }

    private void addListener() {
        btnBack.setOnClickListener(this);
        btnAction.setOnClickListener(this);
        btnPayed.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnApply.setOnClickListener(this);
        btnWaitCancel.setOnClickListener(this);
        btnWaitContact.setOnClickListener(this);
//        lltContact.setOnClickListener(this);
//        lltCallPhone.setOnClickListener(this);
//        lltCart.setOnClickListener(this);
        tvPayNumber.setOnClickListener(this);

        rltAlipay.setOnClickListener(this);
        rltWechat.setOnClickListener(this);

    }

    private void bindingView(TradeOrderDetail tradeOrderDetail) {

        if (tradeOrderDetail.orderDetailHead != null) {

            tvName.setText(tradeOrderDetail.orderDetailHead.name);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + tradeOrderDetail.orderDetailHead.head_img_link).placeholder(R.drawable.mine_avatar).error(R.drawable.mine_avatar).optionalCircleCrop().into(ivLoginAvatar);
            if (tradeOrderDetail.orderDetailHead.state == 1) {
                ivRealed.setVisibility(View.VISIBLE);
            } else {
                ivRealed.setVisibility(View.INVISIBLE);
            }

            tvTradeThirdNumber.setText("成功交易数：" + tradeOrderDetail.orderDetailHead.deal_number);
            tvApplyNumber.setText(tradeOrderDetail.orderDetailHead.appeal_number);
            tvApplySuccessNumber.setText(tradeOrderDetail.orderDetailHead.victory_number);
        } else {
            tvTradeThirdNumber.setText("成功交易数：0");
            tvApplyNumber.setText("0");
            tvApplySuccessNumber.setText("0");
        }

        if (tradeOrderDetail.orderDetailBody != null) {
            tvOrderId.setText("#" + tradeOrderDetail.orderDetailBody.order_id);
            tvTradeAmount.setText(String.format("%.2f", Float.valueOf(tradeOrderDetail.orderDetailBody.money)) + " CNY");
            state = tradeOrderDetail.orderDetailBody.state;
            if (tradeOrderDetail.orderDetailBody.state.equals("0")) {//取消订单
//                tvOrderTime.setVisibility(View.INVISIBLE);
//                tvOrderInfo.setVisibility(View.INVISIBLE);
//                tvOrderStatus.setText("订单已取消");
//                lltBottom.setVisibility(View.GONE);
                lltBottom.setVisibility(View.VISIBLE);
                rltCancel.setVisibility(View.VISIBLE);
                rltWaitCancel.setVisibility(View.GONE);
                tvOrderTime.setVisibility(View.INVISIBLE);
                tvOrderInfo.setVisibility(View.INVISIBLE);
                tvOrderStatus.setText("订单已取消");
                btnPayed.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                btnPayed.setText("我要申诉");
                btnCancel.setVisibility(View.GONE);

            } else if (tradeOrderDetail.orderDetailBody.state.equals("1")) {//待付款
                if (isBuy.equals("true")) {
                    tvOrderTime.setVisibility(View.VISIBLE);
                    tvOrderInfo.setVisibility(View.VISIBLE);
                    lltBottom.setVisibility(View.VISIBLE);
                    rltCancel.setVisibility(View.VISIBLE);
                    rltWaitCancel.setVisibility(View.GONE);
                    tvOrderStatus.setText("待付款");
                    tvOrderStatus.setTextColor(Color.parseColor("#EF7B00"));
                    if (tradeOrderDetail.orderDetailFoot != null) {
                        if (Long.valueOf(tradeOrderDetail.orderDetailFoot.timediff) < 1800) {
                            rxJava(Long.parseLong(tradeOrderDetail.orderDetailFoot.timediff));
                        } else {
                            changeOrderState("0");//买家取消订单
                        }
                    }
                } else {
                    lltBottom.setVisibility(View.GONE);
                    tvOrderTime.setVisibility(View.VISIBLE);
                    tvOrderInfo.setVisibility(View.VISIBLE);
                    tvOrderStatus.setText("待付款");
                    tvOrderStatus.setTextColor(Color.parseColor("#EF7B00"));
                    if (tradeOrderDetail.orderDetailFoot != null) {
                        if (Long.valueOf(tradeOrderDetail.orderDetailFoot.timediff) < 1800) {
                            rxJava(Long.parseLong(tradeOrderDetail.orderDetailFoot.timediff));
                        } else {
                            tvOrderTime.setVisibility(View.INVISIBLE);
                            tvOrderInfo.setVisibility(View.INVISIBLE);
                            tvOrderStatus.setText("订单已取消");
                            lltBottom.setVisibility(View.GONE);
                        }

                    }
                }

            } else if (tradeOrderDetail.orderDetailBody.state.equals("2")) {//已付款
                if (isBuy.equals("true")) {
                    lltBottom.setVisibility(View.VISIBLE);
                    rltCancel.setVisibility(View.VISIBLE);
                    rltWaitCancel.setVisibility(View.GONE);
                    tvOrderTime.setVisibility(View.INVISIBLE);
                    tvOrderInfo.setVisibility(View.INVISIBLE);
                    tvOrderStatus.setText("已付款");
                    btnPayed.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                    btnPayed.setText("我要申诉");
                    btnCancel.setVisibility(View.GONE);
                } else {
                    tvOrderStatus.setText("已付款");
                    tvOrderTime.setVisibility(View.INVISIBLE);
                    tvOrderInfo.setVisibility(View.INVISIBLE);
                    lltBottom.setVisibility(View.VISIBLE);
                    rltCancel.setVisibility(View.GONE);
                    rltWaitCancel.setVisibility(View.VISIBLE);
                }

            } else if (tradeOrderDetail.orderDetailBody.state.equals("3")) {//已完成
//                lltBottom.setVisibility(View.GONE);
//                tvOrderStatus.setText("已完成");
//                tvOrderTime.setVisibility(View.INVISIBLE);
//                tvOrderInfo.setVisibility(View.INVISIBLE);

                lltBottom.setVisibility(View.VISIBLE);
                rltCancel.setVisibility(View.VISIBLE);
                rltWaitCancel.setVisibility(View.GONE);
                tvOrderStatus.setText("已完成");
                tvOrderTime.setVisibility(View.INVISIBLE);
                tvOrderInfo.setVisibility(View.INVISIBLE);
                btnPayed.setBackgroundColor(Color.parseColor("#ff33b5e5"));
                btnPayed.setText("我要申诉");
                btnCancel.setVisibility(View.GONE);
            }
            tvTradePrice.setText("交易单价 " + String.format("%.2f", Float.valueOf(tradeOrderDetail.orderDetailBody.ath_price)) + " CNY/ATH");
            tvTradeNumber.setText("交易数量 " + String.format("%.2f", Float.valueOf(tradeOrderDetail.orderDetailBody.number)) + " ATH");
        }

        if (tradeOrderDetail.orderDetailFoot != null) {
            if (!CUtils.isEmpty(tradeOrderDetail.orderDetailFoot.pay_way_bank)) {
                rltBank.setVisibility(View.VISIBLE);
                tvBankName.setText(tradeOrderDetail.orderDetailFoot.real_name);
                String[] strbank = tradeOrderDetail.orderDetailFoot.pay_way_bank.split("\\|");
                if (strbank.length == 3) {
                    tvBankNumber.setText(strbank[2]);
                    tvBankAddress.setText(strbank[0] + " " + strbank[1] + " 17：00以后打款");
                }

            } else {
                rltBank.setVisibility(View.GONE);
            }

            if (!CUtils.isEmpty(tradeOrderDetail.orderDetailFoot.pay_way_alipay)) {
                rltAlipay.setVisibility(View.VISIBLE);
                tvAlipayName.setText(tradeOrderDetail.orderDetailFoot.real_name);
                String[] stralipay = tradeOrderDetail.orderDetailFoot.pay_way_alipay.split("\\|");
                if (stralipay.length == 2) {
                    aurl = AppConfig.ATH_APP_URL + stralipay[0];
                    tvAlipayNumber.setText(stralipay[1]);
                } else {
                    tvAlipayNumber.setText("");
                }
            } else {
                rltAlipay.setVisibility(View.GONE);
            }

            if (!CUtils.isEmpty(tradeOrderDetail.orderDetailFoot.pay_way_wechat)) {
                rltWechat.setVisibility(View.VISIBLE);
                tvWechatName.setText(tradeOrderDetail.orderDetailFoot.real_name);
                String[] strwechat = tradeOrderDetail.orderDetailFoot.pay_way_wechat.split("\\|");
                if (strwechat.length == 2) {
                    wurl = AppConfig.ATH_APP_URL + strwechat[0];
                    tvWechatNumber.setText(strwechat[1]);
                } else {
                    tvWechatNumber.setText("");
                }
            } else {
                rltWechat.setVisibility(View.GONE);
            }
            if (tradeOrderDetail != null && tradeOrderDetail.orderDetailFoot != null && !CUtils.isEmpty(tradeOrderDetail.orderDetailFoot.reference_number)) {
                copyContent = tradeOrderDetail.orderDetailFoot.reference_number;
                tvPayNumber.setText(tradeOrderDetail.orderDetailFoot.reference_number);
            }
        }

    }

    /**
     * RxJava 方式实现
     */
    private void rxJava(long allTime) {
//        final long count = allTime / 1000;
        if (allTime > 1800) {
            allTime = 1800;
        }
        final long count = allTime;
        Observable.interval(0, 1, TimeUnit.SECONDS)//设置0延迟，每隔一秒发送一条数据
                .take((int) (1800 - allTime + 1)) //设置总共发送的次数
                .map(new Func1<Long, Long>() {//long 值是从小到大，倒计时需要将值倒置
                    @Override
                    public Long call(Long aLong) {
//                        return count - aLong;
                        return count + aLong;
                    }
                })
                .subscribeOn(Schedulers.computation())
                // doOnSubscribe 执行线程由下游逻辑最近的 subscribeOn() 控制，下游没有 subscribeOn() 则跟Subscriber 在同一线程执行
                //执行计时任务前先将 button 设置为不可点击
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
//                        mStart.setEnabled(false);//在发送数据的时候设置为不能点击
//                        mStart.setBackgroundColor(Color.GRAY);//背景色设为灰色
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())//操作UI主要在UI线程
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
//                        mTvValue.setText(getResources().getString(R.string.done));
//                        mStart.setEnabled(true);
//                        mStart.setBackgroundColor(Color.parseColor("#f97e7e"));
                        if (isBuy.equals("true")) {
                            changeOrderState("0");//买家取消订单
                        } else {
                            tvOrderTime.setVisibility(View.INVISIBLE);
                            tvOrderInfo.setVisibility(View.INVISIBLE);
                            tvOrderStatus.setText("订单已取消");
                            lltBottom.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Long aLong) { //接收到一条就是会操作一次UI
                        if (aLong <= 59) {
                            tvOrderTime.setText(String.format("（00分钟%02d秒）", aLong));
                        } else {
                            tvOrderTime.setText(String.format("（%02d分钟%02d秒）", aLong / 60, aLong % 60));
                        }

                    }
                });
    }


    private void orderDetail() {
        LoadingUtils.getInstance().dialogShow(this, "请求中...");
        AthService service = App.get().getAthService();
        service.order(order_number).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<OrderDetailResponse>() {
            @Override
            public void call(OrderDetailResponse orderDetailResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (orderDetailResponse.status == 1) {
                    if (orderDetailResponse.tradeOrderDetail != null) {
                        tradeOrderDetail = orderDetailResponse.tradeOrderDetail;
                        bindingView(orderDetailResponse.tradeOrderDetail);
                    }
                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
            }
        });
    }

    private void changeOrderState(final String state) {
        OrderStatusBody orderStatusBody = new OrderStatusBody();
        orderStatusBody.order_number = order_number;
        orderStatusBody.state = state;

        AthService service = App.get().getAthService();
        service.transaction(orderStatusBody).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                App.toast(TradeDetailAct.this, baseResponse.message);
                if (baseResponse.status == 1) {
//                    if (isBuy && state.equals("2")) {
//                        btnPayed.setBackgroundColor(Color.parseColor("#E6E6E6"));
//                        btnPayed.setText("我要申诉");
//                        btnCancel.setVisibility(View.GONE);
//                    } else if (isBuy && state.equals("1")) {
//                        tvOrderTime.setVisibility(View.INVISIBLE);
//                        tvOrderStatus.setText("订单已取消");
//                        lltBottom.setVisibility(View.GONE);
//                    } else if (!isBuy && state.equals("2")) {
//                        lltBottom.setVisibility(View.GONE);
//                        tvOrderStatus.setText("已完成");
//                        tvOrderTime.setVisibility(View.INVISIBLE);
//                    }
                    orderDetail();
                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isFromPush != null && isFromPush.equals("true")) {
            startActivity(new Intent(TradeDetailAct.this, NewMainAct.class));
            finish();
        } else {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("TradeDetailAct");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnPayed:
                if (btnPayed.getText().toString().equals("我已付款")) {
                    mDialog.setIsCancelable(true).setMsg("请确认您已向卖方付款。恶意点击将直接冻结账户！")
                            .setChooseListener(new CustomDialog.ChooseListener() {

                                @Override
                                public void onChoose(int which) {
                                    if (which == WHICH_RIGHT) {
                                        changeOrderState("2");  //买家已付款
                                    }
                                }
                            }).show();
                } else if (btnPayed.getText().toString().equals("我要申诉")) {
                    if (state.equals("0") || state.equals("2") || state.equals("3")) {
//                        changeOrderState("");//买家申诉   点击跳转到聊天界面  state  0,2,3
                        startActivity(new Intent(TradeDetailAct.this, ApplyAct.class).putExtra("order_number", order_number));
                    } else {
                        App.toast(TradeDetailAct.this, "当前该订单不能进行申诉");
                    }
                }
                break;
            case R.id.btnCancel:
                mDialog.setIsCancelable(true).setMsg("请确认是否取消订单！")
                        .setChooseListener(new CustomDialog.ChooseListener() {
                            @Override
                            public void onChoose(int which) {
                                if (which == WHICH_RIGHT) {
                                    changeOrderState("0");//买家取消订单
                                }
                            }
                        }).show();
                break;
            case R.id.btnApply:
                if (state.equals("0") || state.equals("2") || state.equals("3")) {
//                    changeOrderState("");//卖家申诉   点击跳转到聊天界面  state  2
                    startActivity(new Intent(TradeDetailAct.this, ApplyAct.class).putExtra("order_number", order_number));
                } else {
                    App.toast(TradeDetailAct.this, "当前该订单不能进行申诉");
                }
                break;
            case R.id.btnWaitCancel:
                mDialog.setIsCancelable(true).setMsg("请确认您已收到买方付款！")
                        .setChooseListener(new CustomDialog.ChooseListener() {
                            @Override
                            public void onChoose(int which) {
                                if (which == WHICH_RIGHT) {
                                    changeOrderState("3");//卖家我已收款  即是结束订单
                                }
                            }
                        }).show();
                break;
//            case R.id.lltContact:
//                if (state.equals("2")) {
//                    //点击跳转到聊天界面  state  2
//                    startActivity(new Intent(TradeDetailAct.this, ApplyAct.class).putExtra("order_number", order_number));
//                } else {
//                    App.toast(TradeDetailAct.this, "当前该订单不能进行申诉");
//                }
//                break;
            case R.id.lltCart:
                if (state.equals("0") || state.equals("2") || state.equals("3")) {
                    //点击跳转到聊天界面  state  2
                    startActivity(new Intent(TradeDetailAct.this, ApplyAct.class).putExtra("order_number", order_number));
                } else {
                    App.toast(TradeDetailAct.this, "当前该订单不能进行申诉");
                }
                break;
            case R.id.rltAlipay:
                if (!CUtils.isEmpty(aurl)) {
                    viewImgDialog.setIsCancelable(true).setImgUrl(aurl).show();
                } else {
                    App.toast(TradeDetailAct.this, "支付宝二维码图片地址有误");
                }
                break;
            case R.id.rltWechat:
                if (!CUtils.isEmpty(wurl)) {
                    viewImgDialog.setIsCancelable(true).setImgUrl(wurl).show();
                } else {
                    App.toast(TradeDetailAct.this, "微信二维码图片地址有误");
                }
                break;
            case R.id.btnContact:
                if (tradeOrderDetail != null && tradeOrderDetail.orderDetailHead != null && !CUtils.isEmpty(tradeOrderDetail.orderDetailHead.other_party_id)) {
//                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, tradeOrderDetail.orderDetailHead.other_party_id, tradeOrderDetail.orderDetailHead.name);
                    RongIM.getInstance().startPrivateChat(this, tradeOrderDetail.orderDetailHead.other_party_id, tradeOrderDetail.orderDetailHead.name);
                } else {
                    App.toast(TradeDetailAct.this, "数据错误，请稍候再试");
                }
                break;
            case R.id.btnWaitContact:
                if (tradeOrderDetail != null && tradeOrderDetail.orderDetailHead != null && !CUtils.isEmpty(tradeOrderDetail.orderDetailHead.other_party_id)) {
//                    RongIM.getInstance().startConversation(this, Conversation.ConversationType.PRIVATE, tradeOrderDetail.orderDetailHead.other_party_id, tradeOrderDetail.orderDetailHead.name);
                    RongIM.getInstance().startPrivateChat(this, tradeOrderDetail.orderDetailHead.other_party_id, tradeOrderDetail.orderDetailHead.name);
                } else {
                    App.toast(TradeDetailAct.this, "数据错误，请稍候再试");
                }
                break;
            case R.id.btnAction:
                Map<String, Boolean> mp = new HashMap<String, Boolean>();
                mp.put(Conversation.ConversationType.PRIVATE.getName(), false);
                RongIM.getInstance().startConversationList(this, mp);
                break;
            case R.id.tvPayNumber:
                if (!CUtils.isEmpty(copyContent)) {
                    copy(copyContent);
                } else {
                    App.toast(TradeDetailAct.this, "信息不能为空，请稍候再试");
                }
                break;

        }
    }

    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        App.toast(this, "复制成功");
    }
}
