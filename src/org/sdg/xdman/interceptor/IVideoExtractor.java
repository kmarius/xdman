package org.sdg.xdman.interceptor;

import java.io.*;
import java.util.*;

public interface IVideoExtractor {
	List<VID_INFO> GetVideoList(InputStream s);
}

class VID_INFO {
	public String url;
	public String quality;
	public String type;
	public String name;
}
