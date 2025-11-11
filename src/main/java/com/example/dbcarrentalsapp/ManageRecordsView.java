package com.example.dbcarrentalsapp;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ManageRecordsView {

    private Scene scene;
    public Button departments;
    public Button locations;
    public Button staffs;
    public Button cars;
    public Button jobs;
    public Button branches;
    public Button renters;
    public Button returns;

    public ManageRecordsView(Stage stage) {

        // Initialize buttons
        departments = new Button("Departments");
        locations = new Button("Locations");
        staffs = new Button("Staffs");
        cars = new Button("Cars");
        jobs = new Button("Jobs");
        branches = new Button("Branches");
        renters = new Button("Renters");
        returns = new Button("Return");

        // Root layer
        StackPane stackPane = new StackPane();

        // ✅ Corrected image path
        Image image = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/img2.png"));
        ImageView imageView = new ImageView(image);
        stackPane.getChildren().add(imageView);

        // Style classes
        String buttonStyle = "custom-button";
        departments.getStyleClass().add(buttonStyle);
        locations.getStyleClass().add(buttonStyle);
        staffs.getStyleClass().add(buttonStyle);
        cars.getStyleClass().add(buttonStyle);
        jobs.getStyleClass().add(buttonStyle);
        branches.getStyleClass().add(buttonStyle);
        renters.getStyleClass().add(buttonStyle);
        returns.getStyleClass().add(buttonStyle);

        // Left side (4 buttons)
        VBox leftBox = new VBox(20);
        leftBox.setAlignment(Pos.CENTER);
        leftBox.setStyle("-fx-background-color: transparent");
        leftBox.getChildren().addAll(departments, locations, staffs, cars);

        // Right side (4 buttons)
        VBox rightBox = new VBox(20);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setStyle("-fx-background-color: transparent");
        rightBox.getChildren().addAll(jobs, branches, renters, returns);

        // Combine both sides
        HBox root = new HBox(50);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(leftBox, rightBox);

        stackPane.getChildren().add(root);

        // Create scene
        scene = new Scene(stackPane, 815, 450);

        // Corrected stylesheet path
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}
