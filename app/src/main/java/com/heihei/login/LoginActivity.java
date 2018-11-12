package com.heihei.login;

import android.os.Bundle;

import com.base.host.ActivityManager;
import com.base.host.BaseActivity;
import com.heihei.fragment.login.GuideFragment;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		ActivityManager.getInstance().finishAllActivitysBefore();
	}

	@Override
	protected String initFragmentClassName() {
		return GuideFragment.class.getName();
	}
}
