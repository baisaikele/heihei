package com.base.animator;

import android.animation.TypeEvaluator;
import android.graphics.Point;


public class PointEvaluator implements TypeEvaluator<Point>{

    @Override
    public Point evaluate(float fraction, Point startValue, Point endValue) {
        int x = (int) ((endValue.x - startValue.x) * fraction + startValue.x);
        int y = (int) ((endValue.y - startValue.y) * fraction + startValue.y);
        return new Point(x, y);
    }

}
