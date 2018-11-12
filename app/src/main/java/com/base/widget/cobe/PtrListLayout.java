package com.base.widget.cobe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.base.widget.cobe.loadmore.LoadMoreListViewContainer;
import com.base.widget.cobe.ptr.PtrClassicFrameLayout;

/**
 * listview 刷新 、 加载更多 的整合控件<br>
 * 需要在xml中包裹住listview<br>
 * &lt PtrListLayout &gt<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt ListView /&gt<br>
 * &lt/ PtrListLayout &gt
 * 
 * @author CCCMAX
 */
public class PtrListLayout extends PtrClassicFrameLayout {

    public LoadMoreListViewContainer loadmoreContainer;

    private CodePullHandler pullListener;

    private ListView listview;

    public PtrListLayout(Context context) {
        super(context);
    }

    public PtrListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrListLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        disableWhenHorizontalMove(true);
        int childcount = this.getChildCount();
        if (childcount > 0) {
            for (int i = 0; i < childcount; i++) {
                View v = getChildAt(i);
                if (v instanceof ListView) {
                    listview = (ListView) v;
                    removeView(v);

                    loadmoreContainer = new LoadMoreListViewContainer(getContext(), listview);
                    ViewGroup.LayoutParams lmc_lp = new LayoutParams(v.getLayoutParams().width,
                            v.getLayoutParams().height);
                    loadmoreContainer.setLayoutParams(lmc_lp);

                    break;
                }
            }

            if (childcount > 2) {
                System.out.print("The PtrListLayou can only have one childview");
            }
        }
        addView(loadmoreContainer);

        super.onFinishInflate();
    }

    /**
     * 设置上拉下拉的监听
     * 
     * @param listener
     */
    public void setCodePullHandler(CodePullHandler listener) {
        this.pullListener = listener;

        setLastUpdateTimeRelateObject(listener);

        this.setPtrHandler(pullListener);// 下拉刷新监听

        loadmoreContainer.setLoadMoreHandler(pullListener);// 上拉加载更多监听

        loadmoreContainer.useTransparentFooter();// 上拉加载的footerview 默认设置
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        loadmoreContainer.setAutoLoadMore(autoLoadMore);
    }

    /**
     * 结束加载更多
     * 
     * @param emptyResult
     * @param hasMore
     */
    public void loadMoreFinish(boolean emptyResult, boolean hasMore) {
        loadmoreContainer.loadMoreFinish(emptyResult, hasMore);
    }

    /**
     * 是否正在加载更多数据
     * 
     * @return
     */
    public boolean isLoading() {
        return loadmoreContainer.isLoading();
    }

}
