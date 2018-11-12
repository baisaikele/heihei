package com.heihei.fragment.live.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.base.host.AppLogic;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.DeviceInfoUtils;
import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.base.widget.RippleBackground;
import com.base.widget.RoundedImageView;
import com.heihei.adapter.AudienceAdapter;
import com.heihei.adapter.AudienceGiftAdapter;
import com.heihei.dialog.UserDialog;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.AudienceGift;
import com.heihei.model.LiveInfo;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 直播界面的header部分
 * 
 * @author chengbo
 */
public class LiveHeader extends LinearLayout implements OnClickListener {

	public static final int TYPE_AUDIENCE = 0;// 听众端
	public static final int TYPE_ANCHOR = 1;// 主播端
	public static final int TYPE_REPLAY = 2;// 回放端

	// ----------------R.layout.header_live-------------Start
	private View hint_layout;
	public static WeakReference<View> hint_layout_WeakReference;
	private RelativeLayout ll_top_audience;
	private ImageButton btn_back;
	private TextView btn_follow;
	private RoundedImageView iv_avatar;
	private RippleBackground mRippleBackground;
	private TextView tv_nickname;
	private TextView tv_ticker;
	private TextView tv_topic;
	private ImageView btn_edit_topic;
	private android.support.v7.widget.RecyclerView audience_recycleview;
	private GiftThreeView gifts_content;

	private boolean isRunning = false;

	public void autoLoad_header_live() {
		ll_top_audience = (RelativeLayout) findViewById(R.id.ll_top_audience);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_follow = (TextView) findViewById(R.id.btn_follow);
		hint_layout = (View) findViewById(R.id.hint_layout);
		hint_layout_WeakReference = new WeakReference<View>(hint_layout);
		iv_avatar = (RoundedImageView) findViewById(R.id.iv_avatar);
		mRippleBackground = (RippleBackground) findViewById(R.id.content);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_ticker = (TextView) findViewById(R.id.tv_ticker);
		tv_topic = (TextView) findViewById(R.id.tv_topic);
		btn_edit_topic = (ImageView) findViewById(R.id.btn_edit_topic);
		audience_recycleview = (android.support.v7.widget.RecyclerView) findViewById(R.id.audience_recycleview);
		gifts_content = (GiftThreeView) findViewById(R.id.other_gift_layout);
	}

	// ----------------R.layout.header_live-------------End

	public LiveHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_header_live();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int top = getPaddingTop();
			top = top + DeviceInfoUtils.getStatusBarHeight(getContext());
			setPadding(0, top, 0, 0);
		}

		btn_follow.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		iv_avatar.setOnClickListener(this);
		tv_topic.setOnClickListener(this);
		btn_edit_topic.setOnClickListener(this);

		final LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

		audience_recycleview.setLayoutManager(manager);

