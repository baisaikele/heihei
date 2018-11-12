package com.base.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.animator.FaceFieldEvaluator;
import com.base.animator.PointEvaluator;
import com.base.utils.DeviceInfoUtils;
import com.heihei.fragment.live.logic.GiftAnimationController;
import com.heihei.fragment.live.logic.GiftAnimationController.FaceField;
import com.heihei.fragment.live.logic.OnGiftAnimationListener;
import com.heihei.model.AudienceGift;
import com.wmlives.heihei.R;

/**
 * 礼物雨的动画管理
 * 
 * @author admin
 *
 */
public class FaceRainView extends FrameLayout {

	public static final int DEFAULT_DURATION = 2500;// 移动时间
	public static final int DEFAULT_NEXTLINE_RAIN_DURATION = 240;// 每行雨滴的间隔时间
	public static final int DEFAULT_TEXT_DURATION = 350;// 礼物名称的缩放时间（1.5--1.0--0.0）

	private int animType;

	private OnGiftAnimationListener mOnGiftAnimationListener;

	public FaceRainView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public FaceRainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FaceRainView(Context context) {
		super(context);
		init();
	}

	int width = DeviceInfoUtils.getScreenWidth(getContext());
	int height = DeviceInfoUtils.getScreenHeight(getContext());

	private void init() {

		// setBackgroundResource(R.color.hh_color_gift_bg);
		// setAlpha(70);
		bitmaps = new Bitmap[3];

		if (tipView != null && indexOfChild(tipView) != -1) {
			removeView(tipView);
			tipView = null;
		}

		tipView = LayoutInflater.from(getContext()).inflate(R.layout.layout_facerain_tip, null);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		addView(tipView, params);
	}

