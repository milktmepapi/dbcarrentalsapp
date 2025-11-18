package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.CarUtilizationReport;

import java.util.List;

public class CarUtilizationReportController {
    // 1. Use the correct DAO
    private final CarUtilizationReportDAO carUtilizationReportDAO = new CarUtilizationReportDAO();
    private final CarUtilizationReportView view;
    private final Stage stage;

    public CarUtilizationReportController(CarUtilizationReportView view, Stage stage) {
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
            List<CarUtilizationReport> reportList = carUtilizationReportDAO.getCarUtilizationReport();

            // Convert to ObservableList for JavaFX
            ObservableList<CarUtilizationReport> observableList = FXCollections.observableArrayList(reportList);

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
