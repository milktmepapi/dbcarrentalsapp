package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.DepartmentRecord;

public class DepartmentController {

    private final Stage primaryStage;
    private final DepartmentView view;
    private final DepartmentDAO dao;
    private final ObservableList<DepartmentRecord> departmentList = FXCollections.observableArrayList();

    public DepartmentController(DepartmentView view, Stage primaryStage) {
        this.view = view;
        this.primaryStage = primaryStage;
        this.dao = new DepartmentDAO();

        loadDepartments();
        setupActions();
    }

    /** Load all departments into the table */
    public void loadDepartments() {
        departmentList.setAll(dao.getAllDepartments());
        view.tableView.setItems(departmentList);
    }

    /** Set up button actions */
    private void setupActions() {
        // Add Department
        view.addButton.setOnAction(e ->
                view.showAddDepartmentPopup(dao, this::loadDepartments)
        );

        // Modify Department
        view.modifyButton.setOnAction(e -> {
            DepartmentRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            view.showModifyDepartmentPopup(dao, selected, this::loadDepartments);
        });

        // Delete Department
        view.deleteButton.setOnAction(e -> {
            DepartmentRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (view.showConfirmPopup(selected)) {
                if (dao.deleteDepartment(selected.getDepartmentId())) {
                    loadDepartments();
                    view.showSuccessPopup("Deleted", "Department deleted successfully!");
                } else {
                    view.showSuccessPopup("Error", "Failed to delete department.");
                }
            }
        });

        // Return to Manage Records
        view.returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView();
            new ManageRecordsController(manageView, primaryStage);
            primaryStage.setScene(manageView.getScene());
        });

        // Search / Filter
        view.filterButton.setOnAction(e -> {
            String keyword = view.searchField.getText().trim().toLowerCase();
            String og = view.searchField.getText().trim();

            ObservableList<DepartmentRecord> filtered = departmentList.filtered(
                    d -> d.getDepartmentId().contains(og) ||
                            d.getDepartmentName().toLowerCase().contains(keyword)
            );
            view.tableView.setItems(filtered);
        });
    }
}