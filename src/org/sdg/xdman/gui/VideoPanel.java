package org.sdg.xdman.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

import javax.swing.*;

import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.util.XDMUtil;

public class VideoPanel extends JFrame implements ActionListener {

	private static final long serialVersionUID = -8062958319526539872L;
	int relx, rely;
	int left, top;
	JPopupMenu pop;
	int items;
	ArrayList<MediaInfo> mediaList;
	boolean closed = true;
	XDMConfig config;
	DownloadStateListner dl;

	public void addVideo(MediaInfo mi) {

		mediaList.add(mi);
		String name = mi.name;
		String ext = XDMUtil.getExtension(name);
		String info = (ext == null ? "" : ext) + " " + (mi.size == null ? "" : mi.size);
		name = XDMUtil.createSafeFileName(name);
		if (name.length() > 30) {
			name = name.substring(0, 30) + "...";
		}
		JMenuItem item = new JMenuItem(name + " " + info.toUpperCase());
		int index = mediaList.indexOf(mi);
		item.setName(index + "");
		item.addActionListener(this);
		pop.add(item);
		if (closed) {
			Point p = getLocationOnScreen();
			setLocation(p.x - 200 + 48, p.y);
			// setLocation(left, top);
			this.remove(p2);
			setSize(200, 50);
			this.add(p1);
			this.invalidate();
			this.repaint();
			closed = false;
		}
		// setSize(170, 30);
		lbl.setToolTipText("Total " + mediaList.size() + " Video(s)");
	}

	JLabel lbl;
	JPanel p1, p2;

	public VideoPanel(DownloadStateListner dl, XDMConfig config) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setUndecorated(true);
		this.dl = dl;
		this.config = config;
		setSize(48, 48);
		setIconImage(XDMMainWindow.getIcon("videograb32.png").getImage());
		setTitle("Video Grabber");
		getContentPane().setBackground(new Color(40, 40, 40));
		setAlwaysOnTop(true);
		// left = Toolkit.getDefaultToolkit().getScreenSize().width - 200;
		// top = Toolkit.getDefaultToolkit().getScreenSize().height - 100;

		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width - 70,
				Toolkit.getDefaultToolkit().getScreenSize().height - 90);

		setLayout(null);
		p1 = new JPanel(null);
		p1.setBounds(0, 0, 200, 48);
		p1.setBackground(new Color(40, 40, 40));
		p2 = new JPanel(null);
		p2.setBounds(0, 0, 48, 48);
		p2.setBackground(new Color(40, 40, 40));

		JLabel bg = new JLabel(XDMMainWindow.getIcon("videograb32.png"));
		bg.setBounds(8, 8, 32, 32);
		p2.add(bg);

		lbl = new JLabel("DOWNLOAD VIDEO", JLabel.CENTER);
		lbl.setHorizontalAlignment(JLabel.CENTER);
		lbl.setHorizontalTextPosition(JLabel.CENTER);
		lbl.setBounds(0, 0, 170, 48);
		lbl.setOpaque(true);
		lbl.setBackground(new Color(40, 40, 40));
		lbl.setFont(StaticResource.plainFontBig);
		lbl.setForeground(Color.WHITE);
		lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		p1.add(lbl);

		JButton closeBtn = new JButton();
		closeBtn.setBounds(170, 0, 30, 48);
		closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setMargin(new Insets(0, 0, 0, 0));
		closeBtn.setBackground(new Color(40, 40, 40));
		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		p1.add(closeBtn, BorderLayout.EAST);
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaList.clear();
				pop.removeAll();
				Point p = getLocationOnScreen();
				setLocation(p.x + 200 - 48, p.y);
				closed = true;
				setSize(48, 48);
				remove(p1);
				add(p2);
			}
		});

		pop = new JPopupMenu();

		MouseMotionAdapter moa = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent me) {
				int x = getX();
				int y = getY();
				setLocation(x + me.getX() - relx, y + me.getY() - rely);
			}
		};

		lbl.addMouseMotionListener(moa);
		bg.addMouseMotionListener(moa);

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				relx = me.getX();
				rely = me.getY();
			}

			@Override
			public void mouseClicked(MouseEvent me) {
				if (mediaList.size() > 0) {
					pop.show(lbl, 0, 0);
				}
			}
		};

		lbl.addMouseListener(ma);
		bg.addMouseListener(ma);
		mediaList = new ArrayList<MediaInfo>();
		add(p2);
	}

	void menuClicked(int i) {
		MediaInfo info = mediaList.get(i);
		System.out.println("Final Name: " + info.name);
		if (!XDMUtil.isNullOrEmpty(info.url2)) {
			dl.downloadNow2(info.url, info.url2, XDMUtil.createSafeFileName(info.name), config.destdir, null, null,
					info.referer, info.cookies, info.userAgent);
		} else {
			dl.addDownload(info.url, XDMUtil.createSafeFileName(info.name), config.destdir, null, null, info.referer,
					info.cookies, info.userAgent);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem item = (JMenuItem) e.getSource();
		int i = Integer.parseInt(item.getName());
		menuClicked(i);
	}

}
