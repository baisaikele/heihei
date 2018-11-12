package com.heihei.fragment.live.widget;

import org.json.JSONObject;

import com.base.animator.AnimationDrawableUtil;
import com.base.host.ActivityManager;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.KeyBoardUtil;
import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.base.utils.ThreadManager;
import com.base.utils.UIUtils;
import com.base.widget.RippleBackground;
import com.base.widget.RoundedImageView;
import com.base.widget.SoftKeyBoardListener;
import com.base.widget.SoftKeyBoardListener.OnSoftKeyBoardChangeListener;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.GiftDialog;
import com.heihei.dialog.GiftDialog.OnGiftSendListener;
import com.heihei.dialog.LiveAnchorMessageDialog;
import com.heihei.dialog.LiveAudinceMessageDialog;
import com.heihei.dialog.MessageDialog;
import com.heihei.dialog.ShareDialog;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.live.LiveAnchorFragment;
import com.heihei.fragment.live.LiveAnchorFragment.CloseLiveAnchorCallback;
import com.heihei.fragment.live.LiveAudienceFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.MsgPresent;
import com.heihei.media.RingtoneController;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * 直播底部自定义view
 * 
 * @author admin
 *
 */
public class LiveBottom extends LinearLayout implements OnClickListener {

	// ----------------R.layout.layout_live_bottom-------------Start
	private LinearLayout ll_user_info;
	private ImageView iv_round;
	private RoundedImageView iv_avatar;
	private TextView tv_nickname;
	private TextView tv_ticker;
	private LinearLayout ll_buttons;
	private ImageView btn_comment;
	private ImageView btn_share;
	private RippleBackground mRippleBackground;
	private ImageView btn_gift;
	private ImageView btn_beautful;
	private View btn_beautful_layout;
	private RelativeLayout rl_comment_group;
	private DanmakuSwitch btn_danmaku_switch;
	private Button btn_send;
	private TextView tv_comment;
	private EditText et_comment;
	private RelativeLayout rl_bottom;
	private TextView tv_length_tip;
	private ImageView tv_home_bell_sum;
	private ImageView btn_close;

	public void autoLoad_layout_live_bottom() {
		tv_home_bell_sum = (ImageView) findViewById(R.id.tv_home_bell_sum);
		rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
		ll_user_info = (LinearLayout) findViewById(R.id.ll_user_info);
		iv_round = (ImageView) findViewById(R.id.iv_round);
		iv_round.setVisibility(View.GONE);
		mRippleBackground = (RippleBackground) findViewById(R.id.content);
		iv_avatar = (RoundedImageView) findViewById(R.id.iv_avatar);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_ticker = (TextView) findViewById(R.id.tv_ticker);
		ll_buttons = (LinearLayout) findViewById(R.id.ll_buttons);
		btn_comment = (ImageView) findViewById(R.id.btn_comment);
		btn_share = (ImageView) findViewById(R.id.btn_share);
		btn_gift = (ImageView) findViewById(R.id.btn_gift);
		btn_beautful = (ImageView) findViewById(R.id.btn_beautful);
		rl_comment_group = (RelativeLayout) findViewById(R.id.rl_comment_group);
		btn_danmaku_switch = (DanmakuSwitch) findViewById(R.id.btn_danmaku_switch);
		btn_send = (Button) findViewById(R.id.btn_send);
		tv_comment = (TextView) findViewById(R.id.tv_comment);
		et_comment = (EditText) findViewById(R.id.et_comment);
		btn_close = (ImageView) findViewById(R.id.btn_close);
		btn_beautful_layout = findViewById(R.id.btn_beautful_layout);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (getParent() != null && getParent().getParent() != null) {
			tv_length_tip = (TextView) ((ViewGroup) (getParent().getParent())).findViewById(R.id.tv_length_tip);
		}
	}

	// ----------------R.layout.layout_live_bottom-------------End

