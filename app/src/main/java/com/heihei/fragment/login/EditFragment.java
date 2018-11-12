package com.heihei.fragment.login;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.base.host.BaseFragment;
import com.base.host.HostApplication;
import com.base.host.NavigationController;
import com.base.host.ViewParam;
import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.KeyBoardUtil;
import com.base.utils.LogWriter;
import com.base.utils.SharedPreferencesUtil;
import com.base.utils.StringUtils;
import com.base.utils.UIUtils;
import com.base.utils.location.LocationApi;
import com.heihei.dialog.BirthdayDialog;
import com.heihei.dialog.BirthdayDialog.OnDatePickListener;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.fragment.user.LocationFragment;
import com.heihei.logic.UserMgr;
import com.heihei.logic.present.UserPresent;
import com.heihei.model.User;
import com.wmlives.heihei.R;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
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

public class EditFragment extends BaseFragment implements OnClickListener {

	public static final String TYPE_COMPLETE = "type_complete";// 完善资料
	public static final String TYPE_EDIT = "type_edit";// 编辑资料

	// ----------------R.layout.fragment_edit-------------Start
	private RelativeLayout titlebar;// include R.layout.title_bar
	private LinearLayout ll_left;// include R.layout.title_bar
	private ImageButton iv_back;// include R.layout.title_bar
	private TextView tv_back;// include R.layout.title_bar
	private LinearLayout ll_right;// include R.layout.title_bar
	private ImageButton iv_right;// include R.layout.title_bar
	private TextView tv_right;// include R.layout.title_bar
	private LinearLayout ll_mid;// include R.layout.title_bar
	private TextView tv_title;// include R.layout.title_bar
	private EditText et_nickname;
	private Button btn_male;
	private Button btn_female;
	private RelativeLayout btn_birthday;
	private TextView tv_birthday_tip;
	private ImageView iv_birthday_right_arrow;

	private RelativeLayout rl_birthday_tip;
	private AvatarImageView iv_constellation;
	private TextView tv_birthday;
	private TextView tv_city_title;
	private RelativeLayout btn_city;
	private TextView tv_city_tip;
	private ImageView iv_city_right_arrow;
	private RelativeLayout rl_city_tip;
	private ImageView iv_city;
	private TextView tv_city;
	private TextView tv_sign_title;
	private EditText et_sign;
	private Button btn_submit;
	private LocationApi mLocationApi;
	private TextView tv_edit_information;

	public void autoLoad_fragment_edit() {
		titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
		ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
		iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
		tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
		ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
		iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
		tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
		ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
		tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
		et_nickname = (EditText) findViewById(R.id.et_nickname);
		btn_male = (Button) findViewById(R.id.btn_male);
		btn_female = (Button) findViewById(R.id.btn_female);
		btn_birthday = (RelativeLayout) findViewById(R.id.btn_birthday);
		tv_birthday_tip = (TextView) findViewById(R.id.tv_birthday_tip);
		iv_birthday_right_arrow = (ImageView) findViewById(R.id.iv_birthday_right_arrow);
		rl_birthday_tip = (RelativeLayout) findViewById(R.id.rl_birthday_tip);
		iv_constellation = (AvatarImageView) findViewById(R.id.iv_constellation);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		tv_city_title = (TextView) findViewById(R.id.tv_city_title);
		btn_city = (RelativeLayout) findViewById(R.id.btn_city);
		tv_city_tip = (TextView) findViewById(R.id.tv_city_tip);
		iv_city_right_arrow = (ImageView) findViewById(R.id.iv_city_right_arrow);
		rl_city_tip = (RelativeLayout) findViewById(R.id.rl_city_tip);
		iv_city = (ImageView) findViewById(R.id.iv_city);
		tv_city = (TextView) findViewById(R.id.tv_city);
		tv_sign_title = (TextView) findViewById(R.id.tv_sign_title);
		et_sign = (EditText) findViewById(R.id.et_sign);
		btn_submit = (Button) findViewById(R.id.btn_submit);
		tv_edit_information = (TextView) findViewById(R.id.tv_edit_information);
	}

	// ----------------R.layout.fragment_edit-------------End

	private UserPresent mUserPresent = new UserPresent();

