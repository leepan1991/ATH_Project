package cn.innovativest.ath.ui.frag;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.innovativest.ath.R;
import cn.innovativest.ath.ui.BaseFrag;

public class ContentFragment extends BaseFrag {
    private View view;
    private static final String KEY = "title";
    private TextView tvContent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.tv_content, container, false);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        String string = getArguments().getString(KEY);
        tvContent.setText(string);
        tvContent.setTextColor(Color.BLUE);
        tvContent.setTextSize(30);
        return view;
    }

    /**
     * fragment静态传值
     */
    public static ContentFragment newInstance(String str) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, str);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onClick(View v) {

    }
}