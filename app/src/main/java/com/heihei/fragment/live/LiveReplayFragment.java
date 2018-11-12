package com.heihei.fragment.live;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.base.danmaku.DanmakuItem;
import com.base.host.BaseFragment;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.StringUtils;
import com.base.utils.TimeUtil;
import com.base.utils.UIUtils;
import com.base.widget.message.DanmakuViewFragment;
import com.base.widget.message.InformationPagerAdapter;
import com.base.widget.message.ScrollViewFragment;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.ShareDialog;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.live.LiveAudienceFragment.OnLiveInfoGetListener;
import com.heihei.fragment.live.logic.GiftAnimationController;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.logic.OfflineMsgController;
import com.heihei.fragment.live.widget.LiveHeader;
import com.heihei.logic.StatusController;
import com.heihei.logic.present.LivePresent;
import com.heihei.media.MediaPlayFactory;
import com.heihei.model.AudienceGift;
import com.heihei.model.Gift;
import com.heihei.model.LiveInfo;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.AbstractTextMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.SystemMessage;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PLMediaPlayer.OnBufferingUpdateListener;
import com.pili.pldroid.player.PLMediaPlayer.OnCompletionListener;
import com.pili.pldroid.player.PLMediaPlayer.OnErrorListener;
import com.pili.pldroid.player.PLMediaPlayer.OnInfoListener;
import com.pili.pldroid.player.PLMediaPlayer.OnPreparedListener;
import com.wmlives.heihei.R;

import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 直播的回放端
 * 
 * @author chengbo
 */
public class LiveReplayFragment extends BaseFragment implements OnClickListener {

	private LiveHeader mHeader;
	// private DanmakuView mDanmakuView;
	private ImageView btn_share;
	private ImageView btn_play;
	private TextView tv_time;
	private SeekBar mSeekBar;
	private FrameLayout giftContentView;
	private ImageButton btn_back;
	private PLMediaPlayer mPlayer;

	private LiveInfo mLiveInfo;
	private LivePresent mLivePresent = new LivePresent();

	private GiftAnimationController mGiftAnimationController;
	private OfflineMsgController mOfflineMsgController;

	private ViewPager mViewPager;

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_live_replay);
	}

	@Override
	protected void viewDidLoad() {

		mLiveInfo = (LiveInfo) mViewParam.data;
		if (mLiveInfo == null) {
			getActivity().finish();
		}

		mHeader = (LiveHeader) findViewById(R.id.header);
		mHeader.setType(LiveHeader.TYPE_REPLAY);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		mHeader.setReplayInfo(new PlayActivityInfo(true, getActivity()));
		btn_share = (ImageView) findViewById(R.id.btn_share);
		btn_play = (ImageView) findViewById(R.id.btn_play);
		tv_time = (TextView) findViewById(R.id.tv_time);
		mSeekBar = (SeekBar) findViewById(R.id.seekbar);
		giftContentView = (FrameLayout) findViewById(R.id.gift_contentView);
		btn_play.setEnabled(false);
		btn_play.setOnClickListener(this);
		btn_share.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);

		// mDanmakuView = (DanmakuView) findViewById(R.id.danmakuview);
		// mDanmakuView.setOnClickListener(this);

		mViewPager = (ViewPager) findViewById(R.id.messageviewpager);
		mDanmakuViewFragment = new DanmakuViewFragment();
		mScrollViewFragment = new ScrollViewFragment();
		List<Fragment> list = new ArrayList<Fragment>();
		list.add(mScrollViewFragment);
		list.add(mDanmakuViewFragment);
		InformationPagerAdapter adapter = new InformationPagerAdapter(getChildFragmentManager(), list);
		mViewPager.setAdapter(adapter);
		mViewPager.setCurrentItem(1);

