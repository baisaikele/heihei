package com.heihei.websocket;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.base.utils.HistoryUtils;
import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.PmPresent;
import com.heihei.model.msg.due.DueMessageUtils;
import com.heihei.scoket.MessageDistribute;
import com.loc.br;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.squareup.okhttp.ws.WebSocket;

import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;
import com.squareup.okhttp.ws.WebSocket.PayloadType;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import okio.Buffer;
import okio.BufferedSource;

public class WebSocketClient extends Service implements WebSocketListener {
	public static String TAG = "WebSocketClient";
	private final static OkHttpClient httpClient = new OkHttpClient();

	public static final int CODE_CLOSE = 0;
	public static final int STATE_IDLE = 0;// 闲置状态
	public static final int STATE_CONNECTING = 1;// 正在连接
	public static final int STATE_CONNECTED = 2;// 已连接上
	public static final int STATE_DISCONNECTED = 3;// 已断开连接
	public static final int STATE_RECONNECTION = 4;// chongxin连接
	public static final int STATE_SEND_PING = 5;// chongxin连接
	public static final int STATE_ONMESSAGE = 6;// chongxin连接
	private static int state = STATE_IDLE;// 当前状态

	private WebSocket mWebSocket;
	private Looper mServiceLooper;
	private static Handler mServiceHandler;
	private int tryNumber = 0;
	private Timer mTimer = new Timer();

	private SengPingTimer mTimerTask;

