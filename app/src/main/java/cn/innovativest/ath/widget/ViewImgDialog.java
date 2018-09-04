package cn.innovativest.ath.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import cn.innovativest.ath.GlideApp;
import cn.innovativest.ath.R;


public class ViewImgDialog extends Dialog implements View.OnClickListener {
    ImageView iv_img_big;
    Context mcontext;

    public ViewImgDialog(Context context) {
        super(context, R.style.mDialog);
        setContentView(R.layout.view_img_dialog);
        mcontext = context;
        iv_img_big = (ImageView) findViewById(R.id.iv_img_big);
        iv_img_big.setOnClickListener(this);
    }

    public ViewImgDialog isCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }

    public ViewImgDialog setImgUrl(String url) {
        GlideApp.with(mcontext).load(url).into(iv_img_big);
        return this;
    }

    public ViewImgDialog setIsCancelable(boolean flag) {

        setCancelable(flag);
        return this;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_img_big:
                dismiss();
                break;
        }
    }

}
