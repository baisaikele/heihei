package com.heihei.logic.present;

import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.heihei.logic.UserMgr;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.TextMessage;
import com.wmlives.heihei.R.string;

import android.util.Log;

public class MessagePresent extends BasePresent {
	private static MessagePresent mMessagePresent;

	public static MessagePresent getInstance() {
		if (mMessagePresent == null) {
			synchronized (MessagePresent.class) {
				if (mMessagePresent == null) {
					mMessagePresent = new MessagePresent();
				}
			}
		}
		return mMessagePresent;
	}

	/**
	 * 发送聊天信息
	 * 
	 * @param msg
	 * 
	 * @param response
	 */
	public void postMessage(TextMessage msg, JSONResponse jsonResponse) {
		HttpParam hp = new HttpParam();
//		String url=urls.get(SEND_MESSAGE_KEY);
		String url="http://api.heihei-test.wmlives.com/api/msg/send-msg";
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("text", msg.text);
		hp.put("roomId", msg.roomId);
		hp.put("liveId", msg.liveId);
		HttpUtil.postAsync(url, hp, jsonResponse);
	}

	/**
	 * 发送飘屏信息
	 * 
	 * @param msg
	 * 
	 * @param response
	 */
	public void postBullet(BulletMessage msg, JSONResponse jsonResponse) {
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", msg.liveId);
		hp.put("message", msg.text);

		HttpUtil.postAsync(urls.get(SEND_BULLET_KEY), hp, jsonResponse);
	}

	/**
	 * 发送LIVE礼物信息
	 * 
	 * @param msg
	 * 
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

//	/**
//	 * 发送Like信息
//	 * 
//	 * @param msg
//	 * 
//	 * @param response
//	 */
//	public void postLiveLike(LiveMessage msg, JSONResponse jsonResponse) {
//		HttpParam hp = new HttpParam();
//		hp.put("token", UserMgr.getInstance().getToken());
//		hp.put("liveId", msg.liveId);
//		hp.put("type", msg.type);
//
//		HttpUtil.postAsync(urls.get(SEND_LIVE_LIKE_KEY), hp, jsonResponse);
//	}

}
