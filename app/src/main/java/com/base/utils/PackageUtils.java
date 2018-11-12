package com.base.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
/**
 * ---------------------------------------------------------------
 * Author: Jianfei.G
 * Create: 2015/7/17 18:12
 * ---------------------------------------------------------------
 * Describe: 手机安装程序判别
 * ---------------------------------------------------------------
 * Changes:
 * ---------------------------------------------------------------
 * 2015/7/17 13 : Create by Jianfei.G
 * ---------------------------------------------------------------
 */
public class PackageUtils {
	
	public static class PKGName {
        public static String PKGNAME_WEIBO = "com.sina.weibo";
        public static String PKGNAME_WECHAT = "com.tencent.mm";
        public static String PKGNAME_QQ = "com.tencent.mobileqq";
    }
	
	
	public static boolean isPackageInstalled(Context context, String packageName) {
		if (packageName == null)
			return false;
		
		String curPackageName = context.getPackageName();
		if (curPackageName != null && curPackageName.equals(packageName))
			return true;
		
		PackageManager pm = context.getPackageManager();
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
		} catch (Exception e) { // handle TransactionTooLargeException for eBug YMK141205-0004[Share page]AP crash after launch AP via recent App.
			try {
				pm.getPackageGids(packageName);
				return true;
			} catch (NameNotFoundException e1) {
			} catch (Exception e1) {
			}
		}
		return false;
	}
}
