package org.sdg.xdman.interceptor;

import java.util.ArrayList;

public interface IMediaGrabber {

	void mediaCaptured(String title, String url, String url2, String type, String size, String referer, String ua,
			ArrayList<String> cookies);

	void showGrabber();

	void showNotification();

	void showNotificationText(String text, String title);

}
