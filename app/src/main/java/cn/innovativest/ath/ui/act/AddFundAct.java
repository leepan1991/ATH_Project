package cn.innovativest.ath.ui.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.alipay.sdk.app.PayTask;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.ImgAdapter;
import cn.innovativest.ath.adapter.VideoAdapter;
import cn.innovativest.ath.bean.UploadBean;
import cn.innovativest.ath.bean.VideoItem;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.UploadResponse;
import cn.innovativest.ath.response.UserInfoResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.CUtils;
import cn.innovativest.ath.utils.GifSizeFilter;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PayResult;
import cn.innovativest.ath.widget.CustomDialog;
import cn.innovativest.ath.widget.XGridView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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

//    @BindView(R.id.ivImg1)
//    ImageView ivImg1;
//    @BindView(R.id.ivImg2)
//    ImageView ivImg2;
//    @BindView(R.id.ivImg3)
//    ImageView ivImg3;
//    @BindView(R.id.ivImg4)
//    ImageView ivImg4;
//    @BindView(R.id.ivImg5)
//    ImageView ivImg5;
//
//    @BindView(R.id.ivAddImg1)
//    ImageView ivAddImg1;

    @BindView(R.id.appGird)
    XGridView appGird;

    List<String> lstPath1;
    ImgAdapter imgAdapter1;

    @BindView(R.id.appGird1)
    XGridView appGird2;
    List<String> lstPath2;
    ImgAdapter imgAdapter2;

    @BindView(R.id.appGird2)
    XGridView appGird3;
    List<VideoItem> lstPath3;
    VideoAdapter videoAdapter;

//    @BindView(R.id.ivImg6)
//    ImageView ivImg6;
//    @BindView(R.id.ivImg7)
//    ImageView ivImg7;
//    @BindView(R.id.ivImg8)
//    ImageView ivImg8;
//    @BindView(R.id.ivImg9)
//    ImageView ivImg9;
//    @BindView(R.id.ivImg10)
//    ImageView ivImg10;
//
//    @BindView(R.id.ivAddImg2)
//    ImageView ivAddImg2;

//    @BindView(R.id.detail_player1)
//    ListGSYVideoPlayer detail_player1;
//    @BindView(R.id.detail_player2)
//    ListGSYVideoPlayer detail_player2;
//    @BindView(R.id.detail_player3)
//    ListGSYVideoPlayer detail_player3;
//    @BindView(R.id.detail_player4)
//    ListGSYVideoPlayer detail_player4;
//    @BindView(R.id.detail_player5)
//    ListGSYVideoPlayer detail_player5;

