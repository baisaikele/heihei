package com.heihei.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	public static final int UNSPECIFIED = -1;// 未知
	public static final int MALE = 0;// 男
	public static final int FEMALE = 1;// 女

	/** 登陆的当前用户才有token */
	public String token = "";

	/** ID */
	public String uid = "";
	/** 名称 */
	public String nickname = "";
	/** 头像URL */
	public String avatar = "";
	/** 性别 */
	public int gender = User.UNSPECIFIED;
	/** 出生日期 */
	public String birthday = "";
	/** 城市 */
	public String address = "";

	/** 手机号 */
	public String mobile = "";
	/** 签名 */
	public String sign = "";
	/** 黑票数 */
	public int point = 0;
	public int allEarnPoint = 0;
	/** 钻石数 */
	public int goldCount = 0;
	/** 等级 */
	public int level = 1;
	/** 送出钻石数 */
	public int postCount = 0;
	/** 粉丝数 */
	public int fansCount = 0;
	/** 关注数 */
	public int followingCount = 0;
	/** 直播数 */
	public int liveCount = 0;
	/** 是否已关注 */
	public boolean isFollowed = false;
	/** 是否已拉黑 */
	public boolean isBlocked = false;
	/** 加入房间的时间 */
	public long joinTime = 0l;

	public User() {
		super();
	}

	public User(JSONObject json) {
		jsonParseUserDetails(json);
	}

	/**
	 * 个人详情 个人主页 完整的数据解析
	 */
	public User jsonParseUserDetails(JSONObject json) {
		if (json == null) {
			return this;
		}
		try {
			if (json.has("id")) {
				uid = json.optString("id");
			}

			if (json.has("token")) {
				token = json.optString("token");
			}

			nickname = json.optString("displayName", nickname);

			avatar = json.optString("coverUrl", avatar);
			sign = json.optString("description", sign);
			String genderStr = json.optString("gender");
			if ("Female".equals(genderStr)) {
				gender = User.FEMALE;
			} else if ("Unspecified".equals(genderStr)) {
				gender = User.UNSPECIFIED;
			} else {
				gender = User.MALE;
			}

			address = json.optString("address", address);
			birthday = json.optString("birthDay", birthday);
			mobile = json.optString("mobile", mobile);
			point = json.optInt("point", point);
			allEarnPoint = json.optInt("allEarnPoint", allEarnPoint);
			goldCount = json.optInt("goldCount", goldCount);
			level = json.optInt("level", level);
			postCount = json.optInt("postCount", postCount);
			fansCount = json.optInt("followerCount", fansCount);
			followingCount = json.optInt("followingCount", followingCount);
			liveCount = json.optInt("liveCount", liveCount);
			isFollowed = json.optBoolean("isFollowed", isFollowed);
			isBlocked = json.optBoolean("isBlocked", isBlocked);
			joinTime = json.optLong("joinTime");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}

	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", uid);
			json.put("token", token);
			json.put("displayName", nickname);
			json.put("coverUrl", avatar);
			json.put("mobile", mobile);
			json.put("birthDay", birthday);
			if (gender == User.FEMALE) {
				json.put("gender", "Female");
			} else {
				json.put("gender", "Male");
			}
			json.put("address", address);
			json.put("mobile", mobile);
			json.put("point", point);
			json.put("goldCount", goldCount);
			json.put("level", level);
			json.put("postCount", postCount);
			json.put("followerCount", fansCount);
			json.put("followingCount", followingCount);
			json.put("liveCount", liveCount);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}

	/**
	 * 解析性别
	 * 
	 * @param genderStr
	 */
	public void parseGender(String genderStr) {
		if ("Female".equals(genderStr)) {
			this.gender = User.FEMALE;
		} else if ("Unspecified".equals(genderStr)) {
			this.gender = User.UNSPECIFIED;
		} else {
			this.gender = User.MALE;
		}
	}

	public static String getGenderStr(int gender) {
		if (gender == User.FEMALE) {
			return "Female";
		} else if (gender == User.MALE) {
			return "Male";
		} else {
			return "Unspecified";
		}
	}

	public static int parseGenderFromStr(String genderStr) {
		if ("Female".equals(genderStr)) {
			return User.FEMALE;
		} else if ("Unspecified".equals(genderStr)) {
			return User.UNSPECIFIED;
		} else {
			return User.MALE;
		}
	}

	@Override
	public String toString() {
		return "User [token=" + token + ", uid=" + uid + ", nickname=" + nickname + ", avatar=" + avatar + ", gender=" + gender + ", birthday=" + birthday + ", address=" + address
				+ ", mobile=" + mobile + ", sign=" + sign + ", point=" + point + ", goldCount=" + goldCount + ", level=" + level + ", postCount=" + postCount + ", fansCount="
				+ fansCount + ", followingCount=" + followingCount + ", liveCount=" + liveCount + ", isFollowed=" + isFollowed + ", isBlocked=" + isBlocked + ", joinTime="
				+ joinTime + "]";
	}

}
