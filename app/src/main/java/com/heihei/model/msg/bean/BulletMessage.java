package com.heihei.model.msg.bean;

import com.base.widget.message.MessagePart;

import android.view.View;

public final class BulletMessage extends AbstractTextMessage implements MessagePart  {

	public BulletMessage(String userId, String userName, String text, int gender, String converUrl, String roomId, String liveId,int totalTicket) {
		super(userId, userName, text, MESSAGE_TYPE_BARRAGE, gender, converUrl, roomId, liveId);
		this.totalTicket = totalTicket;
	}

	public int totalTicket;
	
	@Override
	public View accept(Visitor visitor) {
		return super.accept(visitor);
	}
	
	
}
