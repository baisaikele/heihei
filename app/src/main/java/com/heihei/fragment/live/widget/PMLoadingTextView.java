package com.heihei.fragment.live.widget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.wmlives.heihei.R;

public class PMLoadingTextView extends TextView {

	private static final int TIME_INTERVAL = 500;

	public PMLoadingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private LoopHandler mHandler;

	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		if (visibility == View.VISIBLE) {
			count = 0;
			// setCurrentText();
			if (mHandler == null) {
				mHandler = new LoopHandler();
			}
			if (mHandler != null) {
				Message msg = new Message();
				msg.obj = new WeakReference<PMLoadingTextView>(this);
				mHandler.sendMessageDelayed(msg, TIME_INTERVAL);
			}
		} else {
			count = 0;
			if (mHandler != null) {
				mHandler.removeMessages(0);
			}
		}
	}

	private int count = 0;

	public void setCurrentText(ArrayList<String> topics) {
		
//		if (count == 0) {
//			setText(R.string.pm_loading_01);
//		} else if (count == 1) {
//			setText(R.string.pm_loading_02);
//		} else if (count == 2) {
//			setText(R.string.pm_loading_03);
//		}
//		Log.i("pmfragment", "topics : " + topics.toString());
	}

	private Timer timer = new Timer();

	public void tick() {
		count += 1;
		if (count > 2) {
			count = 0;
		}
		// setCurrentText();

		if (getVisibility() == View.VISIBLE) {
			Message msg = new Message();
			msg.obj = new WeakReference<PMLoadingTextView>(this);
			mHandler.sendMessageDelayed(msg, TIME_INTERVAL);
		}
	}

	private static class LoopHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			WeakReference<PMLoadingTextView> tv = (WeakReference<PMLoadingTextView>) msg.obj;
			if (tv.get() != null) {
				if (tv.get().getVisibility() == View.VISIBLE) {
					tv.get().tick();
				}
			}
		}
	}

}
