package org.sdg.xdman.core.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jcifs.util.Base64;

import org.sdg.xdman.core.common.AuthenticationException;
import org.sdg.xdman.core.common.ProxyHelper;
import org.sdg.xdman.core.common.WebProxy;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.io.ChunkedInputStream;
import org.sdg.xdman.util.HTTPUtil;
import org.sdg.xdman.util.XDMUtil;

public class XDMHttpClient2 {
	public HashMap<String, String> requestHeaders = new HashMap<String, String>(),
			resposeHeaders = new HashMap<String, String>();
	private ArrayList<String> cookies = new ArrayList<String>(),
			setCookies = new ArrayList<String>();
	private SSLContext context;
	public boolean connected = false, sendGet = false;
	public Socket sock;
	public URL url;
	public String host, path, protocol, query, referer, ua;
	int port;
	public OutputStream out;
	public InputStream in;
	public String statusLine;
	final int HTTP_PROXY = 10, HTTPS_PROXY = 20, NONE = 30;
	int proxyType = NONE;
	XDMConfig config;
	long len;

	public void addCookies(ArrayList<String> cookieList) {
		if (cookieList != null) {
			cookies.addAll(cookieList);
		}
	}

	public static final int NTLM = 10, BASIC = 20, NO_AUTH = 0;

	public XDMHttpClient2(XDMConfig config) {
		this.config = config;
	}

	public void addRequestHeaders(String key, String value) {
		if (key == null || value == null)
			return;
		requestHeaders.put(key, value);
	}

	public String getResponseHeader(String key) {
		return resposeHeaders.get(key);
	}

	public void addCookie(String cookie) {
		if (cookie != null)
			cookies.add(cookie);
	}

	public ArrayList<String> getCookies() {
		return setCookies;
	}

	private void doConnect() throws IOException, AuthenticationException {
		WebProxy wp = ProxyHelper.getProxyForURL(url, config);

		if (protocol.equalsIgnoreCase("http")) {
			sock = new Socket();
			sock.setTcpNoDelay(true);
			sock.setReceiveBufferSize(config.tcpBuf * 1024);
			sock.setSoTimeout(config.timeout * 1000);
			if (wp != null) {
				proxyType = HTTP_PROXY;
				String proxyHost = wp.host;
				int proxyPort = wp.port;
				sock.connect(new InetSocketAddress(proxyHost, proxyPort));
			} else {
				proxyType = NONE;
				sock.connect(new InetSocketAddress(host, port));
			}
		} else if (protocol.equalsIgnoreCase("https")) {
			if (wp != null) {
				proxyType = HTTPS_PROXY;
				sock = new Socket();
				doTunneling(wp.host, wp.port);
			} else {
				proxyType = NONE;
				acceptAllCerts();
				sock = context.getSocketFactory().createSocket();// SSLSocketFactory.getDefault().createSocket();
				sock.setTcpNoDelay(true);
				sock.setSoTimeout(config.timeout * 1000);
				sock.setReceiveBufferSize(config.tcpBuf * 1024);
				sock.connect(new InetSocketAddress(host, port));
			}
		} else {
			throw new IOException("Protocol " + protocol + " is not supported");
		}
		sock.setTcpNoDelay(true);
		in = sock.getInputStream();
		out = sock.getOutputStream();
		sock.setKeepAlive(true);
		connected = true;
	}

	private void doTunnelConnect(String host, int port) throws IOException {
		sock = new Socket();
		sock.setTcpNoDelay(true);
		sock.setSoTimeout(config.timeout * 1000);
		sock.setReceiveBufferSize(config.tcpBuf * 1024);

		sock.connect(new InetSocketAddress(host, port));
		in = sock.getInputStream();
		out = sock.getOutputStream();
	}

	private void initConnection(String uri) throws IOException,
			URISyntaxException {
		String oldHost = this.host;
		int oldPort = this.port;
		uri = uri.trim();
		url = new URL(uri);
		host = url.getHost();
		port = url.getPort();
		protocol = url.getProtocol();
		path = url.getPath();
		query = url.getQuery();
		if (XDMUtil.isNullOrEmpty(path))
			path = "/";
		if (!(path.startsWith("/"))) {
			path = "/" + path;
		}
		if (query != null) {
			path = path + "?" + query;
		}
		if (port < 0) {
			if (protocol.equals("http")) {
				port = 80;
			}
			if (protocol.equals("https"))
				port = 443;
		}
		if (oldHost != null) {
			if (!(host.equals(oldHost) && port == oldPort)) {
				throw new IOException(
						"ReUse Error: Remote address is incompatiable: New Host: "
								+ host + " Old Host: " + oldHost);
			}
		}
	}

