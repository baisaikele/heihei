package com.base.host;

import java.util.HashMap;
import java.util.List;

import com.base.utils.LogWriter;
import com.base.utils.ThreadManager;
import com.base.utils.location.AmapLocationManagerImpl;
import com.base.utils.location.LocationApi;
import com.heihei.fragment.MainActivity;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.logic.GiftController.OnGiftListGetListener;
import com.heihei.model.Gift;
import com.heihei.model.msg.ActionMessageDispatcher;
import com.heihei.model.msg.MessageDispatcher;
import com.heihei.model.msg.due.DueMessageUtils;
import com.heihei.scoket.MessageDistribute;
import com.mob.MobSDK;
import com.qiniu.pili.droid.rtcstreaming.RTCMediaStreamingManager;
import com.qiniu.pili.droid.streaming.StreamingEnv;

import android.R.integer;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.os.Handler;

public class HostApplication extends Application {

	private static HostApplication mApplication;
	private Handler mMainHandler = null;

	public static HostApplication getInstance() {
		return mApplication;
	}

	// private RefWatcher refWatcher;

	private MainActivity mMainActivity;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		MobSDK.init(this);
		try {
			AppLogic.init();
			/** 推流SDK初始化 */
			StreamingEnv.init(getApplicationContext());
			
			MessageDispatcher.getInstance();
			MessageDistribute.getInstance();
			ActionMessageDispatcher.getInstance();
			DueMessageUtils.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (AppLogic.DEBUG) {
		// refWatcher = LeakCanary.install(this);
		// }

	}

	// public RefWatcher getRefWatcher() {
	// return refWatcher;
	// }

	public Handler getMainHandler() {
		if (mMainHandler == null) {
			mMainHandler = new Handler(getMainLooper());
		}
		return mMainHandler;
	}

	public MainActivity getMainActivity() {
		return this.mMainActivity;
	}

	public void setMainActivity(MainActivity activity) {
		this.mMainActivity = activity;
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
	}

	@Override
	public void onTerminate() {
		DueMessageUtils.getInstance().onDestroy();
		super.onTerminate();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	public LocationApi createLocationApi(LocationApi.LocationMode locationMode) {
		LocationApi locationApi = null;
		switch (locationMode) {
		case AMAPLOCATION:
			locationApi = AmapLocationManagerImpl.getInstance(mApplication);
			break;
		default:
			break;
		}
		return locationApi;
	}
}
