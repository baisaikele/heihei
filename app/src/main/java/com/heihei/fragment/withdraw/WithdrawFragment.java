package com.heihei.fragment.withdraw;

import java.math.BigDecimal;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.BaseFragment;
import com.base.utils.StringUtils;
import com.wmlives.heihei.R;

public class WithdrawFragment extends BaseFragment implements OnClickListener {

    // ----------------R.layout.fragment_withdraw-------------Start
    private RelativeLayout titlebar;// include R.layout.title_bar
    private LinearLayout ll_left;// include R.layout.title_bar
    private ImageButton iv_back;// include R.layout.title_bar
    private TextView tv_back;// include R.layout.title_bar
    private LinearLayout ll_right;// include R.layout.title_bar
    private ImageButton iv_right;// include R.layout.title_bar
    private TextView tv_right;// include R.layout.title_bar
    private LinearLayout ll_mid;// include R.layout.title_bar
    private TextView tv_title;// include R.layout.title_bar
    private LinearLayout ll_success_tip;
    private RelativeLayout btn_account;
    private TextView tv_phone;
    private TextView tv_money_tip;
    private EditText et_money;
    private Button btn_submit;
    private TextView btn_withdraw_proxy;
    private Button btn_close;

    public void autoLoad_fragment_withdraw() {
        titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
        ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
        iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
        tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
        ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
        iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
        tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
        ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
        tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
        ll_success_tip = (LinearLayout) findViewById(R.id.ll_success_tip);
        btn_account = (RelativeLayout) findViewById(R.id.btn_account);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_money_tip = (TextView) findViewById(R.id.tv_money_tip);
        et_money = (EditText) findViewById(R.id.et_money);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_withdraw_proxy = (TextView) findViewById(R.id.btn_withdraw_proxy);
        btn_close = (Button) findViewById(R.id.btn_close);
    }

    // ----------------R.layout.fragment_withdraw-------------End

    @Override
    protected void loadContentView() {
        setContentView(R.layout.fragment_withdraw);
    }

    @Override
    protected String initTitle() {
        return getString(R.string.user_account_withdrawals);
    }

    @Override
    protected void viewDidLoad() {
        autoLoad_fragment_withdraw();
        btn_account.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_close.setOnClickListener(this);
        btn_withdraw_proxy.setOnClickListener(this);
        btn_withdraw_proxy.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
        btn_withdraw_proxy.getPaint().setAntiAlias(true);// 抗锯齿
        
        et_money.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInfo();

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void refresh() {
        checkInfo();
    }

    private void checkInfo() {
        String mMoney = et_money.getText().toString().trim();
        if (StringUtils.isEmpty(mMoney)) {
            btn_submit.setEnabled(false);
            return;
        }
        BigDecimal dbWidthDrawAmount = new BigDecimal(mMoney);
        long longWidthDrawAmount = (long) (dbWidthDrawAmount.doubleValue() * 100);
        if (longWidthDrawAmount > 0) {
            btn_submit.setEnabled(true);
            return;
        }
        btn_submit.setEnabled(false);
    }

    private void setAccount(String phone) {
        if (phone.length() > 7) {
            tv_phone.setText(phone.substring(0, 3) + "****" + phone.substring(7, phone.length()));
        } else {
            tv_phone.setText(phone);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String phone = data.getStringExtra("account");
            setAccount(phone);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_account :
            startFragmentForResult(PayAccountFragment.class, null, 0);
            break;
        case R.id.btn_submit :
            btn_submit.setVisibility(View.GONE);
            btn_account.setEnabled(false);
            et_money.setEnabled(false);
            ll_success_tip.setVisibility(View.VISIBLE);
            btn_close.setVisibility(View.VISIBLE);
            break;
        case R.id.btn_close :
            getActivity().finish();
            break;
        }

    }

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "WithdrawFragment";
	}

}
