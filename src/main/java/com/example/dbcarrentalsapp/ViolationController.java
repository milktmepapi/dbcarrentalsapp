package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.ViolationRecord;

import java.sql.SQLException;
import java.util.List;

/**
 * The ViolationController class handles the business logic for violation management.
 * It acts as the intermediary between the ViolationView (UI) and ViolationDAO (data access),
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
public class ViolationController {

    private final ViolationView view;
    private final Stage stage;
    private final ViolationDAO violationDAO;
    private ObservableList<ViolationRecord> masterList;

    /**
     * Constructor - initializes the controller with view and stage references.
     * Sets up the data access object and initializes the UI.
     *
     * @param view The ViolationView UI component
     * @param stage The primary application stage
     */
    public ViolationController(ViolationView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.violationDAO = new ViolationDAO();

        // Initialize data and setup UI interactions
        loadViolations();
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
        // Opens the add violation dialog
        view.addButton.setOnAction(e ->
                view.showAddViolationPopup(violationDAO, this::loadViolations)
        );

        // ===== MODIFY BUTTON ACTION =====
        // Opens the modify violation dialog for selected record
        view.modifyButton.setOnAction(e -> {
            ViolationRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a violation to modify.");
                return;
            }
            view.showModifyViolationPopup(violationDAO, selected, this::loadViolations);
        });

        // ===== FILTER AND SEARCH ACTIONS =====
        // Apply filter when filter button is clicked or search field is used
        view.filterButton.setOnAction(e -> applyFilter());
        view.searchField.setOnAction(e -> applyFilter());
    }

    /**
     * Loads all violation records from the database and populates the table.
     * This method is called during initialization and after data modifications.
     * Automatically sorts the table by Violation ID after loading.
     */
    public void loadViolations() {
        try {
            // Retrieve all violations from database
            List<ViolationRecord> violations = violationDAO.getAllViolations();
            // Convert to observable list for TableView binding
            masterList = FXCollections.observableArrayList(violations);
            view.tableView.setItems(masterList);

            // Sort the table by Violation ID after loading
            sortByViolationId();
        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Database Error", "Failed to load violations.");
        }
    }

    /**
     * Applies text-based filtering to the violation records in the table.
     * Filters records based on violation ID, rental ID, type, or reason.
     * Case-insensitive search across multiple fields.
     * Maintains sorting after filtering.
     */
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        // If search is empty, show all records
        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            // Reapply sorting when showing all records
            sortByViolationId();
            return;
        }

        // Create filtered list based on search criteria
        ObservableList<ViolationRecord> filteredList = masterList.filtered(record -> {
            // Check if any field contains the search text
            boolean matchId = record.getViolationId().toLowerCase().contains(filterText);
            boolean matchRentalId = record.getRentalId().toLowerCase().contains(filterText);
            boolean matchType = record.getViolationType().toLowerCase().contains(filterText);
            boolean matchReason = record.getReason().toLowerCase().contains(filterText);

            // Return true if any field matches
            return matchId || matchRentalId || matchType || matchReason;
        });

        // Update table with filtered results
        view.tableView.setItems(filteredList);
        // Reapply sorting to filtered results
        sortByViolationId();
    }

    /**
     * Sorts the table by Violation ID in ascending order.
     * This method can be called after any data modification to maintain consistent sorting.
     */
    private void sortByViolationId() {
        view.tableView.getSortOrder().clear();

        // Find the violation ID column
        for (javafx.scene.control.TableColumn<ViolationRecord, ?> column : view.tableView.getColumns()) {
            if ("Violation ID".equals(column.getText())) {
                view.tableView.getSortOrder().add(column);
                column.setSortType(javafx.scene.control.TableColumn.SortType.ASCENDING);
                break;
            }
        }

        view.tableView.sort();
    }

    /**
     * Enhanced method to refresh violations with automatic sorting.
     * Useful for external calls that need to refresh the data.
     */
    public void refreshViolations() {
        loadViolations();
    }

    /**
     * Alternative method that allows specifying sort order.
     *
     * @param ascending true for ascending order, false for descending
     */
    public void sortByViolationId(boolean ascending) {
        view.tableView.getSortOrder().clear();

        // Find the violation ID column
        for (javafx.scene.control.TableColumn<ViolationRecord, ?> column : view.tableView.getColumns()) {
            if ("Violation ID".equals(column.getText())) {
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