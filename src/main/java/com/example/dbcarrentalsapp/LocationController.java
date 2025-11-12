package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.LocationRecord;

import java.util.List;

public class LocationController {

    private final LocationView view;
    private final Stage stage;
    private final LocationDAO dao;
    private ObservableList<LocationRecord> masterList;

    public LocationController(LocationView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new LocationDAO();

        setupActions();
        loadLocations();
    }

    /** Sets up all button and UI actions **/
    private void setupActions() {

        // ===== Return to Manage Records =====
        view.returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView();
            new ManageRecordsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // ===== Add Location =====
        view.addButton.setOnAction(e ->
                view.showAddLocationPopup(dao, this::loadLocations)
        );

        // ===== Modify Location =====
        view.modifyButton.setOnAction(e -> {
            LocationRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                view.showModifyLocationPopup(dao, selected, this::loadLocations);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a location to modify.");
            }
        });

        // ===== Delete Location =====
        view.deleteButton.setOnAction(e -> {
            LocationRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a location to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this location?\n\n"
                    + selected.getLocationId() + " — "
                    + selected.getLocationCity() + ", " + selected.getLocationProvince());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = dao.deleteLocation(selected.getLocationId());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", "Location deleted successfully.");
                        loadLocations(); // refresh table
                        System.out.println("Deleted Location ID: " + selected.getLocationId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete location.");
                    }
                }
            });
        });

        // ===== Filter/Search =====
        view.filterButton.setOnAction(e -> applyFilter());

        // Optional: Press Enter in search field to filter
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Loads all locations from database **/
    public void loadLocations() {
        List<LocationRecord> locations = dao.getAllLocations();
        if (locations == null) locations = List.of(); // avoid NPE
        masterList = FXCollections.observableArrayList(locations);
        view.tableView.setItems(masterList);
    }

    /** Applies text-based filtering **/
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        ObservableList<LocationRecord> filteredList = masterList.filtered(record ->
                record.getLocationCity().toLowerCase().contains(filterText) ||
                        record.getLocationProvince().toLowerCase().contains(filterText)
        );

        view.tableView.setItems(filteredList);
    }

    /** Utility method for showing alerts **/
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}