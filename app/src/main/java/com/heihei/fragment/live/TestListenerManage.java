package com.heihei.fragment.live;


public class TestListenerManage {

    private static TestListenerManage mIns;

    private OnTestListener mListener;
    
    private TestListenerManage() {

    }

    public static TestListenerManage getInstance() {
        if (mIns == null) {
            mIns = new TestListenerManage();
        }
        return mIns;
    }

    public void setOnTestListener(OnTestListener mListener)
    {
        this.mListener = mListener;
    }
    
    public static interface OnTestListener
    {
        public void onTest();
    }
    
}
