package org.sdg.xdman.gui;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

public class XDMTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -1014567003371655959L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean leaf, boolean expanded, int row,
			boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, selected, leaf,
				expanded, row, hasFocus);
		XDMTreeNode node = (XDMTreeNode) value;
		String id = node.id;
		if (id.equals("TREE_DOCUMENTS")) {
			setIcon(XDMIconMap.getIcon("DOC"));
		} else if (id.equals("TREE_MUSIC")) {
			setIcon(XDMIconMap.getIcon("MUSIC"));
		} else if (id.equals("TREE_PROGRAMS")) {
			setIcon(XDMIconMap.getIcon("APP"));
		} else if (id.equals("TREE_VIDEOS")) {
			setIcon(XDMIconMap.getIcon("VID"));
		} else if (id.equals("TREE_COMPRESSED")) {
			setIcon(XDMIconMap.getIcon("ZIP"));
		} else if (id.equals("TREE_DOCUMENTS")) {
			setIcon(XDMIconMap.getIcon("DOC"));
		} else {
			setIcon(XDMIconMap.getIcon("FOLDER"));
		}
		return this;
	}

	public XDMTreeCellRenderer() {
		setBorder(new EmptyBorder(2, 0, 2, 0));
	}
}
