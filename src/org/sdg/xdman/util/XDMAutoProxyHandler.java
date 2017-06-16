package org.sdg.xdman.util;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class XDMAutoProxyHandler extends AbstractAutoProxyHandler {
	private ScriptEngineManager manager = null;
	private ScriptEngine engine = null;
	private static final String jsDnsResolve = "    function dnsResolve(host){\n                       return String(obj.dnsResolve(host));\n     }";

	public String dnsResolve(String paramString) {
		String str = "";
		try {
			str = InetAddress.getByName(paramString).getHostAddress();
		} catch (UnknownHostException localUnknownHostException) {
			localUnknownHostException.printStackTrace();
		}
		return str;
	}

	protected String getBrowserSpecificAutoProxy() {
		return "    function dnsResolve(host){\n                       return String(obj.dnsResolve(host));\n     }";
	}

	public final void init(BrowserProxyInfo paramBrowserProxyInfo)
			throws Exception {
		super.init(paramBrowserProxyInfo);
		this.manager = new ScriptEngineManager(null);
		this.engine = this.manager.getEngineByName("js");
	}

	public ProxyInfo[] getProxyInfo(URL paramURL) throws Exception {
		ProxyInfo[] arrayOfProxyInfo = null;
		if (this.engine != null) {
			try {
				this.engine.put("obj", this);
				//System.out.println(this.autoProxyScript.toString());
				this.engine.eval(this.autoProxyScript.toString());
				if ((this.engine instanceof Invocable)) {
					Invocable localInvocable = (Invocable) this.engine;
					Object localObject = localInvocable.invokeFunction(
							"FindProxyForURL", new Object[] {
									paramURL.toString(), paramURL.getHost() });
					arrayOfProxyInfo = extractAutoProxySetting((String) localObject);
				}
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
		// if (arrayOfProxyInfo == null) {
		// arrayOfProxyInfo = fallbackGetProxyInfo(paramURL);
		// }
		return arrayOfProxyInfo;
	}

//	ProxyInfo[] fallbackGetProxyInfo(URL paramURL) {
//		try {
//			String str1 = null;
//			if (this.jsPacScript != null) {
//				StringTokenizer localStringTokenizer = new StringTokenizer(
//						this.jsPacScript, ";", false);
//				while (localStringTokenizer.hasMoreTokens()) {
//					String str2 = localStringTokenizer.nextToken();
//					int i = str2.indexOf("DIRECT");
//					int j = str2.indexOf("PROXY");
//					int k = str2.indexOf("SOCKS");
//					int m = positiveMin(i, positiveMin(j, k));
//					int n = str2.lastIndexOf("\"");
//					if (m != -1) {
//						if (n <= m) {
//							str1 = str2.substring(m);
//						} else {
//							str1 = str2.substring(m, n);
//						}
//					}
//				}
//			}
//			return extractAutoProxySetting(str1);
//		} catch (Throwable localThrowable) {
//
//		}
//		return tmp145_142;
//	}

	private int positiveMin(int paramInt1, int paramInt2) {
		if (paramInt1 < 0) {
			return paramInt2;
		}
		if (paramInt2 < 0) {
			return paramInt1;
		}
		if (paramInt1 > paramInt2) {
			return paramInt2;
		}
		return paramInt1;
	}
}
