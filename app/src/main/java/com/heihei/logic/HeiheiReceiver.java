package com.heihei.logic;


import com.base.host.HostApplication;
import com.base.utils.NetUtil;
import com.heihei.model.msg.due.DueMessageUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 网络状态改变广播监听
 * @author admin
 *
 */
public class HeiheiReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (UserMgr.getInstance().isLogined() && NetUtil.isHasNetwork(HostApplication.getInstance())) {
            	//网络链接成功
            	DueMessageUtils.getInstance().networkStatusOK();
            }else if(UserMgr.getInstance().isLogined() && !NetUtil.isHasNetwork(HostApplication.getInstance())){
            	//网络链接断开
            	DueMessageUtils.getInstance().networkStatusOFF();
            }
         }        
    }

}
