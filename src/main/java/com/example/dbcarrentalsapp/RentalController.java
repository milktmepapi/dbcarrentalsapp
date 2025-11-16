package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CarRecord;
import model.RentalRecord;
import model.RentalRecord.RentalStatus;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class RentalController {

    private final RentalView view;
    private final Stage stage;
    private final RentalDAO rentalDAO;
    private final RenterDAO renterDAO;
    private final CarDAO carDAO;
    private ObservableList<RentalRecord> masterList;

    public RentalController(RentalView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.rentalDAO = new RentalDAO();
        this.renterDAO = new RenterDAO();
        this.carDAO = new CarDAO();

        loadRentals();
        setupActions();
    }

    private void setupActions() {

        // Return to Manage Transactions
        view.returnButton.setOnAction(e -> {
            ManageTransactionsView manageView = new ManageTransactionsView(stage);
            new ManageTransactionsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // Add Rental: generate id, show popup, validate & insert
        view.addButton.setOnAction(e -> {
            try {
                String newId = rentalDAO.generateNextRentalId();

                // gather branch list from cars
                List<CarRecord> allCars = CarDAO.getAllCars();
                List<String> branches = allCars.stream()
                        .map(CarRecord::getCarBranchId)
                        .distinct()
                        .sorted()
                        .collect(Collectors.toList());

                List<String> renters = renterDAO.getAllRenterDLs();

                view.showAddRentalPopup(newId, data -> {
                    try {
                        // Basic validation
                        if (data.pickup.isAfter(data.returnDate) || data.pickup.isEqual(data.returnDate)) {
                            showError("Validation", "Return must be after pickup.");
                            return;
                        }

                        if (data.totalPayment.compareTo(BigDecimal.ZERO) < 0) {
                            showError("Validation", "Payment must be a non-negative value.");
                            return;
                        }

                        // Car availability check
                        if (!isCarAvailable(data.carPlate, data.pickup, data.returnDate)) {
                            showError("Unavailable", "Selected car is not available for the chosen dates.");
                            return;
                        }

                        // Build RentalRecord
                        RentalRecord r = new RentalRecord(
                                newId,                              // will be overwritten by DAO.generateNextRentalId inside addRental but set anyway
                                data.renterDl,
                                data.carPlate,
                                data.branch,
                                null, // staff pickup
                                null, // staff return
                                LocalDateTime.now(), // rental_datetime
                                data.pickup,
                                null, // actual pickup
                                data.returnDate,
                                null, // actual return
                                data.totalPayment,
                                RentalStatus.UPCOMING
                        );

                        // Persist
                        rentalDAO.addRental(r);
                        loadRentals();
                        showInfo("Success", "Rental added successfully (ID: " + newId + ")");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        showError("Database Error", ex.getMessage());
                    }
                }, branches, renters, allCars);

            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Database Error", "Failed to generate new Rental ID.");
            }
        });

        // Modify Rental (not fully implemented in this snippet)
        view.modifyButton.setOnAction(e -> {
            showError("Not implemented", "Modify rental is not implemented yet.");
        });

        // VIEW RENTAL DETAILS
        view.viewButton.setOnAction(e -> {
            RentalRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("No Selection", "Please select a rental.");
                return;
            }
            showDetailsPopup(selected);
        });

        // Search
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Loads all rentals */
    public void loadRentals() {
        try {
            List<RentalRecord> rentals = rentalDAO.getAllRentals();
            masterList = FXCollections.observableArrayList(rentals);
            view.tableView.setItems(masterList);
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Failed to load rentals.");
        }
    }

    /** Filtering */
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        ObservableList<RentalRecord> filteredList = masterList.filtered(record ->
                record.getRentalId().toLowerCase().contains(filterText)
                        || record.getCarPlateNumber().toLowerCase().contains(filterText)
                        || record.getRentalStatus().name().toLowerCase().contains(filterText)
                        || (record.getRenterDlNumber() != null &&
                        record.getRenterDlNumber().toLowerCase().contains(filterText))
        );

        view.tableView.setItems(filteredList);
    }

    // ================================================================
    // Car availability check: not Rented / Under Maintenance / overlapping upcoming/active rentals
    // ================================================================
    private boolean isCarAvailable(String plate, LocalDateTime start, LocalDateTime end) {
        // check car status
        CarRecord car = carDAO.getCarByPlate(plate);
        if (car == null) return false;
        String status = car.getCarStatus();
        if ("Rented".equalsIgnoreCase(status) || "Under Maintenance".equalsIgnoreCase(status)) {
            return false;
        }

        // check existing rentals for overlaps (UPCOMING or ACTIVE)
        try {
            List<RentalRecord> rentals = rentalDAO.getAllRentals();
            for (RentalRecord r : rentals) {
                if (!plate.equalsIgnoreCase(r.getCarPlateNumber())) continue;
                RentalStatus s = r.getRentalStatus();
                if (s == RentalStatus.UPCOMING || s == RentalStatus.ACTIVE) {
                    LocalDateTime existingStart = r.getExpectedPickupDateTime();
                    LocalDateTime existingEnd = r.getExpectedReturnDateTime();
                    // overlap if start < existingEnd && existingStart < end
                    if (start.isBefore(existingEnd) && existingStart.isBefore(end)) {
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    // ================================================================
    // VIEW DETAILS POPUP (kept same as earlier)
    // ================================================================
    private void showDetailsPopup(RentalRecord r) {

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Rental Details");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(25);
        grid.setPadding(new Insets(20));

        String[][] data = {
                {"Rental ID:", r.getRentalId()},
                {"Driver's License:", r.getRenterDlNumber()},
                {"Car Plate:", r.getCarPlateNumber()},
                {"Branch:", r.getBranchId()},
                {"Pickup (Expected):", r.getExpectedPickupDateTime().format(fmt)},
                {"Return (Expected):", r.getExpectedReturnDateTime().format(fmt)},
                {"Status:", r.getRentalStatus().name()},
                {"Total Payment:", String.valueOf(r.getTotalPayment())}
        };

        int row = 0;
        for (String[] d : data) {
            Label label = new Label(d[0]);
            label.setStyle("-fx-text-fill: #b46bff; -fx-font-weight: bold; -fx-font-size: 14px;");

            Label value = new Label(d[1]);
            value.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            grid.add(label, 0, row);
            grid.add(value, 1, row);
            row++;
        }

        Button closeBtn = new Button("Close");
        closeBtn.setPrefWidth(120);
        closeBtn.setStyle("-fx-background-color: #7a40ff; -fx-text-fill: white; -fx-font-weight: bold;");
        closeBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(20, grid, closeBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle(
                "-fx-background-color: rgba(25,25,35,0.95);" +
                        "-fx-background-radius: 10;"
        );

        Scene scene = new Scene(layout, 420, 500);
        popup.setScene(scene);
        popup.showAndWait();
    }

    // ================================================================
    // Simple helpers
    // ================================================================
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}