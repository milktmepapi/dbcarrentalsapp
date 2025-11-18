package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.CancellationRecord;
import model.RentalRecord;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The CancellationController class handles the business logic for cancellation management.
 * It acts as the intermediary between the CancellationView (UI) and CancellationDAO (data access),
 * following the Controller layer in MVC architecture.
 *
 * Responsibilities:
 * - Initialize the view and setup event handlers
 * - Handle user actions from the UI
 * - Coordinate data operations with the DAO
 * - Manage data filtering and search functionality
 * - Provide user feedback and error handling
 *
 */
public class CancellationController {
    private final CancellationView view;
    private final Stage stage;
    private final CancellationDAO cancellationDAO;
    private ObservableList<CancellationRecord> masterList;

    /**
     * Constructor - initializes the controller with view and stage references.
     * Sets up the data access object and initializes the UI.
     *
     * @param view The Cancellation View UI component
     * @param stage The primary application stage
     */
    public CancellationController(CancellationView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.cancellationDAO = new CancellationDAO();

        // Link controller to view //
        this.view.setController(this);

        // Initialize data and setup UI interactions
        loadCancellations();
        setupActions();
    }

    /**
     * Sets up all event handlers for UI components.
     * This method connects user actions to the appropriate business logic methods.
     */
    private void setupActions() {
        // ===== RETURN BUTTON ACTION =====
        // Navigates back to the Manage Transactions screen
        view.returnButton.setOnAction(e -> {
            ManageTransactionsView manageView = new ManageTransactionsView(stage);
            new ManageTransactionsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // ===== ADD BUTTON ACTION =====
        // Opens the add cancellation dialog
        view.addButton.setOnAction(e ->
                view.showAddCancellationPopup(cancellationDAO, this::loadCancellations)
        );

        // ===== MODIFY BUTTON ACTION =====
        // Opens the modify cancellation dialog for selected record
        view.modifyButton.setOnAction(e -> {
            CancellationRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a cancellation to modify.");
                return;
            }
            view.showModifyCancellationPopup(cancellationDAO, selected, this::loadCancellations);
        });

        // ===== FILTER AND SEARCH ACTIONS =====
        // Apply filter when filter button is clicked or search field is used
        view.filterButton.setOnAction(e -> applyFilter());
        view.searchField.setOnAction(e -> applyFilter());
    }

    /**
     * Loads all cancellation records from the database and populates the table.
     * This method is called during initialization and after data modifications.
     * Automatically sorts the table by cancellation ID after loading.
     */
    public void loadCancellations() {
        try {
            // Retrieve all cancellations from database
            List<CancellationRecord> cancellations = cancellationDAO.getAllCancellations();
            // Convert to observable list for TableView binding
            masterList = FXCollections.observableArrayList(cancellations);
            view.tableView.setItems(masterList);

            // Sort the table by Violation ID after loading
            sortByCancellationId();
        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Database Error", "Failed to load cancellations.");
        }
    }

    /**
     * Applies text-based filtering to the cancellation records in the table.
     * Filters records based on cancellation ID, rental ID, type, or reason.
     * Case-insensitive search across multiple fields.
     * Maintains sorting after filtering.
     */
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        // If search is empty, show all records
        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            // Reapply sorting when showing all records
            sortByCancellationId();
            return;
        }

        // Create filtered list based on search criteria
        ObservableList<CancellationRecord> filteredList = masterList.filtered(record -> {
            // Check if any field contains the search text
            boolean matchId = record.getCancellationId().toLowerCase().contains(filterText);
            boolean matchRentalId = record.getCancellationRentalId().toLowerCase().contains(filterText);
            boolean matchReason = record.getReason().toLowerCase().contains(filterText);

            // Return true if any field matches
            return matchId || matchRentalId || matchReason;
        });

        // Update table with filtered results
        view.tableView.setItems(filteredList);
        // Reapply sorting to filtered results
        sortByCancellationId();
    }

    public void viewReceipt(CancellationRecord record) {
        try {
            RentalDAO rentalDAO = new RentalDAO();
            RentalRecord rental = rentalDAO.getRentalById(record.getCancellationRentalId());
            if (rental == null) { showPopup("Error", "Rental not found."); return; }

            Alert receipt = new Alert(Alert.AlertType.INFORMATION);
            receipt.setTitle("Cancellation Receipt");
            receipt.setHeaderText("Car Cancelled Successfully!");
            receipt.setContentText(
                    "Rental ID: " + rental.getRentalId() + "\n" +
                            "Renter DL: " + rental.getRenterDlNumber() + "\n" +
                            "Car Plate: " + rental.getCarPlateNumber() + "\n" +
                            "Cancellation Date & Time: " + LocalDateTime.now() + "\n"
            );
            receipt.showAndWait();
        } catch (SQLException ex) {
            ex.printStackTrace();
            showPopup("Error", "Failed to generate receipt.");
        }
    }

    private void showPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }

    public void loadTable() throws SQLException {
        List<CancellationRecord> list = cancellationDAO.getAllCancellations();
        view.refreshTable(list);
    }

    /**
     * Sorts the table by Cancellation ID in ascending order.
     * This method can be called after any data modification to maintain consistent sorting.
     */
    private void sortByCancellationId() {
        view.tableView.getSortOrder().clear();

        // Find the cancellation ID column
        for (javafx.scene.control.TableColumn<CancellationRecord, ?> column : view.tableView.getColumns()) {
            if ("Cancellation ID".equals(column.getText())) {
                view.tableView.getSortOrder().add(column);
                column.setSortType(javafx.scene.control.TableColumn.SortType.ASCENDING);
                break;
            }
        }

        view.tableView.sort();
    }

    /**
     * Enhanced method to refresh cancellations with automatic sorting.
     * Useful for external calls that need to refresh the data.
     */
    public void refreshCancellations() {
        loadCancellations();
    }

    /**
     * Alternative method that allows specifying sort order.
     *
     * @param ascending true for ascending order, false for descending
     */
    public void sortByCancellationId(boolean ascending) {
        view.tableView.getSortOrder().clear();

        // Find the cancellation ID column
        for (javafx.scene.control.TableColumn<CancellationRecord, ?> column : view.tableView.getColumns()) {
            if ("Cancellation ID".equals(column.getText())) {
                view.tableView.getSortOrder().add(column);
                column.setSortType(ascending ?
                        javafx.scene.control.TableColumn.SortType.ASCENDING :
                        javafx.scene.control.TableColumn.SortType.DESCENDING);
                break;
            }
        }

        view.tableView.sort();
    }
}