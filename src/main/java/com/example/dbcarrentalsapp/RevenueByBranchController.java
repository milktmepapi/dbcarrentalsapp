package com.example.dbcarrentalsapp;

import javafx.application.Platform;
import model.RevenueByBranchRecord;
import java.time.LocalDate;
import java.util.List;

public class RevenueByBranchController {

    private final RevenueByBranchView view;
    private final RevenueByBranchDAO dao;

    public RevenueByBranchController(RevenueByBranchView view, RevenueByBranchDAO dao) {
        this.view = view;
        this.dao = dao;
        initialize();
    }

    private void initialize() {
        // Load results when LOAD is clicked
        view.getLoadButton().setOnAction(e -> handleLoadRevenue());

        // Also reload when granularity changes
        view.dailyButton.setOnAction(e -> handleLoadRevenue());
        view.monthlyButton.setOnAction(e -> handleLoadRevenue());
        view.yearlyButton.setOnAction(e -> handleLoadRevenue());
    }

    private void handleLoadRevenue() {
        var selectedToggle = view.getSelectedGranularityToggle();

        if (selectedToggle == null) {
            showError("Please select Daily, Monthly, or Yearly.");
            return;
        }

        String granularity = selectedToggle.getText();
        LocalDate today = LocalDate.now();

        // Fetch data in background
        new Thread(() -> {
            List<RevenueByBranchRecord> records =
                    dao.getRevenueByBranch(today, granularity);

            Platform.runLater(() ->
                    view.getTableView().getItems().setAll(records));
        }).start();
    }

    private void showError(String msg) {
        // If you want popup errors later, place code here
        System.err.println("ERROR: " + msg);
    }
}