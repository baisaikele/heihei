package com.base.widget.cobe.ptr.header;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.base.widget.cobe.ptr.PtrFrameLayout;
import com.base.widget.cobe.ptr.PtrUIHandler;
import com.base.widget.cobe.ptr.indicator.PtrIndicator;
import com.wmlives.heihei.R;

public class JiyuHeaderArrow extends RelativeLayout implements PtrUIHandler {

    private int ARROW_SIZE = 43;
    private int PB_SIZE = 31;

    public JiyuHeaderArrow(Context context) {
        super(context);
        init();
    }

    public JiyuHeaderArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JiyuHeaderArrow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // ----------------R.layout.jiyu_header_arrow-------------Start
    private JiyuHeaderArrowView arrowView;
    private ProgressBar pb;

    public void autoLoad_jiyu_header_arrow() {
        arrowView = (JiyuHeaderArrowView) findViewById(R.id.arrowView);
        pb = (ProgressBar) findViewById(R.id.pb);
    }

    // ----------------R.layout.jiyu_header_arrow-------------End

    private void init() {
        final DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        final float screenDensity = metrics.density;
        LayoutInflater.from(getContext()).inflate(R.layout.jiyu_header_arrow, this);
        autoLoad_jiyu_header_arrow();
        arrowView.onLevelChange(0);
        arrowView.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    // PTR下拉刷新 UI控制接口 ----------------------------

    /**
     * Content 重新回到顶部， Header 消失，整个下拉刷新过程完全结束以后，重置 View 。
     */
    public void onUIReset(PtrFrameLayout frame) {
        arrowView.onLevelChange(0);
        arrowView.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    /**
     * 准备刷新，Header 将要出现时调用。
     */
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        arrowView.onLevelChange(0);
        arrowView.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);
    }

    /**
     * 开始刷新，Header 进入刷新状态之前调用。
     */
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        arrowView.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.VISIBLE);
    }

    /**
     * 刷新结束，Header 开始向上移动之前调用。
     */
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        arrowView.setVisibility(View.INVISIBLE);
        pb.setVisibility(View.GONE);
    }

    /**
     * header滑动时调用
     */
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        if (ptrIndicator != null && arrowView != null) {
            int mOffsetToRefresh = ptrIndicator.getOffsetToRefresh();// 滑动到多少距离 开始刷新
            int currentPos = ptrIndicator.getCurrentPosY();// 已经滑动了多少距离
            if (mOffsetToRefresh != 0) {
                int level = (int) (currentPos * 100.0F / mOffsetToRefresh);
                level = Math.max(level, 0);
                level = Math.min(level, 100);
                arrowView.onLevelChange(level);
            }
        }
    }

}
