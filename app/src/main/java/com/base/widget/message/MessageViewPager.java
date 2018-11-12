package com.base.widget.message;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class MessageViewPager extends ViewPager {
	private FragmentManager mFragmentManager = null;
	private ArrayList<Fragment> fragments = new ArrayList<>();

	public MessageViewPager(Context context) {
		super(context);
	}

	public MessageViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public void initFragment(FragmentManager manager) {
		fragments.add(new DanmakuViewFragment());
		setAdapter(new MFragmentPagerAdapter(mFragmentManager));
//		setOffscreenPageLimit(1);
//		setCurrentItem(0);
	}


	private class MFragmentPagerAdapter extends FragmentStatePagerAdapter {

	    public MFragmentPagerAdapter(FragmentManager fm) {  
	        super(fm);  
	    }  
	  
	    @Override  
	    public Fragment getItem(int arg0) {  
	        return fragments.get(arg0);  
	    }  
	  
	    @Override  
	    public int getCount() {  
	        return fragments.size();  
	    } 
	}
}
