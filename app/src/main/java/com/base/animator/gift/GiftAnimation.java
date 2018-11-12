package com.base.animator.gift;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.os.Message;

public class GiftAnimation implements Animatable {

    private LinkedList<GiftAnimationItem> items;

    public static final int STATE_STOPED = 0;
    public static final int STATE_RUNNING = 1;

    public int state = STATE_STOPED;

    public GiftAnimation() {
        items = new LinkedList<>();
    }

    public GiftAnimation(LinkedList<GiftAnimationItem> items) {
        this.items = items;
    }

    @Override
    public void start() {
        if (items == null || items.isEmpty() || isRunning()) {
            return;
        }
        this.state = STATE_RUNNING;
        if (this.mAnimationListener != null) {
            this.mAnimationListener.onAnimationStart(this);
        }
        ticker();
    }

    @Override
    public void stop() {
        this.state = STATE_STOPED;
        items.clear();
        mHandler.removeMessages(0);
        if (this.mAnimationListener != null) {
            this.mAnimationListener.onAnimationEnd(this);
        }
    }

    @Override
    public boolean isRunning() {
        return state == STATE_RUNNING;
    }

    /**
     * 获取总帧数
     */
    public int getNumberOfFrames() {
        if (items == null || items.isEmpty()) {
            return 0;
        }
        return items.size();
    }

    /**
     * 获取动画总时长
     * 
     * @return
     */
    public int getTotalDuration() {
        if (items == null || items.isEmpty()) {
            return 0;
        }

        int duration = 0;

        Iterator<GiftAnimationItem> iterator = items.iterator();

        while (iterator.hasNext()) {

            GiftAnimationItem item = iterator.next();
            duration += item.duration;
        }
        return duration;

    }

    /*
     * 循环
     */
    private void ticker() {
        mHandler.removeMessages(0);
        if (!items.isEmpty()) {
            GiftAnimationItem item = items.removeFirst();
            if (this.mAnimationListener != null) {
                this.mAnimationListener.onAnimationRunning(this, item);
            }
            if (!items.isEmpty()) {
                if (isRunning()) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = new WeakReference<GiftAnimation>(this);
                    mHandler.sendMessageDelayed(msg, item.duration);
                }
            } else {
                stop();
            }

        } else {
            stop();
        }

    }

    private GiftAnimationListener mAnimationListener;

    public void setAnimationListener(GiftAnimationListener mAnimationListener) {
        this.mAnimationListener = mAnimationListener;
    }

    public static interface GiftAnimationListener {

        public void onAnimationStart(GiftAnimation animation);

        public void onAnimationRunning(GiftAnimation animation, GiftAnimationItem item);

        public void onAnimationEnd(GiftAnimation animation);
    }

    private static Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            @SuppressWarnings("unchecked")
            WeakReference<GiftAnimation> wr = (WeakReference<GiftAnimation>) msg.obj;
            if (wr != null && wr.get() != null) {
                wr.get().ticker();
            }
        };
    };

}
