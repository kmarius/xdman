package org.sdg.xdman.interceptor;

import java.io.*;
import java.net.*;
import java.util.*;

import org.sdg.xdman.core.common.*;
import org.sdg.xdman.core.common.hls.*;
import org.sdg.xdman.core.common.http.*;
import org.sdg.xdman.util.*;

public class Intercepter implements Runnable {
	String error = "HTTP/1.1 502 Bad Gateway\r\n" + "\r\n" + "ERROR";
	String captureString = "HTTP/1.1 204 No Content\r\n" + "Connection: Close\r\n" + "Proxy-Connection: Close\r\n"
			+ "\r\n";
	Socket sockLocal, sockRemote;
	InputStream sLocalIn, sRemoteIn;
	OutputStream sLocalOut, sRemoteOut;

	String requestLine, responseLine, url;
	HashMap<String, String> requestHeaders, responseHeaders;
	String host, path;
	int port;
	byte[] b = new byte[8192];
	XDMConfig config;
	ArrayList<String> cookies, setCookies;
	boolean stop = false;
	Thread t;
	DownloadStateListner mgr;
	IMediaGrabber media_grabber;
	String method;

	public Intercepter(Socket sock, XDMConfig conf, DownloadStateListner mgr, IMediaGrabber mg) {
		this.sockLocal = sock;
		this.config = conf;
		this.mgr = mgr;
		this.media_grabber = mg;
	}

	@Override
	public void run() {
		try {
			while (!stop) {
				acceptRequest();
			}
		} catch (Exception e) {
			Logger.log(e);
		} finally {
			closeAll();
		}
	}

	private void acceptRequest() throws Exception {
		sLocalIn = sockLocal.getInputStream();
		sLocalOut = sockLocal.getOutputStream();
		requestLine = HTTPUtil.readLine(sLocalIn);
		//Logger.log(requestLine);
		if (requestLine == null || requestLine.length() < 1) {
			closeAll();
			return;
		}

		if (requestLine.equals("PARAM")) {
			handlePARAMRequest(sLocalIn);
			return;
		}

		String[] arr = requestLine.split(" ");
		if (arr.length < 3) {
			closeAll();
			return;
		}

		url = arr[1].trim();

		if (url.startsWith("/") || url.startsWith("http://127.0.0.1:9614")) {
			handleLocalRequest(url, sLocalIn, sLocalOut);
			closeAll();
			return;
		}
	}

