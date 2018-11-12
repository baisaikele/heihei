package com.heihei.fragment.live.logic;

import java.util.LinkedList;

import com.base.animator.gift.GiftImageView;
import com.base.utils.DeviceInfoUtils;
import com.base.utils.LogWriter;
import com.base.widget.FaceRainView;
import com.heihei.fragment.MainActivity;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.heihei.model.AudienceGift;
import com.heihei.model.Gift;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class GiftAnimationController implements OnGiftAnimationListener
{

    public static final int ANIM_BIANBIAN = 0;// 便便动画
    public static final int ANIM_BUBBLE = 1;// 气球动画
    public static final int ANIM_FINGER = 2;// 拇指动画
    public static final int ANIM_DIAMOND = 3;// 钻石动画
    public static final int ANIM_BAG = 4;// 包包动画

    public static final int STATUS_STOPPED = 0;// 所有动画结束
    public static final int STATUS_RUNNING = 1;// 正在运行
    public static final int STATUS_PAUSED = 2;// 暂停

    public Activity activity;

    // private WindowManager mWindowManager;

    private LinkedList<AudienceGift> gifts = null;

    private int status = STATUS_STOPPED;

    private FrameLayout mContentView;
    private View bgView;

    public GiftAnimationController(Activity activity, FrameLayout mContentView)
    {
        this.activity = activity;
        // mWindowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        gifts = new LinkedList<>();
        this.mContentView = mContentView;
    }

    /**
     * 增加礼物动画
     * 
     * @param gift
     */
    public void addAnimation(AudienceGift gift)
    {
        gifts.add(gift);
        if (!isRunning())
        {
            startAnimation();
        } else
        {
            status = STATUS_RUNNING;
        }
    }

    private boolean isRunning()
    {
        return status == STATUS_RUNNING;
    }

    private void startAnimation()
    {
        if (gifts.size() > 0)
        {
            AudienceGift gift = gifts.removeFirst();
            status = STATUS_RUNNING;
            playAnimation(gift);
        } else
        {
            status = STATUS_STOPPED;
        }
    }

    private void playAnimation(AudienceGift gift)
    {
        switch (gift.gift.type)
        {
        case Gift.TYPE_RAIN_FINGER :
            startRainAnim(ANIM_FINGER, gift);
            break;
        case Gift.TYPE_RAIN_BIANBIAN :
            startRainAnim(ANIM_BIANBIAN, gift);
            break;
        case Gift.TYPE_RAIN_BUBBLE :
            startRainAnim(ANIM_BUBBLE, gift);
            break;
        case Gift.TYPE_FULLSCREEN_DIAMOND :
            startBigGiftAnim(ANIM_DIAMOND, gift);
            break;
        case Gift.TYPE_FULLSCREEN_BAG :
            startBigGiftAnim(ANIM_BAG, gift);
            break;
        }
    }

    /**
     * 表情雨动画
     * 
     * @param animType
     */
    private void startRainAnim(int animType, AudienceGift aGift)
    {

        // if (mRainView != null) {
        // if (mRainView.getParent() != null) {
        // mContentView.removeView(mRainView);
        // }
        // mRainView = null;
        // }

        mContentView.removeAllViews();

        if (mRainView == null)
        {
            mRainView = new FaceRainView(activity);
            mRainView.setData(aGift);
            mContentView.addView(mRainView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        mRainView.start(animType, this);
    }

    /**
     * 大礼物动画
     * 
     * @param animType
     */
    private void startBigGiftAnim(int animType, AudienceGift aGift)
    {

        // if (mGiftImageView != null) {
        // if (mGiftImageView.getParent() != null && mContentView != null) {
        // mContentView.removeView(mGiftImageView);
        // }
        // mGiftImageView = null;
        // }

        mContentView.removeAllViews();

        bgView = new View(activity);
        bgView.setBackgroundColor(activity.getResources().getColor(R.color.hh_color_gift_bg));
        FrameLayout.LayoutParams bgParams = new FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT);
        if (!(activity instanceof MainActivity))
        {
            bgParams.bottomMargin = DeviceInfoUtils.dip2px(activity, 60f);
        }
        mContentView.addView(bgView, bgParams);

        if (mGiftImageView == null)
        {
            mGiftImageView = new GiftImageView(activity);
            mGiftImageView.setData(aGift);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            mContentView.addView(mGiftImageView, params);
        }
        mGiftImageView.start(animType, this);
        EventManager.ins().sendEvent(EventTag.BIG_GIFT_SHOW, 0, 0, null);
    }

    public void pause()
    {
        status = STATUS_PAUSED;
    }

    public void resume()
    {
        status = STATUS_RUNNING;
        if (mRainView == null && mGiftImageView == null)
        {
            startAnimation();
        }

    }

    @Override
    public void onGiftAnimationEnd(View view)
    {
    	LogWriter.d("gift", "animation end");
        if (view != null && view.getParent() != null)
        {
            // if (mContentView != null) {
            // try {
            // mContentView.removeView(view);
            // } catch (Exception e) {
            // // TODO: handle exception
            // }
            //
            // }
            ((ViewGroup) view.getParent()).removeAllViews();
        }

        EventManager.ins().sendEvent(EventTag.BIG_GIFT_DISMISS, 0, 0, null);
        
        if (view instanceof GiftImageView)
        {
            mGiftImageView = null;
        }

        if (view instanceof FaceRainView)
        {
            mRainView = null;
        }

        if (status == STATUS_RUNNING)
        {
            startAnimation();
        }

    }

    public void release()
    {

        this.status = STATUS_STOPPED;

        if (gifts != null)
        {
            gifts.clear();
        }

        // if (mRainView != null && mRainView.getParent() != null) {
        // if (mContentView != null)
        // mContentView.removeView(mRainView);
        // }
        //
        // if (mGiftImageView != null && mGiftImageView.getParent() != null) {
        // if (mContentView != null) {
        // mContentView.removeView(mGiftImageView);
        // }
        //
        // }

        mContentView.removeAllViews();
    }

    private FaceRainView mRainView;

    private GiftImageView mGiftImageView;

    public static class FaceField
    {

        public static final int DEGREES_MIN = -30;// 最小角度
        public static final int DEGREES_MAX = 30;// 最大角度

        public static final float SCALE_MIN = 0.8f;// 缩放最小比例
        public static final float SCALE_MAX = 1.2f;// 缩放最大比例

        public int x;// x坐标
        public int y;// y坐标
        public float scale = 1.0f;
        public int degrees;// 旋转角度
    }

}
