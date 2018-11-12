package com.base.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.base.host.HostApplication;


public class NetUtil {

    private static final String TAG = "NetUtil";
    public static String WIFI = "wifi";
    public static String G2 = "2g";
    public static String G3 = "3g";
    public static String G4 = "4g";
    public static String UNKNOWN = "unknown";

    public static String getWebType(Context context) {

        try {
            if (context == null) {
                context = HostApplication.getInstance();
            }

            TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String name = telManager.getSimOperatorName();
            String operator = telManager.getSimOperator();
            
            if (operator != null) {

                if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                    name = "中国移动";
                    // 中国移动
                } else if (operator.equals("46001")) {
                    name = "中国联通";
                    // 中国联通
                } else if (operator.equals("46003")) {
                    name = "中国电信";
                    // 中国电信
                }
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return UNKNOWN;
    }

    // 网络使用询问
    public static int isWeb(Context context) {
        int WIFI = 1, CMWAP = 2, CMNET = 3;
        try {
            NetworkInfo netInfo = NetUtil.hasNetwork(context);
            if (netInfo != null) {
                int nType = netInfo.getType();
                if (nType == ConnectivityManager.TYPE_MOBILE) {
                    if (netInfo.getExtraInfo() != null && netInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                        nType = CMNET;
                    } else {
                        nType = CMWAP;
                    }
                    
                } else if (nType == ConnectivityManager.TYPE_WIFI) {
                    nType = WIFI;
                }
                return nType;
            }
        } catch (Exception e) {
        }
        return -1;
    }

    private static String intToIp(int paramInt) {
        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }

    public static String getSsidOrId(Context context, int tag) {

        if (context == null) {
            context = HostApplication.getInstance();
        }

        if (isWeb(context) == 1) {
            try {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                // DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                // System.out.println(intToIp(dhcpInfo.dns1) + "  dns==" + dhcpInfo.dns2);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo == null)
                    return "";
                if (tag == 0) {
                    return wifiInfo.getSSID().replaceAll("\"", "");
                } else {
                    return wifiInfo.getIpAddress() + "";
                }
            } catch (Exception e) {
            }
        } else if (tag == 0) {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            } catch (SocketException ex) {
            }

        }
        return "";
    }

    /**
     * 获取网络类型
     * 
     * @param context
     * @return
     */
    public static String netType(Context context) {
        try {
            if (context == null) {
                context = HostApplication.getInstance();
            }
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                String type = networkInfo.getTypeName();
                if (type == null) {
                    return UNKNOWN;
                }
                // LogUtils.d(TAG,"type:"+type);
                if (type.equalsIgnoreCase("WIFI")) {
                    return WIFI;
                } else if (type.equalsIgnoreCase("MOBILE")) {
                    return getSpecificMobileType(context);
                } else {
                    return UNKNOWN;
                }
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return UNKNOWN;
    }

    /**
     * 判断网络是否为漫游
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null && tm.isNetworkRoaming()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param context
     * @return
     */
    public static NetworkInfo hasNetwork(Context context) {
        if (context == null) {
            return null;
        }
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (con == null) {
            return null;
        }
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if (workinfo == null || !workinfo.isAvailable()) {
            return null;
        }
        return workinfo;
    }

    /**
     * 获得网络连接是否可用
     * 
     * @param context
     * @return
     */
    public static boolean isHasNetwork(Context context) {
        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo workinfo = con.getActiveNetworkInfo();
        if (workinfo == null || !workinfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * 根据网速获取具体的移动网络类型
     * 
     * @param context
     * @return
     */
    public static String getSpecificMobileType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
        case TelephonyManager.NETWORK_TYPE_1xRTT :
            return G2; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_CDMA :
            return G2; // ~ 14-64 kbps
        case TelephonyManager.NETWORK_TYPE_EDGE :
            return G2; // ~ 50-100 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_0 :
            return G3; // ~ 400-1000 kbps
        case TelephonyManager.NETWORK_TYPE_EVDO_A :
            return G3; // ~ 600-1400 kbps
        case TelephonyManager.NETWORK_TYPE_GPRS :
            return G2; // ~ 100 kbps
        case TelephonyManager.NETWORK_TYPE_HSDPA :
            return G3; // ~ 2-14 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPA :
            return G3; // ~ 700-1700 kbps
        case TelephonyManager.NETWORK_TYPE_HSUPA :
            return G3; // ~ 1-23 Mbps
        case TelephonyManager.NETWORK_TYPE_UMTS :
            return G3; // ~ 400-7000 kbps
        case TelephonyManager.NETWORK_TYPE_EHRPD :
            return G3; // ~ 1-2 Mbps
        case TelephonyManager.NETWORK_TYPE_EVDO_B :
            return G3; // ~ 5 Mbps
        case TelephonyManager.NETWORK_TYPE_HSPAP :
            return G3; // ~ 10-20 Mbps
        case TelephonyManager.NETWORK_TYPE_IDEN :
            return G2; // ~25 kbps
        case TelephonyManager.NETWORK_TYPE_LTE :
            return G4; // ~ 10+ Mbps
        case TelephonyManager.NETWORK_TYPE_UNKNOWN :
            return UNKNOWN;
        default:
            return UNKNOWN;
        }
    }

}
