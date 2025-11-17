package com.example.dbcarrentalsapp;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.RentalRecord; 
import model.ReturnRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class ReturnController {

    private final ReturnDAO returnDAO = new ReturnDAO();
    private final RentalDAO rentalDAO = new RentalDAO(); 
    private final ReturnView view;
    private final Stage stage;

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
    }

    private void goBack() {
        ManageTransactionsView mtv = new ManageTransactionsView(stage);
        new ManageTransactionsController(mtv, stage);
        stage.setScene(mtv.getScene());
    }

    private void processSelectedReturn() {
        // --- FIX: Get a RentalRecord from the view, not a ReturnRecord
        RentalRecord selected = view.getSelectedRecord();

        if (selected == null) {
            // --- FIX: Update the popup message
            showPopup("No Selection", "Please select an ACTIVE rental to return.");
            return;
        }

        // --- FIX: Pass the RentalRecord to the process method
        processReturn(selected);
    }

    // This is the method that processes the return
    public void processReturn(RentalRecord rental) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // We already have the rental, just check its status
            if (rental.getRentalStatus() != RentalRecord.RentalStatus.ACTIVE) {
                showPopup("Error", "Rental is not active.");
                conn.rollback(); // Rollback before returning
                return;
            }

            ReturnRecord newReturn = new ReturnRecord(null, rental.getRentalId(), "STF001");
            returnDAO.addReturn(conn, newReturn); // Uses your existing DAO method

            // Update rental status to COMPLETED
            rental.setRentalStatus(RentalRecord.RentalStatus.COMPLETED);
            // Set the actual return time
            rental.setActualReturnDateTime(LocalDateTime.now());

            // --- FIX: Use the rentalDAO and the new method you just added
            rentalDAO.updateRentalOnReturn(conn, rental);

            conn.commit(); // Commit transaction

            loadTable(); // Refresh the list (the returned item will disappear)

            showReturnReceipt(rental); // Show receipt for the rental we just processed

        } catch (SQLException ex) {
            ex.printStackTrace();
            // Don't forget to rollback on error!
            showPopup("Error", "Failed to process return. Rolling back.");
        }
    }

    private void showReturnReceipt(RentalRecord rental) {
        Alert receipt = new Alert(Alert.AlertType.INFORMATION);
        receipt.setTitle("Return Receipt");
        receipt.setHeaderText("Car Returned Successfully!");
        receipt.setContentText(
                "Rental ID: " + rental.getRentalId() + "\n" +
                        "Renter DL: " + rental.getRenterDlNumber() + "\n" +
                        "Car Plate: " + rental.getCarPlateNumber() + "\n" +
                        // --- FIX: Use the time from the object, not LocalDateTime.now()
                        "Return Date & Time: " + rental.getActualReturnDateTime() + "\n" +
                        "Total Payment: " + rental.getTotalPayment()
        );
        receipt.showAndWait();
    }

    private void showPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message); alert.showAndWait();
    }

    public void loadTable() {
        List<RentalRecord> list = rentalDAO.getActiveRentals();
        view.refreshTable(list);
    }
}

