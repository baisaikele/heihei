package com.base.widget.cobe.ptr;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.base.widget.cobe.ptr.header.HeiheiPullHeader;
import com.base.widget.cobe.ptr.header.JiyuHeaderArrow;

public class PtrClassicFrameLayout extends PtrFrameLayout {

    private PtrClassicDefaultHeader mPtrClassicHeader;
    private PtrUIHandler ptruihandler;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        // mPtrClassicHeader = new PtrClassicDefaultHeader(getContext());
        // setHeaderView(mPtrClassicHeader);
        // addPtrUIHandler(mPtrClassicHeader);

//        ptruihandler = new JiyuHeaderArrow(getContext());
        ptruihandler = new HeiheiPullHeader(getContext());
        setHeaderView((View) ptruihandler);
        addPtrUIHandler(ptruihandler);
    }

    public View getHeader() {
        if (mPtrClassicHeader != null)
            return mPtrClassicHeader;
        return (View) ptruihandler;
    }

    /**
     * Specify the last update time by this key string
     * 
     * @param key
     */
    public void setLastUpdateTimeKey(String key) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeKey(key);
        }
    }

    /**
     * Using an object to specify the last update time.
     * 
     * @param object
     */
    public void setLastUpdateTimeRelateObject(Object object) {
        if (mPtrClassicHeader != null) {
            mPtrClassicHeader.setLastUpdateTimeRelateObject(object);
        }
    }
}
