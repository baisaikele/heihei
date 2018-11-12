package com.base.host;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

public class HostActivity extends FragmentActivity {

    private String fragmentName;
    private boolean loaded;
    private HostClassLoader classLoader;
    private HostResource myResources;
    private AssetManager assetManager;
    private Resources resources;
    private Theme theme;
    private FrameLayout rootView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader == null ? super.getClassLoader() : classLoader;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }

    @Override
    public Theme getTheme() {
        return theme == null ? super.getTheme() : theme;
    }

    public HostResource getOverrideResources() {
        return myResources;
    }

    void setOverrideResources(HostResource myres) {
        if (myres == null) {
            this.myResources = null;
            this.resources = null;
            this.assetManager = null;
            this.theme = null;
        } else {
            this.myResources = myres;
            this.resources = myres.getResources();
            this.assetManager = myres.getAssets();
            Theme t = myres.getResources().newTheme();
            t.setTo(getTheme());
            this.theme = t;
        }
    }

}
