package com.base.danmaku;

import com.base.host.HostApplication;
import com.base.utils.DeviceInfoUtils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 *  弹幕控制器
 * @author admin
 *
 */
public abstract class DanmakuItemView extends LinearLayout {

	public static final int SPEED_MIN = 7;
	public static final int SPEED_MAX = 9;

	public static final int WIDTH_MIN = (int) (DeviceInfoUtils.getScreenWidth(HostApplication.getInstance()) * 0.33);

	private int speed = 0;

	private long startTime = 0l;
	private long pauseTime = 0l;
	private int startX;
	private int endX;
	private int duration = 8000;
	private int giftId = -1;

	protected DanmakuItem item;

	private ValueAnimator mMoveAnimator;

	public DanmakuItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public DanmakuItemView(Context context) {
		super(context);
	}

	public DanmakuItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setStartX(int startX) {
		this.startX = startX;
	}

	public int getStartX() {
		return this.startX;
	}

	public void setEndX(int endX) {
		this.endX = endX;
		// this.duration = Math.abs(this.endX - this.startX) * 60 / SPEED;
	}

	public int getEndX() {
		return this.endX;
	}

	public int getSpeed() {
		if (this.speed > 0) {
			return this.speed;
		}

		int width = getMeasuredWidth();
		if (width <= WIDTH_MIN) {
			speed = SPEED_MAX;
			return speed;
		}
		speed = (SPEED_MAX - SPEED_MIN) * WIDTH_MIN / width + SPEED_MIN;
		if (speed <= SPEED_MIN) {
			speed = SPEED_MIN;
		}
		return speed;//13
	}

	public void setData(DanmakuItem item) {
		this.item = item;
		this.speed = 0;
	}

	public DanmakuItem getData() {
		return this.item;
	}

	public ValueAnimator getMoveAnimator() {
		return this.mMoveAnimator;
	}

	public void setMoveAnimator(ValueAnimator mMoveAnimator) {
		this.mMoveAnimator = mMoveAnimator;
	}

	public abstract void refreshView();

	private boolean hasMeasured = false;// 是否已经计算过宽高

	public void setHasMeasured(boolean hasMeasured) {
		this.hasMeasured = hasMeasured;
	}

	public boolean hasMeasured() {
		return hasMeasured;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		hasMeasured = true;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		this.mMoveAnimator = null;
	}

}