	static boolean flag = false;

	private void parseResponse() throws IOException {

		statusLine = HTTPUtil.readLine(in);
		// System.out.println("Status: " + statusLine + " " +
		// statusLine.length());
		// Logger.log(statusLine);
		resposeHeaders.clear();
		while (true) {
			String ln = HTTPUtil.readLine(in);
			if (ln.length() < 1)
				break;
			// Logger.log(ln);
			System.out.println(ln);
			int index = ln.indexOf(":");
			String key = ln.substring(0, index).trim().toLowerCase();
			String value = ln.substring(index + 1).trim();
			if (key.equals("proxy-authenticate")) {
				if (value.toLowerCase().indexOf("ntlm") != -1
						|| value.toLowerCase().indexOf("basic") != -1) {
					if (resposeHeaders.get(key) == null) {
						resposeHeaders.put(key, value);
					}
				}
			} else if (key.equals("www-authenticate")) {
				if (value.toLowerCase().indexOf("ntlm") != -1
						|| value.toLowerCase().indexOf("basic") != -1) {
					if (resposeHeaders.get(key) == null) {
						resposeHeaders.put(key, value);
					}
				}
			} else {
				if (key.equals("set-cookie")) {
					setCookies.add(value);
				} else {
					resposeHeaders.put(key, value);
				}
			}
		}
		// Prepare stream depending on transfer-encoding/content-encoding
		String tenc = resposeHeaders.get("transfer-encoding");
		InputStream in2 = in;
		if (tenc != null) {
			if (tenc.equalsIgnoreCase("chunked")) {
				in2 = new ChunkedInputStream(in);
			} else {
				throw new IOException("Transfer Encoding not supported: "
						+ tenc);
			}
		}
		String enc = resposeHeaders.get("content-encoding");
		InputStream in3 = in2;
		if (enc != null) {
			if (enc.equalsIgnoreCase("gzip")) {
				in3 = new GZIPInputStream(in2);
			} else if (enc.equalsIgnoreCase("none")
					|| enc.equalsIgnoreCase("identity")) {
				in3 = in2;
			} else {
				throw new IOException("Content Encoding not supported: " + enc);
			}
		}
		in = in3;
	}

	private String parseProxyResponse(HashMap<String, String> proxyResponse)
			throws IOException {
		String stat = HTTPUtil.readLine(in);
		// Logger.log(stat);
		proxyResponse.clear();
		while (true) {
			String ln = HTTPUtil.readLine(in);
			if (ln.length() < 1)
				break;
			// Logger.log(ln);
			// System.out.println(ln);
			int index = ln.indexOf(":");
			String key = ln.substring(0, index).trim().toLowerCase();
			String value = ln.substring(index + 1).trim();
			if (key.equals("proxy-authenticate")) {
				if (value.toLowerCase().indexOf("ntlm") != -1
						|| value.toLowerCase().indexOf("basic") != -1) {
					if (proxyResponse.get(key) == null) {
						proxyResponse.put(key, value);
					}
				}
			} else if (key.equals("www-authenticate")) {
				if (value.toLowerCase().indexOf("ntlm") != -1
						|| value.toLowerCase().indexOf("basic") != -1) {
					if (proxyResponse.get(key) == null) {
						proxyResponse.put(key, value);
					}
				}
			} else {
				proxyResponse.put(key, value);
			}
		}
		// Prepare stream depending on transfer-encoding/content-encoding
		String tenc = proxyResponse.get("transfer-encoding");
		InputStream in2 = in;
		if (tenc != null) {
			if (tenc.equalsIgnoreCase("chunked")) {
				in2 = new ChunkedInputStream(in);
			} else {
				throw new IOException("Transfer Encoding not supported: "
						+ tenc);
			}
		}
		String enc = proxyResponse.get("content-encoding");
		InputStream in3 = in2;
		if (enc != null) {
			if (enc.equalsIgnoreCase("gzip")) {
				in3 = new GZIPInputStream(in2);
			} else if (enc.equalsIgnoreCase("none")) {
				in3 = in2;
			} else {
				throw new IOException("Content Encoding not supported: " + enc);
			}
		}
		in = in3;
		return stat;
	}

