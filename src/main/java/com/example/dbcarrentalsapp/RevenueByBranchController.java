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

        // Load per-branch revenue when user clicks Load
        view.getLoadButton().setOnAction(e -> handleLoadBranchRevenue());

        // PIE CHART
        view.pieChartButton.setOnAction(e -> {
            var items = view.tableView.getItems();
            if (items.isEmpty()) {
                showError("Empty data. Load revenue first.");
                return;
            }
            view.showPieChartPopup(items);
        });

        // COMPANY TOTAL REVENUE POPUP
        view.getCompanyButton().setOnAction(e -> {
            String granularity = view.getSelectedGranularityToggle().getText();
            LocalDate today = LocalDate.now();

            new Thread(() -> {
                RevenueByBranchRecord company = dao.getCompanyRevenue(today, granularity);
                Platform.runLater(() -> {
                    if (company == null) {
                        showError("No company revenue found.");
                        return;
                    }
                    view.showCompanyPopup(company);
                });
            }).start();
        });

        view.getReturnButton().setOnAction(e -> handleReturn());
    }

    // ============================================================
    // LOAD PER-BRANCH REVENUE
    // ============================================================
    private void handleLoadBranchRevenue() {

        String granularity = view.getSelectedGranularityToggle().getText();
        LocalDate dateToUse;

        switch (granularity) {
            case "Daily":
                if (view.dailyPicker.getValue() == null) {
                    showError("Please select a date.");
                    return;
                }
                dateToUse = view.dailyPicker.getValue();
                break;

            case "Monthly":
                if (view.monthPicker.getValue() == null) {
                    showError("Please select a month.");
                    return;
                }
                if (view.yearPicker.getValue() == null) {
                    showError("Please select a year.");
                    return;
                }
                dateToUse = LocalDate.of(
                        view.yearPicker.getValue(),
                        monthToNumber(view.monthPicker.getValue()),
                        1
                );
                break;

            case "Yearly":
                if (view.yearPicker.getValue() == null) {
                    showError("Please select a year.");
                    return;
                }
                dateToUse = LocalDate.of(view.yearPicker.getValue(), 1, 1);
                break;

            default:
                showError("Invalid granularity.");
                return;
        }

        if (dateToUse.isAfter(LocalDate.now())) {
            showError("Cannot load future revenue.");
            return;
        }

        final LocalDate finalDate = dateToUse;
        final String finalGranularity = granularity;

        new Thread(() -> {
            List<RevenueByBranchRecord> records =
                    dao.getRevenueByBranch(finalDate, finalGranularity);

            Platform.runLater(() -> {
                view.getTableView().getItems().setAll(records);
            });
        }).start();
    }

    private int monthToNumber(String m) {
        switch (m) {
            case "January": return 1;
            case "February": return 2;
            case "March": return 3;
            case "April": return 4;
            case "May": return 5;
            case "June": return 6;
            case "July": return 7;
            case "August": return 8;
            case "September": return 9;
            case "October": return 10;
            case "November": return 11;
            case "December": return 12;
        }
        throw new IllegalArgumentException("Unknown month: " + m);
    }

    // ============================================================
    // ERROR POPUP
    // ============================================================
    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText("Something went wrong");
        a.setContentText(msg);
        a.showAndWait();
    }

    // ============================================================
    // RETURN (Navigation)
    // ============================================================
    private void handleReturn() {
        System.out.println("Return button clicked — implement navigation here.");
    }
}