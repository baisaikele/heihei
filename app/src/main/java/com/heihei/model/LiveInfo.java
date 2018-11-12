package com.heihei.model;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.base.utils.StringUtils;
import com.base.utils.TimeUtil;

public class LiveInfo {
	public static final int TYPE_LIVE = 0;// 直播
	public static final int TYPE_REPLAY = 1;// 回放

	public static final int STATUS_BE_START = 0;// 准备开始
	public static final int STATUS_PLAYING = 1;// 正在直播
	public static final int STATUS_STOPPED = 2;// 已停止
	public static final int STATUS_BANND = 3;
	public static final int STATUS_DELETED = 4;// 已删除

	public User creator = new User();// 创建者
	public String liveId = "";// 直播id
	public String title = "";// 主题
	public String liveCity = "";// 城市
	public String pushAddr = "";// 推流地址
	public String streamAddr = "";// 直播地址
	public String shareAddr = "";// 分享地址
	public String lookbackAddr = "";// 回放地址
	public String messageAddr = "";// 房间消息打包地址
	public String roomId = "";// 房间号
	public int status = 0;// 房间状态
	public int totalUsers = 0;// 看过的人
	public int onlineUsers = 0;// 在线用户数
	public int viewernum = 0;// 观看人数
	public int livetotalticket = 0;// 本次直播获得多少黑票
	public String createTime = "";// 直播创建时间
	public long createTimeLong = 0l;// 直播创建时间

	public int type = TYPE_LIVE;// 直播还是回放

	public JSONObject qiniuJson;

	public LiveInfo() {

	}

	public LiveInfo(JSONObject json) {
		parseJSON(json);
	}

	public void parseJSON(JSONObject json) {
		if (json == null) {
			return;
		}
		User creator = new User();
		String creatorId = json.optString("userId");
		String creatorName = json.optString("userName");
		creator.uid = creatorId;
		creator.nickname = creatorName;
		creator.parseGender(json.optString("gender"));
		creator.birthday = json.optString("birthDay");

		this.creator = creator;
		this.liveId = json.optString("liveId", liveId);
		this.title = json.optString("title", title);
		this.liveCity = json.optString("liveCity");
		this.pushAddr = json.optString("pushAddr");
		this.streamAddr = json.optString("streamAddr", streamAddr);
		this.shareAddr = json.optString("shareAddr");
		this.lookbackAddr = json.optString("lookbackAddr", lookbackAddr);
		this.messageAddr = json.optString("messageAddr", messageAddr);
		this.roomId = json.optString("roomId", roomId);
		this.status = json.optInt("status");
		this.onlineUsers = json.optInt("onlineUsers");
		this.totalUsers = json.optInt("totalUsers");
		this.livetotalticket = json.optInt("livetotalticket");
		this.createTime = json.optString("createTime");

		if (!StringUtils.isEmpty(createTime)) {
			try {
				Date date = TimeUtil.dateFormat.parse(createTime);
				this.createTimeLong = date.getTime();
			} catch (Exception e) {
			}

		}

		if (json.has("qiniuObj")) {
			String qStr = json.optString("qiniuObj");
			try {
				this.qiniuJson = new JSONObject(qStr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 是否可听
	 * 
	 * @return
	 */
	public boolean canListen() {
		return status == STATUS_PLAYING;
	}

}
