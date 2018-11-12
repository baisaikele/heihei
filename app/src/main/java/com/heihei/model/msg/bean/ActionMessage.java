package com.heihei.model.msg.bean;

import java.util.Timer;
import java.util.TimerTask;

import com.heihei.model.LiveInfo;
import com.heihei.model.User;
import com.heihei.model.msg.due.DueMessageUtils;

import android.os.Handler;
import android.renderscript.Type;
import android.util.Log;

public class ActionMessage extends AbstractMessage {

	public static final String ACTION_MESSAGE_TYPE_STOP_LIVE = "stop-live";// 直播结束
	public static final String ACTION_MESSAGE_TYPE_JOIN_CHAT = "join-chat";// 加入对聊
	public static final String ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY = "due-chat-notify";// 约聊通知消息
	public static final String ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY = "cancel-due-chat";// 约聊通知消息
	public static final String ACTION_MESSAGE_TYPE_CHAT_USER_STATUS = "chat-user-status";// 对聊用户状态
	public static final String ACTION_MESSAGE_TYPE_CHAT_TOPIC = "chat-topic";// 约聊话题信息
	public static final String ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE = "live-change";// 修改直播标题
	public static final String ACTION_MESSAGE_TYPE_LIVE_SYSTEM_NOTIFY = "system-notify";// 系统通知
	public static final String ACTION_MESSAGE_TYPE_CHAT_MESSAGE = "message";// 约聊未处理的消息

	public String actionType;
	public String link;
	public String comment;
	public long roomId;
	public LiveInfo live;
	public String text;
	public String msgId;
	public User user;
	public int type; // type 0:第一次约聊 1:反向约聊
	public int expireTime;
	public long chatId;
	public int status;// 0:正在进入,1：正常，2：离开，3：断线，4：...

	public boolean dueTimeout = false;
	private Timer timer;
	private TimeOutTask mTimeoutTask;

	// 加入对聊
	public ActionMessage(String actionType, String comment, long chatId) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.chatId = chatId;
	}

	// 约聊通知消息
	public ActionMessage(String actionType, String comment, int expireTime, long chatId, int type, User user, String id) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.expireTime = expireTime;
		this.chatId = chatId;
		this.type = type;
		this.user = user;
		this.msgId = id;
		timer = new Timer();
	}

	// 服务器下拉消息
	public ActionMessage(String actionType, String comment, int expireTime, long chatId, int type, User user, String id, boolean isTimeout) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.expireTime = expireTime;
		this.chatId = chatId;
		this.type = type;
		this.user = user;
		this.msgId = id;
		this.dueTimeout = isTimeout;
		timer = new Timer();
	}

	// 对聊用户状态
	public ActionMessage(String actionType, String comment, long chatId, User user, int status) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.chatId = chatId;
		this.user = user;
		this.status = status;
	}

	// 约聊话题消息
	public ActionMessage(String actionType, String comment, String text, long roomId) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.text = text;
		this.roomId = roomId;
	}

	// 对方取消约聊话题消息
	public ActionMessage(String actionType, String comment, long chatId, String msgId, User u) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.chatId = chatId;
		this.msgId = msgId;
		this.user = u;
	}

	// 系统消息
	public ActionMessage(String actionType, String comment, String link, String text, String id, User u) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.link = link;
		this.text = text;
		this.msgId = id;
		this.user = u;
	}

	// 消息列表消息
	public ActionMessage(String actionType, User user, MessageData data, String link, String text, long timeStamp) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.user = user;
		this.text = text;
		this.link = link;
		this.timeStamp = timeStamp;
		this.messageData = data;
	}

	public MessageData messageData;
	public long timeStamp;

	
	@Override
	public String toString() {
		return "ActionMessage [comment=" + comment + ", user=" + user + ", messageData=" + messageData + "]";
	}

	// 直播结束
	public ActionMessage(String actionType, String comment, long roomId, LiveInfo live) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.comment = comment;
		this.roomId = roomId;
		this.live = live;
	}

	// 修改直播主题
	public ActionMessage(String actionType, LiveInfo live) {
		super(MESSAGE_TYPE_ACTION);
		this.actionType = actionType;
		this.live = live;
	}

	private Handler mHandler;

	public void setTimeoutTask(Handler handler) {
		if (mTimeoutTask != null)
			mTimeoutTask.cancel();

		mTimeoutTask = new TimeOutTask();
		this.mHandler = handler;
		timer.schedule(mTimeoutTask, 1, 950);
	}

	public class TimeOutTask extends TimerTask {
		@Override
		public void run() {
			expireTime--;
			if (expireTime <= 0) {
				Log.i("duemessage", "expireTime " + mSaveTimestamp);
				dueTimeout = true;
				mHandler.sendEmptyMessage(2);
				mTimeoutTask.cancel();
			}
		}
	}

	public static class MessageData {// 消息data数据
		public int count; // 统计17
		public boolean read;// 是否已读
		public String text;// 约你私聊17次
		public String type;// calling

		public MessageData(int count, boolean read, String text, String type) {
			this.count = count;
			this.read = read;
			this.text = text;
			this.type = type;
		}

		@Override
		public String toString() {
			return "MessageData [count=" + count + ", read=" + read + ", text=" + text + ", type=" + type + "]";
		}
		
		
	}
}
