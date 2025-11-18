package com.example.dbcarrentalsapp;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.RentalRecord;
import model.ReturnRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            showPopup("No Selection", "Please select an ACTIVE rental to return.");
            return;
        }
        processReturn(selected);
    }
    
    public void processReturn(RentalRecord rental) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            if (rental.getRentalStatus() != RentalRecord.RentalStatus.ACTIVE) {
                showPopup("Error", "Rental is not active.");
                conn.rollback();
                return;
            }

            ReturnRecord newReturn = new ReturnRecord(null, rental.getRentalId(), "STF001");
            returnDAO.addReturn(conn, newReturn); 
            rental.setRentalStatus(RentalRecord.RentalStatus.COMPLETED);
            rental.setActualReturnDateTime(LocalDateTime.now());
            rentalDAO.updateRentalOnReturn(conn, rental);
            conn.commit(); 
            loadTable(); 

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
                        "Return Date & Time: " + rental.getActualReturnDateTime() + "\n" +
                        "Total Payment: " + rental.getTotalPayment()
        );
        receipt.showAndWait();
    }

    private void showPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

