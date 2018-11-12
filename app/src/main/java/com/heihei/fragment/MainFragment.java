package com.heihei.fragment;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.base.host.AppLogic;
import com.base.host.BaseActivity;
import com.base.host.BaseActivity.PermissionRequestObj;
import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.ThreadManager;
import com.base.utils.UIUtils;
import com.base.utils.location.LocationApi;
import com.base.utils.location.LocationApi.LocationCallback;
import com.base.utils.location.LocationApi.LocationMode;
import com.base.utils.location.PerfectLocation;
import com.base.utils.update.APKUpdateManager;
import com.base.utils.update.FileDownloadManager;
import com.base.utils.update.UpdateUtils;
import com.base.utils.update.receivers.ApkInstallReceiver;
import com.base.widget.tabhost.main.MainTabHost.OnCheckedChangeListener;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.LoadingDialog;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.logic.StatusController;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.UpdateInfo;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.heihei.websocket.WebSocketClient;
import com.wmlives.heihei.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 主页
 * 
 * @author chengbo
 */
public class MainFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener, Observer {

	// ----------------R.layout.fragment_main-------------Start
	private LinearLayout tabs;
	private com.base.widget.tabhost.main.MainTabHost tab_host;
	private FrameLayout container;

	public void autoLoad_fragment_main() {
		tabs = (LinearLayout) findViewById(R.id.tabs);
		tab_host = (com.base.widget.tabhost.main.MainTabHost) findViewById(R.id.tab_host);
		container = (FrameLayout) findViewById(R.id.container);
		giftContentView = (FrameLayout) findViewById(R.id.gift_contentView);
	}

	public FrameLayout getGiftContentView() {
		return this.giftContentView;
	}

	// ----------------R.layout.fragment_main-------------End

	public enum HostTitle {
		HT_PM, // 私聊
		HT_HOME, // 首页
		HT_MINE, // 我
	}

	public void colsePm() {
		if (child_pm != null) {
			child_pm.close();
		}
	}

	private int checkedPosition = -1;

	private FragmentManager mFragmentManager;
	private PMFragment child_pm;
	private HomeFragment child_home;
	private MeFragment child_me;
	private BaseFragment lastFragment;

	private FrameLayout giftContentView;

