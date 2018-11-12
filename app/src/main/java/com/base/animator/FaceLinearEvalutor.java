package com.base.animator;

import java.util.ArrayList;
import java.util.List;

import android.animation.TypeEvaluator;
import android.graphics.Point;
import android.util.Log;

public class FaceLinearEvalutor implements TypeEvaluator<List<Point>> {

    @Override
    public List<Point> evaluate(float fraction, List<Point> startValue, List<Point> endValue) {
        List<Point> result = new ArrayList<>();
        Point point = null;
        Point sp = null;
        Point ep = null;
        for (int i = 0; i < startValue.size(); i++) {
            sp = startValue.get(i);
            ep = endValue.get(i);
            point = new Point();
            point.x = (int) ((ep.x - sp.x) * fraction + sp.x);
            point.y = (int) ((ep.y - sp.y) * fraction + sp.y);

            Log.d("testEvalutor", "x:" + point.x + "--y:" + point.y);

            result.add(point);
        }
        return result;
    }

}
