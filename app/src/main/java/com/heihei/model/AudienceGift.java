package com.heihei.model;

import org.json.JSONObject;

import com.heihei.model.msg.bean.GiftMessage;

public class AudienceGift {

    public Gift gift = new Gift();// 礼物id
    public User fromUser = new User();
    public int amount = 1;
    public int startAmount = 1;
    public long time = 0l;
    
    public String gift_uuid = "";

    public AudienceGift() {}

    public AudienceGift(JSONObject json) {

    }

    /**
     * 从giftMessage创建
     * 
     * @param msg
     * @return
     */
    public static AudienceGift createFromGiftMessage(GiftMessage msg) {
        AudienceGift aGift = new AudienceGift(); 
        aGift.gift.id = msg.giftId;
        aGift.amount = msg.amount;

        aGift.fromUser.nickname = msg.fromUserName;
        // aGift.fromUser.parseGender(msg.)//解析性别
        return aGift;
    }

    @Override
    public boolean equals(Object o) {
        // return super.equals(o);
        if (o == null) {
            return false;
        }

        if (!(o instanceof AudienceGift)) {
            return false;
        }

        AudienceGift ag = (AudienceGift) o;
        if (ag.fromUser != null && this.fromUser != null && ag.fromUser.uid.equals(this.fromUser.uid) && ag.gift != null && this.gift != null && ag.gift.id==this.gift.id
                && this.gift_uuid.equals(ag.gift_uuid)) {
            return true;
        }
        return super.equals(o);

    }

}
