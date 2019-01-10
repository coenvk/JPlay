package com.arman.jfx;

import java.awt.Desktop;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import com.arman.jfx.util.IconConstants;
import com.arman.jfx.util.KeepAwakeThread;
import com.arman.jfx.util.TimeFormatter;

public class FXPlayList extends Application {

	private static final String DIR = "C:/Users/Coen/Dropbox/myMusic/onPhone";
	public static final List<String> SUPPORTED_EXTENSIONS = Arrays.asList(".mp3", ".m4a");
	public static final int FILE_EXTENSION_LEN = 3;
	private static final String GOOGLE = "http://www.google.com/search?q=";

	private MediaView mv = new MediaView();
	private List<MediaPlayer> players;
	
	private Label curPlaying = new Label();
	private final ProgressBar progress = new SeekBar();
	private final Slider volume = new VolumeSlider();
	private Label curDuration = new Label();
	private Label timeLeft = new Label();
	
	private final Button skip = new SkipButton(IconConstants.FORWARD_ICON);
	private final Button prev = new PreviousButton(IconConstants.BACKWARD_ICON);
	private final Button play = new PlayButton(IconConstants.PAUSE_ICON);
	private final Button repeat = new RepeatButton(IconConstants.REPEAT_ICON);
	private final Button google = new Button("Search Google");
	
	private ToolBar toolbar = new ToolBar();
	private BorderPane borderPane = new BorderPane();
	
	private Slider[] bands = new Slider[EqualizerPresets.AMOUNT_OF_BANDS];
	private int modeIndex = 0;
	
	private Stage mainStage;
	private String curTheme = "Dark";
	
	private final StackPane mainPane = createMainPane();
	private final StackPane equalizerPane = createEqualizerPane();
	private final Pane root = new Pane(borderPane);
	
	public Button getSkipButton() {
		return skip;
	}

	public Button getPreviousButton() {
		return prev;
	}

	public Button getPlayButton() {
		return play;
	}
	
	public Button getRepeatButton() {
		return repeat;
	}

	public MediaView getMediaView() {
		return mv;
	}

	public void setMediaView(MediaView view) {
		this.mv = view;
	}
	
	public ProgressBar getProgressBar() {
		return progress;
	}

	public List<MediaPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(List<MediaPlayer> players) {
		this.players = players;
	}

	public Slider getVolumeSlider() {
		return volume;
	}
	
	public ChangeListener<Duration> getProgressChangeListener() {
		return progressChangeListener;
	}

	public void setProgressChangeListener(ChangeListener<Duration> progressChangeListener) {
		this.progressChangeListener = progressChangeListener;
	}

	private ChangeListener<Duration> progressChangeListener;

	public static void main(String[] args) {
		launch(args);
	}

