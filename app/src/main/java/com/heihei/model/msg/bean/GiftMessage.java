package com.heihei.model.msg.bean;

public final class GiftMessage extends AbstractUserMessage {

    public GiftMessage(String userId, String userName, int gender, String roomId, int giftId, int amount,
            String liveId,int totalTicket,String gift_uuid) {
        super(userId, userName, MESSAGE_TYPE_GIFT, gender);
        this.giftId = giftId;
        this.amount = amount;
        this.roomId = roomId;
        this.liveId = liveId;
        this.totalTicket = totalTicket;
        this.gift_uuid = gift_uuid;
    }

    public final String roomId;
    public final int giftId;
    public final int amount;
    public final String liveId;
    public final int totalTicket;
    
    public final String gift_uuid;
    
}
