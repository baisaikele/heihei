package com.base.widget.message;

import org.json.JSONObject;

import com.base.danmaku.DanmakuItem;
import com.base.danmaku.DanmakuView;
import com.base.danmaku.DanmakuView.OnItemClickListener;
import com.base.host.BaseFragment;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.live.widget.LiveBottom;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.LivePresent;
import com.heihei.model.LiveInfo;
import com.heihei.model.User;
import com.wmlives.heihei.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.ViewGroup;

public class DanmakuViewFragment extends BaseFragment implements OnClickListener {
	private DanmakuView mDanmakuView;
	private View fl_danmu_contentview;
	private ImageView iv_live_righttip;

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_information);
	}

	@Override
	protected void viewDidLoad() {
		fl_danmu_contentview = findViewById(R.id.fl_danmu_contentview);
		fl_danmu_contentview.setVisibility(View.VISIBLE);
		iv_live_righttip = (ImageView) findViewById(R.id.iv_live_righttip);
		mDanmakuView = (DanmakuView) findViewById(R.id.danmakuview);
		mDanmakuView.setVisibility(View.VISIBLE);
		mDanmakuView.setOnClickListener(this);
		mDanmakuView.setOnItemClickListener(mDanmakuItemClickListener);
	}

	private boolean isShow;

	public void showhttip(boolean isShow) {
		this.isShow = isShow;
	}

	@Override
	protected void refresh() {
		if (iv_live_righttip == null)
			return;
		if (isShow)
			iv_live_righttip.setVisibility(View.VISIBLE);
		else
			iv_live_righttip.setVisibility(View.GONE);
	}

	private OnItemClickListener mDanmakuItemClickListener = new OnItemClickListener() {

		@Override
		public void OnItemClick(DanmakuItem item) {
			if (item.type == DanmakuItem.TYPE_SYSTEM) {
				return;
			}

			if (UserMgr.getInstance().getUid().equals(item.userId)) {
				return;
			}

			User user = new User();
			user.uid = item.userId;
			user.nickname = item.userName;
			user.gender = item.gender;
			user.birthday = item.birthday;

			if (mLiveInfo != null && UserMgr.getInstance().getUid().equals(mLiveInfo.creator.uid)) {
				UserDialog ud = new UserDialog(getContext(), user, liveId, true, UserDialog.USERDIALOG_OPENTYPE_ANCHOR);
				ud.show();
			} else {
				UserDialog ud = new UserDialog(getContext(), user, liveId, false, UserDialog.USERDIALOG_OPENTYPE_AUDIENCE);
				ud.show();
			}
		}

		@Override
		public void onItemLongClick(DanmakuItem item) {
			if (item.type == DanmakuItem.TYPE_SYSTEM) {
				return;
			}

			if (UserMgr.getInstance().getUid().equals(item.userId)) {
				return;
			}

			mLiveBottom.atUser(item.userName);
		}
	};

	public void hintTtipView() {
		if (iv_live_righttip != null)
			iv_live_righttip.setVisibility(View.GONE);
	}

	public void setDanmakuPause() {
		if (mDanmakuView != null)
			mDanmakuView.pause();
	}

	public void setDanmakuResume() {
		if (mDanmakuView != null)
			mDanmakuView.resume();
	}

	public void setDanmakuStop() {
		if (mDanmakuView != null)
			mDanmakuView.stop();
	}

	public void putDanmakuItem(DanmakuItem item) {
		if (mDanmakuView != null)
			mDanmakuView.addDanmakuItem(item);
	}

	private LiveBottom mLiveBottom;
	private String liveId;
	private ScrollViewFragment mScrollViewFragment;
	private LiveInfo mLiveInfo;

	public void setLiveBottom(LiveBottom bottom, LiveInfo info, ScrollViewFragment mScrollViewFragment) {
		this.mLiveBottom = bottom;
		this.liveId = info.liveId;
		this.mLiveInfo = info;
		this.mScrollViewFragment = mScrollViewFragment;
	}

	private int likeCount = 0;
	private long firstClickTime = 0l;
	private int LIKE_TIME_INTERVAL = 5000;
	private boolean hasOneLike = false;
	private boolean hasTwoLike = false;
	private boolean hasThreeLike = false;
	public static final int LIKE_ONE = 1;// 点亮了
	public static final int LIKE_AGAIN = 2;// 又点亮了
	public static final int LIKE_AGAIN_AND_AGAIN = 3;// 疯狂的点亮

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.danmakuview:
			if (mLiveBottom != null)
				mLiveBottom.hideKeyboard();

			long nowTime = System.currentTimeMillis();
			if (nowTime - firstClickTime > 1000 * 60 * 5) {// 距离上一次点击超过5分钟了
				firstClickTime = 0l;
				hasOneLike = false;
				hasTwoLike = false;
				hasThreeLike = false;
			}
			if (firstClickTime == 0l)// 表示第一次点击
			{
				if (!hasOneLike) {
					like(LIKE_ONE);
					hasOneLike = true;
				}

				firstClickTime = nowTime;
				likeCount++;
				return;
			}
			if (nowTime - firstClickTime <= LIKE_TIME_INTERVAL) {
				likeCount++;
				if (likeCount == 3) {
					if (!hasTwoLike) {
						like(LIKE_AGAIN);
						hasTwoLike = true;
					}

				} else if (likeCount == 14) {
					if (!hasThreeLike) {
						like(LIKE_AGAIN_AND_AGAIN);
						hasThreeLike = true;
					}

				}
			} else {
				firstClickTime = nowTime;
				likeCount = 0;
			}

			break;

		}
	}

	/**
	 * 点亮
	 */
	private void like(final int type) {
		mLivePresent.like(liveId, type, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

				if (errCode == ErrorCode.ERROR_OK) {
					DanmakuItem item = new DanmakuItem();
					item.userName = UserMgr.getInstance().getLoginUser().nickname;
					item.gender = UserMgr.getInstance().getLoginUser().gender;
					item.birthday = UserMgr.getInstance().getLoginUser().birthday;
					item.type = DanmakuItem.TYPE_LIKE;
					item.userId = UserMgr.getInstance().getUid();

					switch (type) {
					case LIKE_ONE:
						item.text = getString(R.string.like_one_tip);
						break;
					case LIKE_AGAIN:
						item.text = getString(R.string.like_two_tip);
						break;
					case LIKE_AGAIN_AND_AGAIN:
						item.text = getString(R.string.like_three_tip);
						break;
					}
					putDanmakuItem(item);
					mScrollViewFragment.updateSelfMessage(item);
				}

			}
		});
	}

	private LivePresent mLivePresent = new LivePresent();

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "DanmakuViewFragment";
	}

}
