package org.sdg.xdman.core.common.dash;

public interface ISegmentProgress {
	void connected(ISegmentDownloader sd);

	void downloaded(ISegmentDownloader sd, long downloaded);

	void downloadComplete(ISegmentDownloader sd) throws Exception;

	void downloadFailed(ISegmentDownloader sd);
}
