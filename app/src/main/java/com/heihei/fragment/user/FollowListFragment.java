package com.heihei.fragment.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.base.host.AppLogic;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.base.widget.cobe.loadmore.LoadMoreContainer;
import com.base.widget.cobe.ptr.PtrFrameLayout;
import com.heihei.adapter.UserAdapter;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.BaseListFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.User;
import com.wmlives.heihei.R;

/**
 * 关注列表
 * 
 * @author chengbo
 */
public class FollowListFragment extends BaseListFragment implements OnItemClickListener {

	private List<User> data;
	private UserAdapter mAdapter;

	private UserPresent mUserPresent = new UserPresent();
	private PlayActivityInfo info = null;
	private String uid = "";

	@Override
	protected void viewDidLoad() {
		super.viewDidLoad();
		mListView.setOnItemClickListener(this);
	}

	protected void refresh() {
		super.refresh();

		uid = (String) mViewParam.data;
		if (StringUtils.isEmpty(uid)) {
			UIUtils.showToast("该用户不存在");
			getActivity().finish();
		}

		if (mViewParam.data1 != null) {
			try {
				info = (PlayActivityInfo) mViewParam.data1;
			} catch (Exception e) {
				info = null;
				e.printStackTrace();
			}
		}

		if (data == null) {
			data = new ArrayList<>();
		}

		if (mListView.getAdapter() == null) {
			mAdapter = new UserAdapter(data);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		mListLayout.autoRefresh();

	};

	private int offset = 0;

	@Override
	public void onRefreshBegin(PtrFrameLayout frame, boolean byUser) {
		super.onRefreshBegin(frame, byUser);
		offset = 0;
		if (byUser) {
			mUserPresent.getFollowList(uid, UserMgr.getInstance().getUid(), offset, PAGE_SIZE, listResponse, false);
		} else {
			mUserPresent.getFollowList(uid, UserMgr.getInstance().getUid(), offset, PAGE_SIZE, listResponse, true);
		}
	}

	@Override
	public void onLoadMore(LoadMoreContainer loadMoreContainer) {
		super.onLoadMore(loadMoreContainer);
		offset = data.size();
		mUserPresent.getFollowList(uid, UserMgr.getInstance().getUid(), offset, PAGE_SIZE, moreListResponse, false);
	}

	private JSONResponse listResponse = new JSONResponse() {

		@Override
		public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

			if (!cached) {
				mListLayout.refreshComplete();
			}

			if (errCode == ErrorCode.ERROR_OK) {
				int total = json.optInt("totalSize");
				JSONArray result = json.optJSONArray("result");
				if (result != null && result.length() > 0) {
					data.clear();
					for (int i = 0; i < result.length(); i++) {
						User user = new User(result.optJSONObject(i));
						data.add(user);
					}
				}

				if (data.size() <= 0) {

					if (uid.equals(UserMgr.getInstance().getUid())) {
						showEmptyView(getString(R.string.user_follow_declear));
					} else {
						showEmptyView(getString(R.string.user_follow_declear_other));
					}

				} else {
					dismissEmptyView();
				}

				if (data.size() < total) {// 还可以拉更多
					mListLayout.loadMoreFinish(false, true);
				} else {
					mListLayout.loadMoreFinish(false, false);
				}
			} else {
				mListLayout.loadMoreFinish(false, true);
				UIUtils.showToast(msg);
			}

			mAdapter.notifyDataSetChanged();

		}
	};

	private JSONResponse moreListResponse = new JSONResponse() {

		@Override
		public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
			if (errCode == ErrorCode.ERROR_OK) {
				int total = json.optInt("totalSize");
				JSONArray result = json.optJSONArray("result");
				if (result != null && result.length() > 0) {
					for (int i = 0; i < result.length(); i++) {
						User user = new User(result.optJSONObject(i));
						data.add(user);
					}
				}

				if (data.size() < total) {// 还可以拉更多
					mListLayout.loadMoreFinish(false, true);
				} else {
					mListLayout.loadMoreFinish(false, false);
				}

			} else {
				UIUtils.showToast(msg);
				mListLayout.loadMoreFinish(false, false);
			}

			mAdapter.notifyDataSetChanged();

		}
	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			if (!clickOlder()) {
				return;
			}
			User user = (User) parent.getAdapter().getItem(position);
			UserDialog ud = new UserDialog(getContext(), user, null, false, UserDialog.USERDIALOG_OPENTYPE_DEFAULE);
			if (info != null)
				info.setFollowInfo(true, getActivity());
			else {
				info = new PlayActivityInfo(false, null);
				info.setFollowInfo(true, getActivity());
			}
			ud.setReplayInfo(info);
			ud.show();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

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
