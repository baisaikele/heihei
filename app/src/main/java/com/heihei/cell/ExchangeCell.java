package com.heihei.cell;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.base.utils.StringUtils;
import com.heihei.cell.RechargeDiamondCell.OnRightClickListener;
import com.heihei.model.ExchangeInfo;
import com.wmlives.heihei.R;

public class ExchangeCell extends LinearLayout implements ListCell, OnClickListener {

    // ----------------R.layout.cell_recharge_diamond-------------Start
    private Button btn_recharge;
    private TextView tv_diamond_num;
    private TextView tv_tip;

    public void autoLoad_cell_recharge_diamond() {
        btn_recharge = (Button) findViewById(R.id.btn_recharge);
        tv_diamond_num = (TextView) findViewById(R.id.tv_diamond_num);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
    }

    // ----------------R.layout.cell_recharge_diamond-------------End

    public ExchangeCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoLoad_cell_recharge_diamond();
        btn_recharge.setOnClickListener(this);
    }

    private ExchangeInfo mInfo;
    private int position = 0;

    @Override
    public void setData(Object data, int position, BaseAdapter mAdapter) {
        this.position = position;
        this.mInfo = (ExchangeInfo) data;
        tv_diamond_num.setText("" + mInfo.gold);
        if (StringUtils.isEmpty(mInfo.desc)) {
            tv_tip.setVisibility(View.GONE);
        } else {
            tv_tip.setVisibility(View.VISIBLE);
            tv_tip.setText(mInfo.desc);
        }
        btn_recharge.setText(mInfo.point + getResources().getString(R.string.user_account_bill));
    }

    @Override
    public void onClick(View v) {
        if (this.mOnRightClickListener != null) {
            this.mOnRightClickListener.onRightClick(position);
        }

    }

    private OnRightClickListener mOnRightClickListener;

    public void setOnRightClickListener(OnRightClickListener mOnRightClickListener) {
        this.mOnRightClickListener = mOnRightClickListener;
    }

}
