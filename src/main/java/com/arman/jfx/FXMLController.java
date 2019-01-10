package com.arman.jfx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.arman.jfx.util.TimeFormatter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FXMLController {

    public static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".mp3", ".m4a");
    public static final int FILE_EXTENSION_LEN = 3;
    private static final String DIR = "C:/Users/Coen/Dropbox/myMusic/onPhone";

    @FXML
    Label curPlaying;
    @FXML
    ProgressBar progress = new SeekBar();
    @FXML
    Slider volume = new VolumeSlider();
    @FXML
    Label curDuration;
    @FXML
    Label timeLeft;
    @FXML
    SkipButton skip;
    @FXML
    PreviousButton prev;
    @FXML
    PlayButton play;
    @FXML
    RepeatButton repeat;
    @FXML
    MediaView mv;
    List<MediaPlayer> players;
    ChangeListener<Duration> progressChangeListener;

    public FXMLController() {

    }

    public void start(Stage stage) throws IOException {
        stage.setTitle("Audio Player");
        stage.setResizable(false);
        String dirName = null;
        final File dir = (dirName != null &&
                !dirName.trim().equals("")) ? new File(dirName) : new File(DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("Cannot find audio source directory: " + dir
                    + " please supply a directory as a command line argument");
            Platform.exit();
            return;
        }
        players = new ArrayList<MediaPlayer>();
        for (String file : dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                for (String ext : SUPPORTED_EXTENSIONS) {
                    if (name.endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            }
        })) {
            players.add(createPlayer((dir + "/" + file).replaceAll("//", "/")));
        }
        if (players.isEmpty()) {
            System.out.println("No audio found in " + dir);
            Platform.exit();
            return;
        }
        mv = new MediaView(players.get(0));
        for (int i = 0; i < players.size(); i++) {
            final MediaPlayer mp = players.get(i);
            final MediaPlayer nextMp = players.get((i + 1) % players.size());
            mp.setOnEndOfMedia(new Runnable() {
                public void run() {
                    mp.currentTimeProperty().removeListener(progressChangeListener);
                    volume.valueProperty().unbind();
                    mp.stop();
                    mv.setMediaPlayer(nextMp);
                    if (repeat.getText().equals("Don't Repeat")) {
                        repeat.setText("Repeat");
                        prev.fire();
                    } else {
                        nextMp.play();
                    }
                }
            });
        }
        mv.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
            public void changed(ObservableValue<? extends MediaPlayer>
                                        observableValue, MediaPlayer old, MediaPlayer next) {
                setPlayer(next);
            }
        });
        mv.setMediaPlayer(players.get(0));
        mv.getMediaPlayer().play();
        setPlayer(mv.getMediaPlayer());
    }

    @FXML
    public void play() {
        MediaPlayer mp = mv.getMediaPlayer();
        if ("Pause".equals(play.getText())) {
            mp.pause();
            play.setText("Play");
        } else {
            mp.play();
            play.setText("Pause");
        }
    }

    private void setPlayer(MediaPlayer nextMp) {
        nextMp.seek(Duration.ZERO);
        progress.setProgress(0);
        progressChangeListener = new ChangeListener<Duration>() {
            public void changed(ObservableValue<? extends Duration>
                                        observableValue, Duration old, Duration next) {
                progress.setProgress(1.0 * nextMp.getCurrentTime().toMillis()
                        / nextMp.getTotalDuration().toMillis());
                Duration dur = nextMp.getTotalDuration().subtract(nextMp.getCurrentTime());
                timeLeft.setText(TimeFormatter.format(Integer.toString((int) dur.toMinutes()) + ":"
                        + Integer.toString((int) dur.toSeconds() % 60)));
                curDuration.setText(TimeFormatter.format(Integer.toString((int)
                        nextMp.getCurrentTime().toMinutes())
                        + ":" + Integer.toString((int) nextMp.getCurrentTime().toSeconds() % 60)));
            }
        };
        nextMp.currentTimeProperty().addListener(progressChangeListener);
        String src = nextMp.getMedia().getSource();
        src = src.substring(0, src.length() - FILE_EXTENSION_LEN);
        src = src.substring(src.lastIndexOf("/") + 1).replaceAll("%20", " ");
        curPlaying.setText("Now Playing: " + src);
    }

    private MediaPlayer createPlayer(String mediaSrc) {
        final Media m = new Media(new File(mediaSrc).toURI().toString());
        final MediaPlayer mp = new MediaPlayer(m);
        mp.setOnError(new Runnable() {
            public void run() {
                System.out.println("Media error occurred: " + mp.getError());
            }
        });
        volume.valueProperty().bindBidirectional(mp.volumeProperty());
        volume.setValue(0.5);
        return mp;
    }

}
