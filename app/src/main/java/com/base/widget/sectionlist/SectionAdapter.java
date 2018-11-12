package com.base.widget.sectionlist;

import android.widget.BaseAdapter;

public abstract class SectionAdapter extends BaseAdapter {

    @Override
    public Object getItem(int position) {
        return null;
    }

    public static class SectionObj {

        public String section = "";
    }

}
