package com.heihei.cell;

import android.widget.BaseAdapter;

public interface ListCell{
    public void setData(Object data, int position, BaseAdapter mAdapter);
}
