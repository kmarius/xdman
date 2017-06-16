package org.sdg.xdman.interceptor;

import java.io.*;
import java.util.*;

import org.sdg.xdman.util.*;

public class FBExtractor implements IVideoExtractor {

	public List<VID_INFO> GetVideoList(InputStream s) {
		List<VID_INFO> list = new ArrayList<VID_INFO>();
		try {
			JSONReader jsr = new JSONReader(s);
			while (true) {
				String line = jsr.ReadLine();
				if (line == null)
					break;
				System.out.println(line);
				int index = line.indexOf(':');
				if (index > 0) {
					String key = XDMUtil.trim(line.substring(0, index), "\" ");// line.substring(0,
																				// index).trim(new
																				// char[]
																				// {
																				// '"',
																				// '
																				// '
																				// });
					String val = XDMUtil.trim(line.substring(index + 1), "\" ");// line.substring(index
																				// +
																				// 1).trim(new
																				// char[]
																				// {
																				// '"',
																				// '
																				// '
																				// });
					if (key.startsWith("sd_src")) {
						if (!XDMUtil.isNullOrEmpty(val)) {
							VID_INFO vi = new VID_INFO();
							vi.url = val;
							vi.type = "video/mp4";
							vi.name = XDMUtil.getFileName(val);
							list.add(vi);
						}
						// Console.WriteLine(val);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

}
