package com.base.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.heihei.model.ShareInfo;
import com.wmlives.heihei.R;

public class ShareSdkUtils {
	public static final int MSG_USERID_FOUND = 1;
	public static final int MSG_LOGIN = 2;
	public static final int MSG_AUTH_CANCEL = 3;
	public static final int MSG_AUTH_ERROR = 4;
	public static final int MSG_AUTH_COMPLETE = 5;

	public static void init(Context context)
	{
	    ShareSDK.initSDK(context, "169277801c9a4");
	}
	
	public static void showWeiboShare(ShareInfo info, String defaultTitle, Context context,PlatformActionListener mListener) {
		SinaWeibo.ShareParams sp = new SinaWeibo.ShareParams();
		String text = context.getResources().getString(R.string.live_share_weibo_text);
		if (info.content != null) {
			sp.setText(info.title + info.shareUrl);
		} else {
			sp.setText(String.format(text, defaultTitle));
		}
		if (info.shareCover != null) {
			sp.setImageUrl(info.shareCover);
		}
		Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
		weibo.setPlatformActionListener(mListener);
		weibo.share(sp);
	}

	public static void showWechatShare(ShareInfo info, String defaultTitle, Context context,PlatformActionListener mListener) {
		Wechat.ShareParams sp = new Wechat.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		if (info.title != null) {
			sp.setTitle(info.title);
		} else {
			sp.setTitle(context.getResources().getString(R.string.live_share_title));
		}
		String text = context.getResources().getString(R.string.live_share_text);
		if (info.content != null) {
			sp.setText(info.content);
		} else {
			sp.setText(String.format(text, defaultTitle));
		}
		if (info.shareUrl != null) {
			sp.setUrl(info.shareUrl);
		} else {
			// no to do
		}
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		sp.setImageData(bitmap);
		Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
		wechat.setPlatformActionListener(mListener);
		wechat.share(sp);
	}

	public static void showWechatFriendShare(ShareInfo info, String defaultTitle, Context context,PlatformActionListener mListener) {
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		String title = context.getResources().getString(R.string.live_share_text);
		if (info.title != null) {
			sp.setTitle(info.title);
		} else {
			sp.setTitle(String.format(title, defaultTitle));
		}
		
		String text = context.getResources().getString(R.string.live_share_text);
        if (info.content != null) {
            sp.setText(info.content);
        } else {
            sp.setText(String.format(text, defaultTitle));
        }
		
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		sp.setImageData(bitmap);
		if (info.shareUrl != null) {
			sp.setUrl(info.shareUrl);
		} else {
			// no to do
		}
		Platform wechatMoments = ShareSDK.getPlatform(WechatMoments.NAME);
		wechatMoments.setPlatformActionListener(mListener);
		wechatMoments.share(sp);
	}
}
