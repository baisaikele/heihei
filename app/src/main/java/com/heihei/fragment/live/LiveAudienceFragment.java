package com.heihei.fragment.live;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import com.base.danmaku.DanmakuItem;
import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.base.widget.message.DanmakuViewFragment;
import com.base.widget.message.InformationPagerAdapter;
import com.base.widget.message.ScrollViewFragment;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.GiftDialog.OnGiftSendListener;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.live.logic.GiftAnimationController;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.logic.HeartBeatController;
import com.heihei.fragment.live.widget.LiveBottom;
import com.heihei.fragment.live.widget.LiveBottom.OnTextSendListener;
import com.heihei.fragment.live.widget.LiveHeader;
import com.heihei.logic.StatusController;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.LivePresent;
import com.heihei.media.MediaPlayFactory;
import com.heihei.model.AudienceGift;
import com.heihei.model.Gift;
import com.heihei.model.LiveInfo;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.heihei.model.msg.ActionMessageDispatcher;
import com.heihei.model.msg.MessageDispatcher;
import com.heihei.model.msg.api.ActionMessageCallback;
import com.heihei.model.msg.api.LiveMessageCallback;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.bean.SystemMessage;
import com.heihei.model.msg.bean.TextMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer.OnBufferingUpdateListener;
import com.pili.pldroid.player.PLMediaPlayer.OnCompletionListener;
import com.pili.pldroid.player.PLMediaPlayer.OnErrorListener;
import com.pili.pldroid.player.PLMediaPlayer.OnInfoListener;
import com.pili.pldroid.player.PLMediaPlayer.OnPreparedListener;
import com.wmlives.heihei.R;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

/**
 * 直播的观看端
 * 
 * @author admin
 *
 */
public class LiveAudienceFragment extends BaseFragment implements OnClickListener, OnTextSendListener, LiveMessageCallback, OnGiftSendListener, ActionMessageCallback, Observer {
	private static final String TAG = "LiveAudienceFragment";

	private LiveHeader mHeader;
	// private DanmakuView mDanmakuView;
	private ViewPager mViewPager;
	private LiveBottom mBottom;
	private FrameLayout giftContentView;

	private PLMediaPlayer mPlayer;

	private LiveInfo mLiveInfo;
	private LivePresent mLivePresent = new LivePresent();
	private HeartBeatController mHeartBeatController;
	private MessageDispatcher mMessageDispatcher;

