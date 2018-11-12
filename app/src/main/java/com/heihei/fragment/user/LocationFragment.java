package com.heihei.fragment.user;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.base.host.BaseActivity.PermissionRequestObj;
import com.base.host.BaseActivity;
import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.base.utils.location.LocationApi;
import com.base.utils.location.LocationApi.LocationCallback;
import com.base.utils.location.LocationApi.LocationMode;
import com.base.utils.location.PerfectLocation;
import com.wmlives.heihei.R;

public class LocationFragment extends BaseFragment implements OnClickListener {
	private View btn_black;
	private ImageView locationImg;
	private TextView locationCity;
	private LocationApi mLocationApi;

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_location);
	}

	@Override
	protected void viewDidLoad() {
		btn_black = findViewById(R.id.btn_black);
		locationImg = (ImageView) findViewById(R.id.img_location);
		locationCity = (TextView) findViewById(R.id.text_city);
		btn_black.setOnClickListener(this);
		mLocationApi = HostApplication.getInstance().createLocationApi(LocationMode.AMAPLOCATION);

		ArrayList<String> permissions = new ArrayList<String>();
		permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
		((BaseActivity) getActivity()).doRequestPermissions(new PermissionRequestObj(permissions) {

			@Override
			public void callback(boolean allGranted, List<String> permissionsList_denied, PermissionRequestObj pro) {
				if (allGranted) {
					mLocationApi.startLocation(mLocationCallback);
				} else {
					mLocationApi.stopLocation();
				}
			}
		});

	}

	@Override
	protected String initTitle() {
		return getString(R.string.user_city_title);
	}

	@Override
	protected void refresh() {

	}

	private String cityName = "";
	private String latlng = "";

	private LocationCallback mLocationCallback = new LocationCallback() {

		@Override
		public void locationSuccess(PerfectLocation location) {
			try {
				LogWriter.i("jianfei", "location " + location.toString());
				locationImg.setImageResource(R.drawable.hh_mylocation_location);
				locationCity.setText(location.cityName);
				cityName = location.cityName;
				latlng = location.latitude + "," + location.longitude;
				mLocationApi.stopLocation();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void locationFailed() {
			try {
				locationImg.setImageResource(R.drawable.hh_mylocation_location_n);
				locationCity.setText(getText(R.string.user_location_fail));
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_black:
			if (!StringUtils.isEmpty(cityName)) {
				Intent intent = new Intent();
				intent.putExtra("city", cityName);
				intent.putExtra("latlng", latlng);
				getActivity().setResult(Activity.RESULT_OK, intent);
				getActivity().finish();
			}
			break;
		}

	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LocationFragment";
	}

}
