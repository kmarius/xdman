package org.sdg.xdman.interceptor;

import java.util.ArrayList;

import org.sdg.xdman.util.XDMUtil;

public class DownloadIntercepterInfo {

	public String url;
	public String ua;
	public ArrayList<String> cookies;
	public String referer;
	public boolean noconfirm;

	public void copyTo(DownloadIntercepterInfo info) {
		info.url = new String(url.toCharArray());
		if (!XDMUtil.isNullOrEmpty(ua)) {
			info.ua = new String(ua.toCharArray());
		}
		if (!XDMUtil.isNullOrEmpty(referer)) {
			info.referer = new String(referer.toCharArray());
		}
		if (cookies != null) {
			info.cookies = new ArrayList<String>();
			info.cookies.addAll(cookies);
		}
		info.noconfirm = noconfirm;
	}

	
}