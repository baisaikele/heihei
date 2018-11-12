package com.heihei.model.msg.bean;

import com.base.widget.message.MessagePart;
import com.base.widget.message.MessagePart.Visitor;

import android.view.View;

public abstract class AbstractTextMessage extends AbstractUserMessage implements MessagePart {

	public AbstractTextMessage(String userId, String userName, String text, String type, int gender, String converUrl,
			String roomId, String liveId) {
		super(userId, userName, type, gender);
		this.text = text;
		this.coverUrl = converUrl;
		this.roomId = roomId;
		this.liveId = liveId;
	}

	public AbstractTextMessage(String userId, String userName, String text, String type, int gender) {
		super(userId, userName, type, gender);
		this.text = text;
	}

	public String liveId;
	public String roomId;
	public String text;
	public String coverUrl;

	@Override
	public View accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
