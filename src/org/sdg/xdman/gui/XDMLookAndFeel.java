package org.sdg.xdman.gui;

import java.awt.Color;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class XDMLookAndFeel extends MetalLookAndFeel {

	private static final long serialVersionUID = 6437510613485554397L;

	public XDMLookAndFeel() {
		setCurrentTheme(new XDMTheme());
	}

	@Override
	public void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);
		table.putDefaults(new Object[] { "ButtonUI",
				XDMButtonUI.class.getName(), "ScrollBarUI",
				XDMScrollBarUI.class.getName(), "MenuItemUI",
				XDMMenuItemUI.class.getName(), "MenuUI",
				XDMMenuUI.class.getName(), "CheckBoxMenuItemUI",
				XDMMenuItemUI.class.getName(), "TreeUI",
				XDMTreeUI.class.getName(), "SpinnerUI",
				XDMSpinnerUI.class.getName(), "ProgressBarUI",
				XDMProgressBarUI.class.getName(), "ComboBoxUI",
				XDMComboBoxUI.class.getName() // ,
				// "TabbedPaneUI",
				// BasicTabbedPaneUI.class.getName()
				});
		System.setProperty("xdm.defaulttheme", "true");
		UIManager.put("TabbedPane.selected", new Color(220, 220, 220));
		UIManager.put("TabbedPane.borderHightlightColor", Color.LIGHT_GRAY);
		UIManager.put("TabbedPane.contentAreaColor", Color.LIGHT_GRAY);
		UIManager.put("TabbedPane.contentOpaque", Boolean.FALSE);
		UIManager.put("Table.focusCellHighlightBorder", new EmptyBorder(1, 1,
				1, 1));
		UIManager.put("OptionPane.background", new ColorUIResource(Color.WHITE));
		UIManager.put("Panel.background", new ColorUIResource(Color.WHITE));
		UIManager.put("CheckBox.background", new ColorUIResource(Color.WHITE));
	}

	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults(table);
		table.putDefaults(new Object[] { "ComboBox.selectionBackground",
				Color.LIGHT_GRAY });
	}

	public String getName() {
		return "Default";
	}

	public String getID() {
		return "Default";
	}

	@Override
	public String getDescription() {
		return "Default theme for XDM";
	}

	@Override
	public boolean isNativeLookAndFeel() {
		return false;
	}

	@Override
	public boolean isSupportedLookAndFeel() {
		return true;
	}
}
