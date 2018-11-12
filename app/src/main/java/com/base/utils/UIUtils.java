package com.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.base.host.HostApplication;
import com.base.widget.toast.ToastHelper;

public class UIUtils {

    public static void showToast(final String content) {
        HostApplication.getInstance().getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                ToastHelper.makeText(HostApplication.getInstance(), content, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void showToast(final int strId) {
        HostApplication.getInstance().getMainHandler().post(new Runnable() {

            @Override
            public void run() {
                ToastHelper.makeText(HostApplication.getInstance(), strId, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 4.4以上支持透明状态栏 修改状态栏颜色
     */
    public static void setTransparentStatus(Activity activity) {
        setTransparentStatus(activity, android.R.color.transparent);
    }

    /**
     * 4.4以上支持透明状态栏 修改状态栏颜色
     */
    public static void setTransparentStatus(Activity activity, int colorID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            try {
                // 防止布局顶到状态栏要配合CustomInsetFrameLayout使用
                View rootView = ((ViewGroup) (activity.getWindow().getDecorView().findViewById(android.R.id.content)))
                        .getChildAt(0);
                rootView.setFitsSystemWindows(true);
                // 第三方适配库
                SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setTintResource(colorID);
            } catch (Throwable e) {
                // TODO: handle exception
            }

        }
    }

    /**
     * 判断当前应用程序处于前台
     */
    public static boolean isApplicationForground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = context.getApplicationInfo();

            String pkg = context.getApplicationContext().getPackageName();

            int uid = appInfo.uid;

            Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

            try {

                appOpsClass = Class.forName(AppOpsManager.class.getName());

                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                        String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int) opPostNotificationValue.get(Integer.class);
                return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

}
