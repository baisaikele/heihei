package com.heihei.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.DeviceInfoUtils;
import com.base.utils.ImageUtils;
import com.base.utils.StringUtils;
import com.base.utils.ThreadManager;
import com.base.utils.UIUtils;
import com.base.widget.toast.ChatToastHelper;
import com.facebook.fresco.FrescoImageHelper;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.BaseDialog.OnActionSheetSelected;
import com.heihei.fragment.PMFragment;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.PmPresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

public class UserDialog extends Dialog implements android.view.View.OnClickListener, Observer {

	private User user;
	private boolean isCreator;// 是否是主播
	private String liveId = "";

	private PlayActivityInfo info = null;

	private int openType;

	public UserDialog(Context context, User user, String liveId, boolean isCreator, int type) {
		super(context, R.style.BaseDialog);
		this.user = user;
		this.mContext = context;
		this.isCreator = isCreator;
		this.liveId = liveId;
		this.openType = type;
		if (context instanceof Activity) {
			setOwnerActivity((Activity) context);
		}

		setCancelable(false);
		setCanceledOnTouchOutside(false);
		
	}

	public void setReplayInfo(PlayActivityInfo info) {
		this.info = info;
	}

	public static final int USERDIALOG_OPENTYPE_DEFAULE = 0;
	public static final int USERDIALOG_OPENTYPE_PMFRAGMENT = 1;
	public static final int USERDIALOG_OPENTYPE_ANCHOR = 2;// 主播端
	public static final int USERDIALOG_OPENTYPE_AUDIENCE = 3;// 观看端

	// ----------------R.layout.dialog_user-------------Start
	private TextView btn_manager;
	private ImageView btn_close;
	private AvatarImageView iv_avatar;
	private TextView tv_level;
	private TextView tv_nickname;
	private TextView tv_ticker;
	private TextView tv_sign;
	private RelativeLayout btn_followed;
	private TextView tv_follow_num;
	private RelativeLayout btn_fans;
	private TextView tv_fans_num;
	private RelativeLayout btn_live;
	private TextView tv_live_num;
	private RelativeLayout btn_send;
	private TextView tv_send_num;
	private RelativeLayout btn_follow;
	private TextView tv_follow;
	private RelativeLayout btn_private_message;
	private TextView tv_private_message;
	private RelativeLayout btn_black;
	private TextView tv_black;
	private LinearLayout ll_due_control;
	private LinearLayout ll_due_content;
	private ImageView btn_call_stop;
	private TextView tv_call_stop_time;
	private Context mContext;

	public void autoLoad_dialog_user() {
		btn_manager = (TextView) findViewById(R.id.btn_manager);
		btn_close = (ImageView) findViewById(R.id.btn_close);
		btn_call_stop = (ImageView) findViewById(R.id.btn_call_stop);
		iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
		tv_level = (TextView) findViewById(R.id.tv_level);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_ticker = (TextView) findViewById(R.id.tv_ticker);
		tv_sign = (TextView) findViewById(R.id.tv_sign);
		btn_followed = (RelativeLayout) findViewById(R.id.btn_followed);
		tv_follow_num = (TextView) findViewById(R.id.tv_follow_num);
		btn_fans = (RelativeLayout) findViewById(R.id.btn_fans);
		tv_fans_num = (TextView) findViewById(R.id.tv_fans_num);
		btn_live = (RelativeLayout) findViewById(R.id.btn_live);
		tv_live_num = (TextView) findViewById(R.id.tv_live_num);
		tv_call_stop_time = (TextView) findViewById(R.id.tv_call_stop_time);
		btn_send = (RelativeLayout) findViewById(R.id.btn_send);
		tv_send_num = (TextView) findViewById(R.id.tv_send_num);
		btn_follow = (RelativeLayout) findViewById(R.id.btn_follow);
		tv_follow = (TextView) findViewById(R.id.tv_follow);
		btn_private_message = (RelativeLayout) findViewById(R.id.btn_private_message);
		tv_private_message = (TextView) findViewById(R.id.tv_private_message);
		btn_black = (RelativeLayout) findViewById(R.id.btn_black);
		tv_black = (TextView) findViewById(R.id.tv_black);
		ll_due_control = (LinearLayout) findViewById(R.id.ll_due_control);
		ll_due_content = (LinearLayout) findViewById(R.id.ll_due_content);

		if (openType == USERDIALOG_OPENTYPE_PMFRAGMENT) {
			tv_private_message.setTextColor(getContext().getResources().getColor(R.color.hh_color_c));
			Drawable drawable = getContext().getResources().getDrawable(R.drawable.hh_profile_line_n);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
			tv_private_message.setCompoundDrawables(drawable, null, null, null);
		} else {
			tv_private_message.setTextColor(getContext().getResources().getColor(R.color.hh_color_a));
		}

	}

