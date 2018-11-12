package com.heihei.model;

import org.json.JSONObject;

public class UpdateInfo {
	public static final String SYSTEM_UPDATE_INFO_KEY_FORCE = "force";
	public static final String SYSTEM_UPDATE_INFO_KEY_URL = "url";
	public static final String SYSTEM_UPDATE_INFO_KEY_TITLE = "title";
	public static final String SYSTEM_UPDATE_INFO_KEY_TEXT = "text";
	public static final String SYSTEM_UPDATE_INFO_KEY_FILENAME = "fileName";
	public static final String SYSTEM_UPDATE_INFO_KEY_VERSIONCODE = "versionCode";
	public static final String SYSTEM_UPDATE_INFO_KEY_CHANNEL = "channel";
	
	public boolean force;
	public String url;
	public String title;
	public String text;
	public String fileName;
	public String versionCode;
	public String channel;

	public UpdateInfo(boolean force, String url, String title, String text, String fileName, String versionCode, String channel) {
		this.force = force;
		this.url = url;
		this.title = title;
		this.text = text;
		this.fileName = fileName;
		this.versionCode = versionCode;
		this.channel = channel;
	}

	public static UpdateInfo toUpdateInfo(JSONObject response) {
		JSONObject info = response.optJSONObject("info");
		UpdateInfo result = null;
		if (info != null) {
			result = new UpdateInfo(info.optBoolean(SYSTEM_UPDATE_INFO_KEY_FORCE), info.optString(SYSTEM_UPDATE_INFO_KEY_URL), info.optString(SYSTEM_UPDATE_INFO_KEY_TITLE),
					info.optString(SYSTEM_UPDATE_INFO_KEY_TEXT), info.optString(SYSTEM_UPDATE_INFO_KEY_FILENAME), info.optString(SYSTEM_UPDATE_INFO_KEY_VERSIONCODE),
					info.optString(SYSTEM_UPDATE_INFO_KEY_CHANNEL));
		}
		return result;
	}

	@Override
	public String toString() {
		return "UpdateInfo [force=" + force + ", url=" + url + ", title=" + title + ", text=" + text + ", fileName=" + fileName + ", versionCode=" + versionCode + ", channel="
				+ channel + "]";
	}
}
