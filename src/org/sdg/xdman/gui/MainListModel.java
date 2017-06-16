package org.sdg.xdman.gui;

import javax.swing.*;

import org.sdg.xdman.core.common.DownloadInfo;

public class MainListModel extends AbstractListModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5930766243148331109L;
	DownloadList list = null;
	Icon q;

	public MainListModel(DownloadList list) {
		this.list = list;
	}

	@Override
	public Object getElementAt(int row) {
		DownloadListItem item = list.get(row);
		return item;
	}

	void fireListItemUpdated(int index) {
		fireContentsChanged(this, index, index);
	}

	@Override
	public int getSize() {
		if (list == null)
			return 0;
		//System.out.println("size: "+list.size());
		return list.size();
	}

	public void setType(String type) {
		list.setType(type);
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
		fireContentsChanged(this, index, index);
	}
}
