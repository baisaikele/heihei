package com.heihei.fragment.live.logic;

import java.lang.ref.WeakReference;

import org.json.JSONObject;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.heihei.logic.present.LivePresent;

/**
 * 心跳管理器
 * 
 * @author chengbo
 */
public class HeartBeatController {

    private String liveId = "";
    private boolean isCreator = false;

    private LivePresent mLivePresent;

    private int status = STATUS_STOPED;
    public static final int STATUS_STOPED = 0;// 已停止
    public static final int STATUS_RUNNING = 1;// 运行中

    public static final int DURATION = 15;// 间隔时间，秒

    private int errorCount = 0;

    /**
     * @param liveId
     *            房间号
     * @param isCreator
     *            是否是房主
     */
    public HeartBeatController(String liveId, boolean isCreator) {
        this.liveId = liveId;
        this.isCreator = isCreator;
    }

    /**
     * 开始心跳
     */
    public void startHeartBeat() {
        if (mLivePresent == null) {
            mLivePresent = new LivePresent();
        }

        status = STATUS_RUNNING;

        mLivePresent.heartBeat(liveId, isCreator, response);
    }

    private boolean isRunning() {
        return status == STATUS_RUNNING;
    }

    /**
     * 结束心跳
     */
    public void stopHeartBeat() {
        status = STATUS_STOPED;
        mHandler.removeMessages(0);
        mOnHeartbeatErrorListener = null;
    }

    private JSONResponse response = new JSONResponse() {

        @Override
        public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
            if (errCode == ErrorCode.ERROR_OK) {
                errorCount = 0;
                int nextTimeout = json.optInt("nextTimeout");
                if (nextTimeout <= 0) {
                    nextTimeout = DURATION;
                }

                if (isCreator) {
                    nextTimeout = 10;
                }

                if (isRunning()) {
                    Message message = Message.obtain();
                    message.obj = new WeakReference<HeartBeatController>(HeartBeatController.this);
                    mHandler.sendMessageDelayed(message, nextTimeout * 1000);
                }
            } else {
                errorCount += 1;

                if (errorCount >= 2) {
                    if (mOnHeartbeatErrorListener != null) {
                        mOnHeartbeatErrorListener.onHeartbearError();
                    }
                    stopHeartBeat();
                    return;
                }

                int nextTimeout = DURATION;

                if (isCreator) {
                    nextTimeout = 10;
                }

                if (isRunning()) {
                    Message message = Message.obtain();
                    message.obj = new WeakReference<HeartBeatController>(HeartBeatController.this);
                    mHandler.sendMessageDelayed(message, nextTimeout * 1000);
                }
            }

        }
    };

    private static Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            WeakReference<HeartBeatController> wr = (WeakReference<HeartBeatController>) msg.obj;
            if (wr != null && wr.get() != null) {
                if (wr.get().isRunning()) {
                    wr.get().startHeartBeat();
                }
            }
        };
    };

    private OnHeartbeatErrorListener mOnHeartbeatErrorListener;

    public void setOnHeartbeatErrorListener(OnHeartbeatErrorListener mOnHeartbeatErrorListener) {
        this.mOnHeartbeatErrorListener = mOnHeartbeatErrorListener;
    }

    /**
     * 心跳错误回调
     * 
     * @author chengbo
     */
    public static interface OnHeartbeatErrorListener {

        public void onHeartbearError();
    }

}
