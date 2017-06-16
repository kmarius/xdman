package org.sdg.xdman.plugin.youtube;

public class YTVideoInfo {
	public String url;
	public String type;
	public String itag;
	public String quality;
	public String name;
	public String referer;
	public String ua;

	@Override
	public String toString() {
		return itag + " " + url;
	}
}
