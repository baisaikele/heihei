package com.heihei.model.msg.bean;

import org.json.JSONArray;
import org.json.JSONObject;

import com.heihei.logic.UserMgr;
import com.heihei.model.User;

import android.R.bool;
import android.util.Log;

public class ObServerMessage {
	public static final String OB_SERVER_MESSAGE_TYPE_START_BELL_ANIM = "start_bell";
	public static final String OB_SERVER_MESSAGE_TYPE_STOP_BELL_ANIM = "stop_bell";
	public static final String OB_SERVER_MESSAGE_TYPE_MESSAGE_COUNT = "msg_count";
	public static final String OB_SERVER_MESSAGE_TYPE_NOTIFY_DATA = "notify_data";
	public static final String OB_SERVER_MESSAGE_TYPE_JOIN_ROOM = "join_room";
	public static final String OB_SERVER_MESSAGE_TYPE_CHAT_USER_STATUS = "chat_user_status";
	public static final String OB_SERVER_MESSAGE_TYPE_HIDE_MESSAGE_COUNT = "hide_msg_count";
	public static final String OB_SERVER_MESSAGE_TYPE_PM_STOP_DUE = "pmfragment_stop_due";
	public static final String OB_SERVER_MESSAGE_TYPE_NETWORK_OK = "network_status_ok";//网络已连接
	public static final String OB_SERVER_MESSAGE_TYPE_NETWORK_OFF = "network_status_off";//网络已断开
	public static final String OB_SERVER_MESSAGE_CANCEL_DUE_CHAT_NOTIFY = "cancel-due-chat";//网络已连接
	
	public String type;
	public int msgCount;
	public chat chatInfo;
	public qiniu qiniu;
	public long chatId;
	public ActionMessage chatUserStatusMessage;

	public ObServerMessage() {
	}

	public ObServerMessage(String type) {
		this.type = type;
	}

	public ObServerMessage(String type, int count) {
		this.msgCount = count;
		this.type = type;
	}

	public ObServerMessage(String type, ActionMessage actionMessage, long chatId) {
		this.type = type;
		this.chatId = chatId;
		this.chatUserStatusMessage = actionMessage;
	}

	public ObServerMessage(String type, JSONObject object) {
		try {
			this.type = type;
			User u = null;
			JSONObject chatObj = object.optJSONObject("chat");
			JSONObject qiniuObj = chatObj.optJSONObject("qiniu");
			if (qiniuObj != null) {
				qiniu = new qiniu(qiniuObj.optString("roomName"), qiniuObj.optString("roomToken"));
			}
			JSONArray array = chatObj.optJSONArray("users");
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject o = array.optJSONObject(i);
					String id = o.optString("id");
					if (!id.equals(UserMgr.getInstance().getUid())) {
						u = new User(o);
					}
				}
			}

			chat c = new chat(chatObj.optLong("chatId"), chatObj.optLong("roomId"), chatObj.optInt("status"), u, chatObj.optString("initTopic"));
			this.chatInfo = c;
			this.chatId = c.chatId;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class qiniu {
		public String roomName;
		public String roomToken;

		public qiniu(String roomName, String roomToken) {
			this.roomName = roomName;
			this.roomToken = roomToken;
		}
	}

	public class chat {
		public long chatId;
		public long roomId;
		public int chatStatus;
		public User user;
		public String initTopic;

		public chat(long chatId, long roomId, int chatStatus, User user, String topic) {
			this.chatId = chatId;
			this.roomId = roomId;
			this.chatStatus = chatStatus;
			this.user = user;
			this.initTopic = topic;
		}

		@Override
		public String toString() {
			return "chat [chatId=" + chatId + ", roomId=" + roomId + ", chatStatus=" + chatStatus + ", user=" + user.toString() + ", initTopic=" + initTopic + "]";
		}

	}

}
