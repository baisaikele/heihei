package com.heihei.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heihei.cell.ListCell;
import com.heihei.model.Gift;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class BlackUserAdapter extends BaseAdapter<User> {
	public OnDataChangeLintener mLintener;

	public BlackUserAdapter(List<User> data, OnDataChangeLintener lintener) {
		super(data);
		this.mLintener = lintener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_black_user, null);
		}

		ListCell lc = (ListCell) convertView;
		lc.setData(getItem(position), position, this);
		return convertView;
	}

	public static interface OnDataChangeLintener {
		public void onChange();
	}
}
