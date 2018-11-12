package com.heihei.scoket;

import org.json.JSONArray;
import org.json.JSONObject;

import com.base.utils.HistoryUtils;
import com.base.utils.LogWriter;
import com.base.utils.ThreadManager;
import com.heihei.logic.UserMgr;
import com.heihei.model.LiveInfo;
import com.heihei.model.User;
import com.heihei.model.msg.ActionMessageDispatcher;
import com.heihei.model.msg.MessageDispatcher;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.SystemMessage;
import com.heihei.model.msg.bean.TextMessage;

import android.util.Log;

public class MessageDistribute {
	private String TAG = "MessageDistribute";
	private static MessageDistribute mMessageDistribute;

	public static MessageDistribute getInstance() {
		if (mMessageDistribute == null) {
			synchronized (MessageDistribute.class) {
				if (mMessageDistribute == null) {
					mMessageDistribute = new MessageDistribute();
				}
			}
		}
		return mMessageDistribute;
	}

	private MessageDistribute() {
	}

	public void sendMessage(final String json) {
		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				try {
					LogWriter.i(TAG, "msg:" + json);
					JSONArray array = new JSONArray(json);
					for (int i = 0; i < array.length(); i++)
						paserJsonItem(array.getJSONObject(i));

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public AbstractMessage paserLikeMessage(JSONObject obj) {
		LogWriter.i(TAG, "paserLikeMessage");

		LiveMessage msg = new LiveMessage(obj.optString("fromUserId"), obj.optString("fromUserName"), User.parseGenderFromStr(obj.optString("gender")), obj.optString("roomId"), 0,
				obj.optString("text"));

		if (MessageDispatcher.getInstance().getRoomStatus())
			MessageDispatcher.getInstance().addLiveLikeToQuene(msg);

		return msg;

	}

	public AbstractMessage paserTextMessage(JSONObject obj) {

		TextMessage msg = new TextMessage(obj.optString("fromUserId"), obj.optString("fromUserName"), obj.optString("text"), User.parseGenderFromStr(obj.optString("gender")),
				obj.optString("coverUrl"), obj.optString("roomId"), obj.optString("roomId"), obj.optInt("giftId", -1));

		if (MessageDispatcher.getInstance().getRoomStatus())
			MessageDispatcher.getInstance().addTextToQuene(msg);

		return msg;
	}

	public AbstractMessage paserSystemMessage(JSONObject obj) {
		SystemMessage msg = new SystemMessage(obj.optString("text"));
		if (MessageDispatcher.getInstance().getRoomStatus())
			MessageDispatcher.getInstance().addTextToQuene(msg);

		return msg;

	}

	public AbstractMessage paserBarrageMessage(JSONObject obj) {

		BulletMessage msg = new BulletMessage(obj.optString("fromUserId"), obj.optString("fromUserName"), obj.optString("text"), User.parseGenderFromStr(obj.optString("gender")),
				obj.optString("coverUrl"), obj.optString("roomId"), obj.optString("roomId"), obj.optInt("totalTicket"));

		if (MessageDispatcher.getInstance().getRoomStatus())
			MessageDispatcher.getInstance().addTextToQuene(msg);

		return msg;
	}

	public AbstractMessage paserGiftMessage(JSONObject obj) {
		LogWriter.i(TAG, "paserGiftMessage");
		GiftMessage msg = new GiftMessage(obj.optString("fromUserId"), obj.optString("fromUserName"), User.parseGenderFromStr(obj.optString("gender")), obj.optString("roomId"),
				obj.optInt("giftId"), obj.optInt("amount"), obj.optString("roomId"), obj.optInt("totalTicket"), obj.optString("gift_uuid"));

		if (MessageDispatcher.getInstance().getRoomStatus()) {
			LogWriter.i(TAG, "addGiftToQuene");
			MessageDispatcher.getInstance().addGiftToQuene(msg);
		} else {
			LogWriter.i(TAG, "addGiftToQuene getRoomStatus false");
		}

		return msg;
	}

	private void paserStopLive(JSONObject obj) {
		switch (obj.optString("action")) {
		case ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE:
			ActionMessageDispatcher.getInstance().addActionToQuene(
					new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_STOP_LIVE, obj.optString("comment"), obj.optLong(""), paserLiveInfo(obj.optJSONObject("live"))));
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE: {
			LiveInfo mLive = new LiveInfo();
			mLive.liveId = obj.optString("liveId");
			mLive.title = obj.optString("title");
			ActionMessageDispatcher.getInstance().addActionToQuene(new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_LIVE_TOPIC_CHANGE, mLive));
		}
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY:
			try {
				LogWriter.i(TAG, "paserStopLive ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY ");
				User user = new User();
				user.gender = user.parseGenderFromStr(obj.optString("gender"));
				user.uid = obj.optString("fromUserId");
				user.nickname = obj.optString("fromUserName");
				user.birthday = obj.optString("birthDay");
				user.avatar = obj.optString("coverUrl");
				ActionMessageDispatcher.getInstance().addActionToQuene(new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_DUE_CHAT_NOTIFY, obj.optString("comment"),
						obj.optInt("expireTime"), obj.optLong("chatId"), obj.optInt("type"), user, obj.optString("msgId")));
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC:
			ActionMessageDispatcher.getInstance()
					.addActionToQuene(new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_TOPIC, obj.optString("comment"), obj.optString("text"), obj.optLong("chatId")));
			break;

		case ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY:
			try {
				Log.i(TAG, "paserStopLive ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY");
				User user = new User();
				user.gender = user.parseGenderFromStr(obj.optString("gender"));
				user.uid = obj.optString("fromUserId");
				user.nickname = obj.optString("fromUserName");
				user.birthday = obj.optString("birthDay");
				user.avatar = obj.optString("coverUrl");

				ActionMessageDispatcher.getInstance().addActionToQuene(
						new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_CANCEL_DUE_CHAT_NOTIFY, obj.optString("common"), obj.optLong("chatId"), obj.optString("msgId"), user));

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS:
			LogWriter.i(TAG, "paserStopLive ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS ");
			int chatStatus = 0;
			JSONArray array = obj.optJSONArray("user");
			User user = null;
			if (array != null && array.length() > 0) {
				for (int i = 0; i < array.length(); i++) {
					JSONObject itemObj = array.optJSONObject(i);
					int id = itemObj.optInt("id");
					if (!String.valueOf(itemObj.optInt("id")).equals(UserMgr.getInstance().getUid())) {
						chatStatus = itemObj.optInt("status");
						user = new User(itemObj);
					}
				}

				if (user != null) {
					ActionMessageDispatcher.getInstance().addActionToQuene(
							new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_USER_STATUS, obj.optString("comment"), obj.optLong("chatId"), user, chatStatus));

				}

			}

			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT:
			LogWriter.i(TAG, "join_message :" + obj.toString());
			ActionMessageDispatcher.getInstance().addActionToQuene(new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_JOIN_CHAT, obj.optString("comment"), obj.optLong("chatid")));
			break;
		case ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE:
			ActionMessage msg = HistoryUtils.paserDueMessage(obj);
			Log.i("ACTION_MESSAGE_TYPE_CHAT_MESSAGE", msg.toString());
			if (msg != null && msg.actionType != null)
				ActionMessageDispatcher.getInstance().addActionToQuene(msg);
		default:
			break;
		}

	}

	private LiveInfo paserLiveInfo(JSONObject o) {
		LiveInfo info = new LiveInfo();
		info.liveId = o.optString("liveId");
		info.title = o.optString("title");
		info.streamAddr = o.optString("streamAddr");
		info.pushAddr = o.optString("pushAddr");
		info.shareAddr = o.optString("shareAddr");
		info.lookbackAddr = o.optString("lookbackAddr");
		info.roomId = o.optString("userId");
		info.totalUsers = o.optInt("totalUsers");
		info.livetotalticket = o.optInt("totalTickets");
		info.onlineUsers = o.optInt("onlineUsers");
		info.creator.uid = o.optString("userId");
		return info;
	}

	public void paserJsonItem(JSONObject obj) {
		LogWriter.i(TAG, "paserJsonArray:" + obj.toString());

		try {
			String str = obj.optString("msg");

			LogWriter.i(TAG, "str: " + str != null ? str : "str");
			JSONObject ItemObj = new JSONObject(str);
			String msgType = ItemObj.optString("msgType");
			LogWriter.i(TAG, "msgType : " + msgType);
			LogWriter.i(TAG, "=======================================");
			switch (msgType) {
			case AbstractMessage.MESSAGE_TYPE_LIKE:
				LogWriter.i(TAG, "case type : " + AbstractMessage.MESSAGE_TYPE_LIKE);
				paserLikeMessage(ItemObj);
				break;
			case AbstractMessage.MESSAGE_TYPE_TEXT:
				LogWriter.i(TAG, "case type : " + AbstractMessage.MESSAGE_TYPE_TEXT);
				paserTextMessage(ItemObj);
				break;
			case AbstractMessage.MESSAGE_TYPE_SYSTEM:
				LogWriter.i(TAG, "case type : " + AbstractMessage.MESSAGE_TYPE_SYSTEM);
				paserSystemMessage(ItemObj);
				break;
			case AbstractMessage.MESSAGE_TYPE_BARRAGE:
				LogWriter.i(TAG, "case type : " + AbstractMessage.MESSAGE_TYPE_BARRAGE);
				paserBarrageMessage(ItemObj);
				break;
			case AbstractMessage.MESSAGE_TYPE_GIFT:
				LogWriter.i(TAG, "case type : " + AbstractMessage.MESSAGE_TYPE_GIFT);
				paserGiftMessage(ItemObj);
				break;
			case AbstractMessage.MESSAGE_TYPE_ACTION:
				LogWriter.i(TAG, "case type : " + AbstractMessage.MESSAGE_TYPE_ACTION);
				paserStopLive(ItemObj);
				break;

			default:
				LogWriter.i(TAG, "case type : not fond default");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			LogWriter.i(TAG, e.getMessage());
		}
	}

}
