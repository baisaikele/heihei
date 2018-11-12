package com.base.widget.toast;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

import com.base.host.ActivityManager;
import com.base.host.HostApplication;
import com.base.utils.ImageUtils;
import com.base.utils.ThreadManager;
import com.heihei.dialog.CallingUserDialog;
import com.heihei.dialog.UserDialog;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

public class ChatToastHelper implements Observer {

	private volatile static ChatToastHelper mIns;

	private WindowManager mWindowManger;
	private WindowManager.LayoutParams mWindowParams;

	private View contentView;

	private boolean visibity = false;

	private ChatToastHelper() {
		init();
	}

	public static ChatToastHelper getInstance() {
		if (mIns == null) {
			synchronized (ChatToastHelper.class) {
				if (mIns == null) {
					mIns = new ChatToastHelper();
				}
			}
		}
		return mIns;
	}

	private void init() {
		mWindowManger = (WindowManager) HostApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);

		mWindowParams = new WindowManager.LayoutParams();
		mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mWindowParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
		}
		mWindowParams.flags |= WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

		mWindowParams.alpha = 1.0f;
		mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
		mWindowParams.format = PixelFormat.TRANSLUCENT;

		String manufacturer = android.os.Build.MANUFACTURER;

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
			mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
		} else {
			if (manufacturer != null && manufacturer.equals("LENOVO")) {
				try {
					mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
				} catch (Exception e) {
					mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
					e.printStackTrace();
				}
			} else {
				mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
			}
		}

		mWindowParams.setTitle("ChatToastHelper");
		mWindowParams.windowAnimations = R.style.ToastAnimation;
		mWindowParams.packageName = HostApplication.getInstance().getPackageName();
	}

	/**
	 * 构造contentView
	 */
	private void createCallView() {
		if (contentView == null) {
			contentView = LayoutInflater.from(HostApplication.getInstance()).inflate(R.layout.layout_chat_tip, null);
			contentView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Activity activity = ActivityManager.getInstance().peek().getActivity();
					if (activity != null) {
						CallingUserDialog ud = new CallingUserDialog(activity, user, count, chatId);
						mHandler.removeMessages(FLAG_COUNT_DOWN);
						mHandler.removeMessages(FLAG_DISMISS);
						dismiss();
						ud.show();
					}
				}
			});
		}
	}

	private void createContentView() {
		contentView = LayoutInflater.from(HostApplication.getInstance()).inflate(R.layout.layout_success_chat_tip, null);
	}

	private String nickname = "";
	private int count = 0;
	private int sex = 0;
	private User user;
	private long chatId;

	public void showWaiting(int count, User u, long chatId) {
		this.count = count;
		this.nickname = u.nickname;
		this.sex = u.gender;
		this.chatId = chatId;
		this.user = u;
		DueMessageUtils.getInstance().addObserver(this);
		pmStatus = false;
		showInnerWaiting();
	}

	public void setChatStatus(boolean status) {
		this.pmStatus = status;
	}

	/**
	 * 显示正在连接某某
	 * 
	 * @param nickname
	 *            昵称
	 */
	protected void showInnerWaiting() {

		mHandler.removeMessages(FLAG_COUNT_DOWN);
		mHandler.removeMessages(FLAG_DISMISS);

		if (count <= 0) {// 如果count小于0，倒计时结束，显示连接失败
			// showConnectFailed();
			ThreadManager.getInstance().execute(new Runnable() {
				@Override
				public void run() {
					DueMessageUtils.getInstance().insertMessageList(true);
				}
			});
			dismiss();
			return;
		}

		if (!DueMessageUtils.isBackground()) {
			createCallView();
			if (sex == 1) {
				contentView.setBackgroundResource(R.color.hh_color_female);
			} else {
				contentView.setBackgroundResource(R.color.hh_color_male);
			}

			// setText("正在连接" + nickname + "..." + count-- + "s");
			setText(nickname + "向你发起约聊", count-- + "s");
			if (contentView.getParent() == null) {
				mWindowManger.addView(contentView, mWindowParams);
			}
		} else {
			count--;
			dismiss();
		}

		visibity = true;
		/**
		 * 开始显示倒计时，一秒倒计时一次
		 */
		Message msg = Message.obtain();
		msg.what = FLAG_COUNT_DOWN;
		msg.obj = this;
		mHandler.sendMessageDelayed(msg, 1000);

	}

	public boolean ChatToastIsshow() {
		return visibity;
	}

	/**
	 * 显示连接成功，一秒后自动消失
	 */
	public void showConnectSuccess() {
		mHandler.removeMessages(FLAG_COUNT_DOWN);
		mHandler.removeMessages(FLAG_DISMISS);

		ThreadManager.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				DueMessageUtils.getInstance().insertMessageList(false);
			}
		});
		createContentView();
		contentView.setBackgroundResource(R.color.hh_color_g);
		setText(HostApplication.getInstance().getString(R.string.user_connect_success));
		if (contentView.getParent() == null && mHandler != null && ChatToastHelper.this != null) {
			mWindowManger.addView(contentView, mWindowParams);
		}
		Message message = Message.obtain();
		message.what = FLAG_DISMISS;
		message.obj = this;
		mHandler.sendMessageDelayed(message, 1000);
		visibity = true;
	}

	/**
	 * 显示连接失败，一秒后自动消失
	 */
	public void showConnectFailed() {

		mHandler.removeMessages(FLAG_COUNT_DOWN);
		mHandler.removeMessages(FLAG_DISMISS);
		createContentView();
		contentView.setBackgroundResource(R.color.hh_color_g);
		setText(HostApplication.getInstance().getString(R.string.user_calling_end));
		if (contentView.getParent() == null && mHandler != null && ChatToastHelper.this != null) {
			mWindowManger.addView(contentView, mWindowParams);
		}
		ThreadManager.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				DueMessageUtils.getInstance().insertMessageList(false);
			}
		});
		Message message = Message.obtain();
		message.what = FLAG_DISMISS;
		message.obj = this;
		mHandler.sendMessageDelayed(message, 1000);
		visibity = true;
	}

	private void dismiss() {
		DueMessageUtils.getInstance().deleteObserver(this);
		if (contentView != null && contentView.getParent() != null) {
			mWindowManger.removeView(contentView);
		}
		visibity = false;
	}

	private void setText(String str, String expireTime) {
		TextView tv_tip = (TextView) contentView.findViewById(R.id.tv_tip);
		tv_tip.setText(str);
		TextView tv_time = (TextView) contentView.findViewById(R.id.tv_time);
		Drawable drawable = HostApplication.getInstance().getResources().getDrawable(R.drawable.hh_profile_call);
		drawable.setBounds(0, 0, ImageUtils.dip2px(10), ImageUtils.dip2px(10));
		tv_time.setCompoundDrawables(drawable, null, null, null);
		tv_time.setCompoundDrawablePadding(ImageUtils.dip2px(1));
		tv_time.setText(String.format(HostApplication.getInstance().getResources().getString(R.string.due_dialog_time), expireTime));
	}

	private void setText(String str) {
		TextView tv_tip = (TextView) contentView.findViewById(R.id.tv_tip);
		tv_tip.setText(str);
	}

	private static boolean pmStatus = false;
	private static final int FLAG_DISMISS = 0;// 消失
	private static final int FLAG_COUNT_DOWN = 1;// 倒计时
	private static final int FLAG_CONNECT_FAIL = 2;// 显示连接失败
	private static final int FLAG_CONNECT_SUCCESS = 3;// 显示连接成功
	private static Handler mHandler = new Handler(HostApplication.getInstance().getMainLooper()) {

		public void handleMessage(android.os.Message msg) {
			ChatToastHelper mHelper = (ChatToastHelper) msg.obj;
			if (msg.what == FLAG_DISMISS) {
				if (mHelper != null) {
					mHelper.dismiss();
					mHelper.contentView = null;
				}
			} else if (msg.what == FLAG_COUNT_DOWN) {
				mHelper.showInnerWaiting();
			}
		};
	};

	@Override
	public void update(Observable observable, Object data) {
		try {
			ObServerMessage obServerMessage = (ObServerMessage) data;
			switch (obServerMessage.type) {
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM:
				dismiss();
				pmStatus = true;
				showConnectSuccess();
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_CANCEL_DUE_CHAT_NOTIFY:
				dismiss();
				showConnectFailed();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
