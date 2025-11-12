package com.example.dbcarrentalsapp;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UserController {
    private UserView view;
    private Stage stage;

    public UserController(UserView view, Stage stage) {
        this.view = view;
        this.stage = stage;
    }

    public void setupActions() {
        view.records.setOnAction(e -> openRecords());
        view.transactions.setOnAction(e -> openTransactions());
        view.reports.setOnAction(e -> openReports());
        view.exit.setOnAction(e -> System.exit(0));
    }

    private void openRecords() {
        animateTransition(() -> {
            ManageRecordsView manageView = new ManageRecordsView();
            ManageRecordsController manageController = new ManageRecordsController(manageView, stage);
            return manageView.getScene().getRoot();
        });
    }

    private void openTransactions() {
        animateTransition(() -> {
            ManageTransactionsView manageTransView = new ManageTransactionsView(stage);
            ManageTransactionsController manageTransController = new ManageTransactionsController(manageTransView, stage);
            return manageTransView.getScene().getRoot();
        });
    }

    private void openReports() {
        animateTransition(() -> {
            ManageReportsView reportsView = new ManageReportsView(stage);
            ManageReportsController reportsController = new ManageReportsController(reportsView, stage);
            return reportsView.getScene().getRoot();
        });
    }

    private void animateTransition(SceneProvider sceneProvider) {
        // Get the entire scene root to zoom the whole UserView
        Node sceneRoot = view.records.getScene().getRoot();

        // Manual positioning - adjust these values to target the tablet screen
        double tabletScreenCenterX = 640;  // Horizontal position - adjust left/right
        double tabletScreenCenterY = 620;  // Vertical position - adjust up/down

        // Get scene dimensions
        double sceneWidth = sceneRoot.getScene().getWidth();
        double sceneHeight = sceneRoot.getScene().getHeight();

        // Calculate translation to center on the tablet screen area
        double targetTranslateX = (sceneWidth / 2) - tabletScreenCenterX;
        double targetTranslateY = (sceneHeight / 2) - tabletScreenCenterY;

        // Create zoom animation
        ScaleTransition zoom = new ScaleTransition(Duration.millis(600), sceneRoot);
        zoom.setFromX(1.0);
        zoom.setFromY(1.0);
        zoom.setToX(2.7);  // Strong zoom to focus on tablet screen
        zoom.setToY(2.7);

        // Create translation to focus on the tablet screen area
        TranslateTransition translate = new TranslateTransition(Duration.millis(600), sceneRoot);
        translate.setToX(targetTranslateX);
        translate.setToY(targetTranslateY);

        // Create pause to stay still for 1 second
        PauseTransition pause = new PauseTransition(Duration.millis(250));

        // Create sequential transition: zoom -> pause -> switch view
        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(
                new ParallelTransition(zoom, translate), // Zoom and translate together
                pause // Stay at the zoomed position for 1 second
        );

        sequence.setOnFinished(e -> {
            // Create fade out transition for current scene
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), sceneRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(fadeEvent -> {
                // Get the new scene
                Node newSceneRoot = sceneProvider.getNewSceneRoot();
                Scene newScene = newSceneRoot.getScene();

                // Reset transformations on old scene root before switching
                sceneRoot.setScaleX(1.0);
                sceneRoot.setScaleY(1.0);
                sceneRoot.setTranslateX(0);
                sceneRoot.setTranslateY(0);
                sceneRoot.setOpacity(1.0);

                // Set the new scene on the stage
                stage.setScene(newScene);

                // Create fade in for the new scene
                newSceneRoot.setOpacity(0.0); // Start invisible
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), newSceneRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        });

        sequence.play();
    }

    @FunctionalInterface
    private interface SceneProvider {
        Node getNewSceneRoot();
    }
}