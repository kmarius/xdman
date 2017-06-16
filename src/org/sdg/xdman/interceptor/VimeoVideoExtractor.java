package org.sdg.xdman.interceptor;

import java.io.*;
import java.util.*;

import org.sdg.xdman.util.XDMUtil;

public class VimeoVideoExtractor implements IVideoExtractor {
	public List<VID_INFO> GetVideoList(InputStream s) {
		String url = null, type = null, quality = null;
		List<VID_INFO> list = new ArrayList<VID_INFO>();
		try {
			JSONReader jsr = new JSONReader(s);
			while (true) {
				String line = jsr.ReadLine();
				if (line == null)
					break;
				int index = line.indexOf(':');
				if (index > 0) {
					String key = XDMUtil.trim(line.substring(0, index), "\" ");// line.substring(0,
																				// index).Trim(new
																				// char[]
																				// {
																				// '"',
																				// '
																				// '
																				// });
					String val = XDMUtil.trim(line.substring(index + 1), "\" ");// line.substring(index
																					// +
																					// 1).Trim(new
																					// char[]
																					// {
																					// '"',
																					// '
																					// '
																					// });
					// Console.WriteLine(line);
					System.out.println(key);
					if (key.equals("url"))
						url = val;
					if (key.equals("quality"))
						quality = val;
					if (key.equals("mime"))
						type = val;
					if (url != null && quality != null && type != null) {
						// Console.WriteLine("-----\n" + url + "\n" + type +
						// "\n" + quality + "\n-----");
						VID_INFO vi = new VID_INFO();
						vi.url = url;
						vi.type = type;
						vi.quality = quality;
						vi.name = XDMUtil.getFileName(val);
						list.add(vi);
						url = null;
						type = null;
						quality = null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
