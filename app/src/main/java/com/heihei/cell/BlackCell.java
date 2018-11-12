package com.heihei.cell;

import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.host.AppLogic;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.UIUtils;
import com.facebook.fresco.FrescoImageHelper;
import com.heihei.adapter.BlackUserAdapter;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class BlackCell extends LinearLayout implements ListCell, OnClickListener {

	// ----------------R.layout.cell_black_user-------------Start
	private TextView btn_remove;
	private AvatarImageView iv_avatar;
	private TextView tv_nickname;
	private TextView tv_level;

	public void autoLoad_cell_black_user() {
		btn_remove = (TextView) findViewById(R.id.btn_remove);
		iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
		tv_nickname = (TextView) findViewById(R.id.tv_nickname);
		tv_level = (TextView) findViewById(R.id.tv_level);
	}

	// ----------------R.layout.cell_black_user-------------End

	public BlackCell(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		autoLoad_cell_black_user();
		btn_remove.setOnClickListener(this);
		iv_avatar.setOnClickListener(this);
	}

	private User user = null;
	private int position = 0;
	private BaseAdapter mAdapter;

	@Override
	public void setData(Object data, int position, BaseAdapter mAdapter) {
		user = (User) data;
		this.position = position;
		this.mAdapter = mAdapter;
		iv_avatar.setUser(user);
		tv_nickname.setText(user.nickname);
		tv_level.setText("LV." + user.level);

	}

	private UserPresent mUserPresent = new UserPresent();

	private long lastClickTime = 0;
	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_avatar: {
			if (user == null)
				return;
			if (!clickOlder())
				return;
			
			UserDialog ud = new UserDialog(getContext(), user, "", false, UserDialog.USERDIALOG_OPENTYPE_DEFAULE);
			ud.show();
		}
			break;
		case R.id.btn_remove: {
			mUserPresent.unblockUser(user.uid, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode == ErrorCode.ERROR_OK) {
						if (mAdapter instanceof com.heihei.adapter.BlackUserAdapter) {
							((com.heihei.adapter.BlackUserAdapter) (mAdapter)).remove(position);
							mAdapter.notifyDataSetChanged();
							((com.heihei.adapter.BlackUserAdapter) mAdapter).mLintener.onChange();
						}
					} else {
						UIUtils.showToast(msg);
					}

				}
			});
		}
			break;
		}

	}

}
