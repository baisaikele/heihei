package com.heihei.model.msg.bean;

public class TextMessage extends AbstractTextMessage {

	public int giftId;

	public TextMessage(String userId, String userName, String text, int gender, String converUrl, String roomId, String liveId, int giftId) {
		super(userId, userName, text, MESSAGE_TYPE_TEXT, gender, converUrl, roomId, liveId);
		this.giftId = giftId;
	}

	public TextMessage(String userId, String userName, String text, int gender, int giftId) {
		super(userId, userName, text, MESSAGE_TYPE_TEXT, gender);
		this.giftId = giftId;
	}
}
