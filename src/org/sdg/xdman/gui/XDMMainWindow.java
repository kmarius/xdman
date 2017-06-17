package org.sdg.xdman.gui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.sdg.xdman.core.common.Authenticator;
import org.sdg.xdman.core.common.ConnectionManager;
import org.sdg.xdman.core.common.DownloadInfo;
import org.sdg.xdman.core.common.DownloadStateListner;
import org.sdg.xdman.core.common.IDownloader;
import org.sdg.xdman.core.common.IXDMConstants;
import org.sdg.xdman.core.common.XDMConfig;
import org.sdg.xdman.core.common.dash.DASHDownloader;
import org.sdg.xdman.core.common.hls.HLSDownloader;
import org.sdg.xdman.gui.FileTransferHandler.FileTransferable;
import org.sdg.xdman.interceptor.DownloadIntercepterInfo;
import org.sdg.xdman.interceptor.IMediaGrabber;
import org.sdg.xdman.interceptor.XDMServer;
import org.sdg.xdman.util.LinuxUtil;
import org.sdg.xdman.util.Logger;
import org.sdg.xdman.util.OSXUtil;
import org.sdg.xdman.util.XDMUtil;

public class XDMMainWindow extends XDMFrame
		implements TreeSelectionListener, ActionListener, DownloadStateListner, BatchDownloadListener, IMediaGrabber {

	private static final long serialVersionUID = -5562142092210683635L;

	XDMVideoPanel vp;

	String state, type;
	int windowState;
	JPanel toolbar;
	JButton addurl, resume, pause, delete, option, youtube, media, exit;
	JLabel closetree;
	JSplitPane split;
	JTree tree;
	JTable table;
	// MainListModel model;
	MainTableModel model = new MainTableModel();
	static DownloadList list = null;// new DownloadList();
	static String tempdir = null;
	static String destdir = null;
	static String appdir = null;
	static XDMConfig config;
	Toolkit t;
	JPopupMenu pop;
	JSplitPane content;
	XDMToolBarButtonUI toolBtnUI;
	JMenuBar bar;
	XDMButtonUI btnUI;
	ConfigDialog configDlg;
	BatchDlg batchDlg;
	BatchDownloadDlg listDlg;
	MediaTableModel mediaModel = new MediaTableModel();
	YoutubeGrabberDlg ytDlg;
	JPopupMenu ctxPopup;
	BrowserIntegrationDlg biDlg;
	JLabel lbl;
	AboutDialog abtDlg;
	boolean processQueue;
	DownloadListItem qi;
	boolean schedulerActive;
	AssembleDialog asmDlg;
	Clipboard clipboard;
	DropBox w;
	JLabel lblCatArr[];
	SidePanel sp;

	// JList dlist;

	public XDMMainWindow() {
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		setTitle(getString("TITLE"));
		t = Toolkit.getDefaultToolkit();
		if (config.mwW <= 0) {
			Dimension d = t.getScreenSize();
			int w = 700;
			int h = 400;
			if (d.width < w)
				w = d.width;
			if (d.height < h)
				h = d.height;
			setSize(w, h);
			setLocationRelativeTo(null);
		} else {
			setSize(config.mwW, config.mwH);
			setLocation(config.mwX, config.mwY);
		}

		JLabel lblTitle = new JLabel("XDM 2016");
		lblTitle.setBorder(new EmptyBorder(20, 20, 0, 0));
		lblTitle.setFont(new Font(Font.DIALOG, Font.PLAIN, 24));
		lblTitle.setForeground(StaticResource.whiteColor);
		getTitlePanel().add(lblTitle, BorderLayout.WEST);

		bar = new JMenuBar();
		bar.setBorderPainted(false);
		bar.setForeground(StaticResource.whiteColor);
		bar.setMaximumSize(new Dimension(bar.getMaximumSize().width, 30));
		bar.setBackground(StaticResource.titleColor);

		createMenu(bar);

		Box menuBox = Box.createHorizontalBox();
		menuBox.add(Box.createHorizontalGlue());
		menuBox.add(bar);
		menuBox.add(Box.createHorizontalStrut(30));

		getTitlePanel().add(menuBox);

		createTabs();

		JPanel pClient = new JPanel(new BorderLayout());

		JPanel panCenter = new JPanel(new BorderLayout());
		panCenter.setBackground(Color.WHITE);
		BarPanel bp = new BarPanel();
		bp.setLayout(new BorderLayout());

		bp.add(Box.createRigidArea(new Dimension(0, 30)));
		bp.add(createSearchPane(), BorderLayout.EAST);

		panCenter.add(bp, BorderLayout.NORTH);
		pClient.add(panCenter);

		sp = new SidePanel();
		sp.setLayout(null);
		sp.setPreferredSize(new Dimension(150, 250));

		lblCatArr = new JLabel[6];

		JLabel lblAllCat = new JLabel(getString("TREE_ALL"));
		lblAllCat.setName("TREE_ALL");
		lblAllCat.setFont(StaticResource.plainFont);
		lblAllCat.setBorder(new EmptyBorder(5, 20, 5, 5));
		lblCatArr[0] = lblAllCat;

		JLabel lblDocCat = new JLabel(getString("TREE_DOCUMENTS"));
		lblDocCat.setName("TREE_DOCUMENTS");
		lblDocCat.setFont(StaticResource.plainFont);
		lblDocCat.setBorder(new EmptyBorder(5, 20, 5, 5));
		lblCatArr[1] = lblDocCat;

		JLabel lblArcCat = new JLabel(getString("TREE_COMPRESSED"));
		lblArcCat.setName("TREE_COMPRESSED");
		lblArcCat.setFont(StaticResource.plainFont);
		lblArcCat.setBorder(new EmptyBorder(5, 20, 5, 5));
		lblCatArr[2] = lblArcCat;

		JLabel lblMusCat = new JLabel(getString("TREE_MUSIC"));
		lblMusCat.setName("TREE_MUSIC");
		lblMusCat.setFont(StaticResource.plainFont);
		lblMusCat.setBorder(new EmptyBorder(5, 20, 5, 5));
		lblCatArr[3] = lblMusCat;

		JLabel lblVidCat = new JLabel(getString("TREE_VIDEOS"));
		lblVidCat.setName("TREE_VIDEOS");
		lblVidCat.setFont(StaticResource.plainFont);
		lblVidCat.setBorder(new EmptyBorder(5, 20, 5, 5));
		lblCatArr[4] = lblVidCat;

		JLabel lblAppCat = new JLabel(getString("TREE_PROGRAMS"));
		lblAppCat.setName("TREE_PROGRAMS");
		lblAppCat.setFont(StaticResource.plainFont);
		lblAppCat.setBorder(new EmptyBorder(5, 20, 5, 5));
		lblCatArr[5] = lblAppCat;

		lblCatArr[0].setBackground(new Color(242, 242, 242));
		lblCatArr[0].setOpaque(true);

		for (int i = 0; i < 6; i++) {
			lblCatArr[i].setBounds(0, 20 + (i * 35), 149, 27);
			final int c = i;
			lblCatArr[i].addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					// JOptionPane.showMessageDialog(null,"Mouse clicked:
					// "+e.getButton());
					actionPerformed(new ActionEvent(lblCatArr[c], 0, ""));
				}
			});
			sp.add(lblCatArr[i]);
		}

		pClient.add(sp, BorderLayout.WEST);

		add(pClient);

		Box bb = Box.createHorizontalBox();
		bb.add(Box.createRigidArea(new Dimension(20, 60)));
		bb.setBackground(StaticResource.titleColor);
		bb.setOpaque(true);

		JButton btnAdd = new XDMButton(StaticResource.getIcon("tool_add.png"));
		btnAdd.putClientProperty("xdmbutton.grayrollover", "true");
		btnAdd.setBorderPainted(false);
		btnAdd.addActionListener(this);
		btnAdd.setName("ADD_URL");
		btnAdd.setBackground(StaticResource.titleColor);
		btnAdd.setMargin(new Insets(0, 0, 0, 0));
		bb.add(btnAdd);

		bb.add(Box.createRigidArea(new Dimension(10, 10)));

		JButton btnDel = new XDMButton(StaticResource.getIcon("tool_del.png"));
		btnDel.putClientProperty("xdmbutton.grayrollover", "true");
		btnDel.setBorderPainted(false);
		btnDel.setBackground(StaticResource.titleColor);
		btnDel.setMargin(new Insets(0, 0, 0, 0));
		btnDel.addActionListener(this);
		btnDel.setName("DELETE");
		bb.add(btnDel);

		bb.add(Box.createRigidArea(new Dimension(10, 10)));

		JButton btnPause = new XDMButton(StaticResource.getIcon("tool_pause.png"));
		btnPause.putClientProperty("xdmbutton.grayrollover", "true");
		btnPause.setBorderPainted(false);
		btnPause.setBackground(StaticResource.titleColor);
		btnPause.setMargin(new Insets(0, 0, 0, 0));
		btnPause.addActionListener(this);
		btnPause.setName("PAUSE");
		bb.add(btnPause);

		bb.add(Box.createRigidArea(new Dimension(10, 10)));

		JButton btnResume = new XDMButton(StaticResource.getIcon("tool_resume1.png"));
		btnResume.putClientProperty("xdmbutton.grayrollover", "true");
		btnResume.setBorderPainted(false);
		btnResume.setBackground(StaticResource.titleColor);
		btnResume.setMargin(new Insets(0, 0, 0, 0));
		btnResume.addActionListener(this);
		btnResume.setName("RESUME");
		bb.add(btnResume);

		bb.add(Box.createRigidArea(new Dimension(10, 10)));

		JButton btnSettings = new XDMButton(StaticResource.getIcon("tool_settings2.png"));
		btnSettings.putClientProperty("xdmbutton.grayrollover", "true");
		btnSettings.setBorderPainted(false);
		btnSettings.setBackground(StaticResource.titleColor);
		btnSettings.setMargin(new Insets(0, 0, 0, 0));
		btnSettings.addActionListener(this);
		btnSettings.setName("OPTIONS");
		bb.add(btnSettings);

		bb.add(Box.createHorizontalGlue());

		btnTablet = new JLabel(StaticResource.getIcon("on.png"));
		btnTablet.setForeground(Color.WHITE);
		btnTablet.setIconTextGap(15);
		btnTablet.putClientProperty("xdmbutton.norollover", "true");
		btnTablet.setBackground(StaticResource.titleColor);
		btnTablet.setName("TABLET");
		btnTablet.setText("Video Capturing Mode");
		btnTablet.setHorizontalTextPosition(JButton.LEADING);
		// bb.add(btnTablet);

		btnTablet.setIcon(config.tabletMode ? StaticResource.getIcon("on.png") : StaticResource.getIcon("off.png"));

		btnTablet.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				toggleTabletMode();
			}
		});

		// btnTablet
		// .setToolTipText("Load pages faster as tablet browser (Firefox and
		// Chrome only)");

		// bb.add(Box.createHorizontalStrut(20));
		bb.add(Box.createRigidArea(new Dimension(20, 10)));

		pClient.add(bb, BorderLayout.SOUTH);

		// dlist = new JList();
		list = new DownloadList(appdir);

		model = new MainTableModel();// new MainListModel(list);
		model.setList(list);
		table = new JTable(model);
		table.setTableHeader(null);
		table.setDefaultRenderer(DownloadListItem.class, new XDMListItemRenderer());
		table.setRowHeight(70);
		table.setShowGrid(false);
		table.setFillsViewportHeight(true);
		table.setBorder(new EmptyBorder(0, 0, 0, 0));
		table.setTransferHandler(new FileTransferHandler(list, this));
		table.setDragEnabled(true);

		JScrollPane jsp = new JScrollPane(table);
		jsp.setBorder(new EmptyBorder(0, 0, 0, 0));
		// jsp.setBorder(null);

		panCenter.add(jsp);

		// content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		// content.setBorder(new EmptyBorder(2, 5, 5, 5));
		// content.setDividerSize(2);
		// content.setDividerLocation(150);
		// System.out.println(config.dbY);
		//
		// toolBtnUI = new XDMToolBarButtonUI();
		//
		// //createToolbar();
		//
		// // createTable();
		//
		// //createTree();
		//
		// // add(content);
		//
		// // JButton btn=new JButton(getString("ADD_URL"), getIcon("add.png"));
		// //
		// // decorateButton(btn);
		// // add(btn);
		if (config.destdir == null) {
			config.destdir = destdir;
		}
		if (config.tempdir == null) {
			config.tempdir = tempdir;
		}

		// System.out.println(getClass().getResource("/res/icon.png"));
	
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent me) {
				// JOptionPane.showMessageDialog(null,"Mouse clicked:
				// "+me.getButton()+" "+MouseEvent.BUTTON3);
				boolean open = false;
				int r = table.rowAtPoint(me.getPoint());

				if (me.getButton() == MouseEvent.BUTTON1) {
					if (r < 0 || r >= table.getRowCount()) {
				        table.clearSelection();
				    }
				}
				
				if (me.getButton() == MouseEvent.BUTTON3) {
					if (r >= 0 && r < table.getRowCount() && !table.isCellSelected(r,0)) {
				        table.setRowSelectionInterval(r, r);
				    }
					open = true;
				}

				if (me.isPopupTrigger()) {
					open = true;
				}
				if (open) {
					if (ctxPopup == null) {
						createContextMenu();
					}
					ctxPopup.show(table, me.getX(), me.getY());
				}
			}
		});
		
	}

	JLabel btnTablet;

	void toggleTabletMode() {
		boolean mode = !config.tabletMode;
		if (mode) {
			if (JOptionPane.showConfirmDialog(this,
					"XDM is switching to video capturing mode.\nWeb pages will be rendered differently in browser(s)\n\nDo you want to continue?",
					"Please confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			config.tabletMode = true;
			vp.setVisible(true);
		} else {
			config.tabletMode = false;
			vp.setVisible(false);
		}
		btnTablet.setIcon(config.tabletMode ? StaticResource.getIcon("on.png") : StaticResource.getIcon("off.png"));
		config.save();
	}

	JButton btnSort;
	JTextField txtSearch;

	Component createSearchPane() {
		btnSort = new XDMButton("Newest on top");
		btnSort.setBorderPainted(false);
		btnSort.setFocusPainted(false);
		btnSort.setContentAreaFilled(false);

		txtSearch = new JTextField();

		txtSearch.setBorder(null);
		final JButton btnSearch = new JButton();
		btnSearch.setName("BTN_SEARCH");
		btnSearch.addActionListener(this);
		btnSearch.setPreferredSize(new Dimension(20, 20));
		btnSearch.setBackground(Color.WHITE);
		btnSearch.setIcon(StaticResource.getIcon("search16.png"));
		btnSearch.setBorderPainted(false);
		btnSearch.setContentAreaFilled(false);

		txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					btnSearch.doClick();
				}
			}
		});

		Box b = Box.createHorizontalBox();
		b.setOpaque(true);
		b.setBackground(Color.WHITE);
		b.setPreferredSize(new Dimension(130, 20));
		b.setMaximumSize(new Dimension(130, 20));
		txtSearch.setPreferredSize(new Dimension(70, 20));
		txtSearch.setMaximumSize(new Dimension(txtSearch.getMaximumSize().width, 20));
		b.add(txtSearch);
		b.add(btnSearch);
		b.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
		Box bp = Box.createHorizontalBox();
		bp.setOpaque(false);
		bp.setBorder(new EmptyBorder(3, 3, 3, 18));
		// bp.add(new JLabel("Sort by"));
		bp.add(Box.createHorizontalStrut(10));
		bp.add(btnSort);
		bp.add(Box.createHorizontalStrut(20));
		bp.add(b);

		sortItems = new JMenuItem[] { new JMenuItem("Date"), new JMenuItem("Size"), new JMenuItem("Name"),
				new JMenuItem("Type"), new JMenuItem("Ascending"), new JMenuItem("Descending") };

		final JPopupMenu popSort = new JPopupMenu();
		for (int i = 0; i < sortItems.length; i++) {
			popSort.add(sortItems[i]);
			if (i >= 0 && i <= 3) {
				sortItems[i].setName("COL:" + i);
				if (i == XDMConfig.sortField) {
					sortItems[i].setFont(StaticResource.boldFont);
				} else {
					sortItems[i].setFont(StaticResource.plainFont);
				}
			}
			if (i == 3) {
				popSort.addSeparator();
			}
			if (i == 4) {
				sortItems[4].setName("CTX_ASC");
				sortItems[4].setFont(XDMConfig.sortAsc ? StaticResource.boldFont : StaticResource.plainFont);
			}
			if (i == 5) {
				sortItems[5].setName("CTX_DESC");
				sortItems[5].setFont((!XDMConfig.sortAsc) ? StaticResource.boldFont : StaticResource.plainFont);
			}
			sortItems[i].addActionListener(this);
		}

		popSort.setInvoker(btnSort);

		btnSort.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				popSort.show(btnSort, 0, btnSort.getHeight());
			}
		});

		return bp;
	}

	// JMenu createSortMenu() {
	// JMenu sortMenu = new JMenu(getString("CTX_SORT"));
	// sortMenu.setName("CTX_SORT");
	// sortMenu.addActionListener(this);
	// sortMenu.setIcon(new XDMBlankIcon(15, 10));
	// sortMenu.setBorderPainted(false);
	// sortMenu.setMargin(new Insets(10, 0, 0, 0));
	//
	// ButtonGroup bg1 = new ButtonGroup();
	//
	// for (int i = 1; i < model.getColumnCount(); i++) {
	// JCheckBoxMenuItem item = new JCheckBoxMenuItem(model
	// .getColumnName(i));
	// item.setName("COL:" + i);
	// item.addActionListener(this);
	// sortMenu.add(item);
	// bg1.add(item);
	// if (TableSortInfo.getSortingField() == i) {
	// item.setSelected(true);
	// }
	// }
	// sortMenu.addSeparator();
	// ButtonGroup bg2 = new ButtonGroup();
	// JCheckBoxMenuItem item1 = new JCheckBoxMenuItem(getString("CTX_ASC"));
	// item1.setName("CTX_ASC");
	// item1.addActionListener(this);
	// sortMenu.add(item1);
	// bg2.add(item1);
	// JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(getString("CTX_DESC"));
	// item2.setName("CTX_DESC");
	// item2.addActionListener(this);
	// sortMenu.add(item2);
	// bg2.add(item2);
	//
	// if (TableSortInfo.isAscending()) {
	// item1.setSelected(true);
	// } else {
	// item2.setSelected(true);
	// }
	// return sortMenu;
	// }

	JMenuItem[] sortItems;

	JButton btnTabArr[];

	private void createTabs() {
		JButton btnAllTab = new XDMButton(getString("ALL_DOWNLOADS")),
				btnIncompleteTab = new XDMButton(getString("ALL_UNFINISHED")),
				btnCompletedTab = new XDMButton(getString("ALL_FINISHED"));

		btnTabArr = new JButton[3];
		btnTabArr[0] = btnAllTab;
		btnTabArr[0].setName("ALL_DOWNLOADS");
		btnTabArr[1] = btnIncompleteTab;
		btnTabArr[1].setName("ALL_UNFINISHED");
		btnTabArr[2] = btnCompletedTab;
		btnTabArr[2].setName("ALL_FINISHED");

		for (int i = 0; i < 3; i++) {
			btnTabArr[i].setFont(StaticResource.plainFontBig);
			btnTabArr[i].setBorderPainted(false);
			btnTabArr[i].addActionListener(this);
		}

		btnAllTab.setBackground(new Color(242, 242, 242));

		btnIncompleteTab.setBackground(StaticResource.titleColor);
		btnIncompleteTab.setForeground(StaticResource.whiteColor);

		btnCompletedTab.setBackground(StaticResource.titleColor);
		btnCompletedTab.setForeground(StaticResource.whiteColor);

		JPanel pp = new JPanel(new BorderLayout());
		pp.setOpaque(false);

		JPanel p = new JPanel(new GridLayout(1, 3, 5, 0));
		p.setOpaque(false);
		Dimension d = new Dimension(350, 30);
		p.setPreferredSize(d);
		p.setMaximumSize(d);
		p.setMinimumSize(d);
		p.setBackground(Color.WHITE);
		p.add(btnAllTab);
		p.add(btnIncompleteTab);
		p.add(btnCompletedTab);
		pp.add(p, BorderLayout.EAST);

		getTitlePanel().add(pp, BorderLayout.SOUTH);
	}

	int getDupAction(String url) {
		JTextField txt = new JTextField(url, 30);
		String lbl = StringResource.getString("DUP_TXT");
		JComboBox choice = new JComboBox(new String[] { StringResource.getString("DUP_OP1"),
				StringResource.getString("DUP_OP2"), StringResource.getString("DUP_OP3") });
		JCheckBox chk = new JCheckBox(StringResource.getString("DUP_CHK"));
		int ret = JOptionPane.showOptionDialog(null, new Object[] { txt, lbl, choice, chk },
				StringResource.getString("DUP_TITLE"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				null, null);
		if (ret == JOptionPane.OK_OPTION) {
			int index = choice.getSelectedIndex();
			if (chk.isSelected()) {
				config.duplicateLinkAction = index;
			}
			return index;
		}
		return -1;
	}

	void showMessageBox(String msg, String title, int msgType) {
		JOptionPane.showMessageDialog(this, msg, title, msgType);
	}

	void removeDownloads() {
		int indexes[] = table.getSelectedRows();
		if (indexes.length < 1) {
			showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		} else {
			if (JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete selected download" + (indexes.length > 1 ? "s" : "") + "?",
					"Confirm delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
		}
		DownloadListItem items[] = new DownloadListItem[indexes.length];

		for (int i = 0; i < indexes.length; i++) {
			DownloadListItem item = list.get(indexes[i]);
			if (item.mgr != null) {
				showMessageBox(getString("DWN_ACTIVE"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
				return;
			}
			items[i] = item;
		}

		for (int i = 0; i < indexes.length; i++) {
			DownloadListItem item = items[i];
			String tmpdir = item.tempdir;
			list.remove(item);
			if (XDMUtil.isNullOrEmpty(tmpdir)) {
				continue;
			}
			delDirRec(tmpdir);
		}
		model.fireTableDataChanged();
		// model = new MainListModel(list);
		// dlist.setModel(model);
		list.downloadStateChanged();
	
	}

	void delDirRec(String dir) {
		File fdir = new File(dir);
		File files[] = fdir.listFiles();
		if (files == null) {
			fdir.delete();
		} else {
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
			fdir.delete();
		}
	}

	private void resumeDownload() {
		int index = table.getSelectedRow();
		if (index < 0) {
			showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		DownloadListItem item = list.get(index);
		if (item == null) {
			return;
		}

		resumeDownload(item);

	}

	void resumeDownload(DownloadListItem item) {
		if (item.mgr != null) {
			showMessageBox(getString("DWN_ACTIVE"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (item.state == IXDMConstants.COMPLETE) {
			showMessageBox(getString("DWN_FINISHED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (item.tempdir == null || item.tempdir.length() < 1) {
			startDownload(item.url, item.second_url, item.filename, item.saveto, null, null, item.userAgent,
					item.referer, item.cookies, item, true);
		} else {
			IDownloader mgr = null;
			if (item.dtype == IXDMConstants.HLS) {
				System.out.println("HLS");
				mgr = new HLSDownloader(item.id, item.url, item.filename, item.saveto, item.tempdir, item.referer,
						item.userAgent, item.cookies, config);
			} else if (item.dtype == IXDMConstants.HTTP) {
				mgr = new ConnectionManager(item.id, item.url, item.filename, item.saveto, item.tempdir, item.userAgent,
						item.referer, item.cookies, config);
			} else if (item.dtype == IXDMConstants.DASH) {
				mgr = new DASHDownloader(item.id, item.url, item.second_url, item.filename, item.saveto, item.tempdir,
						item.referer, item.userAgent, item.cookies, config);
			}
			if ((!XDMUtil.isNullOrEmpty(item.user)) && (!XDMUtil.isNullOrEmpty(item.pass))) {
				mgr.setCredential(item.user, item.pass);
			}
			item.mgr = mgr;

			// model = new MainListModel(list);
			// dlist.setModel(model);//
			model.fireTableDataChanged();
			list.downloadStateChanged();

			XDMDownloadWindow dw = new XDMDownloadWindow(mgr);
			item.window = dw;
			if (config.showDownloadPrgDlg) {
				if (qi != item) {
					dw.showWindow();
				}
			}
			mgr.setProgressListener(dw);
			mgr.setDownloadListener(this);
			try {
				mgr.resume();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void restartDownload() {
		int index = table.getSelectedRow();
		if (index < 0) {
			showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		DownloadListItem item = list.get(index);
		if (item == null) {
			return;
		}
		restartDownload(item);
	}

	void restartDownload(DownloadListItem item) {
		if (item.mgr != null) {
			showMessageBox(getString("DWN_ACTIVE"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}

		startDownload(item.url, item.second_url, item.filename, item.saveto, null, null, item.userAgent, item.referer,
				item.cookies, item, true);
	}

	private void pauseDownload() {
		int indexes[] = table.getSelectedRows();
		for (int i = 0; i < indexes.length; i++) {
			DownloadListItem item = list.get(indexes[i]);
			if (item.mgr != null) {
				item.mgr.stop();
			}
		}
	}

	void addDownload() {
		NewDownloadWindow fdlg = new NewDownloadWindow(this, config);
		fdlg.setDir(config.destdir);
		fdlg.showDlg();
	}

	boolean isHLS(String url) {
		try {
			return (new URI(url).getPath().endsWith("m3u8"));
		} catch (URISyntaxException e) {
			return false;
		}
	}

	IDownloader createDownloader(UUID id, String url, String url2, String name, String folder, String tempdir,
			String userAgent, String referer, ArrayList<String> cookies, XDMConfig config) {
		if (!XDMUtil.isNullOrEmpty(url2)) {
			return new DASHDownloader(id, url, url2, name, folder, tempdir, referer, userAgent, cookies, config);
		}
		if (isHLS(url)) {
			System.out.println("HLS");
			return new HLSDownloader(id, url, name, folder, tempdir, referer, userAgent, cookies, config);
		} else {
			System.out.println("NON-HLS");
			return new ConnectionManager(id, url, name, folder, config.tempdir, userAgent, referer, cookies, config);
		}
	}

	synchronized void startDownload(String url, String url2, String name, String folder, String user, String pass,
			String userAgent, String referer, ArrayList<String> cookies, DownloadListItem item,
			boolean overriteExisting) {
		UUID id = UUID.randomUUID();

		IDownloader mgr = createDownloader(id, url, url2, name, folder, config.tempdir, userAgent, referer, cookies,
				config);

		mgr.setOverwrite(overriteExisting);

		if (item == null) {
			item = new DownloadListItem();
			list.add(item);
		}
		if ((!XDMUtil.isNullOrEmpty(user)) && (!XDMUtil.isNullOrEmpty(pass))) {
			mgr.setCredential(user, pass);
			item.user = user;
			item.pass = pass;
		}
		item.mgr = mgr;
		item.dtype = mgr.getType();

		item.filename = name;
		item.url = url;
		item.second_url = url2;
		item.q = false;
		item.dateadded = item.lasttry = new SimpleDateFormat("MMM dd").format(new Date());
		item.id = id;
		item.saveto = folder;
		item.icon = IconUtil.getIcon(XDMUtil.findCategory(item.filename));

		item.userAgent = userAgent;
		item.referer = referer;
		if (cookies != null) {
			item.cookies = new ArrayList<String>();
			item.cookies.addAll(cookies);
		}
		// mgr.dwnListener=this;
		item.state = IXDMConstants.CONNECTING;// DownloadListItem.UNFINISHED;
		item.type = XDMUtil.findCategory(name);

		// model = new MainListModel(list);
		// dlist.setModel(model);//

		// updateSortText();
		list.sort();
		model.fireTableDataChanged();
		list.downloadStateChanged();

		XDMDownloadWindow dw = new XDMDownloadWindow(mgr);
		item.window = dw;

		if (config.showDownloadPrgDlg) {
			if (qi != item) {
				dw.showWindow();
			}
		}
		
		if (config.showDownloadPrgNotifySend) {
			if (qi != item) {
				notifySend("XDM", "Started: " + item.filename);
			}
		}

		mgr.setProgressListener(dw);
		mgr.setDownloadListener(this);

		try {
			mgr.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void add2Queue(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent, boolean q) {
		UUID id = UUID.randomUUID();

		DownloadListItem item = new DownloadListItem();
		item.dtype = IXDMConstants.HTTP;
		list.add(item);

		item.user = user;
		item.pass = pass;

		item.filename = name;
		item.url = url;
		item.q = q;
		item.dateadded = item.lasttry = new SimpleDateFormat("MMM dd").format(new Date());
		item.id = id;
		item.saveto = folder;
		item.icon = IconUtil.getIcon(XDMUtil.findCategory(item.filename));

		item.userAgent = userAgent;
		item.referer = referer;
		if (cookies != null) {
			item.cookies.addAll(cookies);
		}
		// mgr.dwnListener=this;
		item.state = IXDMConstants.STOPPED;// DownloadListItem.UNFINISHED;
		item.status = "Stopped";
		item.type = XDMUtil.findCategory(name);

		model.fireTableDataChanged();
		list.downloadStateChanged();
	}

	Thread st;

	@Override
	public void configChanged() {
		if (config.schedule) {
			if (schedulerActive) {
				Logger.log("Scheduler is active");
				return;
			}
			st = new Thread(new Runnable() {
				public void run() {
					schedulerRun();
				};
			});
			st.start();
		}
	}

	@Override
	public void downloadComplete(UUID id) {
		DownloadListItem item = list.getByID(id);

		if (item == null)
			return;
		item.window = null;
		item.status = "Download Complete" + " " + item.size;
		item.timeleft = "";
		item.state = IXDMConstants.COMPLETE;
		

		model.fireTableDataChanged();

		list.downloadStateChanged();

		if (qi == item) {
			qi = null;
			if (processQueue) {
				if (processNextQueuedDownload()) {
					return;
				}
			}
		}

		if (config.halt){ 
			executeCommands();
		} else if (config.showDownloadCompleteDlg) {
			DownloadCompleteDialog cdlg = new DownloadCompleteDialog(config);
			cdlg.setData(item.filename, item.saveto);
			cdlg.setLocationRelativeTo(null);
			cdlg.setVisible(true);
		} else if (config.showDownloadCompleteNotifySend) {
			notifySend("XDM", "Completed: " + item.filename);
		} else {
			executeCommands();
		}
	}
	
	static void notifySend(String title, String text) {
		try {
			Runtime.getRuntime().exec(new String[]{"notify-send", title, text, "--expire-time=1000", "--app-name=XDM"});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void downloadConfirmed(UUID id, Object data) {
		DownloadListItem item = list.getByID(id);
		IDownloader mgr = item.mgr;
		item.tempdir = mgr.getTempdir();
		item.url = mgr.getUrl();
		item.filename = mgr.getFileName();
		item.icon = IconUtil.getIcon(XDMUtil.findCategory(item.filename));
		if (data instanceof DownloadInfo) {
			item.updateData((DownloadInfo) data);
		}
		model.fireTableDataChanged();
		list.downloadStateChanged();
	}

	@Override
	public void downloadFailed(UUID id) {
		System.out.println("Download failed");
		list.downloadStateChanged();
		DownloadListItem item = list.getByID(id);
		if (item == null) {
			return;
		}
		item.window = null;
		if (qi == item) {
			qi = null;
			if (processQueue) {
				processNextQueuedDownload();
			} else {
				System.out.println("Queue stopped");
			}
		} else {
			System.out.println("Not queued");
		}
	}

	@Override
	public void downloadNow(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent) {
		boolean overwrite = false;
		for (int i = 0; i < list.list.size(); i++) {
			DownloadListItem item = list.list.get(i);
			if (url.equals(item.url)) {
				int action = config.duplicateLinkAction;
				if (action == XDMConfig.PROMPT) {
					action = getDupAction(url);
				}
				if (action == -1) {
					return;
				}
				if (action == XDMConfig.RESUME) {
					if (item.state == IXDMConstants.COMPLETE) {
						File file = new File(item.saveto, item.filename);
						if (file.exists()) {
							XDMUtil.open(file.getParentFile());
						} else {
							if (item.mgr == null) {
								restartDownload(item);
							} else {
								showMessageBox(getString("DWN_ACTIVE"), "Message", JOptionPane.ERROR_MESSAGE);
							}
						}
					} else {
						if (item.mgr == null) {
							resumeDownload(item);
						} else {
							showMessageBox(getString("DWN_ACTIVE"), "Message", JOptionPane.ERROR_MESSAGE);
						}
					}
					return;
				} else {
					overwrite = (action == XDMConfig.OVERWRITE);
					break;
				}
			}
		}
		startDownload(url, null, name, folder, user, pass, userAgent, referer, cookies, null, overwrite);
	}

	public void downloadNow2(String url, String url2, String name, String folder, String user, String pass,
			String referer, ArrayList<String> cookies, String userAgent) {
		boolean overwrite = false;
		for (int i = 0; i < list.list.size(); i++) {
			DownloadListItem item = list.list.get(i);
			if (url.equals(item.url)) {
				int action = config.duplicateLinkAction;
				if (action == XDMConfig.PROMPT) {
					action = getDupAction(url);
				}
				if (action == -1) {
					return;
				}
				if (action == XDMConfig.RESUME) {
					if (item.state == IXDMConstants.COMPLETE) {
						File file = new File(item.saveto, item.filename);
						if (file.exists()) {
							XDMUtil.open(file.getParentFile());
						} else {
							if (item.mgr == null) {
								restartDownload(item);
							} else {
								showMessageBox(getString("DWN_ACTIVE"), "Message", JOptionPane.ERROR_MESSAGE);
							}
						}
					} else {
						if (item.mgr == null) {
							resumeDownload(item);
						} else {
							showMessageBox(getString("DWN_ACTIVE"), "Message", JOptionPane.ERROR_MESSAGE);
						}
					}
					return;
				} else {
					overwrite = (action == XDMConfig.OVERWRITE);
					break;
				}
			}
		}
		startDownload(url, url2, name, folder, user, pass, userAgent, referer, cookies, null, overwrite);
	}

	@Override
	public void downloadPaused(UUID id) {
		synchronized (this) {
			DownloadListItem item = list.getByID(id);

			if (item == null)
				return;

			item.window = null;

			item.mgr = null;
			item.status = "Stopped" + " " + item.sprg + "% of " + item.size;
			item.state = IXDMConstants.STOPPED;

			int index = list.getIndex(item);
			if (index < 0)
				return;
			model.fireTableRowsUpdated(index, index);
			list.downloadStateChanged();

			if (qi == item) {
				qi = null;
				if (processQueue) {
					if (JOptionPane.showConfirmDialog(this, getString("CONTINUE_Q"), getString("DEFAULT_TITLE"),
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						processNextQueuedDownload();
					} else {
						processQueue = false;
						qi = null;
					}
				} else {
					qi = null;
				}
			}
		}
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getCredentials(ConnectionManager mgr, String host) {
		// TODO Auto-generated method stub

	}

	@Override
	public void interceptDownload(DownloadIntercepterInfo info) {
		addDownload(info.url, XDMUtil.getFileName(info.url), config.destdir, null, null, info.referer, info.cookies,
				info.ua, info.noconfirm);
	}

	@Override
	public void restoreWindow() {
		setVisible(true);
	}

	@Override
	public void startQueue() {
		if (processQueue) {
			showMessageBox(getString("Q_STARTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (qi != null) {
			return;
		}
		processQueue = true;
		processNextQueuedDownload();
	}

	@Override
	public void stopQueue() {
		processQueue = false;
		if (qi == null) {
			return;
		}
		if (qi.mgr != null) {
			qi.mgr.stop();
			qi = null;
		} else {
			qi = null;
		}
	}

	@Override
	public void updateManager(UUID id, Object data) {
		DownloadListItem item = list.getByID(id);// null;
		if (item == null)
			return;
		if (data instanceof DownloadInfo) {
			DownloadInfo info = (DownloadInfo) data;
			item.updateData(info);
		}

		int index = list.getIndex(item);
		if (index >= 0)
			model.fireTableRowsUpdated(index, index);// model.fireListItemUpdated(index);
	}

	@Override
	public void ytCallback(String yturl) {
		// TODO Auto-generated method stub

	}

	public void valueChanged(TreeSelectionEvent e) {
		String status = null;
		int state = 0;
		Object o[] = e.getPath().getPath();
		for (int i = 0; i < o.length; i++) {
			XDMTreeNode node = (XDMTreeNode) o[i];
			if (node.id.equals("TREE_UNFINISHED")) {
				state = 1;
			}
			if (node.id.equals("TREE_FINISHED")) {
				state = IXDMConstants.COMPLETE;
			}
			if (node.id.equals("TREE_DOCUMENTS")) {
				status = IXDMConstants.DOCUMENTS;
			}
			if (node.id.equals("TREE_COMPRESSED")) {
				status = IXDMConstants.COMPRESSED;
			}
			if (node.id.equals("TREE_MUSIC")) {
				status = IXDMConstants.MUSIC;
			}
			if (node.id.equals("TREE_PROGRAMS")) {
				status = IXDMConstants.PROGRAMS;
			}
			if (node.id.equals("TREE_VIDEOS")) {
				status = IXDMConstants.VIDEO;
			}
			list.setState(state);
			list.setType(status);
			model.fireTableDataChanged();
		}
	}

	// void createTable() {
	// //table = new JTable(model);
	// JScrollPane jsp = new JScrollPane(table);
	// //table.setFillsViewportHeight(true);
	// content.add(jsp, JSplitPane.RIGHT);
	//
	// if (System.getProperty("xdm.defaulttheme") != null) {
	// jsp.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
	// new XDMTableHeaderRenderer());
	// int rh = table.getRowHeight();
	// if (rh > 0)
	// table.setRowHeight(rh + 5);
	// table.getTableHeader().setDefaultRenderer(
	// new XDMTableHeaderRenderer());
	// }
	//
	// table.setShowGrid(false);
	// table.setTransferHandler(new FileTransferHandler(list, this));
	// table.setDragEnabled(true);
	// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	//
	// TableColumnModel cm = table.getColumnModel();
	// for (int i = 0; i < cm.getColumnCount(); i++) {
	// TableColumn c = cm.getColumn(i);
	// if (c.getHeaderValue().equals(""))
	// c.setPreferredWidth(20);
	// else if (c.getHeaderValue().equals(getString("FILE_NAME")))
	// c.setPreferredWidth(200);
	// else if (c.getHeaderValue().equals(getString("Q")))
	// c.setPreferredWidth(50);
	// else
	// c.setPreferredWidth(100);
	// }
	//
	// table.addMouseListener(new MouseAdapter() {
	// @Override
	// public void mouseClicked(MouseEvent me) {
	// if (me.getButton() == MouseEvent.BUTTON3) {
	// if (ctxPopup == null) {
	// createContextMenu();
	// }
	// ctxPopup.show(table, me.getX(), me.getY());
	// }
	// }
	// });
	// }

	JMenu createMenu(String title) {
		JMenu menu = new JMenu(title);
		menu.setForeground(StaticResource.whiteColor);
		menu.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
		menu.setBorderPainted(false);
		return menu;
	}

	void createMenu(JMenuBar bar) {
		JMenu file = createMenu(getString("FILE"));

		addMenuItem("ADD_URL", file);
		addMenuItem("BATCH_DOWNLOAD", file);
		// addMenuItem("YOUTUBE_DOWNLOADER", file);
		// addMenuItem("ADV_YT", file);
		addMenuItem("CLIP_ADD", file);
		addMenuItem("DELETE_DWN", file);
		addMenuItem("DELETE_COMPLETED", file);
		addMenuItem("MAKE_SHORTCUT", file);

		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			JMenuItem item = new JMenuItem("Add to Desktop Menu");
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					File f = new File(System.getProperty("user.home"), ".local/share/applications");
					f.mkdirs();
					LinuxUtil.createDesktopFile(f.getAbsolutePath(), false);
					JOptionPane.showMessageDialog(null, "Please log off to view the changes");
				}
			});
			file.add(item);
		}
		// addMenuItem("MEDIA_GRABBER", file);
		addMenuItem("EXIT", file);
		// addMenuItem("EXIT", file);

		JMenu dwn = createMenu(getString("DOWNLOAD"));

		addMenuItem("PAUSE", dwn);
		addMenuItem("RESUME", dwn);
		addMenuItem("RESTART", dwn);
		addMenuItem("START_Q", dwn);
		addMenuItem("STOP_Q", dwn);

		JMenu tools = createMenu(getString("TOOLS"));

		addMenuItem("OPTIONS", tools);
		addMenuItem("REFRESH_LINK", tools);
		addMenuItem("BROWSER_INT", tools);
		addMenuItem("CTX_ASM", tools);
		addMenuItem("THROTTLE_DLG", tools);

		JMenu view = createMenu(getString("VIEW"));
		addMenuItem("CTX_OPEN", view);
		addMenuItem("CTX_OPEN_FOLDER", view);
		addMenuItem("CTX_SAVE_AS", view);
		addMenuItem("CTX_COPY_URL", view);
		addMenuItem("CTX_COPY_FILE", view);
		// view.add(createSortMenu());

		JMenu help = createMenu(getString("HELP"));

		addMenuItem("CONTENT", help);
		// addMenuItem("REFRESH_HELP", help);
		addMenuItem("HOME_PAGE", help);
		addMenuItem("UPDATE", help);
		addMenuItem("ABOUT", help);

		// JMenuItem i1 = new JMenuItem("ON");
		// i1.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// w.setVideoPopup(true);
		// }
		// });
		// help.add(i1);
		//
		// JMenuItem i2 = new JMenuItem("OFF");
		// i2.addActionListener(new ActionListener() {
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// w.setVideoPopup(false);
		// }
		// });
		// help.add(i2);

		/*
		 * view.add(bint); JMenu help = new JMenu("help"); JMenuItem hvid = new
		 * JMenuItem("Capturing videos"); hvid.addActionListener(this);
		 * help.add(hvid); JMenuItem abi = new JMenuItem("Capturing downloads");
		 * abi.addActionListener(this); help.add(abi); JMenuItem hpg = new
		 * JMenuItem("XDM Home page"); hpg.addActionListener(this);
		 * help.add(hpg); JMenuItem updt = new JMenuItem("Check for update");
		 * updt.addActionListener(this); help.add(updt);
		 * 
		 * JMenuItem abt = new JMenuItem("About XDM");
		 * abt.addActionListener(this); help.add(abt);
		 */
		bar.add(file);
		bar.add(dwn);
		bar.add(view);
		bar.add(tools);
		bar.add(help);
		/*
		 * bar.add(dwn); bar.add(view); bar.add(help);
		 */
		// setJMenuBar(bar);
		// menuBox.add(bar);
		// bar.add(Box.createHorizontalGlue());
		// this.add(new
		// JLabel("Browser: None (Configure/Restart your Browser)"),
		// BorderLayout.SOUTH);
	}

	void addMenuItem(String id, JComponent menu) {
		JMenuItem mitem = new JMenuItem(getString(id));
		mitem.setName(id);
		mitem.addActionListener(this);
		menu.add(mitem);
	}

	String getString(String id) {
		String str = StringResource.getString(id);
		return str == null ? "" : str;
	}

	static ImageIcon getIcon(String name) {
		try {
			return new ImageIcon(XDMMainWindow.class.getResource("/res/" + name));
		} catch (Exception e) {
			return new ImageIcon("res/" + name);
		}
	}

	void tabClicked(ActionEvent e) {
		for (int i = 0; i < 3; i++) {
			if (btnTabArr[i] == e.getSource()) {
				btnTabArr[i].setBackground(new Color(242, 242, 242));
				btnTabArr[i].setForeground(Color.BLACK);
			} else {
				btnTabArr[i].setBackground(StaticResource.titleColor);
				btnTabArr[i].setForeground(StaticResource.whiteColor);
			}
		}
	}

	void updateSortText() {
		String text = "";
		switch (XDMConfig.sortField) {
		case 0:
			text = XDMConfig.sortAsc ? "Oldest on top" : "Newest on top";
			break;
		case 1:
			text = XDMConfig.sortAsc ? "Smallest on top" : "Largest on top";
			break;
		case 2:
			text = XDMConfig.sortAsc ? "Name [A-Z]" : "Name [Z-A]";
			break;
		case 3:
			text = XDMConfig.sortAsc ? "Type [A-Z]" : "Type [Z-A]";
			break;
		}
		btnSort.setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() instanceof JLabel) {
			String status = null;
			JLabel lbl = (JLabel) e.getSource();
			if (lbl.getName().equals("TREE_DOCUMENTS")) {
				status = IXDMConstants.DOCUMENTS;
			}
			if (lbl.getName().equals("TREE_COMPRESSED")) {
				status = IXDMConstants.COMPRESSED;
			}
			if (lbl.getName().equals("TREE_MUSIC")) {
				status = IXDMConstants.MUSIC;
			}
			if (lbl.getName().equals("TREE_PROGRAMS")) {
				status = IXDMConstants.PROGRAMS;
			}
			if (lbl.getName().equals("TREE_VIDEOS")) {
				status = IXDMConstants.VIDEO;
			}

			for (int i = 0; i < lblCatArr.length; i++) {
				if (lbl == lblCatArr[i]) {
					lblCatArr[i].setBackground(new Color(242, 242, 242));
					lblCatArr[i].setOpaque(true);
				} else {
					lblCatArr[i].setOpaque(false);
				}
			}
			sp.repaint();
			list.setType(status);
			model.fireTableDataChanged();
			return;
		}
		if (e.getSource() instanceof AbstractButton) {
			String name = ((AbstractButton) e.getSource()).getName();
			if (name == null) {
				return;
			}
			if ("ADD_URL".equals(name)) {
				addDownload();
			} else if ("ADV_YT".equals(name)) {
				AdvYTDlg aytd = new AdvYTDlg(config);
				aytd.setLocationRelativeTo(null);
				aytd.setVisible(true);
			} else if ("THROTTLE_DLG".equals(name)) {
				XDMThrottleDlg tdlg = new XDMThrottleDlg(config);
				tdlg.setLocationRelativeTo(null);
				tdlg.setVisible(true);
			} else if ("ALL_DOWNLOADS".equals(name)) {
				tabClicked(e);
				int state = 0;
				list.setState(state);
				model.fireTableDataChanged();
				// dlist.setModel(model);
				// dlist.repaint();
			} else if ("ALL_UNFINISHED".equals(name)) {
				tabClicked(e);
				int state = 1;
				list.setState(state);
				model.fireTableDataChanged();
				// dlist.repaint();
			} else if ("ALL_FINISHED".equals(name)) {
				tabClicked(e);
				int state = IXDMConstants.COMPLETE;
				list.setState(state);
				// dlist.setModel(model);//
				model.fireTableDataChanged();
				// dlist.repaint();
			} else if ("PAUSE".equals(name)) {
				pauseDownload();
			} else if ("RESUME".equals(name)) {
				resumeDownload();
			} else if ("DELETE".equals(name) || "DELETE_DWN".equals(name)) {
				removeDownloads();
			} else if ("RESTART".equals(name)) {
				restartDownload();
			} else if ("OPTIONS".equals(name)) {
				if (configDlg == null)
					configDlg = new ConfigDialog(this, config, this);
				configDlg.showDialog();
				// new ConfigWindow(config, this).showDialog();
			} else if ("EXIT".equals(name)) {
				exitXDM();
			} else if ("BATCH_DOWNLOAD".equals(name)) {
				if (batchDlg == null)
					batchDlg = new BatchDlg(this);
				batchDlg.setLocationRelativeTo(this);
				batchDlg.setVisible(true);
			} else if ("CLIP_ADD".equals(name)) {
				if (listDlg == null)
					listDlg = new BatchDownloadDlg();// this, config, this);
				listDlg.setLocationRelativeTo(this);
				listDlg.showDialog(config.destdir, this);
			} else if ("MEDIA_GRABBER".equals(name) || "GRABBER".equals(name)) {
				showGrabber();
			} else if ("YOUTUBE_DOWNLOADER".equals(name) || "YOUTUBE".equals(name)) {
				if (ytDlg == null)
					ytDlg = new YoutubeGrabberDlg(this);
				ytDlg.showDialog(this, config, "");
			} else if ("DELETE_COMPLETED".equals(name)) {
				deleteFinished();
			} else if ("REFRESH_LINK".equals(name)) {
				refreshLink();
			} else if ("CTX_OPEN".equals(name)) {
				openFile();
			} else if ("CTX_OPEN_FOLDER".equals(name)) {
				openFolder();
			} else if ("CTX_SAVE_AS".equals(name)) {
				renameFile();
			} else if ("BROWSER_INT".equals(name)) {
				showBrowserIntegrationDlg();
			} else if ("RESTORE".equals(name)) {
				restoreWindow();
			} else if ("ABOUT".equals(name)) {
				abtDlg = new AboutDialog();
				abtDlg.setLocationRelativeTo(null);
				abtDlg.setVisible(true);
			} else if ("PROPERTIES".equals(name)) {
				int index = table.getSelectedRow();
				if (index < 0) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem item = list.get(index);
				// PropertiesDialog.showDetails(item);
				new PropertyDialog(this, item).setVisible(true);
				;
			} else if ("HOME_PAGE".equals(name)) {
				XDMUtil.browse(AppInfo.HOMEPAGE);
			} else if ("UPDATE".equals(name)) {
				XDMUtil.browse(AppInfo.UPDATEURL);
			} else if ("CONTENT".equals(name)) {
				XDMUtil.browse(AppInfo.HELPURL);
			} else if ("REFRESH_HELP".equals(name)) {
				XDMUtil.browse(AppInfo.HELPURL);
			} else if ("CTX_ASC".equals(name)) {
				XDMConfig.sortAsc = true;
				sortItems[4].setFont(StaticResource.boldFont);
				sortItems[5].setFont(StaticResource.plainFont);
				sort();
			} else if ("CTX_DESC".equals(name)) {
				sortItems[5].setFont(StaticResource.boldFont);
				sortItems[4].setFont(StaticResource.plainFont);
				XDMConfig.sortAsc = false;
				sort();
			} else if (name.startsWith("COL:")) {
				for (int i = 0; i < 4; i++) {
					if (e.getSource() == sortItems[i]) {
						sortItems[i].setFont(StaticResource.boldFont);
					} else {
						sortItems[i].setFont(StaticResource.plainFont);
					}
				}
				String num = name.split(":")[1];
				XDMConfig.sortField = Integer.parseInt(num);
				sort();
			} else if ("BTN_SEARCH".equals(name)) {
				sort();
			} else if ("START_Q".equals(name)) {
				startQueue();
			} else if ("STOP_Q".equals(name)) {
				stopQueue();
			} else if ("CTX_ADD_Q".equals(name)) {
				int index = table.getSelectedRow();
				if (index < 0) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem item = list.get(index);
				if (item == null) {
					return;
				}
				add2Q(item, true);
			} else if ("CTX_DEL_Q".equals(name)) {
				int index = table.getSelectedRow();
				if (index < 0) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem item = list.get(index);
				if (item == null) {
					return;
				}
				add2Q(item, false);
			} else if ("CTX_SHOW_PRG".equals(name)) {
				int index = table.getSelectedRow();
				if (index < 0) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem item = list.get(index);
				if (item == null) {
					return;
				}
				if (item.window != null) {
					item.window.setVisible(true);
				}
			} else if ("MAKE_SHORTCUT".equals(name)) {
				JFileChooser jfc = XDMFileChooser.getFileChooser(JFileChooser.DIRECTORIES_ONLY,
						new File(System.getProperty("user.home")));
				if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
					createShortcut(jfc.getSelectedFile());
				}
			} else if ("CTX_ASM".equals(name)) {
				int index = table.getSelectedRow();
				if (index < 0) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem item = list.get(index);
				if (item == null) {
					return;
				}
				if (item.mgr != null || (item.state == IXDMConstants.COMPLETE)) {
					showMessageBox(getString("DWN_ACTIVE_OR_FINISHED"), getString("DEFAULT_TITLE"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (JOptionPane.showConfirmDialog(this, getString("ASM_WRN"), getString("DEFAULT_TITLE"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					if (asmDlg == null) {
						asmDlg = new AssembleDialog(this);
					}
					asmDlg.startAssemble(item.filename, item.saveto, item.tempdir);
				}
			} else if ("CTX_COPY_FILE".equals(name)) {
				int index[] = table.getSelectedRows();
				if (index == null || index.length < 1) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem items[] = new DownloadListItem[index.length];
				for (int i = 0; i < index.length; i++) {
					items[i] = list.get(index[i]);
				}
				try {
					copyFiles(items);
				} catch (Exception exx) {
					exx.printStackTrace();
				}
			} else if ("CTX_COPY_URL".equals(name)) {
				int index = table.getSelectedRow();
				if (index < 0) {
					showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				DownloadListItem item = list.get(index);
				if (item == null) {
					return;
				}
				copyURL(item.url);
			}
		}
	}

	public void renameFile(DownloadListItem item, int row) {
		if (item.state == IXDMConstants.COMPLETE) {
			showMessageBox(getString("DWN_FINISHED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
		} else {
			String file, folder;
			JFileChooser jfc = XDMFileChooser.getFileChooser(JFileChooser.FILES_ONLY,
					new File(item.saveto, item.filename));
			if (jfc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			file = jfc.getSelectedFile().getName();
			folder = jfc.getSelectedFile().getParent();
			if (item.mgr != null) {
				if (item.mgr.getState() == IXDMConstants.ASSEMBLING) {
					showMessageBox(getString("DWN_ASSEMBLING"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
				} else {
					item.mgr.setDestdir(folder);
					item.mgr.setFileName(file);
				}
			}
			item.filename = file;
			item.saveto = folder;
			list.downloadStateChanged();

			model.fireTableDataChanged();// model.fireListItemUpdated(row);
		}
	}

	private void renameFile() {
		int row = table.getSelectedRow();
		if (row < 0)
			return;
		DownloadListItem item = list.get(row);
		if (item == null)
			return;
		renameFile(item, row);
	}

	private void openFolder() {
		int row = table.getSelectedRow();
		if (row < 0)
			return;
		DownloadListItem item = list.get(row);
		if (item == null)
			return;
		File folder = new File(item.saveto);
		if (!folder.exists()) {
			showMessageBox(getString("FOLDER_NOT_FOUND"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
		} else {
			XDMUtil.open(folder);
		}
	}

	private void openFile() {
		int row = table.getSelectedRow();
		if (row < 0)
			return;
		DownloadListItem item = list.get(row);
		if (item == null)
			return;
		if (item.state == IXDMConstants.COMPLETE) {
			File file = new File(item.saveto, item.filename);
			if (file.exists()) {
				XDMUtil.open(file);
			} else {
				showMessageBox(getString("FILE_NOT_FOUND"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			showMessageBox(getString("DWN_INCOMPLETE"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
		}
	}

	void createContextMenu() {
		ctxPopup = new JPopupMenu();
		addMenuItem("CTX_OPEN", ctxPopup);
		addMenuItem("CTX_OPEN_FOLDER", ctxPopup);
		addMenuItem("CTX_SAVE_AS", ctxPopup);
		addMenuItem("CTX_SHOW_PRG", ctxPopup);
		addMenuItem("PAUSE", ctxPopup);
		addMenuItem("RESUME", ctxPopup);
		addMenuItem("RESTART", ctxPopup);
		addMenuItem("DELETE", ctxPopup);
		addMenuItem("REFRESH_LINK", ctxPopup);
		addMenuItem("CTX_ASM", ctxPopup);
		addMenuItem("CTX_ADD_Q", ctxPopup);
		addMenuItem("CTX_DEL_Q", ctxPopup);

		// ctxPopup.add(createSortMenu());
		addMenuItem("CTX_COPY_URL", ctxPopup);
		addMenuItem("CTX_COPY_FILE", ctxPopup);
		addMenuItem("PROPERTIES", ctxPopup);
		ctxPopup.setInvoker(table);
	}

	// JMenu createSortMenu() {
	// JMenu sortMenu = new JMenu(getString("CTX_SORT"));
	// sortMenu.setName("CTX_SORT");
	// sortMenu.addActionListener(this);
	// sortMenu.setIcon(new XDMBlankIcon(15, 10));
	// sortMenu.setBorderPainted(false);
	// sortMenu.setMargin(new Insets(10, 0, 0, 0));
	//
	// ButtonGroup bg1 = new ButtonGroup();
	//
	// for (int i = 1; i < model.getColumnCount(); i++) {
	// JCheckBoxMenuItem item = new JCheckBoxMenuItem(model
	// .getColumnName(i));
	// item.setName("COL:" + i);
	// item.addActionListener(this);
	// sortMenu.add(item);
	// bg1.add(item);
	// if (TableSortInfo.getSortingField() == i) {
	// item.setSelected(true);
	// }
	// }
	// sortMenu.addSeparator();
	// ButtonGroup bg2 = new ButtonGroup();
	// JCheckBoxMenuItem item1 = new JCheckBoxMenuItem(getString("CTX_ASC"));
	// item1.setName("CTX_ASC");
	// item1.addActionListener(this);
	// sortMenu.add(item1);
	// bg2.add(item1);
	// JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(getString("CTX_DESC"));
	// item2.setName("CTX_DESC");
	// item2.addActionListener(this);
	// sortMenu.add(item2);
	// bg2.add(item2);
	//
	// if (TableSortInfo.isAscending()) {
	// item1.setSelected(true);
	// } else {
	// item2.setSelected(true);
	// }
	// return sortMenu;
	// }

	private void refreshLink() {
		int index = table.getSelectedRow();
		if (index < 0) {
			showMessageBox(getString("NONE_SELECTED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		DownloadListItem item = list.get(index);
		if (item != null) {
			if (item.state != IXDMConstants.COMPLETE && item.mgr == null) {
				String url = XDMUtil.isNullOrEmpty(item.referer) ? item.url : item.referer;
				url = RefreshLinkDlg.showDialog(this, url);
				if (url != null) {
					item.url = url;
					model.fireTableRowsUpdated(index, index);
					list.downloadStateChanged();
				}
			} else {
				showMessageBox(getString("DWN_ACTIVE_OR_FINISHED"), getString("DEFAULT_TITLE"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void exitXDM() {
		config.mwX = getX();
		config.mwY = getY();
		config.mwW = getWidth();
		config.mwH = getHeight();

		if (w != null) {
			if (!w.isVideoMode) {
				config.dbX = w.getX();
			} else {
				int x = w.getLocationOnScreen().x + DropBox.VIDEO_WIDTH - 22;
				if (x > 0) {
					x = Math.min(x, Toolkit.getDefaultToolkit().getScreenSize().width - 30);
				}
				config.dbX = x;
			}
			config.dbY = w.getY();
		}
		list.downloadStateChanged();
		config.save();
		Authenticator.getInstance().save();
		System.exit(0);
	}

	public void initBatchDownload(java.util.List<String> list, String user, String pass) {
		// System.out.println("Batch");
		if (list == null || list.size() < 1) {
			return;
		}
		if (listDlg == null) {
			listDlg = new BatchDownloadDlg();
		}
		listDlg.setLocationRelativeTo(null);
		java.util.List<BatchItem> blist = new ArrayList<BatchItem>();
		for (int i = 0; i < list.size(); i++) {
			BatchItem item = new BatchItem();
			item.url = list.get(i);
			item.fileName = XDMUtil.getFileName(item.url);
			item.user = user;
			item.pass = pass;
			item.dir = config.destdir;
			blist.add(item);
		}
		listDlg.showDialog(blist, config.destdir, this);
	}

	@Override
	public void mediaCaptured(String name, String url, String url2, String type, String size, String referer, String ua,
			ArrayList<String> cookies) {

		String filename = XDMUtil.isNullOrEmpty(name) ? XDMUtil.getFileName(url) : name;
		System.out.println("set media info name: " + filename);
		MediaInfo info = new MediaInfo();
		info.name = XDMUtil.createSafeFileName(filename);
		info.url = url;
		info.url2 = url2;
		info.referer = referer;
		info.userAgent = ua;
		info.type = type;
		info.size = size;
		info.cookies = cookies;
		vp.addVideo(info);
		// mediaModel.add(info);
		// showNotification();
		// mediaDlg.setVisible(true);
	}

	@Override
	public void showGrabber() {

	}

	int hotcount = 0;
	boolean hot = false;

	@Override
	public void showNotification() {
		w.setVideoPopup(true);
	}

	@Override
	public void showNotificationText(String text, String title) {
		showNotification();
	}

	public void addDownload(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent, String noconfirm) {
		NewDownloadWindow fdlg = new NewDownloadWindow(this, config);
		fdlg.setURL(url);
		fdlg.file.setText(name);
		fdlg.setDir(config.destdir);
		fdlg.referer = referer;
		fdlg.cookies = cookies;
		fdlg.userAgent = userAgent;
		fdlg.noconfirm = noconfirm;
		fdlg.showDlg();
	}

	void deleteFinished() {
		if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete all completed downloads",
				"Confirm delete", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		ArrayList<DownloadListItem> lists = new ArrayList<DownloadListItem>();
		for (int i = 0; i < list.list.size(); i++) {
			DownloadListItem item = list.list.get(i);
			if (item.state == IXDMConstants.COMPLETE) {
				lists.add(item);
			}
		}
		for (int i = 0; i < lists.size(); i++) {
			list.list.remove(lists.get(i));
		}
		model.fireTableDataChanged();
		list.downloadStateChanged();
	}

	void createTray(JFrame parent) {
		vp = new XDMVideoPanel(this, config);
		vp.setVisible(true);

		if (SystemTray.isSupported()) {
			ImageIcon ico = StaticResource.getIcon("xdm22.png", "res");
			// Dimension d = SystemTray.getSystemTray().getTrayIconSize();
			TrayIcon ti = new TrayIcon(
					ico.getImage()/*
									 * TrayIconHelper.getImage(d.width,
									 * d.height, ico)
									 */);
			ti.setImageAutoSize(true);
			// System.out.println();
			try {
				SystemTray.getSystemTray().add(ti);
			} catch (AWTException e) {
				e.printStackTrace();
			}

			ti.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					setVisible(true);
				}
			});
		} else {
			System.out.println("System Tray could not be created");
		}
	}

	HelpDialog getHTMLViwer() {
		HashMap<String, URL> map = new HashMap<String, URL>();
		map.put("Browser Integration", getClass().getResource("/help/browser_integration.html"));
		map.put("Capturing Videos", getClass().getResource("/help/video_download.html"));
		map.put("Refresh Broken Downloads", getClass().getResource("/help/refresh_link.html"));
		HelpDialog hlp = new HelpDialog();
		hlp.addPages(map);
		return hlp;
	}

	static HashMap<String, String> arg = new HashMap<String, String>();

	static void parseArgs(String[] a) {
		String key = "url";
		boolean min = false;
		for (int i = 0; i < a.length; i++) {
			if (a[i].equals("-c")) {
				key = "cookie";
				continue;
			} else if (a[i].equals("-r")) {
				key = "referer";
				continue;
			} else if (a[i].equals("-m")) {
				min = true;
				continue;
			} else if (a[i].equals("-u")) {
				key = "url";
				continue;
			} else if (a[i].equals("-n")) {
				arg.put("noconfirm", "noconfirm");
				continue;
			} else {
				arg.put(key, a[i]);
			}
		}
		if (min) {
			arg.put("min", "true");
		}
	}

	void showBrowserIntegrationDlg() {
		if (biDlg == null) {
			biDlg = new BrowserIntegrationDlg(this, config);
		}
		biDlg.setLocationRelativeTo(this);
		// biDlg.setModal(true);
		biDlg.setVisible(true);
	}

	void sort() {
		list.searchStr = txtSearch.getText();
		updateSortText();
		list.sort();
		list.downloadStateChanged();
		model.fireTableDataChanged();
	}

	boolean processNextQueuedDownload() {
		System.out.println("ProcessNextQueue");
		if (!processQueue)
			return false;
		if (qi != null) {
			return false;
		}

		for (int i = 0; i < list.list.size(); i++) {
			DownloadListItem di = (DownloadListItem) list.list.get(i);
			if (di.mgr == null && (!(di.state == IXDMConstants.COMPLETE))) {
				if (di.q) {
					qi = di;
					resumeDownload(di);
					return true;
				}
			}
		}
		qi = null;
		processQueue = false;
		config.schedule = false;
		schedulerActive = false;
		return false;
	}

	void schedulerRun() {
		schedulerActive = true;
		Logger.log("SchedulerRun");
		try {
			if (System.currentTimeMillis() > config.endDate.getTime()) {
				Logger.log("Scheduler outdated");
				return;
			}
			Logger.log("Starting scheduler");
			while (true) {
				if (!config.schedule) {
					Logger.log("Scheduler killed");
					return;
				}

				if (System.currentTimeMillis() > config.startDate.getTime()) {
					break;
				}
				Thread.sleep(1000);
			}

			if (!processQueue) {
				Logger.log("Starting queue scheduler");
				processQueue = true;
				processNextQueuedDownload();
			} else {
				Logger.log("Queue already started");
			}

			while (true) {
				if (!processQueue) {
					return;
				}
				if (!config.schedule) {
					Logger.log("Scheduler killed");
					if (processQueue) {
						stopQueue();
						processQueue = false;
					}
					return;
				}
				if (System.currentTimeMillis() > config.endDate.getTime()) {
					if (processQueue) {
						stopQueue();
						processQueue = false;
					}
					Logger.log("Time ended processQ: " + processQueue);
					config.schedule = false;
					return;
				}
				Thread.sleep(1000);
			}
		} catch (Exception ex) {
			Logger.log(ex);
		} finally {
			schedulerActive = false;
		}
	}

	void add2Q(DownloadListItem item, boolean add) {
		if (item.mgr != null || (item.state == IXDMConstants.COMPLETE)) {
			showMessageBox(getString("DWN_ACTIVE_OR_FINISHED"), getString("DEFAULT_TITLE"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		item.q = add;
		list.downloadStateChanged();
		model.fireTableDataChanged();
	}

	void executeCommands() {
		if (config.executeCmd) {
			exec(config.cmdTxt);
		}
		if (config.halt) {
			if (System.getProperty("os.name").contains("OS X")) {
				OSXUtil.shutdown();
			}
		}
		// if (config.antivir) {
		// exec(config.antivirTxt);
		// }
		// if (config.hungUp) {
		// exec(config.hungUpTxt);
		// }
		// if (config.halt) {
		// if (System.getProperty("os.name").toLowerCase().contains("linux")) {
		// LinuxUtil.initShutdownDeamon();
		// } else {
		// exec(config.haltTxt);
		// }
		// }
	}

	void exec(String cmd) {
		try {
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void createShortcut(File f) {
		if (System.getProperty("os.name").contains("OS X")) {
			OSXUtil.createAppBundle(f);
		} else {
			LinuxUtil.createDesktopFile(f.getAbsolutePath(), false);
		}
	}

	void copyFiles(DownloadListItem items[]) {
		Object values[] = null;
		if (items != null) {
			values = new Object[items.length];
			for (int i = 0; i < items.length; i++) {
				DownloadListItem item = items[i];
				File file = new File(item.saveto, item.filename);
				// System.out.println(file);
				values[i] = file;
			}
		}
		StringBuffer plainBuf = new StringBuffer();
		StringBuffer htmlBuf = new StringBuffer();

		htmlBuf.append("<html>\n<body>\n<ul>\n");

		for (int i = 0; i < values.length; i++) {
			Object obj = values[i];
			String val = ((obj == null) ? "" : obj.toString());
			plainBuf.append(val + "\n");
			htmlBuf.append("  <li>" + val + "\n");
		}
		if (clipboard == null) {
			clipboard = t.getSystemClipboard();
		}
		clipboard.setContents(new FileTransferable(plainBuf.toString(), htmlBuf.toString(), values), null);

	}

	void copyURL(String url) {
		System.out.println("Copying url " + url);
		if (clipboard == null) {
			clipboard = t.getSystemClipboard();
		}
		clipboard.setContents(new StringSelection(url), null);
	}

	public static void main(String[] args) {
		// System.out.println(XDMUtil.getType("http://r1---sn-cvh7zn7r.c.youtube.com/videoplayback?signature=417319AA77E6CF062C8E023A70F08734C7783C8C.5B6DE651A6E81A8642C8D51FFE74054015E9F5CC&ms=nxu&mv=m&mt=1438102993&upn=NhkT6bc_P-U&mn=sn-cvh7zn7r&mm=30&id=o-ANGKCmq6vA-45VhkDMUkOiFOXcBjUZuZAUkFEFkjhZ0j&cp=U0lSTVFNUl9KTUNPMl9JR1ZHOjV5bnJQTmI1OXc1&fexp=901816%2C9408710%2C9408940%2C9409208%2C9410705%2C9412776%2C9413147%2C9414660%2C9414824%2C9414929%2C9415365%2C9415485%2C9415745%2C9416126%2C9416267%2C9416861%2C9418204%2C948207&sparams=cp%2Cdur%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&ip=1.39.12.243&gcr=in&source=youtube&ratebypass=yes&pl=22&initcwndbps=223750&dur=152.253&requiressl=yes&pcm2=no&key=yt5&mime=video%2Fmp4&expire=1438124653&itag=18&sver=3&lmt=1431495463866713&ipbits=0"));
		System.setProperty("apple.awt.UIElement", "true");
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.menu.about.name", "XDM");

		try {
			UIManager.setLookAndFeel(new XDMLookAndFeel());
		} catch (Exception e) {
		}

		String jarPath = XDMUtil.getJarPath();
		// https://plus.google.com/u/0/_/initialdata?soc-app=162&cid=0&soc-platform=1&ozv=es_oz_20160929.14_p1&f.sid=1507918028&_reqid=78852&rt=j

		System.out.println(jarPath);
		if (System.getProperty("os.name").contains("OS X")) {
			if (jarPath != null) {
				if (jarPath.startsWith("/Volumes/xdm6setup")) {
					OSXInstallWindow osx = new OSXInstallWindow();
					osx.setLocationRelativeTo(null);
					osx.setModal(true);
					osx.setVisible(true);
					System.exit(0);
				}
			}
		}

		boolean firstRun = false;
		File fAppDir = new File(System.getProperty("user.home"), ".xdm");
		File fTmpDir = new File(fAppDir, "temp");

		File configFile = new File(fAppDir, ".xdmconf");
		// firstRun = !fAppDir.exists();
		config = XDMConfig.load(configFile);
		firstRun = config.firstRun;
		config.firstRun = false;
		parseArgs(args);

		if (firstRun) {
			fAppDir.mkdirs();
			fTmpDir.mkdirs();
		}

		tempdir = fTmpDir.getAbsolutePath();
		destdir = System.getProperty("user.home");

		if (firstRun) {
			File f = new File(System.getProperty("user.home"), "Downloads");
			if (f.exists()) {
				destdir = f.getAbsolutePath();
				System.out.println(destdir);
			}
		}

		appdir = fAppDir.getAbsolutePath();

		try {
			StringResource.loadResource("en");
			XDMIconMap.setIcon("DOC", getIcon("document.png"));
			XDMIconMap.setIcon("MUSIC", getIcon("music.png"));
			XDMIconMap.setIcon("OTHER", getIcon("other.png"));
			XDMIconMap.setIcon("APP", getIcon("exe.png"));
			XDMIconMap.setIcon("VID", getIcon("video.png"));
			XDMIconMap.setIcon("ZIP", getIcon("arc.png"));
			XDMIconMap.setIcon("FOLDER", getIcon("folder.png"));
			XDMIconMap.setIcon("RIGHT_ARROW", getIcon("left_arrow.png"));
			XDMIconMap.setIcon("LEFT_ARROW", getIcon("right_arrow.png"));
			XDMIconMap.setIcon("UP_ARROW", getIcon("up_arrow.png"));
			XDMIconMap.setIcon("DOWN_ARROW", getIcon("down_arrow.png"));
			XDMIconMap.setIcon("EXPAND_ICON", getIcon("expand.png"));
			XDMIconMap.setIcon("COLLAPSE_ICON", getIcon("collapse.png"));
			XDMIconMap.setIcon("APP_ICON", getIcon("icon.png"));
			XDMIconMap.setIcon("Q_ICON", getIcon("q.png"));
			XDMIconMap.setIcon("YT_ICON", getIcon("youtube72.png"));
			XDMIconMap.setIcon("RF_ICON", getIcon("restart.png"));
			XDMIconMap.setIcon("COLD_ICON", getIcon("xdm22.png"));
			XDMIconMap.setIcon("HOT_ICON", getIcon("xdm22_hot.png"));
			XDMIconMap.setIcon("BACK_ICON", getIcon("back.png"));
			XDMIconMap.setIcon("BACK_R_ICON", getIcon("back_r.png"));
			XDMIconMap.setIcon("NEXT_ICON", getIcon("next.png"));
			XDMIconMap.setIcon("NEXT_R_ICON", getIcon("next_r.png"));
			XDMIconMap.setIcon("FF_ICON", getIcon("firefox.png"));
			XDMIconMap.setIcon("CR_ICON", getIcon("chrome.png"));
			XDMIconMap.setIcon("OP_ICON", getIcon("opera.png"));
			XDMIconMap.setIcon("OT_ICON", getIcon("browser.png"));
			XDMIconMap.setIcon("CI_ICON", getIcon("chrome-inst.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		XDMMainWindow mw = new XDMMainWindow();

		XDMServer server = new XDMServer(config, mw, mw);
		if (!server.start()) {
			server.sendParams(arg);
			System.exit(0);
		}

		if (arg.get("min") == null) {
			mw.setVisible(true);
		}
		mw.createTray(null);

		Authenticator.getInstance().load(new File(fAppDir, ".xdmauth"));

		mw.configChanged();

		if (firstRun) {
			try {

				if (System.getProperty("os.name").contains("OS X")) {
					// OSXUtil.createAppBundle(new
					// File(System.getProperty("user.home"), "Desktop"));
					OSXUtil.enableAutoStart();
					config.autostart = true;
				} else {
					// mw.createShortcut(new File(XDMUtil.getJarPath()));
					mw.createShortcut(new File(System.getProperty("user.home"), "Desktop"));
					LinuxUtil.enableAutoStartLinux();
					config.autostart = true;
					File appDir = new File(System.getProperty("user.home"), ".local/share/applications");
					appDir.mkdirs();
					LinuxUtil.createDesktopFile(appDir.getAbsolutePath(), false);
				}
			} catch (Exception e) {
				System.out.println(e);
			}
			mw.showBrowserIntegrationDlg();
		}
		// new XDMVideoPanel(mw, config).setVisible(true);
		// mw.downloadNow2("http://localhost/gulabi.mp4?clen=44402297",
		// "http://localhost/gulabi.mp4?clen=44402297",
		// "gulabi.mp4", "/home/subhr0/Desktop", null, null, null, null,
		// "test");
		// new
		// VideoDownloadDialog(null,null,null,null,null,null,config,null).setVisible(true);

		// new PropertyDialog(mw,null).setVisible(true);
		// VideoDownloadDialog(null,null,null,null,null,null,config,null).setVisible(true);
	}

	
	@Override
	public void addDownload(String url, String name, String folder, String user, String pass, String referer,
			ArrayList<String> cookies, String userAgent) {
		NewDownloadWindow fdlg = new NewDownloadWindow(this, config);
		fdlg.setURL(url);
		fdlg.file.setText(name);
		fdlg.setDir(config.destdir);
		fdlg.referer = referer;
		fdlg.cookies = cookies;
		fdlg.userAgent = userAgent;
		fdlg.showDlg();
	}
}
