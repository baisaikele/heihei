package com.base.danmaku;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.heihei.model.User;
import com.wmlives.heihei.R;

public class DanmakuSystemMsgItemView extends DanmakuItemView {

    private TextView tv_danmaku;

    public DanmakuSystemMsgItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        tv_danmaku = (TextView) findViewById(R.id.danmaku_text);
    };

    @Override
    public void refreshView() {
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
