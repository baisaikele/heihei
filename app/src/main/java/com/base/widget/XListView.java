package com.base.widget;

/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Scroller;
import android.widget.TextView;

import com.base.utils.DeviceInfoUtils;
import com.base.utils.FileUtils.SharePrefrenceUtil;
import com.wmlives.heihei.R;

public class XListView extends ListView implements OnScrollListener {

    private float mLastY = -1; // save event y
    private Scroller mScroller; // used for scroll back
    private OnScrollListener mScrollListener; // user's scroll listener

    // the interface to trigger refresh and load more.
    private IXListViewListener mListViewListener;

    // -- header view
    public XListViewHeader mHeaderView;
    // header view content, use it to calculate the Header's height. And hide it
    // when disable pull refresh.
    private View mHeaderViewContent;
    private TextView mHeaderTimeView;
    private int mHeaderViewHeight; // header view's height
    private boolean mEnablePullRefresh = false;
    private boolean mPullRefreshing = false; // is refreashing.
    private View searchView;
    // -- footer view
    public XListViewFooter mFooterView;
    private boolean mEnablePullLoad = false;
    private boolean mPullLoading;
    private boolean mIsFooterReady = false;
    private boolean mAutoLoadMore = true;// 是否自动加载更多

    // total list items, used to detect is at the bottom of listview.
    private int mTotalItemCount;

    // for mScroller, scroll back from header or footer.
    private int mScrollBack;
    private final static int SCROLLBACK_HEADER = 0;
    private final static int SCROLLBACK_FOOTER = 1;

    private final static int SCROLL_DURATION = 400; // scroll back duration
    private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
                                                        // at bottom, trigger
                                                        // load more.
    private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
                                                    // feature.
    Context context;
    private boolean isAgain;

    public boolean mIsFocused = false;

    /**
     * @param context
     */
    public XListView(Context context) {
        super(context);
        // LogUtils.d(UIUtils.TAG, "----XListView context");
        initWithContext(context);
        this.context = context;

    }

