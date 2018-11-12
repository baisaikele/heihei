package com.base.utils.location;

import android.content.Context;
import android.os.Handler;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class AmapLocationManagerImpl implements LocationApi, Runnable {
	private static AmapLocationManagerImpl mAmapLocationApiAmapImpl;
	private AMapLocationClient mAmapLocationClient = null;
	private Context mContext;
	private Handler mHandler;
	private LocationCallback mLocationCallback;
    private AMapLocation mAMapLocation;

	public static AmapLocationManagerImpl getInstance(Context context) {
		if (mAmapLocationApiAmapImpl == null) {
			synchronized (AmapLocationManagerImpl.class) {
				if (mAmapLocationApiAmapImpl == null) {
					mAmapLocationApiAmapImpl = new AmapLocationManagerImpl(context);
					mAmapLocationApiAmapImpl.initLocationParam();
				}
			}
		}
		return mAmapLocationApiAmapImpl;
	}

	private AmapLocationManagerImpl(Context context) {
		this.mContext = context.getApplicationContext();
	}

	@Override
	public void run() {
		 if (mAMapLocation == null) {
	            mLocationCallback.locationFailed();
	            stopLocation();
	        }
	}

	@Override
	public void initLocationParam() {
		this.mHandler = new Handler(mContext.getMainLooper());
		initAMaoClientParam();
	}

	private void initAMaoClientParam() {
		mAmapLocationClient = new AMapLocationClient(mContext);
		AMapLocationClientOption mAMapLocationClientOption = new AMapLocationClientOption();
		mAMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
		mAMapLocationClientOption.isGpsFirst();
		mAMapLocationClientOption.setNeedAddress(true);
		mAMapLocationClientOption.setOnceLocation(true);
		mAMapLocationClientOption.setInterval(2000);
		mAmapLocationClient.setLocationOption(mAMapLocationClientOption);
		mAmapLocationClient.startLocation();
	}

	@Override
	public void startLocation(LocationCallback locationCallback) {
		if (locationCallback == null)
			throw new IllegalArgumentException("locationCallback cannot be null, please check your code! thankyou.");

		mLocationCallback = locationCallback;
//		if (NetWork) {
			if (mAmapLocationClient == null)
				initAMaoClientParam();

			mAmapLocationClient.setLocationListener(mAMapLocationListener);
			mHandler.postDelayed(this, 10000);
//		}
	}

	@Override
	public void stopLocation() {
		if (mAmapLocationClient != null)
			mAmapLocationClient.stopLocation();

		mAmapLocationClient = null;
	}
	
    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (location != null) {
                stopLocation();
                AmapLocationManagerImpl.this.mAMapLocation = location;

                if (location.getCity() != null && location.getCity().endsWith("市")) {
                    PerfectLocation perfectLocation = new PerfectLocation(location.getCity(), location.getCityCode(), location.getAddress(), location.getProvince()
                            , location.getDistrict(), location.getStreet(), location.getLatitude(), location.getLongitude());

                    mLocationCallback.locationSuccess(perfectLocation);
                } else {
                    mLocationCallback.locationFailed();
                }
            } else {
                mLocationCallback.locationFailed();
            }
        }
    };

}
