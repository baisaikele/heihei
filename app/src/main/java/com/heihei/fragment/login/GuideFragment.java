package com.heihei.fragment.login;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.DeviceInfoUtils;
import com.base.utils.PackageUtils;
import com.base.utils.ShareSdkUtils;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.facebook.fresco.FrescoConfigConstants;
import com.heihei.dialog.LoadingDialog;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.BasePresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.heihei.websocket.WebSocketClient;
import com.mob.tools.utils.UIHandler;
import com.wmlives.heihei.R;
//import com.base.utils.WeiboUtils.WeiboCallback;
//import com.sina.weibo.sdk.exception.WeiboException;
//import com.base.utils.WeiboUtils;

public class GuideFragment extends BaseFragment implements OnClickListener, Callback, PlatformActionListener {

	private String TAG = "GuideFragment";
	// ----------------R.layout.fragment_guide-------------Start
	private TextView tv_tip;
	private LinearLayout ll_buttons;
	private View btn_login;
	private ImageView btn_wx;
	private ImageView btn_sina;
	private com.base.widget.IndicationView indicator;

	// public WeiboUtils mWeiboInstance;

	public void autoLoad_fragment_guide() {

		tv_tip = (TextView) findViewById(R.id.tv_tip);
		tv_tip.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		tv_tip.getPaint().setAntiAlias(true);// 抗锯齿
		ll_buttons = (LinearLayout) findViewById(R.id.ll_buttons);
		btn_login =  findViewById(R.id.btn_login);
		btn_wx = (ImageView) findViewById(R.id.btn_wx);
		btn_sina = (ImageView) findViewById(R.id.btn_sina);
		indicator = (com.base.widget.IndicationView) findViewById(R.id.indicator);
	}

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_guide);

	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_guide();
		btn_login.setOnClickListener(this);
		btn_wx.setOnClickListener(this);
		btn_sina.setOnClickListener(this);
		tv_tip.setOnClickListener(this);
	}

	@Override
	protected void refresh() {

		UserMgr.getInstance().loadLoginUser();



		if (UserMgr.getInstance().isLogined()) {
			if (UserMgr.getInstance().isNeedEditInfo()) {
				NavigationController.gotoCompleteInfoFragment(getContext());
				// getActivity().finish();
			} else {
				NavigationController.gotoMainFragment(getContext());
			}
			return;
		}

		Message msg = Message.obtain();
		msg.obj = this;
		msg.what = 0;
		mHandler.sendMessageDelayed(msg, 2000);

	}

	private LoadingDialog ld = null;

	private UserPresent userPresent = new UserPresent();

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login:
			startFragment(LoginFragment.class, null);
			break;
		case R.id.btn_wx:
			if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				if (ld == null) {
					ld = new LoadingDialog(getActivity());
				}
				if (!ld.isShowing()) {
					ld.show();
				}

				authorize(new Wechat());

			} else
				UIUtils.showToast(R.string.share_wechat_no_avliible);
			break;
		case R.id.btn_sina:
			if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WEIBO))
				authorize(new Facebook());
			else
				authorizeNotInstallWeiboClient(new Facebook());
			break;
		case R.id.tv_tip:
			NavigationController.gotoWebView(getContext(), userPresent.getServiceTerms(), null);
			break;
		}

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}

	@Override
	public void onResume() {
		super.onResume();
		if (ld != null && ld.isShowing()) {
			ld.dismiss();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void tick() {
		mHandler.removeMessages(0);


		if (!isDetached()) {
			Message msg = Message.obtain();
			msg.obj = this;
			msg.what = 0;
			mHandler.sendMessageDelayed(msg, 2000);
		}
	}

	private TickHandler mHandler = new TickHandler();

	private static class TickHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			GuideFragment frag = (GuideFragment) msg.obj;
			if (frag != null && frag.getActivity() != null) {
				frag.tick();
			}
		}

	}

	// ------------------ShareSdk OpenLogin fuction---------------start
	private void authorize(Platform plat) {
		// if (plat.isValid()) {
		// plat.removeAccount();
		// String userId = plat.getDb().getUserId();
		// if (!TextUtils.isEmpty(userId)) {
		// UIHandler.sendEmptyMessage(ShareSdkUtils.MSG_USERID_FOUND, this);
		// login(plat.getName(), userId, null);
		// return;
		// }
		// }
		plat.removeAccount(true);
		plat.setPlatformActionListener(this);
		plat.SSOSetting(false);
		plat.authorize();
		plat.showUser(null);
	}

	private void authorizeNotInstallWeiboClient(Platform plat) {
		plat.removeAccount(true);
		plat.setPlatformActionListener(this);
		plat.SSOSetting(false);
		plat.showUser(null);
	}

	public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (ld != null) {
						ld.dismiss();
					}
				}
			});
		}

		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(ShareSdkUtils.MSG_AUTH_COMPLETE, this);
			// login(platform.getName(), platform.getDb().getUserId(), res);
			String platName = platform.getName();
			String openId = platform.getDb().getUserId();
			String token = platform.getDb().getToken();
			String unionid = "";
			if (res != null) {
				unionid = (String) res.get("unionid");
			}

			login(platName, openId, token, unionid);

		}
		Log.i(TAG, "------User Name ---------" + platform.getDb().getUserName());
		Log.i(TAG, "------User ID ---------" + platform.getDb().getUserId());
	}

	public void onError(Platform platform, int action, Throwable t) {
		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (ld != null) {
						ld.dismiss();
					}
				}
			});
		}

		Log.i(TAG, "onError : " + t.getMessage());
		if (action == Platform.ACTION_USER_INFOR)
			UIHandler.sendEmptyMessage(ShareSdkUtils.MSG_AUTH_ERROR, this);
		t.printStackTrace();
	}

	public void onCancel(Platform platform, int action) {

		if (getActivity() != null) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (ld != null) {
						ld.dismiss();
					}
				}
			});
		}

		Log.i(TAG, "onCancel");
		if (action == Platform.ACTION_USER_INFOR)
			UIHandler.sendEmptyMessage(ShareSdkUtils.MSG_AUTH_CANCEL, this);
	}

	private UserPresent mUserPresent = new UserPresent();

	private void login(String plat, String openid, String token, String unionid) {
		if (plat.equals(Wechat.NAME)) {
			mUserPresent.loginByWechat(token, openid, unionid, loginResponse);
		} else if (plat.equals(Facebook.NAME)) {
			mUserPresent.loginByWeibo(token, openid, unionid, loginResponse);
		}
	}

	private JSONResponse loginResponse = new JSONResponse() {

		@Override
		public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
			if (errCode == ErrorCode.ERROR_OK) {
				String token = json.optString("token");
				if (StringUtils.isEmpty(token)) {
					UIUtils.showToast(getString(R.string.login_fail_try_retry));
					return;
				}

				User user = new User();
				user.token = token;
				user.jsonParseUserDetails(json.optJSONObject("userInfo"));
				UserMgr.getInstance().setLoginUser(user);
				UserMgr.getInstance().saveLoginUser();

				boolean isRegister = json.optBoolean("needRegist");// 是否是第一次注册
				if (isRegister || StringUtils.isEmpty(user.nickname) || StringUtils.isEmpty(user.birthday)
						|| user.gender == User.UNSPECIFIED) {
					NavigationController.gotoCompleteInfoFragment(getContext());
				} else {
					try {
						UIUtils.showToast(getString(R.string.login_success));
						NavigationController.gotoMainFragment(getContext());
					} catch (IllegalStateException e) {
						e.printStackTrace();
					}
				}

				new Thread() {
					public void run() {
						try {
							BasePresent.requestInitUrls();// 请求服务器初始化地址
						} catch (Exception e) {
							e.printStackTrace();
						}
					};
				}.start();

			} else {
				UIUtils.showToast(msg);
			}

		}
	};

	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case ShareSdkUtils.MSG_USERID_FOUND:
			Log.i(TAG, "-------MSG_USERID_FOUND--------");
			break;
		case ShareSdkUtils.MSG_LOGIN:
			Log.i(TAG, "------MSG_LOGIN---------");
			break;
		case ShareSdkUtils.MSG_AUTH_CANCEL:
			Log.i(TAG, "-------MSG_AUTH_CANCEL--------");
			UIUtils.showToast("获取权限失败");
			break;
		case ShareSdkUtils.MSG_AUTH_ERROR:
			Log.i(TAG, "-------MSG_AUTH_ERROR--------");
			break;
		case ShareSdkUtils.MSG_AUTH_COMPLETE:
			Log.i(TAG, "--------MSG_AUTH_COMPLETE-------");
			break;
		}
		return false;
	}
	// ------------------ShareSdk OpenLogin fuction---------------end

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "GuideFragment";
	}
}
