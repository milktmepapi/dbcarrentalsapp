package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        UserView view = new UserView();
        UserController controller = new UserController(view, stage);
        Scene welcomeScene = view.getScene();
        controller.setupActions();

        stage.setTitle("Forza Rentals");
        stage.setScene(welcomeScene);
        stage.show();
    }
}