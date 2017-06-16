package org.sdg.xdman.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class XDMLinkRefreshDialog extends JDialog {
	int diffx, diffy;

	public XDMLinkRefreshDialog() {
		setUndecorated(true);
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				diffx = me.getXOnScreen() - getLocationOnScreen().x;
				diffy = me.getYOnScreen() - getLocationOnScreen().y;
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent me) {
				setLocation(me.getXOnScreen() - diffx, me.getYOnScreen() - diffy);
			}
		});

		getContentPane().setBackground(new Color(40, 40, 40));

		setLayout(null);

		setSize(410, 350);

		JLabel title = new JLabel("REFRESH LINK");
		title.setFont(StaticResource.plainFontBig2);
		title.setForeground(Color.WHITE);
		title.setBounds(35, 30, 300, 30);
		add(title);

	}

}
