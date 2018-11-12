package com.base.animator;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF mPoint1;//贝塞尔拐点1
    private PointF mPoint2;//贝塞尔拐点2
    
    public BezierEvaluator(PointF mPoint1,PointF mPoint2)
    {
        this.mPoint1 = mPoint1;
        this.mPoint2 = mPoint2;
    }
    
    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        final float t = fraction;
        float oneMinusT = 1.0f - t;
        PointF point = new PointF();
        PointF point0 = (PointF) startValue;
        PointF point1 = new PointF();
        point1.set(mPoint1.x, mPoint1.y);
        PointF point2 = new PointF();
        point2.set(mPoint2.x, mPoint2.y);
        PointF point3 = (PointF) endValue;

        point.x = oneMinusT * oneMinusT * oneMinusT * (point0.x) + 3 * oneMinusT * oneMinusT * t * (point1.x) + 3
                * oneMinusT * t * t * (point2.x) + t * t * t * (point3.x);

        point.y = oneMinusT * oneMinusT * oneMinusT * (point0.y) + 3 * oneMinusT * oneMinusT * t * (point1.y) + 3
                * oneMinusT * t * t * (point2.y) + t * t * t * (point3.y);
        return point;
    }

}
