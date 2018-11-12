package com.heihei.fragment.user;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.DeviceInfoUtils;
import com.base.utils.UIUtils;
import com.base.widget.cobe.CodePullHandler;
import com.base.widget.cobe.PtrListLayout;
import com.base.widget.cobe.loadmore.LoadMoreContainer;
import com.base.widget.cobe.ptr.PtrDefaultHandler;
import com.base.widget.cobe.ptr.PtrFrameLayout;
import com.base.widget.swipemenulistview.SwipeMenu;
import com.base.widget.swipemenulistview.SwipeMenuCreator;
import com.base.widget.swipemenulistview.SwipeMenuItem;
import com.base.widget.swipemenulistview.SwipeMenuListView;
import com.base.widget.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.heihei.adapter.BaseAdapter;
import com.heihei.cell.ListCell;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.TipDialog;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.BaseListFragment;
import com.heihei.logic.StatusController;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.LivePresent;
import com.heihei.model.LiveInfo;
import com.heihei.model.PlayActivityInfo;
import com.wmlives.heihei.R;

/**
 * 直播列表
 * 
 * @author admin
 *
 */
public class LivesListFragment extends BaseFragment implements CodePullHandler, OnItemClickListener, OnMenuItemClickListener {

	private SwipeMenuListView mListView;
	private PtrListLayout mListLayout;

	private LiveAdapter mAdapter;
	private List<LiveInfo> data;
	protected View mEmptyView;
	private LivePresent mLivePresent = new LivePresent();

	private String uid = "";

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_lives_list);
	}

	private PlayActivityInfo info = null;

	@Override
	protected void viewDidLoad() {

		if (mViewParam.data != null) {
			uid = (String) mViewParam.data;
		}

		if (mViewParam.data1 != null) {
			try {
				info = (PlayActivityInfo) mViewParam.data1;
			} catch (Exception e) {
				info = null;
				e.printStackTrace();
			}

		}

		mEmptyView = findViewById(R.id.empty_view);
		mListView = (SwipeMenuListView) findViewById(R.id.listview);
		mListLayout = (PtrListLayout) findViewById(R.id.ptrlistlayout);
		mListLayout.setCodePullHandler(this);
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				createMenu(menu);
			}

			private void createMenu(SwipeMenu menu) {
				SwipeMenuItem item = new SwipeMenuItem(getContext());
				item.setIcon(getResources().getDrawable(R.drawable.hh_mylive_delete));
				item.setWidth(DeviceInfoUtils.dip2px(getContext(), 50f));
				// item.setBackground(R.drawable.hh_mylive_delete_bg);
				menu.addMenuItem(item);
			}

		};

		if (UserMgr.getInstance().getUid().equals(uid)) {
			mListView.setMenuCreator(creator);
			mListView.setOnMenuItemClickListener(this);
		}

		mListView.setOnItemClickListener(this);

	}

	@Override
	protected void refresh() {
		if (data == null) {
			data = new ArrayList<>();
		}
		data.clear();

		if (mListView.getAdapter() == null) {
			mAdapter = new LiveAdapter(data);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
		mListLayout.autoRefresh();

	}

	@Override
	public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
		return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
	}

	JSONResponse listResponse = new JSONResponse() {

		@Override
		public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
			if (!cached) {
				mListLayout.refreshComplete();
			}

			if (errCode == ErrorCode.ERROR_OK) {
				if (offset == 0) {
					data.clear();
				}

				int total = json.optInt("totalCount");
				JSONArray liveArr = json.optJSONArray("lives");
				if (liveArr != null && liveArr.length() > 0) {
					for (int i = 0; i < liveArr.length(); i++) {
						data.add(new LiveInfo(liveArr.optJSONObject(i)));
					}
				}

				mAdapter.notifyDataSetChanged();

				if (data.size() < total) {
					mListLayout.loadMoreFinish(false, true);
				} else {
					mListLayout.loadMoreFinish(false, true);
				}

				if (data.size() <= 0) {
					showEmptyView();
				} else {
					dismissEmptyView();
				}

			}

		}
	};

	private int offset = 0;

	@Override
	public void onRefreshBegin(PtrFrameLayout frame, boolean byUser) {
		offset = 0;
		if (byUser) {
			mLivePresent.getLiveList(this.uid, offset, BaseListFragment.PAGE_SIZE, listResponse, false);
		} else {
			mLivePresent.getLiveList(this.uid, offset, BaseListFragment.PAGE_SIZE, listResponse, true);
		}

	}

	@Override
	public void onLoadMore(LoadMoreContainer loadMoreContainer) {
		offset = data.size();
		mLivePresent.getLiveList(this.uid, offset, BaseListFragment.PAGE_SIZE, listResponse, false);

	}

	@Override
	public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
		showDeleteDialog(position);
		return false;
	}

	private void showDeleteDialog(final int position) {
		TipDialog td = new TipDialog(getContext());
		td.setContent(getResources().getString(R.string.live_delete_tip));
		td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

			@Override
			public void onOkClick(Dialog dialog) {
				deleteLive(position);

			}

			@Override
			public void onCancleClick(Dialog dialog) {

			}
		});
		td.show();
	}

	private void deleteLive(final int position) {
		LiveInfo mLive = data.get(position);
		mLivePresent.deleteLive(mLive.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					data.remove(position);
					mAdapter.notifyDataSetChanged();
				} else {
					UIUtils.showToast(msg);
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (StatusController.getLiveIng()) {
			UIUtils.showToast(getString(R.string.lives_ing));
			return;
		}
		final LiveInfo mLive = (LiveInfo) parent.getAdapter().getItem(position);
		if (info != null && info.isReplay && info.replayActivity != null && info.openType == UserDialog.USERDIALOG_OPENTYPE_DEFAULE) {
			info.replayActivity.finish();
			NavigationController.gotoLiveReplayFragment(getContext(), mLive);
		} else if (info != null && info.openType == UserDialog.USERDIALOG_OPENTYPE_PMFRAGMENT && info.pmFragment != null) {

			final TipDialog td = new TipDialog(getActivity());
			td.setContent(HostApplication.getInstance().getResources().getString(R.string.user_close_pmfragment));
			td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

				@Override
				public void onOkClick(Dialog dialog) {
					try {
						info.pmFragment.close();
						info.isPmfragment = false;
						info.pmFragment = null;
						NavigationController.gotoLiveReplayFragment(getContext(), mLive);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onCancleClick(Dialog dialog) {
					td.dismiss();
				}
			});
			td.show();
		} else {
			NavigationController.gotoLiveReplayFragment(getContext(), mLive);
		}
	}

	class LiveAdapter extends BaseAdapter<LiveInfo> {

		public LiveAdapter(List<LiveInfo> data) {
			super(data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_live, null);
			}
			ListCell lc = (ListCell) convertView;
			lc.setData(getItem(position), position, this);
			return convertView;
		}
	}

	/**
	 * 显示空View
	 */
	protected void showEmptyView() {
		mEmptyView.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.GONE);
	}

	/**
	 * 隐藏空View
	 */
	protected void dismissEmptyView() {
		mEmptyView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LivesListFragment";
	}

}