	public void start(Stage stage) {
		curDuration.setTooltip(new Tooltip("Time In"));
		timeLeft.setTooltip(new Tooltip("Time Left"));
		Thread keepAwake = new KeepAwakeThread();
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
		players = new ArrayList<>();
		for (String file : dir.list((dir1, name) -> {
			for (String ext : SUPPORTED_EXTENSIONS) {
				if (name.endsWith(ext)) {
					return true;
				}
			}
			return false;
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
			mp.setOnEndOfMedia(() -> {
				mp.currentTimeProperty().removeListener(progressChangeListener);
				volume.valueProperty().unbind();
				mp.stop();
				mv.setMediaPlayer(nextMp);
				equalize();
				if (repeat.getText().equals(IconConstants.REPEAT_ONCE_ICON)) {
					repeat.setText(IconConstants.REPEAT_ICON);
					prev.fire();
				} else {
					nextMp.play();
				}
			});
		}
		addActions();
		mv.mediaPlayerProperty().addListener((observableValue, old, next) -> setPlayer(next));
		mv.setMediaPlayer(players.get(0));
		equalize();
		mv.getMediaPlayer().play();
		setPlayer(mv.getMediaPlayer());
		keepAwake.start();
		createMenu();
		borderPane.setCenter(mainPane);
		Scene scene = new Scene(root, 600, 400);
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());
		scene.getStylesheets().setAll(getClass().getResource("darktheme.css").toExternalForm());
		stage.setScene(scene);
		stage.setOnCloseRequest(e -> System.exit(0));
		stage.show();
		mainStage = stage;
	}

	private void setPlayer(MediaPlayer nextMp) {
		nextMp.seek(Duration.ZERO);
		progress.setProgress(0);
		progressChangeListener = (observableValue, old, next) -> {
			progress.setProgress(1.0 * nextMp.getCurrentTime().toMillis()
				/ nextMp.getTotalDuration().toMillis());
			Duration dur = nextMp.getTotalDuration().subtract(nextMp.getCurrentTime());
			timeLeft.setText(TimeFormatter.format(Integer.toString((int) dur.toMinutes()) + ":"
				+ Integer.toString((int) dur.toSeconds() % 60)));
			curDuration.setText(TimeFormatter.format(Integer.toString((int)
				nextMp.getCurrentTime().toMinutes())
				+ ":" + Integer.toString((int) nextMp.getCurrentTime().toSeconds() % 60)));
		};
		nextMp.currentTimeProperty().addListener(progressChangeListener);
		String src = nextMp.getMedia().getSource();
		src = src.replaceAll("file:/", "");
		src = src.replaceAll("%20", " ");
		setCurrentlyPlaying(new Song(src).getMetaData());
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
		return mp;
	}
	
	public void searchGoogle() {
		google.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert dialog = new Alert(AlertType.INFORMATION);
		       	dialog.setTitle("Information Dialog");
		       	dialog.setHeaderText(null);
				dialog.getDialogPane().getStylesheets().setAll(getClass().getResource("dialog.css").toExternalForm());
				try {
					String[] split = curPlaying.getText().split("\n");
					String title = split[0].replaceAll("Title: ", "");
					String artist = split[1].replaceAll("Artist: ", "");
					String album = split[2].replaceAll("Album: ", "");
					title = title.replaceAll(" ", "+");
					artist = artist.replaceAll(" ", "+");
					album = album.replaceAll(" ", "+");
					if (!title.equals(Song.UNKNOWN)) {
						Desktop.getDesktop().browse(new URI(GOOGLE + title));
					} else if (!artist.equals(Song.UNKNOWN)) {
						Desktop.getDesktop().browse(new URI(GOOGLE + artist));
					} else if (!album.equals(Song.UNKNOWN)) {
						Desktop.getDesktop().browse(new URI(GOOGLE + album));
					} else {
				       	dialog.setContentText("This song doesn't have any metadata");
				       	dialog.show();
					}
				} catch (IOException | URISyntaxException e) {
					System.out.println("Error while trying to google");
				}
			}
		});
	}
	
	private void addActions() {
		skip.setOnAction(event -> {
			final MediaPlayer mp = mv.getMediaPlayer();
			final MediaPlayer nextMp = players.get((players.indexOf(mp) + 1) % players.size());
			mp.currentTimeProperty().removeListener(progressChangeListener);
			volume.valueProperty().unbind();
			mp.stop();
			mv.setMediaPlayer(nextMp);
			equalize();
			nextMp.play();
		});
		prev.setOnAction(event -> {
			final MediaPlayer mp = mv.getMediaPlayer();
			MediaPlayer prevMp = null;
			if (players.indexOf(mp) == 0) {
				prevMp = players.get(players.size() - 1);
			} else {
				prevMp = players.get(players.indexOf(mp) - 1);
			}
			mp.currentTimeProperty().removeListener(progressChangeListener);
			volume.valueProperty().unbind();
			mp.stop();
			mv.setMediaPlayer(prevMp);
			equalize();
			prevMp.play();
		});
		play.setOnAction(event -> ((PlayButton) play).togglePlay(mv.getMediaPlayer()));
		repeat.setOnAction(event -> ((RepeatButton) repeat).toggleRepeat());
	}
	
	private StackPane createMainPane() {
		final StackPane layout = new StackPane();
		final VBox content = new VBox(10);
		final HBox buttons = new HBox(10);
		buttons.setAlignment(Pos.CENTER);
		google.setFocusTraversable(false);
		searchGoogle();
		buttons.getChildren().setAll(repeat, prev, play, skip, mv, google);
		final HBox progressReport = new HBox(10);
		progressReport.setAlignment(Pos.CENTER);
		progressReport.getChildren().setAll(curDuration, progress, timeLeft);
		content.getChildren().addAll(curPlaying, buttons, progressReport);
		layout.getChildren().addAll(content, volume);
		HBox.setHgrow(progress, Priority.ALWAYS);
		return layout;
	}
	
	private StackPane createEqualizerPane() {
		final StackPane layout = new StackPane();
		final HBox box = new HBox(10);
		final GridPane sliderPane = new GridPane();
		Label[] labels = new Label[bands.length];
		Label[] labels2 = new Label[bands.length];
		for (int i = 0; i < bands.length; i++) {
			bands[i] = new Slider(-12, 12, 0);
			bands[i].setOrientation(Orientation.VERTICAL);
			labels[i] = new Label("100");
			labels2[i] = new Label("0");
			sliderPane.add(labels[i], i + 1, 0);
			sliderPane.add(bands[i], i + 1, 1);
			sliderPane.add(labels2[i], i + 1, 2);
		}
		Label modeName = new Label("Normal");
		Button nextMode = new Button(IconConstants.RIGHT_HEAVY_ARROW);
		Button previousMode = new Button(IconConstants.LEFT_HEAVY_ARROW);
		nextMode.setFocusTraversable(false);
		previousMode.setFocusTraversable(false);
		nextMode.setOnAction(event -> {
			EqualizerPresets.next(++modeIndex, bands, mv.getMediaPlayer(), modeName);
			modeIndex %= EqualizerPresets.AMOUNT_OF_STYLES;
		});
		previousMode.setOnAction(event -> {
			EqualizerPresets.previous(--modeIndex, bands, mv.getMediaPlayer(), modeName);
			modeIndex = (modeIndex < 0) ? (EqualizerPresets.AMOUNT_OF_STYLES - 1) : modeIndex;
		});
		final VBox modeBox = new VBox(10);
		final HBox modeButtons = new HBox(10);
		modeButtons.getChildren().addAll(previousMode, nextMode);
		modeBox.getChildren().addAll(modeName, modeButtons);
		box.getChildren().addAll(sliderPane, modeBox);
		layout.getChildren().add(box);
		return layout;
	}
	
	private void createMenu() {
		Button main = new Button();
		main.setFocusTraversable(false);
		main.setOnAction(event -> {
			if (!borderPane.getCenter().equals(mainPane)) {
				FadeTransition startFt = new FadeTransition();
				startFt.setNode(equalizerPane);
				startFt.setDuration(new Duration(500));
				startFt.setFromValue(1.0);
				startFt.setToValue(0.0);
				startFt.setCycleCount(1);
				FadeTransition endFt = new FadeTransition();
				endFt.setNode(mainPane);
				endFt.setDuration(new Duration(500));
				endFt.setFromValue(0.0);
				endFt.setToValue(1.0);
				endFt.setCycleCount(1);
				startFt.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						borderPane.setCenter(mainPane);
						endFt.play();
					}
				});
				startFt.play();
			}
		});
		main.setText("Main");
		Button equalizer = new Button();
		equalizer.setFocusTraversable(false);
		equalizer.setOnAction(event -> {
			if (!borderPane.getCenter().equals(equalizerPane)) {
				FadeTransition startFt = new FadeTransition();
				startFt.setNode(mainPane);
				startFt.setDuration(new Duration(500));
				startFt.setFromValue(1.0);
				startFt.setToValue(0.0);
				startFt.setCycleCount(1);
				FadeTransition endFt = new FadeTransition();
				endFt.setNode(equalizerPane);
				endFt.setDuration(new Duration(500));
				endFt.setFromValue(0.0);
				endFt.setToValue(1.0);
				endFt.setCycleCount(1);
				startFt.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						borderPane.setCenter(equalizerPane);
						endFt.play();
					}
				});
				startFt.play();
			}
		});
		Button themes = new Button("Theme");
		themes.setFocusTraversable(false);
		themes.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Alert themes = new Alert(AlertType.CONFIRMATION);
				themes.setTitle("Theme");
				themes.setHeaderText("Change the theme: ");
				ThemeSpinner spinner = new ThemeSpinner();
				spinner.getValueFactory().setValue(curTheme);
				spinner.valueProperty().addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> 
							observable, String oldValue, String newValue) {
						final Scene scene = mainStage.getScene();
						curTheme = newValue;
						scene.getStylesheets().setAll(getClass().getResource(curTheme.toLowerCase()
							+ "theme.css").toExternalForm());
					}
				});
				themes.getDialogPane().getStylesheets().setAll(getClass().getResource("dialog.css").toExternalForm());
				themes.setGraphic(spinner);
				themes.show();
			}
		});
		equalizer.setText("Equalizer");
		toolbar.getItems().addAll(main, equalizer, themes);
		borderPane.setTop(toolbar);
	}
	
	private void equalize() {
		for (int i = 0; i < bands.length; i++) {
			double gain = bands[i].getValue();
			mv.getMediaPlayer().getAudioEqualizer().getBands().get(i).setGain(gain);
		}
		for (int i = 0; i < bands.length; i++) {
			int x = i;
			bands[x].valueProperty().addListener((observable, oldValue, newValue) -> {
				double nextGain = newValue.doubleValue();
				mv.getMediaPlayer().getAudioEqualizer().getBands().get(x).setGain(nextGain);
			});
		}
	}
	
	public void setCurrentlyPlaying(String[] metadata) {
		curPlaying.setText(
				"Title: " + metadata[0] 
			+ "\nArtist: " + metadata[1] 
			+ "\nAlbum: " + metadata[2]);
	}

}
