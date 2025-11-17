package com.example.dbcarrentalsapp;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

        // Load per-branch revenue
        view.getLoadButton().setOnAction(e -> handleLoadBranchRevenue());

        // Reload when granularity changes
        view.dailyButton.setOnAction(e -> handleLoadBranchRevenue());
        view.monthlyButton.setOnAction(e -> handleLoadBranchRevenue());
        view.yearlyButton.setOnAction(e -> handleLoadBranchRevenue());

        // Load whole-company revenue
        view.getCompanyButton().setOnAction(e -> {

            String granularity = view.getSelectedGranularityToggle().getText();
            LocalDate today = LocalDate.now();

            new Thread(() -> {
                RevenueByBranchRecord company =
                        dao.getCompanyRevenue(today, granularity);

                Platform.runLater(() -> {
                    if (company == null) {
                        showError("No company revenue found.");
                        return;
                    }

                    view.showCompanyPopup(company);
                });
            }).start();
        });


        // Return
        view.getReturnButton().setOnAction(e -> handleReturn());
    }

    // ============================================================
    // LOAD BRANCH-BY-BRANCH REVENUE
    // ============================================================
    private void handleLoadBranchRevenue() {

        String granularity = view.getSelectedGranularityToggle().getText();
        LocalDate today = LocalDate.now();

        new Thread(() -> {
            List<RevenueByBranchRecord> records =
                    dao.getRevenueByBranch(today, granularity);

            Platform.runLater(() ->
                    view.getTableView().getItems().setAll(records));
        }).start();
    }

    // ============================================================
    // LOAD WHOLE-COMPANY REVENUE INTO TABLE
    // ============================================================
    private void handleLoadCompanyRevenue() {

        String granularity = view.getSelectedGranularityToggle().getText();
        LocalDate today = LocalDate.now();

        new Thread(() -> {
            RevenueByBranchRecord company =
                    dao.getCompanyRevenue(today, granularity);

            Platform.runLater(() -> {
                if (company == null) {
                    showError("No company revenue found.");
                    return;
                }

                // Replace table contents with 1 row
                view.getTableView().getItems().setAll(company);
            });
        }).start();
    }

    // ============================================================
    // ERROR HANDLER
    // ============================================================
    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText("Something went wrong");
        a.setContentText(msg);
        a.showAndWait();
    }

    // ============================================================
    // RETURN — implement navigation
    // ============================================================
    private void handleReturn() {
        System.out.println("Return button clicked — implement navigation here.");
    }
}