package com.heihei.logic;

import android.app.Dialog;
import android.support.v4.app.Fragment;

import com.base.host.ActivityManager;
import com.base.host.HostApplication;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.TipDialog;
import com.heihei.logic.event.EventManager;
import com.heihei.logic.event.EventTag;

/**
 * 状态管理器
 * 
 * @author chengbo
 */
public class StatusController {

	public static final int STATUS_IDLE = 0;// 啥也没干---可能在打飞机吧
	public static final int STATUS_LIVE = 1;// 正在直播
	public static final int STATUS_AUDIENCE = 2;// 正在观看直播
	public static final int STATUS_CHAT = 3;// 正在约聊
	public static final int STATUS_REPLAY = 4;// 正在看回放

	private int status = STATUS_IDLE;// 默认状态是啥也没干

	private volatile static StatusController mIns = null;

	private StatusController() {

	}

	public static StatusController getInstance() {
		if (mIns == null) {
			synchronized (StatusController.class) {
				if (mIns == null) {
					mIns = new StatusController();
				}
			}
		}
		return mIns;
	}

	/**
	 * 设置当前状态
	 * 
	 * @param status
	 */
	public void setCurrentStatus(int status) {
		this.status = status;
	}

	public static boolean isLive = false;

	public static void setLiveIng(boolean s) {
		isLive = s;
	}

	public static boolean getLiveIng() {
		return isLive;
	}

	/**
	 * 获取当前状态
	 * 
	 * @return
	 */
	public int getCurrentStatus() {
		return this.status;
	}

	/**
	 * 重置当前状态
	 */
	public void resetStatus() {
		this.status = STATUS_IDLE;
	}

	/**
	 * 显示结束提示
	 */
	public void showCompleteTip(final OnCompleteListener mOnCompleteListener) {
		if (HostApplication.getInstance().getMainActivity() != null) {
			Fragment topFragment = ActivityManager.getInstance().peek();
			if (topFragment != null && topFragment.getActivity() != null) {
				TipDialog td = new TipDialog(topFragment.getActivity());
				td.setContent(createTipText());
				td.setCanceledOnTouchOutside(false);
				td.setCancelable(false);
				td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

					@Override
					public void onOkClick(Dialog dialog) {
						handleOkClick();
						if (mOnCompleteListener != null) {
							mOnCompleteListener.onStopClick();
						}
					}

					@Override
					public void onCancleClick(Dialog dialog) {
						if (mOnCompleteListener != null) {
							mOnCompleteListener.onCancelClick();
						}
					}
				});
				td.show();
			}
		}
	}

	private void handleOkClick() {
		switch (this.status) {
		case STATUS_LIVE:
			EventManager.ins().sendEvent(EventTag.STOP_LIVE, 0, 0, null);
			break;
		case STATUS_AUDIENCE:
			EventManager.ins().sendEvent(EventTag.STOP_AUDIENCE, 0, 0, null);
			break;
		case STATUS_REPLAY:
			EventManager.ins().sendEvent(EventTag.STOP_REPLAY, 0, 0, null);
			break;
		case STATUS_CHAT:
			EventManager.ins().sendEvent(EventTag.STOP_CHAT, 0, 0, null);
			break;
		}
	}

	private String createTipText() {
		String str = "";
		switch (this.status) {
		case STATUS_LIVE:
			str = "是否结束当前直播";
			break;
		case STATUS_AUDIENCE:
			str = "是否结束当前观看";
			break;
		case STATUS_REPLAY:
			str = "是否结束当前观看";
			break;
		case STATUS_CHAT:
			str = "是否结束当前聊天";
			break;
		}
		return str;
	}

	public static interface OnCompleteListener {

		public void onCancelClick();

		public void onStopClick();
	}

}
