package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.ViolationRecord;
import model.RentalDetails;
import java.util.List;

/**
 * Controller for handling violation processing operations.
 * Manages the business logic for violation detection, penalty calculation,
 * and violation recording.
 */
public class ViolationController {

    private final ViolationView view;
    private final ViolationDAO dao;
    private final Stage stage;
    private final ObservableList<ViolationRecord> violationList;

    public ViolationController(ViolationView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new ViolationDAO();
        this.violationList = FXCollections.observableArrayList();

        loadViolations();
        setupActions();
    }

    private void loadViolations() {
        violationList.setAll(dao.getAllViolations());
        view.tableView.setItems(violationList);
    }

    private void setupActions() {
        // Process Violation
        view.processButton.setOnAction(e -> processViolation());

        // Generate Report
        view.reportButton.setOnAction(e -> generateViolationReport());

        // Return to menu
        view.returnButton.setOnAction(e -> returnToMain());

        // Refresh violations list
        view.refreshButton.setOnAction(e -> loadViolations());
    }

    private void processViolation() {
        String rentalId = view.rentalIdField.getText().trim();

        if (rentalId.isEmpty()) {
            view.showErrorPopup("Error", "Please enter a rental ID.");
            return;
        }

        // Retrieve rental details
        RentalDetails rental = dao.getRentalById(rentalId);
        if (rental == null) {
            view.showErrorPopup("Error", "Rental not found.");
            return;
        }

        // Verify if car was returned late
        if (!dao.isReturnLate(rentalId)) {
            view.showErrorPopup("No Violation", "This rental was returned on time. No violation to process.");
            return;
        }

        // Calculate penalty (₱200 per hour late)
        double penalty = dao.calculateLatePenalty(rentalId, 200.00);

        // Show violation processing popup
        view.showViolationProcessingPopup(dao, rental, penalty, this::loadViolations);
    }

    private void generateViolationReport() {
        String branchId = view.branchFilterComboBox.getValue();
        List<ViolationRecord> violations;

        if (branchId == null || branchId.equals("All")) {
            violations = dao.getAllViolations();
        } else {
            violations = dao.getViolationsByBranch(branchId);
        }

        view.generateViolationReport(violations);
    }

    private void returnToMain() {
        ManageTransactionsView manageTransactionsView = new ManageTransactionsView(stage);
        new ManageTransactionsController(manageTransactionsView, stage);
        stage.setScene(manageTransactionsView.getScene());
    }
}