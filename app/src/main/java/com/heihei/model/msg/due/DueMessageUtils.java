package com.heihei.model.msg.due;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;

import org.json.JSONObject;

import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.HistoryUtils;
import com.base.utils.LogWriter;
import com.base.widget.toast.ChatToastHelper;
import com.heihei.logic.PushReceiver;
import com.heihei.logic.present.PmPresent;
import com.heihei.media.RingtoneController;
import com.heihei.model.msg.ActionMessageDispatcher;
import com.heihei.model.msg.api.ActionMessageCallback;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.ObServerMessage;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class DueMessageUtils extends Observable implements BellStatusApi {
	private static DueMessageUtils mDueMessageUtils;

	public static DueMessageUtils getInstance() {
		if (mDueMessageUtils == null) {
			synchronized (DueMessageUtils.class) {
				if (mDueMessageUtils == null) {
					mDueMessageUtils = new DueMessageUtils();
				}
			}
		}
		return mDueMessageUtils;
	}

	public ArrayList<Object> getMessageQuene() {
		return dueMsgList;
	}

	public void setMessageQuene(ArrayList<ActionMessage> list) {
		if (this.dueMsgList != null) {
			this.dueMsgList.clear();
			this.dueMsgList.addAll(list);
		}
	}

	private DueMessageUtils() {
		LogWriter.i("duemessage", " DueMessageUtils()");
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY, mActionMessageCallback);
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT, mJoneActionMessageCallback);
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS, mChatUserStatusCallback);
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE, mSystemCallback);
		ActionMessageDispatcher.getInstance().putActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY, mCancelDueMessageCallback);

	}

	public void onDestroy() {
		LogWriter.i("duemessage", " onDestroy()");
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY, mActionMessageCallback);
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT, mJoneActionMessageCallback);
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS, mChatUserStatusCallback);
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE, mSystemCallback);
		ActionMessageDispatcher.getInstance().removeActionCallback(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY, mCancelDueMessageCallback);
	}

	private static ActionMessageCallback mCancelDueMessageCallback = new ActionMessageCallback() {

		@Override
		public void callback(ActionMessage message) {
			LogWriter.i("duemessage", "mCancelDueMessageCallback  message " + message.toString());
			if (message.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY)) {
				ObServerMessage obServerMessage = new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_CANCEL_DUE_CHAT_NOTIFY, message, message.chatId);
				mDueMessageUtils.chatUserStatus(obServerMessage);
			}
		}
	};

	private static ActionMessageCallback mChatUserStatusCallback = new ActionMessageCallback() {

		@Override
		public void callback(ActionMessage message) {
			LogWriter.i("duemessage", " ActionMessageCallback  callback ()");
			if (mDueMessageUtils != null) {
				ObServerMessage obServerMessage = new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_CHAT_USER_STATUS, message, message.chatId);
				mDueMessageUtils.chatUserStatus(obServerMessage);
			}
		}
	};

	private static ActionMessageCallback mSystemCallback = new ActionMessageCallback() {

		@Override
		public void callback(ActionMessage message) {
			LogWriter.i("mSystemCallback", "" + message.toString());
			if (mDueMessageUtils != null && message.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE)) {
				messageList.add(0, message);
			}
		}
	};

	public void insertMessageList(boolean isTimeOut) {

		/*
		 * for (int j = 0; j < messageList.size(); j++) { ActionMessage
		 * newMessage = (ActionMessage) messageList.get(j);
		 * 
		 * if (isTimeOut) newMessage.messageData.read = true;
		 * 
		 * for (int i = 0; i < dueMsgList.size(); i++) { ActionMessage
		 * oldMessage = (ActionMessage) dueMsgList.get(i); if
		 * (oldMessage.user.uid.equals(newMessage.user.uid))
		 * dueMsgList.remove(i); } dueMsgList.add(0, newMessage); }
		 * 
		 * messageList.clear(); try { if (dueMsgList.size() > 30) { for (int i =
		 * 29; i < dueMsgList.size(); i++) { dueMsgList.remove(i); } } } catch
		 * (Exception e) { e.printStackTrace(); }
		 */

		HistoryUtils.getInstance().getAllMessage();

		if (isTimeOut) {
			mDueMessageUtils.bellMessageSum();
			mDueMessageUtils.notifyData();
		} else {
			mDueMessageUtils.hideMessage();
			mDueMessageUtils.notifyData();
		}
	}

	/*
	 * 关闭主播开播不加入messageList private static ActionMessageCallback
	 * mSystemNotifyCallback = new ActionMessageCallback() {
	 * 
	 * @Override public void callback(ActionMessage message) { if
	 * (message.actionType.equals(ActionMessage.
	 * ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY)) { dueMsgList.add(0, message);
	 * mDueMessageUtils.bellMessageSum(); } } };
	 */

	private static ArrayList<Object> dueMsgList = new ArrayList<Object>();
	private static ArrayList<Object> messageList = new ArrayList<Object>();
	private static ActionMessageCallback mActionMessageCallback = new ActionMessageCallback() {

		@Override
		public void callback(ActionMessage message) {
			LogWriter.i("duemessage", "callback");

			if (message.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY)) {
				mDueMessageUtils.bellStartVibration(message);
			}
		}
	};

	public void hideMessage() {
		if (mDueMessageUtils != null) {
			mDueMessageUtils.hideMessageCount();
		}
	}

	private ActionMessageCallback mJoneActionMessageCallback = new ActionMessageCallback() {

		@Override
		public void callback(ActionMessage message) {
			PmPresent.getInstance().getJoinChat(new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						try {
							if (mDueMessageUtils != null) {
								ObServerMessage message = new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM, json);
								mDueMessageUtils.joinMessage(message);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, message.chatId);
		}
	};

	// private static Timer mTimer = new Timer();
	//
	// private void messageTimeoutHandle(ActionMessage msg) {
	// msg.setTimeoutTask(mHandler);
	// }

	// private static final int MESSAGE_WHAT_STOP_VIBRATION = 1;
	// private static Handler mHandler = new Handler() {
	// public void handleMessage(android.os.Message msg) {
	// switch (msg.what) {
	// case MESSAGE_WHAT_STOP_VIBRATION:
	// mDueMessageUtils.bellStopVibration();
	// break;
	// default:
	// break;
	// }
	// };
	// };

	private boolean isStopRunBellAnim = false;

	public void setStopRunbellAnim(boolean b) {
		this.isStopRunBellAnim = b;
	}

	public void setStopPmStatus() {
		if (mDueMessageUtils != null)
			mDueMessageUtils.chatUserStatus(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_PM_STOP_DUE));
	}

	@Override
	public void bellStartVibration(ActionMessage msg) {
		super.setChanged();
		bellAnim = true;

		super.notifyObservers(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_NOTIFY_DATA));

		RingtoneController.playRingBellRingtone(HostApplication.getInstance());
		if (isBackground() && msg != null) {
			if (msg.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY)) {
				String url = "wmlives_heihei://duechat?chatId=" + msg.chatId + "&type=0&userId=" + msg.user.uid + "&userName=" + msg.user.nickname + "&gender=" + msg.user.gender
						+ "&msgId=" + msg.msgId;
				PushReceiver.setNotification("收到一个新的通知", msg.user.nickname + " 约你私聊", url);
				
				ChatToastHelper.getInstance().showWaiting(msg.expireTime, msg.user, msg.chatId);
			} else if (msg.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY)) {
				String url = msg.link + "&msgId=" + msg.msgId;
				PushReceiver.setNotification("收到一个新的通知", msg.user.nickname + msg.comment, url);
			}
		} else {
			if (msg.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY)) {
				ChatToastHelper.getInstance().showWaiting(msg.expireTime, msg.user, msg.chatId);
			}
		}
	}

	public static boolean isBackground() {
		ActivityManager activityManager = (ActivityManager) HostApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(HostApplication.getInstance().getPackageName())) {
				/*
				 * BACKGROUND=400 EMPTY=500 FOREGROUND=100 GONE=1000
				 * PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
				LogWriter.i("isBackground",
						"此appimportace =" + appProcess.importance + ",context.getClass().getName()=" + HostApplication.getInstance().getPackageName().getClass().getName());
				if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					LogWriter.i("isBackground", "处于后台" + appProcess.processName);
					return true;
				} else {
					LogWriter.i("isBackground", "处于前台" + appProcess.processName);
					return false;
				}
			}
		}
		return false;
	}

	private boolean bellAnim = false;

	public boolean getBellStatus() {
		return bellAnim;
	}

	@Override
	public void bellMessageSum() {
		super.setChanged();
		if (!isStopRunBellAnim)
			super.notifyObservers(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_MESSAGE_COUNT, dueMsgList.size()));
	}

	@Override
	public void notifyData() {
		super.setChanged();
		super.notifyObservers(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_NOTIFY_DATA));
	}

	@Override
	public void joinMessage(ObServerMessage msg) {
		super.setChanged();
		super.notifyObservers(msg);
		RingtoneController.playMatchSuccessRingtone(HostApplication.getInstance());
	}

	@Override
	public void chatUserStatus(ObServerMessage msg) {
		super.setChanged();
		super.notifyObservers(msg);
	}

	@Override
	public void hideMessageCount() {
		super.setChanged();
		super.notifyObservers(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_HIDE_MESSAGE_COUNT));
	}

	/**
	 * 网路连接成功
	 */
	public void networkStatusOK() {
		super.setChanged();
		super.notifyObservers(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_NETWORK_OK));
	}

	/**
	 * 网络链接断开
	 */
	public void networkStatusOFF() {
		super.setChanged();
		super.notifyObservers(new ObServerMessage(ObServerMessage.OB_SERVER_MESSAGE_TYPE_NETWORK_OFF));
	}
}
