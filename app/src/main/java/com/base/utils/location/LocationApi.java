package com.base.utils.location;

/**
 * 定位接口
 *
 * @author jianfei.geng
 * @since 2016.08.31
 */
public interface LocationApi {
    /**
     * 初始化定位参数
     */
    public void initLocationParam();

    /**
     * 开始定位
     *
     * @param locationCallback
     */
    public void startLocation(LocationCallback locationCallback);

    /**
     * 结束定位（建议在生命周期onStop()中调用）
     */
    public void stopLocation();

    /**
     * 定位回调接口
     */
    public interface LocationCallback {
        public void locationSuccess(PerfectLocation location);

        public void locationFailed();
    }

    public enum LocationMode {
         AMAPLOCATION
    }
}