	@Override
	protected void loadContentView() {
		setContentView(R.layout.fragment_edit);
	}

	@Override
	protected void viewDidLoad() {
		autoLoad_fragment_edit();
		btn_female.setOnClickListener(this);
		btn_male.setOnClickListener(this);
		btn_birthday.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		tv_right.setOnClickListener(this);
		iv_constellation.setShowCircle(true);
		btn_city.setOnClickListener(this);

		InputFilter[] filters = new InputFilter[1];
		filters[0] = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				LogWriter.d("edit", "source start:" + start + "-end:" + end + "=====ds:" + dstart + "-dend:" + dend);
				int sourceLen = StringUtils.getLengthOfByteCode(source.toString());
				int destLen = StringUtils.getLengthOfByteCode(dest.toString());

				if (dstart == 0 && StringUtils.isBlackChar(source.toString()))// 第一个不能为空格
				{
					return "";
				}

				if (dest.toString().endsWith(" ") && StringUtils.isBlackChar(source.toString()))// 不能连续两个空格
				{
					return "";
				}

				if (dstart >= 1 && dstart <= dest.length()) {
					String sectionStr = dest.subSequence(dstart - 1, dstart).toString();
					LogWriter.d("edit", sectionStr);
					if (StringUtils.isBlackChar(sectionStr) && StringUtils.isBlackChar(source.toString())) {
						return "";
					}
				}

				if (sourceLen + destLen > 16)// 不能超过16个字节
				{
					UIUtils.showToast(R.string.account_nickname_too_long);
					try {
						if (source.length() >= 8) {
							return source.subSequence(0, 8);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					return "";
				}
				return source;
			}
		};

		et_nickname.setFilters(filters);
		et_nickname.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (mViewParam.type.equals(TYPE_COMPLETE)) {
					refreshCompleteButton();
				} else {

				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		InputFilter filter = new InputFilter() {

			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				int sourceLen = StringUtils.getLengthOfByteCode(source.toString());
				int destLen = StringUtils.getLengthOfByteCode(dest.toString());

				if (dstart == 0 && StringUtils.isBlackChar(source.toString()))// 第一个不能为空格
				{
					return "";
				}

				if (dest.toString().endsWith(" ") && StringUtils.isBlackChar(source.toString()))// 不能连续两个空格
				{
					return "";
				}

				if (dstart >= 1 && dstart <= dest.length()) {
					String sectionStr = dest.subSequence(dstart - 1, dstart).toString();
					LogWriter.d("edit", sectionStr);
					if (StringUtils.isBlackChar(sectionStr) && StringUtils.isBlackChar(source.toString())) {
						return "";
					}
				}

				if (sourceLen + destLen > 48)// 不能超过48个字节
				{
					// UIUtils.showToast(R.string.account_nickname_too_long);
					return "";
				}
				return source;
			}
		};
		et_sign.setFilters(new InputFilter[] { filter });
	}

	@Override
	protected void refresh() {
		if (mViewParam.type.equals(TYPE_COMPLETE)) {// 完善资料
			tv_city_title.setVisibility(View.GONE);
			btn_city.setVisibility(View.GONE);
			tv_sign_title.setVisibility(View.GONE);
			et_sign.setVisibility(View.GONE);

			btn_submit.setEnabled(false);

			User user = UserMgr.getInstance().getLoginUser();
			et_nickname.setText(user.nickname);
			et_nickname.setSelection(et_nickname.getText().toString().length());
			if (user.gender == User.FEMALE) {
				btn_female.performClick();
			} else if (user.gender == User.MALE) {
				btn_male.performClick();
			}

			HostApplication.getInstance().getMainHandler().postDelayed(new Runnable() {

				@Override
				public void run() {
					KeyBoardUtil.showSoftKeyBoard(getActivity(), et_nickname);
				}
			}, 200);

		} else {// 编辑资料
			btn_submit.setVisibility(View.GONE);
			TextView tv_right = (TextView) getTitleBar().findViewById(R.id.tv_right);
			tv_right.setVisibility(View.VISIBLE);
			tv_right.setText(getString(R.string.confirm));
			btn_female.setEnabled(false);
			btn_male.setEnabled(false);

			User user = UserMgr.getInstance().getLoginUser();
			et_nickname.setText(user.nickname);
			et_nickname.setSelection(et_nickname.getText().toString().length());
			if (user.gender == User.FEMALE) {
				btn_female.performClick();
			} else {
				btn_male.performClick();
			}
			tv_edit_information.setText(R.string.user_sex_information_edit);
			currentDate = StringUtils.parseYMD(user.birthday);
			String result = user.birthday + "  " + StringUtils.getConstellation(currentDate);
			tv_birthday_tip.setVisibility(View.GONE);
			rl_birthday_tip.setVisibility(View.VISIBLE);
			tv_birthday.setText(result);

			refreshCollensationIcon();// 星座
			if (user.address != null && user.address.length() > 1) {
				refreshCity(user.address);
			} else {
				SharedPreferencesUtil spUtil = new SharedPreferencesUtil(getContext());
				String local = spUtil.get("local", "");
				refreshCity(local);
			}

			et_sign.setText(user.sign);

			HostApplication.getInstance().getMainHandler().postDelayed(new Runnable() {

				@Override
				public void run() {
					KeyBoardUtil.showSoftKeyBoard(getActivity(), et_nickname);
				}
			}, 200);

		}

	}

