package cn.innovativest.ath.ui.act;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.innovativest.ath.App;
import cn.innovativest.ath.R;
import cn.innovativest.ath.common.AppConfig;
import cn.innovativest.ath.core.AthService;
import cn.innovativest.ath.response.BaseResponse;
import cn.innovativest.ath.ui.BaseAct;
import cn.innovativest.ath.utils.LoadingUtils;
import cn.innovativest.ath.utils.LogUtils;
import cn.innovativest.ath.utils.SDUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by leepan on 21/03/2018.
 */

public class UploadIdAct extends BaseAct {

    @BindView(R.id.btnBack)
    ImageButton btnBack;

    @BindView(R.id.tvwTitle)
    TextView tvwTitle;

    @BindView(R.id.lltIDZ)
    LinearLayout lltIDZ;

    @BindView(R.id.ivZheng)
    ImageView ivZheng;

    @BindView(R.id.lltIDB)
    LinearLayout lltIDB;

    @BindView(R.id.ivFan)
    ImageView ivFan;

    @BindView(R.id.btnConfirm)
    Button btnConfirm;

    TextView tvNotice;


    private PopupWindow selectWindow = null;

    // 保存图片本地路径
    public static final String ACCOUNT_DIR = AppConfig.ATH_CACHE_PIC_PATH
            + "/account/";
    public static final String ACCOUNT_MAINTRANCE_ICON_CACHE = "icon_cache/";
    private static final String IMGPATH = ACCOUNT_DIR
            + ACCOUNT_MAINTRANCE_ICON_CACHE;

    private static final String IMAGE_FILE_NAME1 = "idz.jpg";
    private static final String TMP_IMAGE_FILE_NAME1 = "tmp_idz.jpg";

    private static final String IMAGE_FILE_NAME2 = "idf.jpg";
    private static final String TMP_IMAGE_FILE_NAME2 = "tmp_idf.jpg";

    // 常量定义
    public static final int TAKE_A_PICTURE = 10;
    public static final int SELECT_A_PICTURE = 20;
    public static final int SET_PICTURE = 30;
    public static final int SET_ALBUM_PICTURE_KITKAT = 40;
    public static final int SELECET_A_PICTURE_AFTER_KIKAT = 50;
    private String mAlbumPicturePath = null;

    private File fileone1 = null;
    private File filetwo1 = null;

    private File fileone2 = null;
    private File filetwo2 = null;

    // 版本比较：是否是4.4及以上版本
    final boolean mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

    File file1 = null;
    File file2 = null;

