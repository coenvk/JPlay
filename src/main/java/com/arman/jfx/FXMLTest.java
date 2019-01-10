package com.arman.jfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.arman.jfx.util.KeepAwakeThread;

import java.io.IOException;

public class FXMLTest extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("RootLayout.fxml"));
            Parent layout = loader.load();
            FXMLController controller = loader.getController();
            controller.start(stage);
            Scene scene = new Scene(layout, 600, 400);
            stage.setScene(scene);
            stage.show();
            stage.setOnCloseRequest(e -> System.exit(0));
            new KeepAwakeThread().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
