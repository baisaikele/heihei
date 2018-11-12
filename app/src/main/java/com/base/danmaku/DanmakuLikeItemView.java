package com.base.danmaku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.base.utils.ImageUtils;
import com.facebook.drawee.drawable.DrawableUtils;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class DanmakuLikeItemView extends DanmakuItemView {

    private TextView tv_danmaku;
    private TextView tv_nickanme;

    public DanmakuLikeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        tv_nickanme = (TextView) findViewById(R.id.danmaku_nickname);
        tv_danmaku = (TextView) findViewById(R.id.danmaku_text);
    };

    @Override
    public void refreshView() {

        if (item.gender == User.FEMALE) {
            tv_nickanme.setTextColor(getResources().getColor(R.color.hh_color_female));
            Drawable drawable1 = getResources().getDrawable(R.drawable.hh_live_heart_female);
            drawable1.setBounds(0, 0, ImageUtils.dip2px(11), ImageUtils.dip2px(11));//第一0是距左边距离，第二0是距上边距离，40分别是长宽
            tv_danmaku.setCompoundDrawables(null, null, drawable1, null);
            tv_danmaku.setCompoundDrawablePadding(ImageUtils.dip2px(6));
        } else {
            tv_nickanme.setTextColor(getResources().getColor(R.color.hh_color_male));
            Drawable drawable1 = getResources().getDrawable(R.drawable.hh_live_heart_male);
            drawable1.setBounds(0, 0, ImageUtils.dip2px(11), ImageUtils.dip2px(11));//第一0是距左边距离，第二0是距上边距离，40分别是长宽
            tv_danmaku.setCompoundDrawables(null, null, drawable1, null);
            tv_danmaku.setCompoundDrawablePadding(ImageUtils.dip2px(6));
        }

        tv_nickanme.setText(item.userName);
        tv_danmaku.setText(item.text);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }

}
