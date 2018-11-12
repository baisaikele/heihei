package com.base.widget.cobe.ptr.header;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.wmlives.heihei.R;


public class JiyuHeaderArrowView extends View {

    int mLevel = 0;

    /** view宽度 */
    int w = 0;
    /** View宽度 */
    int h = 0;
    /** 实际实用的宽高最小尺寸 */
    int minsize = 0;

    /** 箭头宽度比例 */
    final float r_m1_w = 0.279F;
    /** 箭头高度比例 */
    final float r_m1_h = 0.32558F;// w/h=0.85714

    /** 棍宽度比例 */
    final float r_m2_w = 0.046511F;
    /** 棍高度比例 */
    final float r_m2_h = 0.80232F;// w/h=0.057971
    /** 棍距离顶部比例 */
    final float r_m2_top = 0.19767f;

    // 箭头 棍 的宽高像素
    int m1_w, m1_h, m2_w, m2_h, m2_top;

    Bitmap m1, m2;

    Paint p1, p2;

    public JiyuHeaderArrowView(Context context) {
        super(context);
        init();
    }

    public JiyuHeaderArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JiyuHeaderArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        p1 = new Paint();
        p1.setAntiAlias(true);
        p2 = new Paint();
        p2.setAntiAlias(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        w = right - left;
        h = bottom - top;
        if (w > 0 && h > 0) {
            minsize = Math.min(w, h);
            m1_w = (int) (minsize * r_m1_w);
            m1_h = (int) (minsize * r_m1_h);
            m2_w = (int) (minsize * r_m2_w);
            m2_h = (int) (minsize * r_m2_h);
            m2_top = (int) (minsize * r_m2_top);
        }
        if (m1 == null) {
            m1 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.material_jiyu_arrow_1);
        }
        if (m2 == null) {
            m2 = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.material_jiyu_arrow_2);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (m1 != null) {
            m1.recycle();
            m1 = null;
        }
        if (m2 != null) {
            m2.recycle();
            m2 = null;
        }
    }

    /**
     * @param level
     *            0-100;
     */
    protected void onLevelChange(int level) {
        if (level < 0)
            level = 0;
        if (level > 100)
            level = 100;
        this.mLevel = level;
        if (getVisibility() == View.VISIBLE)
            invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (minsize <= 0 || m1 == null || m2 == null)
            return;

        if (isInEditMode()) {
            mLevel = 100;
        }

        // 画棍子
        float s = 0;
        // int top = (int) (r_m2_top * 100);
        int top = 50;
        if (mLevel < top) {
        } else {
            s = 1.0F * (mLevel - top) / (100 - top);
            s = Math.max(s, 0);
            s = Math.min(s, 1);
        }
        if (s > 0) {
            Rect m2_src = new Rect();
            m2_src.right = m2.getWidth();
            m2_src.top = (int) ((1 - s) * m2.getHeight());
            m2_src.bottom = m2_src.top + m2.getHeight();
            Rect m2_dst = new Rect();
            m2_dst.left = (w - m2_w) / 2;
            m2_dst.right = m2_dst.left + m2_w;
            m2_dst.top = m2_top;
            m2_dst.bottom = (int) (m2_dst.top + s * m2_h);
            canvas.drawBitmap(m2, m2_src, m2_dst, p2);
        }

        // 画箭头
        if (mLevel < 24) {
            float alpha = 1.0F * mLevel / 24;
            p1.setAlpha((int) (alpha * 255));
        } else {
            p1.setAlpha(255);
        }
        canvas.drawBitmap(m1, null, new Rect((w - m1_w) / 2, 0, (w - m1_w) / 2 + m1_w, m1_h), p1);
    }

}
