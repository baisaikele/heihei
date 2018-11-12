package com.heihei.model.msg.bean;

import com.heihei.model.User;

abstract class AbstractUserMessage extends AbstractMessage {
	public final String fromUserName;
	public final String fromUserId;
	public final int gender;

	public static int USER_MESSAGE_GENDER_UNSPECIFIED = -1;// 未知
	public static int USER_MESSAGE_GENDER_MALE = 0;// 男
	public static int USER_MESSAGE_GENDER_FEMALE = 1;// 女

	AbstractUserMessage(String userId, String userName, String type, int gender) {
		super(type);
		this.fromUserName = userName;
		this.fromUserId = userId;
		// this.gender = User.parseGenderFromStr(gender);
		this.gender = gender;
	}
}
