package org.sdg.xdman.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;

public class XDMFrame extends JFrame {

	private static final long serialVersionUID = -8094995420106046965L;

	private boolean maximizeBox = true, minimizeBox = true;

	public XDMFrame() {
		setUndecorated(true);
		createCursors();
		createResizeGrip();
		panTitle = new TitlePanel(new BorderLayout(), this);
		panTitle.setBackground(StaticResource.titleColor);
		panTitle.setBorder(new EmptyBorder(8, 8, 0, 8));
		panTitle.setOpaque(true);
		registerTitlePanel(panTitle);
		panClient = new JPanel(new BorderLayout());
		panClient.setBackground(Color.WHITE);
		JPanel panContent = new JPanel(new BorderLayout());
		panContent.add(panTitle, BorderLayout.NORTH);
		panContent.add(panClient);
		super.add(panContent);
	}

	public JPanel getTitlePanel() {
		return panTitle;
	}

	public void setMaximizeBox(boolean maximizeBox) {
		this.maximizeBox = maximizeBox;
	}

	public boolean isMaximizeBox() {
		return maximizeBox;
	}

	public void setMinimizeBox(boolean minimizeBox) {
		this.minimizeBox = minimizeBox;
	}

	public boolean isMinimizeBox() {
		return minimizeBox;
	}

	@Override
	public Component add(Component c) {
		return panClient.add(c);
	}

	JPanel panTitle, panClient;

	private JLabel lblRightGrip, lblLeftGrip, lblTopGrip, lblBottomGrip;

