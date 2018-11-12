package com.heihei.adapter;

import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.base.utils.DeviceInfoUtils;
import com.facebook.fresco.FrescoImageHelper;
import com.facebook.fresco.FrescoImageView;
import com.wmlives.heihei.R;

public class AudienceGiftAdapter extends RecyclerView.Adapter<AudienceGiftAdapter.ViewHolder> {

    List<Object> data;

    public AudienceGiftAdapter(List<Object> data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_nickname;
        private TextView tv_gift_name;
        private TextView tv_num;
        private FrescoImageView iv_gift_icon;

        public ViewHolder(View arg0) {
            super(arg0);
            tv_nickname = (TextView) arg0.findViewById(R.id.tv_nickname);
            tv_gift_name = (TextView) arg0.findViewById(R.id.tv_gift_name);
            tv_num = (TextView) arg0.findViewById(R.id.tv_num);
            iv_gift_icon = (FrescoImageView) arg0.findViewById(R.id.iv_gift_icon);
        }

    }

    @Override
    public int getItemCount() {
        if (data == null)
            return 0;
        return data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        FrescoImageHelper.getAvatar(FrescoImageHelper.getRandomImageUrl(), vh.iv_gift_icon);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        View view = LayoutInflater.from(arg0.getContext()).inflate(R.layout.cell_audience_gift, arg0,false);
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = DeviceInfoUtils.getScreenWidth(arg0.getContext()) / 3;
        view.setLayoutParams(params);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
}
