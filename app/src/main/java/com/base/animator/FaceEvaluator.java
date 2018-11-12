package com.base.animator;

import java.util.ArrayList;
import java.util.List;

import android.animation.TypeEvaluator;
import android.graphics.Point;

public class FaceEvaluator implements TypeEvaluator<List<Point>> {

    @Override
    public List<Point> evaluate(float fraction, List<Point> startValue, List<Point> endValue) {

        final float t = fraction;
        float oneMinusT = 1.0f - t;
        List<Point> results = new ArrayList<>();

        Point point = null;
        Point point0 = null;
        Point point1 = null;
        Point point2 = null;
        Point point3 = null;

        int count = startValue.size();
        for (int i = 0; i < count; i++) {
            point = new Point();

            point0 = startValue.get(i);
            point3 = endValue.get(i);
            point1 = new Point();
            point2 = new Point();
            if (point0.x < point3.x) {
                point1.x = point0.x - 200;
                point1.y = point3.y / 3;

                point2.x = point0.x + 200;
                point2.y = point3.y / 3 * 2;

            } else {
                point1.x = point0.x + 100;
                point1.y = point3.y / 3;

                point2.x = point0.x - 100;
                point2.y = point3.y / 3 * 2;
            }

            point.x = (int) (oneMinusT * oneMinusT * oneMinusT * (point0.x) + 3 * oneMinusT * oneMinusT * t
                    * (point1.x) + 3 * oneMinusT * t * t * (point2.x) + t * t * t * (point3.x));

            point.y = (int) (oneMinusT * oneMinusT * oneMinusT * (point0.y) + 3 * oneMinusT * oneMinusT * t
                    * (point1.y) + 3 * oneMinusT * t * t * (point2.y) + t * t * t * (point3.y));
            results.add(point);
        }
        return results;

    }

    public static class FacePoint {

        public Point startPoint;

        public Point mPoint1;

        public Point mPoint2;

        public Point endPoint;
    }

}
