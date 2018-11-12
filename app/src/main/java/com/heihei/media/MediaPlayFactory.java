package com.heihei.media;

import android.content.Context;
import android.os.PowerManager;

import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;

public class MediaPlayFactory {

    public static PLMediaPlayer createMediaPlayer(Context context,boolean isLiveStreaming) {
        AVOptions mAVOptions = new AVOptions();

        int isLive = isLiveStreaming ? 1 : 0;
        // the unit of timeout is ms
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        mAVOptions.setInteger(AVOptions.KEY_LIVE_STREAMING, isLive);
        if (isLiveStreaming) {
            mAVOptions.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
//        int codec = getIntent().getIntExtra("mediaCodec", 0);
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0);

        // whether start play automatically after prepared, default value is 1
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 1);
        PLMediaPlayer player = new PLMediaPlayer(context.getApplicationContext(), mAVOptions);
        player.setWakeMode(context.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        return player;
    }
}
