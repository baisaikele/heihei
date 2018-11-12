package com.base.pay;

import java.lang.ref.WeakReference;
import java.util.Map;

import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.base.utils.LogWriter;
import com.base.utils.StringUtils;
import com.heihei.logic.event.EventListener;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class PayController {

    public static final String WX_APP_ID = "wx938414637b332c45";// 微信appid

    private WeakReference<OnPayListener> mPayWr;

    public void payByWechat(final Activity activity,JSONObject json,OnPayListener mOnPayListener)
    {
        this.mPayWr = new WeakReference<PayController.OnPayListener>(mOnPayListener);
        IWXAPI api = WXAPIFactory.createWXAPI(activity, null);
        api.registerApp(WX_APP_ID);

        EventManager.ins().registListener(EventTag.WX_PAY, mEventListener);

         PayReq req = new PayReq();
         req.appId = json.optString("appid");
         req.partnerId = json.optString("partnerid");
         req.prepayId = json.optString("prepayid");
         req.nonceStr = json.optString("noncestr");
         req.timeStamp = json.optString("timestamp");
         req.packageValue = json.optString("package");
         req.sign = json.optString("sign");
         api.sendReq(req);
    }
    
    public void payByAli(final Activity activity,String data,OnPayListener mOnPayListener)
    {
        EventManager.ins().registListener(EventTag.ALI_PAY, mEventListener);
        
        final String orderInfo = data;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                LogWriter.d("msp", result.toString());

                Message msg = new Message();
                msg.what = ALI_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }
    
//    /**
//     * 支付
//     * 
//     * @param activity
//     * @param payMode
//     *            支付方式
//     */
//    public void pay(final Activity activity, int payMode, OnPayListener mOnPayListener) {
//        this.mPayWr = new WeakReference<PayController.OnPayListener>(mOnPayListener);
//        if (PayMode.WECHAT == payMode) {
//            IWXAPI api = WXAPIFactory.createWXAPI(activity, null);
//            api.registerApp(WX_APP_ID);
//
//            EventManager.ins().registListener(EventTag.WX_PAY, mEventListener);
//
//            // PayReq req = new PayReq();
//            // // req.appId = "wxf8b4f85f3a794e77"; // 测试用appId
//            // req.appId = payBean.appid;
//            // req.partnerId = payBean.partnerid;
//            // req.prepayId = payBean.prepayid;
//            // req.nonceStr = payBean.noncestr;
//            // req.timeStamp = payBean.timestamp;
//            // req.packageValue = payBean.packageName;
//            // req.sign = payBean.sign;
//            // api.sendReq(req);
//
//        } else if (PayMode.ALI == payMode) {
//            
//            EventManager.ins().registListener(EventTag.ALI_PAY, mEventListener);
//            
//            final String orderInfo = "";
//
//            Runnable payRunnable = new Runnable() {
//
//                @Override
//                public void run() {
//                    PayTask alipay = new PayTask(activity);
//                    Map<String, String> result = alipay.payV2(orderInfo, true);
//                    LogUtil.d("msp", result.toString());
//
//                    Message msg = new Message();
//                    msg.what = ALI_PAY_FLAG;
//                    msg.obj = result;
//                    mHandler.sendMessage(msg);
//                }
//            };
//
//            Thread payThread = new Thread(payRunnable);
//            payThread.start();
//        } else {
//            throw new RuntimeException("no such pay mode");
//        }
//    }

    private EventListener mEventListener = new EventListener() {

        @Override
        public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
            EventManager.ins().reomoveWhat(EventTag.WX_PAY);
            if (what == EventTag.WX_PAY) {
                if (arg1 == PayResult.SUCCESS) {
                    if (mPayWr != null && mPayWr.get() != null) {
                        mPayWr.get().onPaySuccess("");
                    }
                } else if (arg1 == PayResult.FAILED) {
                    if (mPayWr != null && mPayWr.get() != null) {
                        mPayWr.get().onPayError("");
                    }
                } else {
                    if (mPayWr != null && mPayWr.get() != null) {
                        mPayWr.get().onPayCancel();
                    }
                }
            } else if (what == EventTag.ALI_PAY) {
                if (arg1 == PayResult.SUCCESS) {
                    if (mPayWr != null && mPayWr.get() != null) {
                        mPayWr.get().onPaySuccess(dataobj.toString());
                    }
                } else if (arg1 == PayResult.FAILED) {
                    if (mPayWr != null && mPayWr.get() != null) {
                        mPayWr.get().onPayError(dataobj.toString());
                    }
                } else {
                    if (mPayWr != null && mPayWr.get() != null) {
                        mPayWr.get().onPayCancel();
                    }
                }
            }

        }
    };

    private static final int ALI_PAY_FLAG = 0;
    private static final int WECHAT_PAY_FLAG = 1;

    private static Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            Map<String, String> result = (Map<String, String>) msg.obj;
            String code = result.get("resultStatus");
            String res = result.get("result");
            if (StringUtils.isEmpty(res)) {
                res = "";
            }
            if ("9000".equals(code))// 支付成功
            {
                EventManager.ins().sendEvent(EventTag.ALI_PAY, PayResult.SUCCESS, 0, res);
            } else if ("6001".equals(code))// 支付取消
            {
                EventManager.ins().sendEvent(EventTag.ALI_PAY, PayResult.CANCEL, 0, res);
            } else// 支付失败
            {
                EventManager.ins().sendEvent(EventTag.ALI_PAY, PayResult.FAILED, 0, res);
            }
        };
    };

    public OnPayListener mOnPayListener;

    public void setOnPayListener(OnPayListener mOnPayListener) {
        this.mOnPayListener = mOnPayListener;
    }

    public static interface OnPayListener {

        public void onPaySuccess(String data);// 支付成功

        public void onPayError(String data);// 支付失败

        public void onPayCancel();// 支付取消了
    }

    public static interface PayResult {

        public int SUCCESS = 0;// 成功
        public int FAILED = 1;// 失败
        public int CANCEL = 2;// 取消
    }

    public static interface PayMode {

        public int WECHAT = 0;// 微信支付
        public int ALI = 1;// 支付宝支付
    }
}
