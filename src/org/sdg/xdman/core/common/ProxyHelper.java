package org.sdg.xdman.core.common;

import java.net.URL;

import org.sdg.xdman.util.PACUtil;
import org.sdg.xdman.util.ProxyInfo;
import org.sdg.xdman.util.XDMUtil;


public class ProxyHelper {
	public static WebProxy getProxyForURL(String url, XDMConfig config) {
		if (url == null) {
			return null;
		}

		if (config.useProxyPAC) {
			ProxyInfo pi = PACUtil.getProxyForURL(url, config.proxyPAC);
			if (XDMUtil.isNullOrEmpty(pi.getProxy())) {
				return null;
			}
			WebProxy wp = new WebProxy();
			wp.host = pi.getProxy();
			wp.port = pi.getPort();
			System.out.println("PROXY_HOST: " + wp.host + " PROXY_PORT: " + wp.port);
			return wp;
		}

		if (config.useProxy) {
			String host = config.proxyHost;
			int port = config.proxyPort;
			if (!XDMUtil.isNullOrEmpty(host)) {
				if (port < 0) {
					port = 80;
				}
				WebProxy wp = new WebProxy();
				wp.host = host;
				wp.port = port;
				return wp;
			}
		}

		return null;
	}

	public static WebProxy getProxyForURL(URL url, XDMConfig config) {
		return getProxyForURL(url + "", config);
	}
}