//		audience_recycleview.addOnScrollListener(new OnScrollListener() {
//
//			@Override
//			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//				super.onScrollStateChanged(recyclerView, newState);
//				if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//					int firstVisiblePosition = manager.findFirstVisibleItemPosition();
//					int lastVisiblePosition = manager.findLastVisibleItemPosition();
//					if (firstVisiblePosition > 0 && lastVisiblePosition >= manager.getItemCount() - 1) {
//						LogWriter.d("recycle", "auto load more");
//						if (type != TYPE_REPLAY) {
//							if (audiences != null) {
//								long lastTime = audiences.get(audiences.size() - 1).joinTime;
//								getUserList(lastTime, true);
//							}
//						}
//					}
//				}
//			}
//		});


	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		isRunning = false;
		EventManager.ins().removeListener(EventTag.FOLLOW_CHANGED, followListener);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		EventManager.ins().registListener(EventTag.FOLLOW_CHANGED, followListener);
	}

	EventListener followListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			if (arg1 != 1) {
				if (mAnchor != null) {
					mAnchor.isFollowed = false;
					btn_follow.setVisibility(View.VISIBLE);
					btn_follow.setText(R.string.user_add_follow);
					btn_follow.setTextColor(getResources().getColor(R.color.hh_color_g));

				}
			} else {
				if (mAnchor != null) {
					mAnchor.isFollowed = true;
					btn_follow.setVisibility(View.VISIBLE);
					btn_follow.setText(R.string.user_followed);
					btn_follow.setTextColor(getResources().getColor(R.color.hh_color_b));
				}
			}

		}
	};

	private int type = TYPE_AUDIENCE;

	private List<User> audiences;// 听众数据

	private AudienceAdapter mAudienceAdapter;
	private AudienceGiftAdapter mGiftAdapter;

	private String liveId = "";
	private String topic = "";
	private LivePresent mLivePresent = new LivePresent();

	public void setType(int type) {
		isRunning = true;
		this.type = type;
		switch (type) {
		case TYPE_ANCHOR:// 主播端
			ll_top_audience.setVisibility(View.GONE);
			btn_edit_topic.setVisibility(View.VISIBLE);
			break;
		case TYPE_AUDIENCE:// 听众端
		case TYPE_REPLAY:
			ll_top_audience.setVisibility(View.VISIBLE);
			btn_edit_topic.setVisibility(View.GONE);
			break;
		}
	}

	public void setReplayInfo(PlayActivityInfo info) {
		this.mReplayInfo = info;
	}

	private PlayActivityInfo mReplayInfo = null;

	/**
	 * 设置直播主题
	 * 
	 * @param topic
	 */
	public void setLiveTopic(String topic) {
		this.topic = topic;
		tv_topic.setText(getResources().getString(R.string.live_topic, topic));
	}

	public void setLiveTotalTickets(int total) {

	}

	private User mAnchor;
	private UserPresent mUserPresent = new UserPresent();

	/**
	 * 设置主播信息
	 * 
	 * @param user
	 */
	public void setUserInfo(User user) {
		// FrescoImageHelper.getAvatar(FrescoImageHelper.getRandomImageUrl(),
		// iv_avatar);
		if (user != null) {
			this.mAnchor = user;
			iv_avatar.setUser(user, mRippleBackground, true);
			tv_nickname.setText(user.nickname);
			LogWriter.i("tv_ticker", "" + user.allEarnPoint);
			tv_ticker.setText(getResources().getString(R.string.user_dialog_ticker, user.allEarnPoint));
			if (user.isFollowed) {
				btn_follow.setVisibility(View.VISIBLE);
				btn_follow.setVisibility(View.VISIBLE);
				btn_follow.setText(R.string.user_followed);
				btn_follow.setTextColor(getResources().getColor(R.color.hh_color_b));
			} else {
				btn_follow.setVisibility(View.VISIBLE);
				btn_follow.setText(R.string.user_add_follow);
				btn_follow.setTextColor(getResources().getColor(R.color.hh_color_g));
			}

			if (user.uid.equals(UserMgr.getInstance().getUid())) {
				btn_follow.setVisibility(View.GONE);
			}
		}
	}

	private void followUser() {
		if (this.mAnchor != null) {
			if (this.mAnchor.isFollowed) {
				mUserPresent.unfollowUser(mAnchor.uid, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							mAnchor.isFollowed = false;
							btn_follow.setVisibility(View.VISIBLE);
							btn_follow.setText(R.string.user_add_follow);
							btn_follow.setTextColor(getResources().getColor(R.color.hh_color_g));
						} else {
							UIUtils.showToast(msg);
						}

					}
				});
			} else {
				mUserPresent.followUser(mAnchor.uid, this.liveId, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							mAnchor.isFollowed = true;
							btn_follow.setVisibility(View.VISIBLE);
							btn_follow.setText(R.string.user_followed);
							btn_follow.setTextColor(getResources().getColor(R.color.hh_color_b));
						} else {
							UIUtils.showToast(msg);
						}

					}
				});
			}

		}
	}

	/**
	 * 请求用户列表
	 * 
	 * @param liveId
	 */
	public void refreshUserList(String liveId) {
		if (StringUtils.isEmpty(liveId)) {
			return;
		}
		this.liveId = liveId;
		if (this.audiences == null) {
			this.audiences = new ArrayList<>();
		}
		this.audiences.clear();
		if (this.type == TYPE_AUDIENCE) {
			this.audiences.add(UserMgr.getInstance().getLoginUser());
		}

		mAudienceAdapter = new AudienceAdapter(this.audiences, getContext());
		if (this.type == TYPE_ANCHOR) {
			mAudienceAdapter.setLiveIdAndIsCreator(liveId, true);
		} else {
			mAudienceAdapter.setLiveIdAndIsCreator(liveId, false);
		}
		mAudienceAdapter.setReplayInfo(mReplayInfo);
		audience_recycleview.setAdapter(mAudienceAdapter);
		if (this.type != TYPE_REPLAY) {
			getUserList(0, true);
			if (isRunning) {
				Message message = Message.obtain();
				message.what = FLAG_REFRESH_USERLIST;
				message.obj = new WeakReference<LiveHeader>(this);
				mHandler.sendMessageDelayed(message, 5000 * 2);
			}
		} else {
			getReplayUserList();
		}

	}

	private void getReplayUserList() {
		mLivePresent.getLiveReplayAudiences(this.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

				if (errCode == ErrorCode.ERROR_OK) {
					audiences.clear();
					JSONArray userArr = json.optJSONArray("users");
					if (userArr != null && userArr.length() > 0) {
						for (int i = 0; i < userArr.length(); i++) {
							User user = new User(userArr.optJSONObject(i));
							if (user.uid.equals(UserMgr.getInstance().getUid())) {
								audiences.add(0, user);
							} else {
								audiences.add(user);
							}
							mAudienceAdapter.notifyDataSetChanged();
						}
					} else {
						if (type == TYPE_AUDIENCE) {
							audiences.add(UserMgr.getInstance().getLoginUser());
							mAudienceAdapter.notifyDataSetChanged();
						}
					}

				}
			}
		});
	}

	/**
	 * 获取观众数据
	 * 
	 * @param lastTime
	 * @param isNewest
	 */
	private void getUserList(long lastTime, final boolean isNewest) {

		mLivePresent.getLiveAudiences(this.liveId, lastTime, isNewest, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

				if (errCode == ErrorCode.ERROR_OK) {
					if (isNewest) {
						audiences.clear();
					}

					JSONArray userArr = json.optJSONArray("users");
					if (userArr != null && userArr.length() > 0) {
						for (int i = 0; i < userArr.length(); i++) {
							User user = new User(userArr.optJSONObject(i));
							if (user.uid.equals(UserMgr.getInstance().getUid())) {
								audiences.add(0, user);
							} else {
								audiences.add(user);
							}
							mAudienceAdapter.notifyDataSetChanged();
						}
					} else {
						if (type == TYPE_AUDIENCE) {
							audiences.add(UserMgr.getInstance().getLoginUser());
							mAudienceAdapter.notifyDataSetChanged();
						}
					}
				}
			}
		});
	}

	/**
	 * 设置听众数
	 * 
	 * @param audiences
	 */
	public void setAudiences(List<User> audiences) {
		if (audiences == null) {
			this.audiences = new ArrayList<>();
		} else {
			this.audiences = audiences;
		}

		mAudienceAdapter = new AudienceAdapter(this.audiences, getContext());
		audience_recycleview.setAdapter(mAudienceAdapter);
	}

	/**
	 * 进来一个听众
	 * 
	 * @param user
	 */
	public void addAudiences(User user) {
		if (this.audiences == null) {
			this.audiences = new ArrayList<>();
		}
		this.audiences.add(0, user);
		mAudienceAdapter.notifyDataSetChanged();
	}

	/**
	 * 增加一个礼物
	 * 
	 * @param mGift
	 */
	public void addGift(AudienceGift mGift) {
		gifts_content.addAudienceGift(mGift);
	}

	/**
	 * 设置礼物数据
	 * 
	 * @param data
	 */
	public void setGifts(List<Object> gifts) {
	}

	/**
	 * 清除所有礼物
	 */
	public void clearGifts() {
		gifts_content.clear();
	}

	/**
	 * 暂停礼物
	 */
	public void pauseGifts() {
		gifts_content.pause();

	}

	/**
	 * 恢复礼物
	 */
	public void resumeGifts() {
		gifts_content.resume();
	}

	private static final int FLAG_REFRESH_USERLIST = 0;

	private HeaderHandler mHandler = new HeaderHandler();

	private static class HeaderHandler extends Handler {

		public void handleMessage(android.os.Message msg) {
			removeMessages(0);
			WeakReference<LiveHeader> wr = (WeakReference<LiveHeader>) msg.obj;
			if (wr == null || wr.get() == null || !wr.get().isRunning) {
				return;
			}
			LiveHeader mHeader = wr.get();
			switch (msg.what) {
			case FLAG_REFRESH_USERLIST:
				mHeader.getUserList(0, true);

				Message message = Message.obtain();
				message.what = FLAG_REFRESH_USERLIST;
				message.obj = wr;
				sendMessageDelayed(message, 5000 * 2);
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			if (type == TYPE_ANCHOR) {
				((Activity) getContext()).finish();
			} else if (type == TYPE_AUDIENCE) {
				((Activity) getContext()).finish();
			}
			break;
		case R.id.iv_avatar:
			if (this.mAnchor != null) {
				if (!clickOlder())
					return;
				if (type == TYPE_ANCHOR) {
					UserDialog ud = new UserDialog(getContext(), mAnchor, liveId, true, UserDialog.USERDIALOG_OPENTYPE_ANCHOR);
					// ud.setChatable(false);
					ud.setReplayInfo(mReplayInfo);
					ud.show();
				} else if (type == TYPE_AUDIENCE || type == TYPE_REPLAY) {
					UserDialog ud = new UserDialog(getContext(), mAnchor, liveId, false, UserDialog.USERDIALOG_OPENTYPE_AUDIENCE);
					// ud.setChatable(false);
					ud.setReplayInfo(mReplayInfo);
					ud.show();
				}
			}

			break;
		case R.id.btn_follow:
			followUser();
			break;
		case R.id.tv_topic:
		case R.id.btn_edit_topic: {
			if (type == TYPE_ANCHOR) {
				LiveInfo live = new LiveInfo();
				live.liveId = this.liveId;
				live.title = this.topic;
				NavigationController.gotoUpdateTopic(getContext(), live);
			}
		}
			break;
		}

	}

	private long lastClickTime = 0;

	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}
}
