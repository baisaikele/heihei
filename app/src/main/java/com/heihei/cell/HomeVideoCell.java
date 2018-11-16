package com.heihei.cell;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.host.AppLogic;
import com.base.utils.ImageTools;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.UserMgr;
import com.heihei.model.LiveInfo;
import com.wmlives.heihei.R;

public class HomeVideoCell extends LinearLayout implements ListCell, OnClickListener {

	// ----------------R.layout.cell_home_video-------------Start
	private AvatarImageView iv_avatar;
	private LinearLayout ll_right;
	private FrameLayout tv_live;
	private TextView tv_liveing;
	private TextView tv_record;
	private TextView tv_num;
	private TextView tv_nickname;
	private TextView tv_title;

	public void autoLoad_cell_home_video() {
		iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
		ll_right = (LinearLayout) findViewById(R.id.ll_right);
		tv_live = (FrameLayout) findViewById(R.id.tv_live);
		tv_liveing = (TextView) findViewById(R.id.tv_liveing);
		Drawable drawable = getResources().getDrawable(R.drawable.round_point);
		drawable.setBounds(0, 0, ImageTools.dip2px(4), ImageTools.dip2px(4));
		tv_liveing.setCompoundDrawables(drawable, null, null, null);
		tv_record = (TextView) findViewById(R.id.tv_record);
		tv_num = (TextView) findViewById(R.id.tv_num);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}

	// ----------------R.layout.cell_home_video-------------End

	public HomeVideoCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_cell_home_video();
		iv_avatar.setOnClickListener(this);
	}

	private LiveInfo mLiveInfo;

	@Override
	public void setData(Object data, int position, BaseAdapter mAdapter) {
		mLiveInfo = (LiveInfo) data;
		if (mLiveInfo.type == LiveInfo.TYPE_LIVE) {
			tv_live.setVisibility(View.VISIBLE);
			tv_record.setVisibility(View.GONE);
			tv_num.setText(mLiveInfo.totalUsers + "");
		} else {
			tv_live.setVisibility(View.GONE);
			tv_record.setVisibility(View.VISIBLE);
			tv_num.setText(mLiveInfo.totalUsers + "");
		}

		iv_avatar.setUser(mLiveInfo.creator);
		tv_nickname.setText(mLiveInfo.creator.nickname);
		tv_title.setText(mLiveInfo.title);

	}

	@Override
	public void onClick(View v) {
		if (mLiveInfo == null || mLiveInfo.creator == null)
			return;

		if (mLiveInfo.creator.uid.equals(UserMgr.getInstance().getUid()))
			return;

		if (!clickOlder())
			return;
		UserDialog ud = new UserDialog(getContext(), mLiveInfo.creator, "", false, UserDialog.USERDIALOG_OPENTYPE_DEFAULE);
		ud.show();
	}

	private long lastClickTime = 0;

	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}

}
