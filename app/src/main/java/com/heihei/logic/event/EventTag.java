package com.heihei.logic.event;

public class EventTag
{

    public static final int ACCOUNT_LOGIN = 0x1001;// 登录成功
    public static final int ACCOUNT_LOGOUT = 0x1002;// 注销成功
    public static final int ACCOUNT_UPDATE_INFO = 0x1003;// 用户信息修改
    public static final int ACCOUNT_REFRESH_ONLINE = 0x1004;// 刷新
    public static final int LOCAL_IMAGE_GOTO_COMMON = 0x1005;// 本地图片选择后，通用跳转
    public static final int WX_PAY = 0x1006;// 微信支付
    public static final int ALI_PAY = 0x1007;// 支付宝支付
    public static final int FOLLOW_CHANGED = 0x1008;// 关注状态改变
    public static final int DIAMOND_CHANGED = 0x1009;// 钻石数改变

    public static final int START_CHAT = 0x2001;// 接收约聊
    public static final int CHAT_WITH_SHOW_MESSAGE = 0x2002;// 跳转到黑聊页，同时消息上拉显示

    public static final int STOP_LIVE = 0x3001;// 结束直播
    public static final int STOP_REPLAY = 0x3002;// 结束回放
    public static final int STOP_CHAT = 0x3003;// 结束私聊
    public static final int STOP_AUDIENCE = 0x3004;// 结束观看直播

    public static final int BIG_GIFT_SHOW = 0x4001;// 大礼物显示
    public static final int BIG_GIFT_DISMISS = 0x4002;// 大礼物消失

}
