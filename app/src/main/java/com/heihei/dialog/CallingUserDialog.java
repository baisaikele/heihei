package com.heihei.dialog;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.DeviceInfoUtils;
import com.base.utils.StringUtils;
import com.base.utils.ThreadManager;
import com.base.utils.UIUtils;
import com.base.widget.toast.ChatToastHelper;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.PmPresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CallingUserDialog extends Dialog implements Observer, android.view.View.OnClickListener {

	private User user;

	private int expireTime = 20;
	private long chatId;
	private Context mContext;

	public CallingUserDialog(Context context, User user, int expireTime, long chatId) {
		super(context, R.style.BaseDialog);
		this.mContext = context;
		if (context instanceof Activity)
			setOwnerActivity((Activity) context);

		this.chatId = chatId;
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		DueMessageUtils.getInstance().addObserver(this);
		this.user = user;
		this.expireTime = expireTime;
	}

	public static final int USERDIALOG_OPENTYPE_DEFAULE = 0;
	public static final int USERDIALOG_OPENTYPE_PMFRAGMENT = 1;

	// ----------------R.layout.dialog_user-------------Start
	private TextView btn_manager;
	private AvatarImageView iv_avatar;
	private TextView tv_level;
	private TextView tv_nickname;
	private TextView tv_ticker;
	private TextView tv_sign;
	private TextView tv_follow_num;
	private TextView tv_fans_num;
	private TextView tv_live_num;
	private TextView tv_send_num;
	private TextView btn_call_time;
	private ImageView btn_call_stop;
	private ImageView btn_call_accept;
	private View calling_view;
	private View calling_cancel_due;

	public void autoLoad_dialog_user() {
		btn_manager = (TextView) findViewById(R.id.btn_manager);
		iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
		tv_level = (TextView) findViewById(R.id.tv_level);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_ticker = (TextView) findViewById(R.id.tv_ticker);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		tv_follow_num = (TextView) findViewById(R.id.tv_follow_num);
		tv_fans_num = (TextView) findViewById(R.id.tv_fans_num);
		tv_live_num = (TextView) findViewById(R.id.tv_live_num);
		tv_send_num = (TextView) findViewById(R.id.tv_send_num);
		btn_call_time = (TextView) findViewById(R.id.btn_call_time);
		btn_call_stop = (ImageView) findViewById(R.id.btn_call_stop);
		btn_call_accept = (ImageView) findViewById(R.id.btn_call_accept);
		calling_view = findViewById(R.id.calling_view);
		calling_cancel_due = findViewById(R.id.calling_cancel_due);
	}

	// ----------------R.layout.dialog_user-------------End

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calling_dialog_user);
		autoLoad_dialog_user();
		btn_call_stop.setOnClickListener(this);
		btn_call_accept.setOnClickListener(this);
		btn_manager.setOnClickListener(this);
		btn_manager.setText(R.string.user_black);
		btn_call_time.setText(getContext().getString(R.string.agree));
		btn_call_time.append(StringUtils.createTimeSpannable("（" + expireTime + "s）"));
		refresh();
		requestInfo();
		if (mExpireTask != null)
			mExpireTask.cancel();

		mExpireTask = new expireTimerTask();
		mTimer.schedule(mExpireTask, 0, 1000);
	}

	private UserPresent mUserPresent = new UserPresent();

	/**
	 * 请求用户信息
	 */
	private void requestInfo() {
		if (user != null) {
			mUserPresent.getUserInfo(user.uid, UserMgr.getInstance().getUid(), new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						JSONObject result = json.optJSONObject("result");
						if (result != null) {
							user.jsonParseUserDetails(result);
							refresh();
						}
					} else {
						UIUtils.showToast(msg);
					}

				}
			});
		}
	}

	private void refresh() {
		if (user.birthday != null)
			iv_avatar.setUser(user);
		tv_nickname.setText(user.nickname);
		tv_level.setText("LV." + user.level);
		tv_ticker.setText(getContext().getResources().getString(R.string.user_dialog_ticker, user.allEarnPoint));
		tv_sign.setText(user.sign);
		tv_follow_num.setText("" + user.followingCount);
		tv_fans_num.setText("" + user.fansCount);
		tv_live_num.setText("" + user.liveCount);
		tv_send_num.setText("" + user.postCount);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_manager:// 管理
			final TipDialog tipDialog = new TipDialog(mContext);
			tipDialog.setContent(mContext.getString(R.string.user_black_confirm));
			tipDialog.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

				@Override
				public void onOkClick(Dialog dialog) {
					blockUser();
					tipDialog.dismiss();
				}

				@Override
				public void onCancleClick(Dialog dialog) {
					tipDialog.dismiss();
				}
			});
			tipDialog.show();
			break;
		case R.id.btn_call_stop:
			DueMessageUtils.getInstance().insertMessageList(false);
			cancelDueChat();
			mineDismiss();
			break;
		case R.id.btn_call_accept:

			ThreadManager.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					DueMessageUtils.getInstance().insertMessageList(false);
				}
			});

			PmPresent.getInstance().getAcceptChat(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						ChatToastHelper.getInstance().showConnectSuccess();
						mineDismiss();
					} else if (errCode == 738 || errCode == 409) {
						UIUtils.showToast(msg);
						mineDismiss();
					}
				}
			}, chatId, user.uid, 0, null);
			break;
		}
	}

	private void mineDismiss() {
		try {
			dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void cancelDueChat() {
		PmPresent.getInstance().getStopDueChat(new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

			}
		}, chatId);
	}

	private class expireTimerTask extends TimerTask {

		@Override
		public void run() {
			expireTime--;
			if (expireTime >= 0) {
				mHandler.sendEmptyMessage(TIME_REFRESH_STATUS);
			} else {
				mHandler.sendEmptyMessage(TIME_FAIL_STATUS);
				this.cancel();
			}
		}
	}

	private Timer mTimer = new Timer();
	private expireTimerTask mExpireTask;
	private static final int TIME_REFRESH_STATUS = 1;
	private static final int TIME_FAIL_STATUS = 2;
	private static final int TIME_SUCCESS_STATUS = 3;
	private static final int TIME_CANCEL_DUE_STATUS = 4;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_REFRESH_STATUS:
				btn_call_time.setText(HostApplication.getInstance().getString(R.string.agree));
				btn_call_time.append(StringUtils.createTimeSpannable("（" + expireTime + "s）"));
				break;
			case TIME_FAIL_STATUS:
				mineDismiss();
				break;
			case TIME_SUCCESS_STATUS:
				try {
					TimeUnit.SECONDS.sleep(1);
					mineDismiss();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case TIME_CANCEL_DUE_STATUS:
				calling_view.setVisibility(View.GONE);
				calling_cancel_due.setVisibility(View.VISIBLE);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mineDismiss();
					}
				}, 1000);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void update(Observable observable, Object data) {
		try {
			ObServerMessage obServerMessage = (ObServerMessage) data;
			if (obServerMessage.type.equals(ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM)) {
				mHandler.sendEmptyMessage(TIME_SUCCESS_STATUS);
			} else if (obServerMessage.type.equals(ObServerMessage.OB_SERVER_MESSAGE_CANCEL_DUE_CHAT_NOTIFY)) {
				mHandler.sendEmptyMessage(TIME_CANCEL_DUE_STATUS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void blockUser() {

		mUserPresent.blockUser(user.uid, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					mineDismiss();
					user.isBlocked = true;
				} else {
					UIUtils.showToast(msg);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		DueMessageUtils.getInstance().deleteObserver(this);
		super.onStop();
	}

	private LivePresent mLivePresent = new LivePresent();

}
