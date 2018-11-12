package com.heihei.fragment.live.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.base.utils.DeviceInfoUtils;
import com.wmlives.heihei.R;

/**
 * 数字增长带动画的view
 * 
 * @author chengbo
 */
public class IncreNumberView extends View {

    public IncreNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public IncreNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IncreNumberView(Context context) {
        super(context);
        init();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMS = 0;
        int heightMS = 0;
        // if (layout == null) {
        String str = newNum;
        layout = new StaticLayout(str, mPaint, DeviceInfoUtils.getScreenWidth(getContext()), Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);
        int width = (int) Math.ceil(layout.getDesiredWidth(str, mPaint));
        int height = layout.getHeight();
        widthMS = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMS = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        // }
        setMeasuredDimension(widthMS, heightMS);

    }

    private TextPaint mPaint;
    private StaticLayout layout;
    private String currentNum = "0";
    private String newNum = "0";

    private int offset = 0;

    private void init() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(getResources().getColor(R.color.hh_color_g));
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.gift_count_font_size));
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public synchronized void setDefaultNumber(String number) {
        currentNum = number;
        newNum = number;
        offset = 0;
        requestLayout();
        invalidate();
    }

    public synchronized void setNumber(String number) {
        this.newNum = number;
        offset = 0;
        requestLayout();
//        invalidate();
    }

    public synchronized String getNumber() {
        return this.newNum;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        String oldStr = "" + currentNum;
        String newStr = "" + newNum;
        int left = (int) layout.getLineLeft(0);
        int baseLine = layout.getLineBaseline(0);
        
        canvas.drawText(oldStr, left, baseLine - offset, mPaint);
        canvas.drawText(newStr, left, getHeight() + baseLine - offset, mPaint);
    }

    // private ValueAnimator animator;

    // private void startNumberAnimator() {
    // // if (animator == null)
    // // {
    // animator = ObjectAnimator.ofObject(new NumberOffsetEvaluator(), 0, getHeight());
    // animator.setDuration(100);
    // animator.setInterpolator(new OvershootInterpolator(2.0f));
    // animator.addUpdateListener(new AnimatorUpdateListener() {
    //
    // @Override
    // public void onAnimationUpdate(ValueAnimator animation) {
    // offset = (int) animation.getAnimatedValue();
    // postInvalidate();
    // }
    // });
    // // }
    // animator.addListener(new AnimatorListener() {
    //
    // @Override
    // public void onAnimationStart(Animator animation) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onAnimationRepeat(Animator animation) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public void onAnimationEnd(Animator animation) {
    // // TODO Auto-generated method stub
    // currentNum = newNum;
    // }
    //
    // @Override
    // public void onAnimationCancel(Animator animation) {
    // // TODO Auto-generated method stub
    //
    // }
    // });
    // animator.start();
    // }

}
