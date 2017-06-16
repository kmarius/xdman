package org.sdg.xdman.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import org.sdg.xdman.core.common.Assembler;

public class AssembleDialog extends JDialog implements Runnable {

	private static final long serialVersionUID = -2063101566749868919L;

	JProgressBar prg;
	JButton btn;
	JLabel lbl;
	boolean stop = false;

	private void init() {
		setModal(true);
		setTitle(StringResource.getString("ASM_TTL"));
		setIconImage(XDMIconMap.getIcon("APP_ICON").getImage());
		setSize(300, 150);
		Box box = Box.createVerticalBox();
		box.setBorder(new EmptyBorder(6, 6, 6, 6));
		Box hbox1 = Box.createHorizontalBox();
		lbl = new JLabel(StringResource.getString("ASM_LBL"), JLabel.LEFT);
		hbox1.add(lbl);
		hbox1.add(Box.createHorizontalGlue());
		prg = new JProgressBar();
		prg.setIndeterminate(true);
		btn = new JButton(StringResource.getString("CANCEL"));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		box.add(Box.createVerticalStrut(10));
		box.add(hbox1);
		box.add(Box.createVerticalGlue());
		box.add(prg);
		box.add(Box.createVerticalGlue());
		Box hbox = Box.createHorizontalBox();
		hbox.add(Box.createHorizontalGlue());
		box.add(Box.createVerticalStrut(10));
		hbox.add(btn);
		box.add(hbox);
		add(box);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				stop();
			}
		});
	}

	public AssembleDialog(JFrame f) {
		super(f);
		init();
	}

	public static void main(String[] args) {
		new AssembleDialog(null).setVisible(true);
	}

	String filename, destdir, tmpdir;

	Thread t;

	void startAssemble(String filename, String destdir, String tmpdir) {
		Assembler.stop = false;
		this.destdir = destdir;
		this.tmpdir = tmpdir;
		this.filename = filename;
		t = new Thread(this);
		t.start();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void run() {
		if (!Assembler.forceAssemble(tmpdir, destdir, filename)) {
			if (!Assembler.stop) {
				JOptionPane.showMessageDialog(this, StringResource
						.getString("ASM_FL"));
			}
		}
		setVisible(false);
	}

	void stop() {
		Assembler.stop = true;
		if (t != null)
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		setVisible(false);
	}
}
