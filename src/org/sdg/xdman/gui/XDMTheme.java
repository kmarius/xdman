package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIDefaults;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.OceanTheme;

public class XDMTheme extends OceanTheme {
	FontUIResource fontResource;

	public XDMTheme() {
		fontResource = new FontUIResource(new Font(Font.DIALOG, Font.PLAIN, 12));
	}

	@Override
	public FontUIResource getControlTextFont() {
		return fontResource;
	}

	@Override
	public FontUIResource getWindowTitleFont() {
		return fontResource;
	}

	@Override
	public FontUIResource getUserTextFont() {
		return fontResource;
	}

	@Override
	public FontUIResource getSystemTextFont() {
		return fontResource;
	}

	@Override
	public FontUIResource getSubTextFont() {
		return fontResource;
	}

	@Override
	public FontUIResource getMenuTextFont() {
		return fontResource;
	}

	Color gray = Color.GRAY, light_gray = Color.LIGHT_GRAY,
			lighter_gray = new Color(230, 230, 230);

	ColorUIResource pm1 = new ColorUIResource(Color.GRAY);
	ColorUIResource pm2 = new ColorUIResource(Color.WHITE);//gray);
	ColorUIResource pm3 = new ColorUIResource(lighter_gray);

	ColorUIResource sc1 = new ColorUIResource(Color.GRAY);// BORDER COLOR
	ColorUIResource sc2 = new ColorUIResource(lighter_gray);// BUTTON LOWER
	// GRADIENT
	ColorUIResource sc3 = new ColorUIResource(new Color(240, 240, 240));// lighter_gray);//

	// BACKGROUND
	// COLOR

	@Override
	protected ColorUIResource getPrimary1() {
		return pm1;
	}

	@Override
	protected ColorUIResource getPrimary2() {
		return pm2;
	}

	@Override
	protected ColorUIResource getPrimary3() {
		return pm3;
	}

	@Override
	protected ColorUIResource getSecondary1() {
		return sc1;
	}

	@Override
	protected ColorUIResource getSecondary2() {
		return sc2;
	}

	@Override
	protected ColorUIResource getSecondary3() {
		return sc3;
	}

	// @Override
	// protected ColorUIResource getPrimary1() {
	// return sc1;
	// }

	public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);
		// table.put("Button.gradient", Arrays.asList(new Object[] {
		// new Float(.3f), new Float(0f), new ColorUIResource(light_gray),// new
		// // ColorUIResource(0xDDE8F3),
		// new ColorUIResource(new Color(245,245,245)), getSecondary2() }));

		// Color cccccc = new ColorUIResource(0xCCCCCC);
		Color dadada = new ColorUIResource(0xDADADA);
		// Color c8ddf2 = new ColorUIResource(0xC8DDF2);

		List<Object> buttonGradient = Arrays.asList(new Object[] {
				new Float(1f), new Float(0f), getWhite(), dadada,
				new ColorUIResource(dadada) });
		// Arrays.asList(new Object[] {
		// new Float(.3f),
		// new Float(0f),
		// new ColorUIResource(new Color(230, 230, 230)),// new
		// // ColorUIResource(0xDDE8F3),
		// new ColorUIResource(new Color(235, 235, 235)),
		// new ColorUIResource(new Color(180, 180, 180)) });
		table.put("Button.gradient", buttonGradient);

		table.put("ScrollBar.gradient", buttonGradient);

		table.put("RadioButton.gradient", buttonGradient);
		table.put("RadioButtonMenuItem.gradient", buttonGradient);
		// table.put("ScrollBar.gradient", buttonGradient);

		//		
		// table.put("Button.gradient", Arrays.asList(new Object[] {
		// new Float(.3f), new Float(0f), new ColorUIResource(Color.black),//
		// new
		// // ColorUIResource(0xDDE8F3),
		// getWhite(), getSecondary2() }));
		// // System.out.println(table.get("Button.gradient"));
	}

}
