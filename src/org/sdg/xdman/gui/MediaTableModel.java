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

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class MediaTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 3687589857430853297L;

	ArrayList<MediaInfo> list = new ArrayList<MediaInfo>();

	String cols[] = { StringResource.getString("FILE_NAME"),
			StringResource.getString("INFO"), StringResource.getString("URL") };

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	public int getColumnCount() {
		return cols.length;
	}

	public int getRowCount() {
		return list.size();
	}

	public Object getValueAt(int arg0, int arg1) {
		MediaInfo info = list.get(arg0);
		switch (arg1) {
		case 0:
			return info.name;
		case 1:
			return (info.type == null ? "" : info.type) + " "
					+ (info.size == null ? "" : info.size);
		case 2:
			return info.url;
		}
		return "";
	}

	public synchronized void add(MediaInfo info) {
		for (int i = 0; i < list.size(); i++) {
			MediaInfo mi = list.get(i);
			if (mi.url.equals(info.url)) {
				return;
			}
		}
		synchronized (list) {
			list.add(info);
		}
		fireTableDataChanged();
	}

	public void remove(int index) {
		synchronized (list) {
			list.remove(index);
		}
		fireTableDataChanged();
	}

	public void clear() {
		synchronized (list) {
			list.clear();
		}
		fireTableDataChanged();
	}

	@Override
	public String getColumnName(int c) {
		return cols[c];
	}

}
