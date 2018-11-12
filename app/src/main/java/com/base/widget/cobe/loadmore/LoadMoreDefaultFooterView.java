package com.base.widget.cobe.loadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wmlives.heihei.R;


public class LoadMoreDefaultFooterView extends RelativeLayout implements LoadMoreUIHandler {

    private LinearLayout footer_layout;
    private TextView mTextView;
    private View progressbar, bottom_view;

    public LoadMoreDefaultFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreDefaultFooterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupViews();
    }

    private void setupViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.cube_views_load_more_default_footer, this);
        mTextView = (TextView) findViewById(R.id.cube_views_load_more_default_footer_text_view);
        footer_layout = (LinearLayout) findViewById(R.id.footer_layout);
        progressbar = findViewById(R.id.progressbar);
        bottom_view = findViewById(R.id.bottom_view);
    }

    @Override
    public void onLoading(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_loading);
        footer_layout.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadFinish(LoadMoreContainer container, boolean empty, boolean hasMore) {
        progressbar.setVisibility(View.GONE);
        if (!hasMore) {
            // setVisibility(VISIBLE);
            // if (empty) {
            // mTextView.setText(R.string.cube_views_load_more_loaded_empty);
            // } else {
            // mTextView.setText(R.string.cube_views_load_more_loaded_no_more);
            // }

            footer_layout.setVisibility(View.GONE);
        } else {
            footer_layout.setVisibility(View.VISIBLE);
            setVisibility(INVISIBLE);
        }
    }

    @Override
    public void onWaitToLoadMore(LoadMoreContainer container) {
        setVisibility(VISIBLE);
        mTextView.setText(R.string.cube_views_load_more_click_to_load_more);

        footer_layout.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onLoadError(LoadMoreContainer container, int errorCode, String errorMessage) {
        mTextView.setText(R.string.cube_views_load_more_error);

        setVisibility(VISIBLE);
        footer_layout.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.GONE);
    }

    public void setBottomViewVisibility(int visibility) {
        bottom_view.setVisibility(visibility);
    }
}
