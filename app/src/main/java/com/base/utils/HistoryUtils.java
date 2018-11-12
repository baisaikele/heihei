package com.base.utils;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.heihei.logic.present.PmPresent;
import com.heihei.model.User;
import com.heihei.model.msg.ActionMessageDispatcher;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.ActionMessage.MessageData;
import com.heihei.model.msg.due.DueMessageUtils;
import com.heihei.scoket.MessageDistribute;

import android.content.MutableContextWrapper;

public class HistoryUtils {
	private static ArrayList<ActionMessage> messageList = new ArrayList<ActionMessage>();

	private static HistoryUtils mHistoryUtils;

	public static HistoryUtils getInstance() {
		if (mHistoryUtils == null) {
			synchronized (HistoryUtils.class) {
				if (mHistoryUtils == null) {
					mHistoryUtils = new HistoryUtils();
				}
			}
		}
		return mHistoryUtils;
	}

	public void getAllMessage() {
		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				PmPresent.getInstance().getMsgList(new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							try {
								JSONArray unReadMsg = json.optJSONArray("messages");
								messageList.clear();
								paserMsgArray(unReadMsg);
								LogWriter.i("WebSocketClient123", "messageList " + messageList != null ? messageList.size() : "null");
								if (messageList != null && messageList.size() > 0) {
									DueMessageUtils.getInstance().setMessageQuene(messageList);
									messageList.clear();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				});
			}
		});
	}

	public void deleteMsg(JSONResponse response, String uid) {
		PmPresent.getInstance().getDeleteMsg(response, uid);
	}

	private void paserMsgArray(JSONArray a) {
		try {
			if (a != null && a.length() > 0) {
				for (int i = 0; i < a.length(); i++) {
					JSONObject o = a.optJSONObject(i);
					ActionMessage msg = paserDueMessage(o);
					if (msg != null) {
						messageList.add(msg);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ActionMessage paserDueMessage(JSONObject o) {

		User u = new User();
		u.uid = o.optString("fromUserId");
		u.nickname = o.optString("fromUserName");
		u.gender = User.parseGenderFromStr(o.optString("gender"));
		u.avatar = o.optString("coverUrl");
		u.birthday = o.optString("birthDay");
		JSONObject data = o.optJSONObject("data");
		if (data == null)
			return null;
		MessageData itemData = new MessageData(data.optInt("count"), data.optBoolean("read"), data.optString("text"), data.optString("type"));

		return new ActionMessage(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE, u, itemData, o.optString("link"), o.optString("text"), o.optLong("timeStamp"));
	}
}
