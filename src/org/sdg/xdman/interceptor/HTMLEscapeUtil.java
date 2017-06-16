package org.sdg.xdman.interceptor;

import java.util.HashMap;

public class HTMLEscapeUtil {
	static HashMap<String, String> escapeList = null;

	static void init() {
		escapeList = new HashMap<String, String>();
		escapeList.put("&nbsp;", " ");
		escapeList.put("&quot;", "\"");
		escapeList.put("&amp;", "&");
		escapeList.put("&lt;", "<");
		escapeList.put("&gt;", ">");
		escapeList.put("&iexcl;", "!");
		escapeList.put("&copy;", "(c)");
		escapeList.put("&reg;", "(R)");
	}

	public static String escapeHTMLLine(String line) {
		if (escapeList == null) {
			init();
		}
		for (String key : escapeList.keySet()) {
			line = line.replace(key, (String) escapeList.get(key));
		}
		return line;
	}
}
