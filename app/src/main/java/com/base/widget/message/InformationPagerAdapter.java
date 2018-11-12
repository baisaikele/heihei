package com.base.widget.message;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class InformationPagerAdapter extends FragmentStatePagerAdapter {
	private List<Fragment> list;  
	
	public InformationPagerAdapter(FragmentManager fm, List<Fragment> list) {  
        super(fm);  
        this.list = list;  
    }  
  
    @Override  
    public Fragment getItem(int arg0) {  
        return list.get(arg0);  
    }  
  
    @Override  
    public int getCount() {  
        return list.size();  
    }  
  

}
