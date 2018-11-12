package com.heihei.model;

import org.json.JSONObject;

public class ExchangeInfo {
    
    public String id = "";
    public String desc = "";
    public int point = 0;// 黑票数
    public int gold = 0;// 钻石数

    public ExchangeInfo(JSONObject json) {
        parseJson(json);
    }

    public void parseJson(JSONObject json) {
        if (json == null) {
            return;
        }
        
        this.id = json.optString("id");
        this.desc = json.optString("desc");
        this.point = json.optInt("point");
        this.gold = json.optInt("gold");
        
    }
}
