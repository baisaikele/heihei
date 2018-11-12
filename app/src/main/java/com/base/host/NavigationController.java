package com.base.host;

import com.heihei.dialog.MessageDialog;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.MainActivity;
import com.heihei.fragment.MainFragment;
import com.heihei.fragment.PMFragment;
import com.heihei.fragment.live.LiveAnchorFragment;
import com.heihei.fragment.live.LiveAudienceFragment;
import com.heihei.fragment.live.LiveFinishedFragment;
import com.heihei.fragment.live.LivePrepareFragment;
import com.heihei.fragment.live.LiveReplayFragment;
import com.heihei.fragment.login.EditFragment;
import com.heihei.fragment.setting.AboutFragment;
import com.heihei.fragment.setting.SettingFragment;
import com.heihei.fragment.setting.WebViewFragment;
import com.heihei.fragment.user.BlackListFragment;
import com.heihei.fragment.user.ExchangeFragment;
import com.heihei.fragment.user.FansListFragment;
import com.heihei.fragment.user.FollowListFragment;
import com.heihei.fragment.user.IncomeFragment;
import com.heihei.fragment.user.LivesListFragment;
import com.heihei.fragment.user.MyDiamondFragment;
import com.heihei.fragment.withdraw.WithdrawFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.logic.present.PmPresent;
import com.heihei.login.LoginActivity;
import com.heihei.model.LiveInfo;
import com.heihei.model.PlayActivityInfo;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationController {

	/**
	 * 判断是否是主页，第一级界面
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isMainFragment() {
		Fragment fragment = ActivityManager.getInstance().peek();
		if (fragment != null && fragment instanceof MainFragment) {
			return true;
		}
		return false;
	}

	/**
	 * 启动相应的fragment
	 * @param context
	 * @param clazz
	 * @param mViewParam
	 */
	public static void startFragment(Context context, Class<?> clazz, ViewParam mViewParam) {
		if (context instanceof BaseActivity) {
			BaseActivity activity = (BaseActivity) context;
			Intent intent = new Intent(activity, BaseActivity.class);
			intent.putExtra(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());
			String key = FragmentParams.getInstance().createKey(context.getClass(), clazz);
			FragmentParams.getInstance().put(key, mViewParam);
			intent.putExtra(BaseActivity.FRAGMENT_PARAMS_KEY, key);
			activity.startActivity(intent);
		} else {
			Intent intent = new Intent(context, BaseActivity.class);
			intent.putExtra(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());
			String key = FragmentParams.getInstance().createKey(context.getClass(), clazz);
			FragmentParams.getInstance().put(key, mViewParam);
			intent.putExtra(BaseActivity.FRAGMENT_PARAMS_KEY, key);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public static void startFragmentForResult(Activity context, Class<?> clazz, ViewParam mViewParam, int requestCode) {
		if (context instanceof BaseActivity) {
			BaseActivity activity = (BaseActivity) context;
			Intent intent = new Intent(activity, BaseActivity.class);
			intent.putExtra(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());
			String key = FragmentParams.getInstance().createKey(context.getClass(), clazz);
			FragmentParams.getInstance().put(key, mViewParam);
			intent.putExtra(BaseActivity.FRAGMENT_PARAMS_KEY, key);
			activity.startActivityForResult(intent, requestCode);
		}
	}

	/**
	 * 主页
	 * 
	 * @param context
	 */
	public static void gotoMainFragment(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		context.startActivity(intent);
	}

	/**
	 * 登陆页面
	 * @param context
	 */
	public static void gotoLogin(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		if (context instanceof Activity) {
			context.startActivity(intent);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}

	}

	/**
	 * 完善资料
	 * 
	 * @param context
	 */
	public static void gotoCompleteInfoFragment(Context context) {
		ViewParam vp = new ViewParam();
		vp.type = EditFragment.TYPE_COMPLETE;
		vp.title = HostApplication.getInstance().getString(R.string.setting_improve_data);
		startFragment(context, EditFragment.class, vp);
	}

	/**
	 * 编辑资料
	 * 
	 * @param context
	 */
	public static void gotoEditFragment(Context context) {
		ViewParam vp = new ViewParam();
		vp.type = EditFragment.TYPE_EDIT;
		vp.title = context.getString(R.string.setting_edit_information);
		startFragment(context, EditFragment.class, vp);
	}

	/**
	 * 去设置界面
	 * 
	 * @param context
	 */
	public static void gotoSettingFragment(Context context) {
		startFragment(context, SettingFragment.class, null);
	}

	/**
	 * 去某人的直播列表界面
	 * 
	 * @param context
	 * @param uid
	 */
	public static void gotoLivesListFragment(Context context, String uid, PlayActivityInfo info) {
		ViewParam vp = new ViewParam();
		if (uid.equals(UserMgr.getInstance().getUid())) {
			vp.title = context.getString(R.string.me_my_lives);
		} else {
			vp.title = context.getString(R.string.lives_for_other);
		}
		vp.data = uid;
		if (info != null) {
			vp.data1 = info;
		}
		startFragment(context, LivesListFragment.class, vp);
	}

	public static void gotoAboutFragment(Context context) {
		startFragment(context, AboutFragment.class, null);
	}

	/**
	 * 发起直播
	 * 
	 * @param context
	 */
	public static void gotoStartLive(Context context) {
		ViewParam vp = new ViewParam();
		vp.type = LivePrepareFragment.TYPE_CREATE;
		startFragment(context, LivePrepareFragment.class, vp);
	}

	/**
	 * 修改用户主题
	 * 
	 * @param context
	 * @param mLive
	 */
	public static void gotoUpdateTopic(Context context, LiveInfo mLive) {
		ViewParam vp = new ViewParam();
		vp.title = context.getResources().getString(R.string.user_update_live);
		vp.data = mLive;
		vp.type = LivePrepareFragment.TYPE_UPDATE_TOPIC;
		startFragmentForResult((Activity) context, LivePrepareFragment.class, vp, LivePrepareFragment.REQUEST_CODE);
	}

	/**
	 * 打开消息列表
	 * 
	 * @param context
	 */
	public static void gotoMessageDialog(Context context, ImageView t) {
		MessageDialog md = new MessageDialog(context, t);
		md.show();
		DueMessageUtils.getInstance().hideMessage();
	}

	/**
	 * 我的收益
	 * 
	 * @param context
	 */
	public static void gotoIncomeFragment(Context context) {
		startFragment(context, IncomeFragment.class, null);
	}

	/**
	 * 充值/我的钻石
	 * 
	 * @param context
	 */
	public static void gotoMyDiamondFragment(Context context) {
		ViewParam vp = new ViewParam();
		vp.title = context.getString(R.string.user_black_account);
		startFragment(context, MyDiamondFragment.class, vp);
	}

	/**
	 * 充值
	 * 
	 * @param context
	 */
	public static void gotoRechargeFragment(Context context) {
		ViewParam vp = new ViewParam();
		vp.title = context.getString(R.string.user_account_pay);
		startFragment(context, MyDiamondFragment.class, vp);
	}

	/**
	 * 兑换钻石
	 * 
	 * @param context
	 */
	public static void gotoExchangeFragment(Context context) {
		startFragment(context, ExchangeFragment.class, null);
	}

	/**
	 * 关注列表
	 * 
	 * @param context
	 */
	public static void gotoFollowListFragment(Context context, String uid, PlayActivityInfo info) {
		ViewParam vp = new ViewParam();
		vp.title = context.getString(R.string.user_concerned_people);
		vp.data = uid;
		if (info != null) {
			vp.data1 = info;

			// if (info.isFollow && info.followActivity != null) {
			// info.followActivity.finish();
			// info.isFollow = false;
			// info.followActivity = null;
			// }
			// if (info.isFans && info.fansActivity != null) {
			// info.fansActivity.finish();
			// info.isFans = false;
			// info.fansActivity = null;
			// }

		}
		startFragment(context, FollowListFragment.class, vp);
	}

	/**
	 * 粉丝列表
	 * 
	 * @param context
	 */
	public static void gotoFansListFragment(Context context, String uid, PlayActivityInfo info) {
		ViewParam vp = new ViewParam();
		vp.title = context.getString(R.string.user_fans);
		vp.data = uid;
		if (info != null) {
			vp.data1 = info;
			// if (info.isFans && info.fansActivity != null) {
			// info.fansActivity.finish();
			// info.isFans = false;
			// info.fansActivity = null;
			// }
			// if (info.isFollow && info.followActivity != null) {
			// info.followActivity.finish();
			// info.isFollow = false;
			// info.followActivity = null;
			// }
		}
		startFragment(context, FansListFragment.class, vp);
	}

	// public static void gotoLocationFragment(Context context) {
	// ViewParam vp = new ViewParam();
	// vp.title = context.getString(R.string.user_city_location);
	// startFragment(context, LocationFragment.class, vp);
	// }

	/**
	 * 黑名单列表
	 * 
	 * @param context
	 */
	public static void gotoBlackListFragment(Context context) {
		ViewParam vp = new ViewParam();
		vp.title = context.getString(R.string.setting_black);
		startFragment(context, BlackListFragment.class, vp);
	}

	/**
	 * 进入直播
	 * 
	 * @param context
	 */
	public static void gotoLiveFragment(Context context) {

	}

	/**
	 * 直播结束
	 * 
	 * @param context
	 * @param byUser
	 *            是否主播主动结束
	 */
	public static void gotoLiveFinishedFragment(Context context, LiveInfo mLiveInfo, boolean byUser) {
		ViewParam vp = new ViewParam();
		vp.data = mLiveInfo;
		vp.data1 = byUser;

		startFragment(context, LiveFinishedFragment.class, vp);
	}

	/**
	 * 直播界面，主播端
	 * 
	 * @param context
	 */
	public static void gotoLiveAnchorFragment(Context context, LiveInfo live) {
		ViewParam vp = new ViewParam();
		vp.data = live;
		startFragment(context, LiveAnchorFragment.class, vp);
	}

	public static void gotoWebView(Context context, String url, String title) {
		ViewParam vp = new ViewParam();
		if (title != null)
			vp.title = title;
		vp.data = url;
		startFragment(context, WebViewFragment.class, vp);
	}

	/**
	 * 直播界面，听众端
	 * 
	 * @param context
	 */
	public static void gotoLiveAudienceFragment(Context context, LiveInfo live) {
		ViewParam vp = new ViewParam();
		vp.data = live;
		startFragment(context, LiveAudienceFragment.class, vp);
	}

	/**
	 * 直播界面，回放端
	 * 
	 * @param context
	 */
	public static void gotoLiveReplayFragment(Context context, LiveInfo mLiveInfo) {
		ViewParam vp = new ViewParam();
		vp.data = mLiveInfo;
		startFragment(context, LiveReplayFragment.class, vp);
	}

	/**
	 * 到私聊界面
	 * 
	 * @param context
	 */
	public static void gotoChatFragment(Context context, ObServerMessage message) {
		if (!isMainFragment()) {
			ActivityManager.getInstance().finishActivitysAfter(MainFragment.class);
		}
		EventManager.ins().sendEvent(EventTag.START_CHAT, 0, 0, message);
	}

	/**
	 * 跳转到黑聊页，同时消息list上拉显示
	 * 
	 * @param context
	 */
	public static void gotoChatShowMsgFragment(Context context) {
		if (!isMainFragment()) {
			ActivityManager.getInstance().finishActivitysAfter(MainFragment.class);
		}
		EventManager.ins().sendEvent(EventTag.CHAT_WITH_SHOW_MESSAGE, 0, 0, null);
	}

	public static void gotoHomePageFragment(Context context) {
		if (!isMainFragment()) {
			ActivityManager.getInstance().finishActivitysAfter(MainFragment.class);
		}
	}

	/**
	 * 提现界面
	 * 
	 * @param context
	 */
	public static void gotoWithdrawFragment(Context context) {
		startFragment(context, WithdrawFragment.class, null);
	}

}