    private int flag = 0;// 1 身份证正面，2 身份证反面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_id_act);
        ButterKnife.bind(this);
        initView();
        initFile();
    }

    private void initView() {
        btnBack.setImageResource(R.drawable.login_arrow_left);
        tvwTitle.setText("上传身份证");

        btnBack.setOnClickListener(this);
        lltIDZ.setOnClickListener(this);
        ivZheng.setOnClickListener(this);
        lltIDB.setOnClickListener(this);
        ivFan.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

    }

    private void upload() {

        if (file1 == null) {
            App.toast(this, "请上传身份证正面");
            return;
        }

        if (file2 == null) {
            App.toast(this, "请上传身份证反面");
            return;
        }

        addPic(file1, file2);
    }

    private void addPic(File file1, File file2) {

        LoadingUtils.getInstance().dialogShow(this, "提交中...");
        MultipartBody.Builder body = new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);
        body.addFormDataPart("img1", file1.getName(), RequestBody.create(MediaType.parse("image/jpg"), file1));
        body.addFormDataPart("img2", file2.getName(), RequestBody.create(MediaType.parse("image/jpg"), file2));

        AthService service = App.get().getAthService();
        service.senior_IdAuthentication(body.build()).observeOn(AndroidSchedulers.mainThread()).subscribeOn(App.get().defaultSubscribeScheduler()).subscribe(new Action1<BaseResponse>() {
            @Override
            public void call(BaseResponse baseResponse) {
                LoadingUtils.getInstance().dialogDismiss();
                App.toast(UploadIdAct.this, baseResponse.message);
                if (baseResponse.status == 1) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                LoadingUtils.getInstance().dialogDismiss();
                LogUtils.e(throwable.getMessage().toString());
            }
        });

    }

    private void isShowImg(boolean isShow, boolean isFirst) {
        if (isShow) {
            if (isFirst) {
                lltIDZ.setVisibility(View.GONE);
                ivZheng.setVisibility(View.VISIBLE);
            } else {
                lltIDB.setVisibility(View.GONE);
                ivFan.setVisibility(View.VISIBLE);
            }
        } else {
            lltIDZ.setVisibility(View.VISIBLE);
            ivZheng.setVisibility(View.GONE);
            lltIDB.setVisibility(View.VISIBLE);
            ivFan.setVisibility(View.GONE);
        }
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

        fileone1 = new File(IMGPATH, IMAGE_FILE_NAME1);
        filetwo1 = new File(IMGPATH, TMP_IMAGE_FILE_NAME1);

        fileone2 = new File(IMGPATH, IMAGE_FILE_NAME2);
        filetwo2 = new File(IMGPATH, TMP_IMAGE_FILE_NAME2);

        try {
            if (fileone1 != null && fileone1.exists()) {
                SDUtils.deleteFile(fileone1, true);
            }
            if (filetwo1 != null && filetwo1.exists()) {
                SDUtils.deleteFile(filetwo1, true);
            }
            if (!fileone1.exists() && !filetwo1.exists()) {
                fileone1.createNewFile();
                filetwo1.createNewFile();
            }

            if (fileone2 != null && fileone2.exists()) {
                SDUtils.deleteFile(fileone2, true);
            }
            if (filetwo2 != null && filetwo2.exists()) {
                SDUtils.deleteFile(filetwo2, true);
            }

            if (!fileone2.exists() && !filetwo2.exists()) {
                fileone2.createNewFile();
                filetwo2.createNewFile();
            }
        } catch (Exception e) {
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (flag == 1) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, IMAGE_FILE_NAME1)));
        } else if (flag == 2) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, IMAGE_FILE_NAME2)));
        }
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
            View view = LayoutInflater.from(UploadIdAct.this).inflate(
                    R.layout.add_pic_dialog, null);
            tvNotice = (TextView) view.findViewById(R.id.tvNotice);
            if (flag == 2) {
                tvNotice.setText("上传身份证反面");
            } else if (flag == 1) {
                tvNotice.setText("上传身份证正面");
            }
            view.findViewById(R.id.btnTakePhotos).setOnClickListener(
                    UploadIdAct.this);
            view.findViewById(R.id.btnLocalPhotos).setOnClickListener(
                    UploadIdAct.this);
            view.findViewById(R.id.btnCancele).setOnClickListener(UploadIdAct.this);
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
                    if (flag == 1) {
                        file1 = new File(IMGPATH, TMP_IMAGE_FILE_NAME1);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file1));
                        isShowImg(true, true);
                        ivZheng.setImageBitmap(bitmap);
                    } else if (flag == 2) {
                        file2 = new File(IMGPATH, TMP_IMAGE_FILE_NAME2);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file2));
                        isShowImg(true, false);
                        ivFan.setImageBitmap(bitmap);
                    }


