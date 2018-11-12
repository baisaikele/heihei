package com.base.widget;


import android.view.View;
import android.widget.TextView;

import com.wmlives.heihei.R;

public class TitleBar {

    private View contentView;

    public TitleBar(View view) {
        this.contentView = view;
    }

    public void setVisibility(int visibility) {
        this.contentView.setVisibility(visibility);
    }

    public void setBackground(int id) {
        contentView.setBackgroundResource(id);
    }

    public void setBackgroundColor(int color) {
        contentView.setBackgroundColor(color);
    }

    /**
     * 设置标题
     * 
     * @param title
     */
    public void setTitle(String title) {
        TextView tv_title = (TextView) this.contentView.findViewById(R.id.tv_title);
        if (tv_title != null)
            tv_title.setText(title);
    }

    /**
     * 设置标题
     * 
     * @param strId
     */
    public void setTitle(int strId) {
        TextView tv_title = (TextView) this.contentView.findViewById(R.id.tv_title);
        if (tv_title != null)
            tv_title.setText(strId);
    }

    public View findViewById(int id) {
        return this.contentView.findViewById(id);
    }

    public void setChildVisibility(int viewId, int visibility) {
        View view = findViewById(viewId);
        view.setVisibility(visibility);
    }

}
