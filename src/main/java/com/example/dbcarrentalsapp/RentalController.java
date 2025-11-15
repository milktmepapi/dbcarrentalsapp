package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.RentalRecord;

import java.sql.SQLException;
import java.util.List;

public class RentalController {

    private final RentalView view;
    private final Stage stage;
    private final RentalDAO dao;
    private ObservableList<RentalRecord> masterList;

    public RentalController(RentalView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new RentalDAO();

        loadRentals();
        setupActions();
    }

    /** Sets up all button and UI actions **/
    private void setupActions() {

        // Return to Manage Records
        view.returnButton.setOnAction(e -> {
            ManageTransactionsView manageView = new ManageTransactionsView(stage);
            new ManageTransactionsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // Add Rental - placeholder popup hookup
        view.addButton.setOnAction(e -> {
            view.showAddRentalPopup(dao, this::loadRentals);
        });

        // Modify Rental - placeholder popup hookup
        view.modifyButton.setOnAction(e -> {
            RentalRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // TODO: view.showModifyRentalPopup(...)
            } else {
                view.showSuccessPopup("No Selection", "Please select a rental to modify.");
            }
        });

        // Delete Rental
        view.deleteButton.setOnAction(e -> {
            RentalRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a rental to delete.");
                return;
            }

            boolean confirmed = view.showConfirmPopup(selected);

            if (!confirmed) return;

            try {
                boolean success = dao.deleteRental(selected.getRentalId());

                if (success) {
                    view.showSuccessPopup("Deleted", "Rental deleted successfully!");
                    loadRentals();
                } else {
                    view.showSuccessPopup("Error", "Failed to delete rental. Ensure it’s inactive.");
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                view.showSuccessPopup("Database Error", "An error occurred while trying to delete this rental.");
            }
        });

        // Filter/Search
        view.filterButton.setOnAction(e -> applyFilter());
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Loads all rentals from database **/
    public void loadRentals() {
        try {
            List<RentalRecord> rentals = dao.getAllRentals();
            masterList = FXCollections.observableArrayList(rentals);
            view.tableView.setItems(masterList);
        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Database Error", "Failed to load rentals.");
        }
    }

    /** Applies text-based filtering **/
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();
        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        ObservableList<RentalRecord> filteredList = masterList.filtered(record ->
                record.getRentalId().toLowerCase().contains(filterText) ||
                        record.getRentalStatus().name().toLowerCase().contains(filterText)
        );

        view.tableView.setItems(filteredList);
    }
}