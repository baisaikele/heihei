package com.base.animator;

import android.animation.TimeInterpolator;


public class ReverseLinearInterpolator implements TimeInterpolator{

    @Override
    public float getInterpolation(float input) {
        // TODO Auto-generated method stub
        return 1.0f - input;
    }

}
