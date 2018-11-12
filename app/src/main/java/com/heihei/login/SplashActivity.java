package com.heihei.login;

import java.util.ArrayList;
import java.util.List;

import com.base.host.AppLogic;
import com.base.host.BaseActivity;
import com.base.host.NavigationController;
import com.heihei.logic.UserMgr;
import com.igexin.sdk.PushManager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.wmlives.heihei.R;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

public class SplashActivity extends BaseActivity {

	private static final String TAG = SplashActivity.class.getSimpleName();
	private final String mPageName = "SplashActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		MobclickAgent.setDebugMode(AppLogic.BUMENG_DEBUG);//是否是集成测试环境
		MobclickAgent.openActivityDurationTrack(false);//禁止默认页面的统计
		MobclickAgent.setScenarioType(getApplicationContext(), EScenarioType.E_UM_NORMAL);

		PushManager.getInstance().initialize(getApplicationContext());

		ArrayList<String> permissions = new ArrayList<String>();

		permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);// 外部数据-读取
		permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);// 外部数据-写入
		permissions.add(Manifest.permission.READ_PHONE_STATE);// 读取手机信息

		doRequestPermissions(new PermissionRequestObj(permissions) {

			public void callback(boolean allGranted, List<String> permissionsList_denied, PermissionRequestObj pro) {
				if (allGranted) {
					init();
				} else {
					if (permissionsList_denied != null) {
						boolean storage = permissionsList_denied.contains(Manifest.permission.READ_EXTERNAL_STORAGE)
								|| permissionsList_denied.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE);
						// boolean location =
						// permissionsList_denied.contains(Manifest.permission.ACCESS_FINE_LOCATION)
						// &&
						// permissionsList_denied.contains(Manifest.permission.ACCESS_COARSE_LOCATION);

						if (!storage) {
							// 必要条件 必须有存储权限
							init();
						} else {
							String pstr = "";
							if (storage)
								pstr = "存储";
							pro.showManualSetupDialog(SplashActivity.this, pstr + "权限");
						}
					}
				}
			}

			public void onSettingCannel() {
				super.onSettingCannel();
				SplashActivity.this.finish();
				System.exit(0);
			}

			public void onSettingGoto() {
				super.onSettingGoto();
				SplashActivity.this.finish();
				System.exit(0);
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(getApplicationContext());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(getApplicationContext());
	}

	@Override
	protected String initFragmentClassName() {
		return "";
	}

	private void init() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				startNewActivity();
			}
		}, 3000);
	}

	private Handler handler = new Handler();

	private void startNewActivity() {
		
		MobclickAgent.onEvent(getApplicationContext(), mPageName);

		UserMgr.getInstance().loadLoginUser();
		if (UserMgr.getInstance().isLogined()) {
			if (UserMgr.getInstance().isNeedEditInfo()) {
				NavigationController.gotoCompleteInfoFragment(this);
				finish();
			} else {
				NavigationController.gotoMainFragment(this);
				finish();
			}
			return;
		}

		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
		finish();
	}

}
