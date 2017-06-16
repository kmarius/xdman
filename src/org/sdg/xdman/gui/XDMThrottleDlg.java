package org.sdg.xdman.gui;

import javax.swing.*;

import org.sdg.xdman.core.common.XDMConfig;

import java.awt.*;
import java.awt.event.*;

public class XDMThrottleDlg extends JDialog {

	private static final long serialVersionUID = -4516761235891212218L;
	XDMConfig config;

	public XDMThrottleDlg(XDMConfig config) {
		setModal(true);
		this.config = config;
		setUndecorated(true);
		setSize(400, 150);
		getContentPane().setLayout(null);
		getContentPane().setBackground(Color.black);
		JPanel panel = new JPanel(null);
		panel.setBounds(1, 1, 398, 148);
		panel.setBackground(Color.WHITE);
		add(panel);

		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setBackground(StaticResource.titleColor);
		titlePanel.setBounds(0, 0, 398, 50);

		JLabel titleLbl = new JLabel("XDM SPEED LIMITER", JLabel.CENTER);
		titleLbl.setFont(StaticResource.plainFontBig);
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setBounds(0, 0, 398, 50);
		titlePanel.add(titleLbl);
		panel.add(titlePanel);

		JLabel lbl = new JLabel(
				"Maximum download speed [KB/Sec] (0 unlimited)", JLabel.RIGHT);
		lbl.setBounds(10, 70, 320, 20);
		panel.add(lbl);

		final JTextField txtSpeed = new JTextField(10);
		txtSpeed.setBounds(340, 70, 50, 20);
		panel.add(txtSpeed);
		txtSpeed.setText((config.maxBPS / 1024) + "");

		final JButton okBtn = new JButton("OK");
		okBtn.setBounds(200, 115, 90, 25);
		panel.add(okBtn);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int speed = Integer.parseInt(txtSpeed.getText())*1024;
					if (speed < 0) {
						throw new Exception();
					}
					XDMThrottleDlg.this.config.maxBPS = speed;
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null,
							"Please enter whole number only");
					return;
				}
				dispose();
			}
		});

		JButton closeBtn = new JButton("CANCEL");
		closeBtn.setBounds(300, 115, 90, 25);
		panel.add(closeBtn);
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
}
