package com.heihei.dialog;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.base.http.ErrorCode;
import com.base.http.JSONResponse;
import com.base.utils.PackageUtils;
import com.base.utils.ShareSdkUtils;
import com.base.utils.UIUtils;
import com.heihei.logic.present.LivePresent;
import com.heihei.model.ShareInfo;
import com.wmlives.heihei.R;

public class ShareDialog extends BaseDialog implements android.view.View.OnClickListener {

    private String mLiveName = getContext().getResources().getString(R.string.app_name);

    private String liveId = "";

    public ShareDialog(Context context, String liveId) {
        super(context, R.style.ActionSheet);
        this.liveId = liveId;
        setOwnerActivity((Activity) context);
        Window w = getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        w.getDecorView().setPadding(0, 0, 0, 0);
        lp.gravity = Gravity.BOTTOM;
        lp.width = context.getResources().getDisplayMetrics().widthPixels;
        w.setAttributes(lp);
    }

    // ----------------R.layout.dialog_share-------------Start
    private View btn_wx;
    private View btn_friend;
    private View btn_sina;

    public void autoLoad_dialog_share() {
        btn_wx = (View) findViewById(R.id.btn_wx);
        btn_friend = (View) findViewById(R.id.btn_friend);
        btn_sina = (View) findViewById(R.id.btn_sina);
    }

    // ----------------R.layout.dialog_share-------------End

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ShareSDK.initSDK(getContext(), "169277801c9a4");
        setContentView(R.layout.dialog_share);
        autoLoad_dialog_share();
        btn_wx.setOnClickListener(this);
        btn_friend.setOnClickListener(this);
        btn_sina.setOnClickListener(this);
        getShareInfo(liveId);
    }

    private static final int SHARE_UNKNOWN = -1;
    private static final int SHARE_WEIXIN = 0;
    private static final int SHARE_FRIEND = 1;
    private static final int SHARE_WEIBO = 2;

    private SparseArray<ShareInfo> mShareInfos = new SparseArray<>();

    public void getShareInfo(final String liveID) {
        new LivePresent().shareLive(liveID, new JSONResponse() {

            @Override
            public void onJsonResponse(JSONObject json, int errCode, String msg, boolean cached) {

                if (errCode == ErrorCode.ERROR_OK) {
                    JSONObject shareJ = json.optJSONObject("share");
                    if (shareJ != null) {
                        // if (shareType == SHARE_WEIBO) {
                        JSONObject weiboJ = shareJ.optJSONObject("weibo");
                        ShareInfo weiboInfo = new ShareInfo();
                        weiboInfo.content = weiboJ.optString("content");
                        weiboInfo.shareUrl = weiboJ.optString("url");
                        weiboInfo.shareCover = weiboJ.optString("coverUrl");
                        weiboInfo.title = weiboJ.optString("title");
                        mShareInfos.put(SHARE_WEIBO, weiboInfo);

                        // } else if (shareType == SHARE_WEIXIN) {
                        JSONObject weixinJ = shareJ.optJSONObject("wechat");
                        ShareInfo weixiInfo = new ShareInfo();
                        weixiInfo.content = weiboJ.optString("content");
                        weixiInfo.shareUrl = weiboJ.optString("url");
                        weixiInfo.shareCover = weiboJ.optString("coverUrl");
                        weixiInfo.title = weiboJ.optString("title");
                        mShareInfos.put(SHARE_WEIXIN, weixiInfo);

                        // } else if (shareType == SHARE_FRIEND) {
                        JSONObject friendJ = shareJ.optJSONObject("friend");
                        ShareInfo friendInfo = new ShareInfo();
                        friendInfo.content = weiboJ.optString("content");
                        friendInfo.shareUrl = weiboJ.optString("url");
                        friendInfo.shareCover = weiboJ.optString("coverUrl");
                        friendInfo.title = weiboJ.optString("title");
                        mShareInfos.put(SHARE_FRIEND, friendInfo);

                        // }
                    } else {
                        UIUtils.showToast("未获取到分享信息");
                        return;
                    }

                } else {

                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btn_wx :
            if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
                ShareInfo info = mShareInfos.get(SHARE_WEIXIN);
                if (info != null) {
                    ShareSdkUtils.showWechatShare(info, "", getContext(), null);
                }
            } else UIUtils.showToast(R.string.share_wechat_no_avliible);
            dismiss();
            break;
        case R.id.btn_friend :
            if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WECHAT)) {
                ShareInfo info = mShareInfos.get(SHARE_FRIEND);
                if (info != null) {
                    ShareSdkUtils.showWechatFriendShare(info, "", getContext(), null);
                }
            } else UIUtils.showToast(R.string.share_wechat_no_avliible);
            dismiss();
            break;
        case R.id.btn_sina :
            if (PackageUtils.isPackageInstalled(getContext(), PackageUtils.PKGName.PKGNAME_WEIBO)) {
                ShareInfo info = mShareInfos.get(SHARE_WEIBO);
                if (info != null) {
                    ShareSdkUtils.showWeiboShare(info, "", getContext(), null);
                }
            } else UIUtils.showToast(R.string.share_weibo_no_avliible);
            dismiss();
            break;
        }

    }
}
