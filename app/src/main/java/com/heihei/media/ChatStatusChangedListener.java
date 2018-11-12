package com.heihei.media;

public interface ChatStatusChangedListener {

    public void onEnterRoomSuccess();//进房成功

    public void onEnterRoomFail();//进房失败

    public void onExitRoomSuccess();//退出房间
    
    public void onRoomDisconnect();//房间断开连接
    
    public void onRoomMemberChangedIn(String[] updateList);//有新成员进入
    
    public void onRoomMemberChangedOut(String[] updateList);//有成员退出
    
}
