package org.sdg.xdman.gui;

import javax.swing.*;
import javax.swing.border.*;

import org.sdg.xdman.util.OSXUtil;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class OSXInstallWindow extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9048168403718918075L;

	public OSXInstallWindow() {
		setSize(300, 300);
		setUndecorated(true);

		JPanel panel = new JPanel(new BorderLayout());

		panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		panel.setBackground(Color.WHITE);

		add(panel);

		TitlePanel tp = new TitlePanel(new BorderLayout(), this);
		tp.setBackground(Color.WHITE);

		JLabel title = new JLabel("XDM setup", JLabel.CENTER);
		title.setForeground(StaticResource.selectedColor);

		title.setBorder(new EmptyBorder(20, 20, 20, 20));
		title.setFont(StaticResource.plainFontBig2);
		tp.add(title, BorderLayout.CENTER);

		panel.add(tp, BorderLayout.NORTH);

		JLabel lblDrag = new JLabel("", JLabel.CENTER);
		lblDrag.setIcon(XDMMainWindow.getIcon("icon.png"));
		OSXAppTransferHandler osxh = new OSXAppTransferHandler();
		lblDrag.setTransferHandler(osxh);
		lblDrag.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				JComponent c = (JComponent) e.getSource();
				TransferHandler th = c.getTransferHandler();

				th.exportAsDrag(c, e, TransferHandler.COPY);
			}
		});

		panel.add(lblDrag);// , BorderLayout.NORTH);

		JLabel info = new JLabel("", JLabel.CENTER);
		info.setFont(StaticResource.plainFont);
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Color.WHITE);

		info
				.setText("<html><center>Drag this icon to Application folder to install XDM as app<br>OR<br>Simply drag the icon to any folder and double click to start</center></html>");
		info.setBorder(new EmptyBorder(20, 20, 30, 20));
		info.setForeground(StaticResource.selectedColor);
		p.add(info);

		Box b = Box.createHorizontalBox();

		JButton btnClose = new JButton("CLOSE");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		b.add(Box.createHorizontalGlue());
		b.add(btnClose, BorderLayout.SOUTH);
		b.add(Box.createHorizontalGlue());
		b.setBorder(new EmptyBorder(10, 10, 10, 10));

		p.add(b, BorderLayout.SOUTH);

		panel.add(p, BorderLayout.SOUTH);

		File folder = new File(System.getProperty("user.home"), ".xdm");
		folder.mkdir();

		OSXUtil.createFixedAppBundle(folder);

		osxh.setAppFolderLocation(new File(folder, "xdm.app"));
	}

	public static void main(String[] args) {
		new OSXInstallWindow().setVisible(true);
	}
}
