package com.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.wmlives.heihei.R;

public class IndicationView extends LinearLayout {

    private static final int INDICATION_DISION = R.dimen.indication_size;
    private static final int INDICATION_PADDING = R.dimen.indication_padding;

    public IndicationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(LinearLayout.HORIZONTAL);
    }

    public void setCount(int count) {
        removeAllViews();
        for (int i = 0; i < count; i++) {
            View view = new View(getContext());
            view.setBackgroundResource(R.drawable.indication_selector);
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(0, 0);
            parms.width = getResources().getDimensionPixelSize(INDICATION_DISION);
            parms.height = getResources().getDimensionPixelSize(INDICATION_DISION);
            parms.leftMargin = getResources().getDimensionPixelSize(INDICATION_PADDING);
            addViewInLayout(view, i, parms);
        }
        requestLayout();
        invalidate();
    }

    public void setSelecetdItem(int index) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).setSelected(i == index);
        }
    }

}
