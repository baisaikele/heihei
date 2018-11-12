package com.heihei.fragment.live;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.base.host.BaseActivity;
import com.base.host.BaseActivity.PermissionRequestObj;
import com.base.host.BaseFragment;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.KeyBoardUtil;
import com.base.utils.LogWriter;
import com.base.utils.PackageUtils;
import com.base.utils.ShareSdkUtils;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.heihei.fragment.PMFragment;
import com.heihei.logic.present.LivePresent;
import com.heihei.model.LiveInfo;
import com.heihei.model.ShareInfo;
import com.wmlives.heihei.R;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 发起直播
 * 
 * @author admin
 *
 */
public class LivePrepareFragment extends BaseFragment implements OnClickListener {

	public static final int REQUEST_CODE = 1001;

	public static final String TYPE_CREATE = "create_live";
	public static final String TYPE_UPDATE_TOPIC = "update_topic";

	// ----------------R.layout.fragment_live_start-------------Start
	private RelativeLayout titlebar;// include R.layout.title_bar
	private LinearLayout ll_left;// include R.layout.title_bar
	private ImageButton iv_back;// include R.layout.title_bar
	private TextView tv_back;// include R.layout.title_bar
	private LinearLayout ll_right;// include R.layout.title_bar
	private ImageButton iv_right;// include R.layout.title_bar
	private TextView tv_right;// include R.layout.title_bar
	private LinearLayout ll_mid;// include R.layout.title_bar
	private TextView tv_title;// include R.layout.title_bar
	private EditText et_topic;
	private ImageView btn_sina;
	private ImageView btn_wx;
	private ImageView btn_wx_friend;
	private TextView tv_share_tip;
	private Button btn_start;

	public void autoLoad_fragment_live_start() {
		titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
		ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
		iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
		tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
		ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
		iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
		tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
		ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
		tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
		et_topic = (EditText) findViewById(R.id.et_topic);
		btn_sina = (ImageView) findViewById(R.id.btn_sina);
		btn_wx = (ImageView) findViewById(R.id.btn_wx);
		btn_wx_friend = (ImageView) findViewById(R.id.btn_wx_friend);
		tv_share_tip = (TextView) findViewById(R.id.tv_share_tip);
		btn_start = (Button) findViewById(R.id.btn_start);

		if (PMFragment.mRTCMediaStreamingManagerWeakReference.get() != null) {
			PMFragment.mRTCMediaStreamingManagerWeakReference.get().stopCapture();
		}
	}

