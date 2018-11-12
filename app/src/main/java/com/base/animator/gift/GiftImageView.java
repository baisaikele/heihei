package com.base.animator.gift;

import com.base.animator.gift.GiftAnimation.GiftAnimationListener;
import com.heihei.fragment.live.logic.GiftAnimationController;
import com.heihei.fragment.live.logic.OnGiftAnimationListener;
import com.heihei.model.AudienceGift;
import com.wmlives.heihei.R;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GiftImageView extends RelativeLayout implements GiftAnimationListener {

	private GiftAnimation anim;
	private OnGiftAnimationListener mOnGiftAnimationListener;

	private RelativeLayout rl_title;
	private TextView tv_nickname;
	private TextView tv_gift_name;
	private ImageView iv_image;

	public GiftImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public GiftImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GiftImageView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_gift_imageview, this);
		rl_title = (RelativeLayout) findViewById(R.id.rl_title);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_gift_name = (TextView) findViewById(R.id.tv_gift_name);
		iv_image = (ImageView) findViewById(R.id.iv_image);
	}

	public void setData(AudienceGift aGift) {
		tv_nickname.setText(aGift.fromUser.nickname);
		tv_gift_name.setText(aGift.gift.name + "送");
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void start(int animType, OnGiftAnimationListener mOnGiftAnimationListener) {
		this.mOnGiftAnimationListener = mOnGiftAnimationListener;
		if (animType == GiftAnimationController.ANIM_DIAMOND) {
			GiftAnimation anim = GiftAnimationUtil.createDiamondGiftAnimation(getContext());
			startGiftAnimation(anim);
		} else if (animType == GiftAnimationController.ANIM_BAG) {
			GiftAnimation anim = GiftAnimationUtil.createBagGiftAnimation(getContext());
			startGiftAnimation(anim);
		}
	}

	private void startGiftAnimation(GiftAnimation anim) {

		clearAnimation();

		this.anim = anim;
		this.anim.setAnimationListener(this);
		this.anim.start();
	}

	public void clearGiftAnimation() {
		if (this.anim != null) {
			this.anim.setAnimationListener(null);
			this.anim.stop();
			this.anim = null;
		}
	}

	@Override
	public void onAnimationStart(GiftAnimation animation) {
		ValueAnimator scaleOutAnim = ValueAnimator.ofFloat(0.2f, 1.0f);
		scaleOutAnim.setDuration(350);
		scaleOutAnim.setInterpolator(new OvershootInterpolator(3.0f));
		scaleOutAnim.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float scale = (float) animation.getAnimatedValue();
				rl_title.setScaleX(scale);
				rl_title.setScaleY(scale);

			}
		});
		scaleOutAnim.start();
	}

	private boolean hasRunScaleInAnim = false;

	@Override
	public void onAnimationRunning(GiftAnimation animation, GiftAnimationItem item) {

		if (!hasRunScaleInAnim && animation.getTotalDuration() <= 240) {
			hasRunScaleInAnim = true;
			ValueAnimator scaleInAnim = ValueAnimator.ofFloat(1.0f, 0.2f);
			scaleInAnim.setDuration(250);
			scaleInAnim.setInterpolator(new LinearInterpolator());
			scaleInAnim.addUpdateListener(new AnimatorUpdateListener() {

				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					float scale = (float) animation.getAnimatedValue();
					rl_title.setScaleX(scale);
					rl_title.setScaleY(scale);
				}
			});
			scaleInAnim.start();
		}

		Drawable drawable = iv_image.getDrawable();
		if (drawable != null) {
			if (drawable instanceof BitmapDrawable) {
				if (((BitmapDrawable) drawable).getBitmap() != null && !((BitmapDrawable) drawable).getBitmap().isRecycled()) {
					((BitmapDrawable) drawable).getBitmap().recycle();
				}
			}

			drawable.setCallback(null);
		}

		if (item != null) {
			Drawable newDrawable = getResources().getDrawable(item.drawableId);
			iv_image.setImageDrawable(newDrawable);
		}

	}

	@Override
	public void onAnimationEnd(GiftAnimation animation) {

		iv_image.setImageDrawable(new ColorDrawable());
		if (this.mOnGiftAnimationListener != null) {
			this.mOnGiftAnimationListener.onGiftAnimationEnd(this);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		clearAnimation();
	}

}
