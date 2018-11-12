package com.heihei.media;

import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

import com.wmlives.heihei.R;


public class RingtoneController {
    
    private static final int MESSAGE = R.raw.send_message;
    private static final int MATCH = R.raw.match_success;
    private static final int RINGBELL = R.raw.ring_bell;
    
    private static final ConcurrentHashMap<Integer, Integer> mMaps = new ConcurrentHashMap<>();
    
    private static SoundPool sp = new SoundPool(50, AudioManager.STREAM_MUSIC, 5);
    
    private static float LEFT_VOLUME = (float) 0.4;
    private static  float RIGHT_VOLUME = (float) 0.4;
    
    /**
     * 播放发送消息铃声
     */
    public static void playMessageRingtone(Context context)
    {
        if (mMaps.containsKey(MESSAGE))
        {
            int soundId = mMaps.get(MESSAGE);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(context, MESSAGE, 0);
        sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(MESSAGE, soundId);
                sp.play(soundId, (float)0.8, (float)0.8, 1, 0, 1);
            }
        });
    }
    
    /**
     * 播放匹配成功铃声
     */
    public static void playMatchSuccessRingtone(Context context)
    {
        if (mMaps.containsKey(MATCH))
        {
            int soundId = mMaps.get(MATCH);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(context, MATCH, 0);
        sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(MATCH, soundId);
                sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            }
        });
    }
    
    /**
     * 播放铃铛响起铃声
     */
    public static void playRingBellRingtone(Context context)
    {
        if (mMaps.containsKey(RINGBELL))
        {
            int soundId = mMaps.get(RINGBELL);
            sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            return;
        }
        final int soundId = sp.load(context, RINGBELL, 0);
        sp.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                mMaps.put(RINGBELL, soundId);
                sp.play(soundId, LEFT_VOLUME, RIGHT_VOLUME, 1, 0, 1);
            }
        });
    }
}
