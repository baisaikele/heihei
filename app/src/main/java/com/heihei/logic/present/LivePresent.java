package com.heihei.logic.present;

import org.json.JSONObject;

import com.base.host.AppLogic;
import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.heihei.logic.UserMgr;

import android.util.Log;

public class LivePresent extends BasePresent {

	/**
	 * 获取首页banner
	 * 
	 * @param response
	 * @param readCache
	 *            是否读取缓存数据
	 */
	public void getHomeBanner(JSONResponse response, boolean readCache) {
		String url = urls.get(HOME_BANNER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		HttpUtil.getAsync(url, hp, response, readCache);
	}

	public void getSystemUpdate(JSONResponse response, String versionCode) {
		String url = urls.get(SYSTEM_UPDATE_KEY);
		HttpParam param = new HttpParam();
		param.put("channel", AppLogic.CHANNEL);
		param.put("devicesType", "android");
		param.put("versionCode", AppLogic.VERSION);
		HttpUtil.getAsync(url, param, response);
	}

	/**
	 * 开始直播请求
	 */
	public void getStartLive(JSONResponse response, String liveId) {
		String url = urls.get(START_LIVE_KEY);
		Log.i("LiveAnchorFragment", url);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("liveId", liveId);
		HttpUtil.getAsync(url, param, response);
	}

	/**
	 * 直播推荐列表
	 * 
	 * @param response
	 * @param readCache
	 */
	public void getHomeRecommendList(JSONResponse response, boolean readCache) {
		String url = urls.get(HOME_RECOMMEND_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		HttpUtil.getAsync(url, hp, response, readCache);
	}

	/**
	 * 创建直播
	 * 
	 * @param title
	 * @param response
	 */
	public void createLive(String title, JSONResponse response) {
		String url = urls.get(CREATE_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("title", title);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 获取分享的文案
	 * 
	 * @param liveId
	 * @param response
	 */
	public void shareLive(String liveId, JSONResponse response) {
		String url = urls.get(SHARE_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 更新直播主题
	 * 
	 * @param title
	 * @param response
	 */
	public void updateLiveTitle(String liveId, String title, JSONResponse response) {
		String url = urls.get(UPDATE_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("title", title);
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 暂停直播
	 * 
	 * @param liveId
	 * @param response
	 */
	public void pauseLive(String liveId, JSONResponse response) {
		String url = urls.get(PAUSE_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 继续直播
	 * 
	 * @param liveId
	 * @param response
	 */
	public void resumeLive(String liveId, JSONResponse response) {
		String url = urls.get(CONTINUE_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 结束直播
	 * 
	 * @param liveId
	 * @param response
	 */
	public void stopLive(String liveId, JSONResponse response) {
		String url = urls.get(STOP_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 删除直播
	 * 
	 * @param liveId
	 * @param response
	 */
	public void deleteLive(String liveId, JSONResponse response) {
		String url = urls.get(DELETE_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 获取直播信息
	 * 
	 * @param liveId
	 * @param response
	 */
	public void getLiveInfo(String liveId, JSONResponse response) {
		String url = urls.get(GET_LIVE_INFO_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.getAsync(url, hp, response);
	}

	public void getQuitLive(String liveId, JSONResponse jsonResponse) {
		String url = urls.get(QUIT_LIVE_KEY);
		HttpParam param = new HttpParam();
		param.put("token", UserMgr.getInstance().getToken());
		param.put("liveId", liveId);
		HttpUtil.postAsync(url, param, jsonResponse);
	}

	/**
	 * 加入直播
	 * 
	 * @param liveId
	 * @param response
	 */
	public void joinLive(String liveId, JSONResponse response) {
		String url = urls.get(JOIN_LIVE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 心跳
	 * 
	 * @param liveId
	 * @param isCreator
	 *            是否是创建者
	 * @param response
	 */
	public void heartBeat(String liveId, boolean isCreator, JSONResponse response) {
		String url = urls.get(HEART_BEAT_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("isCreator", isCreator ? 1 : 0);
		HttpUtil.getAsync(url, hp, response);
	}

	/**
	 * 获取用户直播列表
	 * 
	 * @param uid
	 *            用户id
	 * @param offset
	 *            开始
	 * @param limit
	 *            每页个数
	 * @param response
	 */
	public void getLiveList(String uid, int offset, int limit, JSONResponse response, boolean needCache) {
		String url = urls.get(USER_LIVE_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", uid);
		hp.put("offset", offset);
		hp.put("limit", limit);
		HttpUtil.getAsync(url, hp, response, needCache);
	}

	/**
	 * 获取直播间观众
	 * 
	 * @param liveId
	 * @param lastTime
	 *            用户加入房间的时间
	 * @param isNewest
	 *            是否获取最新的
	 * @param response
	 */
	public void getLiveAudiences(String liveId, long lastTime, boolean isNewest, JSONResponse response) {
		String url = urls.get(LIVE_WATCHER_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("lastJoinTime", lastTime);
		hp.put("direction", isNewest ? 0 : 1);
		HttpUtil.getAsync(url, hp, response);
	}

	/**
	 * 获取回放历史观众
	 * 
	 * @param liveId
	 * @param response
	 */
	public void getLiveReplayAudiences(String liveId, JSONResponse response) {
		String url = urls.get(LIVE_REPLAY_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		HttpUtil.getAsync(url, hp, response);
	}

	/**
	 * 获取礼物列表
	 * 
	 * @param response
	 * @param needCache
	 */
	public void getGiftList(JSONResponse response, boolean needCache) {
		String url = urls.get(LIVE_GIFT_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		HttpUtil.getAsync(url, hp, response);
	}

	/**
	 * 直播送礼物
	 * 
	 * @param giftId
	 *            礼物id
	 * @param liveId
	 *            房间id
	 * @param count
	 *            连送数
	 * @param response
	 */
	public JSONObject buyGift(int giftId, String liveId, final int count, String gift_uuid) {
		String url = urls.get(LIVE_BUY_GIFT_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("giftId", giftId);
		hp.put("liveId", liveId);
		hp.put("amount", count);
		hp.put("gift_uuid", gift_uuid);
		return HttpUtil.getSync(url, hp);
	}

	/**
	 * 点亮
	 * 
	 * @param liveId
	 * @param type
	 *            0:a;1:b;3:c
	 */
	public void like(String liveId, int type, JSONResponse response) {
		String url = urls.get(SEND_LIVE_LIKE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("type", type);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 禁言
	 * 
	 * @param userId
	 * @param liveId
	 * @param response
	 */
	public void shutupUser(String userId, String liveId, JSONResponse response) {
		String url = urls.get(SHUTUP_USER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("userId", userId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 取消禁言
	 * 
	 * @param userId
	 * @param liveId
	 * @param response
	 */
	public void unShutupUser(String userId, String liveId, JSONResponse response) {
		String url = urls.get(SHUTUP_OPEN_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("userId", userId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 是否禁言
	 * 
	 * @param userId
	 * @param liveId
	 * @param response
	 */
	public void isShutup(String userId, String liveId, JSONResponse response) {
		String url = urls.get(IS_SHUTUP_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("liveId", liveId);
		hp.put("userId", userId);
		HttpUtil.getAsync(url, hp, response);
	}
}
