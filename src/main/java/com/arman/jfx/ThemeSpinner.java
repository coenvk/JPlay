package com.arman.jfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

public class ThemeSpinner extends Spinner<String> {

	private static final String[] THEMES = new String[]{"Dark"};
	
	private ObservableList<String> themes;
	
	public ThemeSpinner() {
		super();
		initialize();
	}
	
	private void initialize() {
		themes = FXCollections.observableArrayList(
				THEMES);
		SpinnerValueFactory<String> valueFactory = 
			new SpinnerValueFactory.ListSpinnerValueFactory<String>(themes) {
	            @Override
	            public void decrement(int steps) {
	                String curTheme = this.getValue();
	                int index = themes.indexOf(curTheme);
	                int newIndex = (themes.size() + index - steps) % themes.size();
	                String newTheme = themes.get(newIndex);
	                this.setValue(newTheme);
	            }
	
	            @Override
	            public void increment(int steps) {
	                String curTheme = this.getValue();
	                int index = themes.indexOf(curTheme);
	                int newIndex = (index + steps) % themes.size();
	                String newTheme = themes.get(newIndex);
	                this.setValue(newTheme);
	            }
			};
		valueFactory.setValue(themes.get(0));
		valueFactory.setWrapAround(true);
		setValueFactory(valueFactory);
	}
	
}
