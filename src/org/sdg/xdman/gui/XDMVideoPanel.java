package org.sdg.xdman.gui;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import org.sdg.xdman.core.common.*;
import org.sdg.xdman.util.*;

public class XDMVideoPanel extends JDialog implements ActionListener {

	JMenu menu;
	int relx, rely;
	int left, top;
	int items;
	ArrayList<MediaInfo> mediaList;
	boolean closed = true;
	XDMConfig config;
	DownloadStateListner dl;

	public XDMVideoPanel(DownloadStateListner dl, XDMConfig config) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setType(Type.POPUP);
		setResizable(false);
		setUndecorated(true);
		this.dl = dl;
		this.config = config;
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		left = d.width - 250;
		top = d.height - 100;
		setLocation(d.width, d.height);
		mediaList = new ArrayList<MediaInfo>();
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		setTitle("XDM Video Monitor");
		getContentPane().setBackground(new Color(60, 60, 60));
		setAlwaysOnTop(true);
		setSize(1, 1);
		setLayout(null);
		JMenuBar bar = new JMenuBar();
		bar.setBorderPainted(false);
		bar.setBorder(null);
		bar.setForeground(StaticResource.whiteColor);
		bar.setMaximumSize(new Dimension(bar.getMaximumSize().width, 30));
		bar.setBackground(new Color(60, 60, 60));
		bar.setBounds(0, 0, 200, 30);
		add(bar);
		menu = new JMenu();
		menu.setBorderPainted(false);
		menu.setIcon(XDMMainWindow.getIcon("vp.png"));
		bar.add(menu);
		JButton closeBtn = new JButton();
		closeBtn.setBounds(200, 0, 30, 30);
		closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setMargin(new Insets(0, 0, 0, 0));
		closeBtn.setBackground(new Color(60, 60, 60));
		closeBtn.setIcon(StaticResource.getIcon("close_btn.png"));
		add(closeBtn);

		MouseMotionAdapter moa = new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent me) {
				int x = getX();
				int y = getY();
				setLocation(x + me.getX() - relx, y + me.getY() - rely);
			}
		};

		// bar.addMouseMotionListener(moa);
		menu.addMouseMotionListener(moa);
		menu.setCursor(new Cursor(Cursor.HAND_CURSOR));

		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				relx = me.getX();
				rely = me.getY();
			}
		};

		// bar.addMouseListener(ma);
		menu.addMouseListener(ma);

		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaList.clear();
				menu.removeAll();
				Point p = getLocationOnScreen();
				left = p.x;
				top = p.y;
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				setLocation(d.width, d.height);
				closed = true;
				setSize(1, 1);
			}
		});
		closed = true;
	}

	public void addVideo(MediaInfo mi) {
		mediaList.add(mi);
		String name = mi.name;
		String ext = XDMUtil.getExtension(name);
		String info = (ext == null ? "" : ext).replace(".", "") + " " + (mi.size == null ? "" : mi.size);
		name = XDMUtil.createSafeFileName(name);
		if (name.length() > 30) {
			name = name.substring(0, 30) + "...";
		}
		JMenuItem item = new JMenuItem(name + " " + info.toUpperCase());
		int index = mediaList.indexOf(mi);
		item.setName(index + "");
		item.addActionListener(this);
		menu.add(item);
		if (closed) {
			setLocation(left, top);
			setSize(230, 30);
			this.invalidate();
			this.repaint();
			closed = false;
		}
		menu.setToolTipText("Total " + mediaList.size() + " Video(s)");
	}

	void menuClicked(int i) {
		MediaInfo info = mediaList.get(i);
		System.out.println("Final Name: " + info.name);
		VideoDownloadDialog vd = new VideoDownloadDialog(info.name, info.url, info.url2, info.referer, info.userAgent,
				info.cookies, config, dl);
		vd.setVisible(true);
		// if (!XDMUtil.isNullOrEmpty(info.url2)) {
		// dl.downloadNow2(info.url, info.url2,
		// XDMUtil.createSafeFileName(info.name), config.destdir, null, null,
		// info.referer, info.cookies, info.userAgent);
		// } else {
		// dl.addDownload(info.url, XDMUtil.createSafeFileName(info.name),
		// config.destdir, null, null, info.referer,
		// info.cookies, info.userAgent);
		// }
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem item = (JMenuItem) e.getSource();
		int i = Integer.parseInt(item.getName());
		menuClicked(i);
	}

}
