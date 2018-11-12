package com.base.widget.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class CustomToast {

    public static final int LENGTH_LONG = 3500;
    public static final int LENGTH_SHORT = 2000;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowParams;
    private TextView toastView;
    private Context mContext;
    private Handler mHandler;
    private String mToastContent = "";
    private int duration = 0;
    private int animStyleId = android.R.style.Animation_Toast;

    private final Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            removeView();
        }
    };

    public CustomToast(Context context) {
        Context ctx = context.getApplicationContext();
        if (ctx == null) {
            ctx = context;
        }
        this.mContext = ctx;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        init();
    }

    private void init() {
        mWindowParams = new WindowManager.LayoutParams();
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.alpha = 1.0f;
        mWindowParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mWindowParams.format = PixelFormat.TRANSLUCENT;
        mWindowParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowParams.setTitle("ToastHelper");
        mWindowParams.packageName = mContext.getPackageName();
        mWindowParams.windowAnimations = animStyleId;// TODO
        mWindowParams.y = mContext.getResources().getDisplayMetrics().widthPixels / 5;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private TextView getDefaultToastView() {
        TextView view = new TextView(mContext);
        view.setText(mToastContent);
        view.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        view.setFocusable(false);
        view.setClickable(false);
        view.setFocusableInTouchMode(false);
        view.setTextColor(android.graphics.Color.WHITE);
        Drawable drawable = mContext.getResources().getDrawable(android.R.drawable.toast_frame);
        view.setBackground(drawable);
        return view;
    }

    public synchronized void show() {
        // remvoew callback
        if (mHandler == null) {
            mHandler = new Handler();
        } else {
            mHandler.removeCallbacks(timerRunnable);
        }
        if (toastView == null) {
            toastView = getDefaultToastView();
        }
        removeView();
        toastView.setText(mToastContent);
        mWindowParams.gravity = android.support.v4.view.GravityCompat.getAbsoluteGravity(Gravity.CENTER_HORIZONTAL
                | Gravity.BOTTOM, android.support.v4.view.ViewCompat.getLayoutDirection(toastView));
        try {
            mWindowManager.addView(toastView, mWindowParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mHandler.postDelayed(timerRunnable, duration);
    }

    public void removeView() {
        if (toastView != null) {
            try {
                mWindowManager.removeView(toastView);
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    }

    public CustomToast setContent(String content) {
        this.mToastContent = content;
        return this;
    }

    public CustomToast setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public CustomToast setAnimation(int animStyleId) {
        this.animStyleId = animStyleId;
        mWindowParams.windowAnimations = this.animStyleId;
        return this;
    }
}