	private GiftAnimationController mGiftAnimationController;

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_live_andience);
		firstOpen = SharedPreferencesUtil.getInstance().get("firstOpen", true);
	}

	@Override
	protected void viewDidLoad() {

		mLiveInfo = (LiveInfo) mViewParam.data;
		if (mLiveInfo == null) {
			UIUtils.showToast("直播不存在");
			getActivity().finish();
			return;
		}
		mHeader = (LiveHeader) findViewById(R.id.header);
		mHeader.setReplayInfo(new PlayActivityInfo(true, getActivity()));
		mBottom = (LiveBottom) findViewById(R.id.live_bottom);
		giftContentView = (FrameLayout) findViewById(R.id.gift_contentView);
		mHeader.setType(LiveHeader.TYPE_AUDIENCE);
		mBottom.setType(mLiveInfo.liveId, LiveHeader.TYPE_AUDIENCE);
		mBottom.setOnGiftSendListener(this);
		mBottom.setActivity(getActivity());
		mBottom.setOnTextSendListener(this);
		// mDanmakuView = (DanmakuView) findViewById(R.id.danmakuview);
		// mDanmakuView.setOnClickListener(this);
		// mDanmakuView.setOnItemClickListener(mDanmakuItemClickListener);

		getView().setKeepScreenOn(true);

		DueMessageUtils.getInstance().addObserver(this);

		EventManager.ins().registListener(EventTag.STOP_AUDIENCE, mStopListener);

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
		 AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
	     audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
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
		if (mHeader == null) {
			getActivity().finish();
		}
		mHeader.setLiveTopic(mLiveInfo.title);
		mHeader.setUserInfo(mLiveInfo.creator);
		// 首次获取房间信息
		getLiveInfoByJoinLive(new OnLiveInfoGetListener() {
			@Override
			public void onLiveInfoGet(boolean success, int errCode) {
				if (success) {
					if (mLiveInfo.canListen())// 可以听
					{
						StatusController.getInstance().setCurrentStatus(StatusController.STATUS_AUDIENCE);
						mHandler.sendEmptyMessage(FLAG_JOIN_ROOM);
					} else {
						if (getActivity() != null) {
							NavigationController.gotoLiveFinishedFragment(getContext(), mLiveInfo, false);
							getActivity().finish();
						}
					}
				} else {
					if (getActivity() != null) {
						getActivity().finish();
					}
					UIUtils.showToast("网络异常");
				}

			}
		}, true);
       
	}

	/**
	 * 加入直播从而获取直播房间信息
	 */
	private void getLiveInfoByJoinLive(final OnLiveInfoGetListener mListener, final boolean playing) {
		mLivePresent.joinLive(mLiveInfo.liveId, new JSONResponse() {
			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					JSONObject liveJ = json.optJSONObject("live");
					JSONObject userJ = json.optJSONObject("userInfo");
					int totalTicket = 0;
					if (liveJ != null) {
						mLiveInfo.parseJSON(liveJ);
						totalTicket = liveJ.optInt("totalTickets");
					}
					if (userJ != null) {
						mLiveInfo.creator = new User(userJ);
					}
					mLiveInfo.creator.allEarnPoint = totalTicket;
					mHeader.setLiveTopic(mLiveInfo.title);
					mHeader.setUserInfo(mLiveInfo.creator);
					// 请求观看顾客
					requestUserList();
					if (playing) {
						// 允许播放开启
						if (mHeartBeatController == null) {
							mHeartBeatController = new HeartBeatController(mLiveInfo.liveId, false);
						}
						mHeartBeatController.startHeartBeat();
						startInitPlay();

						DanmakuItem item = new DanmakuItem();
						item.type = DanmakuItem.TYPE_NORMAL;
						item.userName = UserMgr.getInstance().getLoginUser().nickname;
						item.birthday = UserMgr.getInstance().getLoginUser().birthday;
						item.gender = UserMgr.getInstance().getLoginUser().gender;
						item.text = "加入房间";
						item.userId = UserMgr.getInstance().getUid();

						mDanmakuViewFragment.putDanmakuItem(item);
						mScrollViewFragment.updateSelfMessage(item);
					}
					if (mListener != null) {
						mListener.onLiveInfoGet(true, errCode);
					}
				} else {
					if (mListener != null) {
						mListener.onLiveInfoGet(false, errCode);
					}

				}

			}
		});
	}

	/**
	 * 获取直播间信息
	 */
	private void getLiveInfoByGetLive(final OnLiveInfoGetListener mListener, final boolean playing) {
		mLivePresent.getLiveInfo(mLiveInfo.liveId, new JSONResponse() {
			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					JSONObject liveJ = json.optJSONObject("live");
					JSONObject userJ = json.optJSONObject("userInfo");
					int totalTicket = 0;
					if (liveJ != null) {
						mLiveInfo.parseJSON(liveJ);
						totalTicket = liveJ.optInt("totalTickets");
					}
					if (userJ != null) {
						mLiveInfo.creator = new User(userJ);
					}
					mLiveInfo.creator.allEarnPoint = totalTicket;
					mHeader.setLiveTopic(mLiveInfo.title);
					mHeader.setUserInfo(mLiveInfo.creator);
					if (playing) {
						// 允许播放开启
						if (mHeartBeatController == null) {
							mHeartBeatController = new HeartBeatController(mLiveInfo.liveId, false);
						}
						mHeartBeatController.startHeartBeat();
						startInitPlay();
					}
					if (mListener != null) {
						mListener.onLiveInfoGet(true, errCode);
					}
				} else {
					if (mListener != null) {
						mListener.onLiveInfoGet(false, errCode);
					}

				}

			}
		});
	}

	/**
	 * 退出房间
	 */
	private void quitRoom() {
		releasePlayer();
		new Thread(new Runnable() {
			@Override
			public void run() {
				mLivePresent.getQuitLive(mLiveInfo.liveId, new JSONResponse() {
					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {

						}
					}
				});
			}
		}).start();

	}

	/**
	 * 加入房间，清除消息队列
	 */
	private void joinRoomMessage() {

		mMessageDispatcher = MessageDispatcher.getInstance();
		mMessageDispatcher.joinRoom(mLiveInfo.liveId);
		mMessageDispatcher.SubscribeCallback(LiveAudienceFragment.this);

		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE, LiveAudienceFragment.this);
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE, LiveAudienceFragment.this);
	}

	/**
	 * 获取房间观众
	 */
	private void requestUserList() {
		mHeader.refreshUserList(mLiveInfo.liveId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.danmakuview:
			mBottom.hideKeyboard();
		}
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
	/**
	 * 初始化播放器
	 */
	private void startInitPlay() {
		try {
			releasePlayer();
			if (StringUtils.isEmpty(mLiveInfo.streamAddr))// 播放地址为空
			{
				UIUtils.showToast("播放地址为空");
				getActivity().finish();
				return;
			}
			LogWriter.e("play---address---", mLiveInfo.streamAddr);
	        //创建对象
			mPlayer = MediaPlayFactory.createMediaPlayer(HostApplication.getInstance(), true);
			mPlayer.setOnInfoListener(mOnInfoListener);
			mPlayer.setOnPreparedListener(mOnPreparedListener);// 准备
			mPlayer.setOnErrorListener(mOnErrorListener);
			mPlayer.setOnCompletionListener(mCompletionListener);
			mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
			//mPlayer.setWakeMode(getContext(), PowerManager.PARTIAL_WAKE_LOCK);
			//mPlayer.setLooping(true);
			prepare(mLiveInfo.streamAddr);
		} catch (NullPointerException e) {
			e.printStackTrace();
			LogWriter.e(TAG, "startPlay start is Exception ");
		}
	}

//	public static WeakReference<PLMediaPlayer> mWeakReference;

	/**
	 * 设置播放地址
	 * 
	 * @param url
	 */
	private void prepare(String url) {
		try {
			mPlayer.setDataSource(url);
			mPlayer.prepareAsync();
//			mWeakReference = new WeakReference<PLMediaPlayer>(mPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			LogWriter.e(TAG, "play is prepare is Exception");
		}
	}

	private OnInfoListener mOnInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(PLMediaPlayer arg0, int whate, int extra) {
			LogWriter.e("play", "OnInfoListener---onInfo===whate:"+whate+",extra="+extra);
//			 switch (whate) {
//			 case PLMediaPlayer.MEDIA_INFO_UNKNOWN:
//			 //未知消息
//				 LogWriter.e("play", "OnInfoListener---onInfo==MEDIA_INFO_UNKNOWN");
//			 break;
//			 case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//			 //第一帧已成功渲染
//				 LogWriter.e("play", "OnInfoListener---onInfo==MEDIA_INFO_VIDEO_RENDERING_START");
//			 break;
//			 case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
//			 //开始缓冲
//				 LogWriter.e("play", "OnInfoListener---onInfo==MEDIA_INFO_BUFFERING_START");
//			 break;
//			 case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
//			 //停止缓冲
//				 LogWriter.e("play", "OnInfoListener---onInfo==MEDIA_INFO_BUFFERING_END");
//			 break;
//			 case PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
//				 LogWriter.e("play", "OnInfoListener---onInfo==MEDIA_INFO_AUDIO_RENDERING_START");
//				 break;
//			 case PLMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
//				 LogWriter.e("play", "OnInfoListener---onInfo==MEDIA_INFO_VIDEO_ROTATION_CHANGED");
//				 break;
//			 default:
//			 break;
//			 }
			return false;
		}
	};

	private boolean isPlayForError = false;// 播放器是否从异常中苏醒
	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(PLMediaPlayer arg0) {
			LogWriter.d("play", "OnPreparedListener---onPrepared");
			mPlayer.start();
			if (isPlayForError) {
				// 结束重练
				isPlayForError = false;
				mHandler.sendEmptyMessage(FLAG_READ_FRAME_UN_TIMEOUT);
				stopReConNetworkAndQiNiu();
			}

		}
	};

	private OnErrorListener mOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(PLMediaPlayer mediaPlayer, int errorCode) {
			LogWriter.e("playerror", errorCode+"");
			 boolean isNeedReconnect = false;
			switch (errorCode) {
			case PLMediaPlayer.ERROR_CODE_INVALID_URI:
				// 无效url
				LogWriter.e("playerror", "Error happened, invalid url");
				break;
			case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
				// 播放资源不存在
				LogWriter.e("playerror", "Error happened, 404");
				break;
			case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
				// 服务器拒绝连接
				LogWriter.e("playerror", "Error happened, refused");
				break;
			case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
				// 连接超时
				LogWriter.e("playerror", "Error happened, timeout");
				isNeedReconnect = true;
				break;
			case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
				// 空的播放列表
				LogWriter.e("playerror", "Error empty playlist");
				break;
			case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
				// 与服务器断开连接
				LogWriter.e("playerror", "Error happened, disconnected");
				isNeedReconnect = true;
				break;
			case PLMediaPlayer.ERROR_CODE_IO_ERROR:
				// 网路异常
				LogWriter.e("playerror", "Error happened, network io error");
				// //重新连接
				isNeedReconnect = true;
				break;
			case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
				// 禁播
				LogWriter.e("playerror", "Error unauthorized ");
				break;
			case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
				// 服务器准备超时
				isNeedReconnect = true;
				break;
			case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT: 
				// 读取数据超时
				LogWriter.e("playerror", "Error happened, read frame_timeout");
				isNeedReconnect = true;
			
				break;
			case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
				LogWriter.e("playerror", "unknown error !");
				break;
			}
				releasePlayer();
	            if (isNeedReconnect) {
	            	isPlayForError = true;
	            	LogWriter.e("playerror", "isNeedReconnect");
					mHandler.sendEmptyMessage(FLAG_READ_FRAME_TIMEOUT);
					// 重新连接					
					startReConNetworkAndQiNiu();
	            } else {
	            	getActivity().finish();
	            }
			// If return false, then `onCompletion` will be called
			return true;
		}
	};

	private OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener() {

		@Override
		public void onBufferingUpdate(PLMediaPlayer arg0, int arg1) {
			//LogWriter.e("play", "OnBufferingUpdateListener---onBufferingUpdate=="+String.valueOf(arg1));
			// 该回调用于监听当前播放器已经缓冲的数据量占整个视频时长的百分比，在播放直播流中无效，仅在播放文件和回放时才有效。
		}
	};

	/**
	 * (无新数据流时，回调) 播放完成
	 */
	private OnCompletionListener mCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(PLMediaPlayer arg0) {
			LogWriter.e("play", "OnCompletionListener---onCompletion");
		}
	};
	/**
	 * 播放器错误时释放资源
	 */
	private void releasePlayer(){
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
			mPlayer = null;
		}
	}

