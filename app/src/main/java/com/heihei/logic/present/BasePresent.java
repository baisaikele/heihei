package com.heihei.logic.present;

import com.base.host.AppLogic;
import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.base.utils.StringUtils;
import com.heihei.logic.UserMgr;
import com.wmlives.heihei.R;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasePresent {

	public static interface API_MODEL {

		public String LIVE = "live";
		public String USER = "user";
		public String SOCIAL = "social";
		public String MESSAGE = "message";
		public String PAYMENT = "payment";
		public String CHAT = "chat";
		public String SYS = "sys";
		public String ABOUTUS = "aboutus";
		public String TIPS = "tips";
	}

	protected static ConcurrentHashMap<String, String> urls = new ConcurrentHashMap<String, String>(64);


	/** 初始化地址 */
	public static final String INIT_URL = AppLogic.DEBUG ?"https://api-01.wmlive.cn/api/init": "http://47.74.129.206:8081/api/init"  ;

	/** host */
	public static final String MAIN_HOST = AppLogic.DEBUG ?"https://api-01.wmlive.cn/": "http://47.74.129.206:8081/"  ;

	public static final String GET_SMS_CODE_KEY = API_MODEL.USER + "getSMSVerificationCode";// 短信验证码key
	public static final String GET_SMS_CODE_VALUE = MAIN_HOST + "api/user/get-sms-verification-code";// 短信验证码地址

	/** 三方登录 */
	public static final String SIGNIN_BY_THIRD_KEY = API_MODEL.USER + "signIn";
	public static final String SIGNIN_BY_THIRD_VALUE = MAIN_HOST + "api/user/sign-in";

	/** 手机短信登录 */
	public static final String SIGNIN_BY_PHONE_KEY = API_MODEL.USER + "signInCLByPhone";
	public static final String SIGNIN_BY_PHONE_VALUE = MAIN_HOST + "api/user/sign-in-cl-by-phone";

	/** 修改用户信息 */
	public static final String UPDATE_USER_INFO_KEY = API_MODEL.USER + "updateUser";
	public static final String UPDATE_USER_INFO_VALUE = MAIN_HOST + "api/user/update";

	/** 注销 */
	public static final String SIGNOUT_KEY = API_MODEL.USER + "signOut";
	public static final String SIGNOUT_VALUE = MAIN_HOST + "api/user/sign-out";

	/** 获取用户信息 */
	public static final String GET_USER_INFO_KEY = API_MODEL.USER + "userInfo";
	public static final String GET_USER_INFO_VALUE = MAIN_HOST + "api/user/info";

	/** 关注某人 */
	public static final String FOLLOW_USER_KEY = API_MODEL.USER + "followUser";
	public static final String FOLLOW_USER_VALUE = MAIN_HOST + "api/user/follow";

	/** 取消关注某人 */
	public static final String UNFOLLOW_USER_KEY = API_MODEL.USER + "unfollowUser";
	public static final String UNFOLLOW_USER_VALUE = MAIN_HOST + "api/user/unfollow";

	/** 拉黑 */
	public static final String BLOCK_USER_KEY = API_MODEL.USER + "blockUser";
	public static final String BLOCK_USER_VALUE = MAIN_HOST + "api/user/block-user";

	/** 取消拉黑 */
	public static final String UNBLOCK_USER_KEY = API_MODEL.USER + "unblockUser";
	public static final String UNBLOCK_USER_VALUE = MAIN_HOST + "api/user/unblock-user";

	/** 举报 */
	public static final String REPORT_USER_KEY = API_MODEL.USER + "reportUser";
	public static final String REPORT_USER_VALUE = MAIN_HOST + "api/usr/report-user";

	/** 粉丝列表 */
	public static final String FANS_LIST_KEY = API_MODEL.USER + "listFollower";
	public static final String FANS_LIST_VALUE = MAIN_HOST + "api/user/list-follower";

	/** 关注列表 */
	public static final String FOLLOW_LIST_KEY = API_MODEL.USER + "listFollowing";
	public static final String FOLLOW_LIST_VALUE = MAIN_HOST + "api/user/list-following";

	/** pushDeviceInfo */
	public static final String PUSH_DEVICE_KEY = API_MODEL.USER + "pushDeviceInfo";
	public static final String PUSH_DEVICE_VALUE = MAIN_HOST + "api/user/push-device-info";

	/** 黑名单列表 */
	public static final String BLOCK_LIST_KEY = API_MODEL.USER + "listBlockedUser";
	public static final String BLOCK_LIST_VALUE = MAIN_HOST + "api/user/list-blocked-user";

	/** 首页banner */
	public static final String HOME_BANNER_KEY = API_MODEL.SOCIAL + "getBanner";
	public static final String HOME_BANNER_VALUE = MAIN_HOST + "api/social/get-banner";

	/** 开始直播通知服务器 */
	public static final String START_LIVE_KEY = API_MODEL.LIVE + "startLive";
	public static final String START_LIVE_VALUE = MAIN_HOST + "api/live/start-live";

	/** 直播推荐列表 */
	public static final String HOME_RECOMMEND_KEY = API_MODEL.LIVE + "listLiveByRecommend";
	public static final String HOME_RECOMMEND_VALUE = MAIN_HOST + "api/live/list-live-by-recommend";

	/** 创建直播 */
	public static final String CREATE_LIVE_KEY = API_MODEL.LIVE + "createLive";
	public static final String CREATE_LIVE_VALUE = MAIN_HOST + "api/live/create-live";

	/** 分享直播 */
	public static final String SHARE_LIVE_KEY = API_MODEL.LIVE + "shareLive";
	public static final String SHARE_LIVE_VALUE = MAIN_HOST + "api/live/share-live";

	/** 更新直播 */
	public static final String UPDATE_LIVE_KEY = API_MODEL.LIVE + "changeLive";
	public static final String UPDATE_LIVE_VALUE = MAIN_HOST + "api/live/change-live";

	/** 暂停直播 */
	public static final String PAUSE_LIVE_KEY = API_MODEL.LIVE + "pauseLive";
	public static final String PAUSE_LIVE_VALUE = MAIN_HOST + "api/live/pause-live";

	/** 继续直播 */
	public static final String CONTINUE_LIVE_KEY = API_MODEL.LIVE + "continueLive";
	public static final String CONTINUE_LIVE_VALUE = MAIN_HOST + "api/live/continue-live";

	/** 结束直播 */
	public static final String STOP_LIVE_KEY = API_MODEL.LIVE + "stopLive";
	public static final String STOP_LIVE_VALUE = MAIN_HOST + "api/live/stop-live";

	/** 删除直播 */
	public static final String DELETE_LIVE_KEY = API_MODEL.LIVE + "deleteLive";
	public static final String DELETE_LIVE_VALUE = MAIN_HOST + "api/live/delete-live";

	/** 获取直播信息 */
	public static final String GET_LIVE_INFO_KEY = API_MODEL.LIVE + "getLive";
	public static final String GET_LIVE_INFO_VALUE = MAIN_HOST + "api/live/get-live";

	/** 加入直播 */
	public static final String JOIN_LIVE_KEY = API_MODEL.LIVE + "joinLive";
	public static final String JOIN_LIVE_VALUE = MAIN_HOST + "api/live/join-live";

	/** 离开直播 */
	public static final String QUIT_LIVE_KEY = API_MODEL.LIVE + "quitLive";
	public static final String QUIT_LIVE_VALUE = MAIN_HOST + "api/live/quit-live";

	/** 心跳 */
	public static final String HEART_BEAT_KEY = API_MODEL.LIVE + "heartBeat";
	public static final String HEART_BEAT_VALUE = MAIN_HOST + "api/live/heart-beat";

	/** 我的/TA的直播 */
	public static final String USER_LIVE_LIST_KEY = API_MODEL.LIVE + "listLiveByUser";
	public static final String USER_LIVE_LIST_VALUE = MAIN_HOST + "api/live/list-live-by-user";

	/** 直播间用户列表 */
	public static final String LIVE_WATCHER_LIST_KEY = API_MODEL.LIVE + "listLiveUsers";
	public static final String LIVE_WATCHER_LIST_VALUE = MAIN_HOST + "api/live/list-live-users";

	/** 回放用户列表 */
	public static final String LIVE_REPLAY_LIST_KEY = API_MODEL.LIVE + "listReplayUsers";
	public static final String LIVE_REPLAY_LIST_VALUE = MAIN_HOST + "api/live/list-replay-users";

	/** 禁言 */
	public static final String SHUTUP_USER_KEY = API_MODEL.LIVE + "shutupUser";
	public static final String SHUTUP_USER_VALUE = MAIN_HOST + "api/live/shutupuser";

	/** 取消禁言 */
	public static final String SHUTUP_OPEN_KEY = API_MODEL.LIVE + "shutupOpen";
	public static final String SHUTUP_OPEN_VALUE = MAIN_HOST + "api/live/shutupopen";

	/** 是否禁言 */
	public static final String IS_SHUTUP_KEY = API_MODEL.LIVE + "isshutup";
	public static final String IS_SHUTUP_VALUE = MAIN_HOST + "api/live/isshutup";

	/** 购买礼物 */
	public static final String LIVE_BUY_GIFT_KEY = API_MODEL.LIVE + "buyGift";
	public static final String LIVE_BUY_GIFT_VALUE = MAIN_HOST + "api/live/buy-gift";

	/** 直播间礼物列表 */
	public static final String LIVE_GIFT_LIST_KEY = API_MODEL.LIVE + "giftInfo";
	public static final String LIVE_GIFT_LIST_VALUE = MAIN_HOST + "api/live/gift-info";

	/** 充值列表页 */
	public static final String RECHARGE_LIST_KEY = API_MODEL.PAYMENT + "listPackage";
	public static final String RECHARGE_LIST_VALUE = MAIN_HOST + "api/payment/list-package";

	/** 创建订单 */
	public static final String CREATE_ORDER_KEY = API_MODEL.PAYMENT + "createOrder";
	public static final String CREATE_ORDER_VALUE = MAIN_HOST + "api/payment/create-order";

	/** 订单通知 */
	public static final String ORDER_NOTICE_KEY = API_MODEL.PAYMENT + "orderNotify";
	public static final String ORDER_NOTICE_VALUE = MAIN_HOST + "api/payment/order-notify";

	/** 黑票兑换钻石列表 */
	public static final String EXCHANGE_DIAMOND_KEY = API_MODEL.PAYMENT + "listP2gPackage";
	public static final String EXCHANGE_DIAMOND_VALUE = MAIN_HOST + "api/payment/list-p2g-package";

	/** 黑票兑换钻石 */
	public static final String EXCHANGE_ACTION_KEY = API_MODEL.PAYMENT + "p2g";
	public static final String EXCHANGE_ACTION_VALUE = MAIN_HOST + "api/payment/p2g";

	/** 我的账户资产 */
	public static final String ACCOUNT_MONEY_KEY = API_MODEL.PAYMENT + "statistic";
	public static final String ACCOUNT_MONEY_VALUE = MAIN_HOST + "api/payment/statistic";

	/** 可提现金额 */
	public static final String ACCOUNT_WITHDRAW_MONEY_KEY = API_MODEL.PAYMENT + "cash-info";
	public static final String ACCOUNT_WITHDRAW_MONEY_VALUE = MAIN_HOST + "api/payment/cash-info";

	/** 获取支付宝信息 */
	public static final String GET_ALI_INFO_KEY = API_MODEL.PAYMENT + "getAlipayInfo";
	public static final String GET_ALI_INFO_VALUE = MAIN_HOST + "api/payment/get-alipay-info";

	/** 验证支付宝信息 */
	public static final String VERIFY_ALIPAY_KEY = API_MODEL.PAYMENT + "verifyAlipay";
	public static final String VERIFY_ALIPAY_VALUE = MAIN_HOST + "api/payment/verify-alipay";

	/** 提现请求 */
	public static final String WITHDRAW_KEY = API_MODEL.PAYMENT + "payReq";
	public static final String WITHDRAW_VALUE = MAIN_HOST + "api/payment/pay-req";

	/** 匹配聊天 */
	public static final String MATCH_CHAT_KEY = API_MODEL.CHAT + "matchChat";
	public static final String MATCH_CHAT_VALUE = MAIN_HOST + "api/chat/match-chat";

	/** 停止匹配聊天 */
	public static final String STOP_MATCH_CHAT_KEY = API_MODEL.CHAT + "stopMatchChat";
	public static final String STOP_MATCH_CHAT_VALUE = MAIN_HOST + "api/chat/stop-match-chat";

	/** 进入聊天 */
	public static final String JOIN_CHAT_KEY = API_MODEL.CHAT + "joinChat";
	public static final String JOIN_CHAT_VALUE = MAIN_HOST + "api/chat/join-chat";

	/** 结束聊天 */
	public static final String STOP_CHAT_KEY = API_MODEL.CHAT + "stopChat";
	public static final String STOP_CHAT_VALUE = MAIN_HOST + "api/chat/stop-chat";

	/** 对聊心跳 */
	public static final String CHAT_HEART_BEAT_KEY = API_MODEL.CHAT + "heartBeat";
	public static final String CHAT_HEART_BEAT_VALUE = MAIN_HOST + "api/chat/heart-beat";

	/** 聊天送礼物 */
	public static final String BUY_GIFT_KEY = API_MODEL.CHAT + "buyGift";
	public static final String BUY_GIFT_VALUE = MAIN_HOST + "api/chat/buy-gift";

	/** 获取chat信息 */
	public static final String GET_CHAT_USER_KEY = API_MODEL.CHAT + "getUserSig";
	public static final String GET_CHAT_USER_VALUE = MAIN_HOST + "api/chat/get-user-sig";

	/** 更改话题 */
	public static final String CHANGE_CHAT_TOPIC_KEY = API_MODEL.CHAT + "changeTopic";
	public static final String CHANGE_CHAT_TOPIC_VALUE = MAIN_HOST + "api/chat/change-topic";

	/** 更改话题 */
	public static final String DUE_CHAT_KEY = API_MODEL.CHAT + "dueChat";
	public static final String DUE_CHAT_VALUE = MAIN_HOST + "api/chat/due-chat";

	/** 取消约聊 */
	public static final String CANCEL_DUE_CHAT_KEY = API_MODEL.CHAT + "cancelDueChat";
	public static final String CANCEL_DUE_CHAT_VALUE = MAIN_HOST + "api/chat/cancel-due-chat";

	/** 更改话题 */
	public static final String ACCEPT_CHAT_KEY = API_MODEL.CHAT + "acceptChat";
	public static final String ACCEPT_CHAT_VALUE = MAIN_HOST + "api/chat/accept-chat";

	/** 发送聊天信息 */
	public static final String SEND_MESSAGE_KEY = API_MODEL.MESSAGE + "sendMsg";
	public static final String SEND_MESSAGE_VALUE = MAIN_HOST + "api/message/send-msg";

	/** 发送聊天信息 */
	public static final String WEBSCOKET_KEY = API_MODEL.MESSAGE + "channel";
	public static final String WEBSCOKET_VALUE = "ws://123.56.0.31:8082/ws/channel";

	/** 发送飘屏信息 */
	public static final String SEND_BULLET_KEY = API_MODEL.LIVE + "buyBullet";
	public static final String SEND_BULLET_VALUE = MAIN_HOST + "api/live/buy-bullet";

	/** 发送LIVE礼物信息 */
	public static final String SEND_LIVE_GIFT_KEY = API_MODEL.LIVE + "buyGift";
	public static final String SEND_LIVE_GIFT_VALUE = MAIN_HOST + "api/live/buy-gift";

	/** 发送Like礼物信息 */
	public static final String SEND_LIVE_LIKE_KEY = API_MODEL.LIVE + "liveLike";
	public static final String SEND_LIVE_LIKE_VALUE = MAIN_HOST + "api/live/live-like";

	public static final String PAY_TIPS_KEY = API_MODEL.TIPS + "pay-tips";
	public static final String PAY_TIPS_VALUE = HostApplication.getInstance().getString(R.string.withdraw_tip);

	/** 协议链接 */
	public static final String ABOUTUS_SERVICE_TERMS_KEY = API_MODEL.ABOUTUS + "serviceTerms";
	public static final String ABOUTUS_SERVICE_TERMS_VALUE = MAIN_HOST + "static/app/service-terms.html";

	public static final String ABOUTUS_SOCIAL_PACT_KEY = API_MODEL.ABOUTUS + "socialPact";
	public static final String ABOUTUS_SOCIAL_PACT_VALUE = MAIN_HOST + "static/app/social-pact.html";

	public static final String ABOUTUS_CONTACT_US_KEY = API_MODEL.ABOUTUS + "contactUs";
	public static final String ABOUTUS_CONTACT_US_VALUE = MAIN_HOST + "static/app/contact-us.html";

	public static final String MESSAGE_DELETE_KEY = API_MODEL.MESSAGE + "messageDel";
	public static final String MESSAGE_DELETE_VALUE = MAIN_HOST + "api/msg/message-del";

	public static final String MESSAGE_READ_ONE_KEY = API_MODEL.MESSAGE + "readOneMsg";
	public static final String MESSAGE_READ_ONE_VALUE = MAIN_HOST + "api/msg/read-one-msg";

	public static final String MESSAGE_READ_ALL_MSG_KEY = API_MODEL.MESSAGE + "readAllMsg";
	public static final String MESSAGE_READ_ALL_MSG_VALUE = MAIN_HOST + "api/msg/read-all-msg";

	public static final String MESSAGE_LIST_KEY = API_MODEL.MESSAGE + "messageList";
	public static final String MESSAGE_LIST_VALUE = MAIN_HOST + "api/msg/message-list";

	public static final String SYSTEM_UPDATE_KEY = API_MODEL.SYS + "update";
	public static final String SYSTEM_UPDATE_VALUE = MAIN_HOST + "api/sys/update";

	static {
		urls.put(PAY_TIPS_KEY, PAY_TIPS_VALUE);
		/** 用户相关接口 */
		urls.put(GET_SMS_CODE_KEY, GET_SMS_CODE_VALUE);
		urls.put(SIGNIN_BY_THIRD_KEY, SIGNIN_BY_THIRD_VALUE);
		urls.put(SIGNIN_BY_PHONE_KEY, SIGNIN_BY_PHONE_VALUE);
		urls.put(UPDATE_USER_INFO_KEY, UPDATE_USER_INFO_VALUE);
		urls.put(SIGNOUT_KEY, SIGNOUT_VALUE);
		urls.put(GET_USER_INFO_KEY, GET_USER_INFO_VALUE);
		urls.put(FOLLOW_USER_KEY, FOLLOW_USER_VALUE);
		urls.put(UNFOLLOW_USER_KEY, UNFOLLOW_USER_VALUE);
		urls.put(BLOCK_USER_KEY, BLOCK_USER_VALUE);
		urls.put(UNBLOCK_USER_KEY, UNBLOCK_USER_KEY);
		urls.put(FANS_LIST_KEY, FANS_LIST_VALUE);
		urls.put(FOLLOW_LIST_KEY, FOLLOW_LIST_VALUE);
		urls.put(BLOCK_LIST_KEY, BLOCK_LIST_VALUE);
		urls.put(REPORT_USER_KEY, REPORT_USER_VALUE);
		urls.put(PUSH_DEVICE_KEY, PUSH_DEVICE_VALUE);
		urls.put(CHAT_HEART_BEAT_KEY, CHAT_HEART_BEAT_VALUE);
		urls.put(QUIT_LIVE_KEY, QUIT_LIVE_VALUE);

		urls.put(MESSAGE_DELETE_KEY, MESSAGE_DELETE_VALUE);
		urls.put(MESSAGE_READ_ONE_KEY, MESSAGE_READ_ONE_VALUE);
		urls.put(MESSAGE_READ_ALL_MSG_KEY, MESSAGE_READ_ALL_MSG_VALUE);
		urls.put(MESSAGE_LIST_KEY, MESSAGE_LIST_VALUE);

		/** 直播相关接口 */
		urls.put(HOME_BANNER_KEY, HOME_BANNER_VALUE);
		urls.put(HOME_RECOMMEND_KEY, HOME_RECOMMEND_VALUE);
		urls.put(CREATE_LIVE_KEY, CREATE_LIVE_VALUE);
		urls.put(SHARE_LIVE_KEY, SHARE_LIVE_VALUE);
		urls.put(UPDATE_LIVE_KEY, UPDATE_LIVE_VALUE);
		urls.put(STOP_LIVE_KEY, STOP_LIVE_VALUE);
		urls.put(DELETE_LIVE_KEY, DELETE_LIVE_VALUE);
		urls.put(GET_LIVE_INFO_KEY, GET_LIVE_INFO_VALUE);
		urls.put(JOIN_LIVE_KEY, JOIN_LIVE_VALUE);
		urls.put(HEART_BEAT_KEY, HEART_BEAT_VALUE);
		urls.put(USER_LIVE_LIST_KEY, USER_LIVE_LIST_VALUE);
		urls.put(LIVE_WATCHER_LIST_KEY, LIVE_WATCHER_LIST_VALUE);
		urls.put(LIVE_GIFT_LIST_KEY, LIVE_GIFT_LIST_VALUE);
		urls.put(LIVE_BUY_GIFT_KEY, LIVE_BUY_GIFT_VALUE);
		urls.put(SEND_BULLET_KEY, SEND_BULLET_VALUE);
		urls.put(SEND_LIVE_GIFT_KEY, SEND_LIVE_GIFT_VALUE);
		urls.put(SEND_LIVE_LIKE_KEY, SEND_LIVE_LIKE_VALUE);
		urls.put(SHUTUP_USER_KEY, SHUTUP_USER_VALUE);
		urls.put(SHUTUP_OPEN_KEY, SHUTUP_OPEN_VALUE);
		urls.put(IS_SHUTUP_KEY, IS_SHUTUP_VALUE);
		urls.put(LIVE_REPLAY_LIST_KEY, LIVE_REPLAY_LIST_VALUE);
		urls.put(START_LIVE_KEY, START_LIVE_VALUE);

		/** 支付相关接口 */
		urls.put(RECHARGE_LIST_KEY, RECHARGE_LIST_VALUE);
		urls.put(CREATE_ORDER_KEY, CREATE_ORDER_VALUE);
		urls.put(ORDER_NOTICE_KEY, ORDER_NOTICE_VALUE);
		urls.put(EXCHANGE_DIAMOND_KEY, EXCHANGE_DIAMOND_VALUE);
		urls.put(EXCHANGE_ACTION_KEY, EXCHANGE_ACTION_VALUE);
		urls.put(ACCOUNT_MONEY_KEY, ACCOUNT_MONEY_VALUE);
		urls.put(GET_ALI_INFO_KEY, GET_ALI_INFO_VALUE);
		urls.put(VERIFY_ALIPAY_KEY, VERIFY_ALIPAY_VALUE);
		urls.put(WITHDRAW_KEY, WITHDRAW_VALUE);
		urls.put(ACCOUNT_WITHDRAW_MONEY_KEY, ACCOUNT_WITHDRAW_MONEY_VALUE);

		/** 聊天相关接口 */
		urls.put(MATCH_CHAT_KEY, MATCH_CHAT_VALUE);
		urls.put(STOP_MATCH_CHAT_KEY, STOP_MATCH_CHAT_VALUE);
		urls.put(JOIN_CHAT_KEY, JOIN_CHAT_VALUE);
		urls.put(STOP_CHAT_KEY, STOP_CHAT_VALUE);
		urls.put(GET_CHAT_USER_KEY, GET_CHAT_USER_VALUE);
		urls.put(BUY_GIFT_KEY, BUY_GIFT_VALUE);
		urls.put(CHANGE_CHAT_TOPIC_KEY, CHANGE_CHAT_TOPIC_VALUE);
		urls.put(DUE_CHAT_KEY, DUE_CHAT_VALUE);
		urls.put(ACCEPT_CHAT_KEY, ACCEPT_CHAT_VALUE);
		urls.put(SEND_MESSAGE_KEY, SEND_MESSAGE_VALUE);
		urls.put(WEBSCOKET_KEY, WEBSCOKET_VALUE);

		/** 相关接口 */
		urls.put(ABOUTUS_CONTACT_US_KEY, ABOUTUS_CONTACT_US_VALUE);
		urls.put(ABOUTUS_SERVICE_TERMS_KEY, ABOUTUS_SERVICE_TERMS_VALUE);
		urls.put(ABOUTUS_SOCIAL_PACT_KEY, ABOUTUS_SOCIAL_PACT_VALUE);

		urls.put(SYSTEM_UPDATE_KEY, SYSTEM_UPDATE_VALUE);
		urls.put(CANCEL_DUE_CHAT_KEY, CANCEL_DUE_CHAT_VALUE);
	}

	private static boolean hasRequestInit = false;

	/**
	 * 请求初始化地址
	 */
	public static void requestInitUrls() {

		if (hasRequestInit) {
			return;
		}

		UserMgr.getInstance().loadLoginUser();

		HttpParam hp = new HttpParam();

		hp.put("ap", "heihei");
		hp.put("version", AppLogic.VERSION);
		hp.put("buildNumber", AppLogic.BUILD_VERSION);
		hp.put("local", "zh_CN");
		try {
			hp.put("uuid", AppLogic.mPhoneInfo.mIMEI);
			hp.put("model", AppLogic.mPhoneInfo.mManufacturerName + ":" + AppLogic.mPhoneInfo.mModelName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		hp.put("resolution", AppLogic.VERSION);
		hp.put("apiVersion", AppLogic.API_VERSION);
		hp.put("deveiceType", "android");
		hp.put("channel", AppLogic.CHANNEL);

		if (UserMgr.getInstance().isLogined()) {
			hp.put("token", UserMgr.getInstance().getToken());
		}
		HttpUtil.getAsync(INIT_URL, hp, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					hasRequestInit = true;
					if (json != null) {
						JSONObject liveArr = json.optJSONObject(API_MODEL.LIVE);
						parseJSON(API_MODEL.LIVE, liveArr);
						JSONObject userArr = json.optJSONObject(API_MODEL.USER);
						parseJSON(API_MODEL.USER, userArr);
						JSONObject socialArr = json.optJSONObject(API_MODEL.SOCIAL);
						parseJSON(API_MODEL.SOCIAL, socialArr);
						JSONObject messageArr = json.optJSONObject(API_MODEL.MESSAGE);
						parseJSON(API_MODEL.MESSAGE, messageArr);
						JSONObject paymentArr = json.optJSONObject(API_MODEL.PAYMENT);
						parseJSON(API_MODEL.PAYMENT, paymentArr);
						JSONObject chatArr = json.optJSONObject(API_MODEL.CHAT);
						parseJSON(API_MODEL.CHAT, chatArr);
						JSONObject aboutusArr = json.optJSONObject(API_MODEL.ABOUTUS);
						parseJSON(API_MODEL.ABOUTUS, aboutusArr);
						JSONObject tip = json.optJSONObject(API_MODEL.TIPS);
						parseJSON(API_MODEL.TIPS, tip);
					}
				}
			}
		}, true);
	}

	private static void parseJSON(String modelKey, JSONObject json) {
		if (json == null) {
			return;
		}

		Iterator<String> it = json.keys();
		while (it.hasNext()) {
			String key = it.next();
			String value = json.optString(key);
			if (!StringUtils.isEmpty(value)) {
				urls.put(modelKey + key, value);
			}
		}
	}

}
