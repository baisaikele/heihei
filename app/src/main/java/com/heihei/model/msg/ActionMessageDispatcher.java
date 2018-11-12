package com.heihei.model.msg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

import com.base.host.HostApplication;
import com.base.utils.LogWriter;
import com.heihei.model.msg.api.ActionMessageCallback;
import com.heihei.model.msg.bean.ActionMessage;

import android.app.Notification.Action;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class ActionMessageDispatcher {
	private static ActionMessageDispatcher mActionMessageDispatcher;
	private static final ConcurrentHashMap<String, ArrayList<ActionMessageCallback>> actionCallbacks = new ConcurrentHashMap();

	private static final BlockingDeque<ActionMessage> actionQueue = new LinkedBlockingDeque<>();

	private static Handler actionHandler = new Handler(HostApplication.getInstance().getMainLooper());

	public static ActionMessageDispatcher getInstance() {
		if (mActionMessageDispatcher == null) {
			synchronized (ActionMessageDispatcher.class) {
				if (mActionMessageDispatcher == null) {
					mActionMessageDispatcher = new ActionMessageDispatcher();
				}
			}
		}
		return mActionMessageDispatcher;
	}

	public void addActionToQuene(ActionMessage messages) {
		LogWriter.i("duemessage", "addActionToQuene");
		actionQueue.add(messages);
	}

	private ActionMessageDispatcher() {
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE, new ArrayList<ActionMessageCallback>());
		actionCallbacks.put(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY, new ArrayList<ActionMessageCallback>());
		actionHandler.postDelayed(actionRunnable, 150L);
	}

	public void putActionCallback(String type, ActionMessageCallback callback) {
		switch (type) {
		case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY:
			LogWriter.i("duemessage", " putActionCallback   ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY ");
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE:
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE).add(callback);
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY:
			LogWriter.i("duemessage", "actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE).add(callback);");
			actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY).add(callback);
			break;
		default:
			break;
		}
	}

	public void removeActionCallback(String type, ActionMessageCallback callback) {
		try {
			switch (type) {
			case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY:
				LogWriter.i("duemessage", " removeActionCallback   ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY ");
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE:
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE).remove(callback);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY:
				LogWriter.i("duemessage", "actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE).remove(callback)");
				actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY).remove(callback);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		actionQueue.clear();
		actionCallbacks.clear();
		actionHandler.removeCallbacks(actionRunnable);
	}

	private Runnable actionRunnable = new Runnable() {

		@Override
		public void run() {
			if (!actionQueue.isEmpty()) {
				updateCallbackUI(actionQueue.peek());
				actionQueue.poll();
			}
			actionHandler.postDelayed(this, 150L);
		}
	};

	private void updateCallbackUI(ActionMessage message) {
		try {
			LogWriter.i("duemessage", "updateCallbackUI");
			switch (message.actionType) {
			case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC:
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS:
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY:
				LogWriter.i("duemessage", " updateCallbackUI   ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY ");
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT:
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE:
				LogWriter.i("duemessage", "ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE");
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE:
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY:
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE:
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE))
					callback.callback(message);
				break;
			case ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY:
				LogWriter.i("duemessage", "ActionMessageCallback callback : actionCallbacks.get");
				LogWriter.i("duemessage", "msg " + message.toString());
				for (ActionMessageCallback callback : actionCallbacks.get(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY))
					callback.callback(message);
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
