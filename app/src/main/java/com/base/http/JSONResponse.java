package com.base.http;

import org.json.JSONObject;

public interface JSONResponse {

    public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached);
}
