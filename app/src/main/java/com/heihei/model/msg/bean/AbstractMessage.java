package com.heihei.model.msg.bean;

public class AbstractMessage {
	public long mSaveTimestamp;
	public final String msgType;
	public final static String MESSAGE_TYPE_GIFT = "gift"; // 礼物类型
	public final static String MESSAGE_TYPE_TEXT = "text"; // 消息类型
	public final static String MESSAGE_TYPE_BARRAGE = "barrage";// 弹幕类型
	public final static String MESSAGE_TYPE_LIKE = "like"; // 点赞消息
	public final static String MESSAGE_TYPE_SYSTEM = "system"; // 系统通知消息
	public final static String MESSAGE_TYPE_ACTION = "action"; // 动作消息

	AbstractMessage(String type) {
		this.msgType = type;
		this.mSaveTimestamp = System.currentTimeMillis();
	}
}
