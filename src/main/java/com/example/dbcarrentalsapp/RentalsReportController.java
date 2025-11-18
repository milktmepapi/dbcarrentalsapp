package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.BranchReport;

import java.util.List;
import java.util.stream.Collectors; 

public class RentalsReportController {

    private final RentalsReportDAO rentalsReportDAO = new RentalsReportDAO();
    private final RentalsReportView view;
    private final Stage stage;

    public RentalsReportController(RentalsReportView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
        loadReportData();
    }

    private void setupActions() {
        view.returnButton.setOnAction(e -> goBack());
        view.getFilterButton().setOnAction(e -> applyFilter());
    }

    private void loadReportData() {
        try {
            List<BranchReport> reportList = rentalsReportDAO.getRentalsByBranch();
            ObservableList<BranchReport> observableList = FXCollections.observableArrayList(reportList);
            view.tableView.setItems(observableList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void applyFilter() {
        String keyword = view.getSearchField().getText().toLowerCase().trim();
        List<BranchReport> allData = rentalsReportDAO.getRentalsByBranch();
        if (keyword.isEmpty()) {
            view.tableView.setItems(FXCollections.observableArrayList(allData));
            return;
        }
        
        List<BranchReport> filteredList = allData.stream()
                .filter(r ->
                        (r.getBranchName() != null && r.getBranchName().toLowerCase().contains(keyword)) ||
                                (r.getCarTransmission() != null && r.getCarTransmission().toLowerCase().contains(keyword))
                )
                .collect(Collectors.toList());
        view.tableView.setItems(FXCollections.observableArrayList(filteredList));
    }

    private void goBack() {
        ManageReportsView mtv = new ManageReportsView(stage);
        new ManageReportsController(mtv, stage);
        stage.setScene(mtv.getScene());
    }
}



