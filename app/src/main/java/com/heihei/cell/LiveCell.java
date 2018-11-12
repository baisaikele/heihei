package com.heihei.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import com.facebook.fresco.FrescoImageHelper;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.model.LiveInfo;
import com.wmlives.heihei.R;

public class LiveCell extends LinearLayout implements ListCell {

	// ----------------R.layout.cell_home_video-------------Start
	private AvatarImageView iv_avatar;
	private LinearLayout ll_right;
	private TextView tv_time;
	private TextView tv_num;
	private TextView tv_nickname;
	private TextView tv_title;

	public void autoLoad_cell_home_video() {
		iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
		ll_right = (LinearLayout) findViewById(R.id.ll_right);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}

	// ----------------R.layout.cell_home_video-------------End

	public LiveCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_cell_home_video();
	}

	LiveInfo mLive;

	@Override
	public void setData(Object data, int position, BaseAdapter mAdapter) {
		mLive = (LiveInfo) data;
		iv_avatar.setUser(mLive.creator);
		tv_title.setText(mLive.title);
		tv_nickname.setText(mLive.creator.nickname);

		try {
			String time=mLive.createTime.trim();
			tv_time.setText(time.substring(0, time.indexOf(" ")));
		} catch (Exception e) {
			tv_time.setText(mLive.createTime);
			e.printStackTrace();
		}
		tv_num.setText(String.valueOf(mLive.totalUsers) + getContext().getString(R.string.psersion));
	}

}
