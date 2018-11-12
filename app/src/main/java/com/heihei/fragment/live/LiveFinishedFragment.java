package com.heihei.fragment.live;

import org.json.JSONObject;

import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.base.host.BaseFragment;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.PackageUtils;
import com.base.utils.ShareSdkUtils;
import com.base.utils.UIUtils;
import com.heihei.logic.present.LivePresent;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.LiveInfo;
import com.heihei.model.ShareInfo;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class LiveFinishedFragment extends BaseFragment implements OnClickListener {

	public boolean isSelfLive = false;

	private LiveInfo mLiveInfo;

	// ----------------R.layout.fragment_live_finished-------------Start
	private TextView tv_look_num;
	private LinearLayout ll_income;
	private TextView tv_ticker_num;
	private TextView tv_live_tip;
	private LinearLayout btn_share_wx;
	private LinearLayout btn_share_friend;
	private LinearLayout btn_share_sina;
	private Button btn_follow;
	private Button btn_back;

	public void autoLoad_fragment_live_finished() {
		tv_look_num = (TextView) findViewById(R.id.tv_look_num);
		ll_income = (LinearLayout) findViewById(R.id.ll_income);
		tv_ticker_num = (TextView) findViewById(R.id.tv_ticker_num);
		tv_live_tip = (TextView) findViewById(R.id.tv_live_tip);
		btn_share_wx = (LinearLayout) findViewById(R.id.btn_share_wx);
		btn_share_friend = (LinearLayout) findViewById(R.id.btn_share_friend);
		btn_share_sina = (LinearLayout) findViewById(R.id.btn_share_sina);
		btn_follow = (Button) findViewById(R.id.btn_follow);
		btn_back = (Button) findViewById(R.id.btn_back);
	}

	// ----------------R.layout.fragment_live_finished-------------End

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_live_finished);

	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_live_finished();
		btn_share_wx.setOnClickListener(this);
		btn_share_friend.setOnClickListener(this);
		btn_share_sina.setOnClickListener(this);
		btn_follow.setOnClickListener(this);
		btn_back.setOnClickListener(this);

		if (mViewParam.data != null) {
			mLiveInfo = (LiveInfo) mViewParam.data;
			isSelfLive = (boolean) mViewParam.data1;
		}

	}

	@Override
	protected void refresh() {

		ShareSdkUtils.init(getContext());

		if (isSelfLive) {
			btn_follow.setVisibility(View.INVISIBLE);
			ll_income.setVisibility(View.VISIBLE);
		} else {
			btn_follow.setVisibility(View.VISIBLE);
			ll_income.setVisibility(View.INVISIBLE);
		}

		refreshLiveInfo();

		mLivePresent.getLiveInfo(mLiveInfo.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (getActivity() == null) {
					return;
				}
				if (errCode == ErrorCode.ERROR_OK) {
					mLiveInfo.parseJSON(json.optJSONObject("live"));
					refreshLiveInfo();

					JSONObject userInfoJ = json.optJSONObject("userInfo");
					User user = new User(userInfoJ);
					mLiveInfo.creator = user;
					if (mLiveInfo.creator.isFollowed) {
						btn_follow.setText("已关注");
					} else {
						btn_follow.setText("关注");
					}
				}

			}
		});
		getShareInfo();
	}

	private SparseArray<ShareInfo> mShareInfos = new SparseArray<>();

	private static final int SHARE_WEIXIN = 0;
	private static final int SHARE_FRIEND = 1;
	private static final int SHARE_WEIBO = 2;

	public void getShareInfo() {
		mLivePresent.shareLive(mLiveInfo.liveId, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (getActivity() == null) {
					return;
				}
				if (errCode == ErrorCode.ERROR_OK) {
					JSONObject shareJ = json.optJSONObject("share");
					if (shareJ != null) {
						// if (shareType == SHARE_WEIBO) {
						JSONObject weiboJ = shareJ.optJSONObject("weibo");
						ShareInfo weiboInfo = new ShareInfo();
						weiboInfo.content = weiboJ.optString("content");
						weiboInfo.shareUrl = weiboJ.optString("url");
						weiboInfo.shareCover = weiboJ.optString("coverUrl");
						weiboInfo.title = weiboJ.optString("title");
						mShareInfos.put(SHARE_WEIBO, weiboInfo);

						// } else if (shareType == SHARE_WEIXIN) {
						JSONObject weixinJ = shareJ.optJSONObject("wechat");
						ShareInfo weixiInfo = new ShareInfo();
						weixiInfo.content = weiboJ.optString("content");
						weixiInfo.shareUrl = weiboJ.optString("url");
						weixiInfo.shareCover = weiboJ.optString("coverUrl");
						weixiInfo.title = weiboJ.optString("title");
						mShareInfos.put(SHARE_WEIXIN, weixiInfo);

						// } else if (shareType == SHARE_FRIEND) {
						JSONObject friendJ = shareJ.optJSONObject("friend");
						ShareInfo friendInfo = new ShareInfo();
						friendInfo.content = weiboJ.optString("content");
						friendInfo.shareUrl = weiboJ.optString("url");
						friendInfo.shareCover = weiboJ.optString("coverUrl");
						friendInfo.title = weiboJ.optString("title");
						mShareInfos.put(SHARE_FRIEND, friendInfo);

						// }
					} else {
						UIUtils.showToast("未获取到分享信息");
						return;
					}

				} else {

				}

			}
		});
	}

	private void refreshLiveInfo() {
		tv_look_num.setText(mLiveInfo.totalUsers + "");
		tv_ticker_num.setText(String.valueOf(mLiveInfo.livetotalticket));
	}

	private LivePresent mLivePresent = new LivePresent();

	private void followUser() {
		if (mLiveInfo.creator != null) {
			if (mLiveInfo.creator.isFollowed) {
				new UserPresent().unfollowUser(mLiveInfo.creator.uid, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							btn_follow.setText("关注");
							mLiveInfo.creator.isFollowed = false;
						} else {
							UIUtils.showToast(msg);
						}

					}
				});
			} else {
				new UserPresent().followUser(mLiveInfo.creator.uid, null, new JSONResponse() {

					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							btn_follow.setText("已关注");
							mLiveInfo.creator.isFollowed = true;
						} else {
							UIUtils.showToast(msg);
						}

					}
				});
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_share_wx:// 分享到微信
			if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				ShareInfo info = mShareInfos.get(SHARE_WEIXIN);
				if (info != null) {
					ShareSdkUtils.showWechatShare(info, "", getContext(), null);
				}
			} else
				UIUtils.showToast(R.string.share_wechat_no_avliible);
			break;
		case R.id.btn_share_friend:// 分享到朋友圈
			if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
				ShareInfo info = mShareInfos.get(SHARE_FRIEND);
				if (info != null) {
					ShareSdkUtils.showWechatFriendShare(info, "", getContext(), null);
				}
			} else
				UIUtils.showToast(R.string.share_wechat_no_avliible);
			break;
		case R.id.btn_share_sina:// 分享到微博
			if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WEIBO)) {
				ShareInfo info = mShareInfos.get(SHARE_WEIBO);
				if (info != null) {
					ShareSdkUtils.showWeiboShare(info, "", getContext(), null);
				}
			} else
				UIUtils.showToast(R.string.share_weibo_no_avliible);
			break;
		case R.id.btn_follow:// 关注主播
			followUser();
			break;
		case R.id.btn_back:// 返回首页
			getActivity().finish();
			break;
		}

	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LiveFinishedFragment";
	}
}
