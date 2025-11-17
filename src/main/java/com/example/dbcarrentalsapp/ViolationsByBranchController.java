package com.example.dbcarrentalsapp;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.ViolationsByBranchRecord;

import java.time.LocalDate;
import java.util.List;

public class ViolationsByBranchController {

    private final ViolationsByBranchView view;
    private final ViolationsByBranchDAO dao;

    public ViolationsByBranchController(ViolationsByBranchView view, ViolationsByBranchDAO dao) {
        this.view = view;
        this.dao = dao;
        initialize();
    }

    private void initialize() {

        // Load per-branch violations
        view.getLoadButton().setOnAction(e -> handleLoadBranchViolations());

        // Reload when granularity changes
        view.dailyButton.setOnAction(e -> handleLoadBranchViolations());
        view.monthlyButton.setOnAction(e -> handleLoadBranchViolations());
        view.yearlyButton.setOnAction(e -> handleLoadBranchViolations());

        // NEW: Pie Chart button handler
        view.getPieChartButton().setOnAction(e -> {
            var items = view.getTableView().getItems();
            if (items.isEmpty()) {
                showError("Empty Data, please load violations data first.");
                return;
            }
            view.showPieChartPopup(items);
        });

        // Load whole-company violations summary
        view.getCompanyButton().setOnAction(e -> {
            String granularity = view.getSelectedGranularityToggle().getText();
            LocalDate today = LocalDate.now();

            new Thread(() -> {
                ViolationsByBranchRecord company =
                        dao.getCompanyViolations(today, granularity);

                Platform.runLater(() -> {
                    if (company == null) {
                        showError("No violation data found for the selected period.");
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
    // LOAD BRANCH-BY-BRANCH VIOLATIONS
    // ============================================================
    private void handleLoadBranchViolations() {

        String granularity = view.getSelectedGranularityToggle().getText();
        LocalDate today = LocalDate.now();

        new Thread(() -> {
            List<ViolationsByBranchRecord> records =
                    dao.getViolationsByBranch(today, granularity);

            Platform.runLater(() -> {
                if (records.isEmpty()) {
                    showInfo("No violations found for the selected period.");
                }
                view.getTableView().getItems().setAll(records);
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

    private void showInfo(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Information");
        a.setHeaderText(null);
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