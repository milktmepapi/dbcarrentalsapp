package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.ViolationRecord;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller for managing violation records and automated violation processing
 * Handles user interactions, data processing, and coordination between view and data layers
 */
public class ViolationController {

    private final ViolationView view;
    private final Stage stage;
    private final ViolationDAO violationDAO;
    private ObservableList<ViolationRecord> masterList;

    public ViolationController(ViolationView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.violationDAO = new ViolationDAO();

        loadViolations();
        setupActions();
        checkForOverdueRentals();
    }

    private void setupActions() {
        // Navigation and basic CRUD actions
        view.returnButton.setOnAction(e -> {
            ManageTransactionsView manageView = new ManageTransactionsView(stage);
            new ManageTransactionsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        view.addButton.setOnAction(e ->
                view.showAddViolationPopup(violationDAO, this::loadViolations)
        );

        view.modifyButton.setOnAction(e -> {
            ViolationRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a violation to modify.");
                return;
            }
            view.showModifyViolationPopup(violationDAO, selected, this::loadViolations);
        });

        view.filterButton.setOnAction(e -> applyFilter());
        view.searchField.setOnAction(e -> applyFilter());

        // Automated processing actions
        view.processReturnButton.setOnAction(e ->
                view.showProcessReturnPopup(violationDAO, this::loadViolations)
        );

        view.generateReceiptButton.setOnAction(e -> {
            ViolationRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a violation to generate receipt.");
                return;
            }
            showRentalReceipt(selected.getRentalId());
        });

        view.checkOverdueButton.setOnAction(e -> checkForOverdueRentals());
    }

    /**
     * Processes car return with automatic violation detection
     * Updates car status, rental status, and creates late return violations if applicable
     */
    public void processCarReturn(String rentalId, String staffId) {
        try {
            // Process return and get any newly created late violation
            ViolationRecord lateViolation = violationDAO.processCarReturn(rentalId, staffId);

            // Get all violations for this rental to display comprehensive summary
            List<ViolationRecord> allViolations = violationDAO.getViolationsByRentalId(rentalId);

            if (!allViolations.isEmpty()) {
                StringBuilder violationMessage = new StringBuilder();
                violationMessage.append("Car returned successfully!\n\n");
                violationMessage.append("ALL VIOLATIONS DETECTED:\n\n");

                double totalPenalties = 0.0;

                for (ViolationRecord violation : allViolations) {
                    violationMessage.append(String.format("• %s:\n", violation.getViolationType()));
                    violationMessage.append(String.format("  Violation ID: %s\n", violation.getViolationId()));
                    violationMessage.append(String.format("  Reason: %s\n", violation.getReason()));
                    if (violation.getDurationHours() > 0) {
                        violationMessage.append(String.format("  Duration: %d hours\n", violation.getDurationHours()));
                    }
                    violationMessage.append(String.format("  Penalty: ₱%.2f\n\n", violation.getPenaltyFee()));

                    totalPenalties += violation.getPenaltyFee();
                }

                violationMessage.append(String.format("TOTAL PENALTIES: ₱%.2f", totalPenalties));

                // Generate receipt including all violations
                String receipt = violationDAO.generateRentalReceipt(rentalId);
                view.showSuccessPopup("Return Processed with Violations",
                        violationMessage.toString() + "\n\nFull receipt has been generated.");

            } else {
                view.showSuccessPopup("Return Processed",
                        "Car returned successfully and marked as available.\nNo violations detected.");
            }

            loadViolations();

        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Error", "Failed to process car return: " + e.getMessage());
        }
    }

    /**
     * Generates and displays rental receipt for the specified rental ID
     */
    public void showRentalReceipt(String rentalId) {
        try {
            String receipt = violationDAO.generateRentalReceipt(rentalId);
            view.showReceiptPopup("Rental Receipt - " + rentalId, receipt);
        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Error", "Failed to generate receipt: " + e.getMessage());
        }
    }

    /**
     * Checks for and displays overdue rentals with proper styling
     * Shows alert popup if overdue rentals are found
     */
    public void checkForOverdueRentals() {
        try {
            List<String> overdueRentals = violationDAO.getOverdueRentals();
            if (!overdueRentals.isEmpty()) {
                StringBuilder message = new StringBuilder("Overdue Rentals Detected:\n\n");
                for (String rentalId : overdueRentals) {
                    int lateHours = violationDAO.calculateLateHours(rentalId);
                    double penalty = violationDAO.calculateLatePenalty(rentalId);
                    message.append(String.format("• %s: %d hours late - Penalty: ₱%.2f\n",
                            rentalId, lateHours, penalty));
                }
                message.append("\nPlease process returns for these rentals.");
                showStyledOverduePopup("Overdue Rentals Alert", message.toString());
            } else {
                view.showSuccessPopup("No Overdue Rentals", "All rentals are currently on time!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Error", "Failed to check overdue rentals: " + e.getMessage());
        }
    }

    /**
     * Displays styled popup for overdue rentals with warning colors
     */
    private void showStyledOverduePopup(String title, String messageText) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);

        Label msg = new Label(messageText);
        msg.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-left;");
        msg.setWrapText(true);

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("small-button");
        okBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #FF9800, #F57C00); -fx-text-fill: white; -fx-font-weight: bold;");
        okBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(20, msg, okBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #FF9800, #FFB74D); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene scene = new Scene(layout, 450, 250);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(scene);
        scene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /**
     * Loads violations from database and populates the table
     */
    public void loadViolations() {
        try {
            List<ViolationRecord> violations = violationDAO.getAllViolations();
            masterList = FXCollections.observableArrayList(violations);
            view.tableView.setItems(masterList);
            sortByViolationId();
        } catch (SQLException e) {
            e.printStackTrace();
            view.showSuccessPopup("Database Error", "Failed to load violations.");
        }
    }

    /**
     * Applies filter to table based on search field text
     * Filters by violation ID, rental ID, type, or reason
     */
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            sortByViolationId();
            return;
        }

        ObservableList<ViolationRecord> filteredList = masterList.filtered(record -> {
            boolean matchId = record.getViolationId().toLowerCase().contains(filterText);
            boolean matchRentalId = record.getRentalId().toLowerCase().contains(filterText);
            boolean matchType = record.getViolationType().toLowerCase().contains(filterText);
            boolean matchReason = record.getReason().toLowerCase().contains(filterText);

            return matchId || matchRentalId || matchType || matchReason;
        });

        view.tableView.setItems(filteredList);
        sortByViolationId();
    }

    /**
     * Sorts table by violation ID in ascending order
     */
    private void sortByViolationId() {
        view.tableView.getSortOrder().clear();

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
     * Refreshes violations data from database
     */
    public void refreshViolations() {
        loadViolations();
    }

    /**
     * Sorts table by violation ID in specified order
     */
    public void sortByViolationId(boolean ascending) {
        view.tableView.getSortOrder().clear();

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