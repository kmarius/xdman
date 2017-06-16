package org.sdg.xdman.interceptor;

import java.io.BufferedReader;
import java.io.IOException;

public class HTMLTitleParser {
	public static String GetTitleFromPage(BufferedReader r) throws IOException {
		int pos = 0;
		boolean end = false;
		StringBuilder title = new StringBuilder();
		while (true) {
			pos = 0;
			String line = r.readLine();

			if (line == null) {
				break;
			}
			if (!end) {
				int index = line.indexOf("<title>");
				if (index == -1)
					continue;
				pos = index + 7;
				end = true;
			}
			if (end) {
				int index = line.indexOf("</title>", pos);
				if (index == -1) {
					title.append(line);
					continue;
				}
				int len = index - pos;
				if (len < 1) {
					return null;
				}
				String stitle = line.substring(pos, pos + len);
				title.append(stitle);
				return title.toString();
			}
		}
		return title.toString();
	}
}
