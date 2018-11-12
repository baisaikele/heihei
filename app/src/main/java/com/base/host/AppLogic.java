package com.base.host;

import android.content.Intent;
import android.os.Environment;

import com.base.utils.DeviceInfoUtils;
import com.base.utils.NetUtil;
import com.base.utils.ThreadManager;
import com.facebook.fresco.FrescoConfigConstants;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.fragment.live.logic.GiftController.OnGiftListGetListener;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.BasePresent;
import com.heihei.model.Gift;
import com.heihei.websocket.WebSocketClient;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * app逻辑控制页面
 * @author admin
 *
 */
public class AppLogic {

	public static final boolean DEBUG = false;//是否开启debug

	public static final String VERSION = "1.1.2";// 对内版本号-----1.0
	public static final String API_VERSION = "1.0";// api版本号-----1.0
	public static final String BUILD_VERSION = "ea9c9f0";
	public static final String CHANNEL = "huawei";
	public static String APP = "9";// app代号
	public static String ZAPP = "9";// app代号统计使用
	public static final String OS_VERSION = "android:" + android.os.Build.VERSION.RELEASE;// os版本
	public static final String LOG_KEY = "867c084ab97ffdca19c7bada3c1583c7621b00ec";// log与服务器沟通时的秘钥
	public static final String MODE = "android";// android
	public static final boolean BUMENG_DEBUG = DEBUG;//友盟的集成测试

	public static boolean isInited = false;
	public static final int MIN_CLICK_DELAY_TIME = 1000;

	/** The m local external path. */
	public static String mLocalExternalPath = Environment.getExternalStorageDirectory().getAbsolutePath();

	// 应用的根目录
	/** The Constant ROOT. */
	public static String ROOT = "heihei";

	// 缺省根目录
	/** The default root path. */
	public static String defaultRootPath = mLocalExternalPath.concat("/").concat(ROOT);

	public static String defaultCache = defaultRootPath.concat("/cache/");
	public static String defaultChat = defaultRootPath.concat("/chat/");
	public static String defaultImage = defaultRootPath.concat("/images/");
	public static String defaultMessages = defaultRootPath.concat("/download_live_message/");
	public static String defaultRecordAudio = defaultRootPath.concat("/record_audio/");
	public static String defaultDownloadVoice = defaultRootPath.concat("/download_voice/");
	public static String defaultError = defaultRootPath.concat("/error/");
	public static String defaultTool = defaultRootPath.concat("/tools/");
	public static String defaultLog = defaultRootPath.concat("/logs");

	// private static ClassLoader mClassLoader;

	// public static ClassLoader getClassLoader() {
	// return mClassLoader;
	// }

	public static DeviceInfoUtils.PhoneInfo mPhoneInfo;

