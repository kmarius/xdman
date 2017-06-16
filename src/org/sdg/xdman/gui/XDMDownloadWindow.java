package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.DownloadProgressListener;
import org.sdg.xdman.core.common.IDownloader;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.util.XDMUtil;

public class XDMDownloadWindow extends JFrame implements ActionListener, DownloadProgressListener {
	private static final long serialVersionUID = 5894247973832127732L;

	// public static void main(String[] args) {
	// new XDMDownloadWindow().setVisible(true);
	// }

	public XDMDownloadWindow(IDownloader mgr) {
		init();
		this.mgr = mgr;
	}

	public void showWindow() {
		setVisible(true);
	}

	public boolean isValidWindow() {
		return valid;
	}

	public void update(DownloadInfo info) {
		if (info.state == IXDMConstants.COMPLETE || info.state == IXDMConstants.STOPPED
				|| info.state == IXDMConstants.FAILED) {
			valid = false;
			if (info.state == IXDMConstants.FAILED) {
				if (isVisible()) {
					JOptionPane.showMessageDialog(this, info.msg);
				}
			}
			if (mgr != null) {
				mgr.setProgressListener(null);
			}
			mgr = null;
			setVisible(false);
			dispose();
			return;
		}
		url.setText(info.file);
		status.setText(info.status);
		// filesize.setText(info.length);
		downloaded.setText("Downloaded " + info.downloaded + " / " + info.length);
		rate.setText(info.speed);
		time.setText("ETA " + info.eta);
		sp.setValues(info.startoff, info.len, info.dwn, info.rlen);
		cPrg.setValue(info.prg);
		// resume.setText(info.resume);
		// p.setValues(info.startoff, info.len, info.dwn, info.rlen);
		// prg.setValue(info.prg);
		// model.update(info.dwnld, info.stat);
		file = info.path;
	}

	public void actionPerformed(ActionEvent e) {
		JButton b = (JButton) e.getSource();
		if (b.getName().equals("PAUSE")) {
			if (mgr != null)
				mgr.stop();
			setVisible(false);
			dispose();
		} else if (b.getName().equals("PREVIEW")) {
			if (file != null) {
				XDMUtil.open(file);
			}
		}
		if (b.getName().equals("BACKGROUND")) {
			setVisible(false);
		}
	}

	void init() {
		setTitle("Downloading...");
		Color bgColor;
		setSize(350, 250);
		setLocationRelativeTo(null);
		bgColor = new Color(73, 73, 73);
		getContentPane().setLayout(null);
		getContentPane().setBackground(bgColor);
		setUndecorated(true);
		setResizable(false);
		JPanel titlePanel = new TitlePanel(null, this);
		titlePanel.setOpaque(false);
		titlePanel.setBounds(0, 0, 350, 50);

		XDMButton closeBtn = new XDMButton();
		closeBtn.setBounds(320, 5, 24, 24);
		closeBtn.setBackground(bgColor);
		// closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);

		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		closeBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				valid = false;
				if (mgr != null)
					mgr.stop();
				mgr = null;
				setVisible(false);
				dispose();
			}
		});
		titlePanel.add(closeBtn);

		XDMButton minBtn = new XDMButton();
		minBtn.setBounds(296, 5, 24, 24);
		minBtn.setBackground(bgColor);
		// minBtn.setContentAreaFilled(false);
		minBtn.setBorderPainted(false);
		minBtn.setFocusPainted(false);

		minBtn.setIcon(StaticResource.getIcon("min_btn.png"));
		minBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent action) {
				XDMDownloadWindow.this.setExtendedState(XDMDownloadWindow.this.getExtendedState() | JFrame.ICONIFIED);
			};
		});
		titlePanel.add(minBtn);

		JLabel titleLbl = new JLabel("Downloading ...");
		url = titleLbl;
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setForeground(StaticResource.selectedColor);
		titleLbl.setBounds(25, 15, 300, 30);
		titlePanel.add(titleLbl);

		add(titlePanel);

		JLabel lineLbl = new JLabel();
		lineLbl.setBackground(StaticResource.selectedColor);
		lineLbl.setBounds(0, 55, 400, 2);
		lineLbl.setOpaque(true);
		add(lineLbl);

		cPrg = new CircleProgressBar();
		cPrg.setValue(0);

		cPrg.setBounds(20, 80, 72, 72);

		add(cPrg);

		JLabel lblSize = new JLabel("---");
		rate = lblSize;
		lblSize.setHorizontalAlignment(JLabel.CENTER);
		lblSize.setBounds(15, 160, 80, 25);
		lblSize.setForeground(Color.WHITE);

		add(lblSize);

		JLabel lblStat = new JLabel("Downloading...");
		status = lblStat;
		lblStat.setBounds(120, 85, 200, 25);
		lblStat.setForeground(Color.WHITE);

		add(lblStat);

		sp = new SegmentPanel();
		sp.setBounds(120, 115, 200, 5);

		add(sp);

		JLabel lblDet = new JLabel("Downloaded ---/---");
		downloaded = lblDet;
		// lblDet.setHorizontalAlignment(JLabel.CENTER);
		lblDet.setBounds(120, 125, 200, 25);
		lblDet.setForeground(Color.WHITE);

		add(lblDet);

		JLabel lblETA = new JLabel("---");
		time = lblETA;
		lblETA.setBounds(120, 150, 200, 25);
		lblETA.setForeground(Color.WHITE);

		add(lblETA);

		JPanel panel = new JPanel(null);
		panel.setBounds(0, 200, 350, 50);
		panel.setBackground(Color.GRAY);
		add(panel);

		final JButton btnMore = new JButton("Hide"), btnDN = new JButton("Preview"), btnCN = new JButton("Pause");
		btnMore.setBounds(0, 1, 100, 50);
		btnMore.setName("BACKGROUND");
		btnMore.addActionListener(this);
		btnMore.setBackground(bgColor);
		btnMore.setForeground(Color.WHITE);
		btnMore.setFont(StaticResource.plainFontBig);
		btnMore.setBorderPainted(false);
		btnMore.setMargin(new Insets(0, 0, 0, 0));
		btnMore.setFocusPainted(false);
		btnMore.addMouseListener(StaticResource.ma);

		panel.add(btnMore);

		btnDN.setBounds(101, 1, 144, 50);
		btnDN.setName("PREVIEW");
		btnDN.addActionListener(this);
		btnDN.setBackground(bgColor);
		btnDN.setForeground(Color.WHITE);
		btnDN.setBorderPainted(false);
		btnDN.setFont(StaticResource.plainFontBig);
		btnDN.setBorderPainted(false);
		btnDN.setMargin(new Insets(0, 0, 0, 0));
		btnDN.setFocusPainted(false);
		btnDN.addMouseListener(StaticResource.ma);
		// btnDN.addActionListener(this);
		panel.add(btnDN);

		btnCN.setBounds(246, 1, 104, 50);
		btnCN.setName("PAUSE");
		btnCN.setBackground(bgColor);
		btnCN.setForeground(Color.WHITE);
		btnCN.setFont(StaticResource.plainFontBig);
		btnCN.setBorderPainted(false);
		btnCN.setMargin(new Insets(0, 0, 0, 0));
		btnCN.setFocusPainted(false);
		btnCN.addMouseListener(StaticResource.ma);
		btnCN.addActionListener(this);
		panel.add(btnCN);

	}

	CircleProgressBar cPrg;
	SegmentPanel sp;
	private JLabel url, status, downloaded, rate, time;
	IDownloader mgr;
	File file;
	boolean valid = true;

}
