package com.base.utils.location;

import java.util.Arrays;

/**
 * 定位结果bean
 *
 * @author jianfei.geng
 * @since 2016.08.31
 */
public class PerfectLocation {
    //城市名
    public String cityName;
    //城市id
    public String cityId;
    //地址
    public String address;
    //省名
    public String province;
    public String[] businessArea;
    //所在区名
    public String district;
    //街道名
    public String streetName;
    //门牌号
    public String streetNum;
    public String responseJson;
    //纬度
    public double latitude;
    //经度
    public double longitude;

    public PerfectLocation(String cityName, String cityId, String address, String province, String district, String streetName, double latitude, double longitude) {
        this.cityName = cityName;
        this.cityId = cityId;
        this.address = address;
        this.province = province;
        this.district = district;
        this.streetName = streetName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Address [cityName=" + cityName + ", cityId=" + cityId + ", address=" + address + ", province=" + province + ", businessArea="
                + Arrays.toString(businessArea) + ", district=" + district + ", streetName=" + streetName + ", streetNum=" + streetNum
                + ", responseJson=" + responseJson + ", latitude=" + latitude + ", longitude=" + longitude +  "]";
    }

    //定位成功后回调json字符串
    public String toJson(){

        return responseJson;
    }

}
