package com.heihei.logic.present;

import org.json.JSONObject;

import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.heihei.logic.UserMgr;

public class PmPresent extends BasePresent {

	private volatile static PmPresent mPmPresent;

	public static PmPresent getInstance() {
		if (mPmPresent == null) {
			synchronized (PmPresent.class) {
				if (mPmPresent == null) {
					mPmPresent = new PmPresent();
				}
			}
		}
		return mPmPresent;
	}

	public String getWebScoketUrl() {
		return urls.get(WEBSCOKET_KEY);
	}

	public String getPayTip() {
		return urls.get(PAY_TIPS_KEY);
	}

	public void getDeleteMsg(JSONResponse jsonResponse, String uid) {
		String url = urls.get(MESSAGE_DELETE_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("userId", uid);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getMsgList(JSONResponse jsonResponse) {
		String url = urls.get(MESSAGE_LIST_KEY);
		LogWriter.i("WebSocketClient123", "getHistoryMsg url : " + url);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		HttpUtil.getAsync(url, param, jsonResponse, false);
	}

	public void getStopDueChat(JSONResponse jsonResponse, long chatId) {
		String url = urls.get(CANCEL_DUE_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("chatId", chatId);
		HttpUtil.getAsync(url, param, jsonResponse);
	}

	public void getReadOneMsg(JSONResponse jsonResponse, String msgId) {
		String url = urls.get(MESSAGE_READ_ONE_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("msgId", msgId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getReadAllMsg(JSONResponse jsonResponse) {
		String url = urls.get(MESSAGE_READ_ALL_MSG_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getStopMatchChat(JSONResponse jsonResponse) {
		String url = urls.get(STOP_MATCH_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getMatchChat(JSONResponse jsonResponse) {
		String url = urls.get(MATCH_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getJoinChat(JSONResponse jsonResponse, long chatId) {
		String url = urls.get(JOIN_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("chatId", chatId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getChatHeartBeat(JSONResponse jsonResponse, long chatId) {
		String url = urls.get(CHAT_HEART_BEAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("chatId", chatId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getStopChat(JSONResponse jsonResponse, long chatId) {
		String url = urls.get(STOP_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("chatId", chatId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getChangeTopic(JSONResponse jsonResponse, long chatId) {
		String url = urls.get(CHANGE_CHAT_TOPIC_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("chatId", chatId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getDueChat(JSONResponse jsonResponse, String userId) {
		String url = urls.get(DUE_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("userId", userId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	public void getAcceptChat(JSONResponse jsonResponse, long chatId, String userId, int type, String msgId) {
		String url = urls.get(ACCEPT_CHAT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("userId", userId);
		param.put("chatId", chatId);
		param.put("type", type);
		if (msgId != null) {
			param.put("msgId", msgId);
		}
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	/**
	 * 聊天送礼物
	 * 
	 * @param chatId
	 * @param userId
	 * @param response
	 */
	public JSONObject buyGift(String chatId, String userId, int giftId, String gift_uuid, final int amount) {
		String url = urls.get(BUY_GIFT_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("userId", userId);
		param.put("chatId", chatId);
		param.put("giftId", giftId);
		param.put("gift_uuid", gift_uuid);
		param.put("amount", amount);
		return HttpUtil.getSync(url, param);
	}

	/**
	 * 获取IM系统信息
	 * 
	 * @param response
	 */
	public void getChatUser(JSONResponse response) {
		String url = urls.get(GET_CHAT_USER_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		HttpUtil.getAsync(url, param, response);
	}

}
