package com.heihei.cell;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Date;

import com.base.utils.DateUtils;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ActionMessage;
import com.wmlives.heihei.R;

public class MessageCell extends LinearLayout implements ListCell, OnClickListener {

	// ----------------R.layout.cell_message-------------Start
	private TextView btn_pm; // 前往按钮
	private TextView btn_ignore; // 忽略按钮
	private TextView tv_nickname; // 昵称
	private ImageView iv_probate; // 加V图标
	private AvatarImageView iv_avatar; // 头像
	private TextView tv_trynumber; // 约聊次数
	private TextView tv_duetime; // 约聊时间

	public void autoLoad_cell_message() {
		btn_pm = (TextView) findViewById(R.id.btn_pm);
		btn_ignore = (TextView) findViewById(R.id.btn_ignore);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		iv_probate = (ImageView) findViewById(R.id.iv_probate);
		iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
		tv_trynumber = (TextView) findViewById(R.id.tv_trynumber);
		tv_duetime = (TextView) findViewById(R.id.tv_duetime);
	}

	// ----------------R.layout.cell_message-------------End

	public MessageCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_cell_message();
		btn_ignore.setOnClickListener(this);
		btn_pm.setOnClickListener(this);
		iv_avatar.setOnClickListener(this);
	}

	private int position = 0;

	@Override
	public void setData(Object data, int position, BaseAdapter mAdapter) {
		this.position = position;

		ActionMessage msg = (ActionMessage) mAdapter.getItem(position);
		if (msg != null && msg.actionType != null && msg.actionType.equals(ActionMessage.ACTION_MESSAGE_TYPE_CHAT_MESSAGE)) {
			if (msg.user != null) {
				tv_nickname.setVisibility(View.VISIBLE);
				tv_nickname.setText(msg.user.nickname);
				iv_avatar.setUser(msg.user);
			}
			tv_trynumber.setText(msg.messageData.text);
			tv_duetime.setText(DateUtils.convertTime(msg.timeStamp));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_ignore:// 忽略
			if (mOnMessageClickListener != null)
				mOnMessageClickListener.onIgnoreClick(position);
			break;
		case R.id.btn_pm:// 前往
			if (mOnMessageClickListener != null)
				mOnMessageClickListener.onSendClick(position);
			break;
		case R.id.iv_avatar:// 前往
			if (mOnMessageClickListener != null)
				mOnMessageClickListener.onAvatar(position);
			break;
		}

	}

	private OnMessageClickListener mOnMessageClickListener;

	public void setOnMessageClickListener(OnMessageClickListener mOnMessageClickListener) {
		this.mOnMessageClickListener = mOnMessageClickListener;
	}

	public static interface OnMessageClickListener {

		public void onIgnoreClick(int position);

		public void onSendClick(int position);

		public void onAvatar(int position);

	}

}
