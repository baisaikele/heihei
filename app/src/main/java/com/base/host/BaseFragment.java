package com.base.host;

import com.base.utils.DeviceInfoUtils;
import com.base.utils.LogWriter;
import com.base.widget.TitleBar;
import com.umeng.analytics.MobclickAgent;
import com.wmlives.heihei.R;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {

    private View mContentView;

    protected TitleBar mTitleBar;

    protected ViewParam mViewParam;
    
    public abstract String getCurrentFragmentName();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Context getContext() {
        try {
            return super.getContext();
        } catch (Exception e) {
            return getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	LogWriter.d("BaseFragment", "onCreateView " + this.getClass().getName());
        if (mViewParam == null && getParentFragment() == null && getActivity().getIntent() != null) {
            mViewParamKey = getActivity().getIntent().getStringExtra(BaseActivity.FRAGMENT_PARAMS_KEY);
            mViewParam = FragmentParams.getInstance().get(mViewParamKey);
            FragmentParams.getInstance().remove(mViewParamKey);
        }

        if (mViewParam == null) {
            mViewParam = new ViewParam();
        }
        loadContentView();
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        modifyTitleBarHeight();
        setTitleBarTitle();
        viewDidLoad();
        refresh();

        LogWriter.d("BaseFragment", "view did load");

    }

    /**
     * 修改titlebar的高度，沉浸模式
     */
    protected void modifyTitleBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int statusBarHeight = DeviceInfoUtils.getStatusBarHeight(getContext());
            View titleBar = findViewById(R.id.titlebar);
            if (titleBar != null && titleBar.getVisibility() == View.VISIBLE) {
                ViewGroup.LayoutParams params = titleBar.getLayoutParams();
                params.height = (int) (getResources().getDimension(R.dimen.titlebar_height) + statusBarHeight);
                titleBar.setPadding(0, statusBarHeight, 0, 0);
                titleBar.setLayoutParams(params);
            }
        }
    }

    @Override
    public void onDestroy() {
    	LogWriter.d("BaseFragment", "onDestroy " + this.getClass().getName());
        super.onDestroy();
    }

    private String mViewParamKey = "";

    public void setViewParam(ViewParam mViewParam) {
        if (mViewParam == null) {
            mViewParam = new ViewParam();
        }
        this.mViewParam = mViewParam;
    }

    /**
     * 加载页面 其中应该调用setContentView
     */
    protected abstract void loadContentView();

    /**
     * view加载完成 其中应该findviewbyid
     */
    protected abstract void viewDidLoad();

    /**
     * 刷新
     */
    protected abstract void refresh();

    protected TitleBar getTitleBar() {
        return mTitleBar;
    }

    private void setTitleBarTitle() {
        if (mTitleBar != null) {
            mTitleBar.setTitle(initTitle());
            View backBtn = mTitleBar.findViewById(R.id.iv_back);
            if (backBtn != null) {
                backBtn.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }
        }
    }

    protected String initTitle() {
        if (mViewParam == null) {
            return "";
        }
        return mViewParam.title;
    }

    protected final void setContentView(int layoutId) {
        HostResource res = HostResource.getResource(getClass());
        mContentView = res.inflate(getActivity(), layoutId, null, false);
        if (mContentView != null) {
            View titleBarView = mContentView.findViewById(R.id.titlebar);
            if (titleBarView != null) {
                mTitleBar = new TitleBar(titleBarView);
            }
        }
    }

    protected final void setContentView(View view) {
        mContentView = view;

        if (mContentView != null) {
            View titleBarView = mContentView.findViewById(R.id.titlebar);
            if (titleBarView != null) {
                mTitleBar = new TitleBar(titleBarView);
            }
        }
    }

    protected final View findViewById(int id) {
        return getView().findViewById(id);
    }

    /**
     * 是否拦截返回键
     * 
     * @return
     */
    protected boolean canBack() {
        return true;
    }

    public void startFragment(Class<?> clazz, ViewParam mViewParam) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), BaseActivity.class);
            intent.putExtra(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());
            String key = FragmentParams.getInstance().createKey(getClass(), clazz);
            FragmentParams.getInstance().put(key, mViewParam);
            intent.putExtra(BaseActivity.FRAGMENT_PARAMS_KEY, key);
            getActivity().startActivity(intent);
        }
    }

    public void startFragmentForResult(Class<?> clazz, ViewParam mViewParam, int requestCode) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), BaseActivity.class);
            intent.putExtra(BaseActivity.FRAGMENT_CLASS_NAME, clazz.getName());
            String key = FragmentParams.getInstance().createKey(getClass(), clazz);
            FragmentParams.getInstance().put(key, mViewParam);
            intent.putExtra(BaseActivity.FRAGMENT_PARAMS_KEY, key);
            getActivity().startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 在一个BaseActivity中 切换当前的fragment，换成一个新的页面
     * 
     * @param clazz
     * @param mViewPatam
     */
    public void replaceFragment(Class<? extends BaseFragment> clazz, ViewParam mViewPatam) {
        if (getActivity() != null && BaseActivity.class.isInstance(getActivity())) {
            BaseActivity mActivity = (BaseActivity) getActivity();
            try {
                BaseFragment bf = (BaseFragment) clazz.newInstance();
                bf.setViewParam(mViewPatam);
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(android.R.id.primary, bf);
                ft.commit();
                mActivity.mMainFragment = bf;
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getCurrentFragmentName());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getCurrentFragmentName());
	}

}
