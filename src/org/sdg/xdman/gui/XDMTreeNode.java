package org.sdg.xdman.gui;

import javax.swing.tree.DefaultMutableTreeNode;

public class XDMTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 7303622050626960257L;

	public XDMTreeNode(String str) {
		super(str);
	}

	public String id;
}
