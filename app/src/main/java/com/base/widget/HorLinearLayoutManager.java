package com.base.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

public class HorLinearLayoutManager extends LinearLayoutManager {

    private static final int CHILD_WIDTH = 0;
    private static final int CHILD_HEIGHT = 1;
    private static final int DEFAULT_CHILD_SIZE = 100;

    private final int[] childDimensions = new int[2];

    private int childSize = DEFAULT_CHILD_SIZE;
    private boolean hasChildSize;

    @SuppressWarnings("UnusedDeclaration")
    public HorLinearLayoutManager(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    @SuppressWarnings("UnusedDeclaration")
    public HorLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        setOrientation(HORIZONTAL);
    }

    public static int makeUnspecifiedSpec() {
        return View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    }

//    @Override
//    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
//        int itemCount = getItemCount();
//        if (itemCount > 0) {
//            View itemView = recycler.getViewForPosition(0);
//            // measureChild(itemView, widthSpec, heightSpec);
//            itemView.measure(widthSpec, heightSpec);
//            int measuredWidth = MeasureSpec.getSize(widthSpec);
//            int itemHeight = itemView.getMeasuredHeight();
//            setMeasuredDimension(measuredWidth, itemHeight);
//        }
//
//    }

}
