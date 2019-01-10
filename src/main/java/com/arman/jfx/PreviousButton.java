package com.arman.jfx;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

public class PreviousButton extends Button {

	private static final String PREVIOUS = "Previous";
	
	public PreviousButton() {
		super();
		initialize();
	}
	
	public PreviousButton(String name) {
		super(name);
		initialize();
	}
	
	private void initialize() {
		setId("previous-button");
		setFocusTraversable(false);
		setTooltip(new Tooltip(PREVIOUS));
	}
	
}
