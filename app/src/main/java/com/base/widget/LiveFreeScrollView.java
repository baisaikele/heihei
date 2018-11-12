package com.base.widget;

import com.base.utils.LogWriter;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class LiveFreeScrollView extends ScrollView {

	public LiveFreeScrollView(Context context) {
		super(context);
	}

	public LiveFreeScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LiveFreeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (getScrollY() + getHeight() >= computeVerticalScrollRange()) {
			isBottom = true;
		} else {
			isBottom = false;
		}
	}

	private boolean isBottom = true;

	public boolean getScrollIsBottom() {
		return isBottom;
	}

}
