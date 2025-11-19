package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class MainApp extends Application {

    public static void main(String[] args) throws ClassNotFoundException {
        RentalDAO rentalDAO = new RentalDAO();
        RentalScheduler scheduler = new RentalScheduler(rentalDAO);
        scheduler.start();

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        UserView view = new UserView();
        UserController controller = new UserController(view, stage);
        Scene welcomeScene = view.getScene();
        controller.setupActions();

        stage.setTitle("Forza Rentals");
        Image icon = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/CCINFOM APPLICATION LOGO.png")
        );

        stage.getIcons().add(icon);
        stage.setScene(welcomeScene);
        stage.show();
    }
}