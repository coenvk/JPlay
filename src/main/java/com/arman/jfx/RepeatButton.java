package com.arman.jfx;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import com.arman.jfx.util.IconConstants;

public class RepeatButton extends Button {

    private static final String REPEAT_ZERO = "Don't Repeat";
    private static final String REPEAT_ONCE = "Repeat Once";

    private String repeat;

    public RepeatButton() {
        super();
        initialize();
    }

    public RepeatButton(String name) {
        super(name);
        initialize();
    }

    private void initialize() {
        setId("repeat-button");
        setFocusTraversable(false);
        setMaxWidth(63);
        setMinWidth(63);
        setTooltip(new Tooltip(REPEAT_ONCE));
    }

    public void toggleRepeat() {
        if (getTooltip().getText().equals(REPEAT_ZERO)) {
		    setTooltip(new Tooltip(REPEAT_ONCE));
        } else {
		    setTooltip(new Tooltip(REPEAT_ZERO));
        }
        if (getText().equals(IconConstants.REPEAT_ICON)) {
            setText(IconConstants.REPEAT_ONCE_ICON);
        } else {
            setText(IconConstants.REPEAT_ICON);
        }
    }

}
