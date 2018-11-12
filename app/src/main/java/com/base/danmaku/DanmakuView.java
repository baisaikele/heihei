package com.base.danmaku;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.base.animator.NumberEvaluator;
import com.base.host.HostApplication;
import com.base.utils.LogWriter;
import com.wmlives.heihei.R;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

public class DanmakuView extends FrameLayout {

	public static final int DEFAULT_FRAME = 1000 / 10;

	public static final int SPACING_HOR = 20;// 水平行间距

	private int maxLines = 0;

	private int width = 0;
	private int height = 0;

	private static final int itemHeight = HostApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.t31dp);

	private LayoutInflater mInflater;

	private DanmakuHandler mHandler;

	private LinkedList<DanmakuItem> mWaitQuene = new LinkedList<DanmakuItem>();

	private RecycleBin mRecycleBin = new RecycleBin();

	public DanmakuView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public DanmakuView(Context context) {
		super(context);
		init();
	}

	public DanmakuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mHandler = new DanmakuHandler();
		mWaitQuene.clear();

		if (lineViews == null) {
			lineViews = new SparseArray<List<DanmakuItemView>>();
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			DanmakuItemView child = (DanmakuItemView) getChildAt(i);
			int widthMs = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			int heightMs = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
			measureChild(child, widthMs, heightMs);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
	}

	@Override
	public void addView(View child) {
		if (!(child instanceof DanmakuItemView)) {
			throw new RuntimeException("child must be DanmakuItemView");
		}
		if (lineViews != null) {
			int line = getLineByPositionY((int) ((DanmakuItemView) child).getY());
			List<DanmakuItemView> views = lineViews.get(line);
			if (views != null) {
				views.add((DanmakuItemView) child);
			} else {
				views = new ArrayList<DanmakuItemView>();
				views.add((DanmakuItemView) child);
				lineViews.put(line, views);
			}
		}
		super.addView(child);
	}

	@Override
	public void removeAllViews() {
		super.removeAllViews();
		if (lineViews != null) {
			lineViews.clear();
		}
	}

	@Override
	public void removeViewInLayout(View view) {
		super.removeViewInLayout(view);
		if (lineViews != null) {
			int line = getLineByPositionY((int) (((DanmakuItemView) view).getY()));
			List<DanmakuItemView> views = lineViews.get(line);
			if (views != null) {
				boolean success = views.remove(((DanmakuItemView) view));
				LogWriter.d("danmaku", "remove view:" + success);
			}
		}
	}

	public void addDanmakuItem(DanmakuItem item) {
		mWaitQuene.add(item);

		if (mWaitQuene.size() > 0) {
			status = STATUS_RUNNIND;
			mHandler.removeMessages(0);
			Message msg = Message.obtain();
			msg.what = 0;
			msg.obj = this;
			mHandler.sendMessageDelayed(msg, DEFAULT_FRAME);
		}

	}

	private void addView(final DanmakuItem item) {
		if (mInflater == null) {
			mInflater = LayoutInflater.from(getContext());
		}

		DanmakuItemView view = mRecycleBin.getView(item.type);// 先从复用列表里找

		if (view == null) {
			LogWriter.d("danmakuView", "create view");
			switch (item.type) {
			case DanmakuItem.TYPE_NORMAL:
				view = (DanmakuItemView) mInflater.inflate(R.layout.item_danmaku_text, null);
				break;
			case DanmakuItem.TYPE_COLOR_BG:
				view = (DanmakuItemView) mInflater.inflate(R.layout.item_danmaku_color_bg, null);
				break;
			case DanmakuItem.TYPE_LIKE:
				view = (DanmakuItemView) mInflater.inflate(R.layout.item_danmaku_like, null);
				break;
			case DanmakuItem.TYPE_GIFT:
				break;
			case DanmakuItem.TYPE_SYSTEM:
				view = (DanmakuItemView) mInflater.inflate(R.layout.item_danmaku_system_message, null);
				break;
			default:
				throw new RuntimeException("type is unknown");
			}
		}

		view.setData(item);
		view.refreshView();

		int widthMs = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		int heightMs = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

		view.measure(widthMs, heightMs);

		view.setX(width);

		int positionY = createPositionY(view);
		LogWriter.d("danmakuView", "create Y:" + positionY);
		if (positionY == -1) {
			mWaitQuene.addFirst(item);
			return;
		}
		view.setY(positionY - getPaddingTop());
		addView(view);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.OnItemClick(item);
				}

			}
		});

		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (mOnItemClickListener != null) {
					mOnItemClickListener.onItemLongClick(item);
				}
				return true;
			}
		});

		final DanmakuItemView child = view;

		int startX = width;
		int endX = -view.getMeasuredWidth();

		LogWriter.d("danmaku", "speed--" + view.getSpeed() + " startX " + startX + " endX " + endX);

		int spleed = 100;
		switch (width) {
		case 540:
			spleed = 110;
			break;
		case 720:
			spleed = 85;
			break;
		case 1080:
			spleed = 60;
			break;
		case 1440:
			spleed = 42;
			break;
		default:
			spleed = 100;
			break;
		}

		int duration = (Math.abs(startX - endX) * spleed) / view.getSpeed();
		LogWriter.d("danmaku", "duration--" + duration + "--speed--" + view.getSpeed());
		ValueAnimator ani = ValueAnimator.ofFloat(width, -view.getMeasuredWidth());
		// ValueAnimator ani = ValueAnimator.ofObject(new NumberEvaluator(),
		// width, -view.getMeasuredWidth());
		ani.setDuration(duration);
		ani.setInterpolator(new LinearInterpolator());
		ani.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float x = (float) animation.getAnimatedValue();
				child.setX(x);
			}
		});
		ani.addListener(new AnimatorListener() {

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
				removeViewInLayout(child);
				mRecycleBin.addRecycleView(child.getData().type, child);
			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});
		ani.start();
		view.setMoveAnimator(ani);

	}

	/**
	 * 用来存储每一行上的view，分行处理
	 */
	SparseArray<List<DanmakuItemView>> lineViews = null;

	/**
	 * 通过Y坐标计算出是哪一行
	 * 
	 * @param y
	 * @return
	 */
	private int getLineByPositionY(int y) {
		y = y + getPaddingTop();
		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();

		int realHeight = height - paddingTop - paddingBottom;// 真正可填充的高度
		maxLines = realHeight / itemHeight;// 最大行数
		int spacing = 0;
		if (maxLines > 1) {
			spacing = (realHeight - maxLines * itemHeight) / (maxLines - 1);// 行间距
		}

		return (y - paddingTop) / (itemHeight + spacing);
	}

	private int createPositionY(DanmakuItemView child) {

		int paddingTop = getPaddingTop();
		int paddingBottom = getPaddingBottom();

		int realHeight = height - paddingTop - paddingBottom;// 真正可填充的高度
		maxLines = realHeight / itemHeight;// 最大行数
		int spacing = 0;
		if (maxLines > 1) {
			spacing = (realHeight - maxLines * itemHeight) / (maxLines - 1);// 行间距
		}

		int line = -1;

		List<Integer> lines = getCanJoinLines(child);
		if (lines != null && lines.size() > 0) {
			int index = (int) (Math.random() * lines.size());// 从可以插入的行中随机一行出来
			line = lines.get(index);
		}

		int y = -1;
		if (line != -1)// 找到空余行了
		{
			y = line * (itemHeight + spacing) + paddingTop;// 算出行数对应的y坐标
		}
		return y;
	}

	/**
	 * 获取到可以加入的空行集合
	 * 
	 * @return
	 */
	private List<Integer> getCanJoinLines(DanmakuItemView view) {

		if (maxLines <= 0) {
			int paddingTop = getPaddingTop();
			int paddingBottom = getPaddingBottom();

			int realHeight = height - paddingTop - paddingBottom;// 真正可填充的高度
			maxLines = realHeight / itemHeight;// 最大行数
		}

		List<Integer> lines = new ArrayList<Integer>();
		for (int i = 0; i < maxLines; i++) {// 判断哪一行可以添加view
			List<DanmakuItemView> views = lineViews.get(i);
			if (views == null || views.size() <= 0) {
				lines.add(i);
			} else {
				DanmakuItemView child = views.get(views.size() - 1);// 获取到每一行最后一个弹幕
				if (child.getX() + child.getMeasuredWidth() + SPACING_HOR <= width) {
					if (view != null) {// 开始判断同向追赶问题
						if (child.getSpeed() >= view.getSpeed())// 如果前面的速度快，永远追不上
						{
							lines.add(i);
						} else {// 如果前面的速度慢，开始判断能否追上
							// 追上所需要的时间\\bug java.lang.ArithmeticException:
							// divide by zero
							int time = (int) (width - (child.getX() + child.getMeasuredWidth())) / (view.getSpeed() - child.getSpeed());
							int s = time * view.getSpeed();
							if (s >= width) {
								lines.add(i);
							}
						}
					} else {
						lines.add(i);
					}
				}
			}
		}
		return lines;
	}

	/**
	 * 判断是否能插入
	 * 
	 * @return
	 */
	private boolean canJoin() {
		return getCanJoinLines(null).size() > 0;
	}

	public void tick() {
		mHandler.removeMessages(0);

		if (getChildCount() <= 0 && mWaitQuene.size() <= 0) {// 如果没有弹幕了，暂停
			pause();
		}

		if (mWaitQuene.size() > 0 && canJoin()) {
			DanmakuItem item = mWaitQuene.removeFirst();
			addView(item);
		}

		if (status == STATUS_RUNNIND && mWaitQuene.size() > 0) {
			Message msg = Message.obtain();
			msg.what = 0;
			msg.obj = this;
			mHandler.sendMessageDelayed(msg, DEFAULT_FRAME);
		}

	}

	private static class DanmakuHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			DanmakuView danmakuView = (DanmakuView) msg.obj;
			danmakuView.tick();
		}
	}

	/**
	 * 暂停
	 */
	public void pause() {
		synchronized (mLock) {
			status = STATUS_PAUSED;
			mHandler.removeMessages(0);
			pauseAllChild();

		}
	}

	private void pauseAllChild() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			DanmakuItemView child = (DanmakuItemView) getChildAt(i);
			ValueAnimator ani = child.getMoveAnimator();
			if (ani != null) {
				ani.pause();
			}
		}
	}

	private void resumeAllChild() {
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			DanmakuItemView child = (DanmakuItemView) getChildAt(i);
			ValueAnimator ani = child.getMoveAnimator();
			if (ani != null) {
				ani.resume();
			}
		}
	}

	public boolean isRunning() {
		synchronized (mLock) {
			return status == STATUS_RUNNIND;
		}
	}

	/**
	 * 恢复
	 */
	public void resume() {
		synchronized (mLock) {
			status = STATUS_RUNNIND;
			// mLock.notifyAll();

			resumeAllChild();

			Message msg = Message.obtain();
			msg.what = 0;
			msg.obj = this;
			mHandler.sendMessageDelayed(msg, DEFAULT_FRAME);

		}
	}

	/**
	 * 停止
	 */
	public void stop() {
		mHandler.removeMessages(0);
		removeAllViews();
		mRecycleBin.clear();
		if (lineViews != null) {
			lineViews.clear();
		}

		if (mWaitQuene != null) {
			mWaitQuene.clear();
		}

		synchronized (mLock) {
			status = STATUS_IDLE;
			mLock.notifyAll();
		}
	}

	public static final int STATUS_IDLE = 0;// 初始状态或者停止状态
	public static final int STATUS_RUNNIND = 1;// 运行状态
	public static final int STATUS_PAUSED = 2;// 暂停状态

	private int status = STATUS_IDLE;

	private byte[] mLock = new byte[0];

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stop();
	}

	private LinkedList<Long> times;

	/** Calculates and returns frames per second */
	private double fps() {

		if (times == null) {
			times = new LinkedList<Long>();
		}

		long lastTime = System.nanoTime();
		times.addLast(lastTime);
		double NANOS = 1000000000.0;
		double difference = (lastTime - times.getFirst()) / NANOS;
		int size = times.size();
		int MAX_SIZE = 100;
		if (size > MAX_SIZE) {
			times.removeFirst();
		}
		return difference > 0 ? times.size() / difference : 0.0;
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
		this.mOnItemClickListener = mOnItemClickListener;
	}

	public static interface OnItemClickListener {

		public void OnItemClick(DanmakuItem item);

		public void onItemLongClick(DanmakuItem item);
	}

}
