package com.wmlives.heihei.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.base.host.RefInvoke;
import com.base.pay.PayController;
import com.base.pay.PayController.PayResult;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTextView = new TextView(getApplicationContext());
        setContentView(mTextView);
        api = WXAPIFactory.createWXAPI(this, PayController.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {}

    @Override
    public void onResp(BaseResp resp) {

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            if (resp.errCode == 0) {
                Object obj = RefInvoke.getFieldOjbect(resp.getClass().getName(), resp, "prepayId");
                EventManager.ins().sendEvent(EventTag.WX_PAY, PayResult.SUCCESS, 0, obj);
            } else if (resp.errCode == 1) {
                EventManager.ins().sendEvent(EventTag.WX_PAY, PayResult.FAILED, 0, null);
            } else {
                EventManager.ins().sendEvent(EventTag.WX_PAY, PayResult.CANCEL, 0, null);
            }
        }
        finish();
    }
}