package org.sdg.xdman.core.common.hls;

public class HLS_SEGMENT {
	public int sequence;
    public String url;
    public long size;
    public long downloaded;
    public String tempfile;
    public boolean done;
    public float duration;
    public BasicDownloader bd;
}
