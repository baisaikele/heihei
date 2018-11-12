package com.heihei.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class Gift {

	public static final int TYPE_NORMAL = 0;// 普通礼物，没有动画
	public static final int TYPE_RAIN_FINGER = 1;// 拇指雨
	public static final int TYPE_RAIN_BIANBIAN = 2;// 便便雨
	public static final int TYPE_RAIN_BUBBLE = 3;// 气球雨
	public static final int TYPE_FULLSCREEN_DIAMOND = 4;// 钻石动画
	public static final int TYPE_FULLSCREEN_BAG = 5;// 包包动画

	public int id = 0;
	public String name = "";
	public int gold = 0;
	public int exp = 0;
	public JSONArray cl = new JSONArray();
	public String image = "";// 这个是礼物动画的图片
	public String icon = "";// 这个是礼物在列表的图片
	public int type = TYPE_NORMAL;

	public String gift_uuid = "";

	public boolean isSelected = false;

	public Gift() {
	}

	public Gift(JSONObject json) {
		parseJson(json);
	}

	private void parseJson(JSONObject json) {
		if (json == null) {
			return;
		}
		this.id = json.optInt("id");
		this.name = json.optString("name");
		this.gold = json.optInt("gold");
		this.exp = json.optInt("exp");
		this.cl = json.optJSONArray("cl");
		if (this.cl == null) {
			this.cl = new JSONArray();
			this.cl.put(255);
			this.cl.put(255);
			this.cl.put(255);
		}

		this.image = json.optString("image");
		this.icon = json.optString("icon");
		this.type = json.optInt("type");

	}

}
