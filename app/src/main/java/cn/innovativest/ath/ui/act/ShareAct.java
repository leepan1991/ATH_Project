package cn.innovativest.ath.ui.act;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;
import cn.innovativest.ath.adapter.ShareTextAdapter;
import cn.innovativest.ath.bean.ImgsItem;
import cn.innovativest.ath.bean.ShareItem;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.response.GiftResponse;
import cn.innovativest.ath.response.ShareResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.AESUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.PrefsManager;
import cn.innovativest.ath.utils.SDUtils;
import cn.innovativest.ath.widget.XListView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.onekeyshare.OnekeyShare;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ShareAct extends BaseAct implements AdapterView.OnItemClickListener, View.OnTouchListener {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.edtContent)
    EditText edtContent;

    @BindView(R.id.btnCopy)
    Button btnCopy;

    @BindView(R.id.lltImgs)
    LinearLayout lltImgs;

    @BindView(R.id.rltImg1)
    RelativeLayout rltImg1;

    @BindView(R.id.rltImg2)
    RelativeLayout rltImg2;

    @BindView(R.id.rltImg3)
    RelativeLayout rltImg3;

    @BindView(R.id.rltImg4)
    RelativeLayout rltImg4;

    @BindView(R.id.ivImg1)
    ImageView ivImg1;

    @BindView(R.id.ivImgCheck1)
    ImageView ivImgCheck1;

    @BindView(R.id.ivImg2)
    ImageView ivImg2;

    @BindView(R.id.ivImgCheck2)
    ImageView ivImgCheck2;

    @BindView(R.id.ivImg3)
    ImageView ivImg3;

    @BindView(R.id.ivImgCheck3)
    ImageView ivImgCheck3;

    @BindView(R.id.ivImg4)
    ImageView ivImg4;

    @BindView(R.id.ivImgCheck4)
    ImageView ivImgCheck4;

    @BindView(R.id.xlvShare)
    XListView xlvShare;

    @BindView(R.id.btnShare)
    Button btnShare;

    private ShareTextAdapter shareTextAdapter;
    private List<ImgsItem> lstImgTexts;

    ShareItem shareItem;

    private PopupWindow selectWindow = null;

    TextView tvNotice;

    // 保存图片本地路径
    public static final String ACCOUNT_DIR = AppConfig.ATH_CACHE_PIC_PATH
            + "/account/";
    public static final String ACCOUNT_MAINTRANCE_ICON_CACHE = "icon_cache/";
    private static final String IMGPATH = ACCOUNT_DIR
            + ACCOUNT_MAINTRANCE_ICON_CACHE;

    private static final String IMAGE_FILE_NAME = "share.jpg";
    private static final String TMP_IMAGE_FILE_NAME = "tmp_share.jpg";

    // 常量定义
    public static final int TAKE_A_PICTURE = 10;
    public static final int SELECT_A_PICTURE = 20;
    public static final int SET_PICTURE = 30;
    public static final int SET_ALBUM_PICTURE_KITKAT = 40;
    public static final int SELECET_A_PICTURE_AFTER_KIKAT = 50;
    private String mAlbumPicturePath = null;

    private File fileone = null;
    private File filetwo = null;

    // 版本比较：是否是4.4及以上版本
    final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    File file = null;

    private boolean isCheck1 = false;
    private boolean isCheck2 = false;
    private boolean isCheck3 = false;
    private boolean isCheck4 = false;

    String shareCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_act);
        ButterKnife.bind(this);
        shareCode = getIntent().getStringExtra("shareCode");
        if (TextUtils.isEmpty(shareCode)) {
            finish();
            return;
        }
        initView();
        initFile();
    }

    private void initFile() {
        File directory = new File(ACCOUNT_DIR);
        File imagepath = new File(IMGPATH);
        if (!directory.exists()) {
            LogUtils.i("directory.mkdir()");
            directory.mkdir();
        }
        if (!imagepath.exists()) {
            LogUtils.i("imagepath.mkdir()");
            imagepath.mkdir();
        }

        fileone = new File(IMGPATH, IMAGE_FILE_NAME);
        filetwo = new File(IMGPATH, TMP_IMAGE_FILE_NAME);

        try {
            if (fileone != null && fileone.exists()) {
                SDUtils.deleteFile(fileone, true);
            }
            if (filetwo != null && filetwo.exists()) {
                SDUtils.deleteFile(filetwo, true);
            }
            if (!fileone.exists() && !filetwo.exists()) {
                fileone.createNewFile();
                filetwo.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("创建分享");
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        edtContent.setOnTouchListener(this);
        lstImgTexts = new ArrayList<ImgsItem>();
        shareTextAdapter = new ShareTextAdapter(this, lstImgTexts, handler);
        xlvShare.setAdapter(shareTextAdapter);
        xlvShare.setOnItemClickListener(this);
        btnCopy.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        rltImg1.setOnClickListener(this);
        rltImg2.setOnClickListener(this);
        rltImg3.setOnClickListener(this);
        rltImg4.setOnClickListener(this);
        PrefsManager.get().save("share", "");
        getData();

        isCheck1 = true;
        isCheck2 = false;
        isCheck3 = false;
        isCheck4 = false;
        ivImgCheck1.setImageResource(R.drawable.ic_share_checked);
        ivImgCheck2.setImageResource(R.drawable.ic_share_check);
        ivImgCheck3.setImageResource(R.drawable.ic_share_check);
        if (file != null) {
            ivImgCheck4.setVisibility(View.VISIBLE);
            ivImgCheck4.setImageResource(R.drawable.ic_share_check);
        } else {
            ivImgCheck4.setVisibility(View.INVISIBLE);
        }
    }

    private void initDataToImgView(List<ImgsItem> lstImgs) {

        if (lstImgs != null && lstImgs.size() == 3) {
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + lstImgs.get(0).text).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivImg1);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + lstImgs.get(1).text).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivImg2);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + lstImgs.get(2).text).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivImg3);
            rltImg1.setVisibility(View.VISIBLE);
            rltImg2.setVisibility(View.VISIBLE);
            rltImg3.setVisibility(View.VISIBLE);
            rltImg4.setVisibility(View.VISIBLE);
        } else if (lstImgs != null && lstImgs.size() == 2) {
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + lstImgs.get(0).text).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivImg1);
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + lstImgs.get(1).text).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivImg2);
            rltImg1.setVisibility(View.VISIBLE);
            rltImg2.setVisibility(View.VISIBLE);
            rltImg3.setVisibility(View.GONE);
            rltImg4.setVisibility(View.VISIBLE);
        } else if (lstImgs != null && lstImgs.size() == 1) {
            GlideApp.with(this).load(AppConfig.ATH_APP_URL + lstImgs.get(0).text).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(ivImg1);
            rltImg1.setVisibility(View.VISIBLE);
            rltImg2.setVisibility(View.GONE);
            rltImg3.setVisibility(View.GONE);
            rltImg4.setVisibility(View.VISIBLE);
        } else {
            rltImg1.setVisibility(View.GONE);
            rltImg2.setVisibility(View.GONE);
            rltImg3.setVisibility(View.GONE);
            rltImg4.setVisibility(View.VISIBLE);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    ImgsItem imgsItem = new Gson().fromJson(PrefsManager.get().getString("share"), ImgsItem.class);
                    if (imgsItem != null && !TextUtils.isEmpty(imgsItem.text)) {
                        edtContent.setText(imgsItem.text.replace("#####", shareCode));
                        edtContent.setSelection((imgsItem.text.replace("#####", shareCode)).length());
                    }
                    break;
            }
        }
    };

    private void initDataToTextView(List<ImgsItem> imgItems) {
        lstImgTexts.clear();
        lstImgTexts.addAll(imgItems);
        shareTextAdapter.notifyDataSetChanged();

    }

    private void getData() {

        AthService service = App.get().getAthService();
        service.share().observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<ShareResponse>() {
            @Override
            public void call(ShareResponse shareResponse) {
                if (shareResponse != null) {
                    if (shareResponse.shareItem != null) {
                        shareItem = shareResponse.shareItem;
                        if (shareResponse.shareItem.lstImgs != null && shareResponse.shareItem.lstImgs.size() > 0) {
                            ArrayList<ImgsItem> lstImgs = new ArrayList<ImgsItem>();
                            if (shareResponse.shareItem.lstImgs.size() > 3) {
                                lstImgs.addAll(shareResponse.shareItem.lstImgs.subList(0, 3));
                            } else {
                                lstImgs.addAll(shareResponse.shareItem.lstImgs);
                            }
                            initDataToImgView(lstImgs);
                        }

                        if (shareResponse.shareItem.lstTexts != null && shareResponse.shareItem.lstTexts.size() > 0) {
                            initDataToTextView(shareResponse.shareItem.lstTexts);
                        }

                    } else {
                        App.toast(ShareAct.this, shareResponse.message);
                    }
                } else {
                    App.toast(ShareAct.this, "数据获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
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
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCopy:
                if (!TextUtils.isEmpty(edtContent.getText().toString())) {
                    copy(edtContent.getText().toString());
                } else {
                    App.toast(ShareAct.this, "文本不能为空，请输入");
                }
                break;
            case R.id.btnShare:
                if (!TextUtils.isEmpty(edtContent.getText().toString())) {
                    if (isCheck4) {
                        showShare(edtContent.getText().toString(), file.getPath());
                    } else {
                        if (isCheck1) {
                            showShare(edtContent.getText().toString(), AppConfig.ATH_APP_URL + shareItem.lstImgs.get(0).text);
                        }

                        if (isCheck2) {
                            showShare(edtContent.getText().toString(), AppConfig.ATH_APP_URL + shareItem.lstImgs.get(1).text);
                        }

                        if (isCheck3) {
                            showShare(edtContent.getText().toString(), AppConfig.ATH_APP_URL + shareItem.lstImgs.get(2).text);
                        }
                    }
                } else {
                    App.toast(this, "请选择或输入分享内容");
                }

                break;
            case R.id.rltImg1:
                isCheck1 = true;
                isCheck2 = false;
                isCheck3 = false;
                isCheck4 = false;
                ivImgCheck1.setImageResource(R.drawable.ic_share_checked);
                ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                if (file != null) {
                    ivImgCheck4.setVisibility(View.VISIBLE);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_check);
                } else {
                    ivImgCheck4.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.rltImg2:
                isCheck1 = false;
                isCheck2 = true;
                isCheck3 = false;
                isCheck4 = false;
                ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                ivImgCheck2.setImageResource(R.drawable.ic_share_checked);
                ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                if (file != null) {
                    ivImgCheck4.setVisibility(View.VISIBLE);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_check);
                } else {
                    ivImgCheck4.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.rltImg3:
                isCheck1 = false;
                isCheck2 = false;
                isCheck3 = true;
                isCheck4 = false;
                ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                ivImgCheck3.setImageResource(R.drawable.ic_share_checked);
                if (file != null) {
                    ivImgCheck4.setVisibility(View.VISIBLE);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_check);
                } else {
                    ivImgCheck4.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.rltImg4:
                ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                if (file != null) {
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    isCheck4 = true;
                    ivImgCheck4.setVisibility(View.VISIBLE);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_checked);
                } else {
                    ivImgCheck4.setVisibility(View.INVISIBLE);
                }
                showSelectWindow();
                break;
            case R.id.btnTakePhotos:// 拍照
                hideSelectWindow();
                take();
                break;
            case R.id.btnLocalPhotos:// 本地图片
                hideSelectWindow();
                selectAlbum();
                break;
            case R.id.btnCancele:// 取消
                hideSelectWindow();
                break;
        }
    }

    public int getSecondTimestampTwo(Date date) {
        if (null == date) {
            return 0;
        }
        String timestamp = String.valueOf(date.getTime() / 1000);
        return Integer.valueOf(timestamp);
    }

    private void showShare(String shareTxt, String urlImg) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();


        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
//        oks.setTitle("ATH分享");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
//        oks.setTitleUrl("http://ath.pub/");
        // text是分享文本，所有平台都需要这个字段
//        oks.setText(shareTxt);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath(imgUrl);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        if (isCheck4) {
            oks.setImagePath(urlImg);
        } else {
            oks.setImageUrl(urlImg);
        }
//        oks.setUrl("http://ath.pub/");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://ath.pub/");

        if (!TextUtils.isEmpty(shareTxt)) {
            copy(shareTxt);
        }

        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                fenxiang(URLEncoder.encode(AESUtils.encryptData(PrefsManager.get().getString("phone") + "+" + AESUtils.encryptData(getSecondTimestampTwo(new Date()) + ""))));
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
// 启动分享GUI
        oks.show(this);
    }

    private void fenxiang(String token) {
        AthService service = App.get().getAthService();
        service.fen_xiang(token).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                if (baseResponse != null) {
                    App.toast(ShareAct.this, baseResponse.message);
                } else {
                    LogUtils.e("分享回调获取失败");
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LogUtils.e(throwable.getMessage());
                LogUtils.e("分享回调获取失败");
            }
        });

    }

    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        App.toast(this, "复制成功");
    }

    /**
     * 该类需要调用
     * OnTouchListener接口
     * 黄色部分是需要更改部分，改为自己的edittext
     **/
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //触摸的是EditText并且当前EditText可以滚动则将事件交给EditText处理；否则将事件交由其父类处理
        if ((view.getId() == R.id.edtContent && canVerticalScroll(edtContent))) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                view.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动  false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ImgsItem imgsItem = lstImgTexts.get(i);
        PrefsManager.get().save("share", App.get().gson.toJson(imgsItem));
        if (!TextUtils.isEmpty(imgsItem.text)) {
            edtContent.setText(imgsItem.text.replace("#####", shareCode));
            edtContent.setSelection((imgsItem.text.replace("#####", shareCode)).length());
        }
        shareTextAdapter.notifyDataSetChanged();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMGPATH, IMAGE_FILE_NAME)));
        startActivityForResult(intent, TAKE_A_PICTURE);
        LogUtils.i("TAKE_A_PICTURE");
    }

    private void selectAlbum() {
        if (mIsKitKat) {
            selectImageUriAfterKikat();
        } else {
            cropImageUri();
        }
    }

    /**
     * Call from Activity#onResume().
     */
    public void take() {
        if (Build.VERSION.SDK_INT >= 23) {
            openCameraWithPermission();
        } else {
            takePhoto();
        }
    }

    private static int cameraPermissionReqCode = 250;

    private boolean askedPermission = false;

    @TargetApi(23)
    private void openCameraWithPermission() {
        if (ContextCompat.checkSelfPermission(mCtx, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else if (!askedPermission) {
            ActivityCompat.requestPermissions(mCtx,
                    new String[]{Manifest.permission.CAMERA},
                    cameraPermissionReqCode);
            askedPermission = true;
        } else {
            // Wait for permission result
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    /**
     * Call from Activity#onRequestPermissionsResult
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == cameraPermissionReqCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted
                takePhoto();
            } else {
                // TODO: display better error message.
//                displayFrameworkBugMessageAndExit();
                App.toast(mCtx, "暂无拍照权限");
            }
        }
    }

    private void instanceSelectWindow() {
        if (selectWindow == null) {
            View view = LayoutInflater.from(ShareAct.this).inflate(
                    R.layout.add_pic_dialog, null);
            tvNotice = (TextView) view.findViewById(R.id.tvNotice);
            tvNotice.setText("请选择分享图片");
            view.findViewById(R.id.btnTakePhotos).setOnClickListener(
                    ShareAct.this);
            view.findViewById(R.id.btnLocalPhotos).setOnClickListener(
                    ShareAct.this);
            view.findViewById(R.id.btnCancele).setOnClickListener(ShareAct.this);
            selectWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            selectWindow.set
//            selectWindow.setContentView(view);
            selectWindow.setAnimationStyle(R.style.AnimBottom);

//            Window window = selectWindow.getWindow();
//            window.setWindowAnimations(R.style.AnimBottom);
//            window.setGravity(Gravity.CENTER);
        }
    }

    private void showSelectWindow() {
        instanceSelectWindow();
        // 在底部显示
        selectWindow.showAtLocation(findViewById(R.id.main),
                Gravity.BOTTOM, 0, 0);
    }

    private void hideSelectWindow() {
        if (selectWindow != null && selectWindow.isShowing()) {
            selectWindow.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        // 拍照后返回
        switch (requestCode) {
            case SELECT_A_PICTURE:
                if (resultCode == RESULT_OK && null != data) {
                    // Log.i("zou", "4.4以下的");
                    file = new File(IMGPATH, TMP_IMAGE_FILE_NAME);
                    bitmap = decodeUriAsBitmap(Uri.fromFile(file));
                    GlideApp.with(this).load(file).optionalCircleCrop().into(ivImg4);
                    ivImg4.setImageBitmap(bitmap);
                    ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_checked);
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    isCheck4 = true;
                    ivImgCheck4.setVisibility(View.VISIBLE);
//                    setImageViewMathParent(this, ivFan, bitmap);
//                    modify(file);//后面打开
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(ShareAct.this, "取消上传", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case SELECET_A_PICTURE_AFTER_KIKAT:
                if (resultCode == RESULT_OK && null != data) {
                    // Log.i("zou", "4.4以上的");
                    mAlbumPicturePath = getPath(getApplicationContext(),
                            data.getData());
//                    cropImageUriAfterKikat(Uri
//                            .fromFile(new File(mAlbumPicturePath)));
                    file = new File(mAlbumPicturePath);
                    bitmap = decodeUriAsBitmap(Uri.fromFile(file));
                    ivImg4.setImageBitmap(bitmap);
                    ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_checked);
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    isCheck4 = true;
                    ivImgCheck4.setVisibility(View.VISIBLE);
//                    setImageViewMathParent(this, ivFan, bitmap);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(ShareAct.this, "取消上传", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case SET_ALBUM_PICTURE_KITKAT:
                Log.i("zou", "4.4以上上的 RESULT_OK");
                file = new File(IMGPATH, TMP_IMAGE_FILE_NAME);
                bitmap = decodeUriAsBitmap(Uri.fromFile(file));
                ivImg4.setImageBitmap(bitmap);
                ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                ivImgCheck4.setImageResource(R.drawable.ic_share_checked);
                isCheck1 = false;
                isCheck2 = false;
                isCheck3 = false;
                isCheck4 = true;
                ivImgCheck4.setVisibility(View.VISIBLE);
//                setImageViewMathParent(this, ivFan, bitmap);
//                modify(file);//后面打开
                break;
            case TAKE_A_PICTURE:
                Log.i("zou", "TAKE_A_PICTURE-resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
//                    cameraCropImageUri(Uri.fromFile(new File(IMGPATH,
//                            IMAGE_FILE_NAME)));
                    file = new File(IMGPATH, IMAGE_FILE_NAME);
                    bitmap = decodeUriAsBitmap(Uri.fromFile(file));
                    ivImg4.setImageBitmap(bitmap);
                    ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_checked);
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    isCheck4 = true;
                    ivImgCheck4.setVisibility(View.VISIBLE);
//                    setImageViewMathParent(this, ivFan, bitmap);
                } else {
                    Toast.makeText(ShareAct.this, "取消上传", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case SET_PICTURE:
                if (resultCode == RESULT_OK && null != data) {
                    file = new File(IMGPATH, IMAGE_FILE_NAME);
                    bitmap = decodeUriAsBitmap(Uri.fromFile(file));
                    ivImg4.setImageBitmap(bitmap);
                    ivImgCheck1.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck2.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck3.setImageResource(R.drawable.ic_share_check);
                    ivImgCheck4.setImageResource(R.drawable.ic_share_checked);
                    isCheck1 = false;
                    isCheck2 = false;
                    isCheck3 = false;
                    isCheck4 = true;
                    ivImgCheck4.setVisibility(View.VISIBLE);
//                    setImageViewMathParent(this, ivFan, bitmap);
//                    modify(file);//后面打开
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(ShareAct.this, "取消上传", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(ShareAct.this, "上传失败", Toast.LENGTH_SHORT)
                            .show();
                }
                // }
                break;
            default:
                break;
        }
    }

    private void dismissAll() {
        hideSelectWindow();
    }

    /**
     * <br>
     * 功能简述:裁剪图片方法实现---------------------- 相册 <br>
     * 功能详细描述: <br>
     * 注意:
     */
    private void cropImageUri() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        int sizeImageHead = (int) getResources().getDimension(R.dimen.photo_w);
//        intent.putExtra("outputX", sizeImageHead);
//        intent.putExtra("outputY", sizeImageHead);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME)));
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, SELECT_A_PICTURE);
    }

    /**
     * <br>
     * 功能简述:4.4以上裁剪图片方法实现---------------------- 相册 <br>
     * 功能详细描述: <br>
     * 注意:
     */
    private void selectImageUriAfterKikat() {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME)));
        startActivityForResult(intent, SELECET_A_PICTURE_AFTER_KIKAT);
    }

    /**
     * <br>
     * 功能简述:裁剪图片方法实现----------------------相机 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param uri
     */
    private void cameraCropImageUri(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        int sizeImageHead = (int) getResources().getDimension(R.dimen.photo_w);
        intent.putExtra("outputX", sizeImageHead);
        intent.putExtra("outputY", sizeImageHead);
        intent.putExtra("scale", true);
        // if (mIsKitKat) {
        // intent.putExtra("return-data", true);
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // } else {
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // }
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, SET_PICTURE);
    }

    /**
     * <br>
     * 功能简述: 4.4及以上改动版裁剪图片方法实现 --------------------相机 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param uri
     */
    private void cropImageUriAfterKikat(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/jpeg");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        int sizeImageHead = (int) getResources().getDimension(R.dimen.photo_w);
        intent.putExtra("outputX", sizeImageHead);
        intent.putExtra("outputY", sizeImageHead);
        intent.putExtra("scale", true);
        // intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME)));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, SET_ALBUM_PICTURE_KITKAT);
    }

    /**
     * <br>
     * 功能简述: <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param uri
     * @return
     */
    private Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * <br>
     * 功能简述:4.4及以上获取图片的方法 <br>
     * 功能详细描述: <br>
     * 注意:
     *
     * @param context
     * @param uri
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


    public static int getScreenWith(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
}
