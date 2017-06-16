package org.sdg.xdman.core.common.hls;

import org.sdg.xdman.core.common.InvalidContentException;

public interface IHLSProgress {
	void setDownloaded(long downloaded);
    void downloadFailed(BasicDownloader bd);
    void downloadComplete(BasicDownloader bd) throws InvalidContentException;
}