//    @BindView(R.id.ivAddImg3)
//    ImageView ivAddImg3;

    String video_link;
    String img_link;
    String aptitude_img_link;

    @BindView(R.id.btnPub)
    Button btnPub;

    UploadBean uploadBean;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_pub_act);
        ButterKnife.bind(this);

        initView();
        initListener();

    }

    private void initView() {

        uploadBean = new UploadBean();
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("添加众筹");
        btnBack.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);
        btnUploadImg1.setOnClickListener(this);
        btnUploadImg2.setOnClickListener(this);
        btnUploadVideo.setOnClickListener(this);
        btnPub.setOnClickListener(this);

        initAdapter();

    }

    private void initAdapter() {
        lstPath1 = new ArrayList<>();
        lstPath1.add("http://addimg");
        imgAdapter1 = new ImgAdapter(AddFundAct.this, lstPath1);
        appGird.setAdapter(imgAdapter1);
        appGird.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lstPath1.size() < 5) {
                    if (position == lstPath1.size() - 1) {
                        getPermission(1);
                    } else {
                        //查看大图
                    }
                }
            }
        });

        lstPath2 = new ArrayList<>();
        lstPath2.add("http://addimg");
        imgAdapter2 = new ImgAdapter(AddFundAct.this, lstPath2);
        appGird2.setAdapter(imgAdapter2);
        appGird2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lstPath2.size() < 5) {
                    if (position == lstPath2.size() - 1) {
                        getPermission(2);
                    } else {
                        //查看大图
                    }
                }
            }
        });

        lstPath3 = new ArrayList<>();
        VideoItem videoItem = new VideoItem();
        videoItem.isVideo = false;
        videoItem.path = "http://addvideo";
        lstPath3.add(videoItem);
        videoAdapter = new VideoAdapter(AddFundAct.this, lstPath3);
        appGird3.setAdapter(videoAdapter);
        appGird3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lstPath3.size() < 5) {
                    if (position == lstPath3.size() - 1) {
                        getPermission(3);
                    } else {
                        //查看大图
                    }
                }
            }
        });

    }

    private void initListener() {
//        ivImg1.setOnClickListener(this);
//        ivImg2.setOnClickListener(this);
//        ivImg3.setOnClickListener(this);
//        ivImg4.setOnClickListener(this);
//        ivImg5.setOnClickListener(this);
//        ivImg6.setOnClickListener(this);
//        ivImg7.setOnClickListener(this);
//        ivImg8.setOnClickListener(this);
//        ivImg9.setOnClickListener(this);
//        ivImg10.setOnClickListener(this);
//        ivAddImg1.setOnClickListener(this);
//        ivAddImg2.setOnClickListener(this);
//        ivAddImg3.setOnClickListener(this);
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

        if (tvEndDate.getText().toString().equals("结束日期")) {
            App.toast(this, "请选择结束日期");
            return;
        }

        if (!TextUtils.isEmpty(uploadBean.img1)) {
            img_link = uploadBean.img1;
        }

        if (!TextUtils.isEmpty(uploadBean.img2)) {
            img_link = img_link + "|" + uploadBean.img2;
        }

        if (!TextUtils.isEmpty(uploadBean.img3)) {
            img_link = img_link + "|" + uploadBean.img3;
        }

        if (!TextUtils.isEmpty(uploadBean.img4)) {
            img_link = img_link + "|" + uploadBean.img4;
        }

        if (!TextUtils.isEmpty(uploadBean.img5)) {
            img_link = img_link + "|" + uploadBean.img5;
        }

        if (!TextUtils.isEmpty(uploadBean.img6)) {
            aptitude_img_link = uploadBean.img6;
        }

        if (!TextUtils.isEmpty(uploadBean.img7)) {
            aptitude_img_link = aptitude_img_link + "|" + uploadBean.img7;
        }

        if (!TextUtils.isEmpty(uploadBean.img8)) {
            aptitude_img_link = aptitude_img_link + "|" + uploadBean.img8;
        }

        if (!TextUtils.isEmpty(uploadBean.img9)) {
            aptitude_img_link = aptitude_img_link + "|" + uploadBean.img9;
        }

        if (!TextUtils.isEmpty(uploadBean.img10)) {
            aptitude_img_link = aptitude_img_link + "|" + uploadBean.img10;
        }

        if (!TextUtils.isEmpty(uploadBean.video1)) {
            video_link = uploadBean.video1;
        }

        if (!TextUtils.isEmpty(uploadBean.video2)) {
            video_link = video_link + "|" + uploadBean.video2;
        }

        if (!TextUtils.isEmpty(uploadBean.video3)) {
            video_link = video_link + "|" + uploadBean.video3;
        }

        if (!TextUtils.isEmpty(uploadBean.video4)) {
            video_link = video_link + "|" + uploadBean.video4;
        }

        if (!TextUtils.isEmpty(uploadBean.video5)) {
            video_link = video_link + "|" + uploadBean.video5;
        }


        if (TextUtils.isEmpty(img_link)) {
            App.toast(this, "请上传众筹图片");
            return;
        }

        postPub(title, text, video_link, aptitude_img_link,
                img_link, tvEndDate.getText().toString(), rmb, target_rmb);
    }

    private void addImg(List<String> lstPath, boolean isFirst) {
        LoadingUtils.getInstance().dialogShow(this, "提交中...");
        MultipartBody.Builder body = new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);

        for (int i = 0; i < (lstPath.size() == 5 ? lstPath.size() : lstPath.size() - 1); i++) {

            File file = new File(lstPath.get(i));
            if (isFirst) {
                body.addFormDataPart("img" + (i + 1), file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
            } else {
                body.addFormDataPart("img" + (i + 6), file.getName(), RequestBody.create(MediaType.parse("image/jpg"), file));
            }

        }
        AthService service = App.get().getAthService();
        service.uploads(body.build()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UploadResponse>() {
            @Override
            public void call(UploadResponse uploadResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (uploadResponse != null) {
                    if (uploadResponse.status == 1) {
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img1)) {
                            uploadBean.img1 = uploadResponse.uploadBean.img1;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img2)) {
                            uploadBean.img2 = uploadResponse.uploadBean.img2;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img3)) {
                            uploadBean.img3 = uploadResponse.uploadBean.img3;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img4)) {
                            uploadBean.img4 = uploadResponse.uploadBean.img4;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img5)) {
                            uploadBean.img5 = uploadResponse.uploadBean.img5;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img6)) {
                            uploadBean.img6 = uploadResponse.uploadBean.img6;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img7)) {
                            uploadBean.img7 = uploadResponse.uploadBean.img7;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img8)) {
                            uploadBean.img8 = uploadResponse.uploadBean.img8;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img9)) {
                            uploadBean.img9 = uploadResponse.uploadBean.img9;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.img10)) {
                            uploadBean.img10 = uploadResponse.uploadBean.img10;
                        }

                        App.toast(AddFundAct.this, uploadResponse.message);

                    } else {
                        App.toast(AddFundAct.this, uploadResponse.message);
                    }
                } else {
                    App.toast(AddFundAct.this, "上传图片失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                LogUtils.e("上传图片失败");
            }
        });
    }

    private void addVideo(List<VideoItem> lstPath) {
        LoadingUtils.getInstance().dialogShow(this, "提交中...");
        MultipartBody.Builder body = new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);

        for (int i = 0; i < (lstPath.size() == 5 ? lstPath.size() : lstPath.size() - 1); i++) {
            File file = new File(lstPath.get(i).path);
            body.addFormDataPart("video" + (i + 1), file.getName(), RequestBody.create(MediaType.parse("video/mp4"), file));
        }
        AthService service = App.get().getAthService();
        service.uploads(body.build()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<UploadResponse>() {
            @Override
            public void call(UploadResponse uploadResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                if (uploadResponse != null) {
                    if (uploadResponse.status == 1) {
                        App.toast(AddFundAct.this, uploadResponse.message);
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.video1)) {
                            uploadBean.video1 = uploadResponse.uploadBean.video1;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.video2)) {
                            uploadBean.video2 = uploadResponse.uploadBean.video2;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.video3)) {
                            uploadBean.video3 = uploadResponse.uploadBean.video3;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.video4)) {
                            uploadBean.video4 = uploadResponse.uploadBean.video4;
                        }
                        if (!TextUtils.isEmpty(uploadResponse.uploadBean.video5)) {
                            uploadBean.video5 = uploadResponse.uploadBean.video5;
                        }
                    } else {
                        App.toast(AddFundAct.this, uploadResponse.message);
                    }
                } else {
                    App.toast(AddFundAct.this, "上传视频失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                LogUtils.e("上传视频失败");
            }
        });
    }

    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime() / 1000;
        res = String.valueOf(ts);
        return res;
    }


    private void postPub(String title, String text, String video_link, String aptitude_img_link,
                         String img_link, String stop_time, String rmb, String target_rmb) {

        HashMap<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("text", text);
        map.put("video_link", video_link);
        map.put("aptitude_img_link", aptitude_img_link);
        map.put("img_link", img_link);
        map.put("stop_time", dateToStamp(stop_time));
        map.put("rmb", rmb);
        map.put("target_rmb", target_rmb);
        AthService service = App.get().getAthService();
        service.crowd_funding_add(map).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    if (baseResponse.status == 1) {
                        App.toast(AddFundAct.this, baseResponse.message);
                        finish();
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
                addImg(lstPath1, true);
                break;
            case R.id.btnUploadImg2:
                addImg(lstPath2, false);
                break;
            case R.id.btnUploadVideo:
                addVideo(lstPath3);
                break;
            case R.id.btnPub:
                request();
                break;
//            case R.id.ivImg1:
//                break;
//            case R.id.ivImg2:
//                break;
//            case R.id.ivImg3:
//                break;
//            case R.id.ivImg4:
//                break;
//            case R.id.ivImg5:
//                break;
//            case R.id.ivImg6:
//                break;
//            case R.id.ivImg7:
//                break;
//            case R.id.ivImg8:
//                break;
//            case R.id.ivImg9:
//                break;
//            case R.id.ivImg10:
//                break;
//            case R.id.ivAddImg1:
//                getPermission();
//                break;
//            case R.id.ivAddImg2:
//                break;
//            case R.id.ivAddImg3:
//                break;


        }
    }

    private final int REQUEST_READ_PHONE_STATE = 100;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int REQUEST_CODE_CHOOSE1 = 24;
    private static final int REQUEST_CODE_CHOOSE2 = 25;

    private void getPermission(int flag) {
        // 申请单个权限。
        AndPermission.with(this)
                .requestCode(REQUEST_READ_PHONE_STATE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        if (requestCode == REQUEST_READ_PHONE_STATE) {
                            if (flag == 1) {
                                Matisse.from(AddFundAct.this)
                                        .choose(MimeType.ofImage())
                                        .theme(R.style.Matisse_Dracula)
                                        .countable(true)
                                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                        .maxSelectable(5)
                                        .originalEnable(true)
                                        .maxOriginalSize(10)
                                        .imageEngine(new PicassoEngine())
                                        .forResult(REQUEST_CODE_CHOOSE);
                            } else if (flag == 2) {
                                Matisse.from(AddFundAct.this)
                                        .choose(MimeType.ofImage())
                                        .theme(R.style.Matisse_Dracula)
                                        .countable(true)
                                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                        .maxSelectable(5)
                                        .originalEnable(true)
                                        .maxOriginalSize(10)
                                        .imageEngine(new PicassoEngine())
                                        .forResult(REQUEST_CODE_CHOOSE1);
                            } else if (flag == 3) {
                                Matisse.from(AddFundAct.this)
                                        .choose(MimeType.ofVideo())
                                        .theme(R.style.Matisse_Dracula)
                                        .countable(true)
                                        .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                        .maxSelectable(5)
                                        .originalEnable(true)
                                        .maxOriginalSize(10)
                                        .imageEngine(new PicassoEngine())
                                        .forResult(REQUEST_CODE_CHOOSE2);
                            }
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

                    }
                })
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(AddFundAct.this, rationale)
                                .show();
                    }
                })
                .start();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            List<String> lstPath = Matisse.obtainPathResult(data);
            if (lstPath.size() == 5) {
                lstPath1.clear();
                lstPath1.addAll(lstPath);
            } else {
                lstPath1.clear();
                lstPath1.addAll(lstPath);
                lstPath1.add("http://addImg");
            }
            imgAdapter1.onRefresh(lstPath1);

        } else if (requestCode == REQUEST_CODE_CHOOSE1 && resultCode == RESULT_OK) {
            List<String> lstPath = Matisse.obtainPathResult(data);
            if (lstPath.size() == 5) {
                lstPath2.clear();
                lstPath2.addAll(lstPath);
            } else {
                lstPath2.clear();
                lstPath2.addAll(lstPath);
                lstPath2.add("http://addImg");
            }
            imgAdapter2.onRefresh(lstPath2);
        } else if (requestCode == REQUEST_CODE_CHOOSE2 && resultCode == RESULT_OK) {
            List<String> lstPath = Matisse.obtainPathResult(data);
            if (lstPath.size() == 5) {
                lstPath3.clear();
                for (String path : lstPath) {
                    VideoItem videoItem = new VideoItem();
                    videoItem.path = path;
                    videoItem.isVideo = true;
                    lstPath3.add(videoItem);
                }
            } else {
                lstPath3.clear();
                for (String path : lstPath) {
                    VideoItem videoItem = new VideoItem();
                    videoItem.path = path;
                    videoItem.isVideo = true;
                    lstPath3.add(videoItem);
                }
                VideoItem videoItem = new VideoItem();
                videoItem.path = "http://addVideo";
                videoItem.isVideo = false;
                lstPath3.add(videoItem);
            }
            videoAdapter.onRefresh(lstPath3);
        }
    }

    protected void showDatePickDlg() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                tvEndDate.setText(year + "-" + (monthOfYear < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "-" + (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

}
