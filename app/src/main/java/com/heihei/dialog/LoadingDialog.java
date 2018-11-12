package com.heihei.dialog;

import com.wmlives.heihei.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class LoadingDialog extends Dialog {

	public LoadingDialog(Context context) {
		super(context, R.style.BaseDialog);
		setOwnerActivity((Activity) context);
		setCancelable(true);
		setCanceledOnTouchOutside(false);
		setOnKeyListener(keylistener);
	}

	private ImageView mProgressBar;
	private AnimationDrawable mAnimDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		mProgressBar = (ImageView) findViewById(R.id.iv_loading);
		mProgressBar.setVisibility(View.VISIBLE);
		mAnimDrawable = (AnimationDrawable) getContext().getResources().getDrawable(R.drawable.voice_loading_anim);
		mProgressBar.setImageDrawable(mAnimDrawable);
	}

	private OnKeyListener keylistener = new DialogInterface.OnKeyListener() {
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
				return true;
			} else {
				return false;
			}
		}
	};

	@Override
	public void show() {
		super.show();
		if (mProgressBar != null) {
			mProgressBar.setVisibility(View.VISIBLE);
			mAnimDrawable.start();
		}
	}

	@Override
	public void dismiss() {
		super.dismiss();
		mProgressBar.setVisibility(View.GONE);
		if (mAnimDrawable != null && mAnimDrawable.isRunning()) {
			mAnimDrawable.stop();
		}
	}

}
