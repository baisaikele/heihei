/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.base.widget.sectionlist;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * A ListView that maintains a header pinned at the top of the list. The pinned header can be pushed up and dissolved as needed.
 */
public class PinnedHeaderListView extends ListView {

    /**
     * Adapter interface. The list adapter must implement this interface.
     */
    public interface PinnedHeaderAdapter {

        /**
         * Pinned header state: don't show the header.
         */
        public static final int PINNED_HEADER_GONE = 0;

        /**
         * Pinned header state: show the header at the top of the list.
         */
        public static final int PINNED_HEADER_VISIBLE = 1;

        /**
         * Pinned header state: show the header. If the header extends beyond the bottom of the first shown element, push it up and clip.
         */
        public static final int PINNED_HEADER_PUSHED_UP = 2;

        /**
         * Computes the desired state of the pinned header for the given position of the first visible list item. Allowed return values are {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or {@link #PINNED_HEADER_PUSHED_UP}.
         */
        int getPinnedHeaderState(int position);

        /**
         * Configures the pinned header view to match the first visible list item.
         * 
         * @param header
         *            pinned header view.
         * @param position
         *            position of the first visible list item.
         * @param alpha
         *            fading of the header view, between 0 and 255.
         */
        void configurePinnedHeader(View header, int position, int alpha);
    }

    private static final int MAX_ALPHA = 255;

    private PinnedHeaderAdapter mAdapter;
    private View mHeaderView;
    private boolean mHeaderViewVisible = false;

    private int mHeaderViewWidth;

    private int mHeaderViewHeight;

    public PinnedHeaderListView(Context context) {
        super(context);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPinnedHeaderView(View view) {
        mHeaderView = view;
        // mHeaderView.setVisibility(View.VISIBLE);
        // Disable vertical fading when the pinned header is present
        // TODO change ListView to allow separate measures for top and bottom fading edge;
        // in this particular case we would like to disable the top, but not the bottom edge.
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        // if (getParent() != null && getParent() instanceof DMBaseListView)
        // {
        // DMBaseListView view = (DMBaseListView) getParent();
        // // view.restoreListPostion();
        // if (adapter == null || adapter.getCount() == 0)
        // {
        // return;
        // }
        // view.endLoading();
        // }

        if (adapter == null || (!(adapter instanceof PinnedHeaderAdapter))) {
            return;
        }
        mAdapter = (PinnedHeaderAdapter) adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }

        if (mAdapter == null || (!(mAdapter instanceof PinnedHeaderAdapter))) {
            return;
        }
        int headerCount = getHeaderViewsCount();
        if (headerCount > 0) {
            position -= headerCount;
        }
        int state = mAdapter.getPinnedHeaderState(position);
        switch (state) {
        case PinnedHeaderAdapter.PINNED_HEADER_GONE : {
            mHeaderViewVisible = false;
            break;
        }

        case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE : {
            mAdapter.configurePinnedHeader(mHeaderView, position, MAX_ALPHA);

            int y = 0;

            if (mHeaderView.getTop() != y) {
                mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
            }
            mHeaderViewVisible = true;
            break;
        }

        case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP : {
            View firstView = getChildAt(0);
            if (firstView == null)
                break;
            int bottom = firstView.getBottom();
            // int itemHeight = firstView.getHeight();
            int headerHeight = mHeaderView.getHeight();
            int y;
            int alpha;
            if (bottom < headerHeight) {
                y = (bottom - headerHeight);
                alpha = MAX_ALPHA;// * (headerHeight + y) / headerHeight;
            } else {
                y = 0;
                alpha = MAX_ALPHA;
            }
            mAdapter.configurePinnedHeader(mHeaderView, position, alpha);
            if (mHeaderView.getTop() != y) {
                mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
            }
            mHeaderViewVisible = true;
            break;
        }
        }

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
            if (mHeaderViewVisible) {
                // mHeaderView.dispatchD
                drawChild(canvas, mHeaderView, getDrawingTime());
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

            ListAdapter adapter = getAdapter();
            if (adapter instanceof HeaderViewListAdapter) {
                HeaderViewListAdapter hAdapter = (HeaderViewListAdapter) adapter;
                ListAdapter wListAdapter = hAdapter.getWrappedAdapter();
                if (wListAdapter instanceof BaseAdapter) {
                    BaseAdapter bAdapter = (BaseAdapter) wListAdapter;
                    bAdapter.notifyDataSetChanged();
                }
            }

        }

    }

}
