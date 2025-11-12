package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

public class LocationController {
    private final LocationView view;
    private final Stage stage;

    public LocationController(LocationView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    private void setupActions() {
        // Return to ManageRecords
        view.returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView();
            ManageRecordsController manageController = new ManageRecordsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // Placeholder actions
        view.addButton.setOnAction(e -> System.out.println("Add location clicked"));
        view.modifyButton.setOnAction(e -> System.out.println("Modify location clicked"));
        //view.viewButton.setOnAction();
        view.deleteButton.setOnAction(e -> System.out.println("Delete location clicked"));
    }
}
