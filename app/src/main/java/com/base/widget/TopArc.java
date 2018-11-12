package com.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class TopArc extends View {

    public TopArc(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        w = right - left;
        h = bottom - top;
    }

    int w = 0;
    int h = 0;
    int def_color = 0xffffffff;
    int color = def_color;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInEditMode()) {
            Drawable bg = getBackground();
            if (bg != null && ColorDrawable.class.isInstance(bg)) {
                int c = ((ColorDrawable) bg).getColor();
                if (c != 0x0) {
                    this.setBackgroundColor(Color.TRANSPARENT);
                    color = c;
                }
            } else {
                color = def_color;
            }
        }

        int start_x = 0;
        int start_y = h;
        int ctrl_x = w / 2;
        int ctrl_y = (int) (-h * 0.33F);
        int end_x = w;
        int end_y = h;

        Path path = new Path();
        path.moveTo(start_x, start_y);
        path.cubicTo(ctrl_x, ctrl_y, ctrl_x, ctrl_y, end_x, end_y);
        path.moveTo(start_x, start_y);
        path.close();

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setDither(true);
        paint.setAntiAlias(true);

        canvas.drawPath(path, paint);
    }

}
