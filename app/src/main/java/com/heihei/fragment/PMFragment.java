package com.heihei.fragment;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.animator.AnimationDrawableUtil;
import com.base.host.AppLogic;
import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.base.utils.ThreadManager;
import com.base.utils.UIUtils;
import com.base.widget.CircleRotateProgressBar;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.GiftDialog;
import com.heihei.dialog.GiftDialog.OnGiftSendListener;
import com.heihei.dialog.MessageDialog;
import com.heihei.dialog.TipDialog;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.logic.GiftAnimationController;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.widget.GiftThreeView;
import com.heihei.fragment.live.widget.LiveUserView;
import com.heihei.fragment.live.widget.PMLoadingTextView;
import com.heihei.logic.StatusController;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.PmPresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.AudienceGift;
import com.heihei.model.Gift;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.heihei.model.msg.ActionMessageDispatcher;
import com.heihei.model.msg.MessageDispatcher;
import com.heihei.model.msg.api.ActionMessageCallback;
import com.heihei.model.msg.api.LiveMessageCallback;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.qiniu.pili.droid.rtcstreaming.RTCConferenceOptions;
import com.qiniu.pili.droid.rtcstreaming.RTCConferenceState;
import com.qiniu.pili.droid.rtcstreaming.RTCConferenceStateChangedListener;
import com.qiniu.pili.droid.rtcstreaming.RTCMediaStreamingManager;
import com.qiniu.pili.droid.rtcstreaming.RTCStartConferenceCallback;
import com.qiniu.pili.droid.rtcstreaming.RTCStreamingManager;
import com.qiniu.pili.droid.rtcstreaming.RTCSurfaceView;
import com.qiniu.pili.droid.rtcstreaming.RTCUserEventListener;
import com.qiniu.pili.droid.rtcstreaming.RTCVideoWindow;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.wmlives.heihei.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * 私聊界面
 * 
 * @author chengbo
 */
public class PMFragment extends BaseFragment implements OnClickListener, Observer, OnGiftSendListener, LiveMessageCallback, ActionMessageCallback {

	public static final int STATUS_IDLE = 0;// 正常初始状态
	public static final int STATUS_LOADING = 1;// 正在匹配状态
	public static final int STATUS_LOAD_SUCCESS = 2;// 匹配成功
	public static final int STATUS_LOAD_FAIL = 3;// 匹配失败
	private boolean pmStatus = false;
	public static int status = STATUS_IDLE;

	// ----------------R.layout.fragment_pm-------------Start
	private RelativeLayout btn_message;
	private LinearLayout other_user_info_top;
	private TextView btn_follow;
	private TextView btn_unfollow;
	private LiveUserView other_user_info;// include R.layout.cell_pm_userinfo
	private ImageView iv_other_sound;
	private com.heihei.fragment.live.widget.GiftThreeView other_gift_layout;// include
																			// R.layout.cell_audience_gift_three
	private RelativeLayout mid_content;
	private TextView tv_title_topic;
	private PMLoadingTextView tv_loading_tip;
	private TextView tv_load_fail_tip;
	private com.heihei.fragment.live.widget.GiftThreeView my_gift_layout;
	private ImageView btn_gift;
	private ImageView btn_close;
	private ImageView iv_bell;
	private ImageView tv_home_bell_sum;
	private LiveUserView my_user_info;
	private RelativeLayout rl_loading_top;
	private ImageView img_title_change;
	private CircleRotateProgressBar progress_loading;

	public void autoLoad_fragment_pm() {
		tv_home_bell_sum = (ImageView) findViewById(R.id.tv_home_bell_sum);
		iv_bell = (ImageView) findViewById(R.id.iv_bell);
		btn_message = (RelativeLayout) findViewById(R.id.btn_message);
		other_user_info_top = (LinearLayout) findViewById(R.id.other_user_info_top);
		btn_follow = (TextView) findViewById(R.id.btn_follow);
		btn_unfollow = (TextView) findViewById(R.id.btn_unfollow);
		img_title_change = (ImageView) findViewById(R.id.img_title_change);
		other_user_info = (LiveUserView) findViewById(R.id.other_user_info);// cell_pm_userinfo
		iv_other_sound = (ImageView) findViewById(R.id.iv_other_sound);
		other_gift_layout = (com.heihei.fragment.live.widget.GiftThreeView) findViewById(R.id.other_gift_layout);// cell_audience_gift_three
		mid_content = (RelativeLayout) findViewById(R.id.mid_content);
		tv_title_topic = (TextView) findViewById(R.id.tv_title_topic);
		tv_loading_tip = (PMLoadingTextView) findViewById(R.id.tv_loading_tip);
		tv_load_fail_tip = (TextView) findViewById(R.id.tv_load_fail_tip);
		my_gift_layout = (com.heihei.fragment.live.widget.GiftThreeView) findViewById(R.id.my_gift_layout);// cell_audience_gift_three
		btn_gift = (ImageView) findViewById(R.id.btn_gift);
		btn_close = (ImageView) findViewById(R.id.btn_close);
		my_user_info = (LiveUserView) findViewById(R.id.my_user_info);// cell_pm_userinfo
		rl_loading_top = (RelativeLayout) findViewById(R.id.rl_loading_top);
		progress_loading = (CircleRotateProgressBar) findViewById(R.id.progress_loading);
		initView();
	}

