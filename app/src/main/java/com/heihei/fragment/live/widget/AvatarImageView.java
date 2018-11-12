package com.heihei.fragment.live.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.base.host.HostApplication;
import com.base.utils.StringUtils;
import com.facebook.fresco.FrescoImageHelper;
import com.facebook.fresco.FrescoImageView;
import com.facebook.fresco.FrescoParam;
import com.heihei.model.User;
import com.wmlives.heihei.R;

/**
 * 带描边的头像
 * 
 * @author chengbo
 *
 */
public class AvatarImageView extends ImageView {

	public AvatarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public AvatarImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AvatarImageView(Context context) {
		super(context);
	}

	private boolean showCircle = true;

	private User user;

	public void setUser(User user) {
		this.user = user;
		// if (user != null && user.avatar != null) {
		// FrescoParam param = new FrescoParam(user.avatar);
		// FrescoImageHelper.getImage(param, this);
		// }else{
		// int res = StringUtils.getConstellationIcon(user.birthday,
		// user.gender);
		// setImageResource(res);
		// }

		int res = StringUtils.getConstellationIcon(user.birthday, user.gender);
		setImageResource(res);
		invalidate();
	}

	public void setShowCircle(boolean showCircle) {
		this.showCircle = showCircle;
		invalidate();
	}

	private Paint mPaint;

	private int stokeWidth = getResources().getDimensionPixelSize(R.dimen.line_height);;

	private Paint createPaint() {
		if (mPaint == null) {
			mPaint = new Paint();
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(stokeWidth);
		}

		if (user.gender == User.FEMALE) {
			mPaint.setColor(getContext().getResources().getColor(R.color.hh_color_female));
		} else {
			mPaint.setColor(getContext().getResources().getColor(R.color.hh_color_male));
		}

		return mPaint;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (user != null && showCircle) {
			int w = getWidth();
			int h = getHeight();

			int centerX = w / 2;
			int centerY = h / 2;
			int radius = w / 2 - stokeWidth;

			canvas.drawCircle(centerX, centerY, radius, createPaint());
		}
	}

}
