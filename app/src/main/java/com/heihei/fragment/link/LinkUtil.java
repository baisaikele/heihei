package com.heihei.fragment.link;

import com.base.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class LinkUtil {

	public static final String LINK_PREFIX = "wmlives_heihei://";

	public static boolean handleLink(Context context, String url, String desc) {
		if (StringUtils.isEmpty(url)) {
			return false;
		}
		if (url.startsWith(LINK_PREFIX)) {
			Uri uri = Uri.parse(url);
			String host = uri.getHost();
			if (StringUtils.isEmpty(host)) {
				return false;
			}

			if (!checkHost(host)) {
				return false;
			}

			Intent intent = new Intent(context, OutLinkActivity.class);
			intent.setData(Uri.parse(url));
			intent.putExtra("title", desc);
			context.startActivity(intent);
			return true;
		}
		return false;
	}

	public static void handleJYLink(Context context, String url) {
		if (StringUtils.isEmpty(url)) {
			return;
		}
		if (url.startsWith(LINK_PREFIX)) {
			Intent intent = new Intent(context, OutLinkActivity.class);
			intent.setData(Uri.parse(url));
			context.startActivity(intent);
		}
	}

	/**
	 * 检查host是否是客户端支持的类型
	 * 
	 * @param host
	 * @return
	 */
	public static boolean checkHost(String host) {
		boolean isSupport = false;
		try {
			LinkHost[] lhs = LinkHost.values();
			if (lhs != null) {
				for (LinkHost lh : lhs) {
					if (lh.host.equalsIgnoreCase(host)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			isSupport = false;
		}

		return isSupport;
	}

	public static enum LinkHost {

		WEBVIEW("webview"), // webview
		LIVE("live"), // 直播间
		MESSAGE("message"), // 首页消息显示
		HOMEPAGE("homepage"), // 首页
		USER("user"), // 用户
		REPLAY("replay"), // 回放
		CHAT("acceptchat"),// 接收chat
		DUECHAT("duechat");//反约

		private String host;

		LinkHost(String host) {
			this.host = host;
		}

		public String getHost() {
			return this.host;
		}

	}

}
