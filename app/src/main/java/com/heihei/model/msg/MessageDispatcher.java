package com.heihei.model.msg;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import com.base.utils.LogWriter;
import com.heihei.model.msg.api.LiveMessageCallback;
import com.heihei.model.msg.bean.AbstractMessage;
import com.heihei.model.msg.bean.GiftMessage;
import com.heihei.model.msg.bean.LiveMessage;

import android.os.Handler;
import android.support.annotation.UiThread;

public class MessageDispatcher {
	private static MessageDispatcher mMessageDispatcher;
	private static LiveMessageCallback mMessageCallback;

	private static final BlockingDeque<AbstractMessage> textQueue = new LinkedBlockingDeque<>();
	private static final BlockingDeque<GiftMessage> giftQuene = new LinkedBlockingDeque<>();
	private static final BlockingQueue<LiveMessage> liveQuene = new LinkedBlockingDeque<>();

	private static boolean isJoinRoomStatus = true;

	private static Handler bulletHandler = new Handler();
	private static Handler giftHandler = new Handler();
	private static Handler liveHandler = new Handler();

	private static String mLiveId;

	public static MessageDispatcher getInstance() {
		if (mMessageDispatcher == null) {
			synchronized (MessageDispatcher.class) {
				if (mMessageDispatcher == null) {
					mMessageDispatcher = new MessageDispatcher();
				}
			}
		}
		return mMessageDispatcher;
	}

	private MessageDispatcher() {
	}

	public boolean getRoomStatus() {
		return true;
	}

	public void addTextToQuene(AbstractMessage messages) {
		// if (!isJoinRoomStatus)
		// throw new IllegalStateException("not join room put fail");
		textQueue.add(messages);
		LogWriter.i("jianfei", "addLiveLikeToQuene");
	}

	public void addGiftToQuene(GiftMessage messages) {
		// if (!isJoinRoomStatus)
		// throw new IllegalStateException("not join room put fail");
		giftQuene.add(messages);
		LogWriter.i("jianfei", "addGiftToQuene");
	}

	public void addLiveLikeToQuene(LiveMessage messages) {
		// if (!isJoinRoomStatus)
		// throw new IllegalStateException("not join room put fail");

		liveQuene.add(messages);
		LogWriter.i("jianfei", "addLiveLikeToQuene");
	}

	public void joinRoom(String liveId) {
//		isJoinRoomStatus = true;
		this.mLiveId = liveId;
		clearQuene();
		bulletHandler.postDelayed(bulletRunnable, 150L);
		giftHandler.postDelayed(giftRunnable, 150L);
		liveHandler.postDelayed(liveRunnable, 150L);
	}

	public void onDestroy() {
		try {
//			isJoinRoomStatus = false;
			UnSubscribeCallback();
			clearQuene();
			bulletHandler.removeCallbacks(bulletRunnable);
			giftHandler.removeCallbacks(giftRunnable);
			liveHandler.removeCallbacks(liveRunnable);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isJoinRoomStatus = false;
		}
	}

	private void clearQuene() {
		textQueue.clear();
		giftQuene.clear();
		liveQuene.clear();
	}

	public void SubscribeCallback(LiveMessageCallback callback) {
		this.mMessageCallback = callback;
	}

	public void UnSubscribeCallback() {
		this.mMessageCallback = null;
	}

	@UiThread
	private void updateTextUI(AbstractMessage message) {
		if (mMessageCallback != null)
			mMessageCallback.addTextCallbackView(message);
	}

	@UiThread
	private void updateGiftUI(GiftMessage message) {
		if (mMessageCallback != null)
			mMessageCallback.addGiftCallbackView(message);
	}

	@UiThread
	private void updateLiveLikeUI(LiveMessage message) {
		if (mMessageCallback != null)
			mMessageCallback.addLiveLikeCallbackView(message);
	}

	private Runnable bulletRunnable = new Runnable() {

		@Override
		public void run() {
			if (!textQueue.isEmpty()) {
				updateTextUI(textQueue.peek());
				textQueue.poll();
			}
			bulletHandler.postDelayed(this, 150L);
		}
	};

	private Runnable giftRunnable = new Runnable() {

		@Override
		public void run() {
			if (!giftQuene.isEmpty()) {
				updateGiftUI(giftQuene.peek());
				giftQuene.poll();
			}
			giftHandler.postDelayed(this, 150L);
		}
	};

	private Runnable liveRunnable = new Runnable() {

		@Override
		public void run() {
			if (!liveQuene.isEmpty()) {
				updateLiveLikeUI(liveQuene.peek());
				liveQuene.poll();
			}
			liveHandler.postDelayed(this, 150L);
		}
	};
}