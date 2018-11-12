package com.heihei.model.pay;

import org.json.JSONObject;

/**
 * 充值列表bean
 * 
 * @author chengbo
 */
public class RechargeInfo {

    public static final String WECHAT = "wechat";
    public static final String ALIPAY = "alipay";
    
    public String id = "0";
    public String channel = "";// 支付渠道
    public String productId = "";// 苹果支付的商品id
    public String desc = "";// 描述
    public int gold = 0;// 钻石数
    public int payMoney = 0;// 价格，分
    public int originMoney = 0;// 原价，分

    public RechargeInfo(JSONObject json) {
        parseJSON(json);
    }

    private void parseJSON(JSONObject json) {
        if (json == null) {
            return;
        }
        this.id = json.optString("id");
        this.channel = json.optString("channel");
        this.productId = json.optString("productId");
        this.desc = json.optString("desc");
        this.gold = json.optInt("gold");
        this.payMoney = json.optInt("payMoney");
        this.originMoney = json.optInt("originMoney");
    }

}
