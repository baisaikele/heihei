package com.heihei.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.animator.AnimationDrawableUtil;
import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.widget.XListView.IXListViewListener;
import com.heihei.adapter.BaseAdapter;
import com.heihei.cell.HomeHeader;
import com.heihei.cell.ListCell;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.MessageDialog;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.MainFragment.HostTitle;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.PmPresent;
import com.heihei.model.LiveInfo;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

/**
 * 首页推荐界面
 * 
 * @author chengbo
 */
public class HomeFragment extends BaseFragment implements OnClickListener, IXListViewListener, OnItemClickListener, Observer {

	// ----------------R.layout.fragment_home-------------Start
	private RelativeLayout titlebar;
	private LinearLayout ll_left;
	private RelativeLayout btn_message;
	private ImageView iv_bell;
	private LinearLayout ll_right;
	private TextView tv_right;
	private LinearLayout ll_mid;
	private TextView tv_title;
	private ImageView tv_home_bell_sum;
	private com.base.widget.XListView listview;

	private List<LiveInfo> data;
	private HomeAdapter mAdapter;

	private LivePresent mLivePresent = new LivePresent();

	public void autoLoad_fragment_home() {
		tv_home_bell_sum = (ImageView) findViewById(R.id.tv_home_bell_sum);
		titlebar = (RelativeLayout) findViewById(R.id.titlebar);
		ll_left = (LinearLayout) findViewById(R.id.ll_left);
		btn_message = (RelativeLayout) findViewById(R.id.btn_message);
		iv_bell = (ImageView) findViewById(R.id.iv_bell);
		ll_right = (LinearLayout) findViewById(R.id.ll_right);
		tv_right = (TextView) findViewById(R.id.tv_right);
		ll_mid = (LinearLayout) findViewById(R.id.ll_mid);
		tv_title = (TextView) findViewById(R.id.tv_title);
		listview = (com.base.widget.XListView) findViewById(R.id.listview);
	}

	private static final int TIME_INTERVAL = 1000 * 60;

	@Override
	protected String initTitle() {
		return getResources().getString(R.string.app_name);
	}

