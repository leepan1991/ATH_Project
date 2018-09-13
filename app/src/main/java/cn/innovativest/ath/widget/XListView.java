package cn.innovativest.ath.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 不带滚动功能的ListView，在ScrollView中使用
 *
 * @author leepan
 * 
 */
public class XListView extends ListView {
	public XListView(Context context) {
		super(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
