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

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractListModel;

public class HelpListModel extends AbstractListModel {

	private static final long serialVersionUID = 5679536364566414367L;

	HashMap<String, URL> map = new HashMap<String, URL>();

	public URL getLinkURL(String pageName) {
		return map.get(pageName);
	}

	public Iterator<String> listPages() {
		return map.keySet().iterator();
	}

	
	public Object getElementAt(int r) {
		return map.keySet().toArray()[r] + "";
	}

	
	public int getSize() {
		return map.size();
	}

}
