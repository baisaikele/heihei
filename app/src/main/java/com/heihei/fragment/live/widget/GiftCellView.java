package com.heihei.fragment.live.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.fresco.FrescoImageHelper;
import com.heihei.fragment.live.logic.GiftController;
import com.heihei.model.AudienceGift;
import com.heihei.model.Gift;
import com.heihei.model.User;
import com.wmlives.heihei.R;

/**
 * 小礼物管理
 * @author admin
 *
 */
public class GiftCellView extends RelativeLayout {

    public GiftCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // ----------------R.layout.cell_audience_gift-------------Start
    private TextView tv_nickname;
    private com.facebook.fresco.FrescoImageView iv_gift_icon;
    private TextView tv_gift_name;
    private com.heihei.fragment.live.widget.GiftNumberView giftNumberView;
    
   private final int DEFAULT_DISMISS_LAUNCH = 3500;//隐藏时间
   private final int DEFAULT_REFRESH_AMOUNT_LAUNCH=100;//刷新次数累计 

    public void autoLoad_cell_audience_gift() {
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        iv_gift_icon = (com.facebook.fresco.FrescoImageView) findViewById(R.id.iv_gift_icon);
        tv_gift_name = (TextView) findViewById(R.id.tv_gift_name);
        giftNumberView = (com.heihei.fragment.live.widget.GiftNumberView) findViewById(R.id.giftNumberView);
    }

    // ----------------R.layout.cell_audience_gift-------------End

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoLoad_cell_audience_gift();
    }

    private AudienceGift mAudienceGift;

    private int currentNum = 0;

    public AudienceGift getData() {
        return this.mAudienceGift;
    }

    private int type = GiftThreeView.TYPE_LIVE;

    public void setType(int type) {
        this.type = type;
    }

    public void setData(AudienceGift audienceGift) {

        if (this.type == GiftThreeView.TYPE_CHAT) {// 如果是约聊，直接显示
            mHandler.removeMessages(FLAG_DISMISS);
            if (audienceGift == null) {
                this.currentNum = 0;
                return;
            }

            Gift gift = GiftController.getInstance().getGiftById(audienceGift.gift.id);
            if (gift != null) {
                tv_gift_name.setText(audienceGift.gift.name);
                FrescoImageHelper.getAvatar(audienceGift.gift.image, iv_gift_icon);
            }

            if (audienceGift.fromUser.gender == User.FEMALE) {
                tv_nickname.setTextColor(getResources().getColor(R.color.hh_color_female));
            } else {
                tv_nickname.setTextColor(getResources().getColor(R.color.hh_color_male));
            }

            tv_nickname.setText(audienceGift.fromUser.nickname);
            this.currentNum = audienceGift.amount;
            giftNumberView.setNumber(this.currentNum);

            Message msg = Message.obtain();
            msg.obj = this;
            msg.what = FLAG_DISMISS;

            mHandler.sendMessageDelayed(msg, DEFAULT_DISMISS_LAUNCH);

            return;
        }

        if (audienceGift == null) {
            this.mAudienceGift = null;
            this.currentNum = 0;
            return;
        } else {
            mHandler.removeMessages(FLAG_DISMISS);

            Message msg = Message.obtain();
            msg.obj = this;
            msg.what = FLAG_DISMISS;

            mHandler.sendMessageDelayed(msg, DEFAULT_DISMISS_LAUNCH);
        }

        if (this.mAudienceGift == null) {// 表示新的数据，不是连送
            this.mAudienceGift = audienceGift;

            Gift gift = GiftController.getInstance().getGiftById(audienceGift.gift.id);
            if (gift != null) {
                tv_gift_name.setText(audienceGift.gift.name);
                FrescoImageHelper.getAvatar(audienceGift.gift.image, iv_gift_icon);
            }

            if (audienceGift.fromUser.gender == User.FEMALE) {
                tv_nickname.setTextColor(getResources().getColor(R.color.hh_color_female));
            } else {
                tv_nickname.setTextColor(getResources().getColor(R.color.hh_color_male));
            }

            tv_nickname.setText(audienceGift.fromUser.nickname);
            this.currentNum = audienceGift.startAmount;
            if (audienceGift.amount - currentNum >= 5) {// 如果跨度太大了
                this.currentNum = audienceGift.amount - 5;
            }

            giftNumberView.setNumber(this.currentNum);

            if (!mHandler.hasMessages(FLAG_REFRESH_AMOUNT) && audienceGift.amount > this.currentNum)// 如果没有正在递增的队列
            {
                Message msg = Message.obtain();
                msg.obj = this;
                msg.what = FLAG_REFRESH_AMOUNT;
                mHandler.sendMessageDelayed(msg, DEFAULT_REFRESH_AMOUNT_LAUNCH);
            }
            return;
        } else {// 不是空表示是连送的数据
            this.mAudienceGift = audienceGift;
            if (!mHandler.hasMessages(FLAG_REFRESH_AMOUNT))// 如果没有正在递增的队列
            {
                Message msg = Message.obtain();
                msg.obj = this;
                msg.what = FLAG_REFRESH_AMOUNT;
                mHandler.sendMessageDelayed(msg, DEFAULT_REFRESH_AMOUNT_LAUNCH);
            }
        }

    }

    private void refreshAmount() {
        mHandler.removeMessages(FLAG_DISMISS);
        mHandler.removeMessages(FLAG_REFRESH_AMOUNT);

        if (this.mAudienceGift != null) {

            if (this.mAudienceGift.amount - this.currentNum <= 5) {
                this.currentNum += 1;
            } else {
                this.currentNum = this.mAudienceGift.amount - 5;
            }
            giftNumberView.setNumber(this.currentNum);
            if (this.currentNum < this.mAudienceGift.amount) {// 还没有涨到最大连送数
                Message msg = Message.obtain();
                msg.obj = this;
                msg.what = FLAG_REFRESH_AMOUNT;
                mHandler.sendMessageDelayed(msg, DEFAULT_REFRESH_AMOUNT_LAUNCH);// 一秒刷新一次连送数
            } else {
                Message msg = Message.obtain();
                msg.obj = this;
                msg.what = FLAG_DISMISS;
                mHandler.sendMessageDelayed(msg, DEFAULT_DISMISS_LAUNCH);
            }
        }

    }

    private void dismiss() {
        this.mAudienceGift = null;
        this.currentNum = 0;
        mHandler.removeMessages(FLAG_DISMISS);
        mHandler.removeMessages(FLAG_REFRESH_AMOUNT);
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss(GiftCellView.this);
        }
    }

    private static final int FLAG_DISMISS = 0;
    private static final int FLAG_REFRESH_AMOUNT = 1;

    private MyHandler mHandler = new MyHandler();

    private static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            GiftCellView view = (GiftCellView) msg.obj;
            switch (msg.what) {
            case FLAG_DISMISS :
                if (view != null) {
                    view.dismiss();
                }
                break;
            case FLAG_REFRESH_AMOUNT :
                if (view != null) {
                    view.refreshAmount();
                }
                break;
            }

        }
    }

    public OnDismissListener mOnDismissListener;

    public void setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }

    public static interface OnDismissListener {

        public void onDismiss(GiftCellView view);
    }

}
