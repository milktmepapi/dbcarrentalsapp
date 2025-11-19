package com.example.dbcarrentalsapp;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.RentalRecord;
import model.ReturnRecord;
import model.ViolationRecord;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReturnController {

    private final ReturnDAO returnDAO = new ReturnDAO();
    private final RentalDAO rentalDAO = new RentalDAO();
    // 1. Add ViolationDAO instance
    private final ViolationDAO violationDAO = new ViolationDAO();

    private final ReturnView view;
    private final Stage stage;

    // Hardcoded staff ID for context (ideally this comes from a Login Session)
    private final String CURRENT_STAFF_ID = "STF001";

    public ReturnController(ReturnView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.view.setController(this);
        setupActions();
        loadTable();
    }

    private void setupActions() {
        view.getBackButton().setOnAction(e -> goBack());
        view.getReturnButton().setOnAction(e -> processSelectedReturn());
        view.getFilterButton().setOnAction(e -> applyFilter());
    }

    private void goBack() {
        ManageTransactionsView mtv = new ManageTransactionsView(stage);
        new ManageTransactionsController(mtv, stage);
        stage.setScene(mtv.getScene());
    }

    private void processSelectedReturn() {
        RentalRecord selected = view.getSelectedRecord();

        if (selected == null) {
            showPopup("No Selection", "Please select an ACTIVE rental to return.", Alert.AlertType.WARNING);
            return;
        }
        processReturn(selected);
    }

    public void processReturn(RentalRecord rental) {
        boolean returnSuccess = false;

        // Step 1: Process the standard Return (Update Rental Table + Add Return Record)
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            if (rental.getRentalStatus() != RentalRecord.RentalStatus.ACTIVE) {
                showPopup("Error", "Rental is not active.", Alert.AlertType.ERROR);
                conn.rollback();
                return;
            }

            ReturnRecord newReturn = new ReturnRecord(null, rental.getRentalId(), CURRENT_STAFF_ID);
            returnDAO.addReturn(conn, newReturn);

            rental.setRentalStatus(RentalRecord.RentalStatus.COMPLETED);
            // IMPORTANT: The Database must have the Actual Return Time to calculate the delay
            rental.setActualReturnDateTime(LocalDateTime.now());

            rentalDAO.updateRentalOnReturn(conn, rental);

            conn.commit();
            returnSuccess = true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            showPopup("Error", "Failed to process return. Rolling back.", Alert.AlertType.ERROR);
        }

        // Step 2: IF Return was successful, Check for Automatic Violations
        if (returnSuccess) {
            try {
                // This method (from your ViolationDAO) calculates the time difference
                // between 'Expected' and 'Actual' (which we just saved above).
                ViolationRecord violation = violationDAO.createAutomaticLateViolation(rental.getRentalId(), CURRENT_STAFF_ID);

                loadTable(); // Refresh table to remove the item

                if (violation != null) {
                    // Case A: Late Return Detected
                    showLateReturnReceipt(rental, violation);
                } else {
                    // Case B: On-Time Return
                    showReturnReceipt(rental);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showPopup("System Warning", "Return saved, but failed to check for violations: " + e.getMessage(), Alert.AlertType.WARNING);
            }
        }
    }

    /**
     * Standard Receipt for On-Time Returns
     */
    private void showReturnReceipt(RentalRecord rental) {
        Alert receipt = new Alert(Alert.AlertType.INFORMATION);
        receipt.setTitle("Return Receipt");
        receipt.setHeaderText("Car Returned Successfully (On Time)");
        receipt.setContentText(
                "Rental ID: " + rental.getRentalId() + "\n" +
                        "Renter DL: " + rental.getRenterDlNumber() + "\n" +
                        "Car Plate: " + rental.getCarPlateNumber() + "\n" +
                        "Date: " + rental.getActualReturnDateTime().toLocalDate() + "\n" +
                        "Total Payment: ₱" + rental.getTotalPayment()
        );
        receipt.showAndWait();
    }

    /**
     * Special Receipt for Late Returns with Warning Styling
     */
    private void showLateReturnReceipt(RentalRecord rental, ViolationRecord violation) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("LATE RETURN DETECTED");
        alert.setHeaderText("Car Returned Late - Violation Recorded");

        // Calculate Total locally to ensure it is accurate
        double baseTotal = (rental.getTotalPayment() != null) ? rental.getTotalPayment().doubleValue() : 0.0;
        double penalty = violation.getPenaltyFee();
        double grandTotal = baseTotal + penalty;

        // Create the content string
        String content = "Rental ID: " + rental.getRentalId() + "\n" +
                "Car Plate: " + rental.getCarPlateNumber() + "\n" +
                "------------------------------------------------\n" +
                "VIOLATION DETAILS:\n" +
                "ID: " + violation.getViolationId() + "\n" +
                "Type: " + violation.getViolationType() + "\n" +
                "Hours Late: " + violation.getDurationHours() + " hours\n" +
                "Penalty Fee: ₱" + String.format("%.2f", penalty) + "\n" +
                "Reason: " + violation.getReason() + "\n" +
                "------------------------------------------------\n" +
                "Base Rental: ₱" + String.format("%.2f", baseTotal) + "\n" +
                "TOTAL TO PAY: ₱" + String.format("%.2f", grandTotal);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        // FIX: Set a preferred size so the box isn't tiny
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        textArea.setPrefRowCount(10); // Shows 10 lines by default
        textArea.setPrefColumnCount(40);

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("Receipt & Violation Details:"), 0, 0);
        expContent.add(textArea, 0, 1);

        // Set the custom content
        alert.getDialogPane().setExpandableContent(expContent);
        // Automatically expand it so the user sees the details immediately
        alert.getDialogPane().setExpanded(true);

        alert.showAndWait();
    }

    private void showPopup(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadTable() {
        List<RentalRecord> list = rentalDAO.getActiveRentals();
        view.refreshTable(list);
    }

    private void applyFilter() {
        String keyword = view.getSearchField().getText().toLowerCase().trim();
        List<RentalRecord> allActiveRentals = rentalDAO.getActiveRentals();
        if (keyword.isEmpty()) {
            view.refreshTable(allActiveRentals);
            return;
        }

        List<RentalRecord> filteredList = allActiveRentals.stream()
                .filter(r ->(r.getRentalId() != null && r.getRentalId().toLowerCase().contains(keyword)) ||
                        (r.getCarPlateNumber() != null && r.getCarPlateNumber().toLowerCase().contains(keyword)) ||
                        (r.getRenterDlNumber() != null && r.getRenterDlNumber().toLowerCase().contains(keyword))
                )
                .collect(Collectors.toList());

        view.refreshTable(filteredList);
    }
}
