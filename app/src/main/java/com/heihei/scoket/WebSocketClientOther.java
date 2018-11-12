package com.heihei.scoket;

public class WebSocketClientOther {
//public class WebSocketClientOther extends Service implements WebSocketListener {
//
//	private static final int CONNECT_TO_WEB_SOCKET = 1;
//	private static final int SEND_MESSAGE = 2;
//	private static final int CLOSE_WEB_SOCKET = 3;
//	private static final int DISCONNECT_LOOPER = 4;
//
//	private static final String KEY_MESSAGE = "keyMessage";
//
//	private Handler mServiceHandler;
//	private Looper mServiceLooper;
//	private WebSocket mWebSocket;
//	private boolean mConnected;
//
//	private final class ServiceHandler extends Handler {
//		public ServiceHandler(Looper looper) {
//			super(looper);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case CONNECT_TO_WEB_SOCKET:
//				connectToWebSocket();
//				break;
//			case SEND_MESSAGE:
//				sendMessageThroughWebSocket(msg.getData().getString(KEY_MESSAGE));
//				break;
//			case CLOSE_WEB_SOCKET:
//				closeWebSocket();
//				break;
//			case DISCONNECT_LOOPER:
//				mServiceLooper.quit();
//				break;
//			}
//		}
//	}
//
//	private void sendMessageThroughWebSocket(String message) {
//		if (!mConnected) {
//			return;
//		}
//		try {
//			mWebSocket.sendMessage(WebSocket.PayloadType.TEXT, new Buffer().write(message.getBytes()));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void connectToWebSocket() {
//		OkHttpClient okHttpClient = new OkHttpClient();
//		UserMgr.getInstance().loadLoginUser();
//		String url = "ws://123.56.0.31:8082/ws/channel?token=" + UserMgr.getInstance().getToken();
//		Log.i("jianfei", "url:"+url);
//		Request request = new Request.Builder().url(url).build();
//		mWebSocket = WebSocket.newWebSocket(okHttpClient, request);
//		try {
//			Response response = mWebSocket.connect(WebSocketClientOther.this);
//			if (response.code() == 101) {
//				mConnected = true;
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		Log.i("jianfei", "connectToWebSocket");
//	}
//
//	private void closeWebSocket() {
//		if (!mConnected) {
//			return;
//		}
//		try {
//			mWebSocket.close(1000, "Goodbye, World!");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	@Override
//	public void onCreate() {
//		super.onCreate();
//		Log.i("jianfei", "onCreate start");
//		HandlerThread thread = new HandlerThread("WebSocket service");
//		thread.start();
//		mServiceLooper = thread.getLooper();
//		mServiceHandler = new ServiceHandler(mServiceLooper);
//
//		mServiceHandler.sendEmptyMessage(CONNECT_TO_WEB_SOCKET);
//		Log.i("jianfei", "onCreate");
//
//	}
//
//	@Override
//	public void onDestroy() {
//		mServiceHandler.sendEmptyMessage(CLOSE_WEB_SOCKET);
//		mServiceHandler.sendEmptyMessage(DISCONNECT_LOOPER);
//		super.onDestroy();
//	}
//
//	@Override
//	public void onMessage(okio.BufferedSource payload, WebSocket.PayloadType type) throws IOException {
//		Log.i("jianfei", "onMessage");
//		if (type == WebSocket.PayloadType.TEXT) {
//			String str=payload.readUtf8();
//			Log.i("jianfei", "str:"+str);
//			payload.close();
//		}
//	}
//
//	@Override
//	public void onClose(int code, String reason) {
//		mConnected = false;
//		Log.i("jianfei", "onClose");
//	}
//
//	@Override
//	public void onFailure(IOException e) {
//		Log.i("jianfei", "onFailure");
//		mConnected = false;
//	}
}
