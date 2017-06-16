package org.sdg.xdman.interceptor;

import java.io.*;

import org.sdg.xdman.util.XDMUtil;

public class JSONReader {
	StringBuilder line = new StringBuilder();
	char[] block = new char[8192];
	int pos = 0, len = 0;

	Reader r;

	public JSONReader(InputStream s) {
		r = new InputStreamReader(s);
	}

	public String ReadLine() throws Exception {
		String rawline = ReadRawLine();
		if (!XDMUtil.isNullOrEmpty(rawline)) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < rawline.length(); i++) {
				if (rawline.charAt(i) == '\\') {
					if (rawline.length() - i < 5) {
						return sb.toString();
					} else {
						if (rawline.charAt(i + 1) == 'u') {
							String codepoint = new String(new char[] { rawline.charAt(i + 2), rawline.charAt(i + 3),
									rawline.charAt(i + 4), rawline.charAt(i + 5) });
							char ch = (char) Integer.parseInt(XDMUtil.trimStart(codepoint, "0"), 16);
							sb.append(ch);
							i = i + 5;
						}
					}
				} else {
					sb.append(rawline.charAt(i));
				}
			}
			return sb.toString();
		} else {
			return rawline;
		}
	}

	public String ReadRawLine() throws Exception {

		while (!ReadJSONLine()) {
			len = r.read(block, 0, block.length);
			pos = 0;
			if (len == -1) {
				if (line.length() < 1) {
					return null;
				} else {
					break;
				}
			}
		}
		String sline = line.toString();
		line = new StringBuilder();
		return sline;

	}

	boolean ReadJSONLine() {
		while (pos < len) {
			char ch = block[pos];
			pos = pos + 1;
			if (ch == ',' || ch == '{' || ch == '}' || ch == '[' || ch == ',' || ch == ']') {
				return true;
			} else {
				if (!(ch == '\r' || ch == '\n' || ch == '\t' || ch == ' '))
					line.append(ch);
			}
		}
		return false;
	}

	

}
