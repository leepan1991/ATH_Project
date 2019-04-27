package cn.innovativest.ath.ui.act;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.CommentAdapter;
import cn.innovativest.ath.bean.Comment;
import cn.innovativest.ath.bean.EComment;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.CommentResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.widget.VpSwipeRefreshLayout;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class HotDetailAct extends BaseAct implements OnRefreshListener, OnLoadMoreListener {


    @BindView(R.id.swipeRefresh)
    VpSwipeRefreshLayout swipeRefresh;

    @BindView(R.id.detail_player)
    ListGSYVideoPlayer detail_player;

    @BindView(R.id.tvFundName)
    TextView tvFundName;

    @BindView(R.id.tvImgTx3)
    TextView tvImgTx3;

    @BindView(R.id.btnPub)
    Button btnPub;

    @BindView(R.id.xlvCoin)
    XListView xlvCoin;

    @BindView(R.id.et_msg)
    EditText etMsg;

    private int pi = 1;

    private String id;

    private String name;

    private String video;

    private EComment eComment;

    private boolean bChatEnable = false;

    private CommentAdapter commentAdapter;
    private List<Comment> lstComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_hot_detail_act);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        video = getIntent().getStringExtra("video");
        initView();
    }

    private void initView() {
        lstComments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, lstComments);
        xlvCoin.setAdapter(commentAdapter);
        tvFundName.setText(name);

        List<GSYVideoModel> urls = new ArrayList<>();
        urls.add(new GSYVideoModel(video, name));
        detail_player.setUp(urls, true, 0);
        detail_player.setIsTouchWiget(true);
        //关闭自动旋转
        detail_player.setRotateViewAuto(false);
        detail_player.setLockLand(false);
        detail_player.setShowFullAnimation(false);
        //detailPlayer.setNeedLockFull(true);
        detail_player.setAutoFullWithSize(true);

        tvImgTx3.setOnClickListener(this);
        btnPub.setOnClickListener(this);
        swipeRefresh.setVisibility(View.VISIBLE);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setOnLoadMoreListener(this);

        etMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    //处理事件
                    if (TextUtils.isEmpty(v.getText().toString())) {
                        return false;
                    }
                    postMsg(id, v.getText().toString());
                    v.setText("");
                }
                return false;
            }
        });

        pi = 1;
        request(id, pi);
    }

    private void changeChatStatus(boolean enable) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bChatEnable = enable;
        if (bChatEnable) {
            etMsg.setVisibility(View.VISIBLE);
            etMsg.requestFocus();
            //打开软键盘
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            //关闭软键盘
            imm.hideSoftInputFromWindow(etMsg.getWindowToken(), 0);
            etMsg.clearFocus();
            etMsg.setVisibility(View.GONE);
        }
    }

    private void request(String id, int page) {
        //获取数据
        LogUtils.e("======" + page);
        getCommentData(id, page);
    }

    private void initDataToView(List<Comment> coinItems) {
        if (pi == 1) {
            lstComments.clear();
        }
        lstComments.addAll(coinItems);
        commentAdapter.notifyDataSetChanged();
    }

    private void getCommentData(String id, int page) {

        AthService service = App.get().getAthService();
        service.getLyList(id, page).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<CommentResponse>() {
            @Override
            public void call(CommentResponse commentResponse) {
                if (commentResponse != null) {
                    if (commentResponse.eComment != null) {
                        if (commentResponse.eComment.getLstComments() != null && commentResponse.eComment.getLstComments().size() > 0) {
                            eComment = commentResponse.eComment;
                            initDataToView(commentResponse.eComment.getLstComments());
                        }
                    } else {
                        App.toast(HotDetailAct.this, commentResponse.message);
                    }
                } else {
                    App.toast(HotDetailAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                if (pi > 1) {
                    pi--;
                }
            }
        });

    }

    private void postMsg(String id, String text) {
        if (TextUtils.isEmpty(text)) {
            App.toast(HotDetailAct.this, "请输入评论内容");
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
                        App.toast(HotDetailAct.this, baseResponse.message);
                    } else {
                        App.toast(HotDetailAct.this, baseResponse.message);
                    }
                } else {
                    App.toast(HotDetailAct.this, "留言失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                App.toast(HotDetailAct.this, "留言失败");
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (bChatEnable) {
            changeChatStatus(false);
            return;
        }
        super.onBackPressed();
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
        switch (v.getId()) {
            case R.id.tvImgTx3:
                break;

            case R.id.btnPub:
                changeChatStatus(!bChatEnable);
                break;
        }
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishLoadMore();
        pi++;
        LogUtils.e("&&&&&&&&&& " + pi);
        if (eComment != null) {
            if (pi <= eComment.total) {
                request(id, pi);
            } else {
                refreshLayout.setNoMoreData(true);
                refreshLayout.setEnableLoadMore(false);
                App.toast(HotDetailAct.this, "没有更多内容了！");
                pi--;
            }
        } else {
            pi--;
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        refreshLayout.finishRefresh();
        refreshLayout.setNoMoreData(false);
        refreshLayout.setEnableLoadMore(true);
        pi = 1;
        request(id, pi);
    }
}
