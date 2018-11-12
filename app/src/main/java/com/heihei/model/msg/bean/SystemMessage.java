package com.heihei.model.msg.bean;

import com.base.widget.message.MessagePart;

import android.view.View;

public final class SystemMessage extends AbstractMessage implements MessagePart {

	public SystemMessage(String text) {
		super(MESSAGE_TYPE_SYSTEM);
		this.text = text;
	}

	public String text;

	@Override
	public View accept(Visitor visitor) {
		return visitor.visit(this);
	}
}
