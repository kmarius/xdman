package org.sdg.xdman.util;

import java.net.URL;



public class PACUtil {
	
	
	
	private static AbstractAutoProxyHandler handler;

	private static void init(String pacURL) throws Exception {
		BrowserProxyInfo b = new BrowserProxyInfo();
		b.setType(2);
		b.setAutoConfigURL(pacURL);
		handler = new XDMAutoProxyHandler();
		handler.init(b);

	}

	public static ProxyInfo getProxyForURL(String ustr, String pacURL) {
		try {
			if (handler == null) {
				init(pacURL);
			}

			URL url = new URL(ustr);
			ProxyInfo[] ps = handler.getProxyInfo(url);
			if (ps == null || ps.length == 0) {
				return null;
			} else {
				return ps[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	

}