	private Date currentDate = null;
	private String cityName = null;
	private String latlng = null;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_male:// 性别男
			btn_male.setSelected(true);
			btn_female.setSelected(false);
			KeyBoardUtil.hideSoftKeyBoard(getActivity());

			refreshCollensationIcon();

			if (mViewParam.type.equals(TYPE_COMPLETE)) {
				refreshCompleteButton();
			} else {

			}
			break;
		case R.id.btn_female:// 性别女
			KeyBoardUtil.hideSoftKeyBoard(getActivity());
			btn_male.setSelected(false);
			btn_female.setSelected(true);

			refreshCollensationIcon();

			if (mViewParam.type.equals(TYPE_COMPLETE)) {
				refreshCompleteButton();
			} else {

			}
			break;
		case R.id.btn_city:// 定位城市
			// NavigationController.gotoLocationFragment(getContext());
			ViewParam vp = new ViewParam();
			vp.title = getString(R.string.user_city_location);
			startFragmentForResult(LocationFragment.class, vp, 1001);
			break;
		case R.id.btn_birthday:// 选择生日
			KeyBoardUtil.hideSoftKeyBoard(getActivity());
			BirthdayDialog bd = new BirthdayDialog(getActivity(), currentDate);
			bd.setOnDatePickListener(new OnDatePickListener() {

				@Override
				public void onDatePick(int year, int month, int day) {
					String str = year + "-" + month + "-" + day;
					currentDate = StringUtils.parseYMD(str);
					String result = str + "  " + StringUtils.getConstellation(currentDate);
					tv_birthday_tip.setVisibility(View.GONE);
					rl_birthday_tip.setVisibility(View.VISIBLE);
					tv_birthday.setText(result);

					refreshCollensationIcon();

					if (mViewParam.type.equals(TYPE_COMPLETE)) {
						refreshCompleteButton();
					} else {

					}

				}
			});
			bd.show();
			break;
		case R.id.btn_submit:// 完善资料提交
			completeSubmite();
			break;
		case R.id.tv_right:// 编辑资料提交
			modifyInfo();
			break;
		}

	}

	/**
	 * 刷新按钮状态
	 */
	private void refreshCompleteButton() {
		if (checkComplete()) {
			btn_submit.setEnabled(true);
		} else {
			btn_submit.setEnabled(false);
		}
	}

	private void refreshCity(String city) {
		if (!StringUtils.isEmpty(city)) {
			tv_city_tip.setVisibility(View.GONE);
//			iv_city_right_arrow.setVisibility(View.GONE);
			rl_city_tip.setVisibility(View.VISIBLE);
			tv_city.setText(city);
		} else {
			tv_city_tip.setVisibility(View.VISIBLE);
//			iv_city_right_arrow.setVisibility(View.VISIBLE);
			rl_city_tip.setVisibility(View.GONE);
		}
	}

	/**
	 * 刷新星座图标
	 */
	private void refreshCollensationIcon() {
		int gender = -1;
		if (btn_male.isSelected()) {
			gender = User.MALE;
		} else if (btn_female.isSelected()) {
			gender = User.FEMALE;
		}

		if (gender == -1) {
			gender = User.MALE;
		}

		if (currentDate == null) {
			return;
		}

		User user = new User();
		user.gender = gender;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		user.birthday = format.format(currentDate);
		iv_constellation.setUser(user);
	}

	/**
	 * 完善资料检查
	 */
	private boolean checkComplete() {
		String nickname = et_nickname.getText().toString().trim();

		if (StringUtils.isEmpty(nickname)) {
			return false;
		}

		int sex = -1;
		if (btn_male.isSelected()) {
			sex = User.MALE;
		} else if (btn_female.isSelected()) {
			sex = User.FEMALE;
		}

		if (sex == -1) {
			return false;
		}

		if (currentDate == null) {
			return false;
		}
		return true;
	}

	/**
	 * 完善资料
	 */
	private void completeSubmite() {
		final String nickname = et_nickname.getText().toString().trim();

		if (StringUtils.isEmpty(nickname)) {
			UIUtils.showToast(getResources().getString(R.string.account_nickname_null));
			return;
		}

		if (StringUtils.getLengthOfByteCode(nickname) > 16) {
			UIUtils.showToast(getResources().getString(R.string.account_nickname_too_long));
			return;
		}

		int sex = -1;
		if (btn_male.isSelected()) {
			sex = User.MALE;
		} else if (btn_female.isSelected()) {
			sex = User.FEMALE;
		}

		if (sex == -1) {
			UIUtils.showToast(getResources().getString(R.string.account_choose_sex));
			return;
		}

		if (currentDate == null) {
			UIUtils.showToast(getResources().getString(R.string.account_choose_birthday));
			return;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		final String birthday = format.format(currentDate);

		final int gender = sex;

		mUserPresent.updateUserInfo(nickname, null, gender, birthday, null, null, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					UserMgr.getInstance().getLoginUser().nickname = nickname;
					UserMgr.getInstance().getLoginUser().gender = gender;
					UserMgr.getInstance().getLoginUser().birthday = birthday;
					UserMgr.getInstance().saveLoginUser();
					NavigationController.gotoMainFragment(getContext());
				} else {
					UIUtils.showToast(msg);
				}

			}
		});

	}

	/**
	 * 修改资料
	 */
	private void modifyInfo() {
		final String nickname = et_nickname.getText().toString().trim();

		if (StringUtils.isEmpty(nickname)) {
			UIUtils.showToast(getResources().getString(R.string.account_nickname_null));
			return;
		}

		if (StringUtils.getLengthOfByteCode(nickname) > 16) {
			UIUtils.showToast(getResources().getString(R.string.account_nickname_too_long));
			return;
		}

		final int gender = UserMgr.getInstance().getLoginUser().gender;

		if (currentDate == null) {
			UIUtils.showToast(getResources().getString(R.string.account_choose_birthday));
			return;
		}

		final String sign = et_sign.getText().toString().trim();

		if (StringUtils.getLengthOfByteCode(sign) > 48) {
			UIUtils.showToast(getResources().getString(R.string.account_sign_too_long));
			return;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		final String birthday = format.format(currentDate);

		mUserPresent.updateUserInfo(nickname, sign, gender, birthday, cityName, latlng, new JSONResponse() {

			@Override
			public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {
				if (errCode == ErrorCode.ERROR_OK) {
					UserMgr.getInstance().getLoginUser().nickname = nickname;
					UserMgr.getInstance().getLoginUser().gender = gender;
					UserMgr.getInstance().getLoginUser().sign = sign;
					UserMgr.getInstance().getLoginUser().birthday = birthday;
					UserMgr.getInstance().saveLoginUser();
					getActivity().finish();
				} else {
					UIUtils.showToast(msg);
				}

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK && data != null) {
			this.cityName = data.getStringExtra("city");
			this.latlng = data.getStringExtra("latlng");
			SharedPreferencesUtil spUtil = new SharedPreferencesUtil(getContext());
			spUtil.setValueStr("local", cityName);
			refreshCity(cityName);
		}
	}

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "EditFragment";
	}

}
