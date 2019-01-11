package cn.innovativest.ath.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.bean.SpeedMine;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.SpeedMineResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.act.RechargeAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PayResult;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class SpeedValueDialog extends Dialog implements View.OnClickListener {
    public static int TYPE_CONFIRM = 1;
    public static int TYPE_TIP = 2;
    //    TextView mMsgTv;
//    Button mLeftBt, mRightBt;
//    ChooseListener mListener;
    @BindView(R.id.rltHigh6to18)
    RelativeLayout rltHigh6to18;

    @BindView(R.id.llt6to18)
    LinearLayout llt6to18;

    @BindView(R.id.rltHigh19to21)
    RelativeLayout rltHigh19to21;

    @BindView(R.id.llt19to21)
    LinearLayout llt19to21;

    @BindView(R.id.rltHigh4)
    RelativeLayout rltHigh4;

    @BindView(R.id.btnHigh4)
    Button btnHigh4;

    @BindView(R.id.rltHigh5)
    RelativeLayout rltHigh5;

    @BindView(R.id.btnHigh5)
    Button btnHigh5;

    @BindView(R.id.rltHigh6)
    RelativeLayout rltHigh6;

    @BindView(R.id.btnHigh6)
    Button btnHigh6;

    @BindView(R.id.rltHigh7)
    RelativeLayout rltHigh7;

    @BindView(R.id.btnHigh7)
    Button btnHigh7;

    @BindView(R.id.rltHigh8)
    RelativeLayout rltHigh8;

    @BindView(R.id.btnHigh8)
    Button btnHigh8;

    @BindView(R.id.rltHigh9)
    RelativeLayout rltHigh9;

    @BindView(R.id.btnHigh9)
    Button btnHigh9;

    @BindView(R.id.rltHigh10)
    RelativeLayout rltHigh10;

    @BindView(R.id.btnHigh10)
    Button btnHigh10;

    @BindView(R.id.rltHigh11)
    RelativeLayout rltHigh11;

    @BindView(R.id.btnHigh11)
    Button btnHigh11;

    @BindView(R.id.rltHigh12)
    RelativeLayout rltHigh12;

    @BindView(R.id.btnHigh12)
    Button btnHigh12;

    @BindView(R.id.rltHigh13)
    RelativeLayout rltHigh13;

    @BindView(R.id.btnHigh13)
    Button btnHigh13;

    @BindView(R.id.rltHigh14)
    RelativeLayout rltHigh14;

    @BindView(R.id.btnHigh14)
    Button btnHigh14;

    @BindView(R.id.rltHigh15)
    RelativeLayout rltHigh15;

    @BindView(R.id.btnHigh15)
    Button btnHigh15;

    @BindView(R.id.rltHigh16)
    RelativeLayout rltHigh16;

    @BindView(R.id.btnHigh16)
    Button btnHigh16;

    @BindView(R.id.rltHigh17)
    RelativeLayout rltHigh17;

    @BindView(R.id.btnHigh17)
    Button btnHigh17;

    @BindView(R.id.rltHigh18)
    RelativeLayout rltHigh18;

    @BindView(R.id.btnHigh18)
    Button btnHigh18;

    @BindView(R.id.rltHigh19)
    RelativeLayout rltHigh19;

    @BindView(R.id.btnHigh19)
    Button btnHigh19;

    @BindView(R.id.rltHigh20)
    RelativeLayout rltHigh20;

    @BindView(R.id.btnHigh20)
    Button btnHigh20;

    @BindView(R.id.rltHigh21)
    RelativeLayout rltHigh21;

    @BindView(R.id.btnHigh21)
    Button btnHigh21;

    @BindView(R.id.ivRightPubManagement)
    ImageView ivRightPubManagement;

    @BindView(R.id.ivRightAdPubManagement)
    ImageView ivRightAdPubManagement;

    @BindView(R.id.tvwHigh4)
    TextView tvwHigh4;

    @BindView(R.id.tvwHigh5)
    TextView tvwHigh5;

    @BindView(R.id.tvwHigh6to18)
    TextView tvwHigh6to18;

    @BindView(R.id.tvwHigh19to21)
    TextView tvwHigh19to21;


    private Context mCtx;

    private CustomDialog customDialog;

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

    private ArrayList<SpeedMine> speedMines;

    private Activity act;


    public SpeedValueDialog(Context context, Activity activity) {
        super(context, R.style.mDialog);
        setContentView(R.layout.dia_speed_value);
        act = activity;
        ButterKnife.bind(this);
        mCtx = context;
        customDialog = new CustomDialog(context);


//        getPitData();
//        mMsgTv = (TextView) findViewById(R.id.dialog_msg_tv);
//        mLeftBt = (Button) findViewById(R.id.dialog_left_bt);
//        mRightBt = (Button) findViewById(R.id.dialog_right_bt);
//
//        mLeftBt.setOnClickListener(this);
//        mRightBt.setOnClickListener(this);

        showView();

        rltHigh6to18.setOnClickListener(this);
        rltHigh19to21.setOnClickListener(this);

        btnHigh4.setOnClickListener(this);
        btnHigh5.setOnClickListener(this);
        btnHigh6.setOnClickListener(this);
        btnHigh7.setOnClickListener(this);
        btnHigh8.setOnClickListener(this);
        btnHigh9.setOnClickListener(this);
        btnHigh10.setOnClickListener(this);
        btnHigh11.setOnClickListener(this);
        btnHigh12.setOnClickListener(this);
        btnHigh13.setOnClickListener(this);
        btnHigh14.setOnClickListener(this);
        btnHigh15.setOnClickListener(this);
        btnHigh16.setOnClickListener(this);
        btnHigh17.setOnClickListener(this);
        btnHigh18.setOnClickListener(this);
        btnHigh19.setOnClickListener(this);
        btnHigh20.setOnClickListener(this);
        btnHigh21.setOnClickListener(this);
    }

    private void showView() {
        if (llt6to18.getVisibility() == View.GONE) {
            ivRightPubManagement.setImageResource(R.drawable.speed_value_expand);
        } else if (llt6to18.getVisibility() == View.VISIBLE) {
            ivRightPubManagement.setImageResource(R.drawable.speed_value_group);
        }

        if (llt19to21.getVisibility() == View.GONE) {
            ivRightAdPubManagement.setImageResource(R.drawable.speed_value_expand);
        } else if (llt19to21.getVisibility() == View.VISIBLE) {
            ivRightAdPubManagement.setImageResource(R.drawable.speed_value_group);
        }
    }

    private void initDataToView(ArrayList<SpeedMine> speedMines) {
        if (speedMines.size() == 18) {

            if (speedMines.get(0) != null) {
                if (speedMines.get(0).status == 0) {
                    btnHigh4.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh4.setEnabled(false);
                } else if (speedMines.get(0).status == 1) {
                    btnHigh4.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh4.setEnabled(true);
                }

                if (!CUtils.isEmpty(speedMines.get(0).rmb)) {
                    tvwHigh4.setText("4号高级矿机（" + speedMines.get(0).rmb + "元）");
                }
            }

            if (speedMines.get(1) != null) {
                if (speedMines.get(1).status == 0) {
                    btnHigh5.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh5.setEnabled(false);
                } else if (speedMines.get(1).status == 1) {
                    btnHigh5.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh5.setEnabled(true);
                }

                if (!CUtils.isEmpty(speedMines.get(1).rmb)) {
                    tvwHigh5.setText("5号高级矿机（" + speedMines.get(1).rmb + "元）");
                }
            }

            if (speedMines.get(2) != null) {
                if (speedMines.get(2).status == 0) {
                    btnHigh6.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh6.setEnabled(false);
                } else if (speedMines.get(2).status == 1) {
                    btnHigh6.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh6.setEnabled(true);
                }

                if (!CUtils.isEmpty(speedMines.get(2).rmb)) {
                    tvwHigh6to18.setText("6-18号高级矿机（" + speedMines.get(2).rmb + "元）");
                }
            }

            if (speedMines.get(3) != null) {
                if (speedMines.get(3).status == 0) {
                    btnHigh7.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh7.setEnabled(false);
                } else if (speedMines.get(3).status == 1) {
                    btnHigh7.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh7.setEnabled(true);
                }
            }

            if (speedMines.get(4) != null) {
                if (speedMines.get(4).status == 0) {
                    btnHigh8.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh8.setEnabled(false);
                } else if (speedMines.get(4).status == 1) {
                    btnHigh8.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh8.setEnabled(true);
                }
            }

            if (speedMines.get(5) != null) {
                if (speedMines.get(5).status == 0) {
                    btnHigh9.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh9.setEnabled(false);
                } else if (speedMines.get(5).status == 1) {
                    btnHigh9.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh9.setEnabled(true);
                }
            }

            if (speedMines.get(6) != null) {
                if (speedMines.get(6).status == 0) {
                    btnHigh10.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh10.setEnabled(false);
                } else if (speedMines.get(6).status == 1) {
                    btnHigh10.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh10.setEnabled(true);
                }
            }

            if (speedMines.get(7) != null) {
                if (speedMines.get(7).status == 0) {
                    btnHigh11.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh11.setEnabled(false);
                } else if (speedMines.get(7).status == 1) {
                    btnHigh11.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh11.setEnabled(true);
                }
            }

            if (speedMines.get(8) != null) {
                if (speedMines.get(8).status == 0) {
                    btnHigh12.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh12.setEnabled(false);
                } else if (speedMines.get(8).status == 1) {
                    btnHigh12.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh12.setEnabled(true);
                }
            }

            if (speedMines.get(9) != null) {
                if (speedMines.get(9).status == 0) {
                    btnHigh13.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh13.setEnabled(false);
                } else if (speedMines.get(9).status == 1) {
                    btnHigh13.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh13.setEnabled(true);
                }
            }

            if (speedMines.get(10) != null) {
                if (speedMines.get(10).status == 0) {
                    btnHigh14.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh14.setEnabled(false);
                } else if (speedMines.get(10).status == 1) {
                    btnHigh14.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh14.setEnabled(true);
                }
            }

            if (speedMines.get(11) != null) {
                if (speedMines.get(11).status == 0) {
                    btnHigh15.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh15.setEnabled(false);
                } else if (speedMines.get(11).status == 1) {
                    btnHigh15.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh15.setEnabled(true);
                }
            }

            if (speedMines.get(12) != null) {
                if (speedMines.get(12).status == 0) {
                    btnHigh16.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh16.setEnabled(false);
                } else if (speedMines.get(12).status == 1) {
                    btnHigh16.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh16.setEnabled(true);
                }
            }

            if (speedMines.get(13) != null) {
                if (speedMines.get(13).status == 0) {
                    btnHigh17.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh17.setEnabled(false);
                } else if (speedMines.get(13).status == 1) {
                    btnHigh17.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh17.setEnabled(true);
                }
            }

            if (speedMines.get(14) != null) {
                if (speedMines.get(14).status == 0) {
                    btnHigh18.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh18.setEnabled(false);
                } else if (speedMines.get(14).status == 1) {
                    btnHigh18.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh18.setEnabled(true);
                }
            }

            if (speedMines.get(15) != null) {
                if (speedMines.get(15).status == 0) {
                    btnHigh19.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh19.setEnabled(false);
                } else if (speedMines.get(15).status == 1) {
                    btnHigh19.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh19.setEnabled(true);
                }

                if (!CUtils.isEmpty(speedMines.get(15).rmb)) {
                    tvwHigh19to21.setText("19-21号高级矿机（" + speedMines.get(15).rmb + "元）");
                }
            }

            if (speedMines.get(16) != null) {
                if (speedMines.get(16).status == 0) {
                    btnHigh20.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh20.setEnabled(false);
                } else if (speedMines.get(16).status == 1) {
                    btnHigh20.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh20.setEnabled(true);
                }
            }

            if (speedMines.get(17) != null) {
                if (speedMines.get(17).status == 0) {
                    btnHigh21.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_locked_min));
                    btnHigh21.setEnabled(false);
                } else if (speedMines.get(17).status == 1) {
                    btnHigh21.setBackground(getContext().getResources().getDrawable(R.drawable.speed_value_unlock_min));
                    btnHigh21.setEnabled(true);
                }
            }

        }

    }

    public void getPitData() {

        AthService service = App.get().getAthService();
        service.pit().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<SpeedMineResponse>() {
            @Override
            public void call(SpeedMineResponse speedMineResponse) {
                if (speedMineResponse != null && speedMineResponse.speedMines != null && speedMineResponse.speedMines.size() > 0) {
                    initDataToView(speedMineResponse.speedMines);
                } else {
                    App.toast(getContext(), "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(getContext(), "数据获取失败");
            }
        });
    }

    public SpeedValueDialog isCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

//    public SpeedValueDialog setMsg(String msg) {
//
//        mMsgTv.setText(msg);
//        return this;
//    }
//
//    public SpeedValueDialog setMRightBt(String mRightBtTxt) {
//
//        mRightBt.setText(mRightBtTxt);
//        return this;
//    }
//
//    public SpeedValueDialog setMLeftBtt(String mLeftBtTxt) {
//
//        mLeftBt.setText(mLeftBtTxt);
//        return this;
//    }

    public SpeedValueDialog setIsCancelable(boolean flag) {

        setCancelable(flag);
        return this;
    }

//    public SpeedValueDialog setType(int type) {
//        if (type == TYPE_TIP) {
//            findViewById(R.id.dialog_line).setVisibility(View.GONE);
//            findViewById(R.id.dialog_left_bt).setVisibility(View.GONE);
//        }
//        return this;
//    }
//
//    public SpeedValueDialog setBtStr(String... BtStr) {
//
//        if (BtStr != null) {
//            if (BtStr.length >= 1) {
//                mLeftBt.setText(BtStr[0]);
//            }
//            if (BtStr.length >= 2) {
//                mRightBt.setText(BtStr[1]);
//            }
//
//        }
//        return this;
//    }
//
//    public SpeedValueDialog setChooseListener(ChooseListener l) {
//        mListener = l;
//        return this;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.dialog_left_bt:
//                dismiss();
//                if (mListener != null) {
//                    mListener.onChoose(ChooseListener.WHICH_LEFT);
//                }
//                break;
//
//            case R.id.dialog_right_bt:
//                dismiss();
//                if (mListener != null) {
//                    mListener.onChoose(ChooseListener.WHICH_RIGHT);
//                }
//                break;
            case R.id.rltHigh6to18:
                if (llt6to18.getVisibility() == View.GONE) {
                    llt6to18.setVisibility(View.VISIBLE);
                } else {
                    llt6to18.setVisibility(View.GONE);
                }
                showView();
                break;
            case R.id.rltHigh19to21:
                if (llt19to21.getVisibility() == View.GONE) {
                    llt19to21.setVisibility(View.VISIBLE);
                } else {
                    llt19to21.setVisibility(View.GONE);
                }
                showView();
                break;
            case R.id.btnHigh4:
                pay(4);
                break;
            case R.id.btnHigh5:
                pay(5);
                break;
            case R.id.btnHigh6:
                pay(6);
                break;
            case R.id.btnHigh7:
                pay(7);
                break;
            case R.id.btnHigh8:
                pay(8);
                break;
            case R.id.btnHigh9:
                pay(9);
                break;
            case R.id.btnHigh10:
                pay(10);
                break;
            case R.id.btnHigh11:
                pay(11);
                break;
            case R.id.btnHigh12:
                pay(12);
                break;
            case R.id.btnHigh13:
                pay(13);
                break;
            case R.id.btnHigh14:
                pay(14);
                break;
            case R.id.btnHigh15:
                pay(15);
                break;
            case R.id.btnHigh16:
                pay(16);
                break;
            case R.id.btnHigh17:
                pay(17);
                break;
            case R.id.btnHigh18:
                pay(18);
                break;
            case R.id.btnHigh19:
                pay(19);
                break;
            case R.id.btnHigh20:
                pay(20);
                break;
            case R.id.btnHigh21:
                pay(21);
                break;
        }
    }

    private void pay(final int pit) {
        customDialog.setMLeftBtt("ATH购买").setMRightBt("支付宝购买").setMsg("充值解锁高级矿机").setIsCancelable(true)
                .setChooseListener(new CustomDialog.ChooseListener() {
                    @Override
                    public void onChoose(int which) {
                        if (which == WHICH_RIGHT) {
                            getOrderInfo(pit);
                        } else if (which == WHICH_LEFT) {
                            getOrderATHInfo(pit);
                        }
                    }
                }).show();
    }

    private void getOrderATHInfo(int pit) {
        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pit", pit + "");
        map.put("type", "ATH");
        service.payOrder(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                LogUtils.e(userInfoResponse.message);
                if (userInfoResponse.status == 1) {
                    getPitData();
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        App.toast(getContext(), userInfoResponse.data);
                    } else {
                        App.toast(getContext(), userInfoResponse.message);
                    }
                } else {
                    App.toast(getContext(), userInfoResponse.message);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage().toString());
            }
        });
    }

    private void getOrderInfo(int pit) {
        AthService service = App.get().getAthService();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pit", pit + "");
        map.put("type", "3");
        service.payOrder(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UserInfoResponse>() {
            @Override
            public void call(UserInfoResponse userInfoResponse) {
                LogUtils.e(userInfoResponse.message);
                if (userInfoResponse.status == 1) {
                    if (!CUtils.isEmpty(userInfoResponse.data)) {
                        payV2(AESUtils.decryptData(userInfoResponse.data));
                    } else {
                        App.toast(getContext(), "支付失败");
                    }
                } else {
                    App.toast(getContext(), userInfoResponse.message);
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
//                            finish();
                            dismiss();
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
                PayTask alipay = new PayTask(act);
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
                        Toast.makeText(getContext(), "支付成功", Toast.LENGTH_SHORT).show();
                        getPitData();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(getContext(), "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


    public interface ChooseListener {
        public final static int WHICH_LEFT = 1;
        public final static int WHICH_RIGHT = 2;

        /**
         * which=1,左button。 which=2,右button。
         */
        void onChoose(int which);
    }

}
