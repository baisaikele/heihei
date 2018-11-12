package com.heihei.logic;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.StringUtils;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.heihei.websocket.WebSocketClient;

public class UserMgr {

	private static final String NAME = "LOGIN_USER";

	private static volatile UserMgr mMgr;

	private User mLoginUser;

	private UserMgr() {
		mLoginUser = new User();
	}

	public static UserMgr getInstance() {
		if (mMgr == null) {
			synchronized (UserMgr.class) {
				if (mMgr == null) {
					mMgr = new UserMgr();
				}
			}
		}
		return mMgr;
	}

	public User getLoginUser() {
		return mLoginUser;
	}

	public void setLoginUser(User user) {
		this.mLoginUser = user;
	}

	public void setUidAndToken(String uid, String token) {
		mLoginUser.uid = uid;
		mLoginUser.token = token;
	}

	public void loadLoginUser() {
		SharedPreferences sp = HostApplication.getInstance().getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
		String content = sp.getString(NAME, "");
		if (!TextUtils.isEmpty(content)) {
			try {
				JSONObject json = new JSONObject(content);
				mLoginUser.jsonParseUserDetails(json);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveLoginUser() {
		SharedPreferences sp = HostApplication.getInstance().getSharedPreferences(NAME, Context.MODE_MULTI_PROCESS);
		Editor editor = sp.edit();
		editor.putString(NAME, mLoginUser.toJSONString());
		editor.commit();
	}

	// public boolean isLogind = false;

	public boolean isLogined() {
		return !StringUtils.isEmpty(getUid()) && !StringUtils.isEmpty(getToken()) && !"0".equals(getUid());
	}

	public String getUid() {
		return mLoginUser.uid;
	}

	public String getToken() {
		return mLoginUser.token;
	}

	/**
	 * 注销
	 */
	public void unlogin() {
		mLoginUser = new User();
		saveLoginUser();
		EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGOUT, 0, 0, null);
		WebSocketClient.unLogin();
		PushReceiver.pushDeviceInfo = false;
		
	}

	/**
	 * 是否需要完善资料
	 */
	public boolean isNeedEditInfo() {
		if (StringUtils.isEmpty(mLoginUser.nickname) || StringUtils.isEmpty(mLoginUser.birthday) || mLoginUser.gender == User.UNSPECIFIED) {
			return true;
		}
		return false;
	}

	public void requestMineInfo(final OnUserInfoListener mListener) {

		UserPresent present = new UserPresent();
		present.getUserInfo(getUid(), null, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK && json != null) {
					JSONObject result = json.optJSONObject("result");
					if (result != null) {
						UserMgr.getInstance().getLoginUser().jsonParseUserDetails(result);
						UserMgr.getInstance().saveLoginUser();
						EventManager.ins().sendEvent(EventTag.ACCOUNT_UPDATE_INFO, 0, 0, null);
					}
				}

			}
		});

	}

	public static interface OnUserInfoListener {

		public void onInfoGet();
	}

}
