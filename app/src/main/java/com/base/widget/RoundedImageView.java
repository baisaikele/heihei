package com.base.widget;

import com.base.utils.StringUtils;
import com.base.utils.ThreadManager;
import com.heihei.model.User;
import com.wmlives.heihei.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
	private float mCornerRadius;

	public RoundedImageView(Context context) {
		super(context);
	}

	private boolean showCircle = true;

	public void setShowCircle(boolean showCircle) {
		this.showCircle = showCircle;
		invalidate();
	}

	private User user;
	private RippleBackground mRippleBackground;

	public void setUser(User user, RippleBackground gBackground, boolean isOther) {
		this.user = user;

		final int res = StringUtils.getConstellationIcon(user.birthday, user.gender);
		setImageResource(res);

//		if (isOther) {
//			startAnim();
//		}
//		this.mRippleBackground = gBackground;
//		if (user.gender == 1) {
//			mRippleBackground.setPaintColor(getResources().getColor(R.color.hh_color_female));
//		} else {
//			mRippleBackground.setPaintColor(getResources().getColor(R.color.hh_color_male));
//		}
//		mRippleBackground.stopRippleAnimation();
//		invalidate();
	}

	public void startAnimation() {
		try {
			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					final int res = StringUtils.getConstellationIcon(user.birthday, user.gender);
					Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), res);
					final Bitmap bmp = replaceBitmapColor(mBitmap, getResources().getColor(R.color.full_transparent), getResources().getColor(R.color.hh_color_d));
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							setImageBitmap(bmp);
							mRippleBackground.startRippleAnimation();
							invalidate();
						}
					});
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isAnim = false;

	private Handler mHandler = new Handler();

	public static Bitmap replaceBitmapColor(Bitmap oldBitmap, int oldColor, int newColor) {
		Bitmap mBitmap = oldBitmap.copy(Config.ARGB_8888, true);
		int mBitmapWidth = mBitmap.getWidth();
		int mBitmapHeight = mBitmap.getHeight();
		int mArrayColorLengh = mBitmapWidth * mBitmapHeight;
		int[] mArrayColor = new int[mArrayColorLengh];
		int count = 0;
		for (int i = 0; i < mBitmapHeight; i++) {
			for (int j = 0; j < mBitmapWidth; j++) {
				int color = mBitmap.getPixel(j, i);
				if (color == oldColor) {
					mBitmap.setPixel(j, i, newColor); // 将白色替换成透明色
				}

			}
		}
		return mBitmap;
	}

	public RoundedImageView(Context context, AttributeSet attributes) {
		super(context, attributes);
		Resources rsc = context.getResources();
		int cornerRadius = (int) rsc.getDisplayMetrics().density * rsc.getDimensionPixelSize(R.dimen.pm_user_head_size) / 14;
		TypedArray array = context.obtainStyledAttributes(attributes, R.styleable.RoundedImageView);
		if (array != null) {
			mCornerRadius = array.getDimension(R.styleable.RoundedImageView_corner_radius, cornerRadius);
			array.recycle();
		}
	}

	public void setCornerRadius(float cornerRadius) {
		mCornerRadius = cornerRadius;
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
		Drawable maiDrawable = getDrawable();
		if (maiDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
			Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
			final int color = 0xff000000;
			Rect bitmapBounds = maiDrawable.getBounds();
			final RectF rectF = new RectF(bitmapBounds);
			int saveCount = canvas.saveLayer(rectF, null,
					Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
			getImageMatrix().mapRect(rectF);

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);

			Xfermode oldMode = paint.getXfermode();
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			super.onDraw(canvas);
			paint.setXfermode(oldMode);
			canvas.restoreToCount(saveCount);
		} else {
			super.onDraw(canvas);
		}

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
