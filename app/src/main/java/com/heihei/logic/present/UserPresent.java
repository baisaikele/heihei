package com.heihei.logic.present;

import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.heihei.logic.UserMgr;
import com.heihei.model.User;

public class UserPresent extends BasePresent {

	/**
	 * 获取短信验证码
	 * 
	 * @param phone
	 *            手机号
	 * @param response
	 */
	public void getSMSCode(String phone, JSONResponse response) {
		String url = urls.get(GET_SMS_CODE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("phoneNo", phone);
		HttpUtil.postAsync(url, hp, response);
	}

	public String getServiceTerms() {
		return urls.get(ABOUTUS_SERVICE_TERMS_KEY);
	}
	
	public String getContactUs() {
		return urls.get(ABOUTUS_CONTACT_US_KEY);
	}
	
	public String getSocialPact() {
		return urls.get(ABOUTUS_SOCIAL_PACT_KEY);
	}

	/**
	 * 微信登录
	 * 
	 * @param token
	 * @param openid
	 * @param uuid
	 * @param response
	 */
	public void loginByWechat(String token, String openid, String unionId, JSONResponse response) {
		String url = urls.get(SIGNIN_BY_THIRD_KEY);
		HttpParam hp = new HttpParam();
		hp.put("accountSource", "Wechat");
		hp.put("accountToken", token);
		hp.put("openId", openid);
		hp.put("unionId", unionId);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 微博登录
	 * 
	 * @param token
	 * @param openid
	 * @param uuid
	 * @param response
	 */
	public void loginByWeibo(String token, String openid, String uuid, JSONResponse response) {
		String url = urls.get(SIGNIN_BY_THIRD_KEY);
		HttpParam hp = new HttpParam();
		hp.put("accountSource", "Weibo");
		hp.put("accountToken", token);
		hp.put("openId", openid);
		hp.put("uuid", uuid);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 手机验证码登录
	 * 
	 * @param phone
	 * @param smsCode
	 * @param uuid
	 * @param response
	 */
	public void loginByPhone(String phone, String smsCode, JSONResponse response) {
		String url = urls.get(SIGNIN_BY_PHONE_KEY);
		HttpParam hp = new HttpParam();
		hp.put("phoneNo", phone);
		hp.put("verifyCode", smsCode);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 更新用户信息
	 * 
	 * @param nickname
	 *            昵称
	 * @param sign
	 *            签名
	 * @param gender
	 *            性别
	 * @param birthday
	 *            出生日期
	 * @param address
	 *            定位地址
	 * @param response
	 */
	public void updateUserInfo(String nickname, String sign, int gender, String birthday, String address, String latlng, JSONResponse response) {
		String url = urls.get(UPDATE_USER_INFO_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("displayName", nickname);
		if (sign != null) {
			hp.put("description", sign);
		}

		if (gender == User.FEMALE) {
			hp.put("gender", "Female");
		} else {
			hp.put("gender", "Male");
		}

		hp.put("birthDay", birthday);
		if (address != null) {
		    address = "";
			hp.put("address", address);
			hp.put("latlng", latlng);
		}

		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 退出登录
	 * 
	 * @param token
	 */
	public void logout() {
		String url = urls.get(SIGNOUT_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		HttpUtil.postAsync(url, hp, null);
	}

	/**
	 * 获取用户信息
	 * 
	 * @param otherUid
	 *            要获取的uid
	 * @param selfUid
	 *            自己的uid，如果有值将会判断是否关注及拉黑
	 * @param response
	 */
	public void getUserInfo(String otherUid, String selfUid, JSONResponse response) {
		String url = urls.get(GET_USER_INFO_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("uid", otherUid);
		if (selfUid != null) {
			hp.put("cuid", selfUid);
		}
		HttpUtil.getAsync(url, hp, response);
	}

	/**
	 * 关注
	 * 
	 * @param uid
	 * @param response
	 */
	public void followUser(String uid, String liveId,JSONResponse response) {
		String url = urls.get(FOLLOW_USER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", uid);
		if (liveId != null)
        {
            hp.put("liveId", liveId);
        }
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 取消关注
	 * 
	 * @param uid
	 * @param response
	 */
	public void unfollowUser(String uid, JSONResponse response) {
		String url = urls.get(UNFOLLOW_USER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", uid);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 拉黑
	 * 
	 * @param uid
	 * @param response
	 */
	public void blockUser(String uid, JSONResponse response) {
		String url = urls.get(BLOCK_USER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", uid);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 取消拉黑
	 * 
	 * @param uid
	 * @param response
	 */
	public void unblockUser(String uid, JSONResponse response) {
		String url = urls.get(UNBLOCK_USER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("targetId", uid);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 举报用户
	 * 
	 * @param uid
	 * @param response
	 */
	public void reportUser(String uid, int reportType, JSONResponse response) {
		String url = urls.get(UNBLOCK_USER_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", uid);
		hp.put("reportType", reportType);
		HttpUtil.postAsync(url, hp, response);
	}

	/**
	 * 粉丝列表
	 * 
	 * @param otherUid
	 * @param selfUid
	 *            自己的uid，Optional; if have value, will check current user is
	 *            already follow the user in result
	 * @param offset
	 *            开始数
	 * @param limit
	 *            每页个数
	 * @param response
	 */
	public void getFansList(String otherUid, String selfUid, int offset, int limit, JSONResponse response, boolean needCache) {
		String url = urls.get(FANS_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", otherUid);
		hp.put("offset", offset);
		hp.put("limit", limit);
		if (selfUid != null) {
			hp.put("curUserId", selfUid);
		}
		HttpUtil.getAsync(url, hp, response, needCache);
	}

	/**
	 * 关注列表
	 * 
	 * @param otherUid
	 * @param selfUid
	 *            自己的uid，Optional; if have value, will check current user is
	 *            already follow the user in result
	 * @param offset
	 *            开始数
	 * @param limit
	 *            每页个数
	 * @param response
	 */
	public void getFollowList(String otherUid, String selfUid, int offset, int limit, JSONResponse response, boolean needCahce) {
		String url = urls.get(FOLLOW_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("userId", otherUid);
		hp.put("offset", offset);
		hp.put("limit", limit);
		if (selfUid != null) {
			hp.put("curUserId", selfUid);
		}
		HttpUtil.getAsync(url, hp, response, needCahce);
	}

	/**
	 * 黑名单列表
	 * 
	 * @param offset
	 * @param limit
	 * @param response
	 */
	public void getBlockList(int offset, int limit, JSONResponse response, boolean needCache) {
		String url = urls.get(BLOCK_LIST_KEY);
		HttpParam hp = new HttpParam();
		hp.put("token", UserMgr.getInstance().getToken());
		hp.put("offset", offset);
		hp.put("limit", limit);
		HttpUtil.getAsync(url, hp, response, needCache);
	}

	/**
	 * 上传设备信息，push用
	 * @param uuid
	 * @param apnsToken
	 */
	public void pushDeviceInfo(String uuid,String apnsToken,JSONResponse response)
	{
	    
	    String url = urls.get(PUSH_DEVICE_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("uuid", uuid);
        hp.put("apnsType", "getui");
        hp.put("apnsToken", apnsToken);
        hp.put("deveiceType", "android");
        HttpUtil.postAsync(url, hp, response);
	}
	
}
