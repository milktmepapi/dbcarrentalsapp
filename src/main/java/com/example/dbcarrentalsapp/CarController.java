package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.CarRecord;

import java.util.List;

public class CarController {

    private final CarView view;
    private final Stage stage;
    private final CarDAO dao;
    private ObservableList<CarRecord> masterList;

    public CarController(CarView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new CarDAO();

        setupActions();
        loadCars();
    }

    /** Sets up all button and UI actions **/
    private void setupActions() {

        // ===== Return to Manage Records =====
        view.returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView();
            new ManageRecordsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // ===== Add Cars =====
        view.addButton.setOnAction(e ->
                view.showAddCarPopup(dao, this::loadCars)
        );

        // ===== Modify Car =====
        view.modifyButton.setOnAction(e -> {
            CarRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                view.showModifyCarPopup(dao, selected, this::loadCars);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a cars to modify.");
            }
        });

        // ===== Delete cars =====
        view.deleteButton.setOnAction(e -> {
            CarRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a cars to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this car?\n\n"
                    + selected.getCarPlateNumber() + " — "
                    + selected.getCarTransmission() + ", " + selected.getCarModel()
                    + selected.getCarBrand() + ", " + selected.getCarYearManufactured()
                    + selected.getCarMileage() + ", " + selected.getCarSeatNumber()
                    + selected.getCarStatus() + ", " + selected.getCarBranchId());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = dao.deleteCar(selected.getCarPlateNumber());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", "Car deleted successfully.");
                        loadCars(); // refresh table
                        System.out.println("Deleted Plate Number: " + selected.getCarPlateNumber());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete car.");
                    }
                }
            });
        });

        // ===== Filter/Search =====
        view.filterButton.setOnAction(e -> applyFilter());

        // Optional: Press Enter in search field to filter
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Loads all Cars from database **/
    public void loadCars() {
        List<CarRecord> cars = dao.getAllCars();
        if (cars == null) cars = List.of(); // avoid NPE
        masterList = FXCollections.observableArrayList(cars);
        view.tableView.setItems(masterList);
    }

    /** Applies text-based filtering **/
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        String temp1 = String.valueOf(filterText);

        ObservableList<CarRecord> filteredList = masterList.filtered(record ->
                record.getCarModel().toLowerCase().contains(filterText) ||
                        record.getCarBrand().toLowerCase().contains(filterText) ||
                        record.getCarPlateNumber().toLowerCase().contains(filterText) ||
                        record.getCarBranchId().toLowerCase().contains(filterText) ||
                        record.getCarStatus().toLowerCase().contains(filterText) ||
                        record.getCarTransmission().toLowerCase().contains(filterText) ||
                        record.getStringVersionOfCarMileage().contains(filterText) ||
                        record.getStringVersionOfCarSeatNumber().contains(filterText) ||
                        record.getStringVersionOfYearManufactured().contains(filterText)
        );

        view.tableView.setItems(filteredList);
    }

    /** Utility method for showing alerts **/
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
