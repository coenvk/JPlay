package com.arman.jfx;

import javafx.animation.*;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.*;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import com.arman.jfx.util.ImageViewer;
import javafx.scene.*;
import javafx.scene.effect.DropShadow;

public class SplashScreen {

	private static final int SPLASH_WIDTH = 800;
	private static final int SPLASH_HEIGHT = 500;
	private static final double SHADOW_RADIUS = 15.0;

	private Pane layout;
	private ImageView imageView;
	private Image image;
	private int width;
	private int height;
	private ProgressBar progressBar;
	
	public SplashScreen(Stage stage, Image img, Task task,
			CompletionHandler showAppMethod) {
		image = img;
		imageView = ImageViewer.grayscaled(image);
		width = SPLASH_WIDTH;
		height = SPLASH_HEIGHT;
		imageView.setFitWidth(width);
		imageView.setFitHeight(height);
		imageView.setPreserveRatio(true);
		imageView.setSmooth(true);
		progressBar = new ProgressBar();
		progressBar.setPrefWidth(image.getWidth());
		layout = new VBox();
		layout.getChildren().addAll(imageView, progressBar);
		layout.setBackground(Background.EMPTY);
		DropShadow shadow = new DropShadow(SHADOW_RADIUS, Color.BLACK);
		layout.setEffect(shadow);
		init(stage, task, showAppMethod);
	}
	
	private void init(Stage stage, Task<Boolean> task, CompletionHandler showAppHandler) {
		progressBar.progressProperty().bind(task.progressProperty());
		final Scene scene = new Scene(layout, width, height, Color.TRANSPARENT);
		final FadeTransition fadeIn = new FadeTransition(Duration.millis(800), layout);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.setOnFinished(e -> {
			final Task<Boolean> fadeTask = new Task<Boolean>() {
				@Override
				protected Boolean call() throws Exception {
					new Thread(task).start();
					return Boolean.TRUE;
				}
			};
			new Thread(fadeTask).start();
			showSplashScreen(stage, task, showAppHandler);
		});
		fadeIn.play();
		stage.setScene(scene);
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		stage.sizeToScene();
		stage.show();
	}
	
	private void showSplashScreen(Stage stage, Task<?> task, CompletionHandler showAppHandler) {
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				progressBar.progressProperty().unbind();
				progressBar.setProgress(1);
				stage.setAlwaysOnTop(true);
				final FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.2), layout);
				fadeOut.setFromValue(1.0);
				fadeOut.setToValue(0.0);
				fadeOut.play();
				fadeOut.setOnFinished(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						stage.hide();
						layout = null;
						imageView = null;
						stage.close();
					}
				});
				showAppHandler.complete();
			}
		});
	}
	
	public interface CompletionHandler {
		
		public void complete();
		
	}
	
}
