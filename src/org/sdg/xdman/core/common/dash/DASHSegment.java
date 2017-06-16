package org.sdg.xdman.core.common.dash;

public class DASHSegment {
	String url;
	long offset, length;
	String filename;
	int id;
	boolean finished;
	boolean first;
	SegmentDownloader sd;
}