//	/**
//	 * 主播网络不好，显示系统消息
//	 */
//	private void showAnchorNetworkError() {
//		DanmakuItem item = new DanmakuItem();
//		item.text = "主播离开一下下，稍后回来";
//		item.type = DanmakuItem.TYPE_SYSTEM;
//		// mDanmakuView.addDanmakuItem(item);
//		mDanmakuViewFragment.putDanmakuItem(item);
//	}

//	/**
//	 * 提示主播回来了的系统消息
//	 */
//	private void showAnchorBack() {
//		DanmakuItem item = new DanmakuItem();
//		item.text = "主播回来啦，直播继续~";
//		item.type = DanmakuItem.TYPE_SYSTEM;
//		// mDanmakuView.addDanmakuItem(item);
//		mDanmakuViewFragment.putDanmakuItem(item);
//	}

	private static final int FLAG_JOIN_ROOM = 0;
	private static final int FLAG_RECONNECT_MEDIA = 1;// 重连播放器
	private static final int FLAG_NETWORK_STATUS_OFF = 2;// 网络不可用
	private static final int FLAG_NETWORK_STATUS_ON = 3;// 网络可用
	private static final int FLAG_READ_FRAME_TIMEOUT = 4;// 网络不畅，读取超时
	private static final int FLAG_READ_FRAME_UN_TIMEOUT = 5;// 网络畅通，
	private static final int FLAG_LIVE_ADDRESS_URL_ERROR = 6;// 主播地址有误，
	private static final int FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE = 7;// 重新连接时的提示

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case FLAG_JOIN_ROOM:
				removeMessages(FLAG_JOIN_ROOM);
				// 加入房间
				joinRoomMessage();
				break;
			case FLAG_RECONNECT_MEDIA:
				removeMessages(FLAG_RECONNECT_MEDIA);
				// 重练播放器
				reConnectMediaPlayer();
				break;
			case FLAG_NETWORK_STATUS_OFF:
				removeMessages(FLAG_NETWORK_STATUS_OFF);
				// 当前网络不可用
				UIUtils.showToast(R.string.network_status_off);
				// fragment.startReconnect();
				break;
			case FLAG_NETWORK_STATUS_ON:
				removeMessages(FLAG_NETWORK_STATUS_ON);
				// 当前网络不可用
				UIUtils.showToast(R.string.network_status_on_success);
				// fragment.startReconnect();
				break;
			case FLAG_READ_FRAME_TIMEOUT:
				removeMessages(FLAG_READ_FRAME_TIMEOUT);
				// 网络超时
				UIUtils.showToast(R.string.network_status_on_limit);
				// fragment.showAnchorNetworkError();
				break;
			case FLAG_READ_FRAME_UN_TIMEOUT:
				removeMessages(FLAG_READ_FRAME_UN_TIMEOUT);
				UIUtils.showToast(R.string.network_status_on_unlimit);
				// fragment.showAnchorBack();
				break;
			case FLAG_LIVE_ADDRESS_URL_ERROR:
				removeMessages(FLAG_LIVE_ADDRESS_URL_ERROR);
				UIUtils.showToast(R.string.error_live_exception_message);
				break;
			case FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE:
				removeMessages(FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE);
				UIUtils.showToast(R.string.network_status_on_limit);
				break;
			}
		};
	};

	@Override
	protected boolean canBack() {
		TipDialog td = new TipDialog(getContext());
		td.setContent("是否离开此房间?");
		td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {
			@Override
			public void onOkClick(Dialog dialog) {
				quitRoom();
				getActivity().finish();
			}

			@Override
			public void onCancleClick(Dialog dialog) {
			}
		});
		td.show();

		return false;
	}

	/**
	 * 自己送礼物
	 */
	@Override
	public void onGiftSend(Gift gift, int amount) {
		if (gift != null) {
			AudienceGift mAudienceGift = new AudienceGift();
			mAudienceGift.fromUser = UserMgr.getInstance().getLoginUser();
			mAudienceGift.gift = gift;
			mAudienceGift.amount = amount;
			mAudienceGift.time = System.currentTimeMillis();
			mAudienceGift.gift_uuid = gift.gift_uuid;
			mHeader.addGift(mAudienceGift);

			if (gift.type != Gift.TYPE_NORMAL) {
				if (mGiftAnimationController == null) {
					mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);
				}
				mGiftAnimationController.addAnimation(mAudienceGift);

				mBottom.dismissGiftDialog();
			}
			// 自己送礼上弹幕
			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_NORMAL;
			item.userName = UserMgr.getInstance().getLoginUser().nickname;
			item.birthday = UserMgr.getInstance().getLoginUser().birthday;
			item.gender = UserMgr.getInstance().getLoginUser().gender;
			item.text = "送主播" + gift.name;
			item.giftId = gift.id;
			item.userId = UserMgr.getInstance().getUid();
			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateSelfMessage(item);
		}
	}

	@Override
	public void addTextCallbackView(AbstractMessage message) {
		switch (message.msgType) {
		case AbstractMessage.MESSAGE_TYPE_BARRAGE: {
			BulletMessage msg = (BulletMessage) message;
			if (!msg.liveId.equals(mLiveInfo.liveId))// 不是本房间的消息不处理
			{
				return;
			}

			if (msg.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的只处理黑票数
			{
				mLiveInfo.creator.allEarnPoint = msg.totalTicket;
				mHeader.setUserInfo(mLiveInfo.creator);
				return;
			}

			DanmakuItem item = new DanmakuItem();
			item.type = DanmakuItem.TYPE_COLOR_BG;
			item.userName = msg.fromUserName;
			item.gender = msg.gender;
			item.text = msg.text;
			item.userId = msg.fromUserId;
			mLiveInfo.creator.allEarnPoint = msg.totalTicket;
			mHeader.setUserInfo(mLiveInfo.creator);

			mDanmakuViewFragment.putDanmakuItem(item);
			mScrollViewFragment.updateBulletMessage(msg);
		}
			break;
		case AbstractMessage.MESSAGE_TYPE_TEXT: {
			TextMessage msg = (TextMessage) message;
			if (!msg.liveId.equals(mLiveInfo.liveId))// 不是本房间的消息不处理
			{
				return;
			}

			if (msg.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
			{
				return;
			}
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

	/**
	 * 接收到礼物消息
	 */
	@Override
	public void addGiftCallbackView(GiftMessage message) {
		LogWriter.i("jianfei", "addGiftCallbackView");
		if (!message.liveId.equals(mLiveInfo.liveId))// 不是本房间的消息不处理
		{
			return;
		}

		if (message.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
		{
			mLiveInfo.creator.allEarnPoint = message.totalTicket;
			mHeader.setUserInfo(mLiveInfo.creator);
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
		mHeader.addGift(aGift);

		mLiveInfo.creator.allEarnPoint = message.totalTicket;
		mHeader.setUserInfo(mLiveInfo.creator);

		if (gift.type != Gift.TYPE_NORMAL)// 带全屏动画的礼物
		{
			if (mGiftAnimationController == null) {
				mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);
			}
			mGiftAnimationController.addAnimation(aGift);
		}

	}

	/**
	 * 接收到点亮消息
	 */
	@Override
	public void addLiveLikeCallbackView(LiveMessage message) {
		if (!message.liveId.equals(mLiveInfo.liveId)) {
			return;
		}

		if (message.fromUserId.equals(UserMgr.getInstance().getUid()))// 如果是自己发的也不处理
		{
			return;
		}

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
	public void callback(ActionMessage message) {
		if (message.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE))// 主播退出房间消息
		{
			LogWriter.d("play", "m:" + message.live.liveId + "-l:" + mLiveInfo.liveId);

			if (message.live != null && message.live.liveId.equals(mLiveInfo.liveId)) {
				quitRoom();// 退出房间
				NavigationController.gotoLiveFinishedFragment(getContext(), mLiveInfo, false);
				getActivity().finish();
			}
		} else if (message.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE)) {
			LiveInfo info = message.live;
			if (info != null && info.liveId.equals(mLiveInfo.liveId)) {
				mLiveInfo.title = info.title;
				mHeader.setLiveTopic(mLiveInfo.title);
			}
		}

	}

	private EventListener mStopListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			quitRoom();// 退出房间
			if (getActivity() != null) {
				getActivity().finish();
			}

		}
	};

	public static interface OnLiveInfoGetListener {

		public void onLiveInfoGet(boolean success, int errCode);
	}


	/**
	 * 释放资源
	 */
	private void release() {
		if (StatusController.getInstance().getCurrentStatus() == StatusController.STATUS_AUDIENCE) {
			StatusController.getInstance().resetStatus();
		}
		releasePlayer();

		if (mHeartBeatController != null) {
			mHeartBeatController.stopHeartBeat();
		}

		if (mMessageDispatcher != null) {
			mMessageDispatcher.onDestroy();
		}

		if (mGiftAnimationController != null) {
			mGiftAnimationController.release();
		}

		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE, this);
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE, this);
		EventManager.ins().removeListener(EventTag.STOP_AUDIENCE, mStopListener);
	}

	@Override
	public void update(Observable observable, Object data) {
		try {
			Message msg = Message.obtain();
			ObServerMessage ob = (ObServerMessage) data;
			switch (ob.type) {
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_NETWORK_OK:
				// 网路连接成功，七牛重新连接
				mHandler.sendEmptyMessage(FLAG_NETWORK_STATUS_ON);
				LogWriter.e(TAG, "network---on");
				startReConNetworkAndQiNiu();
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_NETWORK_OFF:
				mHandler.sendEmptyMessage(FLAG_NETWORK_STATUS_OFF);
				LogWriter.e(TAG, "network-----off");
				break;
			}

			if (mBottom != null) {
				mBottom.messageNotify(data);
			}
		} catch (Exception e) {

		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		releasePlayer();
		AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
	}

	@Override
	public void onDestroy() {
		DueMessageUtils.getInstance().deleteObserver(this);
		quitRoom();
		super.onDestroy();
		release();
		destoryHandler();
	}

	/*
	 * 清除handler
	 */
	private void destoryHandler() {
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	// ================================播放器重新连接=============================
	/**
	 * 重连播放器
	 */
	private void reConnectMediaPlayer() {
		try {
				releasePlayer();
				mPlayer = MediaPlayFactory.createMediaPlayer(HostApplication.getInstance(), true);
				mPlayer.setOnInfoListener(mOnInfoListener);
				mPlayer.setOnPreparedListener(mOnPreparedListener);// 准备
				mPlayer.setOnErrorListener(mOnErrorListener);
				mPlayer.setOnCompletionListener(mCompletionListener);
				mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
				mPlayer.setDataSource(mLiveInfo.streamAddr);
				mPlayer.prepareAsync();
		} catch (Exception e) {
			LogWriter.e(TAG, "--reConnectMediaPlayer-- is exception ");
			mHandler.sendEmptyMessage(FLAG_READ_FRAME_UN_TIMEOUT);
		}
	}

	// private long reconnectTime = 0l;//重新连接时间

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
	 */
	private void conReConNetworkAndQiNiu() {
		//重练提示
		mHandler.sendEmptyMessage(FLAG_LIVE_AND_MEDIAO_RESET_MESSAGE);
		// 获取房间信息
		getLiveInfoByGetLive(new OnLiveInfoGetListener() {
			@Override
			public void onLiveInfoGet(boolean success, int errCode) {
				if (getActivity() == null) {
					return;
				}
				try {
					if (success) {
						LogWriter.e(TAG, "getLiveInfo---OnLiveInfoGetListener=-------true");
						// reconnectTime = 0l;
						if (mLiveInfo.canListen()) {
							// 重连播放器
							mHandler.sendEmptyMessage(FLAG_RECONNECT_MEDIA);
						} else {
							stopReConNetworkAndQiNiu();
							TipDialog td = new TipDialog(getContext());
							td.setContent(LiveAudienceFragment.this.getResources().getString(R.string.error_live_is_not_exit));
							td.setBtnCancelVisibity(View.GONE);
							td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {
								@Override
								public void onOkClick(Dialog dialog) {
									quitRoom();// 推出发房间
									if (getActivity() != null) {
										getActivity().finish();
									}
								}
								@Override
								public void onCancleClick(Dialog dialog) {
								}
							});
							td.show();
						}
					} else {
						// 重连
						mReConControllerHandler.sendEmptyMessageDelayed(TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG, iReconNetworkOff);
					}
				} catch (Exception e) {
					// 重连
					mReConControllerHandler.sendEmptyMessageDelayed(TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG, iRconNetworkOn);
				}

			}
		}, false);
	}

	private static final int TYPE_RECON_NETWORK_QINIU_CONTINUE_FLAG = 6001;// 继续重练
	private static final int TYPE_RECON_NETWORK_QINIU_STOP_FLAG = 6002;// 结束重练
	private int iReconNetworkOff = 5000;// 无网络情况，重练时间
	private int iRconNetworkOn = 5000;// 有网络情况，重练时间
	public Handler mReConControllerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int iTypeWtat = msg.what;
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

	@Override
	public String getCurrentFragmentName() {
		return "LiveAudienceFragment";
	}
}