	public LiveBottom(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Activity mActivity;

	public void setActivity(Activity a) {
		this.mActivity = a;
	}

	public void runBellAnim() {
		// startBellAnim();
	}

	public void messageNotify(Object o) {
		LogWriter.i("liveAudience", " HomeFragment update");
		try {
			ObServerMessage ob = (ObServerMessage) o;
			switch (ob.type) {
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_START_BELL_ANIM:
				// startBellAnim();
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_STOP_BELL_ANIM:
				// stopBellAnim();
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_MESSAGE_COUNT:
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_home_bell_sum.setVisibility(View.VISIBLE);
					}
				});
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_HIDE_MESSAGE_COUNT:
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						tv_home_bell_sum.setVisibility(View.GONE);
					}
				});
				break;
			default:
				break;
			}

		} catch (Exception e) {
			LogWriter.i("liveAudience", "e : " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_layout_live_bottom();
		btn_comment.setOnClickListener(this);
		btn_gift.setOnClickListener(this);
		btn_share.setOnClickListener(this);
		btn_beautful.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_danmaku_switch.setOnClickListener(this);
		btn_close.setOnClickListener(this);

		btn_danmaku_switch.setSelected(false);
		btn_send.setEnabled(false);

		et_comment.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (btn_send.isEnabled()) {
					btn_send.performClick();
				}
				return false;
			}
		});

		et_comment.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = et_comment.getText().toString().trim();
				if (StringUtils.isEmpty(str)) {
					btn_send.setEnabled(false);
					tv_length_tip.setVisibility(View.GONE);
				} else {
					if (StringUtils.getLengthOfByteCode(str) > 60) {
						btn_send.setEnabled(false);
						tv_length_tip.setVisibility(View.VISIBLE);
					} else {
						tv_length_tip.setVisibility(View.GONE);
						btn_send.setEnabled(true);
					}

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		EventManager.ins().registListener(EventTag.BIG_GIFT_SHOW, mBigGiftListener);
		EventManager.ins().registListener(EventTag.BIG_GIFT_DISMISS, mBigGiftListener);

	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		EventManager.ins().removeListener(EventTag.BIG_GIFT_SHOW, mBigGiftListener);
		EventManager.ins().removeListener(EventTag.BIG_GIFT_DISMISS, mBigGiftListener);
	}

	private EventListener mBigGiftListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			switch (what) {
			case EventTag.BIG_GIFT_SHOW:
				rl_bottom.setBackgroundColor(getResources().getColor(R.color.hh_color_gift_bg));
				break;
			case EventTag.BIG_GIFT_DISMISS:
				rl_bottom.setBackgroundColor(getResources().getColor(R.color.full_transparent));
				break;
			}

		}
	};

	private String liveId = "";

	private int type = LiveHeader.TYPE_AUDIENCE;

	public void setType(String liveId, int type) {
		this.liveId = liveId;
		this.type = type;
		switch (type) {
		case LiveHeader.TYPE_AUDIENCE:
			ll_user_info.setVisibility(View.GONE);// 隐藏掉主播信息模块
			break;
		case LiveHeader.TYPE_ANCHOR:
			try {
				btn_beautful.setVisibility(View.GONE);
				btn_beautful_layout.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
			btn_gift.setVisibility(View.GONE);
			break;
		}

	}

	public void setisLiveCreateUser(boolean b) {
		isLiveCreateUser = b;
	}

	private boolean isLiveCreateUser = false;

	/**
	 * 设置用户信息
	 * 
	 * @param user
	 */
	public void setUserInfo(User user) {
		if (user == null)
			return;
		iv_avatar.setUser(user, mRippleBackground, true);
		tv_nickname.setText(user.nickname);
		tv_ticker.setText(getResources().getString(R.string.user_dialog_ticker, user.allEarnPoint));
	}

	private OnGiftSendListener mOnGiftSendListener;

	public void setOnGiftSendListener(OnGiftSendListener mOnGiftSendListener) {
		this.mOnGiftSendListener = mOnGiftSendListener;
	}

	/**
	 * 隐藏键盘
	 */
	public void hideKeyboard() {
		KeyBoardUtil.hideSoftKeyBoard((Activity) getContext());
	}

	public void atUser(String nickname) {
		if (StringUtils.isEmpty(nickname)) {
			return;
		}
		et_comment.setText("@" + nickname + " ");
		et_comment.setSelection(et_comment.getText().toString().length());
		btn_comment.performClick();
	}

	public void refreshVoice(boolean hasVoice) {
		if (hasVoice) {
			if (UserMgr.getInstance().getLoginUser().gender == User.FEMALE) {
				if (!(iv_round.getDrawable() instanceof AnimationDrawable)) {
					iv_round.setImageResource(R.drawable.voice_anim_down_female);
				}
				AnimationDrawable drawable = (AnimationDrawable) iv_round.getDrawable();
				if (!drawable.isRunning()) {
					drawable.start();
				}
			} else {
				if (!(iv_round.getDrawable() instanceof AnimationDrawable)) {
					iv_round.setImageResource(R.drawable.voice_anim_down_male);
				}
				AnimationDrawable drawable = (AnimationDrawable) iv_round.getDrawable();
				if (!drawable.isRunning()) {
					drawable.start();
				}
			}

		} else {
			if (UserMgr.getInstance().getLoginUser().gender == User.FEMALE) {
				iv_round.setImageResource(R.drawable.hh_live_speak_female_short_down);
			} else {
				iv_round.setImageResource(R.drawable.hh_live_speak_male_short_down);
			}
		}
	}

	private CloseLiveAnchorCallback mCallback;

	public void setCloseCallback(CloseLiveAnchorCallback callback) {
		this.mCallback = callback;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_comment:// 评论
			rl_comment_group.setVisibility(View.VISIBLE);
			ll_buttons.setVisibility(View.GONE);
			ll_user_info.setVisibility(View.GONE);
			KeyBoardUtil.showSoftKeyBoard((Activity) getContext(), et_comment);

			SoftKeyBoardListener.setListener((Activity) getContext(), new OnSoftKeyBoardChangeListener() {

				@Override
				public void keyBoardShow(int height) {

				}

				@Override
				public void keyBoardHide(int height) {
					et_comment.setText("");
					ll_buttons.setVisibility(View.VISIBLE);
					rl_comment_group.setVisibility(View.GONE);
					if (tv_length_tip != null) {
						tv_length_tip.setVisibility(View.GONE);
					}

					if (type == LiveHeader.TYPE_ANCHOR) {
						ll_user_info.setVisibility(View.VISIBLE);
					}

				}
			});

			break;
		case R.id.btn_gift:// 送礼物

			rl_bottom.setVisibility(View.INVISIBLE);

			if (mGiftDialog == null) {
				mGiftDialog = new GiftDialog(getContext(), this.liveId, "", GiftDialog.TYPE_LIVE);
			}

			mGiftDialog.setOnGiftSendListener(mOnGiftSendListener);
			mGiftDialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					rl_bottom.setVisibility(View.VISIBLE);
					mGiftDialog = null;
				}
			});
			mGiftDialog.show();
			break;
		case R.id.btn_share:// 分享

			ShareDialog sd = new ShareDialog(getContext(), liveId);
			sd.show();
			break;
		case R.id.btn_beautful:// 消息

			if (ActivityManager.getInstance().peek() instanceof LiveAnchorFragment) {
				LiveAnchorMessageDialog dialog = new LiveAnchorMessageDialog(mActivity, tv_home_bell_sum, liveId, mCallback);
				dialog.show();
			} else if (ActivityManager.getInstance().peek() instanceof LiveAudienceFragment) {
				LiveAudinceMessageDialog dialog = new LiveAudinceMessageDialog(mActivity, tv_home_bell_sum, liveId);
				dialog.show();
			} else {
				MessageDialog md = new MessageDialog(mActivity, tv_home_bell_sum);
				md.show();
			}
			DueMessageUtils.getInstance().hideMessage();
			break;
		case R.id.btn_send:// 发送
			String content = et_comment.getText().toString().trim();

			if (StringUtils.isEmpty(content)) {
				return;
			}

			if (StringUtils.getLengthOfByteCode(content) > 60) {
				UIUtils.showToast("评论字数过长");
				return;
			}

			if (content.equals(beforeMsgContent) && !btn_danmaku_switch.isSelected()) {
				UIUtils.showToast("不能发送相同的内容");
				return;
			}

			sendMessage(content, btn_danmaku_switch.isSelected());
			break;
		case R.id.btn_danmaku_switch:// 弹幕开关
			btn_danmaku_switch.setSelected(!btn_danmaku_switch.isSelected());
			if (btn_danmaku_switch.isSelected()) {
				et_comment.setHint(HostApplication.getInstance().getString(R.string.btn_danmaku_switch_checked_text));
			} else {
				et_comment.setHint(HostApplication.getInstance().getString(R.string.btn_danmaku_switch_checkun_text));
			}
			break;
		case R.id.btn_close: {
			if (mOnCloseClickListener != null) {
				mOnCloseClickListener.onCloseClick();
			}
		}
			break;
		}
	}

	private String beforeMsgContent = "";
	private GiftDialog mGiftDialog;

	/**
	 * 隐藏礼物dialog
	 */
	public void dismissGiftDialog() {
		if (mGiftDialog != null && mGiftDialog.isShowing()) {
			mGiftDialog.dismiss();
		}
	}

	private MsgPresent mMsgPresent = new MsgPresent();

	private void sendMessage(final String text, final boolean isDanmaku) {
		if (StringUtils.isEmpty(text)) {
			return;
		}
		if (!isDanmaku) {// 普通消息
			if (mListener != null) {
				mListener.onTextSend(text, false);
				et_comment.setText("");
				RingtoneController.playMessageRingtone(HostApplication.getInstance());
				beforeMsgContent = text;
			}

			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					int tryNum = 0;
					while (tryNum < 3) {
						tryNum++;
						JSONObject o = mMsgPresent.postMessage(text, liveId, liveId);
						if (o != null && (o.optInt("errorcode", -1) == 0)) {
							break;
						}
					}
				}
			});

		} else {
			if (mListener != null) {
				mListener.onTextSend(text, true);
				et_comment.setText("");
				RingtoneController.playMessageRingtone(HostApplication.getInstance());
			}

			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					int tryNum = 0;
					while (tryNum < 3) {
						tryNum++;
						JSONObject object = mMsgPresent.postBullet(text, liveId);
						if (object == null)
							continue;

						int code = object.optInt("errorcode", -1);
						if (code == ErrorCode.ERROR_OK) {
							int gold = object.optInt("gold");
							UserMgr.getInstance().getLoginUser().goldCount = gold;
							UserMgr.getInstance().saveLoginUser();
							break;
						} else if (code == ErrorCode.ERROR_MONEY_NOT_ENOUGH) {
							mActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									showExchargeDialog();
								}
							});
							break;
						}
					}
				}
			});
		}
	}

	/**
	 * 显示充值提示框
	 */
	private void showExchargeDialog() {
		TipDialog td = new TipDialog(getContext());
		td.setContent(getContext().getResources().getString(R.string.excharge_danmaku_tip));
		td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

			@Override
			public void onOkClick(Dialog dialog) {
				dialog.dismiss();
				NavigationController.gotoExchangeFragment(getContext());
			}

			@Override
			public void onCancleClick(Dialog dialog) {
				dialog.dismiss();

			}
		});
		td.show();
	}

	private OnTextSendListener mListener;

	public void setOnTextSendListener(OnTextSendListener mListener) {
		this.mListener = mListener;
	}

	public static interface OnTextSendListener {

		public void onTextSend(String text, boolean danmakuSwitch);
	}

	private AnimationDrawable mBellAnim;

	private void startBellAnim() {
		if (mBellAnim == null) {
			mBellAnim = AnimationDrawableUtil.createBellAnim(getContext());
		}
		btn_beautful.setImageDrawable(mBellAnim);
		mBellAnim.start();
	}

	private void stopBellAnim() {
		if (mBellAnim == null) {
			mBellAnim = AnimationDrawableUtil.createBellAnim(getContext());
		}
		if (mBellAnim.isRunning()) {
			mBellAnim.stop();
		}
		btn_beautful.setImageDrawable(mBellAnim.getFrame(0));
	}

	private OnCloseClickListener mOnCloseClickListener;

	public void setOnCloseClickListener(OnCloseClickListener mOnCloseClickListener) {
		this.mOnCloseClickListener = mOnCloseClickListener;
	}

	public static interface OnCloseClickListener {

		public void onCloseClick();
	}

}
