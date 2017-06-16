package org.sdg.xdman.interceptor;

import java.util.*;

public class DASHUtility {
	static DASH_INFO lastVid;

	static ArrayList<DASH_INFO> videoQueue = new ArrayList<DASH_INFO>(), audioQueue = new ArrayList<DASH_INFO>();

	static Object lockObject = new Object();

	public static boolean AddToQueue(DASH_INFO info) {
		synchronized (lockObject) {
			if (info.video) {
				for (int i = videoQueue.size() - 1; i >= 0; i--) {
					DASH_INFO di = videoQueue.get(i);
					if (di.clen == info.clen) {
						if (di.id.equals(info.id)) {
							return false;
						}
					}
				}
				videoQueue.add(info);
				return true;
			} else {
				for (int i = audioQueue.size() - 1; i >= 0; i--) {
					DASH_INFO di = audioQueue.get(i);
					if (di.clen == info.clen) {
						if (di.id.equals(info.id)) {
							return false;
						}
					}
				}
				audioQueue.add(info);
				return true;
			}
		}
	}

	public static DASH_INFO GetDASHPair(DASH_INFO info) {
		synchronized (lockObject) {
			if (info.video) {
				if (audioQueue.size() < 1)
					return null;
				for (int i = audioQueue.size() - 1; i >= 0; i--) {
					DASH_INFO di = audioQueue.get(i);
					if (di.id.equals(info.id)) {
						return di;
					}
				}
			} else {
				if (videoQueue.size() < 1)
					return null;
				for (int i = videoQueue.size() - 1; i >= 0; i--) {
					DASH_INFO di = videoQueue.get(i);
					if (di.id.equals(info.id)) {
						if ((lastVid != null) && (lastVid.clen == di.clen)) {
							return null;
						}
						lastVid = di;
						return di;
					}
				}
			}
			return null;
		}
	}

}

class DASH_INFO {
	public String url;
	public long clen;
	public boolean video;
	public String id;
	public int itag;
	public String mime;
}
