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

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.sdg.xdman.core.common.DownloadInfo;

public class MainTableModel extends AbstractTableModel {
	DownloadList list = null;// new DownloadList();
	Icon q;

	public void setList(DownloadList list) {
		this.list = list;
		fireTableDataChanged();
	}

	void setType(String type) {
		list.setType(type);
	}

	@Override
	public Class<?> getColumnClass(int col) {
		return DownloadListItem.class;
	}

	private static final long serialVersionUID = -8936395745120671317L;
	final String cols[] = { "" };

	@Override
	public String getColumnName(int col) {
		return cols[col];
	}

	public int getColumnCount() {
		return cols.length;
	}

	public int getRowCount() {
		if (list == null)
			return 0;
		//System.out.println("list size: "+list.size());
		return list.size();
	}

	public Object getValueAt(int row, int col) {
		DownloadListItem item = list.get(row);
		return item;
	}

	public void updateItem(DownloadInfo info) {
		DownloadListItem item = list.getByID(info.id);
		if (item == null) {
			return;
		}
		item.updateData(info);
		int index = list.getIndex(item);
		if (index < 0)
			return;
		fireTableRowsUpdated(index, index);
	}

	String getString(String id) {
		return StringResource.getString(id);
	}
}
