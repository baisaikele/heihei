package com.heihei.fragment.withdraw;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.BaseFragment;
import com.base.utils.StringUtils;
import com.wmlives.heihei.R;

/**
 * 提现账户信息
 * 
 * @author chengbo
 */
public class PayAccountFragment extends BaseFragment implements OnClickListener {

    // ----------------R.layout.fragment_pay_account-------------Start
    private RelativeLayout titlebar;// include R.layout.title_bar
    private LinearLayout ll_left;// include R.layout.title_bar
    private ImageButton iv_back;// include R.layout.title_bar
    private TextView tv_back;// include R.layout.title_bar
    private LinearLayout ll_right;// include R.layout.title_bar
    private ImageButton iv_right;// include R.layout.title_bar
    private TextView tv_right;// include R.layout.title_bar
    private LinearLayout ll_mid;// include R.layout.title_bar
    private TextView tv_title;// include R.layout.title_bar
    private RelativeLayout btn_account;
    private EditText et_account;
    private EditText et_name;
    private EditText et_card;
    private EditText et_phone;
    private LinearLayout btn_agree;
    private ImageView iv_select;
    private TextView btn_proxy;
    private Button btn_submit;

    public void autoLoad_fragment_pay_account() {
        titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
        ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
        iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
        tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
        ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
        iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
        tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
        ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
        tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
        btn_account = (RelativeLayout) findViewById(R.id.btn_account);
        et_account = (EditText) findViewById(R.id.et_account);
        et_name = (EditText) findViewById(R.id.et_name);
        et_card = (EditText) findViewById(R.id.et_card);
        et_phone = (EditText) findViewById(R.id.et_phone);
        btn_agree = (LinearLayout) findViewById(R.id.btn_agree);
        iv_select = (ImageView) findViewById(R.id.iv_select);
        btn_proxy = (TextView) findViewById(R.id.btn_proxy);
        btn_submit = (Button) findViewById(R.id.btn_submit);
    }

    // ----------------R.layout.fragment_pay_account-------------End

    @Override
    protected void loadContentView() {
        setContentView(R.layout.fragment_pay_account);

    }

    @Override
    protected String initTitle() {
        return getString(R.string.user_account_info);
    }

    @Override
    protected void viewDidLoad() {
        autoLoad_fragment_pay_account();

        btn_agree.setOnClickListener(this);
        btn_proxy.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_proxy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        btn_proxy.getPaint().setAntiAlias(true);// 抗锯齿
        et_account.addTextChangedListener(watcher);
        et_name.addTextChangedListener(watcher);
        et_card.addTextChangedListener(watcher);
        et_phone.addTextChangedListener(watcher);
    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            refreshButton();
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

    @Override
    protected void refresh() {
        refreshButton();
    }

    private void refreshButton() {
        if (checkInfo()) {
            btn_submit.setEnabled(true);
        } else {
            btn_submit.setEnabled(false);
        }
    }

    private boolean checkInfo() {
        String account = et_account.getText().toString().trim();
        String name = et_name.getText().toString().trim();
        String card = et_card.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        if (StringUtils.isEmpty(account)) {
            return false;
        }
        if (StringUtils.isEmpty(name)) {
            return false;
        }

        if (StringUtils.isEmpty(card)) {
            return false;
        }

        if (StringUtils.isEmpty(phone) || !StringUtils.isValidMobiNumber(phone)) {
            return false;
        }

        if (!iv_select.isSelected()) {
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_agree :// 我同意
            iv_select.setSelected(!iv_select.isSelected());
            refreshButton();
            break;
        case R.id.btn_proxy :// 查看协议
            break;
        case R.id.btn_submit :// 验证

            String phone = et_phone.getText().toString().trim();
            Intent intent = new Intent();
            intent.putExtra("account", phone);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();

            break;
        }

    }

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "PayAccountFragment";
	}

}
