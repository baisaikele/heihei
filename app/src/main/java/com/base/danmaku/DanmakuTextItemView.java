package com.base.danmaku;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.host.AppLogic;
import com.base.host.HostApplication;
import com.facebook.fresco.FrescoImageHelper;
import com.facebook.fresco.FrescoParam;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class DanmakuTextItemView extends DanmakuItemView {

	private TextView tv_danmaku;
	private TextView tv_nickanme;
	private ImageView danmaku_icon;

	public DanmakuTextItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		tv_nickanme = (TextView) findViewById(R.id.danmaku_nickname);
		tv_danmaku = (TextView) findViewById(R.id.danmaku_text);
		danmaku_icon = (ImageView) findViewById(R.id.danmaku_icon);
	};

	@Override
	public void refreshView() {

		if (item.gender == User.FEMALE) {
			tv_nickanme.setTextColor(getResources().getColor(R.color.hh_color_female));
		} else {
			tv_nickanme.setTextColor(getResources().getColor(R.color.hh_color_male));
		}

		tv_nickanme.setText(item.userName);
		tv_danmaku.setText(item.text);

		if (item.giftId != -1) {
			danmaku_icon.setVisibility(View.VISIBLE);
			FrescoParam param = new FrescoParam(AppLogic.gifts.get(item.giftId));
			FrescoImageHelper.getImage(param, danmaku_icon);
		}else {
			danmaku_icon.setVisibility(View.GONE);
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

}
