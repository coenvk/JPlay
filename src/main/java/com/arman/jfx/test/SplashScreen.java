package com.arman.jfx.test;

import javafx.application.*;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.concurrent.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.stage.*;
import javafx.util.*;

public class SplashScreen extends Application {

	public static final String APPLICATION_ICON = "file:C:/Users/Coen/Documents/splash.jpeg";
	public static final String SPLASH_IMAGE = "file:C:/Users/Coen/Documents/splash.jpeg";
	
	private Pane splashLayout;
	private ProgressBar progress;
	private Stage mainStage;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void init() {
		Image splashImage = new Image(SPLASH_IMAGE);
		ImageView splash = new ImageView(splashImage);
		progress = new ProgressBar();
		progress.setPrefWidth(splashImage.getWidth());
		splashLayout = new VBox();
		splashLayout.getChildren().addAll(splash, progress);
		splashLayout.setEffect(new DropShadow());
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		final Task<ObservableList<String>> task = new Task<ObservableList<String>>() {
			@Override
			protected ObservableList<String> call() throws Exception {
				ObservableList<String> foundFriends = FXCollections.<String>observableArrayList();
				ObservableList<String> availableFriends = FXCollections.observableArrayList(
					"Fili", "Kili", "Oin", "Gloin", "Thorin", "Dwalin", "Balin", "Bifur",
					"Bofur", "Bombur", "Dori", "Nori", "Ori");
				updateMessage("Finding friends...");
				for (int i = 0; i < availableFriends.size(); i++) {
					Thread.sleep(400);
					updateProgress(i + 1, availableFriends.size());
					String nextFriend = availableFriends.get(i);
					foundFriends.add(nextFriend);
				}
				Thread.sleep(400);
				return foundFriends;
			}
		};
		showSplash(primaryStage, task, () -> showMainStage(task.valueProperty()));
		new Thread(task).start();
	}
	
	private void showMainStage(ReadOnlyObjectProperty<ObservableList<String>> friends) {
		mainStage = new Stage(StageStyle.DECORATED);
		mainStage.setTitle("My Friends");
		mainStage.getIcons().add(new Image(APPLICATION_ICON));
		final ListView<String> peopleView = new ListView<String>();
		peopleView.itemsProperty().bind(friends);
		mainStage.setScene(new Scene(peopleView));
		mainStage.show();
	}
	
	private void showSplash(final Stage stage, Task<?> task, CompletionHandler handler) {
		progress.progressProperty().bind(task.progressProperty());
		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				progress.progressProperty().unbind();
				progress.setProgress(1);
				stage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> stage.hide());
				fadeSplash.play();
				handler.complete();
			}
		});
		Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
		stage.setScene(splashScene);
		stage.sizeToScene();
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.setAlwaysOnTop(true);
		stage.show();
	}
	
	public interface CompletionHandler {
		public void complete();
	}

}
