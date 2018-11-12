package com.heihei.logic.present;

import org.json.JSONObject;

import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.heihei.logic.UserMgr;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.TextMessage;

public class MsgPresent extends BasePresent {

	/**
	 * 发送聊天信息
	 * 
	 * @param msg
	 * @param response
	 */
	public JSONObject postMessage(String text, String roomId, String liveId) {
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("text", text);
		hp.put("roomId", roomId);
		hp.put("liveId", liveId);

		return HttpUtil.getSync(urls.get(SEND_MESSAGE_KEY), hp);
	}

	/**
	 * 发送飘屏信息
	 * 
	 * @param msg
	 * @param response
	 */
	public JSONObject postBullet(String text, String liveId) {
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("message", text);

		return HttpUtil.getSync(urls.get(SEND_BULLET_KEY), hp);
	}

	/**
	 * 发送LIVE礼物信息
	 * 
	 * @param msg
	 * @param response
	 */
	public void postGift(GiftMessage msg, JSONResponse jsonResponse) {
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("giftId", msg.giftId);
		hp.put("roomId", msg.liveId);
		hp.put("liveId", msg.amount);

		HttpUtil.postAsync(urls.get(SEND_LIVE_GIFT_KEY), hp, jsonResponse);
	}

	/**
	 * 发送Like信息
	 * 
	 * @param msg
	 * @param response
	 */
	public void postLiveLike(String liveId, int type, JSONResponse jsonResponse) {
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("type", type);

		HttpUtil.postAsync(urls.get(SEND_LIVE_LIKE_KEY), hp, jsonResponse);
	}
}
