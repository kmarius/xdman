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

package org.sdg.xdman.gui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.Icon;

import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.IDownloader;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

public class DownloadListItem implements Serializable, Comparable<DownloadListItem> {
	private static final long serialVersionUID = -4925098929484510725L;
	ArrayList<String> cookies;
	String filename;
	boolean q;
	int state;
	UUID id;
	String status, timeleft, transferrate, lasttry, description, dateadded, saveto, type, url, size, tempdir = "",
			referer, userAgent, user, pass;
	transient Icon icon;
	transient IDownloader mgr;
	transient XDMDownloadWindow window;
	long date_created = System.currentTimeMillis();
	public String sdwnld, sprg;

	public String prime_url, second_url;
	public String prime_file, second_file;
	public String prime_dest_dir, second_dest_dir;
	public boolean secondary_done;
	public long totalDASHSize;
	public boolean isMerging;
	public int dtype;
	public int lenth1,length2;

	public long length, dwnld;
	public long filesize,downloaded;

	void updateData(DownloadInfo info) {
		this.status = info.status;
		this.timeleft = info.eta;
		this.transferrate = info.speed;
		this.url = info.url;
		this.size = info.length;
		this.type = info.category;
		this.state = info.state;
		this.sdwnld = info.downloaded;
		this.sprg = info.progress;
		this.filesize=info.rlen;
		this.downloaded=info.rdwn;
		if (info.state == IXDMConstants.COMPLETE || info.state == IXDMConstants.STOPPED
				|| info.state == IXDMConstants.FAILED) {
			this.mgr = null;
			this.window = null;
			if (info.state == IXDMConstants.COMPLETE) {
				q = false;
				this.status = StringResource.getString("DOWNLOAD_COMPLETE") + " " + size;// Download
				// complete";
			} else {
				this.status = StringResource.getString("STOPPED")
						+ (XDMUtil.isNullOrEmpty(this.sprg) ? " --- of " : " " + this.sprg + "% of ") + size;// "Stopped";
			}
		} else {
			this.status += " " + this.sprg + "% of " + this.size;
		}
	}

	@Override
	public int compareTo(DownloadListItem item) {
		int c = XDMConfig.sortField;
		switch (c) {
		case 0:
			return this.date_created > item.date_created ? 1 : -1;
		case 1:
			return XDMUtil.nvl(size).compareToIgnoreCase(XDMUtil.nvl(item.size));
		case 2:
			return XDMUtil.nvl(filename).compareToIgnoreCase(XDMUtil.nvl(item.filename));
		case 3:
			// System.out.println(getExt(this.filename) + " --- "
			// + getExt(item.filename));
			return getExt(this.filename).compareToIgnoreCase(getExt(item.filename));
		}

		return 0;
	}

	String getExt(String name) {
		try {

			String arr[] = name.split("\\.");
			// System.out.println("lll "+arr.length+" "+name);
			if (arr.length > 1) {
				return arr[arr.length - 1];
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
			// TODO: handle exception
		}
	}

	// public void update(Observable o, Object obj) {
	// if (this.mgr == null) {
	// return;
	// }
	// DownloadInfo info = (DownloadInfo) obj;
	// this.status = info.status;
	// this.timeleft = info.eta;
	// this.transferrate = info.speed;
	// this.url = info.url;
	// this.size = info.length;
	// this.type = info.category;
	// this.state = info.state;
	// if (info.state == IXDMConstants.COMPLETE
	// || info.state == IXDMConstants.STOPPED
	// || info.state == IXDMConstants.FAILED) {
	// this.mgr = null;
	// this.window = null;
	// System.out.println("removed connection mgr");
	// if (info.state == IXDMConstants.COMPLETE) {
	// q = false;
	// this.status = "Download complete";
	// } else
	// this.status = "Stopped";
	// if (listener != null)
	// listener.downloadStateChanged();
	// listener = null;
	// }
	// if (info.state == IXDMConstants.REDIRECTING) {
	// tempdir = info.tempdir;
	// filename = info.file;
	// icon = IconUtil.getIcon(XDMUtil.findCategory(filename));
	// if (listener != null)
	// listener.downloadStateChanged();
	// }
	// setChanged();
	// notifyObservers(this);
	// }
}
