package com.arman.jfx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;

public class VolumeSlider extends Slider {
	
	public VolumeSlider() {
		super(0, 1, 0.5);
		initialize();
	}
	
	private void initialize() {
		setId("volume-slider");
		setFocusTraversable(false);
		setMaxWidth(300);
		setMinWidth(300);
		valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> 
					observable, Number oldValue, Number newValue) {
				setTooltip(new Tooltip("Volume: " + (int) (getValue() * 100) + "%"));
			}
		});
	}
	
}