    public XListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // initWithContext(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XListView, defStyle, 0);
        boolean isAddBeofre = a.getBoolean(R.styleable.XListView_is_add_loadheaderview_before, false);
        if (isAddBeofre) {
            View view = new View(context);
            view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, DeviceInfoUtils
                    .dip2px(context, 53)));
            addHeaderView(view);
        }
        a.recycle();
        initWithContext(context);
    }

    private void initWithContext(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
        // XListView need the scroll event, and it will dispatch the event to
        // user's listener (as a proxy).
        super.setOnScrollListener(this);
        setCacheColorHint(0);

        // init header view
        mHeaderView = new XListViewHeader(context);

        coverImg = (ImageView) mHeaderView.findViewById(R.id.xlist_header_img);
        // mHeaderViewContent = mHeaderView.findViewById(R.id.xlistview_header_content);
        // mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
//        mHeaderViewContent = mHeaderView.findViewById(R.id.heihei_arrow);// 新的下拉刷新
        mHeaderViewContent = mHeaderView.getContentView();
        // init header height
        addHeaderView(mHeaderView);
        // init footer view
        mFooterView = new XListViewFooter(context);
//        mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
//
//            @Override
//            public void onGlobalLayout() {
//                mHeaderViewHeight = screen == 0 ? mHeaderViewContent.getHeight() : screen;
//                getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                Log.e("cccmax", "mHeaderViewHeight height=" + mHeaderViewHeight);
//            }
//        });
        mHeaderViewHeight = mHeaderView.getInitContentHeight();
        setScrollingCacheEnabled(false);

        mHeaderView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mPullRefreshing) {
                    mPullRefreshing = true;
                    mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                    if (mListViewListener != null) {
                        mListViewListener.onRefresh(true);
                    }
                }
                resetHeaderHeight();
            }
        });

    }

    public void insertHeaderViewOnTop(View view, int height) {
        if (view == null) {
            return;
        }
        mHeaderView.addView(view, 0);
        // mHeaderViewHeight += height;
    }

    public void release()
    {
        setAdapter(null);
        mHeaderView.removeAllViews();
        mFooterView.removeAllViews();
    }
    
    @Override
    public void setAdapter(ListAdapter adapter) {

        // make sure XListViewFooter is the last footer view, and only add once.
        if (mIsFooterReady == false && !mAutoLoadMore) {
            mIsFooterReady = true;
            addFooterView(mFooterView);
        }
        super.setAdapter(adapter);
    }

    /**
     * enable or disable pull down refresh feature.
     * 
     * @param enable
     */
    public void setPullRefreshEnable(boolean enable) {
        mEnablePullRefresh = enable;
        if (!mEnablePullRefresh) { // disable, hide the content
            mHeaderViewContent.setVisibility(View.INVISIBLE);
        } else {
            mHeaderViewContent.setVisibility(View.VISIBLE);
        }
    }

    /**
     * enable or disable pull up load more feature.
     * 
     * @param enable
     */
    public void setPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
        if (mEnablePullLoad) {
            mFooterView.hide();
            mFooterView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        } else {
            // LogUtils.d(UIUtils.TAG,
            // "-----setPullLoadEnable  mFooterView:"+mFooterView);
            mPullLoading = false;

            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    startLoadMore();
                }
            });
        }
    }

    public void setXPullLoadEnable(boolean enable) {
        mEnablePullLoad = enable;
    }

    /**
     * 设置是否自动加载更多
     * 
     * @param enable
     */
    public void setAutoLoadMoreEnable(boolean enable) {
        mAutoLoadMore = enable;
    }

    /**
     * stop refresh, reset header view.
     */
    public void stopRefresh() {
        if (mPullRefreshing == true) {
            mPullRefreshing = false;
            resetHeaderHeight();
        }
    }

    /**
     * stop load more, reset footer view.
     */
    public void stopLoadMore() {
        if (mPullLoading == true) {
            mPullLoading = false;
            mFooterView.setState(XListViewFooter.STATE_NORMAL);
        }
    }

    public void complete() {
        stopLoadMore();
        stopRefresh();
    }

    /**
     * set last refresh time
     * 
     * @param time
     */
    public void setRefreshTime(String time) {
        if (mHeaderTimeView != null)
            mHeaderTimeView.setText(time);
    }

    private void invokeOnScrolling() {
        if (mScrollListener instanceof OnXScrollListener) {
            OnXScrollListener l = (OnXScrollListener) mScrollListener;
            l.onXScrolling(this);
        }
    }

    public ImageView coverImg;

    private void updateHeaderHeight(float delta) {
//        Log.d("pull", "delta:"+delta);
        mHeaderView.setVisiableHeight((int) delta + mHeaderView.getVisiableHeight());

        if (mEnablePullRefresh && !mPullRefreshing) { // δ����ˢ��״̬�����¼�ͷ
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }
        if (getFirstVisiblePosition() != 0) {
            setSelection(0); // scroll to top each time
        }

    }

    private int screen = 0;

