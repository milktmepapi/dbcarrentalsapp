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
    private final RentalDAO rentalDAO;
    private final RenterDAO renterDAO;
    private ObservableList<RentalRecord> masterList;

    public RentalController(RentalView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.rentalDAO = new RentalDAO();
        this.renterDAO = new RenterDAO();

        loadRentals();
        setupActions();
    }

    /** Sets up all button and UI actions **/
    private void setupActions() {

        // Return to Manage Transactions
        view.returnButton.setOnAction(e -> {
            ManageTransactionsView manageView = new ManageTransactionsView(stage);
            new ManageTransactionsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // Add Rental
        view.addButton.setOnAction(e ->
                view.showAddRentalPopup(rentalDAO, renterDAO, this::loadRentals)
        );

        // Modify Rental — only permitted fields handled in popup
        view.modifyButton.setOnAction(e -> {
            RentalRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a rental to modify.");
                return;
            }
            view.showModifyRentalPopup(rentalDAO, selected, this::loadRentals);
        });

        // Filter & Search
        view.filterButton.setOnAction(e -> applyFilter());
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Loads all rentals from the database **/
    public void loadRentals() {
        try {
            List<RentalRecord> rentals = rentalDAO.getAllRentals();
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

        ObservableList<RentalRecord> filteredList = masterList.filtered(record -> {
            boolean matchId = record.getRentalId().toLowerCase().contains(filterText);
            boolean matchStatus = record.getRentalStatus().name().toLowerCase().contains(filterText);

            boolean matchDate = record.getRentalDateTime() != null &&
                    record.getRentalDateTime().toString().toLowerCase().contains(filterText);

            return matchId || matchStatus || matchDate;
        });

        view.tableView.setItems(filteredList);
    }
}