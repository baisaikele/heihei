package com.base.widget;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ListView;
import android.widget.Scroller;

import com.heihei.fragment.MainActivity;

public class CustomGallery extends ViewPager {

    private final static int TIME = 2 * 1000;

    private boolean down = false;
    private float mLastMotionY;
    private float mLastMotionX;

    public CustomGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        setAnimationCacheEnabled(false);
        setDrawingCacheEnabled(false);
        setChildrenDrawingCacheEnabled(false);
        setAlwaysDrawnWithCacheEnabled(false);
        setFadingEdgeLength(0);
        setHorizontalFadingEdgeEnabled(false);
        setStaticTransformationsEnabled(false);
        setSoundEffectsEnabled(false);
        changeViewPageScroller();
    }

    private void changeViewPageScroller() {  
        try {  
            Field mField = ViewPager.class.getDeclaredField("mScroller");  
            mField.setAccessible(true);  
            FixedSpeedScroller scroller;  
            scroller = new FixedSpeedScroller(getContext(),new AccelerateDecelerateInterpolator());  
            mField.set(this, scroller);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
      
    }  
    
    // @Override
    // public boolean dispatchTouchEvent(MotionEvent ev) {
    // // TODO Auto-generated method stub
    // final float y = ev.getY();
    // final float x = ev.getX();
    // switch (ev.getAction()) {
    // case MotionEvent.ACTION_DOWN :
    // getParent().requestDisallowInterceptTouchEvent(true);
    // down = true;
    // mLastMotionY = y;
    // mLastMotionX = x;
    // break;
    // case MotionEvent.ACTION_MOVE :
    // if (down) {
    // int dey = (int) Math.abs(y - mLastMotionY);
    // int dex = (int) Math.abs(x - mLastMotionX);
    // if (dey > 0 && dey > dex) {
    // down = false;
    // getParent().requestDisallowInterceptTouchEvent(false);
    // }
    // }
    // break;
    // case MotionEvent.ACTION_CANCEL :
    // case MotionEvent.ACTION_UP :
    // getParent().requestDisallowInterceptTouchEvent(false);
    // break;
    // }
    // return super.dispatchTouchEvent(ev);
    // }

    // @Override
    // protected boolean getChildStaticTransformation(View child, Transformation t) {
    // // TODO Auto-generated method stub
    // return false;
    // }

    private void moveNext() {

        if (removed) {
            return;
        }

        if (getAdapter() == null) {
            return;
        }

        int index = getCurrentItem();
        if (++index >= getAdapter().getCount()) {
            index = 0;
        }
        setCurrentItem(index, true);

        if (!removed) {
            Message msg = Message.obtain();
            msg.obj = this;
            msg.what = 0;
            mHandler.sendMessageDelayed(msg, 2000);
        }

    }

    int getRelativeTop() {
        int top = 0;
        View current = this;
        // do
        // {
        //
        // } while ();

        while (!(current instanceof ListView)) {
            if (current == null) {
                return 0 - getMeasuredHeight();
            }
            top += current.getTop();
            if (current.getParent() instanceof View) {
                current = (View) current.getParent();
            } else {
                return top;
            }

        }

        return top;
    }

    private static Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            removeMessages(0);
            CustomGallery gallery = (CustomGallery) msg.obj;

            // gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
            gallery.moveNext();

        };
    };

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN :
            removeLoop();
        case MotionEvent.ACTION_MOVE :
            break;
        case MotionEvent.ACTION_CANCEL :
        case MotionEvent.ACTION_UP :
            startLoop();
            break;
        default:

            break;
        }
        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    };

    // @Override
    // public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    // // TODO Auto-generated method stub
    // int keyCode;
    // mHandler.removeMessages(0, this);
    // if (isScrollingLeft(e1, e2)) {
    // keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
    // } else {
    // keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
    // }
    // onKeyDown(keyCode, null);
    // return true;
    // }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // // TODO Auto-generated method stub
    // super.onKeyDown(keyCode, event);
    // return true;
    // }
    //
    // private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
    // if (e2 == null || e1 == null) {
    // return false;
    // }
    // return e2.getX() > e1.getX();
    // }

    public void setAdapter(PagerAdapter adapter) {
        // flag = true;
        super.setAdapter(adapter);
        if (adapter.getCount() > 0) {
            startLoop();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        // TODO Auto-generated method stub
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        super.onDetachedFromWindow();
        removeLoop();
    }

    // private boolean flag = true;

    // protected void onLayout(boolean changed, int l, int t, int r, int b) {
    // // TODO Auto-generated method stub
    // if (!changed && !flag) {
    // return;
    // }
    // flag = false;
    //
    // super.onLayout(changed, l, t, r, b);
    // }

    private boolean removed = false;

    public void startLoop() {
        mHandler.removeMessages(0);
        removed = false;
        Message msg = Message.obtain();
        msg.obj = this;
        msg.what = 0;
        mHandler.sendMessageDelayed(msg, 2000);

    }

    public void removeLoop() {
        mHandler.removeMessages(0);
        removed = true;
    }

}

class FixedSpeedScroller extends Scroller {

    private int mDuration = 450;

    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public void setmDuration(int time) {
        mDuration = time;
    }

    public int getmDuration() {
        return mDuration;
    }

};
