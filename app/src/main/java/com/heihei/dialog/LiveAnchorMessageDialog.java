package com.heihei.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import com.base.host.ActivityManager;
import com.base.host.AppLogic;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.HistoryUtils;
import com.base.utils.ThreadManager;
import com.base.utils.UIUtils;
import com.base.widget.toast.ChatToastHelper;
import com.heihei.adapter.BaseAdapter;
import com.heihei.cell.MessageCell;
import com.heihei.cell.MessageCell.OnMessageClickListener;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.MessageDialog.MessageAdapter;
import com.heihei.fragment.link.LinkUtil;
import com.heihei.fragment.live.LiveAnchorFragment.CloseLiveAnchorCallback;
import com.heihei.logic.present.PmPresent;
import com.heihei.model.User;
import com.heihei.model.msg.bean.ActionMessage;
import com.heihei.model.msg.bean.ObServerMessage;
import com.heihei.model.msg.due.DueMessageUtils;
import com.wmlives.heihei.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LiveAnchorMessageDialog extends Dialog implements Observer {

	private ListView mListView;
	private MessageAdapter mAdapter;
	private List<Object> dataList;
	private ImageView countView;
	private View emptyView;
	private Context context;
	private String liveId;

	private CloseLiveAnchorCallback mCloseLiveAnchorCallback;

	public LiveAnchorMessageDialog(Context context, ImageView view, String liveId, CloseLiveAnchorCallback callback) {
		super(context, R.style.ActionSheet);
		this.context = context;
		this.liveId = liveId;
		this.mCloseLiveAnchorCallback = callback;
		WindowManager mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		mWm.getDefaultDisplay().getMetrics(dm);
		Window w = getWindow();
		w.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.BOTTOM;
		lp.width = dm.widthPixels;
		w.setAttributes(lp);
		this.countView = view;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_message);
		mListView = (ListView) findViewById(R.id.listview);
		emptyView = findViewById(R.id.empty_view);
		DueMessageUtils.getInstance().addObserver(this);
		refresh();
	}

	@Override
	protected void onStop() {
		DueMessageUtils.getInstance().deleteObserver(this);
		super.onStop();
	}

	public void refresh() {

		dataList = DueMessageUtils.getInstance().getMessageQuene();

		if (mListView.getAdapter() == null) {
			mAdapter = new MessageAdapter(dataList);
			mListView.setAdapter(mAdapter);
		} else {
			mAdapter.notifyDataSetChanged();
		}

		if (dataList.size() > 0) {
			mListView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
		} else {
			mListView.setVisibility(View.GONE);
			emptyView.setVisibility(View.VISIBLE);
		}

	}

	class MessageAdapter extends BaseAdapter<Object> {

		public MessageAdapter(List<Object> data) {
			super(data);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.cell_message, null);
			}
			MessageCell lc = (MessageCell) convertView;
			lc.setOnMessageClickListener(mListener);
			lc.setData(getItem(position), position, this);
			return convertView;
		}

	}

	private OnMessageClickListener mListener = new OnMessageClickListener() {

		@Override
		public void onSendClick(final int position) {
			TipDialog dialog = new TipDialog(ActivityManager.getInstance().peek().getActivity());
			dialog.setContent(HostApplication.getInstance().getString(R.string.user_goto_pmfragment));
			dialog.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

				@Override
				public void onOkClick(Dialog dialog) {
					final ActionMessage message = (ActionMessage) dataList.get(position);

					PmPresent.getInstance().getDueChat(new JSONResponse() {

						@Override
						public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
							if (errCode == ErrorCode.ERROR_OK) {
								final int expireTime = json.optInt("expireTime");
								final long chatId = json.optLong("chatId");
								ThreadManager.getInstance().execute(new Runnable() {

									@Override
									public void run() {
										DueMessageUtils.getInstance().insertMessageList(false);
									}
								});
								
								ThreadManager.getInstance().getHandler().postDelayed(new Runnable() {

									@Override
									public void run() {
										CallCreaterUserDialog ud = new CallCreaterUserDialog(ActivityManager.getInstance().peek().getActivity(), message.user, expireTime, chatId);
										ud.show();
									}
								}, 1000);
								if (mCloseLiveAnchorCallback != null)
									mCloseLiveAnchorCallback.onClose();
								dismiss();
							} else if (errCode == 738 || errCode == 739) {
								UIUtils.showToast(msg);
							}
						}
					}, message.user.uid);

					ThreadManager.getInstance().execute(new Runnable() {

						@Override
						public void run() {
							DueMessageUtils.getInstance().insertMessageList(false);
						}
					});
				}

				@Override
				public void onCancleClick(Dialog dialog) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}

		@Override
		public void onIgnoreClick(final int position) {
			try {
				ActionMessage msg = (ActionMessage) dataList.get(position);
				HistoryUtils.getInstance().deleteMsg(new JSONResponse() {
					@Override
					public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
						if (errCode == ErrorCode.ERROR_OK) {
							dataList.remove(position);
							mAdapter.notifyDataSetChanged();

							if (dataList.size() > 0) {
								mListView.setVisibility(View.VISIBLE);
								emptyView.setVisibility(View.GONE);
							} else {
								mListView.setVisibility(View.GONE);
								emptyView.setVisibility(View.VISIBLE);
							}

							if (countView != null) {
								int size = dataList.size();
								if (size <= 0) {
									countView.setVisibility(View.GONE);
								} else {
									countView.setVisibility(View.VISIBLE);
								}
							}
						}
					}
				}, msg.user.uid);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onAvatar(final int position) {
			ActionMessage msg = (ActionMessage) dataList.get(position);
			if (!clickOlder()) {
				return;
			}
			UserDialog ud = new UserDialog(ActivityManager.getInstance().peek().getActivity(), msg.user, liveId, false, UserDialog.USERDIALOG_OPENTYPE_ANCHOR);
			ud.show();
		}

	};

	private long lastClickTime = 0;

	private boolean clickOlder() {
		long currentTime = System.currentTimeMillis();
		if (!(currentTime - lastClickTime > AppLogic.MIN_CLICK_DELAY_TIME))
			return false;
		lastClickTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public void update(Observable observable, Object data) {
		try {

			ThreadManager.getInstance().getHandler().post(new Runnable() {

				@Override
				public void run() {
					dataList = DueMessageUtils.getInstance().getMessageQuene();
					mAdapter.notifyDataSetChanged();
					if (dataList.size() > 0) {
						mListView.setVisibility(View.VISIBLE);
						emptyView.setVisibility(View.GONE);
					} else {
						mListView.setVisibility(View.GONE);
						emptyView.setVisibility(View.VISIBLE);
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
