package com.base.animator.gift;

import java.util.LinkedList;

import android.content.Context;

public class GiftAnimationUtil {

    /**
     * 创建钻石动画
     * 
     * @param context
     * @return
     */
    public static GiftAnimation createDiamondGiftAnimation(Context context) {
        LinkedList<GiftAnimationItem> items = new LinkedList<>();
        for (int i = 3; i <= 106; i++) {
            String num = "" + i;
            if (i < 10) {
                num = "00" + i;
            } else if (i < 100) {
                num = "0" + i;
            }
            int resId = context.getResources().getIdentifier("diamond_00" + num, "drawable", context.getPackageName());
            items.addLast(new GiftAnimationItem(resId, 40));
        }
        GiftAnimation anim = new GiftAnimation(items);
        return anim;
    }

    /**
     * 创建包包动画
     * 
     * @param context
     * @return
     */
    public static GiftAnimation createBagGiftAnimation(Context context) {
        LinkedList<GiftAnimationItem> items = new LinkedList<>();
        for (int i = 3; i <= 107; i++) {
            String num = "" + i;
            if (i < 10) {
                num = "00" + i;
            } else if (i < 100) {
                num = "0" + i;
            }
            int resId = context.getResources().getIdentifier("bag_00" + num, "drawable", context.getPackageName());
            items.addLast(new GiftAnimationItem(resId, 40));
        }
        GiftAnimation anim = new GiftAnimation(items);
        return anim;
    }

    /**
     * 创建loading动画
     * 
     * @param context
     * @return
     */
    public static GiftAnimation createLoadingAnimation(Context context) {
        LinkedList<GiftAnimationItem> items = new LinkedList<>();
        for (int i = 0; i <= 33; i++) {
            String num = "" + i;
            if (i < 10) {
                num = "0" + i;
            }
            int resId = context.getResources().getIdentifier("loading_000" + num, "drawable", context.getPackageName());
            items.addLast(new GiftAnimationItem(resId, 40));
        }
        GiftAnimation anim = new GiftAnimation(items);
        return anim;
    }

}