	private void initAnimRes() {
		switch (animType) {
		case GiftAnimationController.ANIM_BIANBIAN:
			bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.red_bianbian);
			bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.green_bianbian);
			bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_bianbian);
			break;
		case GiftAnimationController.ANIM_BUBBLE:
			bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.pink_bubble);
			bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_bubble);
			bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.green_bubble);
			break;
		case GiftAnimationController.ANIM_FINGER:
			bitmaps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.pink_finger);
			bitmaps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.white_finger);
			bitmaps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.green_finger);
			break;
		}

	}

	private Bitmap[] bitmaps;

	int lineCount = 30;// 行数
	int columnCount = 6;// 列数
	int itemWidth = width / columnCount;
	int itemHeight = height / lineCount;

	private View tipView;
	private int[][] aniArrTmp = { { 0, 1, 1, 0, 1, 1 }, { 1, 1, 1, 1, 0, 0 }, { 0, 1, 1, 1, 1, 0 }, { 1, 1, 1, 0, 0, 1 }, { 0, 1, 1, 0, 1, 0 }, { 0, 0, 1, 1, 1, 1 },
			{ 1, 1, 0, 1, 0, 0 }, { 0, 1, 1, 0, 1, 0 }, { 1, 0, 1, 1, 0, 1 }, { 1, 1, 0, 1, 0, 1 }, { 0, 1, 1, 0, 1, 1 }, { 1, 1, 1, 1, 0, 0 }, { 0, 1, 1, 1, 1, 0 },
			{ 1, 1, 1, 0, 0, 1 }, { 0, 1, 1, 0, 1, 0 }, { 0, 0, 1, 1, 1, 1 }, { 1, 1, 0, 1, 0, 0 }, { 0, 1, 1, 0, 1, 0 }, { 1, 0, 1, 1, 0, 1 }, { 1, 1, 0, 1, 0, 1 },
			{ 0, 1, 1, 0, 1, 1 }, { 1, 1, 1, 1, 0, 0 }, { 0, 1, 1, 1, 1, 0 }, { 1, 1, 1, 0, 0, 1 }, { 0, 1, 1, 0, 1, 0 }, { 0, 0, 1, 1, 1, 1 }, { 1, 1, 0, 1, 0, 0 },
			{ 0, 1, 1, 0, 1, 0 }, { 1, 0, 1, 1, 0, 1 }, { 1, 1, 0, 1, 0, 1 } };

	public void start(int animType, OnGiftAnimationListener mOnGiftAnimationListener) {
		stop();
		this.mOnGiftAnimationListener = mOnGiftAnimationListener;
		this.animType = animType;
		initAnimRes();
		currentIndex = lineCount - 1;
		addNextLineView();

		ValueAnimator ani = ValueAnimator.ofFloat(1.5f, 1.0f);
		ani.setDuration(DEFAULT_TEXT_DURATION);
		ani.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float scale = (float) animation.getAnimatedValue();
				tipView.setScaleX(scale);
				tipView.setScaleY(scale);

			}
		});
		ani.start();

	}

	public void setData(AudienceGift aGift) {
		if (tipView != null) {
			TextView tv_nickname = (TextView) tipView.findViewById(R.id.tv_nickname);
			TextView tv_gift_name = (TextView) tipView.findViewById(R.id.tv_gift_name);
			tv_nickname.setText(aGift.fromUser.nickname);
			tv_gift_name.setText(aGift.gift.name + "送");
		}
	}

	public void stop() {
		currentIndex = -1;
		mHandler.removeMessages(0);
		// removeAllViews();
	}

	private boolean hasRunEndAnim = false;

	public void addNextLineView() {
		if (currentIndex < 0) {
			return;
		}

		mHandler.removeMessages(0);

		addLineFaces(currentIndex);

		if (!hasRunEndAnim && currentIndex == 0) {
			hasRunEndAnim = true;
			ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f);
			anim.setDuration(DEFAULT_TEXT_DURATION);
			anim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float scale = (float) animation.getAnimatedValue();
					tipView.setScaleX(scale);
					tipView.setScaleY(scale);

				}
			});
			anim.start();
		}

		currentIndex = currentIndex - 1;
		if (currentIndex < 0) {
			return;
		}

		Message msg = Message.obtain();
		msg.obj = this;
		mHandler.sendMessageDelayed(msg, DEFAULT_NEXTLINE_RAIN_DURATION);
	}

	private int currentIndex = -1;

	private static Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			FaceRainView view = (FaceRainView) msg.obj;
			view.addNextLineView();
		};
	};

	/**
	 * 增加一行view
	 * 
	 * @param line
	 */
	private void addLineFaces(int line) {
		int i = line;
		for (int j = 0; j < columnCount; j++) {
			if (aniArrTmp[i][j] > 0) {
				addFace(i, j);
			}
		}

		// for (int j = 0; j < columnCount; j++) {
		// if (i == 0) { // 行1，列1，2，4，5
		// if (j == 1 || j == 2 || j == 4 || j == 5) {
		// addFace(i, j);
		// }
		// } else if (i == 1) {
		// if (j == 0 || j == 1 || j == 2 || j == 3) {
		// addFace(i, j);
		// }
		// } else if (i == 2) {
		// if (j == 1 || j == 2 || j == 3 || j == 4) {
		// addFace(i, j);
		// }
		// } else if (i == 3) {
		// if (j == 0 || j == 1 || j == 2 || j == 5) {
		// addFace(i, j);
		// }
		// } else if (i == 4) {
		// if (j == 1 || j == 2 || j == 4) {
		// addFace(i, j);
		// }
		// } else if (i == 5) {
		// if (j == 2 || j == 3 || j == 4 || j == 5) {
		// addFace(i, j);
		// }
		// } else if (i == 6) {
		// if (j == 0 || j == 1) {
		// addFace(i, j);
		// }
		// } else if (i == 7) {
		// if (j == 2 || j == 4) {
		// addFace(i, j);
		// }
		// } else if (i == 8) {
		// if (j == 0 || j == 1 || j == 3 || j == 5) {
		// addFace(i, j);
		// }
		// } else if (i == 9) {
		// if (j == 0 || j == 4) {
		// addFace(i, j);
		// }
		// }
		// }
	}

	private void addFace(int line, int column) {
		int left = column * itemWidth;
		int top = -1 * itemHeight;
		int right = (column + 1) * itemWidth;
		int bottom = 0 * itemHeight;
		final ImageView image = new ImageView(getContext());

		int index = (int) (Math.random() * bitmaps.length);
		Bitmap bp = bitmaps[index];
		image.setImageBitmap(bp);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		addView(image, 0, params);

		int x = (int) (Math.random() * (right - left) + left);
		int y = (int) (Math.random() * (bottom - bp.getHeight() - top) + top);

		Point startP = new Point(x, y);
		Point endP = new Point(x, height);

		ValueAnimator moveAnim = ValueAnimator.ofObject(new PointEvaluator(), startP, endP);
		moveAnim.setDuration(DEFAULT_DURATION);
		moveAnim.setInterpolator(new AccelerateDecelerateInterpolator());
		moveAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				Point p = (Point) animation.getAnimatedValue();
				image.setTranslationX(p.x);
				image.setTranslationY(p.y);
			}
		});

		// if (animType == GiftAnimationController.ANIM_FINGER) {
		// moveAnim.reverse();
		// } else {
		// moveAnim.start();
		// }

		moveAnim.start();

		FaceField startF = new FaceField();
		startF.degrees = (int) (Math.random() * (FaceField.DEGREES_MAX - FaceField.DEGREES_MIN));
		startF.scale = (float) (Math.random() * (FaceField.SCALE_MAX - FaceField.SCALE_MIN));
		ValueAnimator srAnim = ValueAnimator.ofObject(new FaceFieldEvaluator(), startF, startF);
		srAnim.setDuration(DEFAULT_DURATION);
		srAnim.setInterpolator(new LinearInterpolator());
		srAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				FaceField pf = (FaceField) animation.getAnimatedValue();
				image.setRotation(pf.degrees - 30);
				image.setScaleX(pf.scale + 0.8f);
				image.setScaleY(pf.scale + 0.8f);
			}
		});

		if (line == 0 && column == 1) {
			srAnim.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					if (mOnGiftAnimationListener != null) {
						mOnGiftAnimationListener.onGiftAnimationEnd(FaceRainView.this);
					}

				}

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub

				}
			});
		}

		srAnim.start();

	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		removeAllViews();
	}

}
