package com.base.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.base.host.AppLogic;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.utils.FileUtils;
import com.base.utils.LogWriter;
import com.base.utils.MD5;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.heihei.logic.UserMgr;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.wmlives.heihei.R;

import android.R.color;
import android.content.Context;
import android.util.Log;

public class HttpUtil {

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	public static final MediaType TEXT_PLAIN = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");

	private static OkHttpClient client;

	/** okhttp用的hearder */
	private static Headers headers_okhttp;
	static {
		client = new OkHttpClient();
		client.setConnectTimeout(5, TimeUnit.SECONDS);
		client.setReadTimeout(2, TimeUnit.SECONDS);
		client.setWriteTimeout(2, TimeUnit.SECONDS);
		client.setRetryOnConnectionFailure(false);

		Headers.Builder builder = new Headers.Builder();
		builder.add("Appkey", "");
		builder.add("Udid", "");
		builder.add("Os", "");
		builder.add("Osversion", "");
		builder.add("Appversion", "");
		builder.add("Sourceid", "");
		builder.add("Ver", "");
		builder.add("x-jiyu-uid", "");
		builder.add("x-jiyu-v", "");
		builder.add("x-jiyu", "");

		builder.add("ap", "heihei");
		builder.add("version", AppLogic.VERSION);
		builder.add("buildNumber", AppLogic.BUILD_VERSION);
		builder.add("local", "zh_CN");
		try {
			builder.add("uuid", AppLogic.mPhoneInfo.mIMEI);
			builder.add("model", AppLogic.mPhoneInfo.mManufacturerName + ":" + AppLogic.mPhoneInfo.mModelName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		builder.add("resolution", AppLogic.VERSION);
		builder.add("apiVersion", AppLogic.API_VERSION);
		builder.add("deveiceType", "android");
		builder.add("channel", AppLogic.CHANNEL);

		if (UserMgr.getInstance().isLogined()) {
			builder.add("token", UserMgr.getInstance().getToken());
		}
		headers_okhttp = builder.build();
	}

	/**
	 * 异步的get请求，默认不读缓存
	 * 
	 * @param url
	 * @param param
	 * @param response
	 */
	public static void getAsync(String url, HttpParam param, final JSONResponse response) {
		getAsync(url, param, response, false);
	}

	/**
	 * 异步的post请求，默认不读缓存
	 * 
	 * @param url
	 * @param param
	 * @param response
	 */
	public static void postAsync(String url, HttpParam param, final JSONResponse response) {
		postAsync(url, param, response, false);
	}

	/**
	 * 异步的post请求，回调在UI线程
	 * 
	 * @param url
	 * @param param
	 * @param response
	 */
	public static void postAsync(final String url, HttpParam param, final JSONResponse response, final boolean readCache) {
		// if (!URLUtil.isHttpUrl(url)) {
		// return;
		// }
		String content = "";
		if (param != null) {
			content = param.toFormString();
		}

		RequestBody body = RequestBody.create(TEXT_PLAIN, content);
		String tag = MD5.getMD5(url + content);
		try {
			client.cancel(tag);
		} catch (Exception e2) {

		}
		final String key = param.createMD5Key(url);
		if (readCache) {
			String result = readHttpCache(HostApplication.getInstance(), key);
			if (result != null) {
				try {
					JSONObject jResult = new JSONObject(result);
					int errCode = ErrorCode.ERROR_FAIL;
					String msg = "";
					try {
						errCode = jResult.optInt("errorcode", ErrorCode.ERROR_FAIL);
						msg = jResult.optString("errormsg");
						if (errCode == ErrorCode.ERROR_OK) {
							if (response != null) {
								response.onJsonResponse(jResult, errCode, msg, true);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		Request request = new Request.Builder().url(url).headers(headers_okhttp).post(body).tag(tag).build();
		Call call = client.newCall(request);

		LogWriter.d("http", "post request:" + url + "--:" + content);

		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response res) throws IOException {
				LogWriter.i("httpclient", "onResponse " + client.getConnectTimeout());
				if (res.isSuccessful() && res.body() != null) {
					final String result = res.body().string();
					// UIUtils.showToast(result);
					LogWriter.d("http", "post result:" + "url=" + url + "   " + result);
					JSONObject json = null;
					int errCode = ErrorCode.ERROR_FAIL;
					String msg = "";
					try {
						json = new JSONObject(result);
						errCode = json.optInt("errorcode", ErrorCode.ERROR_FAIL);
						msg = json.optString("errormsg");
					} catch (JSONException e) {
						e.printStackTrace();
					}

					if (readCache && json != null && errCode == ErrorCode.ERROR_OK) {
						writeHttpCache(key, json.toString());
					}

					final JSONObject jResult = json;
					final int code = errCode;
					final String message = msg;

					HostApplication.getInstance().getMainHandler().post(new Runnable() {

						@Override
						public void run() {
							if (response != null) {

								response.onJsonResponse(jResult, code, message, false);

								if (code == ErrorCode.ERROR_TOKEN_ERROR)// token无效，重新登录
								{
									if (StringUtils.isAppOnForeground(HostApplication.getInstance()) && HostApplication.getInstance().getMainActivity() != null) {
										UIUtils.showToast(R.string.account_login_other_tip);
										UserMgr.getInstance().unlogin();
										NavigationController.gotoLogin(HostApplication.getInstance());
									}
								}
							}
						}
					});

				} else {
					HostApplication.getInstance().getMainHandler().post(new Runnable() {

						@Override
						public void run() {
							if (response != null) {
								LogWriter.i("httpclient", "onResponse ErrorCode.ERROR_FAIL");
								response.onJsonResponse(null, ErrorCode.ERROR_FAIL, "失败", false);
							}
						}
					});

				}
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				LogWriter.i("httpclient", "onFailure " + client.getConnectTimeout());
				HostApplication.getInstance().getMainHandler().post(new Runnable() {

					@Override
					public void run() {
						if (response != null) {
							response.onJsonResponse(null, ErrorCode.ERROR_FAIL, "失败", false);
						}
					}
				});
			}
		});
	}

	/**
	 * 异步的get请求，回调在UI线程
	 * 
	 * @param url
	 * @param param
	 * @param response
	 */
	public static void getAsync(String url, HttpParam param, final JSONResponse response, final boolean readCache) {
		//
		// if (!URLUtil.isHttpUrl(url)) {
		// return;
		// }

		String content = "";
		if (param != null) {
			content = param.toGetString();
		}

		if (!url.endsWith("?")) {
			url = url + "?";
		}

		url = url + content;

		String tag = MD5.getMD5(url);
		try {
			client.cancel(tag);
		} catch (Exception e2) {

		}

		final String key = param.createMD5Key(url);
		if (readCache) {
			String result = readHttpCache(HostApplication.getInstance(), key);
			if (result != null) {
				try {
					JSONObject jResult = new JSONObject(result);
					int errCode = ErrorCode.ERROR_FAIL;
					String msg = "";
					try {
						errCode = jResult.optInt("errorcode", ErrorCode.ERROR_FAIL);
						msg = jResult.optString("errormsg");
						if (errCode == ErrorCode.ERROR_OK) {
							if (response != null) {
								response.onJsonResponse(jResult, errCode, msg, true);
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		Request request = new Request.Builder().url(url).headers(headers_okhttp).get().tag(tag).build();
		Call call = client.newCall(request);

		LogWriter.d("http", "get request:" + url + "--:" + content);

		final String requestUrl = url;

		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response res) throws IOException {
				LogWriter.i("httpclient", "onResponse " + client.getConnectTimeout());
				if (res.isSuccessful() && res.body() != null) {
					final String result = res.body().string();

					com.base.utils.LogWriter.i("httpUtils", "get result:" + "url=" + requestUrl + "   " + result);

					HostApplication.getInstance().getMainHandler().post(new Runnable() {

						@Override
						public void run() {
							if (response != null) {
								JSONObject json = null;
								int errCode = ErrorCode.ERROR_FAIL;
								String msg = "";
								try {
									json = new JSONObject(result);
									errCode = json.optInt("errorcode", ErrorCode.ERROR_FAIL);
									msg = json.optString("errormsg");
								} catch (JSONException e) {
									e.printStackTrace();
								}

								if (readCache && json != null && errCode == ErrorCode.ERROR_OK) {
									writeHttpCache(key, json.toString());
								}

								response.onJsonResponse(json, errCode, msg, false);

								if (errCode == ErrorCode.ERROR_TOKEN_ERROR) {
									if (StringUtils.isAppOnForeground(HostApplication.getInstance()) && HostApplication.getInstance().getMainActivity() != null) {
										UIUtils.showToast(R.string.account_login_other_tip);
										UserMgr.getInstance().unlogin();
										NavigationController.gotoLogin(HostApplication.getInstance());
									}
								}

							}
						}
					});

				} else {
					if (response != null) {
						HostApplication.getInstance().getMainHandler().post(new Runnable() {

							public void run() {
								LogWriter.i("httpclient", "onResponse ErrorCode.ERROR_FAIL");
								response.onJsonResponse(null, ErrorCode.ERROR_FAIL, "失败", false);
							}
						});

					}
				}
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				LogWriter.i("httpclient", "onFailure " + client.getConnectTimeout());
				HostApplication.getInstance().getMainHandler().post(new Runnable() {

					@Override
					public void run() {
						if (response != null) {
							response.onJsonResponse(null, ErrorCode.ERROR_FAIL, "失败", false);
						}
					}
				});
			}

		});
	}

	/**
	 * 同步的post请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static JSONObject postSync(String url, HttpParam param) {
		JSONObject json = null;

		String content = "";
		if (param != null) {
			content = param.toPostString();
		}

		RequestBody body = RequestBody.create(JSON, content);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String result = response.body().string();
				json = new JSONObject(result);
				return json;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 同步的get请求
	 * 
	 * @param url
	 * @param param
	 * @return
	 */
	public static JSONObject getSync(String url, HttpParam param) {
		JSONObject json = null;

		String content = "";
		if (param != null) {
			content = param.toGetString();
		}

		if (!url.endsWith("?")) {
			url = url + "?";
		}

		url = url + content;
		Request request = new Request.Builder().url(url).get().build();
		Response response = null;
		try {
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				String result = response.body().string();
				json = new JSONObject(result);
				return json;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 表单提交
	 * 
	 * @param url
	 * @param map
	 * @param file
	 * @param response
	 */
	public static void formUpload(String url, Map<String, String> map, File file, final JSONResponse response) {

		MultipartBuilder builder = new MultipartBuilder();
		if (file != null && file.exists()) {
			RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
			builder.addFormDataPart("file", file.getName(), fileBody);
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			builder.addFormDataPart(key, value);
		}

		RequestBody body = builder.build();
		Request request = new Request.Builder().url(url).post(body).build();
		Call call = client.newCall(request);

		call.enqueue(new Callback() {

			@Override
			public void onResponse(final Response res) throws IOException {
				// TODO Auto-generated method stub
				if (res.isSuccessful() && res.body() != null) {
					final String result = res.body().string();
					HostApplication.getInstance().getMainHandler().post(new Runnable() {

						@Override
						public void run() {
							if (response != null) {
								JSONObject json = null;
								int errCode = ErrorCode.ERROR_OK;
								String msg = "";
								try {
									json = new JSONObject(result);
									if (json.optJSONObject("meta") != null) {
										errCode = json.optJSONObject("meta").optInt("code", ErrorCode.ERROR_OK);
										msg = json.optJSONObject("meta").optString("msg");
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								response.onJsonResponse(json, errCode, msg, false);
							}
						}
					});

				} else {
					if (response != null) {
						response.onJsonResponse(null, ErrorCode.ERROR_FAIL, "失败", false);
					}
				}
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				HostApplication.getInstance().getMainHandler().post(new Runnable() {

					@Override
					public void run() {
						if (response != null) {
							response.onJsonResponse(null, ErrorCode.ERROR_FAIL, "失败", false);
						}
					}
				});
			}
		});
	}

	public static void downloadFile(final String url, final File dstFile, final DownloadListener mListener) {
		String tag = MD5.getMD5(url);
		try {
			client.cancel(tag);
		} catch (Exception e2) {

		}
		Request request = new Request.Builder().url(url).headers(headers_okhttp).tag(tag).build();
		Call call = client.newCall(request);
		call.enqueue(new Callback() {

			@Override
			public void onResponse(Response response) throws IOException {
				if (response.isSuccessful()) {
					InputStream is = null;
					byte[] buf = new byte[2018];
					int len = 0;
					FileOutputStream fos = null;
					try {
						is = response.body().byteStream();
						final long length = response.body().contentLength();
						long sum = 0;
						fos = new FileOutputStream(dstFile);
						while ((len = is.read(buf)) != -1) {
							sum += len;
							fos.write(buf, 0, len);
						}
					} catch (Exception e) {

						if (dstFile != null) {
							dstFile.deleteOnExit();
						}

						if (mListener != null) {
							mListener.onFailed(url);
						}

					} finally {
						if (is != null) {
							is.close();
						}

						if (fos != null) {
							fos.flush();
							fos.close();
						}

						if (mListener != null) {
							mListener.onSuccess(url);
						}
					}

				} else {
					if (mListener != null) {
						mListener.onFailed(url);
					}
				}
			}

			@Override
			public void onFailure(Request arg0, IOException arg1) {
				if (mListener != null) {
					mListener.onFailed(url);
				}
			}
		});
	}

	/**
	 * 读缓存
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String readHttpCache(Context context, String key) {
		String path = AppLogic.defaultCache + key;
		return FileUtils.readSDCard(context, path);
	}

	/**
	 * 写缓存
	 * 
	 * @param key
	 * @param content
	 */
	public static void writeHttpCache(String key, String content) {
		String path = AppLogic.defaultCache + key;
		File file = new File(AppLogic.defaultCache);
		if (!file.exists()) {
			file.mkdirs();
		}
		FileUtils.write2SDcardAbsolute(path, content, false);
	}

	public interface DownloadListener {

		public void onSuccess(String url);

		public void onFailed(String url);
	}

}
