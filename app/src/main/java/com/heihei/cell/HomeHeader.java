package com.heihei.cell;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import com.base.utils.DeviceInfoUtils;
import com.base.widget.CustomGallery;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.fresco.FrescoImageHelper;
import com.facebook.fresco.FrescoParam;
import com.heihei.dialog.UserDialog;
import com.heihei.fragment.link.LinkUtil;
import com.wmlives.heihei.R;

public class HomeHeader extends LinearLayout {

	private static final float RATIO = 0.421f;
	private com.base.widget.CustomGallery gallery;

	private BannerAdapter adapter;
	private JSONArray bannerDatas;

	public HomeHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		gallery = (CustomGallery) findViewById(R.id.gallery);
		ViewGroup.LayoutParams params = gallery.getLayoutParams();
		params.height = (int) (DeviceInfoUtils.getScreenWidth(getContext()) * RATIO);
		gallery.setLayoutParams(params);
		gallery.setPageMargin(getResources().getDimensionPixelSize(R.dimen.t10dp));
	}

	public void setData(JSONArray datas) {

		this.bannerDatas = datas;

		// if (adapter == null) {
		adapter = new BannerAdapter(bannerDatas);
		gallery.setAdapter(adapter);
		// } else {
		// adapter.setData(this.bannerDatas);
		// adapter.notifyDataSetChanged();
		// }

		int pos = 0;
		// if (bannerDatas.length() > 1) {
		//
		// pos = Integer.MAX_VALUE / 2;
		// // while (pos % bannerDatas.length() != 0) {
		// // pos++;
		// // }
		// if (pos % bannerDatas.length() != 0) {
		// pos = pos - pos % bannerDatas.length();
		// }
		// // this.indicator.setVisibility(View.VISIBLE);
		// } else {
		// // this.indicator.setVisibility(View.INVISIBLE);
		// }

		gallery.setCurrentItem(pos);
		gallery.startLoop();
	}

	class BannerAdapter extends PagerAdapter {

		private JSONArray data;

		public BannerAdapter(JSONArray data) {
			this.data = data;
		}

		public void setData(JSONArray data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			if (this.data.length() == 1) {
				return 1;
			} else if (this.data.length() == 0) {
				return 0;
			}
			return Integer.MAX_VALUE;
			// return this.data.length();
		}

		public JSONObject getItem(int position) {
			return this.data.optJSONObject(position % data.length());
		}

		public long getItemId(int position) {
			return 0;
		}

		// public View getView(int position, View convertView, ViewGroup parent)
		// {
		// if (convertView == null) {
		// convertView =
		// LayoutInflater.from(getContext()).inflate(R.layout.cell_banner,
		// null);
		// }
		//
		// SimpleDraweeView iv = (SimpleDraweeView)
		// convertView.findViewById(R.id.iv);
		// JSONObject json = getItem(position);
		// FrescoParam fp = new FrescoParam(json.optString("cover"));
		// fp.DefaultImageID = R.drawable.defaulthead;
		// FrescoImageHelper.getImage(fp, iv);
		// return convertView;
		// }

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// super.destroyItem(container, position, object);
			if (object != null && object instanceof View) {
				container.removeView((View) object);
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			View convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_banner, null);
			SimpleDraweeView iv = (SimpleDraweeView) convertView.findViewById(R.id.iv);
			JSONObject json = getItem(position);
			FrescoParam fp = new FrescoParam(json.optString("cover"));
			fp.DefaultImageID = R.drawable.home_banner_bg;
			FrescoImageHelper.getImage(fp, iv);
			container.addView(convertView);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					JSONObject json = (JSONObject) getItem(position);
					String link = json.optString("link");
					String desc = json.optString("desc");
					LinkUtil.handleLink(getContext(), link, desc);
				}
			});

			return convertView;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {

			return arg0 == arg1;
		}

	}

	// @Override
	// public void onItemClick(AdapterView<?> parent, View view, int position,
	// long id) {
	// JSONObject json = (JSONObject) parent.getAdapter().getItem(position);
	// String link = json.optString("link");
	// LinkUtil.handleLink(getContext(), link);
	// }

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		gallery.removeLoop();
	}

}
