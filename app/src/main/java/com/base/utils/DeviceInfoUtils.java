package com.base.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/***
 * public String mIMEI; //1. 手机的IMEI public int mPhoneType; //2.手机的制式类型，GSM OR CDMA 手机 public int mSysVersion; //3系统版本 public String mNetWorkCountryIso; //4手机网络国家编码 public String mNetWorkOperator; //5手机网络运营商ID。 public String mNetWorkOperatorName; //6.手机网络运营商名称 public int mNetWorkType; //6.手机的数据链接类型 public boolean mIsOnLine; //7.是否有可用数据链接 public String mConnectTypeName; //8.当前的数据链接类型 public long mFreeMem; //9.手机剩余内存 public long mTotalMem; //10.手机总内存 public String mCupInfo; //11.手机CPU型号 public
 * String mProductName; //12.手机名称 public String mModelName; //13.手机型号 public String mManufacturerName;//14.手机设备制造商名称 使用方法： DeviceInfoUtils.PhoneInfo info = DeviceInfoUtils.getPhoneInfo(Context context); CPU信息：info.mCupInfo 用于获取Android设备信息工具类
 * 
 * @author fenbb (http://www.6clue.com/)
 * @version 1.0
 * @created 2014-3-5 17:40:36
 */
public class DeviceInfoUtils {

    private static final String TAG = "DeviceInfoUtils";
    private static final String FILE_MEMORY = "/proc/meminfo";
    private static final String FILE_CPU = "/proc/cpuinfo";

    /**
     * private constructor
     */
    public DeviceInfoUtils() {}

    /**
     * private constructor
     */
    public DeviceInfoUtils(Context context) {}

