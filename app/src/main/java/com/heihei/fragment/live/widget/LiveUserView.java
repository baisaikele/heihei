package com.heihei.fragment.live.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.utils.StringUtils;
import com.base.widget.RippleBackground;
import com.base.widget.RoundedImageView;
import com.facebook.fresco.FrescoImageHelper;
import com.heihei.logic.UserMgr;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class LiveUserView extends LinearLayout {

	public LiveUserView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// ----------------R.layout.cell_pm_userinfo-------------Start
	private RoundedImageView iv_avatar;
	private TextView tv_nickname;
	private TextView tv_ticker;
	private RippleBackground rippleBackground;

	public void autoLoad_cell_pm_userinfo() {
		iv_avatar = (RoundedImageView) findViewById(R.id.iv_avatar);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_ticker = (TextView) findViewById(R.id.tv_ticker);
		rippleBackground = (RippleBackground) findViewById(R.id.content);
		
	}

	// ----------------R.layout.cell_pm_userinfo-------------End

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_cell_pm_userinfo();
	}

	public void setUser(User user, boolean isOther) {
		// int res = StringUtils.getConstellationIcon(user.birthday,
		// user.gender);
		// iv_avatar.setImageResource(res);
		rippleBackground.stopRippleAnimation();
		iv_avatar.setUser(user, rippleBackground,isOther);
		tv_nickname.setText(user.nickname);
		tv_ticker.setText("黑票 " + String.valueOf(user.allEarnPoint));
	}

//	public void startAnimations() {
//		if (iv_avatar != null)
//			iv_avatar.startAnim();
//	}

	public void stopAnimation() {
		if (rippleBackground != null) {
			rippleBackground.stopRippleAnimation();
		}
	}

}
