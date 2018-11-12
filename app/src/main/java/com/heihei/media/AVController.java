//package com.heihei.media;
//
//import org.json.JSONObject;
//
//import android.content.Context;
//
//import com.base.host.HostApplication;
//import com.base.http.ErrorCode;
//import com.base.http.JSONResponse;
//import com.base.utils.LogUtil;
//import com.base.utils.UIUtils;
//import com.heihei.logic.present.PmPresent;
//import com.tencent.TIMCallBack;
//import com.tencent.TIMManager;
//import com.tencent.TIMUser;
//import com.tencent.av.sdk.AVAudioCtrl;
//import com.tencent.av.sdk.AVAudioCtrl.AudioFrameWithByteBuffer;
//import com.tencent.av.sdk.AVAudioCtrl.RegistAudioDataCompleteCallback;
//import com.tencent.av.sdk.AVAudioCtrl.RegistAudioDataCompleteCallbackWithByteBuffer;
//import com.tencent.av.sdk.AVCallback;
//import com.tencent.av.sdk.AVContext;
//import com.tencent.av.sdk.AVError;
//import com.tencent.av.sdk.AVRoomMulti;
//import com.tencent.av.sdk.AVContext.StartParam;
//
//public class AVController {
//
//    private static final int TYPE_MEMBER_CHANGE_IN = 1;// 进入房间事件。
//    private static final int TYPE_MEMBER_CHANGE_OUT = 2;// 退出房间事件。
//    private static final int TYPE_MEMBER_CHANGE_HAS_CAMERA_VIDEO = 3;// 有发摄像头视频事件。
//    private static final int TYPE_MEMBER_CHANGE_NO_CAMERA_VIDEO = 4;// 无发摄像头视频事件。
//    private static final int TYPE_MEMBER_CHANGE_HAS_AUDIO = 5;// 有发语音事件。
//    private static final int TYPE_MEMBER_CHANGE_NO_AUDIO = 6;// 无发语音事件。
//    private static final int TYPE_MEMBER_CHANGE_HAS_SCREEN_VIDEO = 7;// 有发屏幕视频事件。
//    private static final int TYPE_MEMBER_CHANGE_NO_SCREEN_VIDEO = 8;// 无发屏幕视频事件。
//
//    private volatile static AVController mInstance = null;
//
//    private Context context;
//
//    private AVContext mAVContext;
//
//    private ChatStatusChangedListener mChatStatusChangedListener;
//
//    private int appId = 0;
//    private String at3rdId = "";
//    private int identify = 0;
//    private String accountType = "";
//    private String userSign = "";
//
//    private int roomId = 0;
//
//    private AVController(Context context) {
//        this.context = context;
//    }
//
//    public static AVController createInstance() {
//        if (mInstance == null) {
//            synchronized (AVController.class) {
//                if (mInstance == null) {
//                    mInstance = new AVController(HostApplication.getInstance());
//                }
//            }
//        }
//        return mInstance;
//    }
//
//    public void setOnChatStatusChangedListener(ChatStatusChangedListener mChatStatusChangedListener) {
//        this.mChatStatusChangedListener = mChatStatusChangedListener;
//    }
//
//    public void reqeustChatUser() {
//
//        if (mAVContext != null) {
//            return;
//        }
//
//        PmPresent.getInstance().getChatUser(new JSONResponse() {
//
//            @Override
//            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
//                if (errCode == ErrorCode.ERROR_OK) {
//                    identify = json.optInt("identifier");
//                    appId = json.optInt("sdkAppId");
//                    accountType = json.optString("accountType");
//                    userSign = json.optString("userSig");
//                    at3rdId = json.optString("appid_at_3rd");
//                    login();
//                } else {
//                    LogUtil.d("chat", "get chat user error");
//                }
//
//            }
//        });
//    }
//
//    /**
//     * 登录
//     */
//    public void login() {
//
//        LogUtil.d("chat", "start login");
//        TIMManager.getInstance().init(context);
////        stopAVContext();
//        TIMUser user = new TIMUser();
//        user.setAccountType(accountType);
//        user.setAppIdAt3rd(at3rdId);
//        user.setIdentifier(String.valueOf(identify));
//        // 发起登录请求
//        TIMManager.getInstance().login(appId, user, userSign, new TIMCallBack() {
//
//            @Override
//            public void onSuccess() {
//                LogUtil.d("chat", "login success");
//                onLogin(true, 0);
//            }
//
//            @Override
//            public void onError(int arg0, String arg1) {
//                LogUtil.d("chat", "login error:" + arg0 + "--" + arg1);
//                onLogin(false, 0);
//
//            }
//        });
//    }
//
//    public AVContext getAVContext() {
//        return this.mAVContext;
//    }
//
//    /**
//     * 注销
//     */
//    public void logout() {
//        TIMManager.getInstance().logout();
//        onLogout();
//    }
//
//    public void enterRoom(long roomId) {
//        if (mAVContext != null) {
//            mAVContext.exitRoom();
//            this.mVoiceChangedListener = null;
//        }
//
//        if (this.roomId != 0) {
//            return;
//        }
//
//        if (mAVContext != null) {
//            this.roomId = (int) roomId;
//            AVRoomMulti.EnterParam.Builder enterRoomParam = new AVRoomMulti.EnterParam.Builder(this.roomId);
//
//            enterRoomParam.auth(AVRoomMulti.AUTH_BITS_DEFAULT, null).avControlRole("Host").autoCreateRoom(true)
//                    .isEnableMic(true).isEnableSpeaker(true);
//            enterRoomParam.isEnableHdAudio(false);
//            enterRoomParam.audioCategory(AVRoomMulti.AUDIO_CATEGORY_VOICECHAT);
//
//            if (mAVContext != null) {
//                // create room
//                int ret = mAVContext.enterRoom(mEventListener, enterRoomParam.build());
//                if (ret == AVError.AV_ERR_REPEATED_OPERATION) {
//
//                }
//                LogUtil.d("chat", "enter room:" + ret);
//            }
//        }
//    }
//
//    private long voiceDisposeTime = 0l;
//    private long netstreamTime = 0l;
//    public RegistAudioDataCompleteCallbackWithByteBuffer mSpeakerCallback = new RegistAudioDataCompleteCallbackWithByteBuffer() {
//
//        @Override
//        public int onComplete(AudioFrameWithByteBuffer audioBuffer, int srcType) {
//            // LogUtil.d("chat", "srcType:" + srcType);
//            long nowTime = System.currentTimeMillis();
//            if (srcType == AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE) {
//                if (nowTime - voiceDisposeTime > TIME_VOICE_INTEVAL) {
//                    byte[] buffer = audioBuffer.data.array();
//                    double volume = calculateVolume(buffer);
//                    // LogUtil.d("chat", "record:" + volume);
//                    if (mVoiceChangedListener != null) {
//                        voiceDisposeTime = nowTime;
//                        if (volume > 10) {
//                            mVoiceChangedListener.recordVoiceChanged(true);
//                        } else {
//                            mVoiceChangedListener.recordVoiceChanged(false);
//                        }
//                    }
//
//                }
//
//            } else if (srcType == AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_NETSTREM) {
//                if (nowTime - netstreamTime > TIME_VOICE_INTEVAL) {
//                    byte[] buffer = audioBuffer.data.array();
//                    double volume = calculateVolume(buffer);
//                    // LogUtil.d("chat", "record:" + volume);
//                    if (mVoiceChangedListener != null) {
//                        netstreamTime = nowTime;
//                        if (volume > 10) {
//                            mVoiceChangedListener.playVoiceChanged(true);
//                        } else {
//                            mVoiceChangedListener.playVoiceChanged(false);
//                        }
//                    }
//
//                }
//            }
//
//            return 0;
//        }
//
//    };
//
//    private double calculateVolume(byte[] buffer) {
//
//        double sumVolume = 0.0;
//
//        double avgVolume = 0.0;
//
//        double volume = 0.0;
//
//        for (int i = 0; i < buffer.length; i += 2) {
//
//            int v1 = buffer[i] & 0xFF;
//
//            if (i + 1 >= buffer.length) {
//                break;
//            }
//            int v2 = buffer[i + 1] & 0xFF;
//
//            int temp = v1 + (v2 << 8);// 小端
//
//            if (temp >= 0x8000) {
//
//                temp = 0xffff - temp;
//
//            }
//
//            sumVolume += Math.abs(temp);
//
//        }
//
//        avgVolume = sumVolume / buffer.length / 2;
//
//        volume = Math.log10(1 + avgVolume) * 10;
//
//        return volume;
//
//    }
//
//    /**
//     * 房间回调
//     */
//    private AVRoomMulti.EventListener mEventListener = new AVRoomMulti.EventListener() {
//
//        // 创建房间成功回调
//        public void onEnterRoomComplete(int result) {
//            if (result == 0) {
//                LogUtil.d("chat", "enter room success");
//                // SxbLog.standardEnterRoomLog(TAG, "enterAVRoom", "" + LogConstants.STATUS.SUCCEED, "room id" + MySelfInfo.getInstance().getMyRoomNum());
//                // 只有进入房间后才能初始化AvView
//                // QavsdkControl.getInstance().setAvRoomMulti(QavsdkControl.getInstance().getAVContext().getRoom());
//                // isInAVRoom = true;
//                initAudioService();
//
//                // mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                // AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIC, mSpeakerCallback);
//                // mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                // AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOPLAY, mSpeakerCallback);
//                // mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                // AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_MIXTOSEND, mSpeakerCallback);
//                // mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                // AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_PLAY, mSpeakerCallback);
//                mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                        AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_NETSTREM, mSpeakerCallback);
//                // mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                // AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_SEND, mSpeakerCallback);
//                mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                        AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE, mSpeakerCallback);
//                // mAVContext.getAudioCtrl().registAudioDataCallbackWithByteBuffer(
//                // AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_END, mSpeakerCallback);
//
//                if (mChatStatusChangedListener != null) {
//                    mChatStatusChangedListener.onEnterRoomSuccess();
//                }
//                // mStepInOutView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
//            } else {
//                LogUtil.d("chat", "enter room fail");
//                if (mChatStatusChangedListener != null) {
//                    mChatStatusChangedListener.onEnterRoomFail();
//                }
//                exitRoom();
//            }
//
//        }
//
//        @Override
//        public void onExitRoomComplete() {
//            roomId = 0;
//            LogUtil.d("chat", "exit room");
//
//            if (mChatStatusChangedListener != null) {
//                mChatStatusChangedListener.onExitRoomSuccess();
//            }
//
//            // isInAVRoom = false;
//            // quiteIMChatRoom();
//            // CurLiveInfo.setCurrentRequestCount(0);
//            uninitAudioService();
//            // 通知结束
//            // notifyServerLiveEnd();
//            // if (mStepInOutView != null)
//            // mStepInOutView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
//        }
//
//        @Override
//        public void onRoomDisconnect(int i) {
//
//            if (mChatStatusChangedListener != null) {
//                mChatStatusChangedListener.onRoomDisconnect();
//            }
//            // isInAVRoom = false;
//            // quiteIMChatRoom();
//            // CurLiveInfo.setCurrentRequestCount(0);
//            uninitAudioService();
//            // 通知结束
//            // notifyServerLiveEnd();
//            // if (mStepInOutView != null)
//            // mStepInOutView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
//        }
//
//        // 房间成员变化回调
//        public void onEndpointsUpdateInfo(int eventid, String[] updateList) {
//
//            switch (eventid) {
//            case TYPE_MEMBER_CHANGE_IN :
//                if (mChatStatusChangedListener != null) {
//                    mChatStatusChangedListener.onRoomMemberChangedIn(updateList);
//                }
//
//                break;
//            case TYPE_MEMBER_CHANGE_HAS_CAMERA_VIDEO :
//                break;
//            case TYPE_MEMBER_CHANGE_NO_CAMERA_VIDEO : {
//            }
//                break;
//            case TYPE_MEMBER_CHANGE_HAS_AUDIO :
//                LogUtil.d("chat", "has_audio");
//                break;
//
//            case TYPE_MEMBER_CHANGE_OUT :
//                LogUtil.d("chat", "member out");
//                if (mChatStatusChangedListener != null) {
//                    mChatStatusChangedListener.onRoomMemberChangedOut(updateList);
//                }
//                break;
//            default:
//                break;
//            }
//
//        }
//
//        @Override
//        public void onPrivilegeDiffNotify(int i) {
//
//        }
//
//        @Override
//        public void onSemiAutoRecvCameraVideo(String[] strings) {
//            // mStepInOutView.alreadyInLive(strings);
//        }
//
//        @Override
//        public void onCameraSettingNotify(int i, int i1, int i2) {
//
//        }
//
//        @Override
//        public void onRoomEvent(int i, int i1, Object o) {
//
//        }
//
//    };
//
//    public void exitRoom() {
//        if (mAVContext != null) {
//            
//            if (this.roomId != 0)
//            {
//                int ret = mAVContext.exitRoom();
//                LogUtil.d("chat", "exit room:" + ret);
//                uninitAudioService();
//                this.roomId = 0;
//            }
//            
//            if (mAVContext.getAudioCtrl() != null) {
//                mAVContext.getAudioCtrl().unregistAudioDataCallbackAll();
//            }
//        }
//        this.mVoiceChangedListener = null;
//    }
//
//    public void stopAVContext() {
//
//        this.mChatStatusChangedListener = null;
//
////        if (mAVContext != null) {
////            mAVContext.destroy();
////            mAVContext = null;
////        }
//    }
//
//    private void onLogin(boolean success, long tinyId) {
//        if (success) {
//            mAVContext = AVContext.createInstance(context, false);
//            if (mAVContext != null) {
//
//                StartParam mConfig = new AVContext.StartParam();
//                mConfig.sdkAppId = appId;
//                mConfig.accountType = accountType;
//                mConfig.appIdAt3rd = Integer.toString(appId);
//                mConfig.identifier = String.valueOf(identify);
//                // mUserSig = usersig;
//
//                int ret = mAVContext.start(mConfig, mStartContextCompleteCallback);
//                if (ret == AVError.AV_ERR_HAS_IN_THE_STATE) {
//                    
//                }
//                LogUtil.d("chat", "avContext create ret:" + ret);
//            } else {
//                LogUtil.d("chat", "avContext create fail");
//                UIUtils.showToast("AVContext create fail");
//            }
//        } else {
//            mStartContextCompleteCallback.onComplete(AVError.AV_ERR_FAILED, "");
//        }
//    }
//
//    private void onLogout() {
//        if (mAVContext != null) {
//            mAVContext.stop();
//            mAVContext.destroy();
//            mAVContext = null;
//        }
//    }
//
//    /**
//     * 启动AVSDK系统的回调接口
//     */
//    private AVCallback mStartContextCompleteCallback = new AVCallback() {
//
//        public void onComplete(int result, String s) {
//            if (result == AVError.AV_OK) {
//                LogUtil.d("chat", "start success");
//            }
//            if (result != AVError.AV_OK) {
//                LogUtil.d("chat", "start fail:" + result + "--s:" + s);
//                UIUtils.showToast("AVContext create fail");
//                mAVContext = null;
//            }
//        }
//    };
//
//    private void initAudioService() {
//
//        if (mAVContext != null) {
//            mAVContext.getAudioCtrl().startTRAEService();
//        }
//
//    }
//
//    private void uninitAudioService() {
//        if (mAVContext != null) {
//            mAVContext.getAudioCtrl().startTRAEService();
//        }
//    }
//
//    public static interface RoomRole {
//
//        public static final String HOST_ROLE = "Host";
//        public static final String VIDEO_MEMBER_ROLE = "VideoMember";
//        public static final String NORMAL_MEMBER_ROLE = "NormalMember";
//    }
//
//    private static final int TIME_VOICE_INTEVAL = 1000;// 每一秒回调一次
//
//    private VoiceChangedListener mVoiceChangedListener;
//
//    public void setVoiceChangedListener(VoiceChangedListener mVoiceChangedListener) {
//        this.mVoiceChangedListener = mVoiceChangedListener;
//    }
//
//    public static interface VoiceChangedListener {
//
//        public void recordVoiceChanged(boolean hasVoice);
//
//        public void playVoiceChanged(boolean hasVoice);
//    }
//
//}
