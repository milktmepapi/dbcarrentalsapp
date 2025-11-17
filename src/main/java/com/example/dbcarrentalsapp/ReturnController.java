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
        ReturnRecord selected = view.getSelectedRecord();
        if (selected == null) {
            showPopup("No Selection", "Please select a return record.");
            return;
        }

        processReturn(selected);
    }

    public void processReturn(ReturnRecord selected) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            RentalDAO rentalDAO = new RentalDAO();
            RentalRecord rental = rentalDAO.getRentalById(selected.getReturnRentalID());
            if (rental == null) {
                showPopup("Error", "Rental not found.");
                return;
            }

            if (rental.getRentalStatus() != RentalRecord.RentalStatus.ACTIVE) {
                showPopup("Error", "Rental is not active.");
                return;
            }

            // Update car status using the same connection
            CarDAO carDAO = new CarDAO();
            carDAO.updateCarStatus(conn, rental.getCarPlateNumber());

            // Insert return record
            ReturnRecord newReturn = new ReturnRecord(null, rental.getRentalId(), "STAFF001");
            returnDAO.addReturn(conn, newReturn);

            // Update rental status to COMPLETED
            rental.setRentalStatus(RentalRecord.RentalStatus.COMPLETED);
            rentalDAO.updateRentalStatus(conn, rental);

            conn.commit(); // Commit transaction

            loadTable();

            showReturnReceipt(rental);

        } catch (SQLException ex) {
            ex.printStackTrace();
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
                        "Return Date & Time: " + java.time.LocalDateTime.now() + "\n" +
                        "Total Payment: " + rental.getTotalPayment()
        );
        receipt.showAndWait();
    }


    public void viewReceipt(ReturnRecord record) {
        try {
            RentalDAO rentalDAO = new RentalDAO();
            RentalRecord rental = rentalDAO.getRentalById(record.getReturnRentalID());
            if (rental == null) { showPopup("Error", "Rental not found."); return; }

            Alert receipt = new Alert(Alert.AlertType.INFORMATION);
            receipt.setTitle("Return Receipt");
            receipt.setHeaderText("Car Returned Successfully!");
            receipt.setContentText(
                    "Rental ID: " + rental.getRentalId() + "\n" +
                            "Renter DL: " + rental.getRenterDlNumber() + "\n" +
                            "Car Plate: " + rental.getCarPlateNumber() + "\n" +
                            "Return Date & Time: " + LocalDateTime.now() + "\n" +
                            "Total Payment: " + rental.getTotalPayment()
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

    public void loadTable() {
            List<ReturnRecord> list = returnDAO.getAllReturns();
            view.refreshTable(list);
    }
}
