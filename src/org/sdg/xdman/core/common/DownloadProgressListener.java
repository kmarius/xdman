package org.sdg.xdman.core.common;

public interface DownloadProgressListener {
	void update(DownloadInfo info);

	boolean isValidWindow();
}