	public static synchronized void init() {
		try {
			if (isInited) {
				return;
			}
			isInited = true;
			// initClassLoader();
			ThreadManager.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					try {
						FrescoConfigConstants.initialize(HostApplication.getInstance());// 图片库初始化
						mkdirs();
						mPhoneInfo = DeviceInfoUtils.getPhoneInfo(HostApplication.getInstance());
						UserMgr.getInstance().loadLoginUser();// 加载用户信息
						BasePresent.requestInitUrls();// 请求服务器初始化地址

						if (UserMgr.getInstance().isLogined()) {
							Intent intent = new Intent(HostApplication.getInstance(), WebSocketClient.class);
							HostApplication.getInstance().startService(intent);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static HashMap<Integer, String> gifts = new HashMap<>();

	public static void initGift() {
		gifts.put(101, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-11.png");
		gifts.put(102, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-12.png");
		gifts.put(103, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-3.png");
		gifts.put(104, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-4.png");
		gifts.put(105, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-5.png");
		gifts.put(106, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-6.png");
		gifts.put(107, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-7.png");
		gifts.put(108, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-8.png");
		gifts.put(109, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-9.png");
		gifts.put(110, "http://cdn.wmlives.com/static/img/heihei/gift/gift-s-10.png");

		GiftController.getInstance().requestGiftList(new OnGiftListGetListener() {

			@Override
			public void onGiftGet(List<Gift> mGifts) {
				if (mGifts != null && mGifts.size() > 0) {
					for (int i = 0; i < mGifts.size(); i++) {
						Gift item = mGifts.get(i);
						if (gifts.containsKey(item.id)) {
							gifts.remove(item.id);
							gifts.put(item.id, item.icon);
						} else {
							gifts.put(item.id, item.icon);
						}
					}
				}
			}
		});
	}

	// public static void initClassLoader() {
	// // new Thread() {
	// //
	// // public void run() {
	// if (mClassLoader != null) {
	// return;
	// }
	// if (true) {
	// mClassLoader = HostApplication.getInstance().getClassLoader();
	// } else {
	// File optimizedDirectoryFile = HostApplication.getInstance().getDir("dex",
	// Context.MODE_PRIVATE);// 在应用安装目录下创建一个名为app_dex文件夹目录,如果已经存在则不创建
	// Log.d("zxy", optimizedDirectoryFile.getPath().toString());//
	// /data/data/com.example.dynamicloadapk/app_dex
	// //
	// 参数：1、包含dex的apk文件或jar文件的路径，2、apk、jar解压缩生成dex存储的目录，3、本地library库目录，一般为null，4、父ClassLoader
	// String apkPath = Environment.getExternalStorageDirectory().getPath() +
	// "/aaa.apk";
	//
	// File file = new File(apkPath);
	// if (file.exists()) {
	// mClassLoader = new HostClassLoader(apkPath,
	// optimizedDirectoryFile.getPath(), null, HostApplication
	// .getInstance().getClassLoader());
	// } else {
	// mClassLoader = HostApplication.getInstance().getClassLoader();
	// }
	// }
	// // };
	// // }.start();
	// }

	public static void mkdirs() {
		try {

			File file = new File(defaultImage);
			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(defaultRecordAudio);
			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(defaultTool);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File file = new File(defaultMessages);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File file = new File(defaultChat);
			if (!file.exists()) {
				file.mkdirs();
			}

			file = new File(defaultDownloadVoice);
			if (!file.exists()) {
				file.mkdirs();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File file = new File(defaultImage);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File file = new File(defaultCache);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			File file = new File(defaultLog);
			if (!file.exists()) {
				file.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 是否是系统的classLoader
	// *
	// * @return
	// */
	// public static boolean isSystemClassLoader() {
	// return mClassLoader == HostApplication.getInstance().getClassLoader();
	// }

	public static JSONObject createClientInfo() {
		JSONObject json = new JSONObject();
		try {
			json.put("type", MODE);
			json.put("version", VERSION);
			json.put("device_id", mPhoneInfo.mIMEI);
			json.put("network", NetUtil.netType(HostApplication.getInstance()));
			json.put("isp", NetUtil.getWebType(HostApplication.getInstance()));
			json.put("channel", APP);
			json.put("app", ZAPP);
			json.put("ssid", NetUtil.getSsidOrId(HostApplication.getInstance(), 0));
			json.put("os_version", OS_VERSION);
			json.put("device_model", mPhoneInfo.mManufacturerName + ":" + mPhoneInfo.mModelName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	// @SuppressLint("NewApi")
	// public static void fa(ClassLoader loader) {
	// Object currentActivityThread =
	// RefInvoke.invokeStaticMethod("Android.app.ActivityThread",
	// "currentActivityThread", new Class[] {}, new Object[] {});
	// String packageName = "com.jiyu.talk";
	// ArrayMap mPackages = (ArrayMap)
	// RefInvoke.getFieldOjbect("android.app.ActivityThread",
	// currentActivityThread,
	// "mPackages");
	// WeakReference wr = (WeakReference) mPackages.get(packageName);
	// /*
	// * DexClassLoader dLoader = new DexClassLoader(dex_in.getAbsolutePath(),
	// optimizedDexOutputPath.getAbsolutePath(), libPath, (ClassLoader)
	// RefInvoke.getFieldOjbect( "android.app.LoadedApk", wr.get(),
	// "mClassLoader"));
	// */
	// RefInvoke.setFieldOjbect("android.app.LoadedApk", "mClassLoader",
	// wr.get(), loader);
	// }

	// public static void monkeyPatchApplication(Context context, Application
	// bootstrap,
	// Application realApplication, String externalResourceFile) {
	// try {
	// // Find the ActivityThread instance for the current thread
	// Class<?> activityThread = Class.forName("android.app.ActivityThread");
	// Object currentActivityThread = getActivityThread(context,
	// activityThread);
	//
	// // Find the mInitialApplication field of the ActivityThread to the real
	// application
	// Field mInitialApplication =
	// activityThread.getDeclaredField("mInitialApplication");
	// mInitialApplication.setAccessible(true);
	// Application initialApplication = (Application)
	// mInitialApplication.get(currentActivityThread);
	// if (realApplication != null && initialApplication == bootstrap) {
	// // **2.替换掉ActivityThread.mInitialApplication**
	// mInitialApplication.set(currentActivityThread, realApplication);
	// }
	//
	// // Replace all instance of the stub application in
	// ActivityThread#mAllApplications with the
	// // real one
	// if (realApplication != null) {
	// Field mAllApplications =
	// activityThread.getDeclaredField("mAllApplications");
	// mAllApplications.setAccessible(true);
	// List<Application> allApplications = (List<Application>)
	// mAllApplications.get(currentActivityThread);
	// for (int i = 0; i < allApplications.size(); i++) {
	// if (allApplications.get(i) == bootstrap) {
	// // **1.替换掉ActivityThread.mAllApplications**
	// allApplications.set(i, realApplication);
	// }
	// }
	// }
	//
	// // Figure out how loaded APKs are stored.
	//
	// // API version 8 has PackageInfo, 10 has LoadedApk. 9, I don't know.
	// Class<?> loadedApkClass;
	// try {
	// loadedApkClass = Class.forName("android.app.LoadedApk");
	// } catch (ClassNotFoundException e) {
	// loadedApkClass = Class.forName("android.app.ActivityThread$PackageInfo");
	// }
	// Field mApplication = loadedApkClass.getDeclaredField("mApplication");
	// mApplication.setAccessible(true);
	// Field mResDir = loadedApkClass.getDeclaredField("mResDir");
	// mResDir.setAccessible(true);
	//
	// // 10 doesn't have this field, 14 does. Fortunately, there are not many
	// Honeycomb devices
	// // floating around.
	// Field mLoadedApk = null;
	// try {
	// mLoadedApk = Application.class.getDeclaredField("mLoadedApk");
	// } catch (NoSuchFieldException e) {
	// // According to testing, it's okay to ignore this.
	// }
	//
	// // Enumerate all LoadedApk (or PackageInfo) fields in
	// ActivityThread#mPackages and
	// // ActivityThread#mResourcePackages and do two things:
	// // - Replace the Application instance in its mApplication field with the
	// real one
	// // - Replace mResDir to point to the external resource file instead of
	// the .apk. This is
	// // used as the asset path for new Resources objects.
	// // - Set Application#mLoadedApk to the found LoadedApk instance
	// for (String fieldName : new String[] { "mPackages", "mResourcePackages"
	// }) {
	// Field field = activityThread.getDeclaredField(fieldName);
	// field.setAccessible(true);
	// Object value = field.get(currentActivityThread);
	//
	// for (Map.Entry<String, WeakReference<?>> entry : ((Map<String,
	// WeakReference<?>>) value).entrySet()) {
	// Object loadedApk = entry.getValue().get();
	// if (loadedApk == null) {
	// continue;
	// }
	//
	// if (mApplication.get(loadedApk) == bootstrap) {
	// if (realApplication != null) {
	// // **3.替换掉mApplication**
	// mApplication.set(loadedApk, realApplication);
	// }
	// if (externalResourceFile != null) {
	// // 替换掉资源目录
	// mResDir.set(loadedApk, externalResourceFile);
	// }
	//
	// if (realApplication != null && mLoadedApk != null) {
	// // **4.替换掉mLoadedApk**
	// mLoadedApk.set(realApplication, loadedApk);
	// }
	// }
	// }
	// }
	// } catch (Throwable e) {
	// throw new IllegalStateException(e);
	// }
	// }
}
