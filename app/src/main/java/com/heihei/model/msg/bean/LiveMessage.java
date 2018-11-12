package com.heihei.model.msg.bean;

import com.base.widget.message.MessagePart;

import android.view.View;

public class LiveMessage extends AbstractUserMessage implements MessagePart {

	public LiveMessage(String userId, String userName, int gender, String liveId, int type, String text) {
		super(userId, userName, MESSAGE_TYPE_LIKE, gender);
		this.liveId = liveId;
		this.text = text;
	}

	public String text;
	public String liveId;
	@Override
	public View accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
