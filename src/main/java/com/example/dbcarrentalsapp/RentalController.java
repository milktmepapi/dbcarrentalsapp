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
import model.RentalRecord;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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

    private void setupActions() {

        // Return to Manage Transactions
        view.returnButton.setOnAction(e -> {
            ManageTransactionsView manageView = new ManageTransactionsView(stage);
            new ManageTransactionsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        /* Add Rental
        view.addButton.setOnAction(e -> {
            String newId = rentalDAO.generateNewRentalId();
            view.showAddRentalPopup(newId, data -> {
                try {
                    rentalDAO.addRental(
                            data.rentalId,
                            data.renterDl,
                            data.carPlate,
                            data.pickup,
                            data.returnDate,
                            data.branch
                    );
                    loadRentals();
                } catch (SQLException ex) {
                    showError("Error", ex.getMessage());
                }
            });
        });

        // Modify Rental
        view.modifyButton.setOnAction(e -> {
            RentalRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("No Selection", "Please select a rental to modify.");
                return;
            }

            view.showModifyRentalPopup(selected, data -> {
                try {
                    rentalDAO.updateRental(
                            data.rentalId,
                            data.renterDl,
                            data.carPlate,
                            data.pickup,
                            data.returnDate,
                            data.branch
                    );
                    loadRentals();
                } catch (SQLException ex) {
                    showError("Database Error", ex.getMessage());
                }
            });
        }); */

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
    // VIEW RENTAL DETAILS POPUP  (dark theme, clean, modern)
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
    // POPUP UTILITIES
    // ================================================================
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}