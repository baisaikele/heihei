package com.base.utils;

import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v4.util.Pair;

import com.base.host.HostApplication;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class AccountManager {
	public static final String EXTRA_KEY_SHARE_ACCOUNT_INFO = "ShareAccountInfo";
	public static final String DEFAULT_PRODUCTION_SERVER = "ShareAcPRODUCTIONcountInfo";
	private static final String PREF_KEY_USER_INFO = "UserInfo";

	/**
	 * 保存用户登录信息
	 * @param userInfo
	 */
	public static void setAccountInfo(User userInfo) {
		SharedPreferencesUtil util = new SharedPreferencesUtil(HostApplication.getInstance());
		util.setValueStr(PREF_KEY_USER_INFO, userInfo.toJSONString());
	}

	/**
	 * 获取用户登录信息
	 * @param userInfo
	 */
	public static User getAccountInfo() {
		User userInfo = null;
		SharedPreferencesUtil util = new SharedPreferencesUtil(HostApplication.getInstance());
		String userJson = util.get(PREF_KEY_USER_INFO, "");
		if (StringUtils.isEmpty(userJson)) {
			try {
				userInfo = userInfo.jsonParseUserDetails(new JSONObject(userJson));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return userInfo;
	}
	/**
	 * 清理用户登录信息
	 * @param userInfo
	 */
	public static void clearAccountInfo(){
		SharedPreferencesUtil util=new SharedPreferencesUtil(HostApplication.getInstance());
		util.delete(PREF_KEY_USER_INFO);
	}

	/**
	 * Create a account and add to account manager with share account info.
	 */
	private static void addAccountIntoAM(String token, User info) {
		android.accounts.AccountManager am = android.accounts.AccountManager.get(HostApplication.getInstance());
		String amType = HostApplication.getInstance().getString(R.string.account_manager_type);

		if (token != null && info != null) {
			String accountName = DEFAULT_PRODUCTION_SERVER;

			if (info.nickname != null)
				accountName += info.nickname;
			else
				accountName += String.valueOf(info.uid);

			String shareAccountInfo = info.toJSONString();
			Bundle extra = new Bundle();
			extra.putString(EXTRA_KEY_SHARE_ACCOUNT_INFO, shareAccountInfo.toString());
			final Account account = new Account(accountName, amType);
			am.addAccountExplicitly(account, null, extra);
			am.setAuthToken(account, DEFAULT_PRODUCTION_SERVER, token);
		}
	}

	/**
	 * Find account and token for current server.
	 *
	 * @return
	 */
	private static Pair<Account, String> findAccountFromAM() {
		android.accounts.AccountManager am = android.accounts.AccountManager.get(HostApplication.getInstance());
		String amType = HostApplication.getInstance().getString(R.string.account_manager_type);
		final Account availableAccounts[] = am.getAccountsByType(amType);
		Account syncAccount = null;
		String syncToken = null;
		for (Account account : availableAccounts) {
			String token = am.peekAuthToken(account, DEFAULT_PRODUCTION_SERVER);
			if (token != null) {
				syncToken = token;
				syncAccount = account;
				break;
			}
		}
		return Pair.create(syncAccount, syncToken);
	}
}
