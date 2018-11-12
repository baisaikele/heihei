package com.heihei.fragment.live.widget;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.heihei.fragment.live.widget.GiftCellView.OnDismissListener;
import com.heihei.model.AudienceGift;
import com.wmlives.heihei.R;

/**
 * 三个礼物view
 * 
 * @author chengbo
 */
public class GiftThreeView extends LinearLayout implements OnDismissListener {

    public static final int TYPE_LIVE = 0;// 直播间礼物
    public static final int TYPE_CHAT = 1;// 对聊礼物

    private static final int TIME_INTEVEL = 1500;

    private long startTime = 0l;
    private boolean isRunning = false;

    LinkedList<AudienceGift> gifts = new LinkedList<>();

    private GiftCellView first;
    private GiftCellView second;
    private GiftCellView third;

    public GiftThreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        first = (GiftCellView) findViewById(R.id.cell_first);
        second = (GiftCellView) findViewById(R.id.cell_second);
        third = (GiftCellView) findViewById(R.id.cell_third);

        first.setVisibility(View.INVISIBLE);
        second.setVisibility(View.INVISIBLE);
        third.setVisibility(View.INVISIBLE);
        first.setOnDismissListener(this);
        second.setOnDismissListener(this);
        third.setOnDismissListener(this);
    }

    private int type = TYPE_LIVE;

    public void setType(int type) {
        this.type = type;
        first.setType(type);
        second.setType(type);
        third.setType(type);
        if (this.type == TYPE_CHAT)
        {
            first.setVisibility(View.GONE);
            third.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isRunning = true;
    }

    public void setAudienceGift(List<AudienceGift> gifts) {

    }

    // private SortByAmount mComapator = new SortByAmount();
    //
    // private void tick() {
    //
    // }

    public void pause() {
        isRunning = false;
    }

    public void resume() {
        isRunning = true;
        checkViewVisibity();
    }

    /**
     * 清除所有礼物
     */
    public void clear() {
        gifts.clear();
        first.setVisibility(View.INVISIBLE);
        first.setData(null);
        second.setVisibility(View.INVISIBLE);
        second.setData(null);
        third.setVisibility(View.INVISIBLE);
        third.setData(null);
    }

    /**
     * 增加礼物
     * 
     * @param mAudienceGift
     */
    public void addAudienceGift(AudienceGift mAudienceGift) {

        if (type == TYPE_CHAT)//如果是对聊，直接替换
        {
            second.setVisibility(View.VISIBLE);
            second.setData(mAudienceGift);
            return;
        }
        
        if (second.getVisibility() == View.VISIBLE && second.getData() != null
                && second.getData().equals(mAudienceGift)) {
            if (mAudienceGift.amount > second.getData().amount)// 
            {
                second.setData(mAudienceGift);
                return;
            }else {
            	return;
            }
        }
        
        if (type == TYPE_LIVE) {
            if (first.getVisibility() == View.VISIBLE && first.getData() != null
                    && first.getData().equals(mAudienceGift)) {
                if (mAudienceGift.amount > first.getData().amount)// 3秒以内算连送
                {
                    first.setData(mAudienceGift);
                    return;
                }else{
                	return;
                }
            }

            if (third.getVisibility() == View.VISIBLE && third.getData() != null
                    && third.getData().equals(mAudienceGift)) {
                if (mAudienceGift.amount > third.getData().amount)// 3秒以内算连送
                {
                    third.setData(mAudienceGift);
                    return;
                }else{
                	return;
                }
            }
        }

        int index = gifts.indexOf(mAudienceGift);
        if (index != -1)// 等待队列里已经有了
        {
            AudienceGift aGift = gifts.get(index);
            if (mAudienceGift.amount > aGift.amount) {
                aGift.amount = mAudienceGift.amount;
                return;
            }
        }
        mAudienceGift.startAmount = mAudienceGift.amount;
        gifts.add(mAudienceGift);
        checkViewVisibity();
    }

    private void checkViewVisibity() {
        if (second.getVisibility() != View.VISIBLE && gifts.size() > 0) {
            AudienceGift aGift = gifts.removeFirst();
            second.setData(aGift);
            second.setVisibility(View.VISIBLE);
        }

        if (type == TYPE_LIVE) {
            if (first.getVisibility() != View.VISIBLE && gifts.size() > 0) {
                AudienceGift aGift = gifts.removeFirst();
                first.setData(aGift);
                first.setVisibility(View.VISIBLE);
            }

            if (third.getVisibility() != View.VISIBLE && gifts.size() > 0) {
                AudienceGift aGift = gifts.removeFirst();
                third.setData(aGift);
                third.setVisibility(View.VISIBLE);
            }
        } else {
            first.setVisibility(View.GONE);
            third.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDismiss(GiftCellView view) {

        if (view.getId() == R.id.cell_first) {
            first.setVisibility(View.INVISIBLE);
        } else if (view.getId() == R.id.cell_second) {
            second.setVisibility(View.INVISIBLE);
        } else if (view.getId() == R.id.cell_third) {
            third.setVisibility(View.INVISIBLE);
        }
        if (isRunning && type == TYPE_LIVE) {
            checkViewVisibity();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        gifts.clear();
        isRunning = false;
    }

    public static class SortByAmount implements Comparator<AudienceGift> {

        @Override
        public int compare(AudienceGift lhs, AudienceGift rhs) {
            if (lhs.amount < rhs.amount) {
                return 1;
            } else if (lhs.amount == rhs.amount) {
                return 0;
            }
            return -1;
        }

    }

}
