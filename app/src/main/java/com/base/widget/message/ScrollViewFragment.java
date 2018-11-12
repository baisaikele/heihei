package com.base.widget.message;

import com.base.danmaku.DanmakuItem;
import com.base.host.BaseFragment;
import com.base.widget.LiveFreeScrollView;
import com.heihei.fragment.live.widget.LiveBottom;
import com.heihei.model.LiveInfo;
import com.heihei.model.msg.bean.AbstractTextMessage;
import com.heihei.model.msg.bean.BulletMessage;
import com.heihei.model.msg.bean.LiveMessage;
import com.heihei.model.msg.bean.SystemMessage;
import com.heihei.model.msg.bean.TextMessage;
import com.wmlives.heihei.R;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ScrollViewFragment extends BaseFragment implements OnClickListener {
	private LiveFreeTextViewHolder mLiveFreeTextViewHolder;
	private LiveFreeScrollView mScrollView;
	private LinearLayout mChatContainer;
	private View fl_scroll_contentview;
	private LiveBottom mLiveBottom;
	private LiveInfo mLiveInfo;
	private ImageView iv_live_lefttip;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scrollview:
			if (mLiveBottom != null)
				mLiveBottom.hideKeyboard();
			break;
		default:
			break;
		}
	}

	public void scrollToBottom() {
		if (mLiveFreeTextViewHolder != null)
			mLiveFreeTextViewHolder.scrollToBottom();
	}

	public void setLiveBottom(LiveBottom bottom, LiveInfo info) {
		this.mLiveBottom = bottom;
		this.mLiveInfo = info;
	}

	public void updateBulletMessage(BulletMessage msg) {
		if (mLiveFreeTextViewHolder != null)
			mLiveFreeTextViewHolder.updateBulletMessage(msg);
	}

	public void updateSystemMessage(SystemMessage msg) {
		if (mLiveFreeTextViewHolder != null)
			mLiveFreeTextViewHolder.updateSystemMessage(msg);
	}

	public void updateLiveMessage(LiveMessage msg) {
		if (mLiveFreeTextViewHolder != null)
			mLiveFreeTextViewHolder.updateLiveMessage(msg);
	}

	public void updateAbstractTextMessage(AbstractTextMessage msg) {
		if (mLiveFreeTextViewHolder != null)
			mLiveFreeTextViewHolder.updateAbstractTextMessage(msg);
	}

	public void updateSelfMessage(DanmakuItem item) {
		switch (item.type) {
		case DanmakuItem.TYPE_NORMAL:
			TextMessage msgNormal = new TextMessage(item.userId, item.userName, item.text, item.gender, item.giftId);
			updateAbstractTextMessage(msgNormal);
			break;
		case DanmakuItem.TYPE_COLOR_BG:
			TextMessage msgColor = new TextMessage(item.userId, item.userName, item.text, item.gender, item.giftId);
			updateAbstractTextMessage(msgColor);
			break;
		case DanmakuItem.TYPE_LIKE:
			LiveMessage like = new LiveMessage(item.userId, item.userName, item.gender, "", item.type, item.text);
			updateLiveMessage(like);
			break;
		default:
			break;
		}
	}

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_information);
	}

	private boolean isShow;

	public void showhttip(boolean isShow) {
		this.isShow = isShow;
	}

	public void hintTtipView() {
		if (iv_live_lefttip != null)
			iv_live_lefttip.setVisibility(View.GONE);
	}

	@Override
	protected void viewDidLoad() {
		fl_scroll_contentview = findViewById(R.id.fl_scroll_contentview);
		fl_scroll_contentview.setVisibility(View.VISIBLE);
		iv_live_lefttip = (ImageView) findViewById(R.id.iv_live_lefttip);
		mScrollView = (LiveFreeScrollView) findViewById(R.id.scrollview);
		mScrollView.setVisibility(View.VISIBLE);
		mScrollView.setOnClickListener(this);
		mChatContainer = (LinearLayout) findViewById(R.id.chat_container);
	}

	@Override
	protected void refresh() {
		mLiveFreeTextViewHolder = new LiveFreeTextViewHolder(getActivity(), mScrollView, mChatContainer);
		mLiveFreeTextViewHolder.setLiveBottom(mLiveInfo, mLiveBottom);

		if (isShow && iv_live_lefttip != null)
			iv_live_lefttip.setVisibility(View.VISIBLE);
		else if (iv_live_lefttip != null)
			iv_live_lefttip.setVisibility(View.GONE);
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "ScrollViewFragment";
	}

}
