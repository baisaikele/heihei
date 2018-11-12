package com.heihei.media;

import android.content.Context;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.rtcstreaming.RTCConferenceOptions;
import com.qiniu.pili.droid.rtcstreaming.RTCMediaStreamingManager;
import com.qiniu.pili.droid.rtcstreaming.RTCSurfaceView;
import com.qiniu.pili.droid.rtcstreaming.RTCVideoWindow;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

public class StreamingManagerFactory {

	// public static MediaStreamingManager createMediaStreamingManger(Context
	// context, JSONObject json, StreamingStateChangedListener
	// mStreamingStateChangedListener) {
	// MediaStreamingManager manager = new MediaStreamingManager(context,
	// AVCodecType.HW_AUDIO_CODEC);
	// StreamingProfile profile = new StreamingProfile();
	// StreamingProfile.Stream stream = new StreamingProfile.Stream(json);
	// profile.setStream(stream);
	// profile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2);
	// profile.setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_544);
	// profile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT);
	// profile.setSendingBufferProfile(new
	// StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
	//
	// manager.setStreamingStateListener(mStreamingStateChangedListener);
	// manager.prepare(profile);
	// manager.setNativeLoggingEnabled(true);
	// return manager;
	// }

	public static StreamingProfile createStreamingProfile(JSONObject json) {
		StreamingProfile profile = new StreamingProfile();
		if (json!=null) {
			StreamingProfile.Stream stream = new StreamingProfile.Stream(json);
			profile.setStream(stream);
		}
		profile.setDnsManager(getMyDnsManager());
		profile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2);
		profile.setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_544);
		profile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT);
		profile.setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
		return profile;
	}

    private static DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }
    
	public static RTCMediaStreamingManager createRTCStreamingManager(Context context, StreamingStateChangedListener streamingStateChangedListener) {
		RTCMediaStreamingManager.init(context,0);
		RTCMediaStreamingManager mRTCStreamingManager = new RTCMediaStreamingManager(context, AVCodecType.HW_AUDIO_CODEC);
		mRTCStreamingManager.setDebugLoggingEnabled(false);
		RTCConferenceOptions options = new RTCConferenceOptions();
		options.setHWCodecEnabled(false);
		mRTCStreamingManager.setConferenceOptions(options);
		mRTCStreamingManager.setDebugLoggingEnabled(false);
	
		RTCVideoWindow remoteAnchorView = new RTCVideoWindow(new RTCSurfaceView(context));
		mRTCStreamingManager.addRemoteWindow(remoteAnchorView);
		if (streamingStateChangedListener != null)
			mRTCStreamingManager.setStreamingStateListener(streamingStateChangedListener);
		return mRTCStreamingManager;
	}

}
