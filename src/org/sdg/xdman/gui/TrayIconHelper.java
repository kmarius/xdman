package org.sdg.xdman.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class TrayIconHelper {
	public static Image getImage(int width, int height, ImageIcon img) {
		System.out.println("width: "+width+" height: "+height);
		return img.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
	}
}
