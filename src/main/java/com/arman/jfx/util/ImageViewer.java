package com.arman.jfx.util;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewer {

	public static ImageView grayscaled(Image img) {
		ImageView view = new ImageView(img);
		ColorAdjust desaturate = new ColorAdjust();
		desaturate.setSaturation(-1);
		desaturate.setBrightness(-0.5);
		view.setEffect(desaturate);
		return view;
	}
	
}
