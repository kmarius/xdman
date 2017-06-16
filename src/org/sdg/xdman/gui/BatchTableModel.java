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

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class BatchTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1897735268013212330L;

	String cols[] = { "#", StringResource.getString("FILE"),
			StringResource.getString("SAVE_IN"),
			StringResource.getString("URL") };
	List<BatchItem> batchList = new LinkedList<BatchItem>();

	@Override
	public Class<?> getColumnClass(int c) {
		if (c == 0) {
			return Boolean.class;
		}
		return String.class;
	}

	@Override
	public String getColumnName(int c) {
		return cols[c];
	}

	public int getColumnCount() {
		return cols.length;
	}

	public int getRowCount() {
		return batchList.size();
	}

	public Object getValueAt(int r, int c) {
		try {
			BatchItem item = batchList.get(r);
			switch (c) {
			case 0:
				return (Boolean) item.selected;
			case 1:
				return item.fileName;
			case 2:
				return item.dir;
			case 3:
				return item.url;
			default:
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

}