	public void reConnect(String uri) throws UnknownHostException, IOException,
			URISyntaxException, AuthenticationException {
		initConnection(uri);
		connected = true;
	}

	public void connect(String uri) throws UnknownHostException, IOException,
			URISyntaxException, AuthenticationException {
		if (connected)
			return;
		initConnection(uri);
		doConnect();
		connected = true;
	}

	public String user, pass;

	public void close() {
		try {
			in.close();
		} catch (Exception e) {

		}
		try {
			out.close();
		} catch (Exception e) {

		}
		try {
			sock.close();
		} catch (Exception e) {

		}
	}

	public String getHostString() {
		return host + ":" + port;
	}

	private int getAuthenticationMethod(String auth) throws IOException {
		if (auth != null) {
			auth = auth.toLowerCase();
			if (auth.indexOf("basic") != -1) {
				return BASIC;
			} else if (auth.indexOf("ntlm") != -1) {
				return NTLM;
			} else {
				throw new IOException("Authentication method not supported");
			}
		} else {
			return NO_AUTH;
		}
	}

	private String getNTChallage(String value) {
		if (value == null)
			return null;
		int index = value.indexOf(' ');
		if (index < 0)
			return null;
		return value.substring(index + 1).trim();
	}

	public void sendRequest() throws IOException, AuthenticationException {
		// requestHeaders.put("accept-encoding", "gzip");
		System.out.println("1");
		int proxyAuthMethod = NO_AUTH, httpAuthMethod = NO_AUTH;
		int proxyNtlmStage = -1, ntlmStage = -1, basicHttpStage = -1, basicProxyStage = -1;
		int prevResponse = 0;
		NTLMAutheticator ntAuth = null, proxyNtAuth = null;
		System.out.println("2");
		if (proxyType == HTTP_PROXY) {
			path = url.toString();
			if (XDMUtil.isNullOrEmpty(url.getQuery())) {
				if (!path.endsWith("/")) {
					path += "/";
				}
			}
		}
		System.out.println("3");
		do {
			if (prevResponse == 401) {
				if (user == null || user.length() < 1) {
					throw new AuthenticationException(
							"Authentication required!");
				}
			}
			System.out.println("4");
			httpAuthMethod = getAuthenticationMethod(resposeHeaders
					.get("www-authenticate"));

			proxyAuthMethod = getAuthenticationMethod(resposeHeaders
					.get("proxy-authenticate"));

			if (httpAuthMethod == NTLM) {
				System.out.println("5");
				if (ntlmStage > 0) {
					throw new AuthenticationException("Authentication Failed!");
				}
				if (ntAuth == null) {
					String u = getUser(user);
					String d = getDomain(user);
					ntAuth = new NTLMAutheticator(d, u, pass);
				}
				String auth = resposeHeaders.get("www-authenticate");
				ntlmStage++;
				requestHeaders.put("authorization", "NTLM "
						+ ntAuth.getNTLMString(getNTChallage(auth)));
			} else if (httpAuthMethod == BASIC) {
				System.out.println("6");
				if (basicHttpStage > -1) {
					throw new AuthenticationException("Authentication Failed!");
				}

				String authString = "Basic "
						+ Base64.encode((user + ":" + pass).getBytes());
				requestHeaders.put("authorization", authString);
				basicHttpStage++;
			} else if (httpAuthMethod != NO_AUTH) {
				throw new IOException("Authentication method not supported");
			}

			if (proxyAuthMethod == NTLM) {
				System.out.println("ntlm**********");
				if (proxyNtlmStage > 1) {
					throw new AuthenticationException(
							"Proxy Authentication Failed!");
				}
				if (proxyNtAuth == null) {
					proxyNtAuth = new NTLMAutheticator(System
							.getenv("USERDOMAIN"), config.proxyUser,
							config.proxyPass);
				}
				String auth = resposeHeaders.get("proxy-authenticate");
				proxyNtlmStage++;
				requestHeaders.put("proxy-authorization", "NTLM "
						+ proxyNtAuth.getNTLMString(getNTChallage(auth)));
			} else if (proxyAuthMethod == BASIC) {
				System.out.println("basic*******************");
				if (basicProxyStage > -1) {
					throw new AuthenticationException(
							"Proxy Authentication Failed!");
				}
				if (XDMUtil.isNullOrEmpty(config.proxyUser)) {
					throw new AuthenticationException(
							"Proxy Authentication Failed!");
				}
				String authString = "Basic "
						+ Base64
								.encode((config.proxyUser + ":" + config.proxyPass)
										.getBytes());
				System.out.println("*****************8 auth :"
						+ config.proxyUser + " " + config.proxyPass);
				requestHeaders.put("proxy-authorization", authString);
				System.out
						.println("*****************************555**************");
				basicProxyStage++;
			} else if (proxyAuthMethod != NO_AUTH) {
				throw new IOException(
						"Proxy Authentication method not supported");
			}

			// Try to send request
			StringBuffer requestBuffer = new StringBuffer();

			// Try to send request without authentication

			appendHeaders(requestBuffer, requestHeaders, false);
			requestBuffer.append("\r\n");
			// System.out.println(requestBuffer);

			if (!sock.isConnected()) {
				doConnect();
			} else {
				if (sock.isClosed()) {
					doConnect();
				}
			}

			out = sock.getOutputStream();
			in = sock.getInputStream();

			out.write(requestBuffer.toString().getBytes());
			out.flush();
			// System.out.println(requestBuffer);

			parseResponse();

			prevResponse = getResponseCode();

			// System.out.println("SERVER_RESP: " + prevResponse + " "
			// + statusLine);

			if (prevResponse == 407 || prevResponse == 401) {
				String tenc = resposeHeaders.get("transfer-encoding");
				skipRemainingStream("chunked".equalsIgnoreCase(tenc),
						resposeHeaders);
				if ("Close".equalsIgnoreCase(resposeHeaders.get("connection"))
						|| "Close".equalsIgnoreCase(resposeHeaders
								.get("proxy-connection"))) {
					close();
				}
			}

		} while (prevResponse == 407 || prevResponse == 401);

		sendGet = true;
	}