	@Override
	public void onCreate() {
		super.onCreate();
		HandlerThread thread = new HandlerThread("WebSocket service");
		thread.start();
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
		mServiceHandler.sendEmptyMessage(STATE_CONNECTING);
		mTimerTask = new SengPingTimer();
		mTimer.schedule(mTimerTask, 5000, 5000);

		log("onCreate");
	}

	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case STATE_CONNECTING:
				connectToWebSocket();
				break;
			case STATE_DISCONNECTED:
				disconnect();
				break;
			case STATE_RECONNECTION:
				reconnect();
				break;
			case STATE_SEND_PING:
				sendPing();
				break;
			case STATE_ONMESSAGE:
				try {
					String res = (String) msg.obj;
					LogWriter.i(TAG, res.toString());
					MessageDistribute.getInstance().sendMessage(res);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 连接
	 */
	public void connectToWebSocket() {
		log("connectToWebSocket_start");
		if (mWebSocket != null && (state == STATE_CONNECTING || state == STATE_CONNECTED)) {
			disconnect();
		}

		UserMgr.getInstance().loadLoginUser();
		String url = PmPresent.getInstance().getWebScoketUrl() + "?token=" + UserMgr.getInstance().getToken();
		log("socket_url:" + url);
		Request r = new Request.Builder().url(url).build();
		state = STATE_CONNECTING;
		WebSocketCall call = WebSocketCall.create(httpClient, r);
		call.enqueue(this);
		log("connectToWebSocket_end");
	}

	private boolean isConnectInt = false;

	public void reconnect() {
		if (isConnectInt)
			return;

		isConnectInt = true;
		log("reconnect start");
		try {
			if (mWebSocket != null) {
				mWebSocket.close(CODE_CLOSE, "");
				mWebSocket = null;
			} else {
				mWebSocket = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			log(e.getMessage());
		} finally {
			try {
				UserMgr.getInstance().loadLoginUser();
				String url = PmPresent.getInstance().getWebScoketUrl() + "?token=" + UserMgr.getInstance().getToken();
				log("socket_url:" + url);
				Request r = new Request.Builder().url(url).build();
				state = STATE_CONNECTING;
				WebSocketCall call = WebSocketCall.create(httpClient, r);
				call.enqueue(this);
				tryNumber = 0;
				trymWebSocket = 0;
				log("reconnect end");
			} catch (Exception e2) {
				log("reconnect fail " + e2.getMessage());
			}

		}

		isConnectInt = false;
	}

	public void disconnect() {
		log("disconnect");

		if (UserMgr.getInstance().isLogined() && state != STATE_CONNECTED) {
			reconnectNum++;
			if (reconnectNum > 20) {
				reconnectTime = 60;
				mServiceHandler.sendEmptyMessageDelayed(STATE_RECONNECTION, reconnectTime * 1000);
			} else if (state != reconnectTime) {
				mServiceHandler.sendEmptyMessageDelayed(STATE_RECONNECTION, 5 * 1000);
			}
		}
	}

	private int reconnectNum = 0;
	private int reconnectTime = 5;

	public static void unLogin() {
		try {
			mServiceHandler.sendEmptyMessage(STATE_DISCONNECTED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msg) {
		try {
			if (mWebSocket != null && state == STATE_CONNECTED) {
				mWebSocket.sendMessage(WebSocket.PayloadType.TEXT, new Buffer().write(msg.getBytes()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPing() {
		String msg = "ping";
		try {
			if (mWebSocket != null && state == STATE_CONNECTED) {
				mWebSocket.sendPing(new Buffer().writeUtf8(msg));
				tryNumber++;
				if (tryNumber > 3) {
					state = STATE_DISCONNECTED;
					mServiceHandler.sendEmptyMessageDelayed(STATE_DISCONNECTED, 5000);
				}
				log("sendPing" + " trynumber " + tryNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(int code, String reason) {
		this.state = STATE_DISCONNECTED;
		log("onClose");
		mServiceHandler.sendEmptyMessageDelayed(STATE_DISCONNECTED, 5000);
	}

	@Override
	public void onFailure(IOException e, Response res) {
		log("onFailure");
		this.state = STATE_DISCONNECTED;
		try {
			tryNumber = 0;
			trymWebSocket = 0;

			mServiceHandler.sendEmptyMessageDelayed(STATE_DISCONNECTED, 5000);
		} catch (Exception e2) {
			e.printStackTrace();
		}

	}

	@Override
	public void onMessage(BufferedSource source, PayloadType type) throws IOException {
		log("onMessage");

		if (WebSocket.PayloadType.TEXT.ordinal() == type.ordinal()) {
			String msg = source.readUtf8();
			log("message " + msg.toString());
			Message m = Message.obtain();
			m.what = STATE_ONMESSAGE;
			m.obj = msg;
			mServiceHandler.sendMessage(m);
			source.close();
		}
	}

	@Override
	public void onOpen(WebSocket websocket, Response res) {
		this.state = STATE_CONNECTED;
		this.mWebSocket = websocket;

		if (System.currentTimeMillis() - queryAllmsgTimestamp > 20000) {
			HistoryUtils.getInstance().getAllMessage();
			queryAllmsgTimestamp =System.currentTimeMillis();
		}

		log("onOpen");
	}

	private long queryAllmsgTimestamp = 0;

	@Override
	public void onPong(Buffer payload) {
		tryNumber = 0;
		trymWebSocket = 0;
		if (payload != null && payload.readUtf8() != null) {
			log("onPong" + " tryNumber " + tryNumber + "	payload : " + payload.readUtf8());
			payload.close();
		} else {
			log("onPong" + " tryNumber " + tryNumber + "	payload : " + payload.readUtf8());
		}
		log("-----------------------------------");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mServiceHandler.sendEmptyMessage(STATE_DISCONNECTED);
	}

	public void log(String msg) {
		LogWriter.i(TAG, msg);
	}

	private int trymWebSocket = 0;

	class SengPingTimer extends TimerTask {

		@Override
		public void run() {
			if (mWebSocket != null) {
				mServiceHandler.sendEmptyMessage(STATE_SEND_PING);
			} else {
				trymWebSocket++;
				log("trymWebSocket : " + trymWebSocket);
				if (trymWebSocket > 4) {
					trymWebSocket = 0;
					mServiceHandler.sendEmptyMessageDelayed(STATE_DISCONNECTED, 5000);
				}
			}
		}
	}
}
