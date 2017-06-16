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

import javax.swing.table.AbstractTableModel;

public class ConnectionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -3901959955250134521L;
	String stat[] = {}, dwn[] = {};
	String cols[] = { "#", "Downloaded", "info" };

	@Override
	public String getColumnName(int col) {
		return cols[col];
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return String.class;
	}

	
	public int getColumnCount() {
		return cols.length;
	}

	
	public int getRowCount() {
		return dwn.length;
	}

	
	public Object getValueAt(int row, int col) {
		try {
			switch (col) {
			case 0:
				return row;
			case 1:
				return dwn[row];
			case 2:
				return stat[row];
			default:
				return "";
			}
		} catch (Exception e) {
			return "";
		}
	}

	public void update(String d[], String s[]) {
		dwn = d;
		stat = s;
		fireTableDataChanged();
	}

}
