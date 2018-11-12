package com.base.widget.toast;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    private static CustomToast toast = null;

    public static CustomToast makeText(Context context, String content, int duration) {
        initToast(context);
        toast.setContent(content);
        if (duration == Toast.LENGTH_SHORT) {
            duration = CustomToast.LENGTH_SHORT;
        } else if (duration == Toast.LENGTH_LONG) {
            duration = CustomToast.LENGTH_LONG;
        }
        toast.setDuration(duration);
        return toast;
    }

    public static CustomToast makeText(Context context, int strId, int duration) {
        initToast(context);
        toast.setContent(context.getString(strId));
        if (duration == Toast.LENGTH_SHORT) {
            duration = CustomToast.LENGTH_SHORT;
        } else if (duration == Toast.LENGTH_LONG) {
            duration = CustomToast.LENGTH_LONG;
        }
        toast.setDuration(duration);
        return toast;
    }

    private static void initToast(Context context) {
        if (null == toast) {
            toast = new CustomToast(context);
        }
    }

}
