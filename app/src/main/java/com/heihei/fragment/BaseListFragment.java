package com.heihei.fragment;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.base.host.BaseFragment;
import com.base.widget.cobe.CodePullHandler;
import com.base.widget.cobe.PtrListLayout;
import com.base.widget.cobe.loadmore.LoadMoreContainer;
import com.base.widget.cobe.ptr.PtrDefaultHandler;
import com.base.widget.cobe.ptr.PtrFrameLayout;
import com.wmlives.heihei.R;

public class BaseListFragment extends BaseFragment implements CodePullHandler {

	public static final int PAGE_SIZE = 20;

	protected PtrListLayout mListLayout;
	protected ListView mListView;
	protected View mEmptyView;
	protected TextView tv_empty_decler;

	@Override
	protected void loadContentView() {
		setContentView(R.layout.activity_list);
	}

	@Override
	protected void viewDidLoad() {
		mListLayout = (PtrListLayout) findViewById(R.id.ptrlistlayout);
		mListView = (ListView) findViewById(R.id.listview);
		mEmptyView = findViewById(R.id.empty_view);
		tv_empty_decler = (TextView) findViewById(R.id.tv_empty_decler);
		mListLayout.setCodePullHandler(this);
	}

	@Override
	protected void refresh() {

	}

	@Override
	public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
		return PtrDefaultHandler.checkContentCanBePulledDown(frame, mListView, header);
	}

	@Override
	public void onRefreshBegin(PtrFrameLayout frame, boolean byUser) {

	}

	@Override
	public void onLoadMore(LoadMoreContainer loadMoreContainer) {

	}

	/**
	 * 显示空View
	 */
	protected void showEmptyView(String content) {
		try {
			mEmptyView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
			tv_empty_decler.setText(content);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		return "BaseListFragment";
	}

}
