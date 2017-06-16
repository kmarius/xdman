package org.sdg.xdman.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;

import org.sdg.xdman.core.common.XDMConfig;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdvYTDlg extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 662954598636682751L;
	Color bgColor;
	XDMConfig config;

	public AdvYTDlg(XDMConfig config) {
		this.config = config;
		init();
	}

	void init() {
		setSize(450, 300);
		bgColor = Color.WHITE;
		setModal(true);
		JPanel p = new JPanel(null);
		p.setBorder(new LineBorder(Color.GRAY));
		p.setBackground(Color.WHITE);
		add(p);

		setUndecorated(true);
		setResizable(false);
		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setOpaque(false);
		titlePanel.setBounds(1, 1, 448, 50);
		p.add(titlePanel);

		XDMButton closeBtn = new XDMButton();
		closeBtn.setBounds(420, 5, 24, 24);
		closeBtn.setBackground(Color.WHITE);
		closeBtn.setContentAreaFilled(true);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		closeBtn.setRolloverIcon(StaticResource.getIcon("close_btn_r.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		titlePanel.add(closeBtn);

		JLabel titleLbl = new JLabel("ADVANCED YOUTUBE DOWNLOADER");
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setForeground(StaticResource.selectedColor);
		titleLbl.setBounds(25, 15, 350, 30);
		titlePanel.add(titleLbl);

		JLabel lineLbl = new JLabel();
		lineLbl.setBackground(StaticResource.selectedColor);
		lineLbl.setBounds(0, 55, 450, 1);
		lineLbl.setOpaque(true);
		p.add(lineLbl);

		JTextArea txtDesc = new JTextArea();
		txtDesc.setEditable(false);
		txtDesc.setLineWrap(true);
		txtDesc.setWrapStyleWord(true);
		txtDesc
				.setText("XDM will try to download YouTube videos by impersonating Chrome or Firefox as tablet browser.\n\nTo download videos click \"Enable\" and play the video in browser, and disable this when done.");

		// txtDesc.setBackground(Color.WHITE);
		txtDesc.setBounds(20, 70, 420, 170);
		p.add(txtDesc);

		btn = new JButton("Enable");
		updateBtnText();
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.tabletMode = !config.tabletMode;
				updateBtnText();
			}
		});
		btn.setFont(StaticResource.plainFontBig2);
		btn.setBounds(20, 240, 410, 40);
		p.add(btn);

	}

	JButton btn;

	void updateBtnText() {
		btn.setText(config.tabletMode ? "Disable" : "Enable");
	}
}
