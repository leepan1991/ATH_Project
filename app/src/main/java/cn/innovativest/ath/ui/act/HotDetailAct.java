package cn.innovativest.ath.ui.act;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.model.GSYVideoModel;
import com.shuyu.gsyvideoplayer.video.ListGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.CommentAdapter;
import cn.innovativest.ath.bean.Comment;
import cn.innovativest.ath.bean.EComment;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.CommentResponse;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.widget.VpSwipeRefreshLayout;
import cn.innovativest.ath.widget.XListView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class HotDetailAct extends GSYBaseActivityDetail<ListGSYVideoPlayer> implements OnRefreshListener, OnLoadMoreListener, View.OnClickListener {


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

    private int pi = 1;

    private String id;

    private String name;

    private String video;

    private String video_img;

    private EComment eComment;

    private CommentAdapter commentAdapter;
    private List<Comment> lstComments;

//    private OrientationUtils orientationUtils;

    private boolean isPlay;
    private boolean isPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_hot_detail_act);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra("id");
        name = getIntent().getStringExtra("name");
        video = getIntent().getStringExtra("video");
        video_img = getIntent().getStringExtra("video_img");
        initVideo();
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
        //增加封面
        ImageView imageView = new ImageView(this);
        GlideApp.with(this).load(video_img).into(imageView);

        detail_player.setThumbImageView(imageView);

        detail_player.setIsTouchWiget(true);
        //关闭自动旋转
        detail_player.setRotateViewAuto(false);
        detail_player.setLockLand(false);
        detail_player.setShowFullAnimation(false);
        //detailPlayer.setNeedLockFull(true);
        detail_player.setAutoFullWithSize(true);

        detail_player.setVideoAllCallBack(this);

        detail_player.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });

//        detail_player.setIsTouchWiget(true);
        //关闭自动旋转
//        detail_player.setRotateViewAuto(false);
//        detail_player.setLockLand(false);
//        detail_player.setShowFullAnimation(false);
//        detail_player.setAutoFullWithSize(true);
//        //外部辅助的旋转，帮助全屏
//        orientationUtils = new OrientationUtils(this, detail_player);
//        //初始化不打开外部的旋转
//        orientationUtils.setEnable(false);

//        GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
//        gsyVideoOption.setThumbImageView(imageView)
//        gsyVideoOption
//                .setIsTouchWiget(true)
//                .setRotateViewAuto(false)
//                .setLockLand(false)
//                .setAutoFullWithSize(true)
//                .setShowFullAnimation(false)
//                .setNeedLockFull(true)
//                .setUrl(video)
////                .setMapHeadData(header)
//                .setCacheWithPlay(false)
//                .setVideoTitle(name)
//                .setVideoAllCallBack(new GSYSampleCallBack() {
//                    @Override
//                    public void onPrepared(String url, Object... objects) {
//                        Debuger.printfError("***** onPrepared **** " + objects[0]);
//                        Debuger.printfError("***** onPrepared **** " + objects[1]);
//                        super.onPrepared(url, objects);
//                        //开始播放了才能旋转和全屏
//                        orientationUtils.setEnable(true);
//                        isPlay = true;
//                    }
//
//                    @Override
//                    public void onEnterFullscreen(String url, Object... objects) {
//                        super.onEnterFullscreen(url, objects);
//                        Debuger.printfError("***** onEnterFullscreen **** " + objects[0]);//title
//                        Debuger.printfError("***** onEnterFullscreen **** " + objects[1]);//当前全屏player
//                    }
//
//                    @Override
//                    public void onAutoComplete(String url, Object... objects) {
//                        super.onAutoComplete(url, objects);
//                    }
//
//                    @Override
//                    public void onClickStartError(String url, Object... objects) {
//                        super.onClickStartError(url, objects);
//                    }
//
//                    @Override
//                    public void onQuitFullscreen(String url, Object... objects) {
//                        super.onQuitFullscreen(url, objects);
//                        Debuger.printfError("***** onQuitFullscreen **** " + objects[0]);//title
//                        Debuger.printfError("***** onQuitFullscreen **** " + objects[1]);//当前非全屏player
//                        if (orientationUtils != null) {
//                            orientationUtils.backToProtVideo();
//                        }
//                    }
//                })
//                .setLockClickListener(new LockClickListener() {
//                    @Override
//                    public void onClick(View view, boolean lock) {
//                        if (orientationUtils != null) {
//                            //配合下方的onConfigurationChanged
//                            orientationUtils.setEnable(!lock);
//                        }
//                    }
//                })
//                .setGSYVideoProgressListener(new GSYVideoProgressListener() {
//                    @Override
//                    public void onProgress(int progress, int secProgress, int currentPosition, int duration) {
//                        Debuger.printfLog(" progress " + progress + " secProgress " + secProgress + " currentPosition " + currentPosition + " duration " + duration);
//                    }
//                })
//                .build(detail_player);
//
//        detail_player.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //直接横屏
//                orientationUtils.resolveByClick();
//
//                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
//                detail_player.startWindowFullscreen(HotDetailAct.this, true, true);
//            }
//        });

        detail_player.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvImgTx3.setOnClickListener(this);
        btnPub.setOnClickListener(this);
        swipeRefresh.setVisibility(View.VISIBLE);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setOnLoadMoreListener(this);


        pi = 1;
        request(id, pi);
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
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }

        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isPlay) {
            getCurPlay().release();
        }
        //GSYPreViewManager.instance().releaseMediaPlayer();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    private GSYVideoPlayer getCurPlay() {
        if (detail_player.getFullWindowPlayer() != null) {
            return detail_player.getFullWindowPlayer();
        }
        return detail_player;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            detail_player.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
        }
    }

    @Override
    public ListGSYVideoPlayer getGSYVideoPlayer() {
        return detail_player;
    }

    @Override
    public GSYVideoOptionBuilder getGSYVideoOptionBuilder() {
        return null;
    }

    @Override
    public void clickForFullScreen() {

    }

    @Override
    public boolean getDetailOrientationRotateAuto() {
        return true;
    }

    @Override
    protected void onResume() {
        getCurPlay().onVideoResume(false);
        super.onResume();
        isPause = false;
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {
        super.onEnterFullscreen(url, objects);
        //隐藏调全屏对象的返回按键
        GSYVideoPlayer gsyVideoPlayer = (GSYVideoPlayer) objects[1];
        gsyVideoPlayer.getBackButton().setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        getCurPlay().onVideoPause();
        super.onPause();
        isPause = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvImgTx3:
                break;

            case R.id.btnPub:
                startActivityForResult(new Intent(HotDetailAct.this, CommentAct.class).putExtra("id", id), 100);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (requestCode == 1) {
                pi = 1;
                request(id, pi);
            }
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
