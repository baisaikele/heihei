package com.base.animator;

import com.wmlives.heihei.R;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;

public class AnimationDrawableUtil {

    public static AnimationDrawable createShakeAnim(Context context)
    {
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.setOneShot(true);
        for (int i = 40; i <= 52; i++) {
            int resId = context.getResources().getIdentifier("pull_down_loading_000" + i, "drawable", context.getPackageName());
            int duration = context.getResources().getInteger(R.integer.pull_down_loading_duration);
            drawable.addFrame(context.getResources().getDrawable(resId), duration);
        }
        return drawable;
    }
    
    public static AnimationDrawable createPullLoadingAnim(Context context) {
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.setOneShot(false);
        for (int i = 53; i <= 92; i++) {
            int resId = context.getResources().getIdentifier("pull_down_loading_000" + i, "drawable", context.getPackageName());
            int duration = context.getResources().getInteger(R.integer.pull_down_loading_duration);
            drawable.addFrame(context.getResources().getDrawable(resId), duration);
        }
        return drawable;
    }
    
    /**
     * 创建铃铛动画
     * @param context
     * @return
     */
    public static AnimationDrawable createBellAnim(Context context)
    {
        AnimationDrawable drawable = new AnimationDrawable();
        drawable.setOneShot(false);
        for (int i = 0; i <= 31; i++) {
            String num = "" + i;
            if (i < 10)
            {
                num = "0" + i;
            }
            int resId = context.getResources().getIdentifier("bell_000" + num, "drawable", context.getPackageName());
            int duration = context.getResources().getInteger(R.integer.pull_down_loading_duration);
            drawable.addFrame(context.getResources().getDrawable(resId), duration);
        }
        return drawable;
    }
    
    
    
}
