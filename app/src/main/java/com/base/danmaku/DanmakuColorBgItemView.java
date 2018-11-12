package com.base.danmaku;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.heihei.model.User;
import com.wmlives.heihei.R;

public class DanmakuColorBgItemView extends DanmakuItemView {

    private TextView tv_danmaku;

    public DanmakuColorBgItemView(Context context, AttributeSet attrs) {
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

        if (item.gender == User.FEMALE) {
            tv_danmaku.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_danmaku_female));
        } else {
            tv_danmaku.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_danmaku_male));
        }

        tv_danmaku.setText(item.userName + ":" + item.text);
    }

}
