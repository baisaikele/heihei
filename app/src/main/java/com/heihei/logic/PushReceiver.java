package com.heihei.logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.base.host.AppLogic;
import com.base.host.HostApplication;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.heihei.fragment.link.OutLinkActivity;
import com.heihei.logic.present.UserPresent;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.wmlives.heihei.R;

import org.json.JSONException;
import org.json.JSONObject;

public class PushReceiver extends BroadcastReceiver
{

    public static boolean pushDeviceInfo = false;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (!UserMgr.getInstance().isLogined())
            return;
        Bundle bundle = intent.getExtras();
        switch (bundle.getInt(PushConsts.CMD_ACTION))
        {
        case PushConsts.GET_CLIENTID :
            String cid = bundle.getString("clientid");
            LogWriter.d("client", "push:" + cid);
            if (UserMgr.getInstance().isLogined())
            {
                if (!pushDeviceInfo)
                {
                    new UserPresent().pushDeviceInfo(AppLogic.mPhoneInfo.mIMEI, cid, new JSONResponse()
                    {

                        @Override
                        public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached)
                        {
                            if (errCode == ErrorCode.ERROR_OK)
                            {
                                pushDeviceInfo = true;
                            }

                        }
                    });
                }
            }

            break;
        case PushConsts.GET_MSG_DATA :
            // 获取透传数据
            // String appid = bundle.getString("appid");
            byte[] payload = bundle.getByteArray("payload");

            String taskid = bundle.getString("taskid");
            String messageid = bundle.getString("messageid");

            // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
            boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
            System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

            if (payload != null)
            {
                String data = new String(payload);

                LogWriter.d("push", "data:" + data.toString());

                try
                {
                    JSONObject json = new JSONObject(data);
                    String title = json.optString("title");
                    String text = json.optString("text");
                    String link = json.optString("link");
                    String msgId = json.optString("msgId");

                    if (!StringUtils.isEmpty(msgId))
                    {
                        if (link.contains("?"))
                        {
                            link = link + "&msgId=" + msgId;
                        } else
                        {
                            link = link + "?" + "msgId=" + msgId;
                        }
                    }

                    setNotification(title, text, link);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

            }
            break;
        }
    }

    private static NotificationManager mNotificationManager;
    private static Notification notification;

    private static int notifyId = 1;

    /**
     * 发送通知
     */
    public static void setNotification(String title, String text, String link)
    {

        /** start */
        // 1.得到NotificationManager：
        mNotificationManager = (NotificationManager) HostApplication.getInstance().getSystemService(
                Context.NOTIFICATION_SERVICE);
//        // 创建一个新的Notification对象，并添加图标
//        notification = new Notification();
//        // 通知显示的图标
//        notification.icon = R.drawable.ic_launcher;
//        notification.largeIcon = BitmapFactory.decodeResource(HostApplication.getInstance().getResources(),
//                R.drawable.ic_launcher);
//        // 在状态栏(Status Bar)显示的通知文本提示，如：
//        notification.tickerText = "收到一个新的通知";
//        // 发出提示音，如：
//        notification.defaults |= Notification.DEFAULT_SOUND;// 或
//        // 填充Notification的各个属性：
        Context context = HostApplication.getInstance();
        CharSequence contentTitle = title;
        CharSequence contentText = text;
        // 点击通知跳转到哪里
        Intent notificationIntent = new Intent(HostApplication.getInstance(), OutLinkActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_DEFAULT);
        notificationIntent.setAction(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(link));
        PendingIntent contentIntent = PendingIntent
                .getActivity(HostApplication.getInstance(), 0, notificationIntent, 0);


        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker("收到一个新的通知")
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setContentIntent(contentIntent);


        Notification noti = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = builder.build();
        }else {
           noti= builder.getNotification();
        }


        // 在通知栏上点击此通知后自动清除此通知
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        // LED灯闪烁
        noti.defaults |= Notification.DEFAULT_LIGHTS;
        // 或者可以自己的LED提醒模式:
        // notification.ledARGB = 0xff00ff00;
        // notification.ledOnMS = 300; //亮的时间
        // notification.ledOffMS = 1000; //灭的时间
        // notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        // 手机振动
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        // 或
        // long[] vibrate = {0,100,200,300};
        // notification.vibrate = vibrate;
        // 发送通知
        
       
        mNotificationManager.notify(notifyId++, notification);
        LogWriter.i("isBackground", "mNotificationManager.notify");
        /** end */
    }

    /**
     * 更新通知
     */
//    public void updateNotification()
//    {
        // notification.tickerText = "收到第二个新的通知";
        // Context context = getApplicationContext();
        // CharSequence contentTitle = "点击跳转";
        // CharSequence contentText = "跳转到添加会员";
        // //点击通知跳转到哪里
        // Intent notificationIntent = new Intent(this,
        // MemberDetailsActivity.class);
        // PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        // notificationIntent, 0);
        // notification.setLatestEventInfo(context, contentTitle, contentText,
        // contentIntent);
        // mNotificationManager.notify(1, notification);
//    }

}