	// ----------------R.layout.fragment_live_start-------------End

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_live_start);

	}

	@Override
	protected String initTitle() {
		return getString(R.string.user_create_live);
	}

	@Override
	protected void viewDidLoad() {

		mLive = (LiveInfo) mViewParam.data;

		autoLoad_fragment_live_start();
		et_topic.addTextChangedListener(watcher);
		btn_sina.setOnClickListener(this);
		btn_wx.setOnClickListener(this);
		btn_wx_friend.setOnClickListener(this);
		btn_start.setOnClickListener(this);
		getView().setOnClickListener(this);

		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				LogWriter.d("edit", "source start:" + start + "-end:" + end + "=====ds:" + dstart + "-dend:" + dend);
				int sourceLen = StringUtils.getLengthOfByteCode(source.toString());
				int destLen = StringUtils.getLengthOfByteCode(dest.toString());

				if (dstart == 0 && StringUtils.isBlackChar(source.toString()))// 第一个不能为空格
				{
					return "";
				}

				if (dest.toString().endsWith(" ") && StringUtils.isBlackChar(source.toString()))// 不能连续两个空格
				{
					return "";
				}

				if (dstart >= 1 && dstart <= dest.length()) {
					String sectionStr = dest.subSequence(dstart - 1, dstart).toString();
					LogWriter.d("edit", sectionStr);
					if (StringUtils.isBlackChar(sectionStr) && StringUtils.isBlackChar(source.toString())) {
						return "";
					}
				}

				if (sourceLen + destLen > 42)// 不能超过16个字节
				{
					UIUtils.showToast(R.string.live_topic_too_long);
					return "";
				}
				return source;
			}
		};

		et_topic.setFilters(filters);

	}

	private TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			checkTopic();

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	@Override
	protected void refresh() {

		ShareSdkUtils.init(getContext());

		if (mViewParam.type.equals(TYPE_CREATE)) {
			checkTopic();

			// SharedPreferencesUtil spUtil = new
			// SharedPreferencesUtil(getContext());
			// int type = spUtil.get(TYPE_CREATE, SHARE_WEIXIN);
			// if (type == SHARE_WEIBO) {
			// btn_sina.performClick();
			// } else if (type == SHARE_WEIXIN) {
			// btn_wx.performClick();
			// } else if (type == SHARE_FRIEND) {
			// btn_wx_friend.performClick();
			// }

			Message msg = new Message();
			msg.obj = this;
			new MyHandler().sendMessageDelayed(msg, 200);
		} else if (mViewParam.type.equals(TYPE_UPDATE_TOPIC)) {
			checkTopic();

			et_topic.setText(mLive.title);
			// et_topic.setSelection(0, et_topic.getText().toString().length());
			et_topic.setSelection(et_topic.getText().toString().length());

			btn_wx.setEnabled(false);
			btn_sina.setEnabled(false);
			btn_wx_friend.setEnabled(false);
			tv_share_tip.setVisibility(View.INVISIBLE);
			btn_start.setText("继续");

			Message msg = new Message();
			msg.obj = this;
			new MyHandler().sendMessageDelayed(msg, 200);
		}
	}

	private void checkTopic() {
		String content = et_topic.getText().toString().trim();
		if (StringUtils.isEmpty(content)) {
			btn_start.setEnabled(false);
		} else {
			btn_start.setEnabled(true);
		}
	}

	private static final int SHARE_UNKNOWN = -1;
	private static final int SHARE_WEIXIN = 0;
	private static final int SHARE_FRIEND = 1;
	private static final int SHARE_WEIBO = 2;

	private int shareType = SHARE_UNKNOWN;

	@Override
	public void onClick(View v) {

		if (v == getView()) {
			KeyBoardUtil.hideSoftKeyBoard(getActivity());
			return;
		}

		switch (v.getId()) {
		case R.id.btn_sina:// 分享到新浪微博
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WEIBO)) {
				UIUtils.showToast(R.string.share_weibo_no_avliible);
				return;
			}

			if (btn_sina.isSelected()) {
				btn_sina.setSelected(false);
				shareType = SHARE_UNKNOWN;
				tv_share_tip.setVisibility(View.INVISIBLE);
			} else {
				btn_sina.setSelected(true);
				shareType = SHARE_WEIBO;
				btn_wx.setSelected(false);
				btn_wx_friend.setSelected(false);
				tv_share_tip.setVisibility(View.VISIBLE);
				tv_share_tip.setText(getString(R.string.create_live_share_sinaweibo));
			}

			break;
		case R.id.btn_wx:// 分享给微信好友
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				UIUtils.showToast(R.string.share_wechat_no_avliible);
				return;
			}
			if (btn_wx.isSelected()) {
				btn_wx.setSelected(false);
				shareType = SHARE_UNKNOWN;
				tv_share_tip.setVisibility(View.INVISIBLE);
			} else {
				btn_wx.setSelected(true);
				shareType = SHARE_WEIXIN;
				btn_sina.setSelected(false);
				btn_wx_friend.setSelected(false);
				tv_share_tip.setVisibility(View.VISIBLE);
				tv_share_tip.setText(getString(R.string.create_live_share_wechat_friend));
			}
			break;
		case R.id.btn_wx_friend:// 分享到微信朋友圈
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				UIUtils.showToast(R.string.share_wechat_no_avliible);
				return;
			}
			if (btn_wx_friend.isSelected()) {
				btn_wx_friend.setSelected(false);
				shareType = SHARE_UNKNOWN;
				tv_share_tip.setVisibility(View.INVISIBLE);
			} else {
				btn_wx_friend.setSelected(true);
				shareType = SHARE_FRIEND;
				btn_sina.setSelected(false);
				btn_wx.setSelected(false);
				tv_share_tip.setVisibility(View.VISIBLE);
				tv_share_tip.setText(getString(R.string.create_live_share_wechat));
			}
			break;
		case R.id.btn_start:// 开始直播

			if (TYPE_CREATE.equals(mViewParam.type)) {

				SharedPreferencesUtil spUtil = new SharedPreferencesUtil(getContext());
				spUtil.setValueInt(TYPE_CREATE, shareType);

				if (!checkPlatform()) {
					return;
				}

				final String topic = et_topic.getText().toString().trim();
				if (StringUtils.isEmpty(topic)) {
					return;
				}

				if (StringUtils.getLengthOfByteCode(topic) > 42) {
					UIUtils.showToast(getResources().getString(R.string.live_topic_too_long));
					return;
				}

				ArrayList<String> permissions = new ArrayList<String>();
				permissions.add(Manifest.permission.RECORD_AUDIO);// 录音

				((BaseActivity) getActivity()).doRequestPermissions(new PermissionRequestObj(permissions) {

					public void callback(boolean allGranted, List<String> permissionsList_denied, PermissionRequestObj pro) {

						if (allGranted) {
							createLive(topic);

						} else {
							pro.showManualSetupDialog(getActivity(), "麦克风权限");
						}
					}
				});
			} else if (TYPE_UPDATE_TOPIC.equals(mViewParam.type)) {
				final String topic = et_topic.getText().toString().trim();
				updateLiveTitle(topic);
			}

			break;
		}

	}

	/**
	 * 检测选中的分享方式是否已安装
	 * 
	 * @return
	 */
	private boolean checkPlatform() {
		if (shareType == SHARE_UNKNOWN) {
			return true;
		} else if (shareType == SHARE_WEIBO) {
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WEIBO)) {
				UIUtils.showToast(R.string.share_weibo_no_avliible);
				return false;
			}

		} else if (shareType == SHARE_WEIXIN) {
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				UIUtils.showToast(R.string.share_wechat_no_avliible);
				return false;
			}
		} else if (shareType == SHARE_FRIEND) {
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				UIUtils.showToast(R.string.share_wechat_no_avliible);
				return false;
			}
		}

		return true;
	}

	private LivePresent mLivePresent = new LivePresent();

	private LiveInfo mLive;

	/**
	 * 根据话题创建直播房间
	 * 
	 * @param topic
	 */
	private void createLive(String topic) {
		mLivePresent.createLive(topic, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (getActivity() == null) {
					return;
				}
				if (errCode == ErrorCode.ERROR_OK) {
					JSONObject liveJ = json.optJSONObject("live");
					LiveInfo live = new LiveInfo(liveJ);
					LivePrepareFragment.this.mLive = live;

					if (shareType != SHARE_UNKNOWN) {
						getShareInfo(live);
					} else {
						NavigationController.gotoLiveAnchorFragment(getContext(), live);
						getActivity().finish();
					}

				} else {
					UIUtils.showToast(msg);
				}

			}
		});
	}

	/**
	 * 修改直播主题
	 * 
	 * @param topic
	 */
	private void updateLiveTitle(final String topic) {
		if (mLive != null) {
			mLivePresent.updateLiveTitle(mLive.liveId, topic, new JSONResponse() {

				@Override
				public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
					if (getActivity() == null) {
						return;
					}
					if (errCode == ErrorCode.ERROR_OK) {
						Intent intent = new Intent();
						intent.putExtra("title", topic);
						getActivity().setResult(Activity.RESULT_OK, intent);
						getActivity().finish();
					} else {
						UIUtils.showToast(msg);
					}

				}
			});
		}
	}

	public void getShareInfo(final LiveInfo live) {
		mLivePresent.shareLive(live.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (getActivity() == null) {
					return;
				}
				if (errCode == ErrorCode.ERROR_OK) {
					JSONObject shareJ = json.optJSONObject("share");
					if (shareJ != null) {
						if (shareType == SHARE_WEIBO) {
							JSONObject weiboJ = shareJ.optJSONObject("weibo");
							ShareInfo info = new ShareInfo();
							info.content = weiboJ.optString("content");
							info.shareUrl = weiboJ.optString("url");
							info.shareCover = weiboJ.optString("coverUrl");
							info.title = weiboJ.optString("title");
							share(info);
						} else if (shareType == SHARE_WEIXIN) {
							JSONObject weiboJ = shareJ.optJSONObject("wechat");
							ShareInfo info = new ShareInfo();
							info.content = weiboJ.optString("content");
							info.shareUrl = weiboJ.optString("url");
							info.shareCover = weiboJ.optString("coverUrl");
							info.title = weiboJ.optString("title");
							share(info);
						} else if (shareType == SHARE_FRIEND) {
							JSONObject weiboJ = shareJ.optJSONObject("friend");
							ShareInfo info = new ShareInfo();
							info.content = weiboJ.optString("content");
							info.shareUrl = weiboJ.optString("url");
							info.shareCover = weiboJ.optString("coverUrl");
							info.title = weiboJ.optString("title");
							share(info);
						}
					} else {
						UIUtils.showToast("未获取到分享信息");
						NavigationController.gotoLiveAnchorFragment(getContext(), live);
						getActivity().finish();
						return;
					}

				} else {
					UIUtils.showToast("未获取到分享信息");
					NavigationController.gotoLiveAnchorFragment(getContext(), live);
					getActivity().finish();
				}

			}
		});
	}

	/**
	 * 开始分享
	 */
	public void share(ShareInfo info) {
		if (shareType == SHARE_WEIBO) {

			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WEIBO)) {
				UIUtils.showToast(R.string.share_weibo_no_avliible);
				// joinRoom();
				return;
			}

			ShareSdkUtils.showWeiboShare(info, "", getContext(), new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					// joinRoom();
					UIUtils.showToast("分享失败");
				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					joinRoom();

				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					UIUtils.showToast("分享取消");

				}
			});
		} else if (shareType == SHARE_WEIXIN) {
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				UIUtils.showToast(R.string.share_wechat_no_avliible);
				joinRoom();
				return;
			}
			ShareSdkUtils.showWechatShare(info, "", getContext(), new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					// joinRoom();
					UIUtils.showToast("分享失败");

				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					joinRoom();

				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					// joinRoom();
					UIUtils.showToast("分享取消");

				}
			});
		} else if (shareType == SHARE_FRIEND) {
			if (!PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				UIUtils.showToast(R.string.share_wechat_no_avliible);
				joinRoom();
				return;
			}
			ShareSdkUtils.showWechatFriendShare(info, "", getContext(), new PlatformActionListener() {

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					// joinRoom();
					UIUtils.showToast("分享失败");

				}

				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
					joinRoom();
				}

				@Override
				public void onCancel(Platform arg0, int arg1) {
					// joinRoom();
					UIUtils.showToast("分享取消");

				}
			});
		}
	}

	private void joinRoom() {
		if (getActivity() == null) {
			return;
		}

		NavigationController.gotoLiveAnchorFragment(getContext(), mLive);
		getActivity().finish();
	}

	public void autoShowKeyboard() {

		if (getActivity() == null) {
			return;
		}

		KeyBoardUtil.showSoftKeyBoard(getActivity(), et_topic);
	}

	private static class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			LivePrepareFragment fragment = (LivePrepareFragment) msg.obj;
			fragment.autoShowKeyboard();
		}
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LivePrepareFragment";
	}

}
