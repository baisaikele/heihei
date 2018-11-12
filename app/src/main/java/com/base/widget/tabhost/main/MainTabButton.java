package com.base.widget.tabhost.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlives.heihei.R;

public class MainTabButton extends RelativeLayout {

    private ImageView image;
    private TextView tab_button;
    private View tab_tip;
    private TextView unread_count;
    private boolean isChecked = false;

    public MainTabButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public MainTabButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public MainTabButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context, attrs);
        // TODO Auto-generated constructor stub
    }

    private void initLayout(Context context, AttributeSet attrs) {
        // TODO Auto-generated method stub
        View contentView = LayoutInflater.from(context).inflate(R.layout.tabhost_main_tab_btn_item, this);
        image = (ImageView) contentView.findViewById(R.id.iv_tab_item_icon);
        tab_button = (TextView) contentView.findViewById(R.id.tv_tab_item_icon);
        tab_tip = contentView.findViewById(R.id.tab_tip);
        unread_count = (TextView) contentView.findViewById(R.id.unread_count);
        TypedArray a = getResources().obtainAttributes(attrs, R.styleable.tab_button);
        Drawable d = a.getDrawable(R.styleable.tab_button_drawableTop);
        String text = a.getString(R.styleable.tab_button_tabtext);
        a.recycle();
        tab_button.setText(text);
        image.setImageDrawable(d);
    }

    public void setImageDrawable(Drawable drawable)
    {
        image.setImageDrawable(drawable);
    }
    
    public void setHasNew(boolean hasNew) {
        if (tab_tip != null) {
            tab_tip.setVisibility(hasNew ? View.VISIBLE : View.GONE);
            ;
        }
    }

    public void setUnreadCount(int count) {
        unread_count.setText(String.valueOf(count));
        unread_count.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSelected(boolean selected) {
        // TODO Auto-generated method stub
        super.setSelected(selected);
    }
}
