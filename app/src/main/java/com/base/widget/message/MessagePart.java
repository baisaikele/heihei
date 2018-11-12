package com.base.widget.message;

import com.heihei.model.msg.bean.AbstractTextMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.SystemMessage;

import android.view.View;

public interface MessagePart {

	public interface Visitor {
		View visit(SystemMessage msg);

		View visit(LiveMessage msg);

		View visit(BulletMessage msg);

		View visit(AbstractTextMessage msg);
	}

	View accept(Visitor visitor);

}
