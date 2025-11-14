package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
                view.showSuccessPopup("No Selection", "Please select a location to modify.");
            }
        });

        // ===== Delete Location =====
        view.deleteButton.setOnAction(e -> {
            LocationRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a location to delete.");
                return;
            }

            // Show confirmation popup with record details
            boolean confirmed = view.showConfirmPopup(selected);

            if (confirmed) {
                boolean success = dao.deleteLocation(selected.getLocationId());
                if (success) {
                    view.showSuccessPopup("Deleted", "Location deleted successfully!");
                    loadLocations();
                    System.out.println("Deleted Location ID: " + selected.getLocationId());
                } else {
                    view.showSuccessPopup("Error", "Failed to delete location.");
                }
            }
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
        String og = view.searchField.getText().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        ObservableList<LocationRecord> filteredList = masterList.filtered(record ->
                record.getLocationCity().toLowerCase().contains(og) ||
                        record.getLocationProvince().toLowerCase().contains(filterText) ||
                        record.getLocationId().contains(og)
        );

        view.tableView.setItems(filteredList);
    }
}