package org.sdg.xdman.core.common.dash;

public interface ISegmentDownloader {
	void start();

	void stop();

	long getSize();
}
