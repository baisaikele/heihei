/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.utils.emojicon;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class EmojiconSpan extends DynamicDrawableSpan {
    private final Context mContext;
    private final int mResourceId;
    private final int mSize;
    private Drawable mDrawable;
    private final float mLinespacing;
    
    public EmojiconSpan(Context context, int resourceId, int size, float linespacing) {
        super();
        mContext = context;
        mResourceId = resourceId;
        mSize = size;
        mLinespacing = linespacing;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x, 
                     int top, int y, int bottom, Paint paint) {
//    	android.util.Log.d("TOMMY", String.format("draw: text(%s) start(%d), end(%d), x(%f), top(%d), y(%d), bottom(%d), leading(%d)", text, start, end, x, top, y, bottom, paint.getFontMetricsInt().leading));
//    	int length = paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top;
    	int offset = (int) mLinespacing;
//    	if( length > mSize )
//    		offset = offset + (length-mSize)/2;
//    	android.util.Log.d("TOMMY", String.format("Tommy: top(%d), bottom(%d), descent(%d), length(%d), offset(%d) mSize(%d), mLinespacing(%d)", paint.getFontMetricsInt().top, paint.getFontMetricsInt().bottom, paint.getFontMetricsInt().descent, length, offset, mSize, mLinespacing));
    	super.draw(canvas, text, start, end, x, top, y, bottom-offset, paint);
    }

    public Drawable getDrawable() {
        if (mDrawable == null) {
            try {
                mDrawable = mContext.getResources().getDrawable(mResourceId);
                int size = mSize;
                mDrawable.setBounds(0, 0, size, size);
            } catch (Exception e) {
                // swallow
            }
        }
        return mDrawable;
    }
}