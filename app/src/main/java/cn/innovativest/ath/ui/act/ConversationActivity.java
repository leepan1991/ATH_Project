package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.utils.CUtils;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.UserInfo;

public class ConversationActivity extends FragmentActivity {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    /**
     * 对方id
     */
    private String mTargetId;

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        if (intent == null || intent.getData() == null) {
            App.toast(ConversationActivity.this, "数据错误");
            return;
        }
        mTargetId = intent.getData().getQueryParameter("targetId");
        if (CUtils.isEmpty(mTargetId)) {
            App.toast(ConversationActivity.this, "数据错误");
            return;
        }

        title = intent.getData().getQueryParameter("title");
        initView();
        setPrivateActionBar(mTargetId);
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 设置私聊界面 ActionBar
     */
    private void setPrivateActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            if (title.equals("null")) {
                if (!TextUtils.isEmpty(targetId)) {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
                    if (userInfo != null) {
                        tvwTitle.setText(userInfo.getName());
                    }
                }
            } else {
                tvwTitle.setText(title);
            }

        } else {
            tvwTitle.setText(targetId);
        }
    }

}