	// ----------------R.layout.dialog_user-------------End

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_user);
		autoLoad_dialog_user();
		btn_manager.setOnClickListener(this);
		btn_close.setOnClickListener(this);
		iv_avatar.setOnClickListener(this);
		btn_followed.setOnClickListener(this);
		btn_fans.setOnClickListener(this);
		btn_live.setOnClickListener(this);
		btn_send.setOnClickListener(this);
		btn_follow.setOnClickListener(this);
		btn_private_message.setOnClickListener(this);
		btn_black.setOnClickListener(this);
		btn_call_stop.setOnClickListener(this);
		DueMessageUtils.getInstance().addObserver(this);
		refresh();

		if (isCreator) {
			getShutupState();
			btn_manager.setText(R.string.user_manager);
			btn_private_message.setEnabled(false);
			tv_private_message.setTextColor(getContext().getResources().getColor(R.color.hh_color_b));
		} else {
			btn_manager.setText(R.string.user_report);
			btn_private_message.setEnabled(true);
			tv_private_message.setTextColor(getContext().getResources().getColor(R.color.hh_color_a));
		}

		requestInfo();
	}
	
	

	private boolean chatable = true;

	/**
	 * 设置是否可以聊天
	 * 
	 * @param chatable
	 */
	public void setChatable(boolean chatable) {
		this.chatable = chatable;
	}

	private UserPresent mUserPresent = new UserPresent();

	/**
	 * 获取禁言状态
	 */
	private void getShutupState() {
		mLivePresent.isShutup(user.uid, liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					isshutup = json.optInt("isshutup") == 1;
				} else {

				}

			}
		});
	}
	
	

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

		refreshFollowState();
		refreshBlockState();

	}

	/**
	 * 刷新关注状态
	 */
	private void refreshFollowState() {
		if (user.isFollowed)// 如果是已关注
		{
			tv_follow.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			tv_follow.setText(R.string.user_followed);
			tv_follow.setTextColor(getContext().getResources().getColor(R.color.hh_color_b));
		} else {
			tv_follow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hh_userlist_unfollow, 0, 0, 0);
			tv_follow.setText(R.string.user_follow);
			tv_follow.setTextColor(getContext().getResources().getColor(R.color.hh_color_a));
		}
	}

	/**
	 * 刷新拉黑状态
	 */
	public void refreshBlockState() {
		if (user.isBlocked)// 如果是已拉黑
		{
			tv_black.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
			tv_black.setText(R.string.user_blacked);
			tv_black.setTextColor(getContext().getResources().getColor(R.color.hh_color_b));
		} else {
			tv_black.setCompoundDrawablesWithIntrinsicBounds(R.drawable.hh_profile_unblock, 0, 0, 0);
			tv_black.setText(R.string.user_black);
			tv_black.setTextColor(getContext().getResources().getColor(R.color.hh_color_a));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_manager:// 管理
			if (isCreator) {
				showManageDialog();
			} else {
				showReportDialog();
			}

			break;
		case R.id.btn_close:// 关闭
			if (isDueIng || chatId != 0L) {
				cancelDueChat();
			} else {
				mineDismiss();
			}
			break;
		case R.id.iv_avatar:// 头像
			break;
		case R.id.btn_followed:// TA关注的
			NavigationController.gotoFollowListFragment(getContext(), user.uid, info);
			break;
		case R.id.btn_fans:// TA的粉丝
			NavigationController.gotoFansListFragment(getContext(), user.uid, info);
			break;
		case R.id.btn_live:// TA的直播

			if (user.isBlocked) {
				UIUtils.showToast("拉黑着呢，不给看~");
				return;
			}

			if (user.liveCount <= 0) {
				return;
			}

			NavigationController.gotoLivesListFragment(getContext(), user.uid, info);
			break;
		case R.id.btn_send:// TA送出的
			break;
		case R.id.btn_follow:// 关注ta
			followUser();
			break;
		case R.id.btn_private_message:// 约聊

			if (PMFragment.status == PMFragment.STATUS_LOAD_SUCCESS || PMFragment.status == PMFragment.STATUS_LOADING) {
				UIUtils.showToast("约聊中，不能太贪心~");
				return;
			} else if (openType == USERDIALOG_OPENTYPE_ANCHOR) {
				UIUtils.showToast(getContext().getString(R.string.lives_ing));
				return;
			} else if (openType == USERDIALOG_OPENTYPE_AUDIENCE) {

			}

			PmPresent.getInstance().getDueChat(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						isDueIng = true;
						expireTime = json.optInt("expireTime");
						chatId = json.optLong("chatId");
						ll_due_control.setVisibility(View.GONE);
						ll_due_content.setVisibility(View.VISIBLE);
						btn_manager.setVisibility(View.GONE);
						if (mExpireTask != null)
							mExpireTask.cancel();

						mExpireTask = new expireTimerTask();
						mTimer.schedule(mExpireTask, 0, 1000);

						btn_followed.setEnabled(false);
						btn_fans.setEnabled(false);
						btn_live.setEnabled(false);
						btn_send.setEnabled(false);

						ThreadManager.getInstance().execute(new Runnable() {

							@Override
							public void run() {
								DueMessageUtils.getInstance().insertMessageList(false);
							}
						});
					} else {
						UIUtils.showToast(msg);
					}
				}
			}, user.uid);
			break;
		case R.id.btn_black:// 拉黑

			if (!user.isBlocked) {
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
			} else {
				blockUser();
			}
			break;
		case R.id.btn_call_stop:
			cancelDueChat();
			break;
		}
	}

	private boolean isDueIng = false;

	private void cancelDueChat() {
		PmPresent.getInstance().getStopDueChat(new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				isDueIng = false;
				mineDismiss();
			}
		}, chatId);
	}

	/**
	 * 关注他
	 */
	private void followUser() {
		if (user.isFollowed) {
			mUserPresent.unfollowUser(user.uid, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						user.isFollowed = false;
						user.fansCount -= 1;
						refresh();
						EventManager.ins().sendEvent(EventTag.FOLLOW_CHANGED, 0, 0, null);
					} else {
						UIUtils.showToast(msg);
					}

				}
			});
		} else {

			String liveId = null;
			if (!isCreator) {
				liveId = this.liveId;
			}

			mUserPresent.followUser(user.uid, liveId, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						user.isFollowed = true;
						user.fansCount += 1;
						refresh();
						EventManager.ins().sendEvent(EventTag.FOLLOW_CHANGED, 1, 0, null);
					} else {
						UIUtils.showToast(msg);
					}

				}

			});
		}

	}

	private void blockUser() {
		if (user.isBlocked) {
			mUserPresent.unblockUser(user.uid, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						user.isBlocked = false;
						refreshBlockState();
					} else {
						UIUtils.showToast(msg);
					}

				}
			});
		} else {
			mUserPresent.blockUser(user.uid, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						user.isBlocked = true;
						if (info != null && info.isPmfragment && info.pmFragment != null) {
							info.pmFragment.close();
							info.isPmfragment = false;
							info.pmFragment = null;
						}
						refreshBlockState();
						mineDismiss();
					} else {
						UIUtils.showToast(msg);
					}
				}

			});
		}

	}

	private void reportUser(int reportType) {
		mUserPresent.reportUser(user.uid, reportType, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				UIUtils.showToast(R.string.user_report_tip);

			}
		});
	}

	private LivePresent mLivePresent = new LivePresent();

	private void shutupUser() {
		mLivePresent.shutupUser(user.uid, this.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					isshutup = true;
					UIUtils.showToast("已禁言");
				} else {
					UIUtils.showToast(msg);
				}

			}
		});
	}

	/**
	 * 取消禁言
	 */
	private void unshutupUser() {
		mLivePresent.unShutupUser(user.uid, this.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					isshutup = false;
					UIUtils.showToast("已取消禁言");
				} else {
					UIUtils.showToast(msg);
				}

			}
		});
	}

	/**
	 * 显示举报对话框
	 */
	private void showReportDialog() {
		final int TYPE_GENDER = 1;// 性别不符
		final int TYPE_AD = 2;// 广告欺诈
		final int TYPE_BITCH = 3;// 骚扰谩骂
		final int TYPE_SEXY = 4;// 淫秽色情
		List<Object[]> titles = new ArrayList<Object[]>();
		titles.add(new Object[] { getContext().getResources().getString(R.string.report_gender_error), TYPE_GENDER });
		titles.add(new Object[] { getContext().getResources().getString(R.string.report_ad), TYPE_AD });
		titles.add(new Object[] { getContext().getResources().getString(R.string.report_bitch), TYPE_BITCH });
		titles.add(new Object[] { getContext().getResources().getString(R.string.report_sexy), TYPE_SEXY });

		ActionSheet ac = new ActionSheet(getOwnerActivity(), titles, new OnActionSheetSelected() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case TYPE_GENDER:
				case TYPE_AD:
				case TYPE_BITCH:
				case TYPE_SEXY:
					reportUser(whichButton);
					break;
				}

			}
		});
		ac.show();
	}

	private boolean isshutup = false;

	private void showManageDialog() {
		final int TYPE_SHUTUP = 0;// 禁言
		final int TYPE_CANCEL_SHUT_UP = 1;// 取消禁言
		final int TYPE_REPORT = 2;// 举报
		List<Object[]> titles = new ArrayList<Object[]>();
		if (isshutup) {
			titles.add(new Object[] { getContext().getResources().getString(R.string.user_cancel_shutup), TYPE_CANCEL_SHUT_UP });
		} else {
			titles.add(new Object[] { getContext().getResources().getString(R.string.user_shutup), TYPE_SHUTUP });
		}

		titles.add(new Object[] { getContext().getResources().getString(R.string.user_report), TYPE_REPORT });

		final ActionSheet ac = new ActionSheet(getOwnerActivity(), titles, new OnActionSheetSelected() {

			@Override
			public void onClick(int whichButton) {
				switch (whichButton) {
				case TYPE_SHUTUP:
					shutupUser();
					break;
				case TYPE_CANCEL_SHUT_UP:
					unshutupUser();
					break;
				case TYPE_REPORT:
					showReportDialog();
					break;
				}

			}
		});
		ac.show();
	}

	private Timer mTimer = new Timer();
	private int expireTime = 20;
	private expireTimerTask mExpireTask;
	private long chatId = 0L;
	private static final int TIME_REFRESH_STATUS = 1;
	private static final int TIME_FAIL_STATUS = 2;
	private static final int TIME_SUCCESS_STATUS = 3;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case TIME_REFRESH_STATUS:
				tv_call_stop_time.setText(HostApplication.getInstance().getString(R.string.due_call_end));
				tv_call_stop_time.append(StringUtils.createTimeSpannable("（" + expireTime + "s）"));
				break;
			case TIME_FAIL_STATUS:
				isDueIng = false;
				btn_call_stop.setVisibility(View.GONE);
				tv_call_stop_time.setText(getContext().getString(R.string.user_connect_fail));
				mineDismiss();
				break;
			case TIME_SUCCESS_STATUS:
				btn_call_stop.setVisibility(View.GONE);
				tv_call_stop_time.setTextColor(getContext().getResources().getColor(R.color.hh_color_g));
				tv_call_stop_time.setText(getContext().getString(R.string.user_connect_success));
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						try {
							mineDismiss();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 1000);
				break;
			default:
				break;
			}
		};
	};

	private void mineDismiss() {
		try {
			dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	@Override
	public void update(Observable observable, Object data) {
		try {
			ObServerMessage obServerMessage = (ObServerMessage) data;
			if (obServerMessage.type.equals(ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM)) {
				if (mExpireTask != null) {
					mExpireTask.cancel();
				}
				mHandler.sendEmptyMessage(TIME_SUCCESS_STATUS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void onStop() {
		DueMessageUtils.getInstance().deleteObserver(this);
		super.onStop();
	}

}
