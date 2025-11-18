package com.example.dbcarrentalsapp;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.ViolationsByBranchRecord;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for the Violations By Branch Report feature.
 * Manages the interaction between the view and data access layer,
 * handling user actions and data retrieval for branch violation reporting.
 */
public class ViolationsByBranchController {

    private final ViolationsByBranchView view;
    private final ViolationsByBranchDAO dao;

    /**
     * Constructs a new controller with the specified view and data access object.
     * @param view the view component for violations by branch reporting
     * @param dao the data access object for violation data operations
     */
    public ViolationsByBranchController(ViolationsByBranchView view, ViolationsByBranchDAO dao) {
        this.view = view;
        this.dao = dao;
        initialize();
    }

    /**
     * Initializes the controller by setting up event handlers and initial data loading.
     * Configures button actions and date change listeners for automatic data refresh.
     */
    private void initialize() {
        // Set controller reference in view for date change listeners
        view.setController(this);

        // Configure refresh button to reload branch violations data
        view.getLoadButton().setOnAction(e -> handleLoadBranchViolations());

        // Set up pie chart button to display violations distribution
        view.getPieChartButton().setOnAction(e -> {
            var items = view.getTableView().getItems();
            if (items.isEmpty()) {
                showError("Empty Data, please load violations data first.");
                return;
            }
            view.showPieChartPopup(items);
        });

        // Configure company summary button to show overall violations
        view.getCompanyButton().setOnAction(e -> {
            String granularity = view.getSelectedGranularityToggle().getText();
            LocalDate selectedDate = getSelectedDateForGranularity(granularity);

            // Execute database query in background thread to prevent UI freezing
            new Thread(() -> {
                ViolationsByBranchRecord company =
                        dao.getCompanyViolations(selectedDate, granularity);

                // Update UI on JavaFX application thread
                Platform.runLater(() -> {
                    if (company == null) {
                        showError("No violation data found for the selected period.");
                        return;
                    }

                    view.showCompanyPopup(company);
                });
            }).start();
        });

        // Set up return button for navigation
        view.getReturnButton().setOnAction(e -> handleReturn());

        // Load initial data automatically on startup
        handleDateChange();
    }

    /**
     * Handles date or granularity changes by refreshing the violations data.
     * Called automatically when user changes date selection or time granularity.
     */
    public void handleDateChange() {
        handleLoadBranchViolations();
    }

    /**
     * Loads branch-by-branch violations data based on current date and granularity selection.
     * Executes database query in background thread and updates table view with results.
     */
    private void handleLoadBranchViolations() {
        String granularity = view.getSelectedGranularityToggle().getText();
        LocalDate selectedDate = getSelectedDateForGranularity(granularity);

        // Execute data retrieval in background thread to maintain UI responsiveness
        new Thread(() -> {
            List<ViolationsByBranchRecord> records =
                    dao.getViolationsByBranch(selectedDate, granularity);

            // Update table view on JavaFX application thread
            Platform.runLater(() -> {
                if (records.isEmpty()) {
                    showInfo("No violations found for the selected period.");
                }
                view.getTableView().getItems().setAll(records);
            });
        }).start();
    }

    /**
     * Converts the current UI selection into a LocalDate based on the specified granularity.
     *
     * @param granularity the time granularity ("daily", "monthly", or "yearly")
     * @return LocalDate representing the selected date period
     */
    private LocalDate getSelectedDateForGranularity(String granularity) {
        switch (granularity.toLowerCase()) {
            case "daily":
                return view.getDatePicker().getValue();
            case "monthly":
                // Create a date representing the first day of selected month/year
                int month = view.getSelectedMonth();
                int year = view.getSelectedYear();
                return LocalDate.of(year, month, 1);
            case "yearly":
                // Create a date representing the first day of selected year
                int yearly = view.getSelectedYear();
                return LocalDate.of(yearly, 1, 1);
            default:
                return LocalDate.now(); // Fallback to current date
        }
    }

    /**
     * Displays an error alert dialog with the specified message.
     * @param msg the error message to display
     */
    private void showError(String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText("Something went wrong");
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Displays an information alert dialog with the specified message.
     * @param msg the information message to display
     */
    private void showInfo(String msg) {
        Alert a = new Alert(AlertType.INFORMATION);
        a.setTitle("Information");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /**
     * Handles the return button action for navigation back to the main application.
     */
    private void handleReturn() {
        System.out.println("Return button clicked — implement navigation here.");
    }
}