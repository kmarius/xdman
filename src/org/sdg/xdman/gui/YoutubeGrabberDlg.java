/*
 * Copyright (c)  Subhra Das Gupta
 *
 * This file is part of Xtream Download Manager.
 *
 * Xtream Download Manager is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Xtream Download Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with Xtream Download Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package org.sdg.xdman.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.http.XDMHttpClient2;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;
import org.sdg.xdman.interceptor.HTMLEscapeUtil;
import org.sdg.xdman.interceptor.HTMLTitleParser;
import org.sdg.xdman.plugin.youtube.JSONParser;
import org.sdg.xdman.plugin.youtube.ParserProgressListner;
import org.sdg.xdman.plugin.youtube.YTVideoInfo;
import org.sdg.xdman.util.XDMUtil;

public class YoutubeGrabberDlg extends XDMFrame implements ActionListener,
		ParserProgressListner, Runnable {
	private static final long serialVersionUID = -1072376334080340930L;
	Dimension btnDim;
	CardLayout card;
	JPanel p1;
	JPanel p2;
	JPanel p3;
	JTextField ytaddr;
	JButton get_video, cancel, abort;
	JPanel p;
	String info1 = StringResource.getString("YT_LBL1"), info2 = StringResource
			.getString("YT_LBL2");
	XDMConfig config;
	Thread ytd;
	JProgressBar prg;
	JTextField lbl;

	public YoutubeGrabberDlg(DownloadStateListner mg) {
		super();
		this.dl = mg;
		setSize(420, 350);
		JLabel titleLbl = new JLabel(StringResource.getString("YT_LBL3"));
		titleLbl.setForeground(Color.WHITE);
		titleLbl.setFont(StaticResource.plainFontBig2);
		titleLbl.setBorder(new EmptyBorder(10, 10, 10, 10));
		getTitlePanel().add(titleLbl);
		createP1();
		createP2();
		createP3();
		card = new CardLayout();
		p = new JPanel(card);
		p.add(p1, "1");
		p.add(p2, "2");
		p.add(p3, "3");
		add(p);
		try {
			setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		} catch (Exception e) {
		}
		card.show(p, "1");
	}

	void showDialog(JFrame f, XDMConfig config, String txt2) {
		if (available) {
			card.show(p, "1");
			ytaddr.setText(txt2);
			setLocationRelativeTo(f);
			String txt = info2 + " 0 KB";
			lbl.setText(txt);
			this.config = config;
			// pack();
			setSize(420, 350);// restrict width
			setVisible(true);
		}
	}

	void createP1() {
		Box p11 = Box.createVerticalBox();
		p11.setBackground(Color.white);
		p11.setOpaque(true);
		p1 = new JPanel(new BorderLayout());
		Box b1 = Box.createHorizontalBox();
		b1.setBackground(Color.white);
		b1.setOpaque(true);
		b1.setBorder(new EmptyBorder(25, 15, 10, 15));
		JLabel lbl = new JLabel();
		lbl.setText(StringResource.getString("YT_LBL1"));
		lbl.setOpaque(false);
		JLabel icon = new JLabel(XDMIconMap.getIcon("YT_ICON"));
		icon.setMaximumSize(new Dimension(75, 75));
		b1.add(icon);
		b1.add(Box.createRigidArea(new Dimension(10, 10)));
		b1.add(lbl);
		ytaddr = new JTextField();
		ytaddr.setMaximumSize(new Dimension(ytaddr.getMaximumSize().width,
				ytaddr.getPreferredSize().height));
		p11.add(b1);
		Box b2 = Box.createHorizontalBox();
		b2.add(ytaddr);
		b2.setBackground(Color.white);
		b2.setOpaque(true);
		b2.setBorder(new EmptyBorder(10, 15, 15, 15));
		p11.add(b2);
		p11.add(Box.createVerticalStrut(20));

		p1.add(p11);

		Box b3 = Box.createHorizontalBox();
		get_video = new JButton(StringResource.getString("YT_LBL4"));
		btnDim = get_video.getPreferredSize();
		get_video.setName("YT_LBL4");
		get_video.addActionListener(this);
		cancel = new JButton(StringResource.getString("CANCEL"));
		cancel.setName("CANCEL");
		cancel.addActionListener(this);
		cancel.setPreferredSize(btnDim);
		b3.add(Box.createHorizontalGlue());
		b3.add(get_video);
		b3.add(Box.createRigidArea(new Dimension(10, 10)));
		b3.add(cancel);
		b3.setBorder(new EmptyBorder(10, 15, 10, 15));
		b3.setOpaque(true);
		b3.setBackground(StaticResource.titleColor);

		p1.add(b3, BorderLayout.SOUTH);
	}

	void createP2() {

		p2 = new JPanel(new BorderLayout());
		Box p22 = Box.createVerticalBox();
		Box b0 = Box.createHorizontalBox();
		b0.setOpaque(true);
		b0.setBackground(Color.white);
		Box b1 = Box.createVerticalBox();
		b1.setOpaque(true);
		b1.setBackground(Color.white);
		b0.setBorder(new EmptyBorder(25, 15, 10, 15));

		// lbl = new JLabel();
		// lbl.setText(StringResource.getString("YT_LBL2"));
		// lbl.setOpaque(false);
		//		
		lbl = new JTextField();
		lbl.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		lbl.setOpaque(false);
		lbl.setEditable(false);
		lbl.setBorder(null);
		// lbl.setHorizontalAlignment(JLabel.LEFT);

		lbl.setText(StringResource.getString("YT_LBL2"));

		JLabel icon = new JLabel(XDMIconMap.getIcon("YT_ICON"));
		icon.setMaximumSize(new Dimension(75, 75));

		Box bb = Box.createVerticalBox();
		bb.add(icon);
		bb.add(Box.createVerticalGlue());
		b0.add(bb);

		b0.add(Box.createRigidArea(new Dimension(10, 10)));

		prg = new JProgressBar();
		prg.setIndeterminate(true);
		prg.setPreferredSize(new Dimension(15, 15));
		prg.setMinimumSize(new Dimension(0, 15));
		prg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 15));

		JTextArea label = new JTextArea(StringResource.getString("YT_LBL9"));
		label.setEditable(false);
		label.setWrapStyleWord(true);
		label.setOpaque(false);
		label.setBorder(null);
		label.setLineWrap(true);

		lbl.setPreferredSize(new Dimension(label.getPreferredSize().width, 50));

		// JPanel p=new JPanel(new BorderLayout());
		// p.add(lbl);
		//		
		b1.add(lbl);

		b1.add(label);

		lbl.setText(StringResource.getString("YT_LBL2"));

		b1.add(Box.createVerticalStrut(20));
		b1.add(prg);
		b1.add(Box.createVerticalStrut(15));

		// b1.add(b);

		b0.add(b1);

		Box b3 = Box.createHorizontalBox();
		abort = new JButton(StringResource.getString("CANCEL"));
		if (btnDim != null) {
			abort.setPreferredSize(btnDim);
		}
		abort.setName("ABORT");
		abort.addActionListener(this);
		b3.add(Box.createHorizontalGlue());
		b3.add(abort);
		b3.setBorder(new EmptyBorder(10, 15, 10, 15));
		b3.setOpaque(true);
		b3.setBackground(StaticResource.titleColor);

		p22.add(b0);
		p2.add(p22);
		p2.add(b3, BorderLayout.SOUTH);
	}

	JList list;

	DefaultListModel model;

	JPanel createP3() {
		p3 = new JPanel(new BorderLayout());
		model = new DefaultListModel();
		list = new JList(model);
		list.setCellRenderer(new YTListRenderer());
		p3.add(new JScrollPane(list));
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalGlue());
		btnDwnld = new JButton("Download");
		btnDwnld.addActionListener(this);
		btnCancel = new JButton("Close");
		btnCancel.addActionListener(this);
		box.add(btnDwnld);
		box.add(Box.createHorizontalStrut(10));
		box.add(btnCancel);
		btnCancel.setPreferredSize(btnDwnld.getPreferredSize());
		box.add(Box.createHorizontalStrut(10));
		box.add(Box.createRigidArea(new Dimension(0, 40)));
		p3.add(box, BorderLayout.SOUTH);

		box.setOpaque(true);
		box.setBackground(StaticResource.titleColor);
		return p3;
	}

	JButton btnDwnld, btnCancel;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == get_video) {
			String addr = ytaddr.getText();
			if (addr.length() < 1) {
				JOptionPane.showMessageDialog(this, StringResource
						.getString("URL_EMPTY"));
				return;
			}
			if (!XDMUtil.validateURL(addr)) {
				JOptionPane.showMessageDialog(this, StringResource
						.getString("YT_LBL5"));
				return;
			}
			if (!(addr.startsWith("http://www.youtube.com/watch") || addr
					.startsWith("https://www.youtube.com/watch"))) {
				JOptionPane.showMessageDialog(this, StringResource
						.getString("YT_LBL5"));
				return;
			}
			card.show(p, "2");
			ytd = new Thread(this);
			ytd.start();
		}
		if (e.getSource() == abort) {
			if (ytd != null) {
				ytd.interrupt();
				setVisible(false);
			}
			setVisible(false);
		}
		if (e.getSource() == cancel) {
			setVisible(false);
		}
		if (e.getSource() == btnDwnld) {
			int index = list.getSelectedIndex();
			if (index < 0) {
				JOptionPane.showMessageDialog(this,
						"Please select an item to download");
				return;
			}
			YTVideoInfo info = (YTVideoInfo) model.get(index);
			DownloadIntercepterInfo di = new DownloadIntercepterInfo();
			di.url = info.url;
			di.referer = info.ua;
			di.referer = info.referer;
			dl.interceptDownload(di);
		}
		if (e.getSource() == btnCancel) {
			setVisible(false);
		}
	}

	DownloadStateListner dl;

	// public void parsingComplete(ArrayList<YTVideoInfo> list) {
	// Iterator<YTVideoInfo> it = list.iterator();
	// setVisible(false);
	// if (mg != null) {
	// while (it.hasNext()) {
	// YTVideoInfo info = it.next();
	// mg
	// .mediaCaptured(
	// info.url,
	// info.type,
	// info.itag + " " + info.quality,
	// ytaddr.getText(),
	// "Mozilla/5.0 (Windows NT 6.1; rv:31.0) Gecko/20100101 Firefox/31.0",
	// null);
	// mg.showGrabber();
	// }
	// }
	// }

	// public void parsingFailed() {
	// if (isVisible()) {
	// JOptionPane.showMessageDialog(this, StringResource
	// .getString("YT_LBL6"));
	// card.show(p, "1");
	// }
	// }

	public void update(long downloaded) {
		String txt = info2 + XDMUtil.getFormattedLength(downloaded);
		lbl.setText(txt);
	}

	byte[] b = new byte[8192];

	boolean network_err = true;

	@Override
	public void run() {
		grabVideo();
	}

	boolean available = true;
	private XDMHttpClient2 client;

	// http://www.youtube.com/watch?v=LzGxL2GaglQ

	void grabVideo() {
		network_err = true;
		String tmpFile = null;
		InputStream tmpStreamIn = null;
		OutputStream tmpStreamOut = null;
		ArrayList<YTVideoInfo> list = null;
		String title = "";
		int count = 0;
		String ua = // "Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";//
		"Mozilla/5.0 (Windows NT 6.1; rv:31.0) Gecko/20100101 Firefox/31.0";
		String url = ytaddr.getText();
		try {
			do {
				available = false;
				client = new XDMHttpClient2(config);
				client.addRequestHeaders("accept-encoding", "gzip");
				if (count == 2) {
					ua = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
				} else if (count == 1) {
					ua = "Mozilla/5.0 (iPad; CPU OS 5_0 like Mac OS X) AppleWebKit/534.46 (KHTML, like Gecko) Version/5.1 Mobile/9A334 Safari/7534.48.3";//	
				}
				client.addRequestHeaders("user-agent", ua);
				client.connect(url);
				client.sendRequest();
				if (Thread.interrupted()) {
					throw new InterruptedException();
				}
				int rc = client.getResponseCode();
				if (rc == 302 || rc == 301) {
					url = client.getResponseHeader("location");
					client.close();
					continue;
				}
				if (!(rc == 200 || rc == 206)) {
					throw new Exception("Invalid response: " + rc);
				}
				InputStream in = client.in;
				tmpFile = XDMUtil.getTempFile(config.tempdir)// File.createTempFile("tmp",
						// "tmp",
						// null)XDMU
						.getAbsolutePath();
				tmpStreamOut = new FileOutputStream(tmpFile);
				long read = 0;
				while (true) {
					int x = in.read(b, 0, b.length);
					if (x == -1)
						break;
					read += x;
					tmpStreamOut.write(b, 0, x);
					update(read);
					if (Thread.interrupted()) {
						throw new InterruptedException();
					}
				}

				tmpStreamOut.close();
				client.close();
				network_err = false;

				tmpStreamIn = new FileInputStream(tmpFile);
				BufferedReader r = new BufferedReader(new InputStreamReader(
						tmpStreamIn));
				title = HTMLTitleParser.GetTitleFromPage(r);
				title = HTMLEscapeUtil.escapeHTMLLine(title);
				title = XDMUtil.createSafeFileName(title);
				tmpStreamIn.close();
				tmpStreamIn = new FileInputStream(tmpFile);
				JSONParser p = new JSONParser();
				list = p.list(tmpStreamIn);
				if (list != null && list.size() > 0) {
					break;
				} else {
					try {
						tmpStreamIn.close();
					} catch (Exception exx) {
					}
					if (tmpFile != null)
						new File(tmpFile).delete();
				}
				count++;
				// System.out.println("Retrying with mobile ua");
			} while (count < 3);
			if (list == null || list.size() < 1) {
				showError();
				new File(tmpFile).delete();
				return;
			}

			model.clear();

			for (int i = 0; i < list.size(); i++) {
				YTVideoInfo info = (YTVideoInfo) list.get(i);
				info.referer = url;
				info.ua = ua;
				info.name = XDMUtil.getFileName(info.url);
				if (info == null)
					continue;
				model.addElement(info);
				// if (mg != null) {
				// mg.mediaCaptured(title, info.url, info.type, info.itag,
				// url, ua, null);
				// mg.showGrabber();
				// }
			}
			card.show(p, "3");
		} catch (Exception e) {
			e.printStackTrace();
			if (!(e instanceof InterruptedException)) {
				showError();
			}
			setVisible(false);
		} finally {
			try {
				tmpStreamIn.close();
			} catch (Exception ee) {
			}
			try {
				tmpStreamOut.close();
			} catch (Exception ee) {
			}
			try {
				client.close();
			} catch (Exception ee) {
			}
			if (tmpFile != null)
				new File(tmpFile).delete();
			available = true;
		}
	}

	void showError() {
		if (network_err) {
			JOptionPane.showMessageDialog(this, StringResource
					.getString("YT_LBL7"));
		} else {
			JOptionPane.showMessageDialog(this, StringResource
					.getString("YT_LBL8"));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// new YoutubeGrabberDlg().showDialog(null,new XDMConfig(n));
	}

}