	private void appendHeaders(StringBuffer requestBuffer,
			HashMap<String, String> requestHeaders, boolean tunnel) {
		if (tunnel) {
			requestBuffer
					.append(("CONNECT " + this.host + ":" + this.port
							+ " HTTP/1.1\r\nHost: " + this.host + ":"
							+ this.port + "\r\n"));
		} else {
			String shost = host;
			if (port != 80) {
				shost += ":" + port;
			}
			requestBuffer
					.append(("GET " + path + " HTTP/1.1\r\nHost: " + shost + "\r\nConnection: close\r\n"));
		}
		Iterator<String> it = requestHeaders.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = requestHeaders.get(key);
			requestBuffer.append((key + ": " + value + "\r\n"));
		}
		if (cookies != null)
			for (int i = 0; i < cookies.size(); i++) {
				requestBuffer.append("cookie: " + cookies.get(i) + "\r\n");
			}
		// Logger.log(requestBuffer);
	}

	private String getDomain(String userdom) {
		int idx = userdom.lastIndexOf('@');
		if (idx < 0) {
			return null;
		} else {
			return userdom.substring(idx + 1);
		}
	}

	private String getUser(String userdom) {
		int idx = userdom.lastIndexOf('@');
		if (idx < 0) {
			return userdom;
		} else {
			return userdom.substring(0, idx);
		}
	}

	public void doTunneling(String host, int port) throws IOException,
			AuthenticationException {
		int authMethod = NO_AUTH;
		int prevResponse = 0;
		int proxyNtlmStage = -1;
		int basicProxyStage = -1;
		NTLMAutheticator ntAuth = null;

		// Store proxy headers
		HashMap<String, String> proxyResponse = new HashMap<String, String>();
		HashMap<String, String> proxyRequest = new HashMap<String, String>();

		do {
			authMethod = getAuthenticationMethod(proxyResponse
					.get("proxy-authenticate"));
			if (authMethod == NTLM) {
				if (proxyNtlmStage > 1) {
					throw new AuthenticationException(
							"Proxy Authentication Failed!");
				}
				if (ntAuth == null) {
					ntAuth = new NTLMAutheticator(System.getenv("USERDOMAIN"),
							config.proxyUser, config.proxyPass);
				}
				String auth = proxyResponse.get("proxy-authenticate");
				proxyNtlmStage++;
				proxyRequest.put("proxy-authorization", "NTLM "
						+ ntAuth.getNTLMString(getNTChallage(auth)));
			} else if (authMethod == BASIC) {
				if (basicProxyStage > -1) {
					throw new AuthenticationException(
							"Proxy Authentication Failed!");
				}
				String authString = "Basic "
						+ Base64
								.encode((config.proxyUser + ":" + config.proxyPass)
										.getBytes());
				proxyRequest.put("proxy-authorization", authString);
				basicProxyStage++;
			} else if (authMethod != NO_AUTH) {
				throw new IOException(
						"Proxy Authentication method not supported");
			}

			// Try to send request
			StringBuffer requestBuffer = new StringBuffer();

			// Try to send request without authentication

			appendHeaders(requestBuffer, proxyRequest, true);

			// System.out.println(sock.isConnected() + " " + sock.isClosed());

			if (!sock.isConnected()) {
				doTunnelConnect(host, port);
			} else {
				if (sock.isClosed()) {
					doTunnelConnect(host, port);
				}
			}

			requestBuffer.append("\r\n");

			out.write(requestBuffer.toString().getBytes());
			out.flush();

			String statusLine = parseProxyResponse(proxyResponse);
			// System.out.println(statusLine);

			prevResponse = HTTPUtil.getResponseCode(statusLine);

			if (prevResponse == 407 || prevResponse == 401) {
				String tenc = proxyResponse.get("transfer-encoding");
				skipRemainingStream("chunked".equalsIgnoreCase(tenc),
						proxyResponse);
			}

			if ("close".equalsIgnoreCase(proxyResponse.get("proxy-connection"))) {
				close();
			}
		} while (prevResponse == 407 || prevResponse == 401);

		if (prevResponse != 200) {
			System.out.println(prevResponse);
			throw new IOException("Proxy tunnelling failed!");
		}

		acceptAllCerts();
		SSLSocket sock2 = (SSLSocket) (context.getSocketFactory())
				.createSocket(sock, this.host, this.port, true);
		sock2.startHandshake();
		sock = sock2;

	}

	private void skipRemainingStream(boolean isChunked,
			HashMap<String, String> map) throws IOException {
		long cLen = HTTPUtil.getContentLength(map);
		byte buf[] = new byte[8192];
		if (cLen > 0) {// Fixed length stream
			while (cLen > 0) {
				int r = (int) (cLen > buf.length ? buf.length : cLen);
				int x = in.read(buf, 0, r);
				if (x == -1)
					break;
				cLen -= x;
			}
		} else if (isChunked) {// Chunked stream optionally compressed
			while (true) {
				int x = in.read(buf);
				if (x == -1)
					break;
			}
		} else {// Neither fixed length nor chunked
			while (true) {
				int x = in.available();
				if (x > 0) {
					if (x > buf.length) {
						x = buf.length;
					}
					if (in.read(buf, 0, x) < 1)
						break;
				} else {
					break;
				}
			}
		}
	}

	public long getContentLength() {
		return HTTPUtil.getContentLength(resposeHeaders);
	}

	public int getResponseCode() {
		return HTTPUtil.getResponseCode(statusLine);
	}

	String getContentName() {
		return XDMUtil
				.getContentName(resposeHeaders.get("content-disposition"));
	}

	private void acceptAllCerts() {
		try {
			try {
				context = SSLContext.getInstance("TLS");
			} catch (Exception e) {
				context = SSLContext.getInstance("SSL");
			}
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[] {};
				}

				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}
			} };
			context.init(null, trustAllCerts, null);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void finishStream() throws IOException {
		String ts = resposeHeaders.get("transfer-encoding");
		if (ts != null) {
			if (ts.equalsIgnoreCase("chunked")) {
				this.in.close();
			}
		}
	}

}
