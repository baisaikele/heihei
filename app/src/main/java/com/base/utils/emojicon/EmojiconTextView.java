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

import com.wmlives.heihei.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconTextView extends TextView {
    private int mEmojiconSize;
    private float mLinespacing = 0;
    private boolean mSystemSupportEmoji = false;
    private boolean isFocused = true;

    public EmojiconTextView(Context context) {
        super(context);
        init(null);
    }

//    @Override
//    public boolean isFocused() {
//        if (isFocused)
//            return true;
//        else return false;
//    }

    public EmojiconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public EmojiconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            mSystemSupportEmoji = true;
        }

        if (attrs == null) {
            mEmojiconSize = (int) getTextSize();
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Emojicon);
            mEmojiconSize = (int) a.getDimension(R.styleable.Emojicon_emojiconSize, getTextSize());
            mLinespacing = a.getDimension(R.styleable.Emojicon_emojiLineSpacingExtra, 0);
            a.recycle();
        }

        if (mLinespacing != 0)
            setLineSpacing(mLinespacing, (float) 1.0);

        if (mSystemSupportEmoji == false) {
            setText(getText());
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (mSystemSupportEmoji == false) {
            if (text != null) {
                SpannableStringBuilder builder = new SpannableStringBuilder(text);
                EmojiconHandler.addEmojis(getContext(), builder, mEmojiconSize, mLinespacing);
                super.setText(builder, type);
            } else
                super.setText(text, type);
        } else
            super.setText(text, type);
    }

    /**
     * Set the size of emojicon in pixels.
     */
    public void setEmojiconSize(int pixels) {
        mEmojiconSize = pixels;
    }
}
