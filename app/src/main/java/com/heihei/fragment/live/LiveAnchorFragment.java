package com.heihei.fragment.live;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import com.base.danmaku.DanmakuItem;
import com.base.danmaku.DanmakuView.OnItemClickListener;
import com.base.host.AppLogic;
import com.base.host.BaseFragment;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.UIUtils;
import com.base.widget.message.DanmakuViewFragment;
import com.base.widget.message.InformationPagerAdapter;
import com.base.widget.message.ScrollViewFragment;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.TipDialog;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.logic.GiftAnimationController;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.logic.HeartBeatController;
import com.heihei.fragment.live.logic.HeartBeatController.OnHeartbeatErrorListener;
import com.heihei.fragment.live.widget.LiveBottom;
import com.heihei.fragment.live.widget.LiveBottom.OnCloseClickListener;
import com.heihei.fragment.live.widget.LiveBottom.OnTextSendListener;
import com.heihei.fragment.live.widget.LiveHeader;
import com.heihei.logic.StatusController;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.LivePresent;
import com.heihei.media.StreamingManagerFactory;
import com.heihei.model.AudienceGift;
import com.heihei.model.Gift;
import com.heihei.model.LiveInfo;
import com.heihei.model.User;
import com.heihei.model.control.LimitQueue;
import com.heihei.model.msg.MessageDispatcher;
import com.heihei.model.msg.api.LiveMessageCallback;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.bean.SystemMessage;
import com.heihei.model.msg.bean.TextMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.qiniu.pili.droid.rtcstreaming.RTCMediaStreamingManager;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingProfile.StreamStatus;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

/**
 * 直播zhubodu
 * 
 * @author admin
 *
 */
