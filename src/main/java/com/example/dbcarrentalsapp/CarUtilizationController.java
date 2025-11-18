package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.BranchReport;

import java.util.List;
/*
public class CarUtilizationController {
    // 1. Use the correct DAO
    private final RentalsReportDAO rentalsReportDAO = new RentalsReportDAO();
    private final RentalsReportView view;
    private final Stage stage;

    public RentalsReportController(RentalsReportView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();

        // 2. Load the data into the table
        loadReportData();
    }

    private void setupActions() {
        // Back button
        view.returnButton.setOnAction(e -> goBack());

        // You can add filter logic here if needed
        // view.filterButton.setOnAction(e -> filterData());
    }

    // 3. New method to load and display the data
    private void loadReportData() {
        try {
            // Get data from the DAO
            List<BranchReport> reportList = rentalsReportDAO.getRentalsByBranch();

            // Convert to ObservableList for JavaFX
            ObservableList<BranchReport> observableList = FXCollections.observableArrayList(reportList);

            // Set the data in the TableView
            view.tableView.setItems(observableList);

        } catch (Exception e) {
            e.printStackTrace();
            // Optionally show an alert to the user
        }
    }

    private void goBack() {
        // This part was correct
        ManageReportsView mtv = new ManageReportsView(stage);
        new ManageReportsController(mtv, stage); // attach controller
        stage.setScene(mtv.getScene());
    }
}
*/
