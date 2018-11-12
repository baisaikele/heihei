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
import com.heihei.model.pay.RechargeInfo;
import com.wmlives.heihei.R;

public class RechargeDiamondCell extends LinearLayout implements ListCell, OnClickListener {

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

    public RechargeDiamondCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoLoad_cell_recharge_diamond();
        btn_recharge.setOnClickListener(this);
    }

    private int position = 0;

    private RechargeInfo mInfo;

    @Override
    public void setData(Object data, int position, BaseAdapter mAdapter) {
        this.position = position;
        mInfo = (RechargeInfo) data;
        this.tv_diamond_num.setText(mInfo.gold + "");
        this.tv_tip.setText(mInfo.desc);
        if (StringUtils.isEmpty(mInfo.desc)) {
            this.tv_tip.setVisibility(View.GONE);
        } else {
            this.tv_tip.setVisibility(View.VISIBLE);
        }

        this.btn_recharge.setText("¥" + StringUtils.fen2yuan(mInfo.payMoney));
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

    public static interface OnRightClickListener {

        public void onRightClick(int position);
    }

}
