package com.base.widget.cobe.ptr.header;

import com.base.animator.AnimationDrawableUtil;
import com.base.utils.LogWriter;
import com.base.widget.cobe.ptr.PtrFrameLayout;
import com.base.widget.cobe.ptr.PtrUIHandler;
import com.base.widget.cobe.ptr.indicator.PtrIndicator;
import com.wmlives.heihei.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HeiheiPullHeader extends LinearLayout implements PtrUIHandler {

	public HeiheiPullHeader(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public HeiheiPullHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public HeiheiPullHeader(Context context) {
		super(context);
		init();

	}

	// ----------------R.layout.heihei_pull_header-------------Start
	private LinearLayout ll_pull;
	private ImageView head_top;
	private ImageView head_mid;
	private ImageView head_bottom;
	private ImageView pb;

	public void autoLoad_heihei_pull_header() {
		ll_pull = (LinearLayout) findViewById(R.id.ll_pull);
		head_top = (ImageView) findViewById(R.id.head_top);
		head_mid = (ImageView) findViewById(R.id.head_mid);
		head_bottom = (ImageView) findViewById(R.id.head_bottom);
		pb = (ImageView) findViewById(R.id.pb);
	}

	// ----------------R.layout.heihei_pull_header-------------End

	private Bitmap mBitmap = null;

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(getContext()).inflate(R.layout.heihei_pull_header, this);
		autoLoad_heihei_pull_header();
		mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pull_loading_mid);
	}

	@Override
	public void onUIReset(PtrFrameLayout frame) {
		ll_pull.setVisibility(View.VISIBLE);
		head_mid.setVisibility(View.GONE);
		pb.setVisibility(View.GONE);
		pb.setBackgroundDrawable(null);
	}

	@Override
	public void onUIRefreshPrepare(PtrFrameLayout frame) {
		ll_pull.setVisibility(View.VISIBLE);
		head_mid.setVisibility(View.GONE);
		pb.setVisibility(View.GONE);
		pb.setBackgroundDrawable(null);
	}

	private AnimationDrawable mShakeAnimation;
	private int shakeTotalDuration = 0;
	private AnimationDrawable mAnimation;

	@Override
	public void onUIRefreshBegin(PtrFrameLayout frame) {
		ll_pull.setVisibility(View.GONE);
		pb.setVisibility(View.VISIBLE);

		if (mShakeAnimation == null) {
			mShakeAnimation = AnimationDrawableUtil.createShakeAnim(getContext());
			int count = mShakeAnimation.getNumberOfFrames();
			for (int i = 0; i < count; i++) {
				shakeTotalDuration += mShakeAnimation.getDuration(i);
			}
		}

		if (mAnimation == null) {
			mAnimation = AnimationDrawableUtil.createPullLoadingAnim(getContext());
		}
		pb.setBackgroundDrawable(mShakeAnimation);// 先做抖动，在做波纹扩展的动画
		mShakeAnimation.start();
		postDelayed(new Runnable() {

			@Override
			public void run() {

				if (pb != null && mAnimation != null) {
					pb.setBackgroundDrawable(mAnimation);
					mAnimation.start();
				}
			}
		}, shakeTotalDuration);
	}

	@Override
	public void onUIRefreshComplete(PtrFrameLayout frame) {
		ll_pull.setVisibility(View.VISIBLE);
		head_mid.setVisibility(View.GONE);
		pb.setVisibility(View.GONE);
		pb.setBackgroundDrawable(null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (topHeight == 0) {
			topHeight = head_top.getMeasuredHeight();
		}

		if (bottomHeight == 0) {
			bottomHeight = head_bottom.getMeasuredHeight();
		}
	}

	private int topHeight = 0;
	private int bottomHeight = 0;

	@Override
	public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
		if (ptrIndicator != null) {
			try {
				int mOffsetToRefresh = ptrIndicator.getOffsetToRefresh();// 滑动到多少距离
																			// 开始刷新
				int currentPos = ptrIndicator.getCurrentPosY();// 已经滑动了多少距离

				if (mOffsetToRefresh != 0) {
					int maxMidHeight = mBitmap.getHeight();
					LogWriter.d("pull", "otr:" + mOffsetToRefresh + "--curPos:" + currentPos);
					LogWriter.d("pull", "topH:" + topHeight + "--bottomH:" + bottomHeight);
					if (currentPos <= topHeight + bottomHeight) {
						head_mid.setVisibility(View.GONE);
					} else if (currentPos <= (topHeight + maxMidHeight + bottomHeight)) {
						int midHeight = currentPos - (topHeight + bottomHeight);

						changeMidHeight(midHeight);
						head_mid.setVisibility(View.VISIBLE);
					} else {
						changeMidHeight(maxMidHeight);
						head_mid.setVisibility(View.VISIBLE);
					}
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	private void changeMidHeight(int height) {
		LinearLayout.LayoutParams params = (LayoutParams) head_mid.getLayoutParams();
		if (params != null) {
			params.height = height;
			head_mid.setLayoutParams(params);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mBitmap != null) {
			mBitmap.recycle();
			mBitmap = null;
		}
		mShakeAnimation = null;
		mAnimation = null;
	}

}
