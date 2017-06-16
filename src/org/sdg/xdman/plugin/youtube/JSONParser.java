/*
 * Copyright (c)  Subhra Das Gupta
 *
 * This file is part of Xtream Download Manager.
 *
 * Xtream Download Manager is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Xtream Download Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with Xtream Download Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.sdg.xdman.plugin.youtube;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;

import org.sdg.xdman.util.MIMEUtil;

public class JSONParser {
	public ParserProgressListner plistener;
	long read;

	public ArrayList<YTVideoInfo> list(InputStream in) throws Exception {
		ArrayList<YTVideoInfo> list = new ArrayList<YTVideoInfo>();
		while (true) {
			String ln = readJSLine(in);
			if (ln == null)
				break;
			String map = parseJSONLine(ln);
			if (map != null) {
				String arr[] = map.split(",");
				for (int i = 0; i < arr.length; i++) {
					YTVideoInfo info = getVideoFmt(decodeJSONEscape(arr[i]));
					if (info != null)
						list.add(info);
				}
				if (list.size() > 0) {
					return list;
				}
			}
		}
		return list;
	}

	private YTVideoInfo getVideoFmt(String encoded_str) throws Exception {
		String enc_arr[] = encoded_str.split("&");
		boolean isEncrypted = false;
		int itag = 0;
		String url = "", sig = "", quality = "", type = "";
		for (int i = 0; i < enc_arr.length; i++) {
			String enc_str = enc_arr[i];
			int index = enc_str.indexOf("=");
			if (index > 0) {
				String key = enc_str.substring(0, index);
				String value = enc_str.substring(index + 1);
				value = value.replace("\\", "");
				System.out.println("YT_KEY: " + key + " VALUE: " + value);

				if (key.trim().equals("url")) {
					url = URLDecoder.decode(value, "utf-8");
					int idx = url.indexOf(";");
					if (idx > 0) {
						url = url.substring(0, idx);
					}
					idx = url.indexOf(",");
					if (idx > 0) {
						url = url.substring(0, idx);
						url = url.replace("\\", "");
					}
				}

				if (key.trim().equals("s")) {
					isEncrypted = true;
				}

				if (key.trim().equals("sig")) {
					sig = value;
					int idx = sig.indexOf(";");
					if (idx > 0) {
						sig = sig.substring(0, idx);
					}
					idx = sig.indexOf(",");
					if (idx > 0) {
						sig = sig.substring(0, idx);
						sig = sig.replace("\\", "");
					}
				}

				if (key.trim().equals("itag")) {
					try {
						String itg = URLDecoder.decode(value, "utf-8");
						itg = itg.replace("\\", "");
						itg = itg.trim();
						itag = Integer.parseInt(itg);
					} catch (Exception e) {
					}
				}
				if (key.trim().equals("quality")) {
					quality = URLDecoder.decode(value, "utf-8");
					quality = quality.replace("\\", "");
				}
				if (key.trim().equals("type")) {
					type = URLDecoder.decode(value, "utf-8");
					type = type.replace("\\", "");
				}
			}
		}
		if (url.length() < 1 || isEncrypted) {
			return null;
		}
		YTVideoInfo info = new YTVideoInfo();
		info.url = url + (sig.length() < 1 ? "" : "&signature=" + sig);
		info.itag = YouTubeFmtMap.getFMTInfo(itag + "");
		info.quality = quality;
		if (type != null) {
			try {
				type = type.split(";")[0].trim();
			} catch (Exception e) {
			}
		}
		type = MIMEUtil.getFileExt(type);
		info.type = type;
		return info;
	}

	private String decodeJSONEscape(String json) {
		StringBuffer buf = new StringBuffer();
		int pos = 0;
		while (true) {
			int index = json.indexOf("\\u", pos);
			if (index < 0) {
				if (pos < json.length()) {
					buf.append(json.substring(pos));
				}
				break;
			}
			buf.append(json.substring(pos, index));
			pos = index;
			String code = json.substring(pos + 2, pos + 2 + 4);
			int char_code = Integer.parseInt(code, 16);
			buf.append((char) char_code);
			pos += 6;
		}
		return buf.toString();
	}

	private String parseJSONLine(String line) {
		String key = "url_encoded_fmt_stream_map";
		int index = line.indexOf(key);
		if (index < 0) {
			return null;
		}
		int colonIndex = line.indexOf(':', index + key.length());
		if (colonIndex < 0) {
			return null;
		}
		int quoteStartIndex = line.indexOf('"', colonIndex);
		if (quoteStartIndex < 0) {
			return null;
		}
		int quoteEndIndex = line.indexOf('"', quoteStartIndex + 1);
		if (quoteEndIndex < 0) {
			return null;
		}
		String url_encoded_fmt_stream = line.substring(quoteStartIndex + 1,
				quoteEndIndex);
		return url_encoded_fmt_stream;
	}

	private String readJSLine(InputStream in) throws Exception {
		StringBuffer buf = null;
		while (true) {
			int x = in.read();
			if (x == -1) {
				break;
			}
			read++;
			if (buf == null) {
				buf = new StringBuffer();
			}
			if (x == '\n' || x == '\r') {
				continue;
			}
			if (x == ';') {
				break;
			}
			buf.append((char) x);
		}
		if (plistener != null) {
			plistener.update(read);
		}
		if (buf == null)
			return null;
		return buf.toString().trim();
	}
}