	private void initView() {
		RTCMediaStreamingManager.init(getContext(),0);
		mRTCStreamingManager = new RTCMediaStreamingManager(getContext(), AVCodecType.SW_AUDIO_CODEC);
		mRTCStreamingManager.setConferenceStateListener(mRTCStreamingStateChangedListener);
		mRTCStreamingManager.setDebugLoggingEnabled(false);
		mRTCMediaStreamingManagerWeakReference = new WeakReference<RTCMediaStreamingManager>(mRTCStreamingManager);
		RTCConferenceOptions options = new RTCConferenceOptions();
		options.setHWCodecEnabled(false);
		mRTCStreamingManager.setConferenceOptions(options);
		mRTCStreamingManager.setUserEventListener(mRTCUserEventListener);
		mRTCStreamingManager.setDebugLoggingEnabled(false);

		MicrophoneStreamingSetting setting = new MicrophoneStreamingSetting();
		setting.setBluetoothSCOEnabled(false);

		RTCVideoWindow remoteAnchorView = new RTCVideoWindow(new RTCSurfaceView(getContext()));
		mRTCStreamingManager.addRemoteWindow(remoteAnchorView);
		mRTCStreamingManager.prepare(setting);
	}

	public boolean isCaptureIng = false;

	public void setCaptureStatus(boolean capture) {
		if (mRTCStreamingManager == null) {
			return;
		}

		if (capture) {
			if (status != STATUS_LOAD_SUCCESS) {
				Log.i("mRTCStreamingManager", "startCapture");
				mRTCStreamingManager.stopCapture();
				isCaptureIng = mRTCStreamingManager.startCapture();
			}
		}
	}

	public static WeakReference<RTCMediaStreamingManager> mRTCMediaStreamingManagerWeakReference;

	private RTCUserEventListener mRTCUserEventListener = new RTCUserEventListener() {
		@Override
		public void onUserJoinConference(String remoteUserId) {
			LogWriter.i("PMFragment", "onUserJoinConference: " + remoteUserId);
		}

		@Override
		public void onUserLeaveConference(String remoteUserId) {
			LogWriter.i("PMFragment", "onUserLeaveConference: " + remoteUserId);
		}
	};

	public int status() {
		return status;
	}

	public void close() {
		stop(false);
	}

	public void setPmChatInfo(ObServerMessage message) {
		if (message != null)
			initSuccessStatus(message);
	}

	// ----------------R.layout.fragment_pm-------------End

