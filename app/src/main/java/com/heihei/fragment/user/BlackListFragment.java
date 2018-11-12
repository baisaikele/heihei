package com.heihei.fragment.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.UIUtils;
import com.base.widget.cobe.loadmore.LoadMoreContainer;
import com.base.widget.cobe.ptr.PtrFrameLayout;
import com.heihei.adapter.BlackUserAdapter;
import com.heihei.adapter.BlackUserAdapter.OnDataChangeLintener;
import com.heihei.adapter.UserAdapter;
import com.heihei.fragment.BaseListFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.wmlives.heihei.R;

/**
 * 关注列表
 * 
 * @author chengbo
 */
public class BlackListFragment extends BaseListFragment {

	private List<User> data;
	private BlackUserAdapter mAdapter;

	private UserPresent mUserPresent = new UserPresent();

	@Override
	protected void viewDidLoad() {
		super.viewDidLoad();
	}

	protected void refresh() {
		super.refresh();

		if (data == null) {
			data = new ArrayList<>();
		}
		// data.clear();
		//
		// for (int i = 0; i < 10; i++) {
		// data.add(new User());
		// }

		if (mListView.getAdapter() == null) {
			mAdapter = new BlackUserAdapter(data, mChangeLintener);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		mListLayout.autoRefresh();

	};

	@Override
	public void onDestroy() {
		data.clear();
		super.onDestroy();
	}

	private OnDataChangeLintener mChangeLintener = new OnDataChangeLintener() {

		@Override
		public void onChange() {
			if (data.size() <= 0) {
				showEmptyView(getString(R.string.user_black_declear));
			} else {
				dismissEmptyView();
			}
		}
	};

	private int offset = 0;

	@Override
	public void onRefreshBegin(PtrFrameLayout frame, boolean byUser) {
		super.onRefreshBegin(frame, byUser);
		offset = 0;
		if (byUser) {
			mUserPresent.getBlockList(offset, PAGE_SIZE, listResponse, false);
		} else {
			mUserPresent.getBlockList(offset, PAGE_SIZE, listResponse, false);
		}
	}

	@Override
	public void onLoadMore(LoadMoreContainer loadMoreContainer) {
		super.onLoadMore(loadMoreContainer);
		offset = data.size();
		mUserPresent.getBlockList(offset, PAGE_SIZE, moreListResponse, false);
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
					showEmptyView(getString(R.string.user_black_declear));
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
			}

			mAdapter.notifyDataSetChanged();

		}
	};

}
