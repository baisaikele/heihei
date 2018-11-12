package com.heihei.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.base.host.AppLogic;
import com.facebook.fresco.FrescoImageHelper;
import com.facebook.fresco.FrescoImageView;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.UserMgr;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class AudienceAdapter extends RecyclerView.Adapter<AudienceAdapter.ViewHolder> {

	List<User> data;

	private String liveId = "";
	private boolean isCreator = false;
	private PlayActivityInfo info = null;
	private Context context;

	public AudienceAdapter(List<User> data, Context context) {
		this.data = data;
		this.context = context;
	}

	public void setLiveIdAndIsCreator(String liveId, boolean isCreator) {
		this.liveId = liveId;
		this.isCreator = isCreator;
	}

	public void setReplayInfo(PlayActivityInfo info) {
		this.info = info;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		private AvatarImageView iv_avatar;
		private ImageView iv_followed;

		public ViewHolder(View arg0) {
			super(arg0);
			iv_avatar = (AvatarImageView) arg0.findViewById(R.id.iv_avatar);
			iv_followed = (ImageView) arg0.findViewById(R.id.iv_followed);
		}

	}

	@Override
	public int getItemCount() {
		if (data == null)
			return 0;
		return data.size();
	}

	@Override
	public void onBindViewHolder(ViewHolder vh, int position) {
		final User itemuser = data.get(position);

		if (itemuser.isFollowed) {
			vh.iv_followed.setVisibility(View.VISIBLE);

			if (itemuser.gender == User.FEMALE) {
				vh.iv_followed.setImageResource(R.drawable.hh_live_following_female);
			} else {
				vh.iv_followed.setImageResource(R.drawable.hh_live_following_male);
			}
		} else {
			vh.iv_followed.setVisibility(View.GONE);
		}

		vh.iv_avatar.setUser(data.get(position));
		vh.iv_avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (UserMgr.getInstance().getUid().equals(itemuser.uid)) {
					return;
				}
				if (!clickOlder()) {
					return;
				}
				UserDialog ud = new UserDialog(context, itemuser, liveId, isCreator, UserDialog.USERDIALOG_OPENTYPE_AUDIENCE);
//				if (isCreator) {
//					ud.setChatable(false);
//				}
				ud.setReplayInfo(info);
				ud.show();
			}
		});
	}

	private long lastClickTime = 0;

	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
		View view = LayoutInflater.from(arg0.getContext()).inflate(R.layout.cell_audience, arg0, false);
		ViewHolder vh = new ViewHolder(view);
		return vh;
	}
}
