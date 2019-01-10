package com.arman.jfx;

import javafx.event.EventHandler;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import com.arman.jfx.util.TimeFormatter;

public class SeekBar extends ProgressBar {
	
	public SeekBar() {
		super();
		initialize();
	}
	
	private void initialize() {
		setId("seek-bar");
		setMinWidth(450);
		setMaxWidth(450);
		setTooltip(new Tooltip("Progress"));
//		setOnMouseEntered(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				showProgressAtPointer();
//			}
//		});
	}

	public void showProgressAtPointer() {
		setTooltip(new Tooltip(getProgressAtPointer(300000, 100)));
	}

	public String getProgressAtPointer(int totalTime, float px) {
		float percentage = (float) (px / getWidth());
		float pointerTime = (totalTime * percentage);
		int seconds = (int) ((pointerTime / (float) 1000.0) % 60);
		int minutes = (int) ((pointerTime / (float) (1000.0 * 60)) % 60);
		String formattedTime = TimeFormatter.format(minutes + ":" + seconds);
		return formattedTime;
	}
	
}
