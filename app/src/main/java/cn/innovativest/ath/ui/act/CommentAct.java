package cn.innovativest.ath.ui.act;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

public class CommentAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.etId)
    EditText etId;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_act);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        initView();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("发布评论");
        btnBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void postMsg(String id, String text) {
        if (TextUtils.isEmpty(text)) {
            App.toast(CommentAct.this, "请输入评论内容");
            return;
        }

        HashMap<String, String> map = new HashMap();
        map.put("id", id);
        map.put("text", text);

        AthService service = App.get().getAthService();
        service.getLyAdd(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (baseResponse.status == 1) {
                        setResult(1);
                        App.toast(CommentAct.this, baseResponse.message);
                        onBackPressed();
                    } else {
                        App.toast(CommentAct.this, baseResponse.message);
                    }
                } else {
                    App.toast(CommentAct.this, "留言失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(CommentAct.this, "留言失败");
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.btnConfirm:
                postMsg(id, etId.getText().toString());
                break;
        }
    }
}
