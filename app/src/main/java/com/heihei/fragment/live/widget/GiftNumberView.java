package com.heihei.fragment.live.widget;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;

import com.base.animator.NumberEvaluator;

/**
 * 礼物数量带动画的view
 * 
 * @author chengbo
 */
public class GiftNumberView extends LinearLayout {

    public GiftNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public GiftNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GiftNumberView(Context context) {
        super(context);
        init();
    }

    @Override
    public void setOrientation(int orientation) {
        if (orientation != LinearLayout.HORIZONTAL) {
            throw new RuntimeException("not support the orientation");
        }
        super.setOrientation(orientation);
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
        initViews();
    }

    private void initViews() {
        removeAllViews();
        String newStr = String.valueOf(currentNum);
        
        createAndAddView("X");
        
        for (int i = 0; i < newStr.length(); i++) {
            createAndAddView(newStr.charAt(i) + "");
        }
    }

    /**
     * 创建并加入一个view
     * 
     * @param num
     */
    private IncreNumberView createAndAddView(String numStr) {
        IncreNumberView inv = new IncreNumberView(getContext());
        inv.setDefaultNumber(numStr);
        addView(inv, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        return inv;
    }

    private int currentNum = 0;

    /**
     * 数字增长
     * 
     * @param number
     */
    public void increaseNumber(int number) {
        setNumber(currentNum + number);
    }

    public void setNumber(int number) {
        if (number < 0) {
            throw new RuntimeException("");
        }
        if (number == 0) {
            this.currentNum = number;
            removeAllViews();
            initViews();
            return;
        }
        String oldStr = String.valueOf(currentNum);
        if (currentNum == number) {
            return;
        }

        if (number > currentNum) {// 数字变大了
            String newStr = String.valueOf(number);
            if (oldStr.length() == newStr.length()) {// 如果位数相等则直接设置数字，显示动画
                ArrayList<IncreNumberView> childs = new ArrayList<>();//需要做动画的view集合
                for (int i = 0; i < oldStr.length(); i++) {
                    IncreNumberView inv = (IncreNumberView) getChildAt(i + 1);
                    String a = newStr.charAt(i) + "";
                    if (!a.equals(inv.getNumber())) {
                        childs.add(inv);
                        inv.setNumber(a);
                    }
                    
                }

                createAnimator(childs).start();

            } else if (oldStr.length() < newStr.length()) {//如果位数不相等则需要新增view
                ArrayList<IncreNumberView> childs = new ArrayList<>();//需要做动画的view集合
                for (int i = 0; i < newStr.length(); i++) {
                    if (i < oldStr.length()) {
                        IncreNumberView inv = (IncreNumberView) getChildAt(i + 1);
                        String a = newStr.charAt(i) + "";
                        if (!a.equals(inv.getNumber())) {
                            childs.add(inv);
                        }
                        inv.setNumber(a);

                    } else {
                        IncreNumberView inv = createAndAddView(newStr.charAt(i) + "");
                        childs.add(inv);
                    }

                    createAnimator(childs).start();

                }
            }

            currentNum = number;

        } else {
            currentNum = number;
            initViews();
        }

    }

    private ValueAnimator createAnimator(final ArrayList<IncreNumberView> views) {
        ValueAnimator animator = ValueAnimator.ofObject(new NumberEvaluator(), 0, getHeight());
        animator.setDuration(200);
        animator.setInterpolator(new OvershootInterpolator(2.0f));
        animator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (int) animation.getAnimatedValue();
                for (int i = 0; i < views.size(); i++) {
                    views.get(i).setOffset(offset);
                }
            }
        });
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                views.clear();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub

            }
        });
        return animator;
    }
}
