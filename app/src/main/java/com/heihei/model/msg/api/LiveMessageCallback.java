package com.heihei.model.msg.api;

import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;

public interface LiveMessageCallback extends MessageCallback {
	public void addTextCallbackView(AbstractMessage message);

	public void addGiftCallbackView(GiftMessage message);
	
	public void addLiveLikeCallbackView(LiveMessage message);
}



