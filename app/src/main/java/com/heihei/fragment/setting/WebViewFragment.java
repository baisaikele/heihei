package com.heihei.fragment.setting;

import com.base.host.BaseFragment;
import com.wmlives.heihei.R;

import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewFragment extends BaseFragment {
	private WebView mWebview;
	private String url;

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_webview);
	}

	@Override
	protected void viewDidLoad() {
		mWebview = (WebView) findViewById(R.id.rule_webview);
	}

	@Override
	protected String initTitle() {
		return mViewParam.title;
	}

	@Override
	protected void refresh() {
		initView();
	}


	private void initView() {
		// 设置可以支持缩放
		mWebview.getSettings().setSupportZoom(true);
		// 设置出现缩放工具
		mWebview.getSettings().setBuiltInZoomControls(true);
		// 扩大比例的缩放
		mWebview.getSettings().setUseWideViewPort(true);
		// 自适应屏幕
		mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		mWebview.getSettings().setLoadWithOverviewMode(true);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.setBackgroundColor(getResources().getColor(R.color.full_transparent));
		
		ShowWebView(mViewParam.data.toString());
	}

	private void ShowWebView(String url) {
		mWebview.loadUrl(url);
		mWebview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed();
				super.onReceivedSslError(view, handler, error);
			}
		});
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "WebViewFragment";
	}

}
