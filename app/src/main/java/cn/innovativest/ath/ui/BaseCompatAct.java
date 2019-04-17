package cn.innovativest.ath.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public abstract class BaseCompatAct extends AppCompatActivity implements
        OnClickListener {
    protected FragmentActivity mCtx;
    protected LayoutInflater mLayoutInft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCtx = this;
        mLayoutInft = (LayoutInflater) mCtx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//			AppUtils.finish(this);
        }
        return true;
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
//		AppUtils.hideImm(mCtx);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//		AppUtils.hideImm(mCtx);
        return super.onTouchEvent(event);
    }

    @Override
    public abstract void onClick(View v);

}