public class LiveAnchorFragment extends BaseFragment
		implements OnTextSendListener, LiveMessageCallback, Observer, OnCloseClickListener, OnHeartbeatErrorListener, StreamStatusCallback {
	private static final String TAG_LOG = "LiveAnchorFragment";

	private LiveHeader mHeader;
	private ViewPager mViewPager;
	private LiveBottom mBottom;
	private FrameLayout giftContentView;
	private CloseLiveAnchorCallback mCloseLiveAnchorCallback = new CloseLiveAnchorCallback() {

		@Override
		public void onClose() {
			getActivity().finish();
		}
	};
	private RTCMediaStreamingManager mRTCMediaStreamingManager;

	public static interface CloseLiveAnchorCallback {
		public void onClose();
	}

	private HeartBeatController mHeartBeatController;
	private GiftAnimationController mGiftAnimationController;

	private LiveInfo mLiveInfo;
	private LivePresent mLivePresent = new LivePresent();
	//private boolean isCurrentNetworkStatue = true;// false：断开 true：链接
	private LimitQueue<AbstractMessage> mLimitQueue = new LimitQueue<AbstractMessage>(3);// 礼物的缓存队列
	private boolean isPaused = false;// 是否页面被遮盖住

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_live_anchor);
		firstOpen = SharedPreferencesUtil.getInstance().get("firstOpen", true);
	}

	@Override
	protected void viewDidLoad() {
		mLiveInfo = (LiveInfo) mViewParam.data;
		if (mLiveInfo != null && mLiveInfo.creator != null && TextUtils.isEmpty(mLiveInfo.creator.uid)) {
			if (UserMgr.getInstance().getUid().equals(mLiveInfo.creator.uid)) {
				isFirstSTREAMING = false;
			}
		}

		mHeader = (LiveHeader) findViewById(R.id.header);
		mBottom = (LiveBottom) findViewById(R.id.live_bottom);
		giftContentView = (FrameLayout) findViewById(R.id.gift_contentView);
		mHeader.setType(LiveHeader.TYPE_ANCHOR);
		mBottom.setType(mLiveInfo.liveId, LiveHeader.TYPE_ANCHOR);
		mBottom.setisLiveCreateUser(true);
		mBottom.setActivity(getActivity());
		mBottom.setOnTextSendListener(this);
		mBottom.setOnCloseClickListener(this);
		mBottom.setCloseCallback(mCloseLiveAnchorCallback);
		getView().setKeepScreenOn(true);
		DueMessageUtils.getInstance().addObserver(this);
		EventManager.ins().registListener(EventTag.STOP_LIVE, mStopListener);

		mViewPager = (ViewPager) findViewById(R.id.messageviewpager);
		mScrollViewFragment = new ScrollViewFragment();
		mScrollViewFragment.setLiveBottom(mBottom, mLiveInfo);
		mDanmakuViewFragment = new DanmakuViewFragment();
		mDanmakuViewFragment.setLiveBottom(mBottom, mLiveInfo, mScrollViewFragment);
		checkFirstOpen();
		List<Fragment> list = new ArrayList<Fragment>();
		list.add(mScrollViewFragment);
		list.add(mDanmakuViewFragment);
		InformationPagerAdapter adapter = new InformationPagerAdapter(getChildFragmentManager(), list);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setOnPageChangeListener(mPageChangeListener);
	}

	private boolean firstOpen = false;

	private void checkFirstOpen() {
		mScrollViewFragment.showhttip(firstOpen);
		mDanmakuViewFragment.showhttip(firstOpen);
		SharedPreferencesUtil.getInstance().setValueBoolean("firstOpen", false);
	}

	/**
	 * ViewPager滑动监听
	 */
	private OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0 && mScrollViewFragment != null) {
				mScrollViewFragment.scrollToBottom();
			}

			if (firstOpen) {
				if (arg0 == 0) {
					mDanmakuViewFragment.hintTtipView();
				} else if (arg0 == 1) {
					mScrollViewFragment.hintTtipView();
				}
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private DanmakuViewFragment mDanmakuViewFragment;
	private ScrollViewFragment mScrollViewFragment;

	@Override
	protected void refresh() {

		try {
			User u = UserMgr.getInstance().getLoginUser();
			if (u != null) {
				mBottom.setUserInfo(u);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mHeader.refreshUserList(mLiveInfo.liveId);
		mHeader.setLiveTopic(mLiveInfo.title);

		if (mHeartBeatController == null) {
			mHeartBeatController = new HeartBeatController(mLiveInfo.liveId, true);
		}
		mHeartBeatController.setOnHeartbeatErrorListener(this);
		mHeartBeatController.startHeartBeat();

		MessageDispatcher.getInstance().joinRoom(mLiveInfo.liveId);
		MessageDispatcher.getInstance().SubscribeCallback(this);

		StatusController.setLiveIng(true);
	
		checkFirstOpen();
		if (mLiveInfo.qiniuJson == null) {
			// 回到自己创建的直播间
			mLivePresent.joinLive(mLiveInfo.liveId, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (getActivity() == null) {
						return;
					}
					if (errCode == ErrorCode.ERROR_OK) {
						JSONObject liveJ = json.optJSONObject("live");
						mLiveInfo = new LiveInfo(liveJ);
						if (mLiveInfo != null && null != mLiveInfo.qiniuJson) {
							initMediaStreamManager();
						} else {
							UIUtils.showToast(R.string.error_live_is_not_exit);
							getActivity().finish();
						}
					} else {
						UIUtils.showToast(R.string.error_live_is_not_exit);
						getActivity().finish();
					}
				}
			});

		} else {
			// 主播端，创建新的直播间
			startCustomerStreaming();
		}
	}

	/**
	 * 开始直播
	 */
	private void startCustomerStreaming() {
		initMediaStreamManager();
		StatusController.setLiveIng(true);
		mLivePresent.joinLive(mLiveInfo.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (getActivity() == null) {
					return;
				}
				if (errCode == ErrorCode.ERROR_OK) {
					LogWriter.i("jianfei", "liveid : " + mLiveInfo.liveId);
				}
			}
		});
	}

	/**
	 * 初始化播放器
	 */
	private void initMediaStreamManager() {
		LogWriter.e(TAG_LOG, "--startCustomerStreaming--");
		if (mRTCMediaStreamingManager != null) {
			mRTCMediaStreamingManager.stopStreaming();
			mRTCMediaStreamingManager.destroy();
		}
		String url = mLiveInfo.pushAddr;
		LogWriter.e(TAG_LOG, "--qiuniujson--" + mLiveInfo.qiniuJson);
		if (mLiveInfo == null || mLiveInfo.qiniuJson == null)
			return;

		mStreamingProfile = StreamingManagerFactory.createStreamingProfile(mLiveInfo.qiniuJson);

		// mMediaStreamingManager =
		// StreamingManagerFactory.createMediaStreamingManger(getActivity(),
		// mLiveInfo.qiniuJson, mStreamingStateChangedListener);

		mRTCMediaStreamingManager = StreamingManagerFactory.createRTCStreamingManager(getContext(), mStreamingStateChangedListener);

		MicrophoneStreamingSetting mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
		mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);
		mRTCMediaStreamingManager.prepare(mMicrophoneStreamingSetting);

		mRTCMediaStreamingManager.setStreamStatusCallback(this);
		mRTCMediaStreamingManager.setAudioSourceCallback(mAudioSourceCallback);
		mRTCMediaStreamingManager.setStreamingSessionListener(mStreamingSessionListener);
		mRTCMediaStreamingManager.startCapture();

	}

	private StreamingProfile mStreamingProfile;

	private StreamingSessionListener mStreamingSessionListener = new StreamingSessionListener() {

		@Override
		public boolean onRestartStreamingHandled(int arg0) {
			// 开始调用系统重新
			LogWriter.e(TAG_LOG, "--StreamingSessionListener--onRestartStreamingHandled--");
			return mRTCMediaStreamingManager.startStreaming();
		}

		@Override
		public boolean onRecordAudioFailedHandled(int arg0) {
			LogWriter.e(TAG_LOG, "--StreamingSessionListener--onRecordAudioFailedHandled--");
			return false;
		}

		@Override
		public Size onPreviewSizeSelected(List<Size> arg0) {
			LogWriter.e(TAG_LOG, "--StreamingSessionListener--onPreviewSizeSelected--");

			return null;
		}

		@Override
		public int onPreviewFpsSelected(List<int[]> list) {
			return 0;
		}
	};

	/**
	 * 结束直播
	 */
	private void stopCustomerStreaming() {

		StatusController.setLiveIng(false);

		if (mRTCMediaStreamingManager != null) {
			mRTCMediaStreamingManager.stopStreaming();
			mRTCMediaStreamingManager.stopCapture();
			mRTCMediaStreamingManager.destroy();
		}
		mLivePresent.stopLive(mLiveInfo.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

			}
		});

	}

	private void startSreaming() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mRTCMediaStreamingManager.setStreamingProfile(mStreamingProfile);
				mRTCMediaStreamingManager.startStreaming();
			}
		}).start();
	}

	// private int tryReconnectionNumber = -10000;
	private boolean isFirstSTREAMING = true;
	private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {

		@Override
		public void onStateChanged(StreamingState streamingState, Object extra) {
			LogWriter.d("streaming", streamingState.ordinal() + "");
			LogWriter.e(TAG_LOG, "--StreamingStateChangedListener--");
			switch (streamingState) {
			case NO_NV21_PREVIEW_FORMAT: // 不支持nv21预览格式.
				LogWriter.e(TAG_LOG, "--NO_NV21_PREVIEW_FORMAT--");
				break;
			case PREPARING:// 网络连接环境的准备.
				LogWriter.e(TAG_LOG, "--PREPARING--");
				break;
			case READY:// 准备好了，可以开始推流了
				startSreaming();
				LogWriter.e(TAG_LOG, "--READY--");
				break;
			case CONNECTING:// 连接中
				LogWriter.e(TAG_LOG, "--CONNECTING--");
				if (isFirstSTREAMING) {
					// 七牛连接时，延迟500ms .进行通知服务器，开始直播
					mToastHandler.sendEmptyMessageDelayed(FLAG_STREAMING_READY, 500);
				} else {
					// 非首次推流
					stopReConNetworkAndQiNiu();
				}
				break;
			case STREAMING:// 推流中
				LogWriter.e(TAG_LOG, "--STREAMING--");
				break;
			case SHUTDOWN:// 关闭
				LogWriter.e(TAG_LOG, "--SHUTDOWN--");
				break;
			case IOERROR:// io异常
				LogWriter.e(TAG_LOG, "--IOERROR--");
//				if (iReconNetworkAndQiNiuStatus == 1) {
//					return;
//				}
				startReConNetworkAndQiNiu();
				break;
			case UNKNOWN:// 位置错误
				LogWriter.e(TAG_LOG, "--UNKNOWN--");
				break;
			case SENDING_BUFFER_EMPTY:// 发送缓冲区是空的。
				LogWriter.e(TAG_LOG, "--SENDING_BUFFER_EMPTY--");
				break;
			case SENDING_BUFFER_FULL:// 发送缓冲区已满
				LogWriter.e(TAG_LOG, "--SENDING_BUFFER_FULL--");
				break;
			case SENDING_BUFFER_HAS_FEW_ITEMS:// 发送缓冲区有几个项目，等待被发送.
				LogWriter.e(TAG_LOG, "--SENDING_BUFFER_HAS_FEW_ITEMS--");
				break;
			case SENDING_BUFFER_HAS_MANY_ITEMS:// 发送缓冲区有许多项目，等待被发送.
				LogWriter.e(TAG_LOG, "--SENDING_BUFFER_HAS_MANY_ITEMS--");
				break;
			case AUDIO_RECORDING_FAIL:// 录音失败
				LogWriter.e(TAG_LOG, "--AUDIO_RECORDING_FAIL--");
				break;
			case OPEN_CAMERA_FAIL:// 打开摄像头失败
				LogWriter.e(TAG_LOG, "--OPEN_CAMERA_FAIL--");
				break;
			case DISCONNECTED:// 已断开连接
				LogWriter.e(TAG_LOG, "--DISCONNECTED--");
				break;
			case INVALID_STREAMING_URL:// 推流地址不合法
				LogWriter.e(TAG_LOG, "--INVALID_STREAMING_URL--");
				break;
			case UNAUTHORIZED_STREAMING_URL:// 推流地址未授权
				LogWriter.e(TAG_LOG, "--UNAUTHORIZED_STREAMING_URL--");
				break;
			case CAMERA_SWITCHED:// 摄像头切换
				LogWriter.e(TAG_LOG, "--CAMERA_SWITCHED--");
				break;
			case TORCH_INFO:// 相机激活后通知火炬信息。
				LogWriter.e(TAG_LOG, "--TORCH_INFO--");
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onStart() {
		super.onStart();
		LogWriter.e("showgift", "-----onStart------");
	}

	public void onResume() {
		super.onResume();
		LogWriter.e("showgift", "-----onResume------");
		mHandler.removeMessages(FLAG_STOP_LIVE);
		if (isPausedByBackground) {
			if (mRTCMediaStreamingManager != null) {
				mLivePresent.resumeLive(mLiveInfo.liveId, null);
			}
		}
		isPausedByBackground = false;

		showCacheGift();
		isPaused = false;
	}

	private boolean isPausedByBackground = false;

	public void onPause() {
		super.onPause();
		isPaused = true;
		LogWriter.e("showgift", "-----onpause------");
	};

	@Override
	public void onStop() {
		super.onStop();
		LogWriter.e("showgift", "-----onStop------");
		if (!UIUtils.isApplicationForground(getContext()))// 如果程序不是在前台
		{
			LogWriter.d("streaming", "gotoBackGround");
			if (mRTCMediaStreamingManager != null) {
				mLivePresent.pauseLive(mLiveInfo.liveId, null);
				isPausedByBackground = true;
				Message msg = Message.obtain();
				msg.obj = new WeakReference<LiveAnchorFragment>(this);
				msg.what = FLAG_STOP_LIVE;

				mHandler.sendMessageDelayed(msg, 1000 * 60);
			}
		}
	}

	@Override
	public void onDestroyView() {
		LogWriter.e("showgift", "-----onDestroyView------");
		DueMessageUtils.getInstance().deleteObserver(this);
		super.onDestroyView();
		destroyHandler();
		
		try {
			if (mRTCMediaStreamingManager!=null) {
				mRTCMediaStreamingManager.stopCapture();
				mRTCMediaStreamingManager.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		super.onDestroy();
		LogWriter.e("showgift", "-----onDestroy------");
		release();
		StatusController.setLiveIng(false);
	};

	/**
	 * 销毁所有handle队列
	 */
	public void destroyHandler() {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
		if (mToastHandler != null) {
			mToastHandler.removeCallbacksAndMessages(null);
		}
		if (mReConControllerHandler != null) {
			mReConControllerHandler.removeCallbacksAndMessages(null);
		}
	}

	/**
	 * 释放资源
	 */
	private void release() {
		stopCustomerStreaming();
		if (mHeartBeatController != null) {
			mHeartBeatController.stopHeartBeat();
		}

		if (mGiftAnimationController != null) {
			mGiftAnimationController.release();
		}
		MessageDispatcher.getInstance().onDestroy();
		EventManager.ins().removeListener(EventTag.STOP_LIVE, mStopListener);
	}

	@Override
	public void onCloseClick() {
		canBack();
	}

	@Override
	public void onTextSend(String text, boolean danmakuOpen) {
		if (danmakuOpen) {
			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_COLOR_BG;
			item.userName = UserMgr.getInstance().getLoginUser().nickname;
			item.birthday = UserMgr.getInstance().getLoginUser().birthday;
			item.gender = UserMgr.getInstance().getLoginUser().gender;
			item.text = text;
			item.userId = UserMgr.getInstance().getUid();
			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateSelfMessage(item);
		} else {
			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_NORMAL;
			item.userName = UserMgr.getInstance().getLoginUser().nickname;
			item.birthday = UserMgr.getInstance().getLoginUser().birthday;
			item.gender = UserMgr.getInstance().getLoginUser().gender;
			item.text = text;
			item.userId = UserMgr.getInstance().getUid();
			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateSelfMessage(item);
		}

	}

	@Override
	protected boolean canBack() {
		showExitLiveDialog("是否结束直播?", false);
		return false;
	}

	@Override
	public void addTextCallbackView(AbstractMessage message) {

		switch (message.msgType) {
		case AbstractMessage.MESSAGE_TYPE_BARRAGE: {
			BulletMessage msg = (BulletMessage) message;

			if (!msg.liveId.equals(mLiveInfo.liveId))// 不是本房间的消息不处理
				return;

			if (msg.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
			{
				UserMgr.getInstance().getLoginUser().allEarnPoint = msg.totalTicket;
				mBottom.setUserInfo(UserMgr.getInstance().getLoginUser());
				return;
			}

			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_COLOR_BG;
			item.userName = msg.fromUserName;
			item.userId = msg.fromUserId;
			item.gender = msg.gender;
			item.text = msg.text;
			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateBulletMessage(msg);

			UserMgr.getInstance().getLoginUser().allEarnPoint = msg.totalTicket;
			mBottom.setUserInfo(UserMgr.getInstance().getLoginUser());

		}
			break;
		case AbstractMessage.MESSAGE_TYPE_TEXT: {
			TextMessage msg = (TextMessage) message;
			if (!msg.liveId.equals(mLiveInfo.liveId))// 不是本房间的消息不处理
				return;

			if (msg.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
				return;
			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_NORMAL;
			item.userId = msg.fromUserId;
			item.userName = msg.fromUserName;
			item.gender = msg.gender;
			item.text = msg.text;
			item.giftId = msg.giftId;
			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateAbstractTextMessage(msg);
		}
			break;
		case AbstractMessage.MESSAGE_TYPE_SYSTEM: {
			SystemMessage msg = (SystemMessage) message;
			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_SYSTEM;
			item.text = msg.text;
			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateSystemMessage(msg);
		}
			break;
		default:
			break;
		}

	}

	@Override
	public void addGiftCallbackView(GiftMessage message) {
		if (!message.liveId.equals(mLiveInfo.liveId))// 不是本房间的消息不处理
			return;

		if (message.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
		{
			UserMgr.getInstance().getLoginUser().allEarnPoint = message.totalTicket;
			mBottom.setUserInfo(UserMgr.getInstance().getLoginUser());
			return;
		}

		Gift gift = GiftController.getInstance().getGiftById(message.giftId);
		if (gift == null)
			return;

		// 用户的黑票
		UserMgr.getInstance().getLoginUser().allEarnPoint = message.totalTicket;
		mBottom.setUserInfo(UserMgr.getInstance().getLoginUser());

		if (isPaused) {
			// 此时主页面被遮盖住了
			mLimitQueue.offer(message);
			return;
		}
		// 礼物上墙
		AudienceGift aGift = new AudienceGift();
		User user = new User();
		user.uid = message.fromUserId;
		user.nickname = message.fromUserName;
		user.gender = message.gender;
		aGift.fromUser = user;
		aGift.gift = gift;
		aGift.amount = message.amount;
		aGift.time = System.currentTimeMillis();
		mHeader.addGift(aGift);

		if (gift.type != Gift.TYPE_NORMAL)// 带全屏动画的礼物
		{
			if (mGiftAnimationController == null)
				mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);

			mGiftAnimationController.addAnimation(aGift);
		}
	}

	@Override
	public void addLiveLikeCallbackView(LiveMessage message) {
		if (!message.liveId.equals(mLiveInfo.liveId))
			return;

		if (message.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
			return;

		DanmakuItem item = new DanmakuItem();
		item.type = DanmakuItem.TYPE_LIKE;
		item.userName = message.fromUserName;
		item.gender = message.gender;
		item.text = message.text;
		item.userId = message.fromUserId;
		mDanmakuViewFragment.putDanmakuItem(item);
		mScrollViewFragment.updateLiveMessage(message);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (getActivity() == null)
			return;

		if (LivePrepareFragment.REQUEST_CODE == requestCode) {
			if (Activity.RESULT_OK == resultCode) {
				String topic = data.getStringExtra("title");
				mLiveInfo.title = topic;
				mHeader.setLiveTopic(topic);
			}
		}
	}

	/**
	 * 刷新音量条
	 * 
	 * @param hasVoice
	 */
	private void refreshSelfVoice(boolean hasVoice) {
		mBottom.refreshVoice(hasVoice);
	}

	private static final int FLAG_STOP_LIVE = 0;// 结束直播
	private static final int FLAG_REFRESH_SELF_VOICE = 1;// 刷新自己的音量条
	private static final int FLAG_AF_BUFFER = 2;
	private static final int FLAG_STREAMING = 3;
	private static final int FLAG_STREAMING_READY = 4;
	private static final int FLAG_NETWORK_STATUS_OFF = 5;// 网路断开
	private static final int FLAG_NETWORK_STATUS_ON = 6;// 网路连接
	private static final int FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE = 7;// 重新连接时的提示

	private LiveHandler mHandler = new LiveHandler();

	private static class LiveHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			WeakReference<LiveAnchorFragment> wr = (WeakReference<LiveAnchorFragment>) msg.obj;
			if (wr != null && wr.get() != null) {
				switch (msg.what) {
				case FLAG_STOP_LIVE:
					if (wr.get().getActivity() != null) {
						NavigationController.gotoLiveFinishedFragment(wr.get().getContext(), wr.get().mLiveInfo, true);
						wr.get().getActivity().finish();
					}
					break;
				case FLAG_REFRESH_SELF_VOICE:
					removeMessages(FLAG_REFRESH_SELF_VOICE);
					if (wr.get().getActivity() != null) {
						wr.get().refreshSelfVoice(msg.arg1 == 1);
					}
					break;
				}
			}
		}
	}

	private Handler mToastHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case FLAG_AF_BUFFER:
				UIUtils.showToast(R.string.network_status_on_limit);
				break;
			case FLAG_STREAMING:
				UIUtils.showToast(R.string.network_status_on_unlimit);
				break;
			case FLAG_STREAMING_READY:
				isFirstSTREAMING = false;

				mLivePresent.getStartLive(new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {

						}
					}
				}, mLiveInfo.liveId);
				break;
			case FLAG_NETWORK_STATUS_OFF:
				//
				UIUtils.showToast(R.string.network_status_off);
				break;
			case FLAG_NETWORK_STATUS_ON:
				UIUtils.showToast(R.string.network_status_on_success);
				break;
			case FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE:
				removeMessages(FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE);
				UIUtils.showToast(R.string.network_status_on_limit);
			default:
				break;
			}
		}
	};

	@Override
	public void update(Observable observable, Object data) {
		try {
			ObServerMessage ob = (ObServerMessage) data;
			switch (ob.type) {
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_NETWORK_OK:
				// 网路连接成功，七牛重新连接
				mToastHandler.sendEmptyMessage(FLAG_NETWORK_STATUS_ON);
				LogWriter.e(TAG_LOG, "network---on");
				startReConNetworkAndQiNiu();
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_NETWORK_OFF:
				mToastHandler.sendEmptyMessage(FLAG_NETWORK_STATUS_OFF);
				LogWriter.e(TAG_LOG, "network-----off");
				break;
			}

			if (mBottom != null) {
				mBottom.messageNotify(data);
			}
		} catch (Exception e) {

		}
	}

	private OnItemClickListener mDanmakuItemClickListener = new OnItemClickListener() {

		@Override
		public void OnItemClick(DanmakuItem item) {
			if (item.type == DanmakuItem.TYPE_SYSTEM) {
				return;
			}

			if (UserMgr.getInstance().getUid().equals(item.userId)) {
				return;
			}
			User user = new User();
			user.uid = item.userId;
			user.nickname = item.userName;
			user.gender = item.gender;
			user.birthday = item.birthday;
			if (!clickOlder())
				return;

			UserDialog ud = new UserDialog(getContext(), user, mLiveInfo.liveId, false, UserDialog.USERDIALOG_OPENTYPE_ANCHOR);
			ud.show();
		}

		private long lastClickTime = 0;

		private boolean clickOlder() {
			long currentTime = System.currentTimeMillis();
			if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
				return false;
			lastClickTime = System.currentTimeMillis();
			return true;
		}

		@Override
		public void onItemLongClick(DanmakuItem item) {
			if (item.type == DanmakuItem.TYPE_SYSTEM) {
				return;
			}

			if (UserMgr.getInstance().getUid().equals(item.userId)) {
				return;
			}

			mBottom.atUser(item.userName);
		}
	};

	/**
	 * 心跳错误了，提示结束直播
	 */
	@Override
	public void onHeartbearError() {
		release();// 结束直播
		showExitLiveDialog(getResources().getString(R.string.live_network_error_tip), true);
	}

	@Override
	public void notifyStreamStatusChanged(StreamStatus streamStatus) {
		// LogWriter.e(TAG_LOG, "--notifyStreamStatusChanged--");
		if (streamStatus != null) {
			int ab = streamStatus.audioBitrate;
			int af = streamStatus.audioFps;
			if (af < 36 && ((System.currentTimeMillis() - timeStamp) >= 60000)) {
				timeStamp = System.currentTimeMillis();
				Message message = Message.obtain();
				message.what = FLAG_AF_BUFFER;
				mToastHandler.sendMessage(message);
			}
			// LogWriter.e(TAG_LOG,
			// "--notifyStreamStatusChanged--ab="+ab+"--af="+af);
			// LogUtil.d("LiveAnchorFragment", "ab:" + ab + "---af:" + af +
			// "---");
		}
	}

	private long timeStamp = System.currentTimeMillis();

	private EventListener mStopListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			if (getActivity() != null) {
				getActivity().finish();
			}

		}
	};

	private long recordCallTime = 0l;

	private AudioSourceCallback mAudioSourceCallback = new AudioSourceCallback() {

		@Override
		public void onAudioSourceAvailable(ByteBuffer byteBuffer, int arg1, long arg2, boolean arg3) {
			long nowTime = System.currentTimeMillis();
			if (nowTime - recordCallTime > 500) {
				if (byteBuffer != null) {
					byte[] buffer = byteBuffer.array();
					double volume = calculateVolume(buffer);
					LogWriter.d("anchor", "volume:" + volume);
					if (volume > 8) {
						Message msg = Message.obtain();
						msg.obj = new WeakReference<LiveAnchorFragment>(LiveAnchorFragment.this);
						msg.what = FLAG_REFRESH_SELF_VOICE;
						msg.arg1 = 1;
						mHandler.sendMessage(msg);
					} else {
						Message msg = Message.obtain();
						msg.obj = new WeakReference<LiveAnchorFragment>(LiveAnchorFragment.this);
						msg.what = FLAG_REFRESH_SELF_VOICE;
						msg.arg1 = 0;
						mHandler.sendMessage(msg);
					}
				}
				recordCallTime = nowTime;
			}
		}
	};

	private double calculateVolume(byte[] buffer) {
		double sumVolume = 0.0;
		double avgVolume = 0.0;
		double volume = 0.0;

		for (int i = 0; i < buffer.length; i += 2) {
			int v1 = buffer[i] & 0xFF;
			if (i + 1 >= buffer.length)
				break;
			int v2 = buffer[i + 1] & 0xFF;
			int temp = v1 + (v2 << 8);// 小端
			if (temp >= 0x8000) {
				temp = 0xffff - temp;
			}
			sumVolume += Math.abs(temp);
		}
		avgVolume = sumVolume / buffer.length / 2;
		volume = Math.log10(1 + avgVolume) * 10;

		return volume;
	}

	/**
	 * 关闭直播对话框
	 * 
	 * @param title
	 * @param content
	 * @param iscancle
	 */
	private void showExitLiveDialog(String content, boolean iscancle) {
		TipDialog td = new TipDialog(getContext());
		td.setContent(content);
		if (iscancle) {
			td.setBtnCancelVisibity(View.GONE);
			td.setCancelable(false);
			td.setCanceledOnTouchOutside(false);
		}
		td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

			@Override
			public void onOkClick(Dialog dialog) {
				NavigationController.gotoLiveFinishedFragment(getContext(), mLiveInfo, true);
				getActivity().finish();
			}

			@Override
			public void onCancleClick(Dialog dialog) {

			}
		});
		td.show();

	}

	// ========================网路状态变更七牛重新连接=========================
	// 结束重连接
	private void stopReConNetworkAndQiNiu() {
		mReConControllerHandler.sendEmptyMessage(TYPE_RECON_NETWORK_QINIU_STOP_FLAG);

	}

	/**
	 * 开始重练
	 */
	private void startReConNetworkAndQiNiu() {
		conReConNetworkAndQiNiu();
	}

	/**
	 * 继续重练
	 * 
	 * @param networkType
	 */
	private void conReConNetworkAndQiNiu() {
		mToastHandler.sendEmptyMessage(FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE);
		
		mLivePresent.getLiveInfo(mLiveInfo.liveId, new JSONResponse() {
			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					try {
						int istatus = json.optJSONObject("live").optInt("status");
						if (istatus == 1) {
							// 房间存在
							mHeartBeatController.stopHeartBeat();
							mHeartBeatController.startHeartBeat();
							mRTCMediaStreamingManager.stopStreaming();
							// 开始推流服务
							mRTCMediaStreamingManager.startStreaming();
							LogWriter.e(TAG_LOG, "---getLiveInfo---status =1");
						} else {
							// 房间不存在
							mHeartBeatController.stopHeartBeat();
							release();// 结束直播
							stopReConNetworkAndQiNiu();// 结束重练
							showExitLiveDialog(getResources().getString(R.string.live_network_error_tip), true);
						}
					} catch (Exception e) {
						LogWriter.e(TAG_LOG, "--getLiveInfo--Exception");
						// 继续重练
						mReConControllerHandler.sendEmptyMessageDelayed(TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG, iRconNetworkOn);
					}
				} else {
					// 网路请求失败
					LogWriter.e(TAG_LOG, "---conReConNetworkAndQiNiu---status =404");
					mReConControllerHandler.sendEmptyMessageDelayed(TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG, iReconNetworkOff);
				}
			}

		});
	}

	private static final int TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG = 5001;// 继续重练
	private static final int TYPE_RECON_NETWORK_QINIU_STOP_FLAG = 5002;// 结束重练
	private int iReconNetworkOff = 5000;// 无网络情况，重练时间
	private int iRconNetworkOn = 5000;// 有网络情况，重练时间

	public Handler mReConControllerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int iTypeWtat = msg.what;
			LogWriter.e(TAG_LOG, "--mReConControllerHandler--" + iTypeWtat);
			switch (iTypeWtat) {
			case TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG:
				removeMessages(TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG);
				conReConNetworkAndQiNiu();
				break;
			case TYPE_RECON_NETWORK_QINIU_STOP_FLAG:
				mReConControllerHandler.removeCallbacksAndMessages(null);
				break;
			}
		}
	};

	// =============================礼物缓存====================
	public void showCacheGift() {
		if (mLimitQueue != null && mLimitQueue.size() > 0) {
			// 显示缓存的礼物
			int isize = mLimitQueue.size();
			for (int i = 0; i < isize; i++) {
				showCachGiftToWall((GiftMessage) mLimitQueue.poll());
			}
		}

	}

	/**
	 * 礼物上墙
	 */
	public void showCachGiftToWall(GiftMessage message) {
		// 礼物上墙
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
		mHeader.addGift(aGift);

		if (gift.type != Gift.TYPE_NORMAL)// 带全屏动画的礼物
		{
			if (mGiftAnimationController == null)
				mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);

			mGiftAnimationController.addAnimation(aGift);
		}
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LiveAnchorFragment";
	}
}