	// ----------------R.layout.fragment_home-------------End
	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_home);
	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_home();

		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(true);
		listview.setXListViewListener(this);
		listview.setOnItemClickListener(this);
		ll_left.setOnClickListener(this);
		ll_right.setOnClickListener(this);
		DueMessageUtils.getInstance().addObserver(this);
	}

	private HomeHeader mHeader;

	@Override
	protected void refresh() {

		mHeader = (HomeHeader) LayoutInflater.from(getContext()).inflate(R.layout.header_excellent, null);
		listview.insertHeaderViewOnTop(mHeader, 0);
		// setHeader();

		if (data == null) {
			data = new ArrayList<LiveInfo>();
		}
		data.clear();
		// for (int i = 0; i < 10; i++) {
		// data.add(new LiveInfo());
		// }

		if (listview.getAdapter() == null) {
			mAdapter = new HomeAdapter(data);
			listview.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		listview.autoRefresh();
		// startBellAnim();
	}

	/**
	 * 解析banner
	 */
	private JSONResponse bannerResponse = new JSONResponse() {

		@Override
		public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
			if (errCode == ErrorCode.ERROR_OK && json != null) {
				JSONArray banners = json.optJSONArray("banners");
				mHeader.setData(banners);
			}

		}
	};

	private JSONResponse listResponse = new JSONResponse() {

		@Override
		public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
			Log.d("home", "refresh:" + cached);
			if (!cached) {
				listview.stopRefresh();
			}
			if (errCode == ErrorCode.ERROR_OK && json != null) {
				data.clear();
				JSONArray liveArr = json.optJSONArray("lives");// 解析lives节点
				if (liveArr != null && liveArr.length() > 0) {
					for (int i = 0; i < liveArr.length(); i++) {
						LiveInfo live = new LiveInfo(liveArr.optJSONObject(i));
						live.type = LiveInfo.TYPE_LIVE;
						data.add(live);
					}

				}

				JSONArray backArr = json.optJSONArray("liveskback");
				if (backArr != null && backArr.length() > 0) {
					for (int i = 0; i < backArr.length(); i++) {
						LiveInfo live = new LiveInfo(backArr.optJSONObject(i));
						live.type = LiveInfo.TYPE_REPLAY;
						data.add(live);
					}

				}
			}

			mAdapter.notifyDataSetChanged();

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_left:
			if (PMFragment.status == PMFragment.STATUS_LOAD_SUCCESS || PMFragment.status == PMFragment.STATUS_LOADING) {
				NavigationController.gotoMessageDialog(getActivity(), tv_home_bell_sum);
			} else {
				NavigationController.gotoMessageDialog(getActivity(), tv_home_bell_sum);
			}

			break;
		case R.id.ll_right:
			if (PMFragment.status == PMFragment.STATUS_LOAD_SUCCESS) {
				TipDialog td = new TipDialog(getContext());
				td.setContent(getContext().getResources().getString(R.string.stop_yueliao_topic));
				td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

					@Override
					public void onOkClick(Dialog dialog) {
						try {
							List<android.support.v4.app.Fragment> list = getFragmentManager().getFragments();
							for (android.support.v4.app.Fragment f : list) {
								if (f instanceof PMFragment) {
									PMFragment pm = (PMFragment) f;
									pm.close();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						NavigationController.gotoStartLive(getContext());
					}

					@Override
					public void onCancleClick(Dialog dialog) {
						dialog.dismiss();

					}
				});
				td.show();

			} else {
				NavigationController.gotoStartLive(getContext());
			}
			break;
		}

	}

	private class HomeAdapter extends BaseAdapter<LiveInfo> {

		public HomeAdapter(List<LiveInfo> data) {
			super(data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_home_video, null);
			}

			ListCell lc = (ListCell) convertView;
			lc.setData(getItem(position), position, this);

			return convertView;
		}
	}

	private long preRefreshTime = 0l;

	@Override
	public void onResume() {
		super.onResume();
//		if (System.currentTimeMillis() - preRefreshTime > TIME_INTERVAL) {
			onRefresh(true);
//		}

		mHandler.removeMessages(0);
		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = this;
		mHandler.sendMessageDelayed(msg, TIME_INTERVAL);
	}

	@Override
	public void onPause() {
		super.onPause();
		mHandler.removeMessages(0);
	}

	private TickHandler mHandler = new TickHandler();

	private void tick() {
		mHandler.removeMessages(0);

		onRefresh(true);

		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = this;
		mHandler.sendMessageDelayed(msg, TIME_INTERVAL);
	}

	@Override
	public void onRefresh(boolean byUser) {
		preRefreshTime = System.currentTimeMillis();
		if (byUser)// 手动刷新
		{
			mLivePresent.getHomeBanner(bannerResponse, false);
			mLivePresent.getHomeRecommendList(listResponse, false);
		} else {
			mLivePresent.getHomeBanner(bannerResponse, true);
			mLivePresent.getHomeRecommendList(listResponse, true);
		}
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	AnimationDrawable mBellAnim;

	private void startBellAnim() {
		if (mBellAnim == null) {
			mBellAnim = AnimationDrawableUtil.createBellAnim(getContext());
		}
		iv_bell.setImageDrawable(mBellAnim);
		mBellAnim.start();
	}

	private void stopBellAnim() {
		if (mBellAnim == null) {
			mBellAnim = AnimationDrawableUtil.createBellAnim(getContext());
		}
		if (mBellAnim.isRunning()) {
			mBellAnim.stop();
		}
		iv_bell.setImageDrawable(mBellAnim.getFrame(0));
	}

	private Timer bellStatusTimer = new Timer();
	// private BellTimerTask bellStatusTask = null;

	// private class BellTimerTask extends TimerTask {
	//
	// @Override
	// public void run() {
	// if (!mBellAnim.isRunning()) {
	// DueMessageUtils.getInstance().bellStopVibration();
	// this.cancel();
	// }
	// }
	// }

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		listview.release();
	}

	@Override
	public void onDestroy() {
		DueMessageUtils.getInstance().deleteObserver(this);
		super.onDestroy();
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
		if (getParentFragment() instanceof MainFragment) {
			final MainFragment mFragment = (MainFragment) getParentFragment();
			if (mFragment.getPMFragmentStatus() != PMFragment.STATUS_IDLE || mFragment.getPMFragmentStatus() != PMFragment.STATUS_LOAD_FAIL) {
				mFragment.colsePm();
			}
			
			try {
				LiveInfo liveInfo = (LiveInfo) parent.getAdapter().getItem(position);
				if (liveInfo.type == LiveInfo.TYPE_LIVE) {
					//直播
					if((liveInfo.status==1) && (UserMgr.getInstance().getUid().equals(liveInfo.creator.uid))){
						//当前为直播，并且房间状态为1and 本人创建，直接进入
						NavigationController.gotoLiveAnchorFragment(getContext(), liveInfo);
					}else{
						NavigationController.gotoLiveAudienceFragment(getContext(), liveInfo);
					}
				} else {
					//回放
					NavigationController.gotoLiveReplayFragment(getContext(), liveInfo);
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		try {
			ObServerMessage ob = (ObServerMessage) data;
			switch (ob.type) {
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_START_BELL_ANIM:
				// startBellAnim();
				// if (bellStatusTask != null)
				// bellStatusTask.cancel();
				// bellStatusTask = new BellTimerTask();
				// bellStatusTimer.schedule(bellStatusTask, 0, 1000);
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_STOP_BELL_ANIM:
				stopBellAnim();
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_MESSAGE_COUNT:
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_home_bell_sum.setVisibility(View.VISIBLE);
					}
				});
				break;
			case ObServerMessage.OB_SERVER_MESSAGE_TYPE_HIDE_MESSAGE_COUNT:
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						tv_home_bell_sum.setVisibility(View.GONE);
					}
				});
				
				break;
			default:
				break;
			}

		} catch (Exception e) {
			Log.i("duemessage", "e : " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static class TickHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			HomeFragment frag = (HomeFragment) msg.obj;
			if (frag != null && frag.getActivity() != null) {
				frag.tick();
			}
		}
	}

	@Override
	public String getCurrentFragmentName() {
		return "HomeFragment";
	}

}
