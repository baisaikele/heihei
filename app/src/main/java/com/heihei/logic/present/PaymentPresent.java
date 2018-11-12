package com.heihei.logic.present;

import com.base.http.HttpParam;
import com.base.http.HttpUtil;
import com.base.http.JSONResponse;
import com.heihei.logic.UserMgr;


public class PaymentPresent extends BasePresent{
    
    /**
     * 获取微信充值列表页
     * @param response
     */
    public void getWechatRechargeList(JSONResponse response)
    {
        String url = urls.get(RECHARGE_LIST_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("channel", "wechat");
        HttpUtil.getAsync(url, hp, response);
    }
    
    /**
     * 获取支付宝充值列表页
     * @param response
     */
    public void getAliRechargeList(JSONResponse response)
    {
        String url = urls.get(RECHARGE_LIST_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("channel", "alipay");
        HttpUtil.getAsync(url, hp, response);
    }
    /**
     * 创建微信支付订单
     * @param packageId 商品号
     * @param response
     */
    public void createWechatOrder(String packageId,JSONResponse response)
    {
        String url = urls.get(CREATE_ORDER_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("packageId", packageId);
        hp.put("channel", "wechat");
        HttpUtil.postAsync(url, hp, response);
    }
    
    
    /**
     * 创建支付宝支付订单
     * @param packageId 商品号
     * @param response
     */
    public void createAliOrder(String packageId,JSONResponse response)
    {
        String url = urls.get(CREATE_ORDER_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("packageId", packageId);
        hp.put("channel", "alipay");
        HttpUtil.postAsync(url, hp, response);
    }
    
    /**
     * 订单通知
     * @param order 订单号
     * @param status 状态
     * @param data sdk返回的data数据
     */
    public void orderNotify(String order,int status,String data)
    {
        String url = urls.get(ORDER_NOTICE_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("order", order);
        hp.put("status", status);
        hp.put("data", data);
        HttpUtil.postAsync(url, hp, null);
    }
    
    /**
     * 我的账户资产
     * @param response
     */
    public void getMyAccountMoney(JSONResponse response)
    {
        String url = urls.get(ACCOUNT_MONEY_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        HttpUtil.getAsync(url, hp, response);
    }
    
    /**
     * 获取可提现金额
     * @param response
     */
    public void getWithdrawMoney(JSONResponse response)
    {
        String url = urls.get(ACCOUNT_WITHDRAW_MONEY_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        HttpUtil.getAsync(url, hp, response);
    }
    
    /**
     * 获取我的支付宝信息
     * @param response
     */
    public void getAlipayInfo(JSONResponse response)
    {
        String url = urls.get(GET_ALI_INFO_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        HttpUtil.getAsync(url, hp, response);
    }
    
    
    /**
     * 验证支付宝信息
     * @param response
     */
    public void verifyAlipayInfo(String alipayCode,String username,String idCard,String phone,JSONResponse response)
    {
        String url = urls.get(VERIFY_ALIPAY_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("alipaycode", alipayCode);
        hp.put("username", username);
        hp.put("idcard", idCard);
        hp.put("phone", phone);
        HttpUtil.postAsync(url, hp, response);
    }
    /**
     * 提现
     * @param alipaycode 支付宝账号
     * @param money 金额 分
     * @param response
     */
    public void withdraw(String alipaycode,int money,JSONResponse response)
    {
        String url = urls.get(WITHDRAW_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("alipaycode", alipaycode);
        hp.put("payamount", money);
        HttpUtil.postAsync(url, hp, response);
    }
    
    /**
     * 积分兑换钻石的列表
     * @param response
     */
    public void exchangeList(JSONResponse response)
    {
        String url = urls.get(EXCHANGE_DIAMOND_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        HttpUtil.getAsync(url, hp, response);
    }
    
    /**
     * 积分兑换钻石
     * @param packageid
     * @param response
     */
    public void exchange(String packageid,JSONResponse response)
    {
        String url = urls.get(EXCHANGE_ACTION_KEY);
        HttpParam hp = new HttpParam();
        hp.put("token", UserMgr.getInstance().getToken());
        hp.put("packageId", packageid);
        HttpUtil.postAsync(url, hp, response);
    }
    
}
