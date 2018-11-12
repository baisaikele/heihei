package com.base.widget.tabhost.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class MainTabHost extends LinearLayout implements OnClickListener {

    public MainTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof MainTabButton) {
                view.setOnClickListener(this);
            }
        }
    }

    public void setChecked(int position) {
        setChecked(position, false);
    }

    private void setChecked(int position, boolean byUser) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (view instanceof MainTabButton) {
                ((MainTabButton) view).setSelected(position == i);
                if (position == i && mListener != null) {
                    mListener.onCheckedChange(position, byUser);
                }
            }
        }
    }

    public void setImageDrawable(int position, Drawable drawable) {
        MainTabButton button = (MainTabButton) getChildAt(position);
        button.setImageDrawable(drawable);
    }

    /**
     * 设置是否有更新
     * 
     * @param position
     * @param hasNew
     */
    public void setHasNew(int position, boolean hasNew) {
        MainTabButton button = (MainTabButton) getChildAt(position);
        if (button != null) {
            button.setHasNew(hasNew);
        }
    }

    /**
     * 设置未读数
     * 
     * @param position
     * @param count
     */
    public void setUnreadCount(int position, int count) {
        MainTabButton button = (MainTabButton) getChildAt(position);
        if (button != null) {
            button.setUnreadCount(count);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View view = getChildAt(i);
            if (v == view) {
                setChecked(i, true);
                break;
            }
        }
    }

    private OnCheckedChangeListener mListener;

    public void setOnCheckedChangeListener(OnCheckedChangeListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCheckedChangeListener {

        void onCheckedChange(int checkedPosition, boolean byUser);
    }
}