	private void handlePARAMRequest(InputStream s) {
		try {
			HashMap<String, String> args = new HashMap<String, String>();
			while (true) {
				String ln = HTTPUtil.readLine(s);

				if (XDMUtil.isNullOrEmpty(ln)) {
					break;
				}
				int index = ln.indexOf(":");
				if (index > 0) {
					String key = ln.substring(0, index);
					String val = ln.substring(index + 1).trim();
					args.put(key, val);
				}
			}
			String url2 = args.get("url");
			String rfr = args.get("referer");
			String cks = args.get("cookies");
			String min = args.get("min");
			String noc = args.get("noconfirm");

			if (!XDMUtil.isNullOrEmpty(url2)) {
				DownloadIntercepterInfo info = new DownloadIntercepterInfo();
				info.url = url2;
				info.referer = rfr;
				info.noconfirm = noc;
				ArrayList<String> cl = null;
				if (!XDMUtil.isNullOrEmpty(cks)) {
					cl = new ArrayList<String>();
					cl.add(cks);
				}
				info.cookies = cl;
				mgr.interceptDownload(info);// , (DownloadWaitObject)null);
			}

			if (min == null) {
				if (mgr != null) {
					mgr.restoreWindow();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void closeAll() {
		stop = true;
		// Close Remote connection
		if (responseHeaders != null) {
			responseHeaders.clear();
			responseHeaders = null;
		}

		if (setCookies != null) {
			setCookies.clear();
			setCookies = null;
		}

		if (sockRemote != null) {
			try {
				sockRemote.close();
			} catch (Exception e) {
			}
		}

		sockRemote = null;

		try {
			sockLocal.close();
			sockLocal = null;
		} catch (Exception e) {

		}
	}

	public static String getFileNameFromDisposition(String content_disposition) {
		if (content_disposition == null) {
			return "";
		}
		try {
			String[] p = content_disposition.split(";");
			for (int i = 0; i < p.length; i++) {
				String param = p[i].trim().toLowerCase();
				if (param.startsWith("filename")) {
					int index = param.indexOf('=');
					String file = param.substring(index + 1);
					file = file.replace("\"", "");
					return file;
				}
			}
		} catch (Exception e) {
			return "";
		}
		return "";
	}

	void onVideoArgs(InputStream in, OutputStream out, long len) throws IOException {
		try {
			String ua = "", referer = "", url = "", title = "", type = "";
			ByteArrayOutputStream bout = new ByteArrayOutputStream((int) len);
			byte[] b = new byte[8192];
			while (len > 0) {
				int x = in.read(b);
				if (x == -1)
					break;
				len -= x;
				bout.write(b, 0, x);
			}
			String str = new String(bout.toByteArray(), "UTF-8");
			System.out.println(str);

			long size = 0L;
			ArrayList<String> cookies = new ArrayList<String>();

			String[] arr = str.replace("\r", "").split("\n");
			for (int i = 0; i < arr.length; i++) {
				String s = arr[i].trim();
				int index = s.indexOf(":");
				if (index > 0) {
					String key = s.substring(0, index);
					String val = s.substring(index + 1).trim();

					if (key.equals("url")) {
						url = val;
					} else if (key.equals("ua")) {
						ua = val;
					} else if (key.equals("cookie")) {
						cookies.add(val);
					} else if (key.equals("referer")) {
						referer = val;
					} else if (key.equals("title")) {
						title = val.trim();
					} else if (key.equals("type")) {
						try {
							type = val.split(";")[0];
						} catch (Exception e) {

						}
					} else if (key.equals("size")) {
						try {
							size = Long.parseLong(val.trim());
						} catch (Exception e) {

						}
					}
				}
			}

			boolean hls = false;

			String file = XDMUtil.getFileName(url);
			if (XDMUtil.isNullOrEmpty(title)) {
				title = file;
			}
			title = title.trim().replace("\n", "");
			title = XDMUtil.createSafeFileName(title);
			String ext = XDMUtil.getExtension(file);
			if (!XDMUtil.isNullOrEmpty(ext)) {
				ext = ext.toLowerCase();
			} else {
				ext = "";
			}

			if (type.contains("text/html") || type.contains("application/json")) {
				if (type.contains("text/html") && url.contains(".facebook.com/") && url.contains("pagelet")) {
					File tf = new File(config.tempdir, UUID.randomUUID() + "");
					if (downloadManifest(url, referer, ua, cookies, tf.getAbsolutePath())) {
						handleVideo(tf.getAbsolutePath(), url, title, ua, referer, cookies, 0);
						tf.delete();
					}
				}
				if (type.contains("application/json") && url.contains("player.vimeo.com/")) {
					System.out.println("vimeo");
					File tf = new File(config.tempdir, UUID.randomUUID() + "");
					if (downloadManifest(url, referer, ua, cookies, tf.getAbsolutePath())) {
						handleVideo(tf.getAbsolutePath(), url, title, ua, referer, cookies, 1);
						tf.delete();
					}
				}
				return;
			}

			if (ext.equals(".m3u8")) {
				File tf = new File(config.tempdir, UUID.randomUUID() + "");
				if (downloadManifest(url, referer, ua, cookies, tf.getAbsolutePath())) {
					handleM3U8(url, tf.getAbsolutePath(), title, referer, ua, cookies);
					tf.delete();
				}
			} else {
				if (type.contains("video/mp4")) {
					ext = ".mp4";
				} else if (type.contains("video/x-flv")) {
					ext = ".flv";
				} else if (type.contains("video/webm")) {
					ext = ".mkv";
				} else if (type.contains("matroska") || type.contains("mkv")) {
					ext = ".mkv";
				} else if (type.contains("audio/mpeg")) {
					ext = ".mp3";
				}
			}

			if (type.contains("f4f") || type.contains("m4s") || type.contains("f4m") || type.contains("mp2t")) {
				return;
			}

			if (ext.contains("f4f") || ext.contains("m4s") || ext.contains("f4m") || ext.contains("mp2t")) {
				return;
			}

			System.out.println("file: " + title);

			if (!ProcessYTVideo(url, title, referer, ua, cookies, type, size)) {
				if (type.contains("video/") || type.contains("audio/") || type.contains("application/octet")) {
					if (!(url.contains(".fbcdn") || url.contains(".m4s") || url.contains(".ts") || type.contains("f4f")
							|| type.contains("f4x") || type.contains("abst") || type.contains("fcs"))) {
						if (url.length() > 1) {
							if (!XDMUtil.isNullOrEmpty(ext)) {
								title += ext;
							}
							if (media_grabber != null) {
								media_grabber.mediaCaptured(title, url, null, type,
										hls ? "" : XDMUtil.getFormattedLength(size), referer, ua, cookies);
							}
						}
					}
				}
			}
		} finally {

			out.write(
					("HTTP/1.1 204 OK\r\n" + "Content-Type: text/plain\r\n" + "Connection: Close\r\n\r\n").getBytes());
			out.flush();
		}
	}

	String getUTF8String(String str) {
		try {
			byte[] b = new byte[str.length()];
			for (int i = 0; i < str.length(); i++) {
				b[i] = (byte) str.charAt(i);
			}
			return new String(b, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}
	}

	void onDownloadArgs(InputStream in, OutputStream out) throws IOException {
		try {
			String ua = "", referer = "", url = "", type = "", attachment = "";
			ArrayList<String> cookies = new ArrayList<String>();
			while (true) {
				String s = HTTPUtil.readLine(in);
				if (XDMUtil.isNullOrEmpty(s)) {
					break;
				}
				System.out.println(s);
				int index = s.indexOf(":");
				if (index > 0) {
					String key = s.substring(0, index);
					String val = s.substring(index + 1).trim();

					if (key.equals("url")) {
						url = val;
					} else if (key.equals("ua")) {
						ua = val;
					} else if (key.equals("cookie")) {
						cookies.add(val);
					} else if (key.equals("referer")) {
						referer = val;
					} else if (key.equals("type")) {
						type = val;
					} else if (key.equals("attachment")) {
						attachment = val;
					}
				}
			}

			if (type.contains("json") || type.contains("html") || type.contains("xml") || type.contains("javascript")) {
				return;
			}

			String attach = XDMUtil.getContentName(attachment);
			if (!XDMUtil.isNullOrEmpty(attach)) {
				if (attach.contains(".json") || attach.contains(".js")) {
					return;
				}
			}

			if (url.length() > 1) {
				DownloadIntercepterInfo info = new DownloadIntercepterInfo();
				info.url = url;
				info.ua = ua;
				info.referer = referer;
				info.cookies = cookies;
				if (mgr != null) {
					mgr.interceptDownload(info);
				}
			}
		} finally {
			out.write(
					("HTTP/1.1 204 OK\r\n" + "Content-Type: text/plain\r\n" + "Connection: Close\r\n\r\n").getBytes());
			out.flush();
		}
	}

	void updateHook(InputStream in, OutputStream out) throws Exception {
		StringBuffer data = new StringBuffer("TRUE|");
		for (int i = 0; i < config.fileTypes.length; i++) {
			if (i > 0) {
				data.append(",");
			}
			String s = config.fileTypes[i].replace("\n", "").trim();
			if (s.length() > 0) {
				data.append(s);
			}
		}
		data.append("|player.vimeo.com/video,facebook.com/ajax/pagelet|");
		for (int i = 0; i < config.siteList.length; i++) {
			if (i > 0) {
				data.append(",");
			}
			String s = config.siteList[i].replace("\n", "").trim();
			if (s.length() > 0) {
				data.append(s);
			}
		}
		data.append("|MP4,MPEG,MPG,FLV,MKV,WEBM,MP3|");
		// data.append("TRUE");//// config.tabletMode ? "TRUE" : "FALSE");
		// data.append(
		// "|Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46
		// (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3");
		byte[] databytes = data.toString().getBytes();

		out.write(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n" + "Connection: Close\r\n" + "Content-Length: "
				+ databytes.length + "\r\n\r\n").getBytes());
		out.write(databytes);
		out.flush();
	}

	// void updateHookStatus2(InputStream in, OutputStream out) throws Exception
	// {
	// String data = config.browserInt ? "1" : "0";
	// data += "|";
	// for (int i = 0; i < config.fileTypes.length; i++) {
	// if (i > 0) {
	// data += ",";
	// }
	// data += config.fileTypes[i];
	// }
	// data += "|";
	// for (int i = 0; i < config.siteList.length; i++) {
	// if (i > 0) {
	// data += ",";
	// }
	// data += config.siteList[i];
	// }
	// data += "|";
	// data += 1;// config.tabletMode ? 1 : 0;
	// data += config.tabletURL;
	//
	// byte[] databytes = data.getBytes();
	//
	// out.write(("HTTP/1.1 200 OK\r\n" + "Content-Type: text/plain\r\n" +
	// "Connection: Close\r\n" + "Content-Length: "
	// + databytes.length + "\r\n\r\n").getBytes());
	// out.write(databytes);
	// out.flush();
	// }

	private void handleLocalRequest(String url, InputStream in, OutputStream out) {
		try {
			long clen = 0;
			while (true) {
				String s = HTTPUtil.readLine(in);
				if (XDMUtil.isNullOrEmpty(s)) {
					break;
				}
				if (s.toLowerCase().startsWith("content-length")) {
					clen = Integer.parseInt(s.split(":")[1].trim());
				}
			}
			if (url.contains("/xdmhook")) {
				updateHook(in, out);
			}
			if (url.contains("/download")) {
				onDownloadArgs(in, out);
			}
			if (url.contains("/video")) {
				onVideoArgs(in, out, clen);
			}
			if (url.contains("/generate_204")) {
				out.write(("HTTP/1.1 204 OK\r\n" + "Connection: Close\r\n\r\n").getBytes());
				out.flush();
			} else if (url.contains("/chrome")) {
				out.write(("HTTP/1.1 400 bad request\r\nConnection: close\r\n\r\n").getBytes());
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean ProcessYTVideo(String url, String title, String referer, String ua, ArrayList<String> cookies, String type,
			long size) {
		try {
			URL uri = new URL(url);
			String host = uri.getHost();
			String pathAndQuery = uri.getPath() + "?" + uri.getQuery();
			if (!(host.contains("youtube.com") || host.contains("googlevideo.com"))) {
				return false;
			}

			if (!(type.contains("audio/") || type.contains("video/"))) {
				return false;
			}

			String low_path = pathAndQuery.toLowerCase();
			if (low_path.indexOf("videoplayback") >= 0 && low_path.indexOf("itag") >= 0) {
				// found DASH audio/video stream
				int index = url.indexOf("?");
				if (index >= 0) {
					if (index + 1 < url.length()) {
						String _path = url.substring(0, index);
						String query = url.substring(index + 1);
						String[] arr = query.split("&");
						StringBuilder yt_url = new StringBuilder();
						yt_url.append(_path + "?");
						int itag = 0;
						long clen = 0;
						String id = "";
						String mime = "";
						for (int i = 0; i < arr.length; i++) {
							String str = arr[i];
							if (!str.startsWith("range")) {
								if (str.startsWith("itag")) {
									itag = Integer.parseInt(str.split("=")[1]);
								}
								if (str.startsWith("mime")) {
									mime = str.split("=")[1];
									mime = URLDecoder.decode(mime, "UTF-8");// Uri.UnescapeDataString(mime);
								}
								if (str.startsWith("clen")) {
									clen = Long.parseLong(str.split("=")[1]);
								}
								if (str.startsWith("id")) {
									id = str.split("=")[1];
								}
								yt_url.append(str);
								if (i < arr.length - 1) {
									yt_url.append("&");
								}
							}
						}
						if (itag != 0) {
							if (DASHItagHelper.IsNormalVideo(itag)) {
								captureMedia(url, size, type, referer, title, ua, cookies);
								// AddVideo(url,null,type,size,
								return true;
							}
							if (mime.startsWith("video"))// DASHItagHelper.IsMP4Video(itag)
															// ||
															// DASHItagHelper.IsWEBMVideo(itag))
							{
								DASH_INFO info = new DASH_INFO();
								info.url = yt_url.toString();
								info.clen = clen;
								info.video = true;
								info.itag = itag;
								info.id = id;
								info.mime = mime;
								if (DASHUtility.AddToQueue(info)) {
									DASH_INFO di = DASHUtility.GetDASHPair(info);

									if (di != null) {
										YT_DASH_ENTRY ent = new YT_DASH_ENTRY();
										ent.a_url = di.url;
										ent.v_url = info.url;
										ent.v_itag = itag;
										ent.a_itag = di.itag;
										captureMedia(ent, mime, referer, title, ua, cookies);
										return true;
									}
								}
							}
							if (mime.startsWith("audio/"))//// DASHItagHelper.IsMP4Audio(itag)
															//// ||
															//// DASHItagHelper.IsWEBMAudio(itag))
							{
								DASH_INFO info = new DASH_INFO();
								info.url = yt_url.toString();
								info.clen = clen;
								info.video = false;
								info.itag = itag;
								info.id = id;
								// System.Windows.Forms.MessageBox.Show("Adding
								// audio to q: " + info.itag + "\n" + info.url +
								// "\nvideo: " + info.video);

								if (DASHUtility.AddToQueue(info)) {
									DASH_INFO di = DASHUtility.GetDASHPair(info);

									if (di != null) {
										YT_DASH_ENTRY ent = new YT_DASH_ENTRY();
										ent.v_url = di.url;
										ent.a_url = info.url;
										ent.a_itag = itag;
										ent.v_itag = di.itag;
										captureMedia(ent, mime, referer, title, ua, cookies);
										return true;
									}
								}
							}
						}
					}
				}

				return true;
			}
			return false;
		} catch (Exception exx) {
			return false;
		}
	}

	void captureMedia(String url, long size, String type, String referer, String title, String ua,
			ArrayList<String> cookies) {
		if (media_grabber != null) {
			try {
				String cleanContentType = type.split(";")[0];
				if (cleanContentType.contains("webm")) {
					title += ".mkv";
				} else if (cleanContentType.contains("mp4")) {
					title += ".mp4";
				} else if (cleanContentType.contains("flv")) {
					title += ".flv";
				}
				if (media_grabber != null) {
					media_grabber.mediaCaptured(title, url, null, type, XDMUtil.getFormattedLength(size), referer, ua,
							cookies);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void captureMedia(YT_DASH_ENTRY ent, String type, String referer, String title, String ua,
			ArrayList<String> cookies) {
		System.out.println(
				"DASH URL**************************\n" + "VIDEO_URL: " + ent.v_url + "\nAUDIO_URL: " + ent.a_url);
		if (media_grabber != null) {
			try {

				String cleanContentType = type.split(";")[0];
				String size = DASHItagHelper.GetInfoFromITAG(ent.v_itag);
				if (size == null) {
					size = "";
				}
				if (XDMUtil.isNullOrEmpty(title)) {
					title = XDMUtil.getFileName(ent.v_url);
				}
				if (ent.v_url.contains("%2Fwebm") || ent.a_url.contains("%2Fwebm")) {
					title += ".mkv";
				} else if (ent.v_url.contains("%2Fmp4") && ent.a_url.contains("%2Fmp4")) {
					title += ".mp4";
				}
				title = XDMUtil.createSafeFileName(title);
				media_grabber.mediaCaptured(title, ent.v_url, ent.a_url, cleanContentType, size, referer, ua,
						(cookies == null ? new ArrayList<String>() : new ArrayList<String>(cookies)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	boolean handleM3U8(String url, String file, String title, String referer, String ua, ArrayList<String> cookies) {
		int pl[] = new int[1];
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> props = new ArrayList<String>();
		FileInputStream fs = null;
		try {
			fs = new FileInputStream(file);
			if (!M3U8.parse(fs, list, pl, props)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				fs.close();
			} catch (Exception e) {
			}
		}

		if (list.size() < 1) {
			return false;
		}
		try {
			if (pl[0] == 1) {
				list = M3U8.getURIList(list, url);
				for (int i = 0; i < list.size(); i++) {
					String prop = props.get(i).trim();
					String[] arr = prop.split(",");
					String resolution = "", bandwidth = "";
					for (int j = 0; j < arr.length; j++) {
						try {
							String ss = arr[j].toUpperCase();
							if (ss.startsWith("RESOLUTION")) {
								if (ss.contains("=")) {
									resolution = ss.split("=")[1].trim();
								}
							}
							if (ss.startsWith("BANDWIDTH")) {
								if (ss.contains("=")) {
									bandwidth = ss.split("=")[1].trim();
									int bps = 0;
									try {
										bps = Integer.parseInt(bandwidth);
										bandwidth = (bps / 1000) + " kbps";
									} catch (Exception e) {
									}
								}
							}
						} catch (Exception e) {
						}
					}

					if (media_grabber != null) {
						media_grabber.mediaCaptured(
								XDMUtil.isNullOrEmpty(title) ? XDMUtil.getFileName(list.get(i)) : title + ".ts",
								list.get(i), null, "application/x-mpegurl", bandwidth + " " + resolution, referer, ua,
								cookies);
					}
				}
			} else {
				if (media_grabber != null) {
					media_grabber.mediaCaptured(XDMUtil.isNullOrEmpty(title) ? XDMUtil.getFileName(url) : title + ".ts",
							url, null, "application/x-mpegurl", "", referer, ua, cookies);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	boolean downloadManifest(String url, String referer, String ua, ArrayList<String> cookies, String tempfile) {
		XDMHttpClient2 hc = null;
		FileOutputStream outstream = null;
		try {
			hc = new XDMHttpClient2(config);
			hc.connect(url);
			hc.addRequestHeaders("User-Agent", ua);
			hc.addRequestHeaders("Referer", referer);
			hc.addCookies(cookies);
			hc.sendRequest();
			int rc = hc.getResponseCode();
			if (rc == 200) {
				outstream = new FileOutputStream(tempfile);
				InputStream instream = hc.in;
				XDMUtil.copyStream(instream, outstream, hc.getContentLength());
				outstream.flush();
			} else {
				System.out.println("manifest download failed: " + rc);
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (hc != null) {
				hc.close();
			}
			if (outstream != null) {
				try {
					outstream.close();
				} catch (Exception e) {
				}
			}
		}
	}

	void handleVideo(String file, String _url, String title, String ua, String referer, ArrayList<String> cookies,
			int type) {
		FileInputStream tmpStream = null;
		try {
			tmpStream = new FileInputStream(file);
			List<VID_INFO> list = null;
			if (type == 0) {// fb video
				System.out.println("checking fb video...");
				list = new FBExtractor().GetVideoList(tmpStream);
			} else if (type == 1) {// vimeo
				list = new VimeoVideoExtractor().GetVideoList(tmpStream);
			}
			tmpStream.close();
			for (int i = 0; i < list.size(); i++) {
				String ext = XDMUtil.getFileName(list.get(i).url);
				ext = XDMUtil.getExtension(ext);
				String fn = title;
				if (!XDMUtil.isNullOrEmpty(ext)) {
					fn += ext;
				}
				System.out.println("Adding video: " + list.get(i).url);
				VID_INFO info = list.get(i);
				media_grabber.mediaCaptured(fn, info.url, null, info.type, info.quality == null ? "" : info.quality,
						referer, ua, cookies);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				tmpStream.close();
			} catch (Exception e2) {
			}
		}
	}
}
