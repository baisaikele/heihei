package com.heihei.dialog;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.animator.NumberEvaluator;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.utils.MD5;
import com.base.utils.ThreadManager;
import com.heihei.adapter.GiftAdapter;
import com.heihei.adapter.OnItemClickListener;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.logic.GiftController.OnGiftListGetListener;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.PmPresent;
import com.heihei.model.Gift;
import com.wmlives.heihei.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GiftDialog extends Dialog implements android.view.View.OnClickListener {

	public static final int TYPE_LIVE = 0;// 直播
	public static final int TYPE_CHAT = 1;// 对聊

	// ----------------R.layout.dialog_gift-------------Start
	private android.support.v4.view.ViewPager viewpager;
	private Button btn_send;
	private LinearLayout btn_recharge;
	private TextView tv_diamond_num;
	private RelativeLayout btn_continue;
	private TextView tv_continue_num;

	public void autoLoad_dialog_gift() {
		viewpager = (android.support.v4.view.ViewPager) findViewById(R.id.viewpager);
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_recharge = (LinearLayout) findViewById(R.id.btn_recharge);
		tv_diamond_num = (TextView) findViewById(R.id.tv_diamond_num);
		btn_continue = (RelativeLayout) findViewById(R.id.btn_continue);
		tv_continue_num = (TextView) findViewById(R.id.tv_continue_num);
	}

	// ----------------R.layout.dialog_gift-------------End

	public GiftDialog(Context context, String customId, String userId, int type) {
		super(context, R.style.ActionSheet_Not_Dark);

		if (context instanceof Activity) {
			setOwnerActivity((Activity) context);
		}
		this.userId = userId;
		this.type = type;
		this.customId = customId;
		WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		mWm.getDefaultDisplay().getMetrics(dm);
		Window w = getWindow();
		w.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = dm.widthPixels;
		w.setAttributes(lp);
	}

	private int type = TYPE_LIVE;
	private String userId = "";
	private String customId = "";
	private List<List<Gift>> data;

	private GiftPagerAdapter mGiftPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_gift);
		autoLoad_dialog_gift();

		btn_send.setOnClickListener(this);
		btn_recharge.setOnClickListener(this);

		if (data == null)
			data = new ArrayList<List<Gift>>();

		btn_send.setEnabled(false);
		btn_continue.setOnClickListener(this);
		tv_diamond_num.setText(UserMgr.getInstance().getLoginUser().goldCount + "");
		requestGiftList();
		EventManager.ins().registListener(EventTag.DIAMOND_CHANGED, mDiamondListener);
	}

	EventListener mDiamondListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			tv_diamond_num.setText(UserMgr.getInstance().getLoginUser().goldCount + "");
		}
	};

	private LivePresent mLivePresent = new LivePresent();

	private void requestGiftList() {

		GiftController.getInstance().requestGiftList(new OnGiftListGetListener() {

			@Override
			public void onGiftGet(List<Gift> mGifts) {
				if (mGifts != null)
					refreshAdapter(mGifts);
			}
		});
	}

	private void refreshAdapter(List<Gift> gifts) {
		data.clear();
		if (gifts != null && gifts.size() > 0) {
			List<Gift> innerGifts = null;
			for (int i = 0; i < gifts.size(); i++) {
				Gift gift = gifts.get(i);
				if (innerGifts == null) {
					innerGifts = new ArrayList<>();
				}
				innerGifts.add(gift);
				if (innerGifts.size() == 10) {
					data.add(innerGifts);
					innerGifts = null;
				}
			}
		}

		viewpager.setAdapter(new GiftPagerAdapter());
	}

	/**
	 * 选中礼物
	 * 
	 * @param position
	 *            当前处于ViewPager的position
	 * @param index
	 *            当前处于RecyclerView的index
	 */
	private void selectGift(int position, int index) {
		btn_send.setEnabled(true);
		if (currentSelectPosition == position && currentSelectIndex == index)
			return;

		btn_continue.setVisibility(View.GONE);
		btn_send.setVisibility(View.VISIBLE);
		giftAmount = 1;

		if (currentSelectPosition != -1 && currentSelectIndex != -1) {
			Gift gift = data.get(currentSelectPosition).get(currentSelectIndex);
			gift.isSelected = false;
			RecyclerView rv = rvs.get(currentSelectPosition);
			if (rv != null)
				rv.getAdapter().notifyItemChanged(currentSelectIndex);
		}

		Gift gift = data.get(position).get(index);
		gift.isSelected = true;

		RecyclerView rv = rvs.get(position);
		if (rv != null)
			rv.getAdapter().notifyItemChanged(index);

		currentSelectPosition = position;
		currentSelectIndex = index;
	}

	public Gift getSelectedGift() {
		if (currentSelectPosition < 0 || currentSelectIndex < 0)
			return null;

		return data.get(currentSelectPosition).get(currentSelectIndex);
	}

	private int currentSelectPosition = -1;
	private int currentSelectIndex = -1;
	private static final int SPAN_COUNT = 5;// 列数
	private SparseArray<RecyclerView> rvs;

	class GiftPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			if (data == null)
				return 0;
			return data.size();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// super.destroyItem(container, position, object);
			// container.removeView((View) object);
			// rvs.remove(position);
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {

			if (rvs == null) {
				rvs = new SparseArray<>();
			}

			if (rvs.get(position) != null) {
				return rvs.get(position);
			}

			RecyclerView view = new RecyclerView(getContext());
			ViewGroup.LayoutParams parmas = new ViewGroup.LayoutParams(-1, -1);
			container.addView(view, parmas);

			view.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
			GiftAdapter mAdapter = new GiftAdapter(data.get(position), getContext());
			mAdapter.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(int index) {
					selectGift(position, index);
				}
			});
			view.setAdapter(mAdapter);

			rvs.put(position, view);

			return view;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();

		if (rvs != null) {
			rvs.clear();
		}
		EventManager.ins().removeListener(EventTag.DIAMOND_CHANGED, mDiamondListener);
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}

	private int giftAmount = 0;

	private long giftContinueTime = 0l;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			tv_diamond_num.setText(String.valueOf(msg.arg1));
		};
	};

	/**
	 * 购买
	 * 
	 * @param gift
	 */
	private void buyGift(final Gift gift) {

		if (giftAmount == 1) {
			giftContinueTime = System.currentTimeMillis();
		}

		String uid = UserMgr.getInstance().getUid();
		final String gift_uuid = MD5.getMD5(uid + gift.id + giftContinueTime);
		gift.gift_uuid = gift_uuid;

		if (TYPE_LIVE == type) {

			if (mOnGiftSendListener != null) {
				if (UserMgr.getInstance().getLoginUser().goldCount > gift.gold) {
					UserMgr.getInstance().getLoginUser().goldCount = (UserMgr.getInstance().getLoginUser().goldCount - gift.gold);
					mOnGiftSendListener.onGiftSend(gift, giftAmount);
				}
			}
			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					int tryNum = 0;
					while (tryNum < 3) {
						tryNum++;
						final JSONObject object = mLivePresent.buyGift(gift.id, customId, giftAmount, gift_uuid);
						if (object == null)
							continue;
						int errCode = object.optInt("errorcode", -1);
						if (errCode == ErrorCode.ERROR_OK) {
							ThreadManager.getInstance().getHandler().post(new Runnable() {

								@Override
								public void run() {
									int gold = object.optInt("gold");
									giftAmount += 1;
									Message msg = Message.obtain();
									msg.arg1 = gold;
									mHandler.sendMessage(msg);
									UserMgr.getInstance().getLoginUser().goldCount = gold;
								}
							});
							break;
						} else if (errCode == ErrorCode.ERROR_MONEY_NOT_ENOUGH) {
							ThreadManager.getInstance().getHandler().post(new Runnable() {

								@Override
								public void run() {
									showExchargeDialog();
									btn_continue.setVisibility(View.GONE);
									btn_send.setVisibility(View.VISIBLE);
									stopCountDown();
								}
							});
							break;
						}
					}
				}
			});

			if (gift.type == Gift.TYPE_NORMAL) {
				btn_continue.setVisibility(View.VISIBLE);
				btn_send.setVisibility(View.GONE);
				startCountDown();
			}
		} else if (TYPE_CHAT == type) {

			if (mOnGiftSendListener != null) {
				if (UserMgr.getInstance().getLoginUser().goldCount > gift.gold) {
					UserMgr.getInstance().getLoginUser().goldCount = (UserMgr.getInstance().getLoginUser().goldCount - gift.gold);
					mOnGiftSendListener.onGiftSend(gift, giftAmount);
				}
			}

			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					int tryNum = 0;
					while (tryNum < 3) {
						tryNum++;
						final JSONObject object = PmPresent.getInstance().buyGift(customId, userId, gift.id, gift_uuid, giftAmount);
						if (object == null)
							continue;
						int errCode = object.optInt("errorcode", -1);
						if (errCode == ErrorCode.ERROR_OK) {
							ThreadManager.getInstance().getHandler().post(new Runnable() {

								@Override
								public void run() {
									giftAmount += 1;
									int gold = object.optInt("gold");
									Message msg = Message.obtain();
									msg.arg1 = gold;
									mHandler.sendMessage(msg);
								}
							});
							break;
						} else if (errCode == ErrorCode.ERROR_MONEY_NOT_ENOUGH) {
							ThreadManager.getInstance().getHandler().post(new Runnable() {

								@Override
								public void run() {
									showExchargeDialog();
									btn_continue.setVisibility(View.GONE);
									btn_send.setVisibility(View.VISIBLE);
									stopCountDown();
								}
							});
							break;
						}
					}
				}
			});
			if (gift.type == Gift.TYPE_NORMAL) {

				btn_continue.setVisibility(View.VISIBLE);
				btn_send.setVisibility(View.GONE);
				startCountDown();
			}
		}
	}

	private ValueAnimator mValueAnimator;

	/**
	 * 开始倒计时
	 */
	private void startCountDown() {
		if (mValueAnimator != null) {
			mValueAnimator.removeAllListeners();
			mValueAnimator.removeAllUpdateListeners();
			mValueAnimator.isRunning();
			mValueAnimator.cancel();
			mValueAnimator = null;
		}

		final int max = 30;
		final int min = 0;

		mValueAnimator = ValueAnimator.ofObject(new NumberEvaluator(), max, min);
		mValueAnimator.setDuration(3000);
		mValueAnimator.setInterpolator(new LinearInterpolator());
		mValueAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				int num = (int) animation.getAnimatedValue();
				tv_continue_num.setText(String.valueOf(num));
				if (num == min) {
					btn_continue.setVisibility(View.GONE);
					btn_send.setVisibility(View.VISIBLE);
					giftAmount = 1;
				}
			}
		});
		mValueAnimator.start();

	}

	/**
	 * 结束倒计时
	 */
	public void stopCountDown() {
		if (mValueAnimator != null) {
			mValueAnimator.removeAllListeners();
			mValueAnimator.removeAllUpdateListeners();
			mValueAnimator.isRunning();
			mValueAnimator.cancel();
			mValueAnimator = null;
			giftAmount = 1;
		}
	}

	/**
	 * 显示充值提示框
	 */
	private void showExchargeDialog() {
		TipDialog td = new TipDialog(getOwnerActivity());
		td.setContent(getContext().getResources().getString(R.string.excharge_gift_tip));
		td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

			@Override
			public void onOkClick(Dialog dialog) {
				dialog.dismiss();
				NavigationController.gotoRechargeFragment(getContext());
			}

			@Override
			public void onCancleClick(Dialog dialog) {
				dialog.dismiss();

			}
		});
		td.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_send: {
			Gift gift = getSelectedGift();
			if (gift == null) {
				return;
			}
			buyGift(gift);
		}

			break;
		case R.id.btn_recharge:// 充值

			NavigationController.gotoRechargeFragment(getContext());

			break;
		case R.id.btn_continue:// 连送
		{
			Gift gift = getSelectedGift();
			if (gift == null) {
				return;
			}
			buyGift(gift);
		}
			break;
		}

	}

	private OnGiftSendListener mOnGiftSendListener;

	public void setOnGiftSendListener(OnGiftSendListener mOnGiftSendListener) {
		this.mOnGiftSendListener = mOnGiftSendListener;
	}

	public static interface OnGiftSendListener {

		public void onGiftSend(Gift gift, int amount);
	}

}
