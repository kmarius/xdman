package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import org.sdg.xdman.plugin.youtube.YTVideoInfo;

public class YTListRenderer extends JPanel implements ListCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5256235367737001829L;

	JLabel title, status, icon;

	public YTListRenderer() {
		setLayout(new BorderLayout(5, 5));
		setBorder(new EmptyBorder(5, 5, 5, 5));
		icon = new JLabel(XDMIconMap.getIcon("VID"));
		add(icon, BorderLayout.WEST);
		title = new JLabel();
		title.setFont(StaticResource.plainFontBig2);
		status = new JLabel();
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.add(title);
		p.add(status, BorderLayout.SOUTH);
		add(p);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object obj,
			int index, boolean selected, boolean focused) {
		YTVideoInfo info = (YTVideoInfo) obj;
		title.setText(info.name);
		status.setText(info.itag + " " + info.type);// + " " + info.quality);
		if (selected) {
			setBackground(StaticResource.selectedColor);
		} else {
			setBackground(Color.WHITE);
		}
		return this;
	}

}
