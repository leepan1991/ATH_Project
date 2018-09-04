package cn.innovativest.ath.ui.act;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.FriendHelpAdapter;
import cn.innovativest.ath.adapter.GiftAdapter;
import cn.innovativest.ath.adapter.TeamAdapter;
import cn.innovativest.ath.bean.FriendHelpItem;
import cn.innovativest.ath.bean.Gift;
import cn.innovativest.ath.bean.UserInfo;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.FriendListResponse;
import cn.innovativest.ath.response.GiftResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class FriendHelpAct extends BaseAct implements RadioGroup.OnCheckedChangeListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.rgTrade)
    RadioGroup rgTrade;

    @BindView(R.id.btnInvite)
    RadioButton btnInvite;

    @BindView(R.id.btnGift)
    RadioButton btnGift;

    @BindView(R.id.btnTeam)
    RadioButton btnTeam;

    @BindView(R.id.lltInvate)
    LinearLayout lltInvate;

    @BindView(R.id.xlvInvate)
    XListView xlvInvate;

    @BindView(R.id.lltGift)
    LinearLayout lltGift;

    @BindView(R.id.xlvGift)
    XListView xlvGift;

    @BindView(R.id.lltTeam)
    LinearLayout lltTeam;

    @BindView(R.id.xlvTeam)
    XListView xlvTeam;

//    @BindView(R.id.tvwGift)
//    TextView tvwGift;
//
//    @BindView(R.id.tvwTeam)
//    TextView tvwTeam;

    @BindView(R.id.tvwCopyContent)
    TextView tvwCopyContent;

    @BindView(R.id.tvwCopy)
    TextView tvwCopy;

    @BindView(R.id.ivBarcode)
    ImageView ivBarcode;

    private String copyContent;

    private FriendHelpAdapter friendHelpAdapter;
    private List<FriendHelpItem> lstFriendHelpItems;

    private GiftAdapter giftAdapter;
    private List<Gift> lstGifts;

    private TeamAdapter teamAdapter;
    private List<Gift> lstTeams;

    UserInfo userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_help_act);
        ButterKnife.bind(this);
        initView();
        getPeopleData();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("邀请好友助力");
        btnBack.setOnClickListener(this);
        tvwCopy.setOnClickListener(this);

        lstFriendHelpItems = new ArrayList<FriendHelpItem>();
        friendHelpAdapter = new FriendHelpAdapter(this, lstFriendHelpItems);
        xlvInvate.setAdapter(friendHelpAdapter);

        lstGifts = new ArrayList<Gift>();
        giftAdapter = new GiftAdapter(this, lstGifts);
        xlvGift.setAdapter(giftAdapter);

        lstTeams = new ArrayList<Gift>();
        teamAdapter = new TeamAdapter(this, lstTeams);
        xlvTeam.setAdapter(teamAdapter);

        lltInvate.setVisibility(View.VISIBLE);
        lltGift.setVisibility(View.GONE);
        lltTeam.setVisibility(View.GONE);

        btnInvite.setChecked(true);
        btnGift.setChecked(false);
        btnTeam.setChecked(false);
        rgTrade.setOnCheckedChangeListener(this);

        if (!CUtils.isEmpty(PrefsManager.get().getString("userinfo"))) {
            userInfo = new Gson().fromJson(AESUtils.decryptData(PrefsManager.get().getString("userinfo")), UserInfo.class);
            tvwCopyContent.setText(userInfo.code);
        }

    }

    private void initDataToView(List<FriendHelpItem> items) {
        lstFriendHelpItems.clear();
        lstFriendHelpItems.addAll(items);
        friendHelpAdapter.notifyDataSetChanged();
    }

    private void initDataGiftToView(boolean isGift, List<Gift> items) {
        if (isGift) {
            lstGifts.clear();
            lstGifts.addAll(items);
            giftAdapter.notifyDataSetChanged();
        } else {
            lstTeams.clear();
            lstTeams.addAll(items);
            teamAdapter.notifyDataSetChanged();
        }

    }


    private void getPeopleData() {
        AthService service = App.get().getAthService();
        service.person_list().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<FriendListResponse>() {
            @Override
            public void call(FriendListResponse friendListResponse) {
                if (friendListResponse != null) {
                    if (friendListResponse.friendHelpBean != null) {
                        if (friendListResponse.friendHelpBean.lstFriendHelpItems.size() > 0) {
                            initDataToView(friendListResponse.friendHelpBean.lstFriendHelpItems);
                        }
                        copyContent = friendListResponse.friendHelpBean.link;
                        if (!CUtils.isEmpty(friendListResponse.friendHelpBean.code)) {
                            tvwCopyContent.setText(friendListResponse.friendHelpBean.code);
                        }
                        GlideApp.with(FriendHelpAct.this).load(friendListResponse.friendHelpBean.barcodeurl).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivBarcode);
                    } else {
                        friendHelpAdapter.notifyDataSetChanged();
                    }
                } else {
                    App.toast(FriendHelpAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(FriendHelpAct.this, "数据获取失败");
            }
        });

    }

    private void getGiftData(final boolean isGift, String type) {
        AthService service = App.get().getAthService();
        service.invite(type).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<GiftResponse>() {
            @Override
            public void call(GiftResponse giftResponse) {
                if (giftResponse != null) {
                    if (giftResponse.lstGifts != null && giftResponse.lstGifts.size() > 0) {
                        initDataGiftToView(isGift, giftResponse.lstGifts);
                    } else {
                        if (isGift) {
                            giftAdapter.notifyDataSetChanged();
                        } else {
                            teamAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    App.toast(FriendHelpAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(FriendHelpAct.this, "数据获取失败");
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
        MobclickAgent.onPageStart("FriendHelpAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("FriendHelpAct");
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
                finish();
                break;
            case R.id.tvwCopy:
                if (!CUtils.isEmpty(copyContent) && userInfo != null && !CUtils.isEmpty(userInfo.code)) {
                    copy("快来我在ATH赚钱了，零投入有手机就行，特邀请您加入ATH生态圈财富直通车领取，邀请码：" + userInfo.code + "，我的邀请次数有限，还有随机红包赶快加入哦~" + copyContent);
                } else {
                    App.toast(FriendHelpAct.this, "信息不能为空，请稍候再试");
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == btnInvite.getId()) { // 切换到邀请人数
            lltInvate.setVisibility(View.VISIBLE);
            lltGift.setVisibility(View.GONE);
            lltTeam.setVisibility(View.GONE);
            getPeopleData();
        } else if (i == btnGift.getId()) { // 切换到邀请有礼
            lltInvate.setVisibility(View.GONE);
            lltGift.setVisibility(View.VISIBLE);
            lltTeam.setVisibility(View.GONE);
            getGiftData(true, "1");
        } else if (i == btnTeam.getId()) {//切换到团队增益
            lltInvate.setVisibility(View.GONE);
            lltGift.setVisibility(View.GONE);
            lltTeam.setVisibility(View.VISIBLE);
            getGiftData(false, "2");
        }
    }
}
