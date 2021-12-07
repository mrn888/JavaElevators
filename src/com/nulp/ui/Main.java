package com.nulp.ui;

import com.nulp.logic.utils.MyLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {
    private static final Logger LOGGER = MyLogger.getLOGGER();


    @Override
    public void start(Stage primaryStage) throws Exception{
        LOGGER.info("Start application");
        Parent root = FXMLLoader.load(Objects.requireNonNull
                (getClass().getResource("mainConfig.fxml")));
        primaryStage.setTitle("ElevatorsConfigsWindow");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
