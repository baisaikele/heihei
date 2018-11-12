package com.base.widget.message;

import java.lang.ref.WeakReference;

import com.base.host.AppLogic;
import com.base.widget.LiveFreeScrollView;
import com.facebook.fresco.FrescoImageHelper;
import com.facebook.fresco.FrescoParam;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.widget.LiveBottom;
import com.heihei.logic.UserMgr;
import com.heihei.model.LiveInfo;
import com.heihei.model.User;
import com.heihei.model.msg.bean.AbstractTextMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.SystemMessage;
import com.heihei.model.msg.bean.TextMessage;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LiveFreeTextViewHolder {
	private int TOTAL_VIEWS_LIMIT = 1000;
	private static WeakReference<Context> mOwner;
	private LiveFreeScrollView mScrollView;
	private LinearLayout mChatContainer;

	public LiveFreeTextViewHolder(Context ctx, LiveFreeScrollView scrollView, LinearLayout chatContainer) {
		mOwner = new WeakReference<>(ctx);
		this.mScrollView = scrollView;
		this.mChatContainer = chatContainer;
		if (mScrollView != null) {
			mScrollView.setOnTouchListener(mOnTouchListener);
		}
	}

	private long timestamp = 0;
	private static LiveInfo mLiveInfo;
	private static LiveBottom mLiveBottom;
	private OnTouchListener mOnTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mScrollView.getScrollIsBottom()) {
				timestamp = 0;
			} else {
				timestamp = System.currentTimeMillis();
			}

			return false;
		}
	};

	public void setLiveBottom(LiveInfo info, LiveBottom bottom) {
		mLiveInfo = info;
		mLiveBottom = bottom;
	}

	public void updateBulletMessage(BulletMessage msg) {
		final View view = msg.accept(new ViewDisplayVisitor(mOwner.get()));
		update(view);
	}

	public void updateSystemMessage(SystemMessage msg) {
		final View view = msg.accept(new ViewDisplayVisitor(mOwner.get()));
		update(view);
	}

	public void updateLiveMessage(LiveMessage msg) {
		final View view = msg.accept(new ViewDisplayVisitor(mOwner.get()));
		update(view);
	}

	public void updateAbstractTextMessage(AbstractTextMessage msg) {
		final View view = msg.accept(new ViewDisplayVisitor(mOwner.get()));
		update(view);
	}

	private void update(final View view) {
		((Activity) mOwner.get()).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateChatContainer(view);
			}
		});
	}

	private void updateChatContainer(final View view) {
		if (mChatContainer.getChildCount() == TOTAL_VIEWS_LIMIT) {
			mChatContainer.removeViewAt(0);
		}

		mChatContainer.addView(view);
		if (System.currentTimeMillis() - timestamp > 2000) {
			scrollToBottom();
		}
	}

	public void scrollToBottom() {
		mScrollView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mScrollView.fullScroll(View.FOCUS_DOWN);
			}
		}, 1000);
	}

	private static class ViewDisplayVisitor implements MessagePart.Visitor {

		private static final int TEXT_MAX_LINES = 4;

		private final WeakReference<Context> owner;

		private ViewDisplayVisitor(Context ctx) {
			owner = new WeakReference<>(ctx);
		}

		@Override
		public View visit(SystemMessage msg) {
			return createSystemMsgView(msg);
		}

		@Override
		public View visit(LiveMessage msg) {
			return createLiveMsgView(msg);
		}

		@Override
		public View visit(BulletMessage msg) {
			return createTextMsgView(msg);
		}

		@Override
		public View visit(AbstractTextMessage msg) {
			return createTextMsgView(msg);
		}

		private View createSystemMsgView(final SystemMessage msg) {
			final Context ctx = owner.get();
			View view = View.inflate(ctx, R.layout.scroll_item_system_message, null);
			TextView tv_content = (TextView) view.findViewById(R.id.danmaku_text);
			tv_content.setText(msg.text);
			return view;
		}

		private View createTextMsgView(final AbstractTextMessage msg) {
			final Context ctx = owner.get();
			View view = View.inflate(ctx, R.layout.scroll_item_system_message, null);
			TextView tv_nickname = (TextView) view.findViewById(R.id.danmaku_text);
			ImageView iv_gift = (ImageView) view.findViewById(R.id.danmaku_icon);
			tv_nickname.setText(msg.fromUserName + "：");

			if (msg.gender == User.FEMALE) {
				tv_nickname.setTextColor(ctx.getResources().getColor(R.color.hh_color_female));
			} else {
				tv_nickname.setTextColor(ctx.getResources().getColor(R.color.hh_color_male));
			}

			if (msg instanceof TextMessage) {
				int id = ((TextMessage) msg).giftId;
				if (id != -1) {
					iv_gift.setVisibility(View.VISIBLE);
					FrescoParam param = new FrescoParam(AppLogic.gifts.get(id));
					FrescoImageHelper.getImage(param, iv_gift);
				} else {
					iv_gift.setVisibility(View.GONE);
				}
			}
			tv_nickname.append(createSpannable(msg.text, Color.parseColor("#ffffff")));
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (msg.fromUserId.equals(UserMgr.getInstance().getUid()))
						return;

					User u = new User();
					u.uid = msg.fromUserId;
					u.nickname = msg.fromUserName;
					u.avatar = msg.coverUrl;
					if (mLiveInfo != null && (UserMgr.getInstance().getUid().equals(mLiveInfo.creator.uid) || msg.fromUserId.equals(mLiveInfo.creator.uid))) {
						UserDialog dialog = new UserDialog(ctx, u, mLiveInfo.liveId, true, UserDialog.USERDIALOG_OPENTYPE_ANCHOR);
						dialog.show();
					} else if (mLiveInfo != null && mLiveInfo.liveId != null) {
						UserDialog dialog = new UserDialog(ctx, u, mLiveInfo.liveId, false, UserDialog.USERDIALOG_OPENTYPE_AUDIENCE);
						dialog.show();
					} else {
						UserDialog dialog = new UserDialog(ctx, u, "", false, UserDialog.USERDIALOG_OPENTYPE_AUDIENCE);
						dialog.show();

					}

				}
			});
			view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (UserMgr.getInstance().getUid().equals(msg.fromUserId)) {
						return true;
					}
					if (mLiveBottom != null)
						mLiveBottom.atUser(msg.fromUserName);

					return true;
				}
			});
			return view;
		}

		private View createLiveMsgView(final LiveMessage msg) {
			final Context ctx = owner.get();
			View view = View.inflate(ctx, R.layout.scroll_item_system_message, null);
			TextView tv_nickanme = (TextView) view.findViewById(R.id.danmaku_text);

			if (msg.gender == User.FEMALE) {
				tv_nickanme.setTextColor(ctx.getResources().getColor(R.color.hh_color_female));
				tv_nickanme.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hh_live_heart_female, 0);
				tv_nickanme.setCompoundDrawablePadding(8);
			} else {
				tv_nickanme.setTextColor(ctx.getResources().getColor(R.color.hh_color_male));
				tv_nickanme.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hh_live_heart_male, 0);
				tv_nickanme.setCompoundDrawablePadding(8);
			}

			tv_nickanme.setText(msg.fromUserName + "：");
			tv_nickanme.append(createSpannable(msg.text, Color.parseColor("#ffffff")));

			return view;
		}

		private static Spannable createSpannable(final CharSequence msg, final int color) {
			Spannable span = new SpannableString(msg);
			span.setSpan(new ForegroundColorSpan(color), 0, span.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			return span;
		}
	}
}
