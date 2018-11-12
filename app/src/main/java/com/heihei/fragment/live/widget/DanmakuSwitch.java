package com.heihei.fragment.live.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.utils.DeviceInfoUtils;
import com.wmlives.heihei.R;

public class DanmakuSwitch extends LinearLayout {

    public DanmakuSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DanmakuSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmakuSwitch(Context context) {
        super(context);
    }

    // ----------------R.layout.layout_danmaku_switch-------------Start
    private TextView tv_title;

    public void autoLoad_layout_danmaku_switch() {
        tv_title = (TextView) findViewById(R.id.tv_title);
    }

    // ----------------R.layout.layout_danmaku_switch-------------End

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoLoad_layout_danmaku_switch();
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        tv_title.setEnabled(selected);
        if (!selected) {
            LayoutParams params = (LayoutParams) tv_title.getLayoutParams();
            params.leftMargin = DeviceInfoUtils.dip2px(getContext(), 3f);
            tv_title.setLayoutParams(params);
        } else {
            LayoutParams params = (LayoutParams) tv_title.getLayoutParams();
            params.leftMargin = DeviceInfoUtils.dip2px(getContext(), 16f);
            tv_title.setLayoutParams(params);
        }
    }

}