//    public void setScree(int scre) {
//        try {
//            float now = ((float) scre / 5 * 4);
//            this.screen = (int) now;
//            if (coverImg instanceof FrescoImageView) {
//                ((FrescoImageView) coverImg).getHierarchy().setPlaceholderImage(
//                        getContext().getResources().getDrawable(R.drawable.supercard_cover),
//                        ScalingUtils.ScaleType.CENTER_CROP);
//            } else {
//                coverImg.setImageResource(R.drawable.supercard_cover);
//            }
//            mHeaderView.setVisiableHeight(screen);
//            mHeaderView.findViewById(R.id.xlistview_header_content_out).setVisibility(View.INVISIBLE);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Auto call back refresh.
     */
    public void autoRefresh() {

        if (mPullRefreshing) {
            return;
        }

        mHeaderView.setVisiableHeight(mHeaderViewHeight);

        if (mEnablePullRefresh && !mPullRefreshing) {
            // update the arrow image not refreshing
            if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                mHeaderView.setState(XListViewHeader.STATE_READY);
            } else {
                mHeaderView.setState(XListViewHeader.STATE_NORMAL);
            }
        }

        mPullRefreshing = true;
        mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
        if (mListViewListener != null) {
            mListViewListener.onRefresh(false);
        }
    }

    /**
     * reset header view's height.
     */
    private void resetHeaderHeight() {
        postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                resetHeaderRunnable();
            }
        }, 0);
    }

    private void resetHeaderRunnable() {
        int height = mHeaderView.getVisiableHeight();
        if (height == 0) // not visible.
            return;
        // refreshing and header isn't shown fully. do nothing.
        if (mPullRefreshing && height <= mHeaderViewHeight) {
            return;
        }
        int finalHeight = screen; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mPullRefreshing && height > mHeaderViewHeight) {
            finalHeight = mHeaderViewHeight;
        }
        mScrollBack = SCROLLBACK_HEADER;

        mScroller.startScroll(0, height, 0, (finalHeight - height), SCROLL_DURATION);
        // trigger computeScroll
        invalidate();
        if (vBackCompleListener != null) {
            vBackCompleListener.onViewBackComplete();
        }
    }

    private void updateFooterHeight(float delta) {
        int height = mFooterView.getBottomMargin() + (int) delta;
        if (mEnablePullLoad && !mPullLoading) {
            if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
                                                 // more.
                mFooterView.setState(XListViewFooter.STATE_READY);
            } else {
                mFooterView.setState(XListViewFooter.STATE_NORMAL);
            }
        }
        mFooterView.setBottomMargin(height);

        // setSelection(mTotalItemCount - 1); // scroll to bottom
    }

    private void resetFooterHeight() {
        int bottomMargin = mFooterView.getBottomMargin();
        if (bottomMargin > 0) {
            mScrollBack = SCROLLBACK_FOOTER;
            mScroller.startScroll(0, bottomMargin, 0, -bottomMargin, SCROLL_DURATION);
            invalidate();
        }
    }

    private void startLoadMore() {
        if (mFooterView != null) {
            mPullLoading = true;
            // LogUtils.d(UIUtils.TAG, "-----mProgressBar  startLoadMore");
            mFooterView.setState(XListViewFooter.STATE_LOADING);
            if (mListViewListener != null) {
                Log.d("loadMore", "onloadMore");
                mListViewListener.onLoadMore();
            }
        }
    }

    public void setView(View view) {
        this.searchView = view;
    }

    private float mInterceptLastY = -1;

    // @Override
    // public boolean onInterceptTouchEvent(MotionEvent ev) {
    //
    // if (mInterceptLastY == -1)
    // {
    // mLastY = ev.getRawY();
    // }
    //
    // if(ev.getAction() == MotionEvent.ACTION_DOWN)
    // {
    // mInterceptLastY = ev.getRawY();
    // } else if(ev.getAction() == MotionEvent.ACTION_MOVE)
    // {
    // final float deltaY = ev.getRawY() - mInterceptLastY;
    // mInterceptLastY = ev.getRawY();
    // if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0
    // && (mHeaderView.getVisiableHeight() > screen || deltaY > 0)) {
    // return true;
    // }
    //
    // }else
    // {
    // mInterceptLastY = ev.getRawY();
    // }
    //
    //
    // return super.onInterceptTouchEvent(ev);
    // }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        setTopShow();
        try {
            if (mLastY == -1) {
                mLastY = ev.getRawY();
            }
            if (xTouchEvent != null) {
                xTouchEvent.onXTouchEvent(ev);
            }

            switch (ev.getAction()) {

            case MotionEvent.ACTION_DOWN :
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE :
                final float deltaY = ev.getRawY() - mLastY;
                if (searchView != null) {
                    if (deltaY > 10) {
                        searchView.setVisibility(View.VISIBLE);
                    } else if (deltaY < -10) {
                        searchView.setVisibility(View.GONE);
                    }
                }
                mLastY = ev.getRawY();

                if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= 0
                        && (mHeaderView.getVisiableHeight() > screen || deltaY > 0)) {
                    // the first item is showing, header has shown or pull down.
                    updateHeaderHeight(deltaY / OFFSET_RADIO);
                    invokeOnScrolling();
                    if (mHeaderView.getChildCount() > 1) {
                        return true;
                    }

                } else if (getLastVisiblePosition() == mTotalItemCount - 1
                        && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
                    // last item, already pulled up or want to pull up.
                    updateFooterHeight(-deltaY / OFFSET_RADIO);
                }
                break;
            default:
                mLastY = -1; // reset
                if (getFirstVisiblePosition() == 0) {
                    // invoke refresh
                    if (mEnablePullRefresh && mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
                        if (!mPullRefreshing) {
                            mPullRefreshing = true;
                            mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
                            if (mListViewListener != null) {
                                mListViewListener.onRefresh(true);
                            }

                            if (ev.getAction() == MotionEvent.ACTION_UP && mHeaderView.getChildCount() > 1) {
                                resetHeaderHeight();
                                return true;
                            }
                        }
                    }
                    resetHeaderHeight();
                } else if (getLastVisiblePosition() == mTotalItemCount - 1) {
                    // invoke load more.
                    if (mEnablePullLoad && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
                        // startLoadMore();
                    }
                    resetFooterHeight();
                }
                break;
            }

            return super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
