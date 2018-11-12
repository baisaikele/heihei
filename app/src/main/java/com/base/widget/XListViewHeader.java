package com.base.widget;

/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;

import com.base.widget.cobe.ptr.header.HeiheiPullHeader;
import com.base.widget.cobe.ptr.indicator.PtrIndicator;

public class XListViewHeader extends LinearLayout {

//    private LinearLayout mContainer;
    // private ImageView mArrowImageView;
    // private ProgressBar mProgressBar;
    // private TextView mHintTextView, time;

//    private JiyuHeaderArrow jiyu_arrow;
    private HeiheiPullHeader mHeihei_arrow;

    private int mState = -1;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    public XListViewHeader(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public XListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    // @Override
    // protected void onLayout(boolean changed, int l, int t, int r, int b) {
    // // TODO Auto-generated method stub
    // super.onLayout(changed, l, t, r, b);
    // if (isInTouchMode()) {
    // return;
    // }
    // }
    //
    // @Override
    // protected void onFinishInflate() {
    // // TODO Auto-generated method stub
    // super.onFinishInflate();
    // if (isInTouchMode()) {
    // return;
    // }
    // }

    private void initView(Context context) {

        this.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
//        mContainer = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.xlistview_header, null);
        mHeihei_arrow = new HeiheiPullHeader(getContext());
        
        if (pi == null)
        {
            pi = new PtrIndicator();
        }
        
        if (pi.getHeaderHeight() <= 0)
        {
            int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
            int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            mHeihei_arrow.measure(widthMeasureSpec, heightMeasureSpec);
            pi.setHeaderHeight(mHeihei_arrow.getMeasuredHeight());
            initHeight = mHeihei_arrow.getMeasuredHeight();
        }
        
        addView(mHeihei_arrow, lp);
//        setGravity(Gravity.TOP);

        // mArrowImageView = (ImageView) findViewById(R.id.xlistview_header_arrow);
        // mHintTextView = (TextView) findViewById(R.id.xlistview_header_hint_textview);
        // mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_progressbar);
        // time = (TextView) findViewById(R.id.xlistview_header_time);

//        mHeihei_arrow = (HeiheiPullHeader) findViewById(R.id.heihei_arrow);// 新的下拉刷新

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    private int initHeight = 0;
    
    public int getInitContentHeight()
    {
        return initHeight;
    }
    
    
    public View getContentView()
    {
        return mHeihei_arrow;
    }
    
    public void setState(int state) {
        if (state == mState)
            return;
        // if (state == STATE_REFRESHING) { // flush
        // mArrowImageView.clearAnimation();
        // mArrowImageView.setVisibility(View.INVISIBLE);
        // mProgressBar.setVisibility(View.VISIBLE);
        // } else { // ��ʾ��ͷͼƬ
        // mArrowImageView.setVisibility(View.VISIBLE);
        // mProgressBar.setVisibility(View.INVISIBLE);
        // }
        //
        // switch (state) {
        // case STATE_NORMAL :
        // if (mState == STATE_READY) {
        // mArrowImageView.startAnimation(mRotateDownAnim);
        // }
        // if (mState == STATE_REFRESHING) {
        // mArrowImageView.clearAnimation();
        // }
        // mHintTextView.setText(R.string.xlistview_header_hint_normal);
        // break;
        // case STATE_READY :
        // if (mState != STATE_READY) {
        // mArrowImageView.clearAnimation();
        // mArrowImageView.startAnimation(mRotateUpAnim);
        // mHintTextView.setText(R.string.xlistview_header_hint_ready);
        // }
        // break;
        // case STATE_REFRESHING :
        // mHintTextView.setText(R.string.xlistview_header_hint_loading);
        // break;
        // default:
        // }

        switch (state) {
        case STATE_NORMAL : {
            mHeihei_arrow.onUIRefreshPrepare(null);
        }
            break;
        case STATE_READY : {
        }
            break;
        case STATE_REFRESHING : {
            mHeihei_arrow.onUIRefreshBegin(null);
            // jiyu_arrow.onUIRefreshComplete(null);
        }
            break;
        }

        mState = state;
    }

    private PtrIndicator pi = null;
    
    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        
        Log.d("pull:", "height:"+height);
        
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mHeihei_arrow.getLayoutParams();
        lp.height = height;
        mHeihei_arrow.setLayoutParams(lp);
        
        pi.setCurrentPos(height);
//        pi.setOffsetToRefresh(mHeihei_arrow.getMeasuredHeight());
//        pi.setHeaderHeight(height);
        byte a = 1;
        mHeihei_arrow.onUIPositionChange(null, false, a, pi);
    }

    public int getVisiableHeight() {
        return mHeihei_arrow.getHeight();
    }

}
