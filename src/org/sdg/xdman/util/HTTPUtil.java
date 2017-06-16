package org.sdg.xdman.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class HTTPUtil {
	public static byte[] getBytes(String str)
	{
		return str.getBytes();
	}
	public static final String readLine(InputStream in) throws IOException {
		StringBuffer buf = new StringBuffer();
		while (true) {
			int x = in.read();
			if (x == -1)
				throw new IOException(
						"Unexpected EOF while reading header line");
			if (x == '\n')
				return buf.toString();
			if (x != '\r')
				buf.append((char) x);
		}
	}

	public static final int getResponseCode(String statusLine) {
		String arr[] = statusLine.split(" ");
		if (arr.length < 2)
			return 400;
		return Integer.parseInt(arr[1]);
	}

	public static long getContentLength(HashMap<String, String> map) {
		try {
			String clen = map.get("content-length");
			if (clen != null) {
				return Long.parseLong(clen);
			} else {
				clen = map.get("content-range");
				if (clen != null) {
					String str = clen.split(" ")[1];
					str = str.split("/")[0];
					String arr[] = str.split("-");
					return Long.parseLong(arr[1]) - Long.parseLong(arr[0]) + 1;
				} else {
					return -1;
				}
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
