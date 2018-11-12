package com.heihei.model.msg.due;

import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.ObServerMessage;

public interface BellStatusApi {
	public void bellMessageSum();

	public void bellStartVibration(ActionMessage msg);

	public void notifyData();

	public void joinMessage(ObServerMessage msg);

	public void chatUserStatus(ObServerMessage msg);

	public void hideMessageCount();
}
