package com.arman.jfx;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class SkipButton extends Button {

	private static final String SKIP = "Skip";
	
	public SkipButton() {
		super();
		initialize();
	}
	
	public SkipButton(String name) {
		super(name);
		initialize();
	}
	
	private void initialize() {
		setId("skip-button");
		setFocusTraversable(false);
		setTooltip(new Tooltip(SKIP));
	}
	
}
