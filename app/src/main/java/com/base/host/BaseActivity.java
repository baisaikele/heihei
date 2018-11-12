package com.base.host;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.base.widget.CustomInsetFrameLayout;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.MainFragment;
import com.heihei.fragment.MainFragment.HostTitle;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.umeng.analytics.MobclickAgent;
import com.wmlives.heihei.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class BaseActivity extends HostActivity implements Observer {

	public static final String FRAGMENT_CLASS_NAME = "fragment_class_name";
	public static final String FRAGMENT_PARAMS_KEY = "framgment_params_key";

	private CustomInsetFrameLayout mFrameLayout;

	protected Fragment mMainFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initFragment();
		DueMessageUtils.getInstance().addObserver(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		ActivityManager.getInstance().bountToTop(getMainFragment());
	}

	protected void initFragment() {
		UIUtils.setTransparentStatus(this);

		mFrameLayout = new CustomInsetFrameLayout(this);
		mFrameLayout.setId(android.R.id.primary);
		mFrameLayout.setFitsSystemWindows(true);
		setContentView(mFrameLayout);
		if (getIntent() != null) {
			String className = getIntent().getStringExtra(FRAGMENT_CLASS_NAME);
			if (TextUtils.isEmpty(className)) {
				className = initFragmentClassName();
			}

			if (StringUtils.isEmpty(className)) {
				return;
			}

			try {
				Class<?> clazz = Class.forName(className);
				mMainFragment = (BaseFragment) clazz.newInstance();
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.add(android.R.id.primary, mMainFragment);
				ft.commit();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (getMainFragment() != null) {
			ActivityManager.getInstance().push(getMainFragment());
		}

	}

	protected String initFragmentClassName() {
		return null;
	}

	public Fragment getMainFragment() {
		return mMainFragment;
	}

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		fixInputMethodManagerLeak(this);
		if (getMainFragment() != null) {
			ActivityManager.getInstance().remove(getMainFragment());
		}
		DueMessageUtils.getInstance().deleteObserver(this);
	}

	@Override
	public void onBackPressed() {
		if (getMainFragment() != null) {
			Object obj = RefInvoke.invokeMethod(BaseFragment.class.getName(), "canBack", getMainFragment(), null, null);
			if (obj != null && obj instanceof Boolean) {
				if ((boolean) obj) {
					super.onBackPressed();
				}
			} else {
				super.onBackPressed();
			}
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (mMainFragment != null) {
			mMainFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * 解决InputMethodManager的内存泄露问题
	 * 
	 * @param destContext
	 */
	public static void fixInputMethodManagerLeak(Context destContext) {
		if (destContext == null) {
			return;
		}

		InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm == null) {
			return;
		}

		String[] arr = new String[] { "mCurRootView", "mServedView", "mNextServedView" };
		Field f = null;
		Object obj_get = null;
		for (int i = 0; i < arr.length; i++) {
			String param = arr[i];
			try {
				f = imm.getClass().getDeclaredField(param);
				if (f.isAccessible() == false) {
					f.setAccessible(true);
				} // author: sodino mail:sodino@qq.com
				obj_get = f.get(imm);
				if (obj_get != null && obj_get instanceof View) {
					View v_get = (View) obj_get;
					if (v_get.getContext() == destContext) { // 被InputMethodManager持有引用的context是想要目标销毁的
						f.set(imm, null); // 置空，破坏掉path to gc节点
					} else {
						// 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
						// if (QLogWriter.isColorLevel()) {
						// QLog.d(ReflecterHelper.class.getSimpleName(),
						// QLog.CLR, "fixInputMethodManagerLeak break, context
						// is not suitable, get_context=" + v_get.getContext()+"
						// dest_context=" + destContext);
						// }
						break;
					}
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	// ANDROID
	// 6.0（SDK23）开始手动申请权限----------------------------------------------------------------------Start

	/** 权限正在申请中 */
	public static boolean permissionRequesting = false;

	final static int PERMISSIONS_REQUEST_CODE_ACTIVITY = 0x01;

	PermissionRequestObj currentPermissionRequest;

	/** 权限申请 */
	public void doRequestPermissions(PermissionRequestObj pro) {
		if (Build.VERSION.SDK_INT < 23) {
			if (pro != null) {
				pro.callback(true, null, pro);
			}
			return;
		}

		// 需要请求的权限列表
		final List<String> permissions_NeedRequest = new ArrayList<String>();
		// 用户设置不在提醒的权限列表 需要给出提示
		final List<String> permissions_NeedTip = new ArrayList<String>();

		for (int i = 0; i < pro.size(); i++) {
			String permission = pro.get(i);
			if (!filterPermission(permissions_NeedRequest, permission, pro)) {
				// 添加到需要提示内容中
				permissions_NeedTip.add(permission);
			}
		}

		if (permissions_NeedRequest.size() > 0) {

			// if (permissions_NeedTip.size() > 0) {
			// permissionRequesting = false;
			// // 请求权限之前 显示dialog 告知用户用权限的原因
			// showPermissionTipDialog(pro.makeReasonTips(permissions_NeedTip),
			// new View.OnClickListener() {
			//
			// public void onClick(View view) {
			// switch (view.getId()) {
			// case R.id.ok : {
			// // ok确定后 重新请求权限
			// permissionRequesting = true;
			// ActivityCompat.requestPermissions(BaseActivity.this,
			// permissions_NeedRequest.toArray(new
			// String[permissions_NeedRequest.size()]),
			// PERMISSIONS_REQUEST_CODE_ACTIVITY);
			// }
			// break;
			// case R.id.cancel :
			// // 取消
			// try {
			// if (currentPermissionRequest != null)
			// currentPermissionRequest.callback(false, null);
			// currentPermissionRequest = null;
			// } catch (Exception e) {
			// LogUtils.e("PermissionRequest", "Callback Exception tipcancel",
			// e);
			// }
			// break;
			// }
			//
			// }
			// });
			// currentPermissionRequest = pro;
			// return;
			// }

			permissionRequesting = true;
			currentPermissionRequest = pro;
			ActivityCompat.requestPermissions(BaseActivity.this, permissions_NeedRequest.toArray(new String[permissions_NeedRequest.size()]), PERMISSIONS_REQUEST_CODE_ACTIVITY);
			return;
		}

		// do something
		currentPermissionRequest = null;
		permissionRequesting = false;
		try {
			pro.callback(true, null, pro);
		} catch (Exception e) {
			LogWriter.e("PermissionRequest", "Callback Exception");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 过滤权限 是否已经授权，没有授权的添加到请求列表中
	 * 
	 * @param permissions_NeedRequest
	 *            需要请求的权限列表
	 * @param permission
	 *            权限
	 * @param pro
	 * @return true 请求权限就好了；false 用户设置了不在提醒 需要给出提示
	 */
	private boolean filterPermission(List<String> permissions_NeedRequest, String permission, PermissionRequestObj pro) {
		if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
			permissions_NeedRequest.add(permission);// 没有权限的添加到需要申请的列表中
			if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission))
				return false;
		} else {
			pro.authItem(permission);
		}
		return true;
	}

	private void showPermissionTipDialog(CharSequence message, View.OnClickListener clickListener) {
		// Dialog dlg = MyDialog.showTipsDialogs(this, message, "取消", "确定",
		// clickListener);
		// dlg.setCancelable(false);
		// dlg.setCanceledOnTouchOutside(false);
	}

	@SuppressLint("Override")
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// 权限请求的返回结果
		permissionRequesting = false;
		switch (requestCode) {
		case PERMISSIONS_REQUEST_CODE_ACTIVITY: {
			try {
				if (currentPermissionRequest != null) {
					for (int i = 0; i < permissions.length; i++) {
						int r = grantResults[i];
						if (r == PackageManager.PERMISSION_GRANTED) {
							String p = permissions[i];
							currentPermissionRequest.authItem(p);
						}
					}
					boolean AllGranted = currentPermissionRequest.isAllGranted();
					currentPermissionRequest.isAsyn = true;
					currentPermissionRequest.callback(AllGranted, currentPermissionRequest.permissionsList_denied, currentPermissionRequest);
				}
			} catch (Exception e) {
				LogWriter.e("PermissionRequest", "Callback Exception onRequestPermissionsResult");
			}
		}
			break;
		default:
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	public static abstract class PermissionRequestObj {

		// 是否是异步执行
		public boolean isAsyn = false;

		List<String> permissionsList;
		Map<String, Integer> perms = new HashMap<String, Integer>();

		// 请求结果 没有通过的权限列表
		List<String> permissionsList_denied;

		public PermissionRequestObj(List<String> permissionsList) {
			this.permissionsList = permissionsList;
			for (String p : permissionsList) {
				perms.put(p, PackageManager.PERMISSION_DENIED);
			}
		}

		public int size() {
			return permissionsList.size();
		}

		public String get(int index) {
			return permissionsList.get(index);
		}

		private void authItem(String permission) {
			perms.put(permission, PackageManager.PERMISSION_GRANTED);
		}

		/**
		 * 判断是否所有权限申请都通过了
		 * 
		 * @return
		 */
		private boolean isAllGranted() {
			boolean ret = true;
			for (String p : permissionsList) {
				int auth = perms.get(p);
				if (auth != PackageManager.PERMISSION_GRANTED) {

					ret = false;

					if (permissionsList_denied == null)
						permissionsList_denied = new ArrayList<String>();
					permissionsList_denied.add(p);
				}
			}
			return ret;
		}

		public CharSequence makeReasonTips(List<String> permissions_NeedTip) {
			String appname = HostApplication.getInstance().getResources().getString(R.string.app_name);
			String ret = "您的手机已禁用了当前需要使用的功能权限，请到手机“<font color=#ff0000>设置--权限管理</font>”中设置找到\"<font color=#ff0000>" + appname
					+ "</font>\",并设置“<font color=#ff0000>信任此应用</font>”后点确定。";
			return Html.fromHtml(ret);
		}

		/**
		 * 获取权限失败之后 显示手动设置的Dialog 引导用户去系统设置里设置权限
		 */
		public void showManualSetupDialog(final Activity activity, String permission) {
			String appname = HostApplication.getInstance().getResources().getString(R.string.app_name);
			String msg = "请在<font color=#ff0000>设置</font>-<font color=#ff0000>应用</font>-<font color=#ff0000>" + appname + "</font>-<font color=#ff0000>权限</font>中开启" + permission
					+ "，否则将无法正常使用。";

			TipDialog td = new TipDialog(activity);
			td.setContent(Html.fromHtml(msg));
			td.setBtnOKText("去设置");
			td.setBtnCancelText("取消");
			td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

				public void onOkClick(Dialog dialog) {
					// ok确定后 引导用户去设置
					Uri packageURI = Uri.parse("package:" + activity.getPackageName());
					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
					activity.startActivity(intent);
					onSettingGoto();
				}

				public void onCancleClick(Dialog dialog) {
					onSettingCannel();
				}
			});
			td.show();

		}

		/**
		 * 权限申请的结果回调
		 * 
		 * @param AllGranted
		 *            是否所有请求的权限都获取到了
		 * @param permissionsList_denied
		 *            没有被授权的权限列表
		 */
		public abstract void callback(boolean allGranted, List<String> permissionsList_denied, PermissionRequestObj pro);

		public void onSettingCannel() {
		}

		public void onSettingGoto() {
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		ObServerMessage obServerMessage = (ObServerMessage) data;
		switch (obServerMessage.type) {
		case ObServerMessage.OB_SERVER_MESSAGE_TYPE_JOIN_ROOM:
			LogWriter.i("ObServerMessage", "BaseActivity   OB_SERVER_MESSAGE_TYPE_JOIN_ROOM");
			Fragment f = ActivityManager.getInstance().peek();
			if (!(f instanceof MainFragment)) {
				LogWriter.i("ObServerMessage", "BaseActivity  ! instanceof MainFragment");
				if (obServerMessage != null && obServerMessage.chatInfo != null && obServerMessage.chatInfo.user != null) {
					NavigationController.gotoChatFragment(BaseActivity.this, obServerMessage);
				}
				// if ((!(f instanceof LiveAnchorFragment)) && obServerMessage
				// != null && obServerMessage.chatInfo != null &&
				// obServerMessage.chatInfo.user != null) {
				// NavigationController.gotoChatFragment(BaseActivity.this,
				// obServerMessage);
				// }
			} else if (ActivityManager.getInstance().peek() instanceof MainFragment) {

				MainFragment mainFragment = (MainFragment) ActivityManager.getInstance().peek();
				mainFragment.setHostChecked(HostTitle.HT_PM, obServerMessage);
			}
			break;
		default:
			break;
		}
	}

	public Fragment getVisibleFragment(List<Fragment> fragments) {
		for (Fragment fragment : fragments) {
			if (fragment != null && fragment.isVisible())
				return fragment;
		}
		return null;
	}

	// ANDROID
	// 6.0（SDK23）开始手动申请权限----------------------------------------------------End


}
