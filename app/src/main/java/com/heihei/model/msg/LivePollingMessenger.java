package com.heihei.model.msg;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.heihei.model.msg.bean.TextMessage;

import android.os.Handler;

public class LivePollingMessenger {
	private volatile boolean mSignedIn;
	private final BlockingDeque<TextMessage> mQueuedSendingMessages = new LinkedBlockingDeque<>();
	private Handler sendMessageHandler = new Handler();
	private static LivePollingMessenger mPollingMessenger;
	private Thread sendMsgThread;

//	public static LivePollingMessenger getInstance() {
//		if (mPollingMessenger == null) {
//			synchronized (LivePollingMessenger.class) {
//				if (mPollingMessenger == null) {
//					mPollingMessenger = new LivePollingMessenger();
//				}
//			}
//		}
//		mPollingMessenger.initSendMsgThread();
//		return mPollingMessenger;
//	}
//
//	private void initSendMsgThread() {
//		sendMsgThread = null;
////		sendMsgThread = new Thread(sendMessageRunnable);
//		sendMsgThread.start();
//	}

//	public void send(TextMessage message) {
//		synchronized (message) {
//			mQueuedSendingMessages.add(message);
//			return;
//		}
//	}

//	public ListenableFuture<JSONObject> sendMsg(final TextMessage msg) {
//		final SettableFuture<JSONObject> future = SettableFuture.create();
//		MessagePresent.getInstance().postMessage(msg, new JSONResponse() {
//
//			@Override
//			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
//				if (errCode == 0)
//					future.set(json);
//				else
//					future.set(null);
//			}
//		});
//		return future;
//	}
//
//	public ListenableFuture<JSONObject> sendBullet(final BulletMessage msg) {
//		final SettableFuture<JSONObject> future = SettableFuture.create();
//		MessagePresent.getInstance().postBullet(msg, new JSONResponse() {
//
//			@Override
//			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
//				if (errCode == 0)
//					future.set(json);
//				else
//					future.set(null);
//			}
//		});
//		return future;
//	}
//
//	public ListenableFuture<JSONObject> sendGift(final GiftMessage msg) {
//		final SettableFuture<JSONObject> future = SettableFuture.create();
//		MessagePresent.getInstance().postGift(msg, new JSONResponse() {
//
//			@Override
//			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
//				if (errCode == 0)
//					future.set(json);
//				else
//					future.set(null);
//			}
//		});
//		return future;
//	}
//
//	public ListenableFuture<JSONObject> sendLiveLike(final LiveMessage msg) {
//		final SettableFuture<JSONObject> future = SettableFuture.create();
//		MessagePresent.getInstance().postLiveLike(msg, new JSONResponse() {
//
//			@Override
//			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
//				if (errCode == 0)
//					future.set(json);
//				else
//					future.set(null);
//			}
//		});
//		return future;
//	}

//	private Runnable sendMessageRunnable = new Runnable() {
//
//		@Override
//		public void run() {
//			while (true) {
//				for (TextMessage m; (m = mQueuedSendingMessages.poll()) != null;)
//					sendMsg(m);
//			}
//		}
//	};
}
