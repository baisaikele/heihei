package com.heihei.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.NavigationController;
import com.base.utils.DeviceInfoUtils;
import com.facebook.fresco.FrescoImageHelper;
import com.heihei.fragment.live.widget.AvatarImageView;
import com.heihei.logic.UserMgr;
import com.wmlives.heihei.R;

public class MeHeader extends LinearLayout implements OnClickListener {

    // ----------------R.layout.header_me-------------Start
    private RelativeLayout rl_top;
    private AvatarImageView iv_avatar;
    private TextView tv_level;
    private TextView tv_nickname;
    private View mid_line;
    private TextView tv_follow;
    private TextView tv_fans;
    private TextView tv_sign;
    private View bolt;
    private RelativeLayout btn_money;
    private TextView tv_money_num;
    private RelativeLayout btn_ticker;
    private TextView tv_ticker_num;

    public void autoLoad_header_me() {
        rl_top = (RelativeLayout) findViewById(R.id.rl_top);
        iv_avatar = (AvatarImageView) findViewById(R.id.iv_avatar);
        tv_level = (TextView) findViewById(R.id.tv_level);
        tv_nickname = (TextView) findViewById(R.id.tv_nickname);
        mid_line = findViewById(R.id.mid_line);
        tv_follow = (TextView) findViewById(R.id.tv_follow);
        tv_fans = (TextView) findViewById(R.id.tv_fans);
        tv_sign = (TextView) findViewById(R.id.tv_sign);
        bolt = findViewById(R.id.bolt);
        btn_money = (RelativeLayout) findViewById(R.id.btn_money);
        tv_money_num = (TextView) findViewById(R.id.tv_money_num);
        btn_ticker = (RelativeLayout) findViewById(R.id.btn_ticker);
        tv_ticker_num = (TextView) findViewById(R.id.tv_ticker_num);
    }

    // ----------------R.layout.header_me-------------End

    public MeHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        autoLoad_header_me();
        btn_ticker.setOnClickListener(this);
        btn_money.setOnClickListener(this);
        rl_top.setOnClickListener(this);
        tv_follow.setOnClickListener(this);
        tv_fans.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LayoutParams params = (LayoutParams) rl_top.getLayoutParams();
            params.topMargin += DeviceInfoUtils.getStatusBarHeight(getContext());
            rl_top.setLayoutParams(params);
        }

    }


    /**
     * 2018/11/16
     * zhangyu
     * 添加suppresslint   解决 tv_follow引用报红线
     * **/
    @SuppressLint("StringFormatMatches")
    public void setData() {
        iv_avatar.setUser(UserMgr.getInstance().getLoginUser());
        tv_nickname.setText(UserMgr.getInstance().getLoginUser().nickname);
        tv_level.setText("LV." + UserMgr.getInstance().getLoginUser().level);
        tv_follow.setText(getResources().getString(R.string.me_follow_num,
                UserMgr.getInstance().getLoginUser().followingCount));
        tv_fans.setText(getResources().getString(R.string.me_fans_num, UserMgr.getInstance().getLoginUser().fansCount));
        tv_sign.setText(UserMgr.getInstance().getLoginUser().sign);
        tv_money_num.setText(String.valueOf(UserMgr.getInstance().getLoginUser().goldCount));
        tv_ticker_num.setText(String.valueOf(UserMgr.getInstance().getLoginUser().point));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ticker:// 我的黑票
                NavigationController.gotoIncomeFragment(getContext());
                break;
            case R.id.btn_money:// 我的钻
                NavigationController.gotoMyDiamondFragment(getContext());
                break;
            case R.id.rl_top:// 编辑资料
                NavigationController.gotoEditFragment(getContext());
                break;
            case R.id.tv_follow:// 我的关注
                NavigationController.gotoFollowListFragment(getContext(), UserMgr.getInstance().getUid(), null);
                break;
            case R.id.tv_fans:// 我的粉丝
                NavigationController.gotoFansListFragment(getContext(), UserMgr.getInstance().getUid(), null);
                break;
        }

    }

}