	@Override
	protected void loadContentView() {
		mFragmentManager = getChildFragmentManager();
		setContentView(R.layout.fragment_main);

		update();

		try {
			Intent intent = new Intent(HostApplication.getInstance(), WebSocketClient.class);
			getActivity().stopService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Intent intent = new Intent(HostApplication.getInstance(), WebSocketClient.class);
			getActivity().startService(intent);
		}

		ThreadManager.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				AppLogic.initGift();
			}
		});
	}

	public void setHostChecked(HostTitle title, ObServerMessage message) {
		switch (title) {
		case HT_PM:
			tab_host.setChecked(HostTitle.HT_PM.ordinal());
			if (child_pm != null) {
				child_pm.setPmChatInfo(message);
			}
			break;
		case HT_HOME:
			tab_host.setChecked(HostTitle.HT_HOME.ordinal());

			break;
		case HT_MINE:
			tab_host.setChecked(HostTitle.HT_MINE.ordinal());
			break;
		default:
			break;
		}
	}

	public int getPMFragmentStatus() {
		if (child_pm != null) {
			return child_pm.status();
		}
		return -1;
	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_main();

		if (UserMgr.getInstance().getLoginUser().gender == User.FEMALE) {
			tab_host.setImageDrawable(HostTitle.HT_MINE.ordinal(), getResources().getDrawable(R.drawable.home_tab_btn_bg_selector_female));
		} else {
			tab_host.setImageDrawable(HostTitle.HT_MINE.ordinal(), getResources().getDrawable(R.drawable.home_tab_btn_bg_selector_male));
		}

		tab_host.setOnCheckedChangeListener(this);
		tab_host.setChecked(HostTitle.HT_PM.ordinal());
		tab_host.setChecked(HostTitle.HT_HOME.ordinal());

		EventManager.ins().registListener(EventTag.START_CHAT, mChatListener);
		EventManager.ins().registListener(EventTag.CHAT_WITH_SHOW_MESSAGE, mChatListener);

	}

	@Override
	protected void refresh() {
		if (UserMgr.getInstance().isLogined()) {
			UserMgr.getInstance().requestMineInfo(null);
		}
		/** 请求下礼物数据，缓存下 */
		GiftController.getInstance().requestGiftList(null);
		// AVController.createInstance().reqeustChatUser();
		checkCity();
	}

	private LocationApi mLocationApi;

	private void checkCity() {
		mLocationApi = HostApplication.getInstance().createLocationApi(LocationMode.AMAPLOCATION);

		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		((BaseActivity) getActivity()).doRequestPermissions(new PermissionRequestObj(permissions) {

			@Override
			public void callback(boolean allGranted, List<String> permissionsList_denied, PermissionRequestObj pro) {
				if (allGranted) {
					mLocationApi.startLocation(mLocationCallback);
				} else {
					mLocationApi.stopLocation();
				}
			}
		});
	}

	private String cityName = "";
	private String latlng = "";

	private LocationCallback mLocationCallback = new LocationCallback() {

		@Override
		public void locationSuccess(PerfectLocation location) {
			try {
				LogWriter.i("jianfei", "location " + location.toString());
				cityName = location.cityName;
				latlng = location.latitude + "," + location.longitude;
				mLocationApi.stopLocation();
				User user = UserMgr.getInstance().getLoginUser();
				if (!user.address.equals(cityName)) {
					new UserPresent().updateUserInfo(user.nickname, user.sign, user.gender, user.birthday, cityName, latlng, null);
				}

			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void locationFailed() {
			try {
				// locationImg.setImageResource(R.drawable.hh_mylocation_location_n);
				// locationCity.setText(getText(R.string.user_location_fail));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	};

	private EventListener mChatListener = new EventListener() {

		@Override
		public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
			switch (what) {
			case EventTag.START_CHAT:
				tab_host.setChecked(HostTitle.HT_PM.ordinal());
				break;
			case EventTag.CHAT_WITH_SHOW_MESSAGE:
				tab_host.setChecked(HostTitle.HT_PM.ordinal());
				break;
			}

		}
	};

	@Override
	public void onClick(View v) {

	}

	public PMFragment getPmFragment() {
		return child_pm;
	}

	@Override
	public void onCheckedChange(int checkedPosition, boolean byUser) {
		if (this.checkedPosition == checkedPosition) {
			// 点击当前页面
			return;
		}
		this.checkedPosition = checkedPosition;

		if (child_pm != null) {
			switch (checkedPosition) {
			case 0:
				child_pm.setCaptureStatus(true);
				break;
			case 1:
				child_pm.setCaptureStatus(false);
				break;
			case 2:
				child_pm.setCaptureStatus(false);
				break;
			default:
				break;
			}
		}
		// child_pm.setCaptureStatus(true);
		Log.i("mRTCStreamingManager", "checkedPosition " + checkedPosition);
		FragmentTransaction beginTransaction = mFragmentManager.beginTransaction();
		if (checkedPosition == HostTitle.HT_HOME.ordinal())// 首页
		{
			DueMessageUtils.getInstance().setStopPmStatus();
			if (child_home == null) {
				child_home = new HomeFragment();
				beginTransaction.add(R.id.container, child_home);
			} else {
				beginTransaction.show(child_home);
			}
			if (lastFragment != null) {
				beginTransaction.hide(lastFragment);
			}
			lastFragment = child_home;
			beginTransaction.commitAllowingStateLoss();
		} else if (checkedPosition == HostTitle.HT_PM.ordinal())// 私聊
		{
			if (child_pm == null) {
				child_pm = new PMFragment();
				beginTransaction.add(R.id.container, child_pm);
			} else {
				beginTransaction.show(child_pm);
			}
			if (lastFragment != null) {
				beginTransaction.hide(lastFragment);
			}
			lastFragment = child_pm;
			beginTransaction.commitAllowingStateLoss();
		} else if (checkedPosition == HostTitle.HT_MINE.ordinal())// 我
		{
			DueMessageUtils.getInstance().setStopPmStatus();
			if (child_me == null) {
				child_me = new MeFragment();
				beginTransaction.add(R.id.container, child_me);
			} else {
				beginTransaction.show(child_me);
			}
			if (lastFragment != null) {
				beginTransaction.hide(lastFragment);
			}
			lastFragment = child_me;
			beginTransaction.commitAllowingStateLoss();
		}
	}

	long last_back_time = 0;

	@Override
	protected boolean canBack() {

		long current_time = System.currentTimeMillis();
		if (current_time - last_back_time > 2000) {
			UIUtils.showToast(getString(R.string.app_exit_dialog));
			last_back_time = current_time;
			return false;
		} else {
			return true;
		}

		// return super.canBack();
	}

	@Override
	public void onResume() {
		super.onResume();
		apkUpdateUpdate();
	}

	private void update() {
		try {
			PackageInfo packageInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);

			LivePresent present = new LivePresent();
			present.getSystemUpdate(new JSONResponse() {
				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (errCode != ErrorCode.ERROR_OK) {
						try {
							UpdateInfo info = UpdateInfo.toUpdateInfo(json);
							LogWriter.i("BaseActivityUpdate", "info " + info.toString());
							updateInformation(info.force, info.url, info.title, info.fileName, info.text);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}, packageInfo.versionName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LoadingDialog ld = null;

	private void updateInformation(final Boolean type, final String url, final String title, final String fileName, String text) {
		final TipDialog updateDialog = new TipDialog(getActivity());
		updateDialog.setTitle(getText(R.string.update_information_title));

		if (type) {
			updateDialog.setBtnCancelVisibity(View.GONE);
		}

		updateDialog.setContent(text);
		updateDialog.setBtnOKText(getText(R.string.update_information_ok));
		updateDialog.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

			@Override
			public void onOkClick(Dialog dialog) {
				UpdateUtils.download(getContext(), url, title, fileName);
				if (type) {
					if (ld == null)
						ld = new LoadingDialog(getActivity());
					if (!ld.isShowing())
						ld.show();
				}
			}

			@Override
			public void onCancleClick(Dialog dialog) {
				updateDialog.dismiss();
			}
		});
		updateDialog.show();
	}

	private void apkUpdateUpdate() {
		LogWriter.i("BaseActivityUpdate", "onResume");
		long downloadId = SharedPreferencesUtil.getInstance().get(APKUpdateManager.KEY_DOWNLOAD_ID, -1L);
		LogWriter.i("BaseActivityUpdate", "downloadId " + downloadId);
		if (downloadId != -1L) {
			LogWriter.i("BaseActivityUpdate", "downloadId != -1L");
			FileDownloadManager fdm = FileDownloadManager.getInstance(getContext());
			int status = fdm.getDownloadStatus(downloadId);
			if (status == DownloadManager.STATUS_SUCCESSFUL) {
				// 启动更新界面
				LogWriter.i("BaseActivityUpdate", "DownloadManager.STATUS_SUCCESSFUL");
				String path = getFilePath(downloadId);
				if (path != null) {
					LogWriter.i("BaseActivityUpdate", "uri != null");
					if (APKUpdateManager.compare(APKUpdateManager.getApkInfo(getContext(), path), getContext())) {
						ApkInstallReceiver.installApk(getContext(), downloadId);
						return;
					} else {
						LogWriter.i("BaseActivityUpdate", "fdm.getDm().remove(downloadId)");
						fdm.getDm().remove(downloadId);
						SharedPreferencesUtil.getInstance().delete(APKUpdateManager.KEY_DOWNLOAD_ID);
					}
				}
			} else if (status == DownloadManager.STATUS_FAILED) {
				fdm.getDm().remove(downloadId);
				SharedPreferencesUtil.getInstance().delete(APKUpdateManager.KEY_DOWNLOAD_ID);
			}
		}
	}

	private String getFilePath(long downloadId) {
		DownloadManager dManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
		Cursor cursor = dManager.query(new DownloadManager.Query().setFilterById(downloadId));
		if (cursor == null) {
			return null;
		}
		cursor.moveToFirst();
		String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));

		return path;
	}

	@Override
	public void onPause() {
		DueMessageUtils.getInstance().setStopPmStatus();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventManager.ins().removeListener(EventTag.START_CHAT, mChatListener);
		EventManager.ins().removeListener(EventTag.CHAT_WITH_SHOW_MESSAGE, mChatListener);
		// AVController.createInstance().stopAVContext();
		StatusController.getInstance().resetStatus();
	}

	@Override
	public void update(Observable observable, Object data) {

	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "MainFragment";
	}

	// /**
	// * 这段可以解决fragment嵌套fragment会崩溃的问题
	// */
	// @Override
	// public void onDetach() {
	// super.onDetach();
	// try {
	// // 参数是固定写法
	// Field childFragmentManager =
	// Fragment.class.getDeclaredField("mChildFragmentManager");
	// childFragmentManager.setAccessible(true);
	// childFragmentManager.set(this, null);
	// } catch (NoSuchFieldException e) {
	// throw new RuntimeException(e);
	// } catch (IllegalAccessException e) {
	// throw new RuntimeException(e);
	// }
	// }
}
