package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.CarRecord;
import model.StaffRecord;

import java.util.List;

public class StaffController {
    private final Stage stage;
    private final StaffView view;
    private final StaffDAO dao;
    private ObservableList<StaffRecord> masterList;

    public StaffController(StaffView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new StaffDAO();

        loadStaff();
        setupActions();
    }

    /** Load all staff into the table */
    public void loadStaff() {
        List<StaffRecord> staff = dao.getAllStaff();
        if (staff == null) staff = List.of(); // avoid NPE
        masterList = FXCollections.observableArrayList(staff);
        view.tableView.setItems(masterList);
    }

    /** Set up button actions */
    private void setupActions() {
        // ===== Return to Manage Records =====
        view.returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView();
            new ManageRecordsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // Add Staff
        view.addButton.setOnAction(e ->
                view.showAddStaffPopup(dao, this::loadStaff)
        );


        // ===== Modify Car =====
        view.modifyButton.setOnAction(e -> {
            StaffRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                view.showModifyStaffPopup(dao, selected, this::loadStaff);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a cars to modify.");
            }
        });
        // Delete Staff
        view.deleteButton.setOnAction(e -> {
            StaffRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select staff to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this staff member?\n\n"
                    + selected.getStaffId() + " — "
                    + selected.getStaffFirstName() + ", " + selected.getStaffLastName()
                    + selected.getStaffJobId() + ", " + selected.getStaffBranchId());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = dao.deleteStaff(selected.getStaffId());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", "Staff deleted successfully.");
                        loadStaff(); // refresh table
                        System.out.println("Deleted Staff Id: " + selected.getStaffId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete staff member.");
                    }
                }
            });
        });

        // ===== Filter/Search =====
        view.filterButton.setOnAction(e -> applyFilter());

        // Optional: Press Enter in search field to filter
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Applies text-based filtering **/
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        ObservableList<StaffRecord> filteredList = masterList.filtered(record ->
                record.getStaffJobId().toLowerCase().contains(filterText) ||
                        record.getStaffBranchId().toLowerCase().contains(filterText) ||
                        record.getStaffId().toLowerCase().contains(filterText)
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
