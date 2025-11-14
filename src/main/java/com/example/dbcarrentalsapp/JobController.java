package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import model.JobRecord;

import java.util.List;

public class JobController {
    private final Stage stage;
    private final JobView view;
    private final JobDAO dao;
    private ObservableList<JobRecord> masterList;

    public JobController(JobView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new JobDAO();

        loadJobs();
        setupActions();
    }

    /** Load all jobs into the table */
    public void loadJobs() {
        List<JobRecord> jobs = dao.getAllJobs();
        if (jobs == null) jobs = List.of(); // avoid NPE
        masterList = FXCollections.observableArrayList(jobs);
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

        // Add Jobs
        view.addButton.setOnAction(e ->
                view.showAddJobPopup(dao, this::loadJobs)
        );

        // ===== Modify Jobs =====
        view.modifyButton.setOnAction(e -> {
            JobRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                view.showModifyJobPopup(dao, selected, this::loadJobs);
            } else {
                view.showSuccessPopup("No Selection", "Please select a job to modify.");
            }
        });

        // ===== Delete Jobs =====
        view.deleteButton.setOnAction(e -> {
            JobRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a job to delete.");
                return;
            }

            // Show confirmation popup with record details
            boolean confirmed = view.showConfirmPopup(selected);

            if (confirmed) {
                boolean success = dao.deleteJob(selected.getJobId());
                if (success) {
                    view.showSuccessPopup("Deleted", "Job deleted successfully!");
                    loadJobs();
                    System.out.println("Deleted Job ID: " + selected.getJobId());
                } else {
                    view.showSuccessPopup("Error", "Failed to delete job.");
                }
            }
        });

        // ===== Filter/Search =====
        view.filterButton.setOnAction(e -> applyFilter());

        // Optional: Press Enter in search field to filter
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Applies text-based filtering **/
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();
        String og = view.searchField.getText().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        String temp = String.valueOf(og);

        ObservableList<JobRecord> filteredList = masterList.filtered(record ->
                record.getJobTitle().toLowerCase().contains(filterText) ||
                        record.getJobDepartmentId().contains(og) ||
                        record.getJobId().contains(og)||
                        record.getStringVersionOfSalary().contains(temp)
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