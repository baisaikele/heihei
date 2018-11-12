package com.base.http;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.base.utils.MD5;

public class HttpParam {

    private JSONObject json;

    public HttpParam() {
        json = new JSONObject();
    }

    public HttpParam(JSONObject json) {
        this.json = json;
        if (this.json == null) {
            this.json = new JSONObject();
        }
    }

    public HttpParam(String str) {
        try {
            json = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null) {
            json = new JSONObject();
        }
    }

    public HttpParam(Map copyMap) {
        try {
            json = new JSONObject(copyMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (json == null) {
            json = new JSONObject();
        }
    }

    public HttpParam put(String name, boolean value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
        }
        return this;
    }

    public HttpParam put(String name, int value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
        }
        return this;
    }

    public HttpParam put(String name, double value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
        }
        return this;
    }

    public HttpParam put(String name, long value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
        }
        return this;
    }

    public HttpParam put(String name, Object value) {
        try {
            json.put(name, value);
        } catch (Exception e) {
        }
        return this;
    }

    public HttpParam putOpt(String name, Object value) {
        try {
            json.putOpt(name, value);
        } catch (Exception e) {
        }
        return this;
    }

    public String toPostString() {
        return this.json.toString();
    }

    public String toFormString()
    {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = this.json.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = this.json.optString(key);
            sb = sb.append(key + "=" + value);
            if (iterator.hasNext()) {
                sb = sb.append("&");
            }
        }
        return sb.toString();
    }
    
    public String toGetString() {
        StringBuilder sb = new StringBuilder();
        Iterator iterator = this.json.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String value = this.json.optString(key);
            sb = sb.append(key + "=" + value);
            if (iterator.hasNext()) {
                sb = sb.append("&");
            }
        }
        return sb.toString();
    }

    /**
     * 生成唯一key
     * 
     * @param url
     * @return
     */
    public String createMD5Key(String url) {
        String key = "";
        if (this.json != null) {
            json.remove("lat");
            json.remove("lng");
            json.remove("latlng");
            if (json.has("client_info")) {
                JSONObject clientJson = json.optJSONObject("client_info");
                clientJson.remove("network");
                clientJson.remove("ip");
                clientJson.remove("ssid");
            }
            key = MD5.getMD5(url + json.toString());
        } else {
            key = MD5.getMD5(url);
        }
        return key;
    }

}
