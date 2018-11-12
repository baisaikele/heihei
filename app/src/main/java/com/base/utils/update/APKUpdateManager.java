package com.base.utils.update;

import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.update.receivers.ApkInstallReceiver;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

public class APKUpdateManager {
	public static final String TAG = APKUpdateManager.class.getSimpleName();
	public static final String KEY_DOWNLOAD_ID = "downloadId";

	public static void download(Context context, String url, String title, String fileName) {
		long downloadId = SharedPreferencesUtil.getInstance().get(KEY_DOWNLOAD_ID, -1L);
		if (downloadId != -1L) {
			FileDownloadManager fdm = FileDownloadManager.getInstance(context);
			int status = fdm.getDownloadStatus(downloadId);
			if (status == DownloadManager.STATUS_SUCCESSFUL) {
				// 启动更新界面
				Uri uri = fdm.getDownloadUri(downloadId);
				if (uri != null) {
					if (compare(getApkInfo(context, uri.getPath()), context)) {
						ApkInstallReceiver.installApk(context, downloadId);
						return;
					} else {
						fdm.getDm().remove(downloadId);
					}
				}
				start(context, url, title, fileName);
			} else if (status == DownloadManager.STATUS_FAILED) {
				start(context, url, title, fileName);
			} else {
				LogWriter.i(TAG, "apk is already downloading");
			}
		} else {
			start(context, url, title, fileName);
		}
	}

	/**
	 * 下载的apk和当前版本比较
	 *
	 * @param apkInfo apk file's packageInfo
	 * @param context
	 *            Context
	 * @return 如果当前版本小于apk的版本则返回true
	 */
	public static boolean compare(PackageInfo apkInfo, Context context) {
		if (apkInfo == null) {
			LogWriter.i("BaseActivityUpdate", "apkInfo == null ");
			return false;
		}
		String localPackage = context.getPackageName();
		if (apkInfo.packageName.equals(localPackage)) {
			try {
				PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
				LogWriter.i("BaseActivityUpdate", "PackageInfo " + packageInfo.versionCode + "  " + packageInfo.versionName);
				LogWriter.i("BaseActivityUpdate", "apkInfo " + apkInfo.versionCode + "  " + apkInfo.versionName);
				if (apkInfo.versionCode > packageInfo.versionCode) {
					LogWriter.i("BaseActivityUpdate", "areturn true; ");
					return true;
				}
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		LogWriter.i("BaseActivityUpdate", "return false; ");
		return false;
	}

	/**
	 * 获取apk程序信息[packageName,versionName...]
	 *
	 * @param context Context
	 * @param path apk path
	 */
	public static PackageInfo getApkInfo(Context context, String path) {
		LogWriter.i("BaseActivityUpdate", "path "+path);
		PackageManager pm = context.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
		if (info != null) {
			return info;
		}
		return null;
	}

	private static void start(Context context, String url, String title, String fileName) {
		long id = FileDownloadManager.getInstance(context).startDownload(url, title, "下载完成后点击打开", fileName);
		LogWriter.i("BaseActivityUpdate", "start id "+id);
		SharedPreferencesUtil.getInstance().setValueLong(KEY_DOWNLOAD_ID, id);
		LogWriter.i(TAG, "apk start download " + id);
	}
}
