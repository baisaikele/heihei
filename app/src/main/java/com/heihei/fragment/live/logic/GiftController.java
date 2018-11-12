package com.heihei.fragment.live.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.heihei.logic.present.LivePresent;
import com.heihei.model.Gift;

public class GiftController {

    private static GiftController mIns;

    private GiftController() {}

    private HashMap<Integer, Gift> mGifts = new HashMap<>();

    public static GiftController getInstance() {
        if (mIns == null) {
            synchronized (GiftController.class) {
                if (mIns == null) {
                    mIns = new GiftController();
                }
            }
        }
        return mIns;
    }

    /**
     * 通过礼物id获取礼物信息
     * 
     * @param id
     */
    public Gift getGiftById(int id) {
        return mGifts.get(id);
    }

    private LivePresent mLivePresent = new LivePresent();

    
    /**
     * 请求礼物列表
     */
    public void requestGiftList(final OnGiftListGetListener mListener) {
        mLivePresent.getGiftList(new JSONResponse() {

            @Override
            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                if (errCode == ErrorCode.ERROR_OK) {
                    mGifts.clear();
                    List<Gift> gifts = new ArrayList<>();
                    JSONArray giftArr = json.optJSONArray("gifts");
                    if (giftArr != null && giftArr.length() > 0) {
                        for (int i = 0; i < giftArr.length(); i++) {
                            Gift gift = new Gift(giftArr.optJSONObject(i));
                            gifts.add(gift);
                            mGifts.put(gift.id, gift);
                        }

                    }

                    if (mListener != null) {
                        mListener.onGiftGet(gifts);
                    }

                }

            }
        }, true);
    }

    public static interface OnGiftListGetListener {

        public void onGiftGet(List<Gift> mGifts);
    }

}
