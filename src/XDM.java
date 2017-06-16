
import java.util.Locale;

import org.sdg.xdman.gui.XDMMainWindow;

public class XDM {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// enable anti-aliased text:
		Locale.setDefault(Locale.ENGLISH);
		System.setProperty("awt.useSystemAAFontSettings", "lcd");
		System.setProperty("swing.aatext", "true");
		System.setProperty("sun.java2d.xrender", "false");
		XDMMainWindow.main(args);
	}

}
