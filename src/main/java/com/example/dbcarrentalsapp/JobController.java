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
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a job to modify.");
            }
        });
        // Delete Jobs
        view.deleteButton.setOnAction(e -> {
            JobRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select staff to delete.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this job?\n\n"
                    + selected.getJobId() + " — "
                    + selected.getJobTitle() + ", " + selected.getJobDepartmentId()
                    + selected.getJobSalary());

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean success = dao.deleteJob(selected.getJobId());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Deleted", "Job deleted successfully.");
                        loadJobs(); // refresh table
                        System.out.println("Deleted Staff Id: " + selected.getJobId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete job.");
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