//                    modify(file);//后面打开
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(UploadIdAct.this, "取消设置", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case SELECET_A_PICTURE_AFTER_KIKAT:
                if (resultCode == RESULT_OK && null != data) {
                    // Log.i("zou", "4.4以上的");
                    mAlbumPicturePath = getPath(getApplicationContext(),
                            data.getData());

                    if (flag == 1) {
                        file1 = new File(mAlbumPicturePath);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file1));
                        isShowImg(true, true);
                        ivZheng.setImageBitmap(bitmap);
                    } else if (flag == 2) {
                        file2 = new File(mAlbumPicturePath);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file2));
                        isShowImg(true, false);
                        ivFan.setImageBitmap(bitmap);
                    }


                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(UploadIdAct.this, "取消设置", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case SET_ALBUM_PICTURE_KITKAT:
                Log.i("zou", "4.4以上上的 RESULT_OK");
                if (flag == 1) {
                    file1 = new File(IMGPATH, TMP_IMAGE_FILE_NAME1);
                    bitmap = decodeUriAsBitmap(Uri.fromFile(file1));
                    isShowImg(true, true);
                    ivZheng.setImageBitmap(bitmap);
                } else if (flag == 2) {
                    file2 = new File(IMGPATH, TMP_IMAGE_FILE_NAME2);
                    bitmap = decodeUriAsBitmap(Uri.fromFile(file2));
                    isShowImg(true, false);
                    ivFan.setImageBitmap(bitmap);
                }

//                modify(file);//后面打开
                break;
            case TAKE_A_PICTURE:
                Log.i("zou", "TAKE_A_PICTURE-resultCode:" + resultCode);
                if (resultCode == RESULT_OK) {
                    if (flag == 1) {
//                        cameraCropImageUri(Uri.fromFile(new File(IMGPATH,
//                                IMAGE_FILE_NAME1)));
                        file1 = new File(IMGPATH, IMAGE_FILE_NAME1);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file1));
                        isShowImg(true, true);
                        ivZheng.setImageBitmap(bitmap);
                    } else if (flag == 2) {
//                        cameraCropImageUri(Uri.fromFile(new File(IMGPATH,
//                                IMAGE_FILE_NAME2)));
                        file2 = new File(IMGPATH, IMAGE_FILE_NAME2);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file2));
                        isShowImg(true, false);
                        ivFan.setImageBitmap(bitmap);
                    }

                } else {
                    Toast.makeText(UploadIdAct.this, "取消设置", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case SET_PICTURE:
                if (resultCode == RESULT_OK && null != data) {
                    if (flag == 1) {
                        file1 = new File(IMGPATH, IMAGE_FILE_NAME1);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file1));
                        isShowImg(true, true);
                        ivZheng.setImageBitmap(bitmap);
                    } else if (flag == 2) {
                        file1 = new File(IMGPATH, IMAGE_FILE_NAME1);
                        bitmap = decodeUriAsBitmap(Uri.fromFile(file1));
                        isShowImg(true, false);
                        ivFan.setImageBitmap(bitmap);
                    }

//                    modify(file);//后面打开
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(UploadIdAct.this, "取消设置", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(UploadIdAct.this, "设置失败", Toast.LENGTH_SHORT)
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
//        intent.putExtra("aspectY", 2);
//        int sizeImageHead = (int) getResources().getDimension(R.dimen.photo_w);
//        intent.putExtra("outputX", sizeImageHead);
//        intent.putExtra("outputY", sizeImageHead * 2);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", false);
        if (flag == 1) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME1)));
        } else if (flag == 2) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME2)));
        }

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

        if (flag == 1) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME1)));
        } else if (flag == 2) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME2)));
        }

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
        intent.putExtra("aspectY", 2);
        int sizeImageHead = (int) getResources().getDimension(R.dimen.photo_w);
        intent.putExtra("outputX", sizeImageHead);
        intent.putExtra("outputY", sizeImageHead * 2);
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
//        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        int sizeImageHead = (int) getResources().getDimension(R.dimen.photo_w);
//        intent.putExtra("outputX", sizeImageHead);
//        intent.putExtra("outputY", sizeImageHead * 2);
        intent.putExtra("scale", true);
        // intent.putExtra("return-data", true);
        intent.putExtra("return-data", false);
        if (flag == 1) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME1)));
        } else if (flag == 2) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(IMGPATH, TMP_IMAGE_FILE_NAME2)));
        }

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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UploadIdAct");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UploadIdAct");
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
            case R.id.lltIDZ:
                if (tvNotice != null) {
                    tvNotice.setText("上传身份证正面");
                }
                flag = 1;
                showSelectWindow();
                break;
            case R.id.ivZheng:
                if (tvNotice != null) {
                    tvNotice.setText("上传身份证正面");
                }
                flag = 1;
                showSelectWindow();
                break;
            case R.id.lltIDB:
                if (tvNotice != null) {
                    tvNotice.setText("上传身份证反面");
                }
                flag = 2;
                showSelectWindow();
                break;
            case R.id.ivFan:
                if (tvNotice != null) {
                    tvNotice.setText("上传身份证反面");
                }
                flag = 2;
                showSelectWindow();
                break;
            case R.id.btnConfirm:
                upload();
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
}
