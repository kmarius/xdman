package org.sdg.xdman.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public abstract class AbstractAutoProxyHandler implements ProxyHandler {
	private BrowserProxyInfo bpi = null;
	protected StringBuffer autoProxyScript = null;
	protected String jsPacScript = null;

	public final boolean isSupported(int paramInt) {
		return paramInt == 2;
	}

	public final boolean isProxyCacheSupported() {
		return true;
	}

	protected abstract String getBrowserSpecificAutoProxy();

	public void init(BrowserProxyInfo paramBrowserProxyInfo) throws Exception {
		// Trace.msgNetPrintln("net.proxy.loading.auto");
		if (!isSupported(paramBrowserProxyInfo.getType())) {
			throw new Exception("Unable to support proxy type: "
					+ paramBrowserProxyInfo.getType());
		}
		this.bpi = paramBrowserProxyInfo;
		this.autoProxyScript = new StringBuffer();
		this.autoProxyScript
				.append("var _mon = new Array('JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC');\nvar _day = new Array('SUN', 'MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT');\nfunction _isGmt(i) {\n return typeof i == 'string' && i == 'GMT'; }");
		this.autoProxyScript
				.append("function dnsDomainIs(host, domain) {\nif (domain != null && domain.charAt(0) != '.')\nreturn shExpMatch(host, domain);\nreturn shExpMatch(host, '*' + domain); }");
		this.autoProxyScript
				.append("function isPlainHostName(host){\nreturn (dnsDomainLevels(host) == 0); }");
		this.autoProxyScript
				.append("function convert_addr(ipchars) {\n    var bytes = ipchars.split('.');\n    var result = ((bytes[0] & 0xff) << 24) |\n                 ((bytes[1] & 0xff) << 16) |\n                 ((bytes[2] & 0xff) <<  8) |\n                  (bytes[3] & 0xff);\n    return result;\n}\n");
		this.autoProxyScript
				.append("function isInNet(ipaddr, pattern, maskstr) {\n    var ipPattern = /^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$/;\n    var test = ipaddr.match(ipPattern);\n    if (test == null) {\n        ipaddr = dnsResolve(ipaddr);\n        if (ipaddr == null)\n            return false;\n    } else if ((test[1] > 255) || (test[2] > 255) || \n               (test[3] > 255) || (test[4] > 255) ) {\n        return false;\n    }\n    var host = convert_addr(ipaddr);\n    var pat  = convert_addr(pattern);\n    var mask = convert_addr(maskstr);\n    return ((host & mask) == (pat & mask));\n    \n}\n");
		this.autoProxyScript.append(getBrowserSpecificAutoProxy());
		this.autoProxyScript
				.append("function isResolvable(host){\nreturn (dnsResolve(host) != ''); }");
		this.autoProxyScript
				.append("function localHostOrDomainIs(host, hostdom){\nreturn shExpMatch(hostdom, host + '*'); }");
		this.autoProxyScript
				.append("function dnsDomainLevels(host){\nvar s = host + '';\nfor (var i=0, j=0; i < s.length; i++)\nif (s.charAt(i) == '.')\nj++;\nreturn j; }");
		this.autoProxyScript.append("function myIpAddress(){\nreturn '");
		try {
			InetAddress localInetAddress = InetAddress.getLocalHost();
			this.autoProxyScript.append(localInetAddress.getHostAddress());
		} catch (Throwable localThrowable) {
			localThrowable.printStackTrace();
			this.autoProxyScript.append("127.0.0.1");
		}
		this.autoProxyScript.append("'; }");
		this.autoProxyScript
				.append("function shExpMatch(str, shexp){ \n if (typeof str != 'string' || typeof shexp != 'string') return false; \n if (shexp == '*') return true; \n if (str == '' && shexp == '') return true; \n str = str.toLowerCase();\n shexp = shexp.toLowerCase();\n var index = shexp.indexOf('*');\n if (index == -1) { return (str == shexp); } \n else if (index == 0) { \n for (var i=0; i <= str.length; i++) { \n if (shExpMatch(str.substring(i), shexp.substring(1))) return true; \n } return false; } \nelse { \nvar sub = null, sub2 = null; \nsub = shexp.substring(0, index);\nif (index <= str.length) sub2 = str.substring(0, index); \nif (sub != '' && sub2 != '' && sub == sub2) { \nreturn shExpMatch(str.substring(index), shexp.substring(index)); }\nelse { return false; }\n} }");
		this.autoProxyScript
				.append("function _dateRange(day1, month1, year1, day2, month2, year2, gmt){\nif (typeof day1 != 'number' || day1 <= 0 || typeof month1 != 'string' || typeof year1 != 'number' || year1 <= 0\n || typeof day2 != 'number' || day2 <= 0 || typeof month2 != 'string' || typeof year2 != 'number' || year2 <= 0\n || typeof gmt != 'boolean') return false; \nvar m1 = -1, m2 = -1;\nfor (var i=0; i < _mon.length; i++){\nif (_mon[i] == month1)\nm1 = i;\nif (_mon[i] == month2)\nm2 = i;\n}\nvar cur = new Date();\nvar d1 = new Date(year1, m1, day1, 0, 0, 0);\nvar d2 = new Date(year2, m2, day2, 23, 59, 59);\nif (gmt == true)\ncur = new Date(cur.getTime() - cur.getTimezoneOffset() * 60 * 1000);\nreturn ((d1.getTime() <= cur.getTime()) && (cur.getTime() <= d2.getTime()));\n}\nfunction dateRange(p1, p2, p3, p4, p5, p6, p7){\nvar cur = new Date();\nif (typeof p1 == 'undefined')\nreturn false;\nelse if (typeof p2 == 'undefined' || _isGmt(p2))\n{\nif ((typeof p1) == 'string')\nreturn _dateRange(1, p1, cur.getFullYear(), 31, p1, cur.getFullYear(), _isGmt(p2));\nelse if (typeof p1 == 'number' && p1 > 31)\nreturn _dateRange(1, 'JAN', p1, 31, 'DEC', p1, _isGmt(p2));\nelse {\nfor (var i=0; i < _mon.length; i++)\nif (_dateRange(p1, _mon[i], cur.getFullYear(), p1, _mon[i], cur.getFullYear(), _isGmt(p2)))\n return true;\nreturn false;\n}\n}\nelse if (typeof p3 == 'undefined' || _isGmt(p3))\n{\nif ((typeof p1) == 'string')\nreturn _dateRange(1, p1, cur.getFullYear(), 31, p2, cur.getFullYear(), _isGmt(p3));\nelse if (typeof p1 == 'number' && typeof p2 == 'number' && (p1 > 31 || p2 > 31))\nreturn _dateRange(1, 'JAN', p1, 31, 'DEC', p2, _isGmt(p3));\nelse \n{\nif ((typeof p2) == 'string')\n{\nreturn _dateRange(p1, p2, cur.getFullYear(), p1, p2, cur.getFullYear(), _isGmt(p3));\n}\nelse \n{\nfor (var i=0; i < _mon.length; i++)\nif (_dateRange(p1, _mon[i], cur.getFullYear(), p2, _mon[i], cur.getFullYear(), _isGmt(p3)))\nreturn true;\nreturn false;\n}\n}\n}\nelse if (typeof p4 == 'undefined' || _isGmt(p4))\nreturn _dateRange(p1, p2, p3, p1, p2, p3, _isGmt(p4));\nelse if (typeof p5 == 'undefined' || _isGmt(p5))\n{\nif (typeof p2 == 'number')\nreturn _dateRange(1, p1, p2, 31, p3, p4, _isGmt(p5));\nelse \nreturn _dateRange(p1, p2, cur.getFullYear(), p3, p4, cur.getFullYear(), _isGmt(p5))\n}\nelse if (typeof p6 == 'undefined')\nreturn false;\nelse \nreturn _dateRange(p1, p2, p3, p4, p5, p6, _isGmt(p7));\n}");
		this.autoProxyScript
				.append("function timeRange(p1, p2, p3, p4, p5, p6, p7) {\nif (typeof p1 == 'undefined')\nreturn false;\nelse if (typeof p2 == 'undefined' || _isGmt(p2))\nreturn _timeRange(p1, 0, 0, p1, 59, 59, _isGmt(p2));\nelse if (typeof p3 == 'undefined' || _isGmt(p3))\nreturn _timeRange(p1, 0, 0, p2, 0, 0, _isGmt(p3));\nelse if (typeof p4 == 'undefined')\nreturn false;\nelse if (typeof p5 == 'undefined' || _isGmt(p5))\nreturn _timeRange(p1, p2, 0, p3, p4, 0, _isGmt(p5));\nelse if (typeof p6 == 'undefined')\nreturn false;\nelse \nreturn _timeRange(p1, p2, p3, p4, p5, p6, _isGmt(p7));\n}\nfunction _timeRange(hour1, min1, sec1, hour2, min2, sec2, gmt) {\nif (typeof hour1 != 'number' || typeof min1 != 'number' || typeof sec1 != 'number' \n|| hour1 < 0 || min1 < 0 || sec1 < 0 \n|| typeof hour2 != 'number' || typeof min2 != 'number' || typeof sec2 != 'number' \n|| hour2 < 0 || min2 < 0 || sec2 < 0 \n|| typeof gmt != 'boolean')  return false; \nvar cur = new Date();\nvar d1 = new Date();\nvar d2 = new Date();\nd1.setHours(hour1);\nd1.setMinutes(min1);\nd1.setSeconds(sec1);\nd2.setHours(hour2);\nd2.setMinutes(min2);\nd2.setSeconds(sec2);\nif (gmt == true)\ncur = new Date(cur.getTime() - cur.getTimezoneOffset() * 60 * 1000);\nreturn ((d1.getTime() <= cur.getTime()) && (cur.getTime() <= d2.getTime()));\n}");
		this.autoProxyScript
				.append("function weekdayRange(wd1, wd2, gmt){\nif (typeof wd1 == 'undefined') \nreturn false;\nelse if (typeof wd2 == 'undefined' || _isGmt(wd2)) \nreturn _weekdayRange(wd1, wd1, _isGmt(wd2)); \nelse \nreturn _weekdayRange(wd1, wd2, _isGmt(gmt)); }\nfunction _weekdayRange(wd1, wd2, gmt) {\nif (typeof wd1 != 'string' || typeof wd2 != 'string' || typeof gmt != 'boolean') return false; \nvar w1 = -1, w2 = -1;\nfor (var i=0; i < _day.length; i++) {\nif (_day[i] == wd1)\nw1 = i;\nif (_day[i] == wd2)\nw2 = i; }\nvar cur = new Date();\nif (gmt == true)\ncur = new Date(cur.getTime() - cur.getTimezoneOffset() * 60 * 1000);\nvar w3 = cur.getDay();\nif (w1 > w2)\nw2 = w2 + 7;\nif (w1 > w3)\nw3 = w3 + 7;\nreturn (w1 <= w3 && w3 <= w2); }");
		this.autoProxyScript.append(" function alert() {} ");
		URL localURL1 = null;
		try {
			localURL1 = new URL(this.bpi.getAutoConfigURL());
		} catch (MalformedURLException localMalformedURLException) {
			throw new Exception("Auto config URL is malformed");
		}
		URLConnection localURLConnection = getDirectURLConnection(localURL1);
		if (localURLConnection != null) {
			if (isSupportedINSFile(localURLConnection) == true) {
				URL localURL2 = getAutoConfigURLFromINS(localURLConnection);
				localURLConnection = getDirectURLConnection(localURL2);
			}
			this.jsPacScript = getJSFileFromURL(localURLConnection);
			this.autoProxyScript.append(this.jsPacScript);
			// Trace.msgNetPrintln("net.proxy.loading.done");
		}
	}

	private static boolean isSupportedINSFile(URLConnection paramURLConnection) {
		boolean bool = false;
		String str1 = paramURLConnection.getURL().getFile();
		if ((str1 != null) && (str1.toLowerCase().endsWith(".ins") == true)) {
			bool = true;
		} else if (paramURLConnection != null) {
			String str2 = paramURLConnection.getContentType();
			bool = "application/x-internet-signup".equalsIgnoreCase(str2);
		}
		return bool;
	}

	public abstract ProxyInfo[] getProxyInfo(URL paramURL) throws Exception;

	private URLConnection getDirectURLConnection(URL paramURL) throws Exception {
		URLConnection localURLConnection = null;
		if (paramURL == null) {
			return null;
		}
		try {
			String str1 = paramURL.getProtocol();
			if (str1.equals("file")) {
				String str2 = paramURL.toExternalForm();
				int i = str2.indexOf('/');
				if (i == -1) {
					throw new Exception("Malformed URL specified:" + paramURL);
				}
				while (str2.charAt(++i) == '/') {
				}
				URL localURL = new URL("file:/" + str2.substring(i));
				localURLConnection = localURL.openConnection();
			} else {
				try {
					localURLConnection = paramURL
							.openConnection(Proxy.NO_PROXY);
				} catch (NoClassDefFoundError localNoClassDefFoundError) {
					localURLConnection = paramURL.openConnection();
				}
			}
		} catch (IOException localIOException) {
			throw new Exception("Unable to obtain a connection from "
					+ paramURL, localIOException);
		}
		return localURLConnection;
	}

	private URL getAutoConfigURLFromINS(URLConnection paramURLConnection)
			throws Exception {
		URL localURL = null;
		// Trace.msgNetPrintln("net.proxy.auto.download.ins", new Object[] {
		// paramURLConnection.getURL() });
		String str = null;
		try {
			INIFile localINIFile = new INIFile(paramURLConnection
					.getInputStream());
			str = localINIFile.readString("URL", "AutoConfigJSURL");
			if (str != null) {
				localURL = new URL(str);
			} else {
				throw new Exception(
						"Unable to locate 'AutoConfigJSURL' in INS file");
			}
		} catch (Exception localProxyConfigException) {
			throw localProxyConfigException;
		}
		return localURL;
	}

	private String getJSFileFromURL(URLConnection paramURLConnection)
			throws Exception {
		// Trace.msgNetPrintln("net.proxy.auto.download.js", new Object[] {
		// paramURLConnection.getURL() });
		try {
			// RemoveCommentReader localRemoveCommentReader = new
			// RemoveCommentReader(new
			// InputStreamReader(paramURLConnection.getInputStream()));
			InputStreamReader localRemoveCommentReader = new InputStreamReader(
					paramURLConnection.getInputStream());
			BufferedReader localBufferedReader = new BufferedReader(
					localRemoveCommentReader);
			StringWriter localStringWriter = new StringWriter();
			char[] arrayOfChar = new char[100];
			int i;
			while ((i = localBufferedReader.read(arrayOfChar)) != -1) {
				localStringWriter.write(arrayOfChar, 0, i);
			}
			localBufferedReader.close();
			localRemoveCommentReader.close();
			localStringWriter.close();
			return localStringWriter.toString();
		} catch (Throwable localThrowable) {
			throw new Exception("Unable to obtain auto proxy file from "
					+ paramURLConnection.getURL(), localThrowable);
		}
	}

	protected final ProxyInfo[] extractAutoProxySetting(String paramString) {
		if (paramString != null) {
			StringTokenizer localStringTokenizer = new StringTokenizer(
					paramString, ";", false);
			ProxyInfo[] arrayOfProxyInfo = new ProxyInfo[localStringTokenizer
					.countTokens()];
			int i = 0;
			while (localStringTokenizer.hasMoreTokens()) {
				String str = localStringTokenizer.nextToken();
				int j = str.indexOf("PROXY");
				if (j != -1) {
					arrayOfProxyInfo[(i++)] = new ProxyInfo(str
							.substring(j + 6));
				} else {
					j = str.indexOf("SOCKS");
					if (j != -1) {
						arrayOfProxyInfo[(i++)] = new ProxyInfo(null, str
								.substring(j + 6));
					} else {
						arrayOfProxyInfo[(i++)] = new ProxyInfo(null, -1);
					}
				}
			}
			return arrayOfProxyInfo;
		}
		return new ProxyInfo[] { new ProxyInfo(null) };
	}

	private static final class INIFile {
		private InputStream _inputStream;

		public INIFile(InputStream paramInputStream) {
			this._inputStream = paramInputStream;
		}

		private String readString(String paramString1, String paramString2)
				throws IOException {
			String str1 = null;
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(this._inputStream));
			try {
				int i = 0;
				String str2 = null;
				paramString1 = "[" + paramString1 + "]";
				paramString2 = paramString2 + "=";
				do {
					str2 = localBufferedReader.readLine();
					if (str2 != null) {
						if (i == 1) {
							if ((str2.indexOf("[") == 0)
									&& (str2.indexOf("]") > 0)) {
								i = 0;
								break;
							}
							if (str2.indexOf(paramString2) == 0) {
								str1 = str2.substring(paramString2.length());
								break;
							}
						} else if (str2.indexOf(paramString1) == 0) {
							i = 1;
						}
					}
				} while (str2 != null);
			} finally {
				if (localBufferedReader != null) {
					localBufferedReader.close();
				}
			}
			return str1;
		}
	}
}
