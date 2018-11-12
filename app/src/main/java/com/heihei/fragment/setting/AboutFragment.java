package com.heihei.fragment.setting;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.BaseFragment;
import com.base.host.NavigationController;
import com.heihei.logic.present.UserPresent;
import com.wmlives.heihei.R;

public class AboutFragment extends BaseFragment implements OnClickListener {

    // ----------------R.layout.fragment_about-------------Start
    private RelativeLayout titlebar;// include R.layout.title_bar
    private LinearLayout ll_left;// include R.layout.title_bar
    private ImageButton iv_back;// include R.layout.title_bar
    private TextView tv_back;// include R.layout.title_bar
    private LinearLayout ll_right;// include R.layout.title_bar
    private ImageButton iv_right;// include R.layout.title_bar
    private TextView tv_right;// include R.layout.title_bar
    private LinearLayout ll_mid;// include R.layout.title_bar
    private TextView tv_title;// include R.layout.title_bar
    private RelativeLayout btn_notice;
    private RelativeLayout btn_proxt;
    private RelativeLayout btn_contact_us;
    private UserPresent present=new UserPresent();

    public void autoLoad_fragment_about() {
        titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
        ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
        iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
        tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
        ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
        iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
        tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
        ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
        tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
        btn_notice = (RelativeLayout) findViewById(R.id.btn_notice);
        btn_proxt = (RelativeLayout) findViewById(R.id.btn_proxt);
        btn_contact_us = (RelativeLayout) findViewById(R.id.btn_contact_us);
    }

    // ----------------R.layout.fragment_about-------------End

    @Override
    protected void loadContentView() {
        setContentView(R.layout.fragment_about);

    }

    @Override
    protected String initTitle() {
        return getString(R.string.setting_about);
    }
    
    @Override
    protected void viewDidLoad() {
        autoLoad_fragment_about();
        btn_notice.setOnClickListener(this);
        btn_proxt.setOnClickListener(this);
        btn_contact_us.setOnClickListener(this);

    }

    @Override
    protected void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_notice ://社会公约
        	NavigationController.gotoWebView(getContext(), present.getSocialPact(),getString(R.string.setting_notice));
            break;
        case R.id.btn_proxt ://服务条款与使用协议
        	NavigationController.gotoWebView(getContext(), present.getServiceTerms(),getString(R.string.setting_proxy));
            break;
        case R.id.btn_contact_us ://联系我们
        	NavigationController.gotoWebView(getContext(), present.getContactUs(),getString(R.string.setting_contact_us));
            break;
        }

    }

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "AboutFragment";
	}

}
