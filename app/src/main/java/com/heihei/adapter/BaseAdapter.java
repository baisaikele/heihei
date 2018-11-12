package com.heihei.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;

public class BaseAdapter<E> extends android.widget.BaseAdapter {

    private List<E> data;

    // public BaseAdapter(){};

    public BaseAdapter(List<E> data) {
        this.data = data;
    }

    public void setData(List<E> data) {
        this.data = data;
    }

    public void remove(int position) {
        if (position >= 0 && position < data.size()) {
            this.data.remove(position);
        }
    }

    @Override
    public int getCount() {
        if (data == null) {
            return 0;
        }
        return data.size();
    }

    @Override
    public E getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

}
