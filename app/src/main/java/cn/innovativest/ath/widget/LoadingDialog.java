package cn.innovativest.ath.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import cn.innovativest.ath.R;


/**
 * 加载中Dialog
 *
 * @author leepan
 */
public class LoadingDialog extends AlertDialog {

    private TextView tips_loading_msg;
    private int layoutResId;
    private String message = null;

    /**
     * 构造方法
     *
     * @param context     上下文
     * @param layoutResId 要传入的dialog布局文件的id
     */
    public LoadingDialog(Context context, int layoutResId, String msg) {
        super(context);
        this.layoutResId = layoutResId;
        message = msg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.setContentView(layoutResId);
        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
        tips_loading_msg.setText(this.message);
    }

}
