package com.arman.jfx;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.media.MediaPlayer;
import com.arman.jfx.util.IconConstants;

public class PlayButton extends Button {

    private static final String PLAY = "Play";
    private static final String PAUSE = "Pause";

    public PlayButton() {
        super();
        initialize();
    }

    public PlayButton(String name) {
        super(name);
        initialize();
    }

    private void initialize() {
        setId("play-button");
        setFocusTraversable(false);
        setMaxWidth(63);
        setMinWidth(63);
        setTooltip(new Tooltip(PAUSE));
    }

    public void togglePlay(MediaPlayer mp) {
        if (getTooltip().getText().equals(PLAY)) {
            setTooltip(new Tooltip(PAUSE));
        } else {
            setTooltip(new Tooltip(PLAY));
        }
        if (getText().equals(IconConstants.PAUSE_ICON)) {
            setText(IconConstants.PLAY_ICON);
            mp.pause();
        } else if (getText().equals(IconConstants.PLAY_ICON)) {
            setText(IconConstants.PAUSE_ICON);
            mp.play();
        }
    }

}
