package cn.innovativest.ath.ui.act;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.LogUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class AddFundAct extends BaseAct {


    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etName)
    EditText etName;

    @BindView(R.id.etGoal)
    EditText etGoal;

    @BindView(R.id.etAmount)
    EditText etAmount;

    @BindView(R.id.tvEndDate)
    TextView tvEndDate;

    @BindView(R.id.etContent)
    EditText etContent;

    @BindView(R.id.btnUploadImg1)
    Button btnUploadImg1;

    @BindView(R.id.lltAddImg1)
    LinearLayout lltAddImg1;

    @BindView(R.id.btnUploadImg2)
    Button btnUploadImg2;

    @BindView(R.id.lltAddImg2)
    LinearLayout lltAddImg2;

    @BindView(R.id.btnUploadVideo)
    Button btnUploadVideo;

    @BindView(R.id.lltAddVideo)
    LinearLayout lltAddVideo;

    @BindView(R.id.btnPub)
    Button btnPub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_pub_act);
        ButterKnife.bind(this);

        initView();

    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("添加众筹");
        btnBack.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnUploadImg1.setOnClickListener(this);
        btnUploadImg2.setOnClickListener(this);
        btnUploadVideo.setOnClickListener(this);
        btnPub.setOnClickListener(this);

    }

    private void request() {
        String title = etName.getText().toString();
        String text = etContent.getText().toString();
        String rmb = etAmount.getText().toString();
        String target_rmb = etGoal.getText().toString();

        if (TextUtils.isEmpty(title)) {
            App.toast(this, "商品名不能为空");
            return;
        }

        if (TextUtils.isEmpty(text)) {
            App.toast(this, "详细内容不能为空");
            return;
        }

        if (TextUtils.isEmpty(rmb)) {
            App.toast(this, "众筹金额不能为空");
            return;
        }

        if (TextUtils.isEmpty(target_rmb)) {
            App.toast(this, "目标金额不能为空");
            return;
        }


    }


    private void postPub(String title, String text, String video_link, String aptitude_img_link,
                         String img_link, String stop_time, String rmb, String target_rmb, String id) {

        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("text", text);
        map.put("video_link", video_link);
        map.put("aptitude_img_link", aptitude_img_link);
        map.put("img_link", img_link);
        map.put("stop_time", stop_time);
        map.put("rmb", rmb);
        map.put("target_rmb", target_rmb);
        map.put("id", id);//修改时使用
        AthService service = App.get().getAthService();
        service.crowd_funding_add(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (baseResponse.status == 1) {
                        App.toast(AddFundAct.this, baseResponse.message);
                    } else {
                        App.toast(AddFundAct.this, baseResponse.message);
                    }
                } else {
                    App.toast(AddFundAct.this, "添加失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e("添加失败");
            }
        });

    }

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
        btnBack.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnUploadImg1.setOnClickListener(this);
        btnUploadImg2.setOnClickListener(this);
        btnUploadVideo.setOnClickListener(this);
        btnPub.setOnClickListener(this);

        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.tvEndDate:
                showDatePickDlg();
                break;
            case R.id.btnUploadImg1:
                break;
            case R.id.btnUploadImg2:
                break;
            case R.id.btnUploadVideo:
                break;
            case R.id.btnPub:
                break;

        }
    }

    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvEndDate.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

}
