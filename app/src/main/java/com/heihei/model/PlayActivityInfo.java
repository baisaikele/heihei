package com.heihei.model;

import com.base.host.BaseFragment;
import com.heihei.fragment.PMFragment;

import android.app.Activity;
import android.app.Fragment;

public class PlayActivityInfo {
	public boolean isReplay = false;
	public Activity replayActivity = null;

	public boolean isFans = false;
	public Activity fansActivity = null;

	public boolean isFollow = false;
	public Activity followActivity = null;

	public boolean isLiveList = false;
	public Activity liveListActivity = null;

	public PMFragment pmFragment = null;
	public boolean isPmfragment = false;

	public int openType = -1;

	public PlayActivityInfo() {
	}

	public PlayActivityInfo(boolean replay, Activity a) {
		this.isReplay = replay;
		this.replayActivity = a;
	}

	public void setFansInfo(boolean fans, Activity a) {
		this.isFans = fans;
		this.fansActivity = a;
	}

	public void setFollowInfo(boolean follow, Activity a) {
		this.isFollow = follow;
		this.followActivity = a;
	}

	public void setLiveListActivity(boolean livelist, Activity a) {
		this.isLiveList = livelist;
		this.liveListActivity = a;
	}

	public void setPmFragment(boolean isFragment, PMFragment a) {
		this.isPmfragment = isFragment;
		this.pmFragment = a;
	}

	@Override
	public String toString() {
		return "ReplayActivityInfo [isReplay=" + isReplay + "]";
	}
}
