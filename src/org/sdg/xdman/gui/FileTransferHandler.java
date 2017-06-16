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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;

import org.sdg.xdman.core.common.DownloadStateListner;

/**
 * Data transfer support for the file chooser. Since files are currently
 * presented as a list, the list support is reused with the added flavor of
 * DataFlavor.javaFileListFlavor
 */
public class FileTransferHandler extends XDMTransferHandler {
	private static final long serialVersionUID = -339416811193865790L;

	DownloadList list;

	public FileTransferHandler(DownloadList list, DownloadStateListner mgr) {
		super(mgr);
		this.list = list;
	}

	/**
	 * Create a Transferable to use as the source for a data transfer.
	 * 
	 * @param c
	 *            The component holding the data to be transfered. This argument
	 *            is provided to enable sharing of TransferHandlers by multiple
	 *            components.
	 * @return The representation of the data to be transfered.
	 * 
	 */
	protected Transferable createTransferable(JComponent c) {
		Object[] values = null;
		if (c instanceof JList) {
			values = ((JList) c).getSelectedValues();
		} else if (c instanceof JTable) {
			JTable table = (JTable) c;
			int[] rows = table.getSelectedRows();
			if (rows != null) {
				values = new Object[rows.length];
				for (int i = 0; i < rows.length; i++) {
					// int index = table.convertRowIndexToModel(rows[i]);
					int index = rows[i];
					DownloadListItem item = list.get(index);
					File file = new File(item.saveto, item.filename);
					values[i] = file;// table.getValueAt(rows[i], 0);
				}
			}
		}
		if (values == null || values.length == 0) {
			return null;
		}

		StringBuffer plainBuf = new StringBuffer();
		StringBuffer htmlBuf = new StringBuffer();

		htmlBuf.append("<html>\n<body>\n<ul>\n");

		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			String val = ((obj == null) ? "" : obj.toString());
			plainBuf.append(val + "\n");
			htmlBuf.append("  <li>" + val + "\n");
		}

		// remove the last newline
		plainBuf.deleteCharAt(plainBuf.length() - 1);
		htmlBuf.append("</ul>\n</body>\n</html>");

		return new FileTransferable(plainBuf.toString(), htmlBuf.toString(),
				values);
	}

	public int getSourceActions(JComponent c) {
		return COPY;
	}

	static class FileTransferable extends XDMTransferable {

		Object[] fileData;

		FileTransferable(String plainData, String htmlData, Object[] fileData) {
			super(plainData, htmlData);
			this.fileData = fileData;
		}

		/**
		 * Best format of the file chooser is DataFlavor.javaFileListFlavor.
		 */
		protected DataFlavor[] getRicherFlavors() {
			DataFlavor[] flavors = new DataFlavor[1];
			flavors[0] = DataFlavor.javaFileListFlavor;
			return flavors;
		}

		/**
		 * The only richer format supported is the file list flavor
		 */
		protected Object getRicherData(DataFlavor flavor) {
			if (DataFlavor.javaFileListFlavor.equals(flavor)) {
				ArrayList<Object> files = new ArrayList<Object>();
				for (int i = 0; i < fileData.length; i++) {
					files.add(fileData[i]);
				}
				return files;
			}
			if (DataFlavor.stringFlavor.equals(flavor)) {
				ArrayList<Object> files = new ArrayList<Object>();
				for (int i = 0; i < fileData.length; i++) {
					files.add(fileData[i]);
				}
				return files;
			}
			return null;
		}
	}
}
