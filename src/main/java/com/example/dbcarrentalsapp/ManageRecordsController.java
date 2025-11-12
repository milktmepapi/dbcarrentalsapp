package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

public class ManageRecordsController {
    private final ManageRecordsView view;
    private final Stage stage;

    public ManageRecordsController(ManageRecordsView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    public void setupActions() {

        // Locations Button
        view.locations.setOnAction(e -> {
            LocationView locationView = new LocationView();
            LocationController locationController = new LocationController(locationView, stage);
            stage.setScene(locationView.getScene());
        });

        // Return Button
        view.returns.setOnAction(e -> {
            UserView userView = new UserView();
            UserController userController = new UserController(userView, stage);
            userController.setupActions();
            stage.setScene(userView.getScene());
        });

        // You can add others like:
        // view.departments.setOnAction(e -> { ... });
        // view.staffs.setOnAction(e -> { ... });
    }
}