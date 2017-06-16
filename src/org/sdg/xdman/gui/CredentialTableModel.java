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
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.AbstractTableModel;

import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.Credential;

public class CredentialTableModel extends AbstractTableModel implements
		Observer {

	private static final long serialVersionUID = -4277859942575427821L;

	String cols[] = { StringResource.getString("HOST"),
			StringResource.getString("LBL_PROXY_USER") };
	List<Credential> list = new ArrayList<Credential>();

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return cols.length;
	}

	public int getRowCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public Object getValueAt(int row, int col) {
		Credential c = list.get(row);
		switch (col) {
		case 0:
			return c.host;
		case 1:
			return c.user;
		}
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getColumnName(int col) {
		// TODO Auto-generated method stub
		return cols[col];
	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		// TODO Auto-generated method stub
		return String.class;
	}

	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		list.clear();
		Iterator<Credential> it = Authenticator.auth.values().iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		fireTableDataChanged();
	}

	void load() {
		list.clear();
		Iterator<Credential> it = Authenticator.auth.values().iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		fireTableDataChanged();
	}
}
