package com.heihei.fragment.login;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.AppLogic;
import com.base.host.BaseFragment;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.KeyBoardUtil;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.heihei.dialog.LoadingDialog;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.wmlives.heihei.R;

public class LoginFragment extends BaseFragment implements OnClickListener {

    // ----------------R.layout.fragment_login-------------Start
    private RelativeLayout titlebar;// include R.layout.title_bar
    private LinearLayout ll_left;// include R.layout.title_bar
    private ImageButton iv_back;// include R.layout.title_bar
    private TextView tv_back;// include R.layout.title_bar
    private LinearLayout ll_right;// include R.layout.title_bar
    private ImageButton iv_right;// include R.layout.title_bar
    private TextView tv_right;// include R.layout.title_bar
    private LinearLayout ll_mid;// include R.layout.title_bar
    private TextView tv_title;// include R.layout.title_bar
    private Button btn_code;
    private EditText et_phone;
    private EditText et_code;
    private Button btn_login;

    public void autoLoad_fragment_login() {
        titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
        ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
        iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
        tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
        ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
        iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
        tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
        ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
        tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
        btn_code = (Button) findViewById(R.id.btn_code);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        btn_login = (Button) findViewById(R.id.btn_login);
    }

    // ----------------R.layout.fragment_login-------------End

    private UserPresent mUserPresent = new UserPresent();

    @Override
    protected String initTitle() {
        return getString(R.string.login);
    }

    @Override
    protected void loadContentView() {
        setContentView(R.layout.fragment_login);
    }

    @Override
    protected void viewDidLoad() {
        autoLoad_fragment_login();
        btn_code.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        et_phone.addTextChangedListener(phoneWatcher);
        et_code.addTextChangedListener(codeWatcher);
    }

    private TextWatcher phoneWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkPhone();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    private TextWatcher codeWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkPhoneAndCode();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    };

    private void checkPhone() {
        String phone = et_phone.getText().toString().trim();
        if (phone.length() == 11 && StringUtils.isValidMobiNumber(phone)) {
            btn_code.setEnabled(true);
        } else {
            btn_code.setEnabled(false);
        }
    }

    private MyHandler mHandler;

    @Override
    protected void refresh() {

        if (mHandler == null) {
            mHandler = new MyHandler();
        }

        checkPhone();
        checkPhoneAndCode();

        Message msg = new Message();
        msg.obj = this;
        msg.what = FLAG_SHOW_KEYBOARD;
        mHandler.sendMessageDelayed(msg, 250);
    }

    public void autoShowKeyboard() {
        KeyBoardUtil.showSoftKeyBoard(getActivity(), et_phone);
    }

    private void checkPhoneAndCode() {
        String phone = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        if (phone.length() == 11 && StringUtils.isValidMobiNumber(phone) && code.length() >= 6) {
            btn_login.setEnabled(true);
        } else {
            btn_login.setEnabled(false);
        }
    }
    
    /**
     * 请求短信验证码
     */
    private void requestSMSCode() {
        String phone = et_phone.getText().toString().trim();
        mUserPresent.getSMSCode(phone, new JSONResponse() {

            @Override
            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

                if (errCode == ErrorCode.ERROR_OK) {
                    UIUtils.showToast(getString(R.string.login_send_sms));
                    et_code.requestFocus();
                } else {
                    UIUtils.showToast(msg);
                    btn_code.setText(R.string.login_identifying_code);
                    btn_code.setEnabled(true);
                    mHandler.removeMessages(FLAG_COUNT_DOWN);
                }

            }
        });
    }

    /**
     * 登录
     */
    private void requestLogin() {
        String phone = et_phone.getText().toString().trim();
        String code = et_code.getText().toString().trim();

        mUserPresent.loginByPhone(phone, code, new JSONResponse() {

            @Override
            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
                
                if(getActivity() == null)
                {
                    return;
                }
                
                if (errCode == ErrorCode.ERROR_OK) {
                    String token = json.optString("token");
                    if (StringUtils.isEmpty(token)) {
                        UIUtils.showToast(getString(R.string.login_fail_try_retry));
                        return;
                    }
                    User user = new User();
                    user.token = token;
                    user.jsonParseUserDetails(json.optJSONObject("userInfo"));
                    UserMgr.getInstance().setLoginUser(user);
                    UserMgr.getInstance().saveLoginUser();

                    

                    boolean isRegister = json.optBoolean("needRegist");// 是否是第一次注册
                    if (isRegister || StringUtils.isEmpty(user.nickname) || StringUtils.isEmpty(user.birthday)
                            || user.gender == User.UNSPECIFIED) {
                        NavigationController.gotoCompleteInfoFragment(getContext());
                        getActivity().finish();
                    } else {
                    	UIUtils.showToast(getString(R.string.login_success));
                        NavigationController.gotoMainFragment(getContext());
                    }

                } else {
                    UIUtils.showToast(msg);
                }

            }
        });
        // NavigationController.gotoCompleteInfoFragment(getContext());

    }

    private int currentCount = 60;

    private void countDown() {
        currentCount -= 1;
        if (currentCount == 0) {
            btn_code.setEnabled(true);
            btn_code.setText(R.string.login_identifying_code_try);
            return;
        }
        btn_code.setText("" + currentCount + "s");
        Message msg = Message.obtain();
        msg.what = FLAG_COUNT_DOWN;
        msg.obj = this;
        mHandler.sendMessageDelayed(msg, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_code :

            btn_code.setEnabled(false);
            currentCount = 60;
            btn_code.setText("" + currentCount + "s");
            Message msg = Message.obtain();
            msg.what = FLAG_COUNT_DOWN;
            msg.obj = this;
            mHandler.sendMessageDelayed(msg, 1000);

            requestSMSCode();

            break;
        case R.id.btn_login :
            requestLogin();
            break;
        }
    }

    private static final int FLAG_SHOW_KEYBOARD = 0;
    private static final int FLAG_COUNT_DOWN = 1;

    private static class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LoginFragment fragment = (LoginFragment) msg.obj;
            if (fragment.getActivity() == null) {
                return;
            }
            switch (msg.what) {
            case FLAG_SHOW_KEYBOARD :

                fragment.autoShowKeyboard();
                break;
            case FLAG_COUNT_DOWN :
                fragment.countDown();
                break;
            }
        }
    }

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "LoginFragment";
	}

}