    /**
     * get imei
     * 
     * @return
     */
    public static String getIMEI(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
//        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {

//            WifiInfo info = wifi.getConnectionInfo();

            return manager.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return manager.getDeviceId();
            } catch (Exception e2) {
                e2.printStackTrace();
                return "";
            }
        }
    }

    /**
     * get phone type,like :GSM
     * 
     * @param context
     * @return
     */
    public static int getPhoneType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getPhoneType();
    }

    /**
     * get phone sys version 例如：19
     * 
     * @return
     */
    public static int getSysVersion() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * get phone sys version 例如：4.4.2
     * 
     * @return
     */
    public static String getSysVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Returns the ISO country code equivalent of the current registered operator's MCC (Mobile Country Code).
     * 
     * @param context
     * @return
     */
    public static String getNetWorkCountryIso(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkCountryIso();
    }

    /**
     * Returns the numeric name (MCC+MNC) of current registered operator.may not work on CDMA phone
     * 
     * @param context
     * @return
     */
    public static String getNetWorkOperator(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkOperator();
    }

    /**
     * Returns the alphabetic name of current registered operator.may not work on CDMA phone
     * 
     * @param context
     * @return
     */
    public static String getNetWorkOperatorName(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkOperatorName();
    }

    /**
     * get type of current network
     * 
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        return manager.getNetworkType();
    }

    /**
     * is webservice aviliable
     * 
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * get current data connection type name ,like ,Mobile
     * 
     * @param context
     * @return
     */
    public static String getConnectTypeName(Context context) {
        if (!isOnline(context)) {
            return "OFFLINE";
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null) {
            return info.getTypeName();
        } else {
            return "OFFLINE";
        }
    }

    /**
     * get free memory of phone, in M
     * 
     * @param context
     * @return
     */
    public static long getFreeMem(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        MemoryInfo info = new MemoryInfo();
        manager.getMemoryInfo(info);
        long free = info.availMem / 1024 / 1024;
        return free;
    }

    /**
     * get total memory of phone , in M
     * 
     * @param context
     * @return
     */
    public static long getTotalMem(Context context) {
        try {
            FileReader fr = new FileReader(FILE_MEMORY);
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split("\\s+");
            LogWriter.w(TAG, text);
            return Long.valueOf(array[1]) / 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getCpuInfo() {
        try {
            FileReader fr = new FileReader(FILE_CPU);
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get product name of phone
     * 
     * @return
     */
    public static String getProductName() {
        return Build.PRODUCT;
    }

    /**
     * get model of phone
     * 
     * @return
     */
    public static String getModelName() {
        return Build.MODEL;
    }

    /**
     * get Manufacturer Name of phone
     * 
     * @return
     */
    public static String getManufacturerName() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取当前设置的电话号码 get mobile of mPhoneNumber
     * 
     * @return
     */
    public static String getNativePhoneNumber(Context context) {
        String phoneNumber = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = tm.getLine1Number();
        return phoneNumber;
    }

    // 其它设备信息
    /**
     * 得到屏幕高度
     * 
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 得到屏幕宽度
     * 
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到屏幕尺寸400X800
     * 
     * @param context
     * @return
     */
    public static String getDeviceScreen(Activity context) {
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels + "x" + metric.heightPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getSmallestScreenDimension(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        return Math.min(display.getHeight(), display.getWidth());
    }

    /**
     * 获取屏幕的宽高
     * 
     * @return
     */
    public static Point getDeviceSize(Context ctx) {
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        Point size = new Point();
        size.x = dm.widthPixels;
        size.y = dm.heightPixels;
        return size;
    }

    public static DeviceInfoUtils.PhoneInfo getPhoneInfo(Context context) {
        DeviceInfoUtils.PhoneInfo result = new DeviceInfoUtils.PhoneInfo();
        result.mIMEI = getIMEI(context);
        result.mPhoneType = getPhoneType(context);
        result.mSysVersion = getSysVersion();
        result.mSysVersion2 = getSysVersionRelease();

        result.mNetWorkCountryIso = getNetWorkCountryIso(context);
        result.mNetWorkOperator = getNetWorkOperator(context);
        result.mNetWorkOperatorName = getNetWorkOperatorName(context);
        result.mNetWorkType = getNetworkType(context);
        result.mIsOnLine = isOnline(context);
        result.mConnectTypeName = getConnectTypeName(context);
        result.mFreeMem = getFreeMem(context);
        result.mTotalMem = getTotalMem(context);
        result.mCupInfo = getCpuInfo();
        result.mProductName = getProductName();
        result.mModelName = getModelName();
        result.mManufacturerName = getManufacturerName();
        return result;
    }

    public static class PhoneInfo {

        /**
         * private constructor
         */
        private PhoneInfo() {}

        public String mIMEI; // 1. 手机的IMEI
        public int mPhoneType; // 2.手机的制式类型，GSM OR CDMA 手机
        public int mSysVersion; // 3系统版本
        public String mSysVersion2;// 3系统版本
        public String mNetWorkCountryIso; // 4手机网络国家编码
        public String mNetWorkOperator; // 5手机网络运营商ID。
        public String mNetWorkOperatorName; // 6.手机网络运营商名称
        public int mNetWorkType; // 6.手机的数据链接类型
        public boolean mIsOnLine; // 7.是否有可用数据链接
        public String mConnectTypeName; // 8.当前的数据链接类型
        public long mFreeMem; // 9.手机剩余内存
        public long mTotalMem; // 10.手机总内存
        public String mCupInfo; // 11.手机CPU型号
        public String mProductName; // 12.手机名称
        public String mModelName; // 13.手机型号
        public String mManufacturerName;// 14.手机设备制造商名称
        public String mPhoneNumber;// 15.手机号码

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("IMEI : " + mIMEI + "\n");
            builder.append("mPhoneType : " + mPhoneType + "\n");
            builder.append("mSysVersion : " + mSysVersion + "\n");
            builder.append("mNetWorkCountryIso : " + mNetWorkCountryIso + "\n");
            builder.append("mNetWorkOperator : " + mNetWorkOperator + "\n");
            builder.append("mNetWorkOperatorName : " + mNetWorkOperatorName + "\n");
            builder.append("mNetWorkType : " + mNetWorkType + "\n");
            builder.append("mIsOnLine : " + mIsOnLine + "\n");
            builder.append("mConnectTypeName : " + mConnectTypeName + "\n");
            builder.append("mFreeMem : " + mFreeMem + "M\n");
            builder.append("mTotalMem : " + mTotalMem + "M\n");
            builder.append("mCupInfo : " + mCupInfo + "\n");
            builder.append("mProductName : " + mProductName + "\n");
            builder.append("mModelName : " + mModelName + "\n");
            builder.append("mManufacturerName : " + mManufacturerName + "\n");
            builder.append("mPhoneNumber : " + mPhoneNumber + "\n");
            return builder.toString();
        }
    }

    public static int getStatusBarHeight(Context context) {

        Class c = null;

        Object bj = null;

        Field field = null;

        int x = 0, statusBarHeight = 0;

        try {

            c = Class.forName("com.android.internal.R$dimen");

            bj = c.newInstance();

            field = c.getField("status_bar_height");

            x = Integer.parseInt(field.get(bj).toString());

            statusBarHeight = context.getResources().getDimensionPixelSize(x);

        } catch (Exception e1) {

            e1.printStackTrace();

        }

        return statusBarHeight;

    }

}
