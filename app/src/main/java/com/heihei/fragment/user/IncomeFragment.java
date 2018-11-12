package com.heihei.fragment.user;

import org.json.JSONObject;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.BaseFragment;
import com.base.host.NavigationController;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.StringUtils;
import com.base.widget.MoneyTextView;
import com.heihei.dialog.TipDialog;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.PaymentPresent;
import com.heihei.logic.present.PmPresent;
import com.heihei.logic.present.UserPresent;
import com.wmlives.heihei.R;

/**
 * 我的收益
 * 
 * @author chengbo
 */
public class IncomeFragment extends BaseFragment implements OnClickListener {

	// ----------------R.layout.fragment_income-------------Start
	private RelativeLayout titlebar;// include R.layout.title_bar
	private LinearLayout ll_left;// include R.layout.title_bar
	private ImageButton iv_back;// include R.layout.title_bar
	private TextView tv_back;// include R.layout.title_bar
	private LinearLayout ll_right;// include R.layout.title_bar
	private ImageButton iv_right;// include R.layout.title_bar
	private TextView tv_right;// include R.layout.title_bar
	private LinearLayout ll_mid;// include R.layout.title_bar
	private TextView tv_title;// include R.layout.title_bar
	private TextView tv_ticker;
	private MoneyTextView tv_money;
	private Button btn_withdraw;
	private Button btn_exchange;

	public void autoLoad_fragment_income() {
		titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
		ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
		iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
		tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
		ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
		iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
		tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
		ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
		tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
		tv_ticker = (TextView) findViewById(R.id.tv_ticker);
		tv_money = (MoneyTextView) findViewById(R.id.tv_money);
		btn_withdraw = (Button) findViewById(R.id.btn_withdraw);
		btn_exchange = (Button) findViewById(R.id.btn_exchange);
	}

	// ----------------R.layout.fragment_income-------------End

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_income);
	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_income();
		btn_exchange.setOnClickListener(this);
		btn_withdraw.setOnClickListener(this);

	}

	@Override
	protected String initTitle() {
		return getString(R.string.user_mine_income);
	}

	private PaymentPresent mPayPresent = new PaymentPresent();

	@Override
	protected void refresh() {

		refreshView(UserMgr.getInstance().getLoginUser().point, 0);

		mPayPresent.getWithdrawMoney(new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

				if (getActivity() == null) {
					return;
				}

				if (errCode == ErrorCode.ERROR_OK) {
					int money = json.optInt("cash");
					refreshView(UserMgr.getInstance().getLoginUser().point, money);
				}

			}
		});
	}

	private void refreshView(int diamondNum, int money) {
		tv_ticker.setText("" + diamondNum);
		tv_money.setMoney(money);
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_withdraw:// 提现
			// NavigationController.gotoWithdrawFragment(getContext());
			TipDialog td = new TipDialog(getContext());
			if (PmPresent.getInstance().getPayTip() != null && PmPresent.getInstance().getPayTip().length() > 1) {
				td.setContent(PmPresent.getInstance().getPayTip());
			} else {
				td.setContent(getResources().getString(R.string.withdraw_tip));
			}
			td.setBtnCancelVisibity(View.GONE);
			td.setBtnOKText(getResources().getString(R.string.confirm));
			td.show();
			break;
		case R.id.btn_exchange:// 兑换钻石
			NavigationController.gotoExchangeFragment(getContext());
			break;
		}

	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "IncomeFragment";
	}

}
