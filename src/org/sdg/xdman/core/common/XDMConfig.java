/*
 * Copyright (c)  Subhra Das Gupta
 *
 * This file is part of Xtream Download Manager.
 *
 * Xtream Download Manager is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Xtream Download Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with Xtream Download Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.sdg.xdman.core.common;

import java.io.*;
import java.util.Date;
import java.util.Observable;

public class XDMConfig extends Observable implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8667244115569025116L;
	public static boolean hasTray = false;
	public String lafClass, lafName;
	File file;
	public String jarPath;
	public boolean useProxyPAC = false;//true;
	public String proxyPAC = null;//"http://BC-PROXY-VIP.de.pri.o2.com:8081/accelerated_pac_base.pac";
	public boolean useProxy;
	public String proxyHost;
	public int proxyPort;
	public String proxyUser, proxyPass;

	// private static final long serialVersionUID = -4029431942579013439L;
	public boolean showDownloadPrgDlg = true;
	public boolean showDownloadPrgNotifySend = true;
	public boolean showDownloadCompleteDlg = true;
	public boolean showDownloadCompleteNotifySend = true;
	public boolean showDownloadBox = false;

	public static final int PROMPT = 3, AUTO_RENAME = 0, RESUME = 2, OVERWRITE = 1;

	public int duplicateLinkAction = PROMPT;

	public int maxConn = 8, timeout = 60;

	public String destdir, tempdir;

	public boolean executeCmd = false, hungUp = false, halt = false, antivir = false;

	public String cmdTxt, hungUpTxt, haltTxt = "", antivirTxt;

	public int antidrop = 0, hungdrop = 0, haltdrop = 0;

	public String ntDomain = "";

	public final String defaultFileTypes[] = { "3GP", "7Z", "AAC", "ACE", "AIF", "ARJ", "ASF", "AVI", "BIN", "BZ2",
			"EXE", "DEB", "DMG", "GZ", "GZIP", "ISO", "LZH", "M4A", "M4V", "MOV", "MP3", "MPA", "MPE", "MPEG", "MPG",
			"MSI", "MSU", "OGG", "PDF", "PLJ", "PPS", "PPT", "QT", "RA", "RAR", "RM", "RPM", "SEA", "SIT", "SITX",
			"TAR", "TIF", "TIFF", "WAV", "WMA", "WMV", "Z", "ZIP", "JAR", "TLZ", "TBZ2", "TXZ", "XZ", "CBZ", "PKG",
			"RUN" };

	public String fileTypes[] = defaultFileTypes;

	public boolean schedule;
	public Date startDate, endDate;
	public boolean allowbrowser = false;
	public transient int port = 9614;
	public int tcpBuf = 64;
	public boolean compress = false;
	public boolean attachProxy = false;
	public boolean autostart = false;

	public String getDefaultShutdownCommand() {
		return "dbus-send --system --print-reply --dest=\"org.freedesktop.ConsoleKit\" /org/freedesktop/ConsoleKit/Manager org.freedesktop.ConsoleKit.Manager.Stop";
	}

	public String getDefaultDisconnectCommand() {
		return "";
	}

	public XDMConfig(File f) {
		file = f;
		haltTxt = getDefaultShutdownCommand();
		hungUpTxt = getDefaultDisconnectCommand();
		mwX = mwY = mwW = mwH = mgX = mgY = mgW = mgH = dbX = dbY = -1;
	}

	public void save() {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(this);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		setChanged();
		notifyObservers();
	}

	public int version = 0;

	public static XDMConfig load(File file) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			XDMConfig c = (XDMConfig) in.readObject();
			c.port = 9614;
			c.version = 4;
			c.tabletMode = false;
			c.halt = false;
			in.close();
			return c;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				in.close();
			} catch (Exception e2) {
			}
		}

		return new XDMConfig(file);
	}

	public static boolean dubugMode = false;

	public String[] siteList = {};

	public int mwX, mwY, mwW, mwH, mgX, mgY, mgW, mgH, dbX, dbY;

	public boolean tabletMode = false;

	public transient static boolean sortAsc = false;

	public transient static int sortField = 0;

	public int maxBPS = 0;

	public String tabletURL = "Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";

	public boolean browserInt;

	public boolean firstRun = true;
}
