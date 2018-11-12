package com.base.utils.update.receivers;

import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

public class ApkInstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			long downloadApkId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			long id = SharedPreferencesUtil.getInstance().get("downloadId", -1L);
			if (downloadApkId == id) {
				installApk(context, downloadApkId);
			}
		}
	}

	public static void installApk(Context context, long downloadApkId) {
		DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		try {
			Cursor cursor = dManager.query(new DownloadManager.Query().setFilterById(downloadApkId));
			if (cursor == null) {
				LogWriter.i("DownloadManager", "FAILED: Unable to read downloaded file");
				return;
			}
			cursor.moveToFirst();
			String path = "file://" + cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
			Uri downloadFileUri = Uri.parse(path);

			if (downloadFileUri != null) {
				LogWriter.i("downloadmanager", "dm query: " + path);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
				context.startActivity(intent);
			} else {
				LogWriter.i("DownloadManager", "下载失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				Intent install = new Intent(Intent.ACTION_VIEW);
				Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadApkId);
				if (downloadFileUri != null) {
					LogWriter.i("DownloadManager", downloadFileUri.toString());
					install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
					install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(install);
				} else {
					LogWriter.i("DownloadManager", "download error");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}
