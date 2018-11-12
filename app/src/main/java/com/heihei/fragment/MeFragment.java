package com.heihei.fragment;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.host.NavigationController;
import com.heihei.adapter.BaseAdapter;
import com.heihei.cell.MeHeader;
import com.heihei.dialog.UserDialog;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.wmlives.heihei.R;

/**
 * 个人主页界面
 * 
 * @author chengbo
 */
public class MeFragment extends BaseListFragment implements OnItemClickListener {

	private MeAdapter mAdapter;

	private MeHeader mHeader;

	private List<MeObj> data;

	@Override
	protected void viewDidLoad() {
		super.viewDidLoad();
		getTitleBar().setVisibility(View.GONE);
		mListLayout.setPullToRefreshEnable(false);
		mListView.setOnItemClickListener(this);

		EventManager.ins().registListener(EventTag.ACCOUNT_UPDATE_INFO, mInfoListener);
	}

	@Override
	protected void refresh() {
		super.refresh();

		mHeader = (MeHeader) LayoutInflater.from(getContext()).inflate(R.layout.header_me, null);
		mListView.addHeaderView(mHeader, null, false);

		refreshHeader();
		refreshList();

	}

	EventListener mInfoListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			refreshHeader();
			refreshList();
		}
	};

	public void onDestroy() {
		super.onDestroy();
		EventManager.ins().removeListener(EventTag.ACCOUNT_UPDATE_INFO, mInfoListener);
	};

	private void refreshHeader() {
		mHeader.setData();
	}

	private void refreshList() {
		if (data == null) {
			data = new ArrayList<MeObj>();
		}
		data.clear();
		data.add(new MeObj(MeObj.TYPE_MY_LIVES, UserMgr.getInstance().getLoginUser().liveCount));
		data.add(new MeObj(MeObj.TYPE_SETTING, 0));
		if (mListView.getAdapter() == null) {
			mAdapter = new MeAdapter(data);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			UserMgr.getInstance().requestMineInfo(null);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		UserMgr.getInstance().requestMineInfo(null);
	}

	class MeAdapter extends BaseAdapter<MeObj> {

		public MeAdapter(List<MeObj> data) {
			super(data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_me, null);
			}
			MeObj obj = getItem(position);

			ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
			TextView tv_num = (TextView) convertView.findViewById(R.id.tv_num);
			ImageView icon_right = (ImageView) convertView.findViewById(R.id.iv_right_arrow);

			switch (obj.type) {
			case MeObj.TYPE_MY_LIVES:
				tv_title.setText(R.string.me_my_lives);
				tv_num.setText(obj.num + "");
				tv_num.setVisibility(View.VISIBLE);
				icon_right.setVisibility(View.GONE);
				iv_icon.setImageResource(R.drawable.hh_me_mylive);
				break;
			case MeObj.TYPE_SETTING:
				tv_title.setText(R.string.me_setting);
				tv_num.setText(obj.num + "");
				tv_num.setVisibility(View.GONE);
				icon_right.setVisibility(View.VISIBLE);
				iv_icon.setImageResource(R.drawable.hh_me_setting);
				break;
			}
			return convertView;

		}

	}

	public static class MeObj {

		public static final int TYPE_MY_LIVES = 0;// 我的直播
		public static final int TYPE_SETTING = 1;// 设置

		public int type;
		public int num;

		public MeObj(int type, int num) {
			this.type = type;
			this.num = num;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		try {
			MeObj obj = (MeObj) parent.getAdapter().getItem(position);
			switch (obj.type) {
			case MeObj.TYPE_MY_LIVES:
				NavigationController.gotoLivesListFragment(getContext(), UserMgr.getInstance().getUid(), null);
				break;
			case MeObj.TYPE_SETTING:
				NavigationController.gotoSettingFragment(getContext());
				break;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

}
