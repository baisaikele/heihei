package com.heihei.fragment.setting;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.base.host.ActivityManager;
import com.base.host.AppLogic;
import com.base.host.BaseFragment;
import com.base.host.NavigationController;
import com.base.utils.FileUtils;
import com.heihei.dialog.BaseDialog.BaseDialogOnclicklistener;
import com.heihei.dialog.TipDialog;
import com.heihei.fragment.MainFragment;
import com.heihei.logic.UserMgr;
import com.wmlives.heihei.R;

public class SettingFragment extends BaseFragment implements OnClickListener {

    // ----------------R.layout.fragment_setting-------------Start
    private RelativeLayout titlebar;// include R.layout.title_bar
    private LinearLayout ll_left;// include R.layout.title_bar
    private ImageButton iv_back;// include R.layout.title_bar
    private TextView tv_back;// include R.layout.title_bar
    private LinearLayout ll_right;// include R.layout.title_bar
    private ImageButton iv_right;// include R.layout.title_bar
    private TextView tv_right;// include R.layout.title_bar
    private LinearLayout ll_mid;// include R.layout.title_bar
    private TextView tv_title;// include R.layout.title_bar
    private RelativeLayout btn_black;
    private RelativeLayout btn_clear_memory;
    private ImageView iv_memory_right;
    private TextView tv_memory_size;
    // private RelativeLayout btn_comment;
    private RelativeLayout btn_about;
    private Button btn_unlogin;

    public void autoLoad_fragment_setting() {
        titlebar = (RelativeLayout) findViewById(R.id.titlebar);// title_bar
        ll_left = (LinearLayout) findViewById(R.id.ll_left);// title_bar
        iv_back = (ImageButton) findViewById(R.id.iv_back);// title_bar
        tv_back = (TextView) findViewById(R.id.tv_back);// title_bar
        ll_right = (LinearLayout) findViewById(R.id.ll_right);// title_bar
        iv_right = (ImageButton) findViewById(R.id.iv_right);// title_bar
        tv_right = (TextView) findViewById(R.id.tv_right);// title_bar
        ll_mid = (LinearLayout) findViewById(R.id.ll_mid);// title_bar
        tv_title = (TextView) findViewById(R.id.tv_title);// title_bar
        btn_black = (RelativeLayout) findViewById(R.id.btn_black);
        btn_clear_memory = (RelativeLayout) findViewById(R.id.btn_clear_memory);
        iv_memory_right = (ImageView) findViewById(R.id.iv_memory_right);
        tv_memory_size = (TextView) findViewById(R.id.tv_memory_size);
        // btn_comment = (RelativeLayout) findViewById(R.id.btn_comment);
        btn_about = (RelativeLayout) findViewById(R.id.btn_about);
        btn_unlogin = (Button) findViewById(R.id.btn_unlogin);
    }

    // ----------------R.layout.fragment_setting-------------End

    @Override
    protected void loadContentView() {
        setContentView(R.layout.fragment_setting);
    }

    @Override
    protected String initTitle() {
        return getString(R.string.me_setting);
    }

    @Override
    protected void viewDidLoad() {
        autoLoad_fragment_setting();
        btn_black.setOnClickListener(this);
        btn_clear_memory.setOnClickListener(this);
        // btn_comment.setOnClickListener(this);
        btn_about.setOnClickListener(this);
        btn_unlogin.setOnClickListener(this);
    }

    @Override
    protected void refresh() {
        String path = AppLogic.defaultCache;
        File file = new File(path);
        long size = FileUtils.getDirSize(file);
        String sizeStr = FileUtils.formatFileSize(size);
        tv_memory_size.setText(sizeStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_about :// 关于我们
            NavigationController.gotoAboutFragment(getContext());
            break;
        case R.id.btn_clear_memory :// 清理缓存
        {
            TipDialog td = new TipDialog(getActivity());
            td.setContent("确定清除缓存？");
            td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

                @Override
                public void onOkClick(Dialog dialog) {
                    String path = AppLogic.defaultCache;
                    FileUtils.deleteDirectory(path);
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    tv_memory_size.setText("0MB");
                }

                @Override
                public void onCancleClick(Dialog dialog) {
                    // TODO Auto-generated method stub

                }
            });
            td.show();
        }
            break;
        // case R.id.btn_comment :// 给黑黑好评
        // break;
        case R.id.btn_black :// 黑名单
            NavigationController.gotoBlackListFragment(getContext());
            break;
        case R.id.btn_unlogin :// 退出登录
        {
            TipDialog td = new TipDialog(getActivity());
            td.setContent(getResources().getString(R.string.user_unlogin_declare));
            td.setBaseDialogOnclicklistener(new BaseDialogOnclicklistener() {

                @Override
                public void onOkClick(Dialog dialog) {
                    UserMgr.getInstance().unlogin();
                    NavigationController.gotoLogin(getContext());

                }

                @Override
                public void onCancleClick(Dialog dialog) {
                    // TODO Auto-generated method stub

                }
            });
            td.show();
        }
            break;
        }

    }

	@Override
	public String getCurrentFragmentName() {
		// TODO Auto-generated method stub
		return "SettingFragment";
	}

}
