package com.base.host;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class ActivityManager {

    private Stack<WeakReference<Fragment>> mStack = new Stack<WeakReference<Fragment>>();

    private static ActivityManager mManager;

    public ActivityManager() {
        mStack.clear();
    }

    public static ActivityManager getInstance() {
        if (mManager == null) {
            mManager = new ActivityManager();
        }
        return mManager;
    }

    public Fragment peek() {
        WeakReference<Fragment> wr = mStack.peek();
        return wr.get();
    }

    public void pop() {
        mStack.pop();
    }

    public void push(Fragment fragment) {
        mStack.push(new WeakReference<Fragment>(fragment));
    }

    public void bountToTop(Fragment fragment) {
        remove(fragment);
        push(fragment);
    }

    public void remove(Fragment fragment) {
        List<WeakReference<Fragment>> dels = new ArrayList<WeakReference<Fragment>>();
        for (WeakReference<Fragment> fra : mStack) {

            if (fra.get() == null || (fra.get() != null && fra.get() == fragment)) {
                dels.add(fra);
            }
        }
        mStack.removeAll(dels);
    }

    public void clear() {
        mStack.clear();
    }

    /**
     * 结束一个Activity
     * 
     * @param fragment
     */
    public void finishActivity(BaseFragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    /**
     * 通过传一个class来结束activity
     * 
     * @param clazz
     */
    public void finishActivityByFragmentClass(Class<?> clazz) {
        for (WeakReference<Fragment> fra : mStack) {

            if (fra.get() != null && fra.get().getClass().getName().equals(clazz.getName())) {
                Activity activity = fra.get().getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivitys() {
        for (WeakReference<Fragment> frag : mStack) {
            if (frag.get() != null) {
                Activity activity = frag.get().getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

    public void finishActivitysAfter(Class<?> clazz) {
        int count = mStack.size();
        for (int i = count - 1; i >= 0; i--) {
            WeakReference<Fragment> fra = mStack.get(i);
            if (fra.get() != null)
            {
                if (fra.get().getClass().getName().equals(clazz.getName()))
                {
                    break;
                }
                
                Activity activity = fra.get().getActivity();
                if (activity != null)
                {
                    activity.finish();
                }
            }
        }
    }

    public void finishAllActivitysBefore() {
        for (WeakReference<Fragment> fra : mStack) {

            if (fra.get() != null && !fra.get().getClass().getName().equals(peek().getClass().getName())) {
                Activity activity = fra.get().getActivity();
                if (activity != null) {
                    activity.finish();
                }
            }
        }
    }

}