	@Override
	protected String initTitle() {
		return getString(R.string.title_pm);
	}

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_pm);
	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_pm();

		btn_message.setOnClickListener(this);
		btn_follow.setOnClickListener(this);
		btn_unfollow.setOnClickListener(this);
		other_user_info.setOnClickListener(this);
		btn_gift.setOnClickListener(this);
		btn_gift.setClickable(true);
		btn_close.setOnClickListener(this);
		mid_content.setOnClickListener(this);
		tv_title_topic.setOnClickListener(this);
		img_title_change.setOnClickListener(this);
		other_gift_layout.setType(GiftThreeView.TYPE_CHAT);
		my_gift_layout.setType(GiftThreeView.TYPE_CHAT);

		EventManager.ins().registListener(EventTag.START_CHAT, mInfoListener);
		EventManager.ins().registListener(EventTag.CHAT_WITH_SHOW_MESSAGE, mInfoListener);
		EventManager.ins().registListener(EventTag.ACCOUNT_UPDATE_INFO, mInfoListener);
		EventManager.ins().registListener(EventTag.STOP_CHAT, mInfoListener);
		EventManager.ins().registListener(EventTag.FOLLOW_CHANGED, mInfoListener);
		DueMessageUtils.getInstance().addObserver(PMFragment.this);
	}

	EventListener mInfoListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {

			switch (what) {
			case EventTag.START_CHAT:
				try {
					if (dataobj == null)
						return;
					ObServerMessage message = (ObServerMessage) dataobj;
					if (message.type.equals(ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM)) {
						LogWriter.i("PMFragment", " ObServerMessage OB_SERVER_MESSAGE_TYPE_JOIN_ROOM ");
						initSuccessStatus(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case EventTag.CHAT_WITH_SHOW_MESSAGE:
				btn_message.performClick();
				break;
			case EventTag.ACCOUNT_UPDATE_INFO:
				my_user_info.setUser(UserMgr.getInstance().getLoginUser(), false);
				break;
			case EventTag.STOP_CHAT:
				stop(false);
				break;
			case EventTag.FOLLOW_CHANGED:
				if (arg1 == 1) {
					btn_follow.setVisibility(View.GONE);
					btn_unfollow.setVisibility(View.VISIBLE);
				} else if (arg1 == 0) {
					btn_follow.setVisibility(View.VISIBLE);
					btn_unfollow.setVisibility(View.GONE);
				}
				break;
			}
		}
	};

	@Override
	protected void refresh() {
		setStatus(STATUS_IDLE);
		my_user_info.setUser(UserMgr.getInstance().getLoginUser(), false);

		PmPresent.getInstance().getStopMatchChat(new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
				}
			}
		});
	}

	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden) {

			if (HostApplication.getInstance().getMainActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						MainFragment fragment = (MainFragment) HostApplication.getInstance().getMainActivity().getMainFragment();
						FrameLayout giftContentView = fragment.getGiftContentView();
						giftContentView.setVisibility(View.GONE);
						if (getView() != null) {
							getView().setKeepScreenOn(false);
						}
					}
				});
			}
		} else {
			if (HostApplication.getInstance().getMainActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						MainFragment fragment = (MainFragment) HostApplication.getInstance().getMainActivity().getMainFragment();
						FrameLayout giftContentView = fragment.getGiftContentView();
						giftContentView.setVisibility(View.VISIBLE);
						if (getView() != null) {
							getView().setKeepScreenOn(true);
						}
					}
				});
			}
		}
	};

	AnimationDrawable mBellAnim;

	private void startBellAnim() {
		if (mBellAnim == null) {
			mBellAnim = AnimationDrawableUtil.createBellAnim(getContext());
		}
		iv_bell.setImageDrawable(mBellAnim);
		mBellAnim.start();
	}

	private void stopBellAnim() {
		if (mBellAnim == null) {
			mBellAnim = AnimationDrawableUtil.createBellAnim(getContext());
		}
		if (mBellAnim.isRunning()) {
			mBellAnim.stop();
		}
		iv_bell.setImageDrawable(mBellAnim.getFrame(0));
	}

	public void setStatus(int status) {
		this.status = status;
		if (status == STATUS_IDLE) {
			StatusController.getInstance().setCurrentStatus(StatusController.STATUS_IDLE);
			other_user_info_top.setVisibility(View.GONE);
			tv_title_topic.setVisibility(View.GONE);
			tv_loading_tip.setVisibility(View.GONE);
			tv_load_fail_tip.setVisibility(View.VISIBLE);
			tv_load_fail_tip.setText(R.string.pm_load_start);
			img_title_change.setVisibility(View.GONE);
			tv_title_topic.setClickable(false);
			btn_gift.setEnabled(false);
			btn_close.setEnabled(false);
			my_gift_layout.setVisibility(View.GONE);
			rl_loading_top.setVisibility(View.GONE);
			progress_loading.setVisibility(View.GONE);
			my_user_info.stopAnimation();
		} else if (status == STATUS_LOADING) {
			other_user_info_top.setVisibility(View.GONE);
			tv_title_topic.setVisibility(View.GONE);
			img_title_change.setVisibility(View.GONE);
			tv_loading_tip.setVisibility(View.VISIBLE);
			tv_load_fail_tip.setVisibility(View.GONE);
			// btn_gift.setEnabled(false);
			tv_title_topic.setVisibility(View.GONE);
			btn_close.setEnabled(true);
			my_user_info.stopAnimation();
			my_gift_layout.setVisibility(View.GONE);
			rl_loading_top.setVisibility(View.VISIBLE);
			progress_loading.setVisibility(View.VISIBLE);
		} else if (status == STATUS_LOAD_FAIL) {
			StatusController.getInstance().setCurrentStatus(StatusController.STATUS_IDLE);
			other_user_info_top.setVisibility(View.GONE);
			tv_title_topic.setVisibility(View.GONE);
			tv_loading_tip.setVisibility(View.GONE);
			img_title_change.setVisibility(View.GONE);
			tv_load_fail_tip.setVisibility(View.VISIBLE);
			tv_load_fail_tip.setText(getString(R.string.pm_load_fail));
			btn_gift.setEnabled(false);
			my_user_info.stopAnimation();
			tv_title_topic.setVisibility(View.GONE);
			btn_close.setEnabled(false);
			my_gift_layout.setVisibility(View.GONE);
			rl_loading_top.setVisibility(View.GONE);
			progress_loading.setVisibility(View.GONE);
		} else if (status == STATUS_LOAD_SUCCESS) {
			StatusController.getInstance().setCurrentStatus(StatusController.STATUS_CHAT);
			other_user_info_top.setVisibility(View.VISIBLE);
			tv_title_topic.setVisibility(View.VISIBLE);
			tv_loading_tip.setVisibility(View.GONE);
			tv_load_fail_tip.setVisibility(View.GONE);
			img_title_change.setVisibility(View.GONE);
			btn_gift.setEnabled(true);
			tv_title_topic.setVisibility(View.GONE);
			btn_close.setEnabled(true);
			// my_user_info.startAnimation();
			my_gift_layout.setVisibility(View.VISIBLE);
			rl_loading_top.setVisibility(View.GONE);
			progress_loading.setVisibility(View.GONE);
		}
	}

	@Override
	public void onStart() {
		mRTCStreamingManager.startCapture();
		super.onStart();
	}

	private ActionMessage otherUserActionMessage;
	private ObServerMessage mObServerMessage;

	private void initSuccessStatus(final ObServerMessage msg) {
		setStatus(STATUS_LOAD_SUCCESS);
		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(6);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					DueMessageUtils.getInstance().insertMessageList(false);
				}

			}
		});
		if (mTimerTask != null)
			mTimerTask.cancel();
		if (editTask != null)
			editTask.cancel();

		mObServerMessage = null;
		mObServerMessage = msg;
		stopConference();
		img_title_change.setVisibility(View.GONE);
		pmStatus = true;
		LogWriter.i("PMFragment", "STATUS_LOAD_SUCCESS comment" + msg.chatInfo.initTopic);
		if (msg.chatInfo.user.isFollowed) {
			btn_unfollow.setVisibility(View.VISIBLE);
			btn_follow.setVisibility(View.GONE);
		}

		tv_title_topic.setVisibility(View.VISIBLE);
		tv_title_topic.setText(msg.chatInfo.initTopic);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (status == STATUS_LOAD_SUCCESS) {
					img_title_change.setVisibility(View.VISIBLE);
					tv_title_topic.setVisibility(View.GONE);
					tv_title_topic.setClickable(true);
				}
			}
		}, 3000);

		other_user_info.setUser(msg.chatInfo.user, true);
		if (msg.chatInfo.user.gender == User.FEMALE) {
			iv_other_sound.setImageResource(R.drawable.hh_live_speak_female);
		} else {
			iv_other_sound.setImageResource(R.drawable.hh_live_speak_male);
		}

		chatId = msg.chatId;
		otherUser = msg.chatInfo.user;

		MessageDispatcher.getInstance().onDestroy();// 匹配成功后先清除之前的监听
		MessageDispatcher.getInstance().joinRoom(String.valueOf(chatId));
		MessageDispatcher.getInstance().SubscribeCallback(this);
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC, PMFragment.this);

		if (chatHeartBeatTask != null)
			chatHeartBeatTask.cancel();

		chatHeartBeatTask = new heartBeatTask();
		chatHeartBeatTimer.schedule(chatHeartBeatTask, 3, nextTimeout);
		tryNumber = 0;
		AsyncTask.execute(new Runnable() {

			@Override
			public void run() {
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						initChat(UserMgr.getInstance().getUid(), msg.qiniu.roomName, msg.qiniu.roomToken);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});

	}

	private RTCMediaStreamingManager mRTCStreamingManager;

	private void initChat(final String id, final String name, final String token) {
		LogWriter.i("PMFragment", "id: " + id + " name: " + name + " token: " + token);

		mRTCStreamingManager.startConference(id, name, token, new RTCStartConferenceCallback() {
			@Override
			public void onStartConferenceSuccess() {
				if (status != STATUS_LOAD_SUCCESS) {
					LogWriter.i("PMFragment", "status " + status + " onStartConferenceSuccess: stopConference");
					stopConference();
				}
				LogWriter.i("PMFragment", "onStartConferenceSuccess: ");
			}

			@Override
			public void onStartConferenceFailed(int errorCode) {
				LogWriter.i("PMFragment", "onStartConferenceFailed: " + errorCode);
				if (errorCode == 1016 && tryNumber < 3) {
					tryNumber++;
					try {
						TimeUnit.SECONDS.sleep(3);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						initChat(id, name, token);
					}
				}
			}
		});
	}

	private int tryNumber = 0;

	private RTCConferenceStateChangedListener mRTCStreamingStateChangedListener = new RTCConferenceStateChangedListener() {
		@Override
		public void onConferenceStateChanged(RTCConferenceState state, int extra) {
			switch (state) {
			case READY:
				break;
			case VIDEO_PUBLISH_FAILED:
			case AUDIO_PUBLISH_FAILED:
				break;
			case VIDEO_PUBLISH_SUCCESS:
				break;
			case AUDIO_PUBLISH_SUCCESS:
				break;
			case USER_JOINED_AGAIN:
				break;
			case USER_KICKOUT_BY_HOST:
				break;
			case OPEN_CAMERA_FAIL:
				break;
			case AUDIO_RECORDING_FAIL:
				break;
			default:
				return;
			}
		}
	};

	private User otherUser = null;
	private long chatId = 0;

	/**
	 * 开始匹配
	 */
	private void startLoading() {

		getView().setKeepScreenOn(true);
		PmPresent.getInstance().getMatchChat(new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					int expireTime = json.optInt("expireTime");
					JSONArray tips = json.optJSONArray("tips");
					Message message = Message.obtain();
					message.obj = tips;
					message.what = MESSAGE_WHAT_MATCH_CHAT;
					message.arg2 = expireTime;
					mHandler.sendMessage(message);
				} else {
					setStatus(STATUS_LOAD_FAIL);
				}
			}
		});

	}

	private UserPresent userPresent = new UserPresent();

	public void stop(boolean isObserver) {
		LogWriter.i("PMFragment", "stop");
		img_title_change.setVisibility(View.GONE);
		if (editTask != null)
			editTask.cancel();

		if (mTimerTask != null)
			mTimerTask.cancel();

		if (tv_title_topic != null)
			tv_title_topic.setClickable(false);

		if (chatHeartBeatTask != null)
			chatHeartBeatTask.cancel();
		stopConference();
		LogWriter.i("PMFragment", "stopConference");
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC, PMFragment.this);

		if (gd != null && gd.isShowing()) {
			gd.dismiss();
		}

		if (mGiftAnimationController != null) {
			mGiftAnimationController.release();
		}

		if (status == STATUS_LOADING) {
			LogWriter.i("PMFragment", "STATUS_LOADING");
			PmPresent.getInstance().getStopMatchChat(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						LogWriter.i("PMFragment", "stop onJsonResponse");
						pmStatus = false;
						chatId = 0;
						setStatus(STATUS_IDLE);
					}
				}
			});
		} else if (status == STATUS_LOAD_SUCCESS && !isObserver) {
			LogWriter.i("PMFragment", "STATUS_LOAD_SUCCESS");
			PmPresent.getInstance().getStopChat(new JSONResponse() {
				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						setStatus(STATUS_IDLE);
						chatId = 0;
						LogWriter.i("PMFragment", errCode);
					}
				}

			}, chatId);
		}

		chatId = 0;
		// if (StatusController.getInstance().getCurrentStatus() ==
		// StatusController.STATUS_CHAT) {
		// StatusController.getInstance().resetStatus();
		// LogWriter.i("PMFragment", "resetStatus");
		// }
	}

	GiftDialog gd = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_title_change:
			PmPresent.getInstance().getChangeTopic(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						img_title_change.setVisibility(View.GONE);
					}
				}
			}, chatId);
			break;
		case R.id.tv_title_topic:
			PmPresent.getInstance().getChangeTopic(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {

					}
				}
			}, chatId);
			break;
		case R.id.btn_unfollow:
			if (otherUserActionMessage != null && otherUserActionMessage.user != null) {
				userPresent.unfollowUser(otherUserActionMessage.user.uid, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							otherUserActionMessage.user.isFollowed = false;
							btn_unfollow.setVisibility(View.GONE);
							btn_follow.setVisibility(View.VISIBLE);
						}
					}
				});
			}
			break;
		case R.id.btn_message:

			if (status == STATUS_LOAD_SUCCESS || PMFragment.status == STATUS_LOADING) {
				MessageDialog md = new MessageDialog(getActivity(), tv_home_bell_sum);
				md.show();
			} else {
				MessageDialog md = new MessageDialog(getActivity(), tv_home_bell_sum);
				md.show();
			}

			DueMessageUtils.getInstance().hideMessage();
			break;
		case R.id.btn_follow:

			if (otherUserActionMessage != null && otherUserActionMessage.user != null) {
				userPresent.followUser(otherUserActionMessage.user.uid, null, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							otherUserActionMessage.user.isFollowed = true;

							btn_unfollow.setVisibility(View.VISIBLE);
							btn_follow.setVisibility(View.GONE);
						} else {
							UIUtils.showToast(msg);
						}
					}
				});
			} else if (mObServerMessage != null && mObServerMessage.chatInfo != null && mObServerMessage.chatInfo.user != null) {
				userPresent.followUser(mObServerMessage.chatInfo.user.uid, null, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							mObServerMessage.chatInfo.user.isFollowed = true;
							btn_follow.setVisibility(View.INVISIBLE);
						} else {
							UIUtils.showToast(msg);
						}

					}
				});
			}

			break;
		case R.id.other_user_info:
			if (status == STATUS_LOAD_SUCCESS) {
				if (!clickOlder()) {
					return;
				}
				if (otherUserActionMessage != null && otherUserActionMessage.user != null) {
					UserDialog ud = new UserDialog(getActivity(), otherUserActionMessage.user, null, false, UserDialog.USERDIALOG_OPENTYPE_PMFRAGMENT);
					PlayActivityInfo info = new PlayActivityInfo();
					info.setPmFragment(true, PMFragment.this);
					info.openType = UserDialog.USERDIALOG_OPENTYPE_PMFRAGMENT;
					ud.setReplayInfo(info);
					ud.show();
				} else if (mObServerMessage != null && mObServerMessage.chatInfo != null && mObServerMessage.chatInfo.user != null) {
					UserDialog ud = new UserDialog(getActivity(), mObServerMessage.chatInfo.user, null, false, UserDialog.USERDIALOG_OPENTYPE_PMFRAGMENT);
					PlayActivityInfo info = new PlayActivityInfo();
					info.setPmFragment(true, PMFragment.this);
					info.openType = UserDialog.USERDIALOG_OPENTYPE_PMFRAGMENT;
					ud.setReplayInfo(info);
					ud.show();
				}

			}
			break;
		case R.id.btn_gift:
			if (this.chatId != 0 && otherUser != null) {
				gd = new GiftDialog(getContext(), String.valueOf(this.chatId), otherUser.uid, GiftDialog.TYPE_CHAT);
				gd.setOnGiftSendListener(this);
				gd.show();
			}

			break;
		case R.id.btn_close:
			if (chatHeartBeatTask != null)
				chatHeartBeatTask.cancel();

			if (status == STATUS_LOADING) {
				stop(false);
			} else {
				final TipDialog td = new TipDialog(getActivity());
				td.setContent(HostApplication.getInstance().getResources().getString(R.string.user_close_pmfragment));
				td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

					@Override
					public void onOkClick(Dialog dialog) {
						stop(false);
						td.dismiss();
					}

					@Override
					public void onCancleClick(Dialog dialog) {
						td.dismiss();
					}
				});
				td.show();
			}
			break;
		case R.id.mid_content:
			if (status == STATUS_IDLE || status == STATUS_LOAD_FAIL) {
				startLoading();
			}
			break;
		}

	}

	private void stopConference() {
		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				mRTCStreamingManager.stopConference();
			}
		});
	}

	private long lastClickTime = 0;

	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}

	private final int MESSAGE_WHAT_MATCH_CHAT = 0;
	private final int MESSAGE_WHAT_MATCH_CHAT_TIMEOUT = 1;
	private final int MESSAGE_WHAT_EDIT_TOPIC = 2;
	private final int MESSAGE_WHAT__RENAME_EDIT_TOPIC = 3;
	private final int MESSAGE_WHAT_OTHER_QUIT_ROOM = 4;// 对方退出房间

	private WeakReference<PMFragment> frag = new WeakReference<PMFragment>(this);

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			try {
				switch (msg.what) {
				case MESSAGE_WHAT_MATCH_CHAT:
					try {
						setStatus(STATUS_LOADING);
						JSONArray array = (JSONArray) msg.obj;
						topics.clear();
						for (int i = 0; i < array.length(); i++) {
							topics.add(array.getString(i));
						}
//						修改 中央显示的词
						if (topics != null && topics.size() > 0) {
							topicIndex = topics.size() - 1;
							if (editTask != null)
								editTask.cancel();

							editTask = new editTopicTask();
							editTopicTimer.schedule(editTask, 1000, 8000);
						}

						if (msg.arg2 > 0) {
							waitTime = msg.arg2;
							if (mTimerTask != null)
								mTimerTask.cancel();
							mTimerTask = new MyTask();
							timer.schedule(mTimerTask, 1, 1000);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case MESSAGE_WHAT_MATCH_CHAT_TIMEOUT:
					if (editTask != null)
						editTask.cancel();
					if (!pmStatus) {
						setStatus(STATUS_LOAD_FAIL);
					}
					break;

				case MESSAGE_WHAT_EDIT_TOPIC:
					tv_loading_tip.setText("");
					editTopic();
					break;

				case MESSAGE_WHAT__RENAME_EDIT_TOPIC:
					tv_loading_tip.setText("");
					break;
				case MESSAGE_WHAT_OTHER_QUIT_ROOM:
					tv_title_topic.setClickable(true);
					stop(false);
					setStatus(STATUS_IDLE);
					break;
				default:
					break;
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}

		};
	};

	@Override
	public void onDestroy() {

		EventManager.ins().removeListener(EventTag.START_CHAT, mInfoListener);
		EventManager.ins().removeListener(EventTag.CHAT_WITH_SHOW_MESSAGE, mInfoListener);
		EventManager.ins().removeListener(EventTag.ACCOUNT_UPDATE_INFO, mInfoListener);
		EventManager.ins().removeListener(EventTag.STOP_CHAT, mInfoListener);
		MessageDispatcher.getInstance().onDestroy();
		stop(false);
		mRTCStreamingManager.destroy();
		RTCStreamingManager.deinit();
		DueMessageUtils.getInstance().deleteObserver(this);
		if (mGiftAnimationController != null) {
			mGiftAnimationController.release();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		// mRTCStreamingManager.stopCapture();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	private ArrayList<String> topics = new ArrayList<String>();
	private int topicIndex = -1;

	private void editTopic() {
		try {
			if (topicIndex >= 0) {
				tv_loading_tip.setText(topics.get(topicIndex));
				topicIndex--;
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mHandler.sendEmptyMessage(MESSAGE_WHAT__RENAME_EDIT_TOPIC);
					}
				}, 5000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private int waitTime = 0;
	private Timer timer = new Timer();
	private Timer editTopicTimer = new Timer();
	private MyTask mTimerTask;
	private editTopicTask editTask;

	class editTopicTask extends TimerTask {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(MESSAGE_WHAT_EDIT_TOPIC);
		}
	};

	class MyTask extends TimerTask {

		@Override
		public void run() {
			waitTime--;
			if (waitTime <= 0) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						PmPresent.getInstance().getStopMatchChat(new JSONResponse() {

							@Override
							public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
								if (errCode == ErrorCode.ERROR_OK) {
									pmStatus = false;
									chatId = 0;
								}
							}
						});
					}
				});
				mHandler.sendEmptyMessage(MESSAGE_WHAT_MATCH_CHAT_TIMEOUT);
				if (mTimerTask != null)
					mTimerTask.cancel();
			}
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		LogWriter.i("PMFragment", " HomeFragment update");
		try {
			final ObServerMessage ob = (ObServerMessage) data;
			switch (ob.type) {
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_PM_STOP_DUE:
				if (status == STATUS_LOADING) {
					getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							stop(true);
						}
					});
				}
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_MESSAGE_COUNT:
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_home_bell_sum.setVisibility(View.VISIBLE);
					}
				});

				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_HIDE_MESSAGE_COUNT:
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_home_bell_sum.setVisibility(View.GONE);
					}
				});
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM:
				LogWriter.i("PMFragment", "OB_SERVER_MESSAGE_TYPE_JOIN_ROOM");
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initSuccessStatus(ob);
					}
				});
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_CHAT_USER_STATUS:
				if (ob != null && ob.chatUserStatusMessage != null) {
					if (ob.chatId != this.chatId)
						return;
					LogWriter.i("PMFragment", "ObServerMessage.OB_SERVER_MESSAGE_TYPE_CHAT_USER_STATUS:");

					otherUserActionMessage = null;
					otherUserActionMessage = ob.chatUserStatusMessage;
					switch (otherUserActionMessage.status) {
					case 0:
					case 1:
						other_user_info.setUser(ob.chatUserStatusMessage.user, true);
						btn_gift.setClickable(true);
						break;
					case 2:
					case 3:
						stopConference();
						getActivity().runOnUiThread(new Runnable() {
							@Override
							public void run() {
								tv_title_topic.setClickable(false);
								img_title_change.setVisibility(View.GONE);
								tv_load_fail_tip.setVisibility(View.GONE);
								tv_loading_tip.setVisibility(View.GONE);
								tv_title_topic.setVisibility(View.VISIBLE);
								tv_title_topic.setText("对方被黑社会带走了");
								if (chatHeartBeatTask != null)
									chatHeartBeatTask.cancel();
								other_user_info_top.setVisibility(View.GONE);
								mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_OTHER_QUIT_ROOM, 3000);
							}
						});
						break;
					default:
						break;
					}

				}
				break;

			default:
				break;
			}

		} catch (Exception e) {
			LogWriter.i("PMFragment", "e : " + e.getMessage());
			e.printStackTrace();
		}
	}

	private GiftAnimationController mGiftAnimationController;

	@Override
	public void onGiftSend(Gift gift, int amount) {
		if (gift != null) {
			AudienceGift mAudienceGift = new AudienceGift();
			mAudienceGift.fromUser = UserMgr.getInstance().getLoginUser();
			mAudienceGift.gift = gift;
			mAudienceGift.amount = amount;
			mAudienceGift.time = System.currentTimeMillis();
			mAudienceGift.gift_uuid = gift.gift_uuid;
			if (gift.type == Gift.TYPE_NORMAL) {
				other_gift_layout.addAudienceGift(mAudienceGift);
			} else {
				if (gd != null && gd.isShowing()) {
					gd.dismiss();
				}

				if (HostApplication.getInstance().getMainActivity() != null) {
					MainFragment fragment = (MainFragment) HostApplication.getInstance().getMainActivity().getMainFragment();
					FrameLayout giftContentView = fragment.getGiftContentView();
					if (mGiftAnimationController == null) {
						mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);
					}
					mGiftAnimationController.addAnimation(mAudienceGift);
				}
			}
		}
	}

	@Override
	public void addTextCallbackView(AbstractMessage message) {

	}

	@Override
	public void addGiftCallbackView(GiftMessage message) {
		LogWriter.i("MessageDistribute", "PmFragment addGiftCallbackView");
		if (!message.roomId.equals(String.valueOf(this.chatId)))// 不是本房间的消息不处理
		{
			LogWriter.i("MessageDistribute", "PmFragment message not of this room roomId : " + message.roomId + " chatId : " + chatId);
			return;
		}

		if (message.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的刷新别人的黑票
		{
			if (otherUser != null) {
				otherUser.allEarnPoint = message.totalTicket;
				other_user_info.setUser(otherUser, true);
			}
			return;
		}

		Gift gift = GiftController.getInstance().getGiftById(message.giftId);
		if (gift == null) {
			return;
		}

		AudienceGift aGift = new AudienceGift();
		User user = new User();
		user.uid = message.fromUserId;
		user.nickname = message.fromUserName;
		user.gender = message.gender;
		aGift.fromUser = user;
		aGift.gift = gift;
		aGift.amount = message.amount;
		aGift.time = System.currentTimeMillis();
		aGift.gift_uuid = message.gift_uuid;

		UserMgr.getInstance().getLoginUser().allEarnPoint = message.totalTicket;// 如果是别人发的礼物刷新自己的黑票
		my_user_info.setUser(UserMgr.getInstance().getLoginUser(), false);

		if (gift.type == Gift.TYPE_NORMAL) {
			my_gift_layout.addAudienceGift(aGift);
		} else {
			if (HostApplication.getInstance().getMainActivity() != null) {
				MainFragment fragment = (MainFragment) HostApplication.getInstance().getMainActivity().getMainFragment();
				FrameLayout giftContentView = fragment.getGiftContentView();
				if (mGiftAnimationController == null) {
					mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);
				}
				mGiftAnimationController.addAnimation(aGift);
			}
		}

	}

	@Override
	public void addLiveLikeCallbackView(LiveMessage message) {

	}

	private class heartBeatTask extends TimerTask {

		@Override
		public void run() {
			PmPresent.getInstance().getChatHeartBeat(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						int time = json.optInt("nextTimeout");
						LogWriter.i("heartBeatTask", "time");
						if (time > 0)
							nextTimeout = time * 1000;
					}
				}
			}, chatId);
		}
	}

	private heartBeatTask chatHeartBeatTask;
	private int nextTimeout = 5000;
	private Timer chatHeartBeatTimer = new Timer();

	@Override
	public void callback(ActionMessage message) {
		if (status != STATUS_LOAD_SUCCESS)
			return;

		if (img_title_change != null && img_title_change.getVisibility() == View.VISIBLE)
			img_title_change.setVisibility(View.GONE);
		if (message.actionType == ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC) {
			if (tv_title_topic != null && status == STATUS_LOAD_SUCCESS) {
				if (tv_title_topic.getVisibility() == View.GONE)
					tv_title_topic.setVisibility(View.VISIBLE);
				tv_title_topic.setText(message.text);
			}
		}
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "PMFragment";
	}

}
