package com.base.danmaku;

import java.util.LinkedList;
import java.util.List;

import android.util.SparseArray;

public class RecycleBin {

    public SparseArray<LinkedList<DanmakuItemView>> recycles;

    public RecycleBin() {
        recycles = new SparseArray<>();
    }

    public DanmakuItemView getView(int type) {
        LinkedList<DanmakuItemView> views = recycles.get(type);
        if (views == null) {
            return null;
        }
        
        if (views.size() <= 0) {
            return null;
        }
        
        return views.removeFirst();
    }

    public void addRecycleView(int type, DanmakuItemView mView) {
        LinkedList<DanmakuItemView> views = recycles.get(type);
        if (views == null) {
            views = new LinkedList<>();
            recycles.put(type, views);
        }
        views.addLast(mView);
    }

    public void clear()
    {
        if (recycles != null)
        {
            recycles.clear();
        }
    }
    
}
