package com.heihei.model.msg.api;

import com.heihei.model.msg.bean.ActionMessage;

public interface ActionMessageCallback extends MessageCallback {
	public void callback(ActionMessage message);
}
