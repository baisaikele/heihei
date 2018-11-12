package com.heihei.fragment.link;

import java.net.URLDecoder;

import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.base.host.ActivityManager;
import com.base.host.AppLogic;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.HistoryUtils;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.base.widget.toast.ChatToastHelper;
import com.heihei.dialog.MessageDialog;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.MainActivity;
import com.heihei.fragment.PMFragment;
import com.heihei.fragment.link.LinkUtil.LinkHost;
import com.heihei.logic.StatusController;
import com.heihei.logic.StatusController.OnCompleteListener;
import com.heihei.logic.present.PmPresent;
import com.heihei.login.SplashActivity;
import com.heihei.model.LiveInfo;
import com.heihei.model.User;

public class OutLinkActivity extends FragmentActivity {

	// ----------------R.layout.dialog_share_link-------------End
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.dealIntent();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.dealIntent();
	}

	private void dealIntent() {
		Intent intent = getIntent();
		if (HostApplication.getInstance().getMainActivity() != null) {
			if (intent.getData() != null) {
				final Uri uri = intent.getData();
				String host = uri.getHost();
				if (LinkHost.WEBVIEW.getHost().equalsIgnoreCase(host)) {
					String urll = uri.getQueryParameter("url");
					String type = uri.getQueryParameter("type");
					String desc = intent.getStringExtra("title");
					if (urll != null) {
						try {
							String realUrl = URLDecoder.decode(urll, "UTF-8");
							if (("1").equalsIgnoreCase(type)) {
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(realUrl));
								startActivity(i);
							} else {
								NavigationController.gotoWebView(this, realUrl, desc);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					finish();
					MainActivity.outIntent = null;

				} else if (LinkHost.LIVE.getHost().equalsIgnoreCase(host))// 直播间
				{
					handleLive(uri);
					// if (StatusController.getInstance().getCurrentStatus() !=
					// StatusController.STATUS_IDLE) {
					// StatusController.getInstance().showCompleteTip(new
					// OnCompleteListener() {
					//
					// @Override
					// public void onStopClick() {
					// handleLive(uri);
					// }
					//
					// @Override
					// public void onCancelClick() {
					//
					// }
					// });
					// } else {
					// handleLive(uri);
					// }

					finish();
					MainActivity.outIntent = null;
				} else if (LinkHost.MESSAGE.getHost().equalsIgnoreCase(host)) {
					Fragment frag = ActivityManager.getInstance().peek();
					if (frag != null && frag.getActivity() != null) {
						if (PMFragment.status == PMFragment.STATUS_LOAD_SUCCESS || PMFragment.status == PMFragment.STATUS_LOADING) {
							NavigationController.gotoMessageDialog(frag.getActivity(), null);
						} else {
							NavigationController.gotoMessageDialog(frag.getActivity(), null);
						}
					}
					finish();
					MainActivity.outIntent = null;
				} else if (LinkHost.HOMEPAGE.getHost().equalsIgnoreCase(host)) {
					NavigationController.gotoHomePageFragment(this);
					finish();
					MainActivity.outIntent = null;
				} else if (LinkHost.USER.getHost().equalsIgnoreCase(host)) {
					String uid = uri.getQueryParameter("id");
					User u = new User();
					u.uid = uid;
					if (!StringUtils.isEmpty(uid)) {

						Fragment frag = ActivityManager.getInstance().peek();
						if (frag != null && frag.getActivity() != null) {
							if (!clickOlder()) {
								return;
							}
							UserDialog ud = new UserDialog(frag.getActivity(), u, "", false, UserDialog.USERDIALOG_OPENTYPE_DEFAULE);
							ud.show();
						}
					}
					finish();
					MainActivity.outIntent = null;
				} else if (LinkHost.REPLAY.getHost().equalsIgnoreCase(host)) {
					String liveId = uri.getQueryParameter("id");
					LiveInfo mLiveInfo = new LiveInfo();
					mLiveInfo.liveId = liveId;
					NavigationController.gotoLiveReplayFragment(this, mLiveInfo);
					finish();
					MainActivity.outIntent = null;
				} else if (LinkHost.CHAT.getHost().equalsIgnoreCase(host)) {
					// NavigationController.gotoChatShowMsgFragment(this);
					// finish();
					// MainActivity.outIntent = null;

					if (StatusController.getInstance().getCurrentStatus() != StatusController.STATUS_IDLE) {
						StatusController.getInstance().showCompleteTip(new OnCompleteListener() {

							@Override
							public void onStopClick() {
								handleDueChat(uri);
							}

							@Override
							public void onCancelClick() {

							}
						});
					} else {
						handleDueChat(uri);
					}

					finish();
					MainActivity.outIntent = null;

				} else if (LinkHost.DUECHAT.getHost().equalsIgnoreCase(host)) {

					if (StatusController.getInstance().getCurrentStatus() != StatusController.STATUS_IDLE) {
						StatusController.getInstance().showCompleteTip(new OnCompleteListener() {

							@Override
							public void onStopClick() {
								handleDueChat(uri);
							}

							@Override
							public void onCancelClick() {

							}
						});
					} else {
						handleDueChat(uri);
					}

					finish();
					MainActivity.outIntent = null;
				} else {
					finish();
					MainActivity.outIntent = null;
				}

			}
		} else {
			MainActivity.outIntent = intent;
			Intent in = new Intent(this, SplashActivity.class);
			in.putExtra("share_type", "outLink");
			startActivity(in);
			finish();
		}
	}

	private long lastClickTime = 0;

	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}

	/**
	 * 处理直播push
	 * 
	 * @param uri
	 */
	private void handleLive(Uri uri) {
		String liveId = uri.getQueryParameter("id");
		if (!StringUtils.isEmpty(liveId)) {
			LiveInfo live = new LiveInfo();
			live.liveId = liveId;
			NavigationController.gotoLiveAudienceFragment(OutLinkActivity.this, live);
		}

		String msgId = uri.getQueryParameter("msgId");
	}

	/**
	 * 处理约聊
	 * 
	 * @param uri
	 */
	private void handleDueChat(Uri uri) {

		try {
			String chatIdStr = uri.getQueryParameter("chatId");
			final long chatId = Long.parseLong(chatIdStr);
			int type = 0;
			try {
				type = Integer.parseInt(uri.getQueryParameter("type"), 0);
			} catch (Exception e) {

			}

			final String nickname = uri.getQueryParameter("userName");

			int gender = User.MALE;
			try {
				gender = Integer.parseInt(uri.getQueryParameter("gender"));
			} catch (Exception e) {

			}

			final int sex = gender;
			String userId = uri.getQueryParameter("userId");
			String msgId = uri.getQueryParameter("msgId");

			final User user = new User();
			user.uid = userId;
			user.gender = sex;
			user.nickname = nickname;
			if (chatId > 0) {
				PmPresent.getInstance().getAcceptChat(new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							int expireTime = json.optInt("expireTime");
							ChatToastHelper.getInstance().showWaiting(expireTime, user, chatId);
						} else {
							UIUtils.showToast(msg);
						}
					}
				}, chatId, userId, type, msgId);
			} else {
				UIUtils.showToast("加入房间失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}