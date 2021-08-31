package com.github.maximumcactus.todo;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TodoApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        // -- Splash Screen --

        FXMLLoader splashLoader = new FXMLLoader(TodoApplication.class.getResource("splash.fxml"));
        Scene splashScene = new Scene(splashLoader.load(),600,337.5);
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setTitle("Todo");
        splashStage.setScene(splashScene);
        //splashStage.setAlwaysOnTop(true);
        splashStage.show();

        // ------

        // -- Home Screen --
        /*
        System.out.println(System.getProperty("user.dir"));
        FXMLLoader fxmlLoader = new FXMLLoader(TodoApplication.class.getResource("todo-list.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 600, 400);
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Todo");
        stage.setScene(scene);
        stage.show();
         */
        // ------

        // -- Splash Screen Waiting Time --
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                splashStage.close();
                FXMLLoader fxmlLoader = new FXMLLoader(TodoApplication.class.getResource("todo-list.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load(), 600, 400);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stage.setTitle("Todo");
                stage.setScene(scene);
                stage.show();
            }
        });
        delay.play();
        // ----

    }

    public static void main(String[] args) {
        launch(args);
    }
}
