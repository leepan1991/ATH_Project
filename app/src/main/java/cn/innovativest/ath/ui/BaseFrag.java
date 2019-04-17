package cn.innovativest.ath.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public abstract class BaseFrag extends Fragment implements OnClickListener {
	protected final String TAG = getClass().getSimpleName();
	protected FragmentActivity mCtx;
	protected View mRootView;
	protected LayoutInflater mLayoutInft;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mCtx = getActivity();
		mLayoutInft = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public abstract void onClick(View v);
}