//		if (LiveAudienceFragment.mWeakReference != null) {
//			LiveAudienceFragment.mWeakReference.get().pause();
//		}
	}

	private DanmakuViewFragment mDanmakuViewFragment;
	private ScrollViewFragment mScrollViewFragment;

	@Override
	protected void refresh() {

		if (mLiveInfo != null) {
			mHeader.setLiveTopic(mLiveInfo.title);
			mHeader.setUserInfo(mLiveInfo.creator);
		}

		getLiveInfo(new OnLiveInfoGetListener() {

			@Override
			public void onLiveInfoGet(boolean success, int errCode) {

				if (getActivity() == null) {
					return;
				}

				if (success) {
					if (!StringUtils.isEmpty(mLiveInfo.lookbackAddr))// 可以听
					{
						StatusController.getInstance().setCurrentStatus(StatusController.STATUS_REPLAY);
						Message msg = Message.obtain();
						msg.obj = LiveReplayFragment.this;
						msg.what = FLAG_START_PLAY;
						mHandler.sendMessage(msg);
					} else {
						if (getActivity() != null) {
							getActivity().finish();
						}
					}

					if (!StringUtils.isEmpty(mLiveInfo.messageAddr)) {
						mOfflineMsgController = new OfflineMsgController(mLiveInfo.messageAddr);
						mOfflineMsgController.download();
					}

				} else {
					if (getActivity() != null) {
						getActivity().finish();
					}
					UIUtils.showToast("网络异常");
				}

			}
		});

	}

	/**
	 * 获取直播信息
	 */
	private void getLiveInfo(final OnLiveInfoGetListener mListener) {
		mLivePresent.getLiveInfo(mLiveInfo.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					JSONObject liveJ = json.optJSONObject("live");
					JSONObject userJ = json.optJSONObject("userInfo");

					int totalTicket = 0;

					if (liveJ != null) {
						totalTicket = liveJ.optInt("totalTickets");
						mLiveInfo.parseJSON(liveJ);
					}

					if (userJ != null) {
						mLiveInfo.creator = new User(userJ);
						mLiveInfo.creator.allEarnPoint = totalTicket;
					}

					mHeader.setLiveTopic(mLiveInfo.title);
					mHeader.setUserInfo(mLiveInfo.creator);
					requestUserList();

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
	 * 获取房间观众
	 */
	private void requestUserList() {
		mHeader.refreshUserList(mLiveInfo.liveId);
	}

	@Override
	public void onPause() {
		SharedPreferencesUtil util = new SharedPreferencesUtil(getContext());
		util.setValueInt(REPLAY_URL_SHEEK, mSeekBar.getProgress());
		super.onPause();
	}

	/**
	 * 释放资源
	 */
	private void release() {

		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}

		if (mGiftAnimationController != null) {
			mGiftAnimationController.release();
		}

		if (mOfflineMsgController != null) {
			mOfflineMsgController.release();
		}
		if (StatusController.getInstance().getCurrentStatus() == StatusController.STATUS_REPLAY) {
			StatusController.getInstance().resetStatus();
		}
	}

	/**
	 * 刷新播放时间
	 */
	private void refreshTime() {
		int duration = (int) mPlayer.getDuration();
		int currentDuration = (int) mPlayer.getCurrentPosition();
		mSeekBar.setMax(duration);
		mSeekBar.setProgress(currentDuration);
		int time = duration - currentDuration;
		if (time < 0) {
			time = 0;
		}
		tv_time.setText(TimeUtil.formatPlayTime(time));

		mHandler.removeMessages(FLAG_REFRESH_TIME);
		if (mPlayer.isPlaying()) {
			Message msg = Message.obtain();
			msg.what = FLAG_REFRESH_TIME;
			msg.obj = LiveReplayFragment.this;
			mHandler.sendMessageDelayed(msg, 1000);
		}

		refreshPlayState();

	}

	/**
	 * 刷新播放状态
	 */
	private void refreshPlayState() {
		btn_play.setEnabled(true);
		if (mPlayer != null && mPlayer.isPlaying()) {
			btn_play.setImageResource(R.drawable.hh_playback_pause);
			if (sheek > 0) {
				mSeekBar.setProgress(sheek);
				mPlayer.seekTo(sheek);
				sheek = -1;
				sheek--;
			}
		} else {
			btn_play.setImageResource(R.drawable.hh_playback_play);
		}
	}

	/**
	 * 播放出现错误
	 */
	private void playError() {
		refreshPlayState();
	}

	/**
	 * 播放完成
	 */
	private void playCompleted() {
		refreshPlayState();
	}

	private static final int FLAG_START_PLAY = 0;
	private static final int FLAG_REFRESH_TIME = 1;// 刷新播放时间
	private static final int FLAG_REFRESH_PLAY_STATE = 2;// 刷新播放状态
	private static final int FLAG_PLAY_ERROR = 3;// 播放失败
	private static final int FLAG_PLAY_COMPLETE = 4;// 播放完了

	private static Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			LiveReplayFragment fragment = (LiveReplayFragment) msg.obj;
			if (fragment.getActivity() == null) {
				return;
			}
			switch (msg.what) {
			case FLAG_START_PLAY:
				fragment.startPlay();
				break;
			case FLAG_REFRESH_PLAY_STATE:
				fragment.refreshPlayState();
				break;
			case FLAG_REFRESH_TIME:
				fragment.refreshTime();
				break;
			case FLAG_PLAY_ERROR:
				fragment.playError();
				break;
			case FLAG_PLAY_COMPLETE:
				fragment.playCompleted();
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.danmakuview: {

		}
			break;
		case R.id.btn_play:
			if (mPlayer != null && mPlayer.isPlaying()) {
				mPlayer.pause();
				btn_play.setImageResource(R.drawable.hh_playback_play);
				mDanmakuViewFragment.setDanmakuPause();
				mHeader.pauseGifts();
				if (mGiftAnimationController != null) {
					mGiftAnimationController.pause();
				}
			} else {
				mPlayer.start();
				btn_play.setImageResource(R.drawable.hh_playback_pause);
				mDanmakuViewFragment.setDanmakuResume();
				mHeader.resumeGifts();
				if (mGiftAnimationController != null) {
					mGiftAnimationController.resume();
				}

				Message msg = Message.obtain();
				msg.what = FLAG_REFRESH_TIME;
				msg.obj = LiveReplayFragment.this;
				mHandler.sendMessage(msg);
			}
			break;
		case R.id.btn_share:
			ShareDialog sd = new ShareDialog(getContext(), mLiveInfo.liveId);
			sd.show();
			break;

		case R.id.btn_back:
			getActivity().finish();
			break;
		default:
			break;
		}

	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			currentMsgTime = 0;
			mDanmakuViewFragment.setDanmakuStop();
			mHeader.clearGifts();

			if (mGiftAnimationController != null) {
				mGiftAnimationController.release();
			}

			mPlayer.seekTo(seekBar.getProgress());
			mPlayer.start();

			Message msg = Message.obtain();
			msg.what = FLAG_REFRESH_TIME;
			msg.obj = LiveReplayFragment.this;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
			} else {
				getOfflineMessage();
			}

		}
	};

	private long currentMsgTime = 0l;

	/**
	 * 获取离线消息
	 */
	private void getOfflineMessage() {

		if (mOfflineMsgController != null && mPlayer != null && mPlayer.isPlaying()) {
			long duration = mPlayer.getCurrentPosition();
			long time = (duration + mLiveInfo.createTimeLong) / 1000l;
			if (time == currentMsgTime)// 防止重复获取消息
			{
				return;
			}
			List<AbstractMessage> msgs = mOfflineMsgController.getMessageByTimestamp(time);
			this.currentMsgTime = time;
			if (msgs != null && msgs.size() > 0) {
				for (int i = 0; i < msgs.size(); i++) {
					AbstractMessage msg = msgs.get(i);
					if (msg != null) {
						switch (msg.msgType) {
						case AbstractMessage.MESSAGE_TYPE_LIKE: {
							LiveMessage message = (LiveMessage) msg;
							DanmakuItem item = new DanmakuItem();
							item.type = DanmakuItem.TYPE_LIKE;
							item.userName = message.fromUserName;
							item.gender = message.gender;
							item.text = message.text;

							switch (item.type) {
							case 1:
								item.text = getString(R.string.like_one_tip);
								break;
							case 2:
								item.text = getString(R.string.like_two_tip);
								break;
							case 3:
								item.text = getString(R.string.like_three_tip);
								break;
							}
							mDanmakuViewFragment.putDanmakuItem(item);
							mScrollViewFragment.updateLiveMessage(message);
						}
							break;
						case AbstractMessage.MESSAGE_TYPE_TEXT: {
							AbstractTextMessage message = (AbstractTextMessage) msg;
							DanmakuItem item = new DanmakuItem();
							item.type = DanmakuItem.TYPE_NORMAL;
							item.userId = message.fromUserId;
							item.userName = message.fromUserName;
							item.gender = message.gender;
							item.text = message.text;
							mDanmakuViewFragment.putDanmakuItem(item);
							mScrollViewFragment.updateAbstractTextMessage(message);
						}
							break;
						case AbstractMessage.MESSAGE_TYPE_SYSTEM: {
							SystemMessage message = (SystemMessage) msg;
							DanmakuItem item = new DanmakuItem();
							item.type = DanmakuItem.TYPE_SYSTEM;
							item.text = message.text;
							mDanmakuViewFragment.putDanmakuItem(item);
							mScrollViewFragment.updateSystemMessage(message);
						}
							break;
						case AbstractMessage.MESSAGE_TYPE_BARRAGE: {
							BulletMessage message = (BulletMessage) msg;

							DanmakuItem item = new DanmakuItem();
							item.type = DanmakuItem.TYPE_COLOR_BG;
							item.userName = message.fromUserName;
							item.gender = message.gender;
							item.text = message.text;
							mDanmakuViewFragment.putDanmakuItem(item);
							mScrollViewFragment.updateBulletMessage(message);
						}
							break;
						case AbstractMessage.MESSAGE_TYPE_GIFT: {
							GiftMessage message = (GiftMessage) msg;

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
							aGift.time = System.currentTimeMillis();
							aGift.amount = message.amount;
							mHeader.addGift(aGift);

							if (gift.type != Gift.TYPE_NORMAL)// 带全屏动画的礼物
							{
								if (mGiftAnimationController == null) {
									mGiftAnimationController = new GiftAnimationController(getActivity(), giftContentView);
								}
								mGiftAnimationController.addAnimation(aGift);
							}
						}
							break;
						case AbstractMessage.MESSAGE_TYPE_ACTION:
							break;
						}
					}
				}
			}

		}
	}

	/**
	 * 开始播放
	 * 
	 * @param url
	 *            播放地址
	 */
	private void startPlay() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}

		mPlayer = MediaPlayFactory.createMediaPlayer(getContext(), false);
		mPlayer.setOnInfoListener(mOnInfoListener);
		mPlayer.setOnPreparedListener(mOnPreparedListener);
		mPlayer.setOnErrorListener(mOnErrorListener);
		mPlayer.setOnCompletionListener(mCompletionListener);
		mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);

		prepare(mLiveInfo.lookbackAddr);
	}

	private static final String REPLAY_URL_KEY = "replayUrl";
	private static final String REPLAY_URL_SHEEK = "replaysheek";
	private int sheek = 0;

	private void sp(String url) {
		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(getContext());
		String beforeUrl = sharedPreferencesUtil.get(REPLAY_URL_KEY, "");
		if (beforeUrl.equals(url)) {
			sheek = sharedPreferencesUtil.get(REPLAY_URL_SHEEK, 0);
		} else {
			sharedPreferencesUtil.setValueStr(REPLAY_URL_KEY, url);
		}
	}

	private void prepare(String url) {
		try {
			mPlayer.setDataSource(url);
			mPlayer.prepareAsync();
			sp(url);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private OnInfoListener mOnInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(PLMediaPlayer arg0, int arg1, int arg2) {
			return false;
		}
	};

	private OnPreparedListener mOnPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(PLMediaPlayer arg0) {
			mPlayer.start();
			Message playMsg = Message.obtain();
			playMsg.what = FLAG_REFRESH_PLAY_STATE;
			playMsg.obj = LiveReplayFragment.this;
			mHandler.sendMessage(playMsg);

			Message msg = Message.obtain();
			msg.what = FLAG_REFRESH_TIME;
			msg.obj = LiveReplayFragment.this;
			mHandler.sendMessage(msg);
		}
	};

	private OnErrorListener mOnErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(PLMediaPlayer mediaPlayer, int errorCode) {
			LogWriter.e("play", "Error happened, errorCode = " + errorCode);
			switch (errorCode) {
			case PLMediaPlayer.ERROR_CODE_INVALID_URI:
				UIUtils.showToast("Invalid URL !");
				break;
			case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
				UIUtils.showToast("404 resource not found !");
				break;
			case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
				UIUtils.showToast("Connection refused !");
				break;
			case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
				UIUtils.showToast("Connection timeout !");
				break;
			case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
				UIUtils.showToast("Empty playlist !");
				break;
			case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
				UIUtils.showToast("Stream disconnected !");
				break;
			case PLMediaPlayer.ERROR_CODE_IO_ERROR:
				UIUtils.showToast("Network IO Error !");
				break;
			case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
				UIUtils.showToast("Unauthorized Error !");
				break;
			case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
				UIUtils.showToast("Prepare timeout !");
				break;
			case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
				UIUtils.showToast("Read frame timeout !");
				break;
			case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
				break;
			default:
				UIUtils.showToast("unknown error !");
				break;
			}

			Message msg = Message.obtain();
			msg.what = FLAG_PLAY_ERROR;
			msg.obj = LiveReplayFragment.this;
			mHandler.sendMessage(msg);

			return true;
		}
	};

	private OnBufferingUpdateListener mOnBufferingUpdateListener = new OnBufferingUpdateListener() {

		@Override
		public void onBufferingUpdate(PLMediaPlayer arg0, int arg1) {

		}
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(PLMediaPlayer arg0) {
			Message msg = Message.obtain();
			msg.what = FLAG_PLAY_COMPLETE;
			msg.obj = LiveReplayFragment.this;
			mHandler.sendMessage(msg);
		}
	};

	public void onDestroy() {
		super.onDestroy();
		release();
	};

	@Override
	public void onStop() {
//		if (LiveAudienceFragment.mWeakReference != null) {
//			LiveAudienceFragment.mWeakReference.get().start();
//		}
		super.onStop();
	}

	@Override
	protected boolean canBack() {

		TipDialog td = new TipDialog(getContext());
		td.setContent("是否离开此房间?");
		td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

			@Override
			public void onOkClick(Dialog dialog) {
				getActivity().finish();

			}

			@Override
			public void onCancleClick(Dialog dialog) {

			}
		});
		td.show();

		return false;
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LiveReplayFragment";
	}

}
