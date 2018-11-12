package com.base.animator;

import com.heihei.fragment.live.logic.GiftAnimationController.FaceField;

import android.animation.TypeEvaluator;


public class FaceFieldEvaluator implements TypeEvaluator<FaceField> {

    int rotateCount = 6;
    int maxDegrees = FaceField.DEGREES_MAX - FaceField.DEGREES_MIN;
    float onceRotateTime = 1.0f / rotateCount;
    
    int scaleCount = 2;
    float onceScaleTime = 1.0f / scaleCount;
    float maxScale = FaceField.SCALE_MAX - FaceField.SCALE_MIN;

    boolean reverse = false;

    @Override
    public FaceField evaluate(float fraction, FaceField startValue, FaceField endValue) {
        FaceField field = new FaceField();
//        field.x = (int) ((endValue.x - startValue.x) * fraction + startValue.x);
//        field.y = (int) ((endValue.y - startValue.y) * fraction + startValue.y);
        
        float userT = startValue.degrees * onceRotateTime / maxDegrees;

        float degressFraction = userT + fraction;
        if (degressFraction > 1.0f) {
            degressFraction = degressFraction - 1.0f;
        }

        int t = (int) (degressFraction / onceRotateTime);
        
        int degrees = 0;

        degrees = (int) (maxDegrees * (degressFraction - onceRotateTime * t) / onceRotateTime);

        if (t % 2 == 0)// 正向
        {

        } else// 反向
        {
            degrees = maxDegrees - degrees;
        }
        field.degrees = degrees;

        
        
        /** 计算缩放比例 */
        float userScaleT = startValue.scale * onceScaleTime / maxScale;

        float scaleFraction = userScaleT + fraction;
        if (scaleFraction > 1.0f) {
            scaleFraction = scaleFraction - 1.0f;
        }

        int scaleT = (int) (scaleFraction / onceScaleTime);
        
        float scale = 1.0f;
        
        scale = maxScale * (scaleFraction - onceScaleTime * scaleT) / onceScaleTime;

        if (scaleT % 2 == 0)// 正向
        {
            
        } else// 反向
        {
            scale = maxScale - scale;
        }
        field.scale = scale;
        
        return field;
    }

}
