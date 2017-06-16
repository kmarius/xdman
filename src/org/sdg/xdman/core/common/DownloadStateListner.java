package org.sdg.xdman.core.common;

import java.util.ArrayList;
import java.util.UUID;

import org.sdg.xdman.interceptor.DownloadIntercepterInfo;

public interface DownloadStateListner {
	void addDownload(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent);

	void downloadNow(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent);

	void downloadNow2(String url, String url2, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent);

	void add2Queue(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent, boolean q);

	void updateManager(UUID id, Object data);

	void downloadComplete(UUID id);

	void downloadFailed(UUID id);

	void downloadConfirmed(UUID id, Object data);

	void downloadPaused(UUID id);

	void interceptDownload(DownloadIntercepterInfo info);

	void getCredentials(ConnectionManager mgr, String host);

	void configChanged();

	void ytCallback(String yturl);

	void restoreWindow();

	void startQueue();

	void stopQueue();

	void exit();
}