//            LogUtil.e(e.toString());

            return false;
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScrollBack == SCROLLBACK_HEADER) {
                mHeaderView.setVisiableHeight(mScroller.getCurrY());
            } else {
                mFooterView.setBottomMargin(mScroller.getCurrY());
            }
            postInvalidate();
            invokeOnScrolling();
        }
        super.computeScroll();
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mScrollListener = l;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // LogUtils.d(UIUtils.TAG,
        // "-----------xlistview, scrollState："+scrollState);
        if (mScrollListener != null) {
            mScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    int preTotalCount = 0;
    public boolean isToBottom = false;
    public boolean isToDown = false;
    int lastVisibleItem;

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        setTopShow();
        // send to user's listener
        mTotalItemCount = totalItemCount;
        if (firstVisibleItem - lastVisibleItem >= 0) {
            isToDown = true;
        } else {
            isToDown = false;
        }
        lastVisibleItem = firstVisibleItem;
        /*
         * LogUtils.d(UIUtils.TAG, "-----XListView  firstVisibleItem:"+firstVisibleItem); LogUtils.d(UIUtils.TAG, "-----XListView  visibleItemCount:"+visibleItemCount); LogUtils.d(UIUtils.TAG, "-----XListView  totalItemCount:"+totalItemCount); LogUtils.i(UIUtils.TAG, "-----XListView  isAgain:  "+isAgain);
         */

        // 这里加firstVisibleItem!=0是为了防止得脉键在一进入就出现toast提示没有数据了
        if (firstVisibleItem + visibleItemCount == totalItemCount && firstVisibleItem != 0) {
            isToBottom = true;
        } else {
            isToBottom = false;
        }
        // firstVisibleItem!=0&&visibleItemCount>4 为了屏蔽软键盘弹出状态下导致进入if条件出现误加载。
        // 增加对pullLoading的判断，防止重复刷新
        // 增加对mAutoLoadMore的判断
        if (firstVisibleItem + visibleItemCount == totalItemCount && !isAgain && firstVisibleItem != 0 && mAutoLoadMore
                && mEnablePullLoad) {
            // LogUtils.i(UIUtils.TAG, "-----XListView  isAgain1进入 ");
            /*
             * if (totalItemCount==preTotalCount) { LogUtils.i(UIUtils.TAG, "-----XListView 相等 preTotalCount："+preTotalCount); isAgain1 =false; return; }
             */
            startLoadMore();
//            LogUtils.i(UIUtils.TAG, "-----XListView-------startLoadMore");
            isAgain = true;
//            LogUtils.i(UIUtils.TAG, "-----XListView  isToBottom==true ");
        } else {
            if (firstVisibleItem + visibleItemCount < totalItemCount - 1) {
                isAgain = false;
            }
        }

        if (mScrollListener != null) {
            mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }

    public void setXListViewListener(IXListViewListener l) {
        mListViewListener = l;
    }

    /**
     * you can listen ListView.OnScrollListener or this one. it will invoke onXScrolling when header/footer scroll back.
     */
    public interface OnXScrollListener extends OnScrollListener {

        public void onXScrolling(View view);
    }

    /**
	 *
	 */
    public interface onXTouchEvent {

        public void onXTouchEvent(MotionEvent ev);
    }

    private onXTouchEvent xTouchEvent;
    private ViewBackCompleListener vBackCompleListener;

    public void setxTouchEvent(onXTouchEvent xTouchEvent) {
        this.xTouchEvent = xTouchEvent;
    }

    public void setViewBackCompleteListener(ViewBackCompleListener vBackCompleListener) {
        this.vBackCompleListener = vBackCompleListener;
    }

    public interface ViewBackCompleListener {

        public void onViewBackComplete();
    }

    /**
     * implements this interface to get refresh/load more event.
     */
    public interface IXListViewListener {

        public void onRefresh(boolean byUser);

        public void onLoadMore();
    }

    public void setFreshTime(Context context, String name) {
        Object object = SharePrefrenceUtil.get(context, name, (long) 0);
        if (object != null) {
            long time = (Long) object;
            if (time > 0) {
                String patten = "yyyy-MM-dd HH:mm";
                SimpleDateFormat formatter = new SimpleDateFormat(patten);
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                SimpleDateFormat sdfhm = new SimpleDateFormat("HH:mm");
                Date curDate = new Date(time);
                String str = formatter.format(curDate);
                String now = sdf.format(new Date(System.currentTimeMillis()));
                if (str.contains("-" + now)) {
                    str = sdfhm.format(curDate);
                    if (System.currentTimeMillis() - time < 60000) {
                        str = context.getString(R.string.xlistview_time_rightnow);
                    }
                } else {
                    str = sdf.format(curDate);
                }
                setRefreshTime(str);
            }
        }
        SharePrefrenceUtil.put(context, name, System.currentTimeMillis());
    }

    private LinearLayout topLayout, centerLayout;
    private boolean hasSetTop = true;

    public void setTochTopListner(LinearLayout layout, LinearLayout center) {
        topLayout = layout;
        centerLayout = center;

    }

    public void setTopShow() {
        if (topLayout != null && centerLayout != null && hasSetTop) {
            hasSetTop = false;
            int location[] = new int[2], location1[] = new int[2];
            centerLayout.getLocationInWindow(location);// 中间布局的坐标
            topLayout.getLocationInWindow(location1);// 中间布局的坐标
            if (location[1] <= location1[1]) {
                topLayout.setVisibility(View.VISIBLE);// 纵坐标，隐藏的fixation就会可见，否则就消失，这就从视觉上实现类似效果
            } else {
                topLayout.setVisibility(View.INVISIBLE);
            }
            hasSetTop = true;
        }
    }

    /****
     * 解决： parameter must be a descendant of this view 是说这个parameter必须是这个view的子孙
     */
    @Override
    public boolean isFocused() {
        if (!mIsFocused) {
            return super.isFocused();
        }
        return true;
    }

    /*
     * public void addLoadHeaderView() { // init header height mHeaderView = new XListViewHeader(context); mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content); mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time); addHeaderView(mHeaderView); mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
     * @Override public void onGlobalLayout() { mHeaderViewHeight = mHeaderViewContent.getHeight(); getViewTreeObserver().removeGlobalOnLayoutListener(this); } }); }
     */

}
