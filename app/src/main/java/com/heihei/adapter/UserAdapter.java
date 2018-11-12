package com.heihei.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heihei.cell.ListCell;
import com.heihei.model.User;
import com.wmlives.heihei.R;


public class UserAdapter extends BaseAdapter<User>{

    public UserAdapter(List<User> data) {
        super(data);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_user, null);
        }
        
        ListCell lc = (ListCell) convertView;
        lc.setData(getItem(position), position, this);
        return convertView;
    }
    
}
