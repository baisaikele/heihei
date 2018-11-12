package com.base.widget;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.base.animator.NumberEvaluator;
import com.base.utils.StringUtils;

public class MoneyTextView extends TextView {

    public MoneyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public MoneyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MoneyTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    private int money = 0;

    /**
     * 设置money
     * 
     * @param money
     *            单位分
     */
    public void setMoney(final int money) {
        if (this.money == money) {
            return;
        }

        ValueAnimator mAnimator = ValueAnimator.ofObject(new NumberEvaluator(), this.money, money);
        mAnimator.setDuration(1000);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int money = (int) animation.getAnimatedValue();
                setText(StringUtils.fen2yuan(money));
            }
        });
        mAnimator.addListener(new AnimatorListener() {
            
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
                
                
            }
            
            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                
            }
        });
        mAnimator.start();
        MoneyTextView.this.money = money;
    }
}
