package com.example.dbcarrentalsapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class ManageRecordsView {

    private Scene scene;
    public Button departments, locations, staffs, cars, jobs, branches, renters, returns;

    public ManageRecordsView() {
        // Initialize buttons
        departments = new Button("Departments");
        locations = new Button("Locations");
        staffs = new Button("Staffs");
        cars = new Button("Cars");
        jobs = new Button("Jobs");
        branches = new Button("Branches");
        renters = new Button("Renters");
        returns = new Button("Return");

        // Background
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/img2.png"));
        ImageView imageView = new ImageView(image);
        imageView.setMouseTransparent(true);
        stackPane.getChildren().add(imageView);

        // Style
        String buttonStyle = "custom-button";
        departments.getStyleClass().add(buttonStyle);
        locations.getStyleClass().add(buttonStyle);
        staffs.getStyleClass().add(buttonStyle);
        cars.getStyleClass().add(buttonStyle);
        jobs.getStyleClass().add(buttonStyle);
        branches.getStyleClass().add(buttonStyle);
        renters.getStyleClass().add(buttonStyle);
        returns.getStyleClass().add(buttonStyle);

        // Layout
        VBox leftBox = new VBox(20, departments, locations, staffs, cars);
        VBox rightBox = new VBox(20, jobs, branches, renters, returns);
        leftBox.setAlignment(Pos.CENTER);
        rightBox.setAlignment(Pos.CENTER);

        HBox root = new HBox(100, leftBox, rightBox);
        root.setAlignment(Pos.CENTER);

        stackPane.getChildren().add(root);

        // Scene setup
        scene = new Scene(stackPane, 815, 450);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}