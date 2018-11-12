package com.heihei.adapter;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.fresco.FrescoImageHelper;
import com.heihei.model.Gift;
import com.wmlives.heihei.R;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {

    List<Gift> data;

    private Context context;

    public GiftAdapter(List<Gift> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // ----------------R.layout.cell_gift-------------Start
        private com.facebook.fresco.FrescoImageView iv_gift_icon;
        private TextView tv_gift_name;
        private TextView tv_diamond_num;
        private TextView tv_express_num;
        private View bg_view;

        public void autoLoad_cell_gift(View arg0) {
            bg_view = arg0.findViewById(R.id.bg_view);
            iv_gift_icon = (com.facebook.fresco.FrescoImageView) arg0.findViewById(R.id.iv_gift_icon);
            tv_gift_name = (TextView) arg0.findViewById(R.id.tv_gift_name);
            tv_diamond_num = (TextView) arg0.findViewById(R.id.tv_diamond_num);
            tv_express_num = (TextView) arg0.findViewById(R.id.tv_express_num);
        }

        // ----------------R.layout.cell_gift-------------End

        public ViewHolder(View arg0) {
            super(arg0);
            autoLoad_cell_gift(arg0);
        }

    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, final int position) {
        Gift gift = data.get(position);
        if (gift.isSelected) {
            vh.bg_view.setSelected(true);
            vh.tv_diamond_num.setVisibility(View.GONE);
            vh.tv_express_num.setVisibility(View.VISIBLE);
        } else {
            vh.bg_view.setSelected(false);
            vh.tv_diamond_num.setVisibility(View.VISIBLE);
            vh.tv_express_num.setVisibility(View.GONE);
        }

        FrescoImageHelper.getAvatar(gift.image, vh.iv_gift_icon);
        vh.tv_gift_name.setText(gift.name);
        vh.tv_diamond_num.setText(gift.gold + "钻");
        vh.tv_express_num.setText("+" + gift.exp + "经验");
        vh.itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View view = LayoutInflater.from(arg0.getContext()).inflate(R.layout.cell_gift, arg0, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

}