	private void createResizeGrip() {
		GripMouseAdapter gma = new GripMouseAdapter();
		lblRightGrip = new JLabel();
		lblRightGrip.setMaximumSize(new Dimension(2, lblRightGrip
				.getMaximumSize().height));
		lblRightGrip.setPreferredSize(new Dimension(2, lblRightGrip
				.getPreferredSize().height));
		lblRightGrip.setBackground(Color.BLACK);
		lblRightGrip.setOpaque(true);
		super.add(lblRightGrip, BorderLayout.EAST);

		lblBottomGrip = new JLabel();
		lblBottomGrip.setMaximumSize(new Dimension(lblBottomGrip
				.getPreferredSize().width, 2));
		lblBottomGrip.setPreferredSize(new Dimension(lblBottomGrip
				.getPreferredSize().width, 2));
		lblBottomGrip.setBackground(Color.BLACK);
		lblBottomGrip.setOpaque(true);
		super.add(lblBottomGrip, BorderLayout.SOUTH);

		lblLeftGrip = new JLabel();
		lblLeftGrip.setMaximumSize(new Dimension(2, lblLeftGrip
				.getPreferredSize().height));
		lblLeftGrip.setPreferredSize(new Dimension(2, lblLeftGrip
				.getPreferredSize().height));
		lblLeftGrip.setBackground(Color.BLACK);
		lblLeftGrip.setOpaque(true);
		super.add(lblLeftGrip, BorderLayout.WEST);

		lblTopGrip = new JLabel();
		lblTopGrip.setMaximumSize(new Dimension(
				lblTopGrip.getPreferredSize().width, 2));
		lblTopGrip.setPreferredSize(new Dimension(
				lblTopGrip.getPreferredSize().width, 2));
		lblTopGrip.setBackground(Color.BLACK);
		lblTopGrip.setOpaque(true);
		super.add(lblTopGrip, BorderLayout.NORTH);

		if (isResizable()) {

			lblTopGrip.addMouseListener(gma);

			lblTopGrip.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent me) {
					int y = me.getYOnScreen();
					int diff = XDMFrame.this.getLocationOnScreen().y - y;
					XDMFrame.this.setLocation(XDMFrame.this.getLocation().x, me
							.getLocationOnScreen().y);
					System.out.println(diff);
					XDMFrame.this.setSize(XDMFrame.this.getWidth(),
							XDMFrame.this.getHeight() + diff);
				}
			});

			lblRightGrip.addMouseListener(gma);

			lblRightGrip.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent me) {
					int x = me.getXOnScreen();
					int diff = x - XDMFrame.this.getLocationOnScreen().x;
					XDMFrame.this.setSize(diff, XDMFrame.this.getHeight());
				}
			});

			lblLeftGrip.addMouseListener(gma);

			lblLeftGrip.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent me) {
					int x = me.getXOnScreen();
					int diff = XDMFrame.this.getLocationOnScreen().x - x;
					XDMFrame.this.setLocation(me.getLocationOnScreen().x,
							XDMFrame.this.getLocation().y);
					XDMFrame.this.setSize(diff + XDMFrame.this.getWidth(),
							XDMFrame.this.getHeight());
				}
			});

			lblBottomGrip.addMouseListener(gma);

			lblBottomGrip.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent me) {
					int y = me.getYOnScreen();
					int diff = y - XDMFrame.this.getLocationOnScreen().y;
					XDMFrame.this.setSize(XDMFrame.this.getWidth(), diff);
				}
			});
		}
	}

	int diffx, diffy;

	Box vBox;

	void registerTitlePanel(JPanel panel) {

		vBox = Box.createVerticalBox();
		vBox.setOpaque(true);
		vBox.setBackground(StaticResource.titleColor);
		Box hBox = Box.createHorizontalBox();
		hBox.setBackground(StaticResource.titleColor);

		if (minimizeBox) {
			hBox.add(createTransparentButton(StaticResource
					.getIcon("min_btn.png"), new Dimension(24, 24), actMin));
		}

		if (maximizeBox) {
			hBox.add(createTransparentButton(StaticResource
					.getIcon("max_btn.png"), new Dimension(24, 24), actMax));
		}

		hBox.add(createTransparentButton(StaticResource
				.getIcon("close_btn.png"), new Dimension(24, 24), actClose));

		vBox.add(hBox);
		vBox.add(Box.createVerticalGlue());
		// vBox.add(new JButton("text"));

		panel.add(vBox, BorderLayout.EAST);
	}

	ActionListener actClose = new ActionListener() {
		public void actionPerformed(ActionEvent action) {
			XDMFrame.this.dispatchEvent(new WindowEvent(XDMFrame.this,
					WindowEvent.WINDOW_CLOSING));
		};
	};

	ActionListener actMax = new ActionListener() {
		public void actionPerformed(ActionEvent action) {
			XDMFrame.this.setMaximizedBounds(GraphicsEnvironment
					.getLocalGraphicsEnvironment().getMaximumWindowBounds());
			XDMFrame.this
					.setExtendedState((XDMFrame.this.getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH ? JFrame.NORMAL
							: JFrame.MAXIMIZED_BOTH);
		};
	};

	ActionListener actMin = new ActionListener() {
		public void actionPerformed(ActionEvent action) {
			XDMFrame.this.setExtendedState(XDMFrame.this.getExtendedState()
					| JFrame.ICONIFIED);
		};
	};

	class GripMouseAdapter extends MouseAdapter {
		@Override
		public void mouseEntered(MouseEvent me) {
			if (me.getSource() == lblBottomGrip) {
				lblBottomGrip.setCursor(curSResize);
			} else if (me.getSource() == lblRightGrip) {
				lblRightGrip.setCursor(curEResize);
			} else if (me.getSource() == lblLeftGrip) {
				lblLeftGrip.setCursor(curWResize);
			} else if (me.getSource() == lblTopGrip) {
				lblTopGrip.setCursor(curNResize);
			}
		}

		@Override
		public void mouseExited(MouseEvent me) {
			((JLabel) me.getSource()).setCursor(curDefault);
		}

	}

	Cursor curDefault, curNResize, curEResize, curWResize, curSResize,
			curSEResize, curSWResize;

	private void createCursors() {
		curDefault = new Cursor(Cursor.DEFAULT_CURSOR);
		curNResize = new Cursor(Cursor.N_RESIZE_CURSOR);
		curWResize = new Cursor(Cursor.W_RESIZE_CURSOR);
		curEResize = new Cursor(Cursor.E_RESIZE_CURSOR);
		curSResize = new Cursor(Cursor.S_RESIZE_CURSOR);
	}

	JButton createTransparentButton(ImageIcon icon, Dimension d,
			ActionListener actionListener) {
		XDMButton btn = new XDMButton(icon);
		btn.setBackground(StaticResource.titleColor);
		// btn.setRolloverIcon(rIcon);
		btn.setBorderPainted(false);
		// btn.setContentAreaFilled(false);
		btn.setFocusPainted(false);
		btn.setPreferredSize(d);
		btn.addActionListener(actionListener);
		return btn;
	}

}
