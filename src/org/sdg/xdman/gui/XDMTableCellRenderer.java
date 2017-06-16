package org.sdg.xdman.gui;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class XDMTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 6316045936375094809L;

	@Override
	public Component getTableCellRendererComponent(JTable arg0, Object arg1,
			boolean arg2, boolean arg3, int arg4, int arg5) {
		if((arg1+"").contains("other.png")){
			System.out.println(arg5);
		}
		Component c = super.getTableCellRendererComponent(arg0, arg1, arg2,
				arg3, arg4, arg5);
		if (c instanceof JComponent) {
			((JComponent) c).setBorder(null);
		}
		return c;
	}
}
