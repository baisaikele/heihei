package com.base.animator;

import android.animation.TypeEvaluator;

public class NumberEvaluator implements TypeEvaluator<Integer> {

    @Override
    public Integer evaluate(float fraction, Integer startValue, Integer endValue) {

        return (int) ((endValue - startValue) * fraction + startValue);
    }
}
