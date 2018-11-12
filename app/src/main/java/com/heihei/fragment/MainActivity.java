package com.heihei.fragment;

import com.base.host.ActivityManager;
import com.base.host.BaseActivity;
import com.base.host.HostApplication;
import com.base.utils.LogWriter;
import com.heihei.fragment.link.OutLinkActivity;
import com.heihei.logic.present.BasePresent;
import com.igexin.sdk.PushManager;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends BaseActivity {

    public static Intent outIntent = null;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        HostApplication.getInstance().setMainActivity(this);
        ActivityManager.getInstance().finishAllActivitysBefore();
        BasePresent.requestInitUrls();
        String clientId = PushManager.getInstance().getClientid(getApplicationContext());
        LogWriter.d("client", "main:"+clientId);
    }

    protected String initFragmentClassName() {
        return MainFragment.class.getName();
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (outIntent != null) {

            if ("outLink".equalsIgnoreCase(outIntent.getStringExtra("share_type"))) {
                outIntent.setClass(this, OutLinkActivity.class);
            }
            startActivity(outIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HostApplication.getInstance().setMainActivity(null);
    }

}
