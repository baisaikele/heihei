package com.base.danmaku;

public class DanmakuItem {

	public static final int TYPE_NORMAL = 0;// 普通的
	public static final int TYPE_COLOR_BG = 1;// 有背景颜色的
	public static final int TYPE_GIFT = 2;// 送礼消息
	public static final int TYPE_SYSTEM = 3;// 系统消息
	public static final int TYPE_LIKE = 4;// 点亮消息

	public int type = TYPE_NORMAL;
	public String url;
	public String text;
	public String userName;
	public String userId;
	public int gender;
	public String birthday;
	public int giftId = -1;

	public DanmakuItem() {
	}

	public DanmakuItem(String userName, String userId, String text, int type) {
		this.userName = userName;
		this.userId = userId;
		this.text = text;
		this.type = type;
	}

}
