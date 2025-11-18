package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

public class ManageReportsController {

    private ManageReportsView view;
    private Stage stage;

    public ManageReportsController(ManageReportsView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    private void setupActions() {
        view.revenueButton.setOnAction(e -> showRevenueReport());
        view.rentalsButton.setOnAction(e -> showRentalsReport());
        view.utilizationButton.setOnAction(e -> showUtilizationReport());
        view.violationsButton.setOnAction(e -> showViolationsReport());
        view.returnButton.setOnAction(e -> goBack());
    }

    private void showRevenueReport() {
        System.out.println("Opening Revenue by Branch Report...");

        // Instantiate DAO and View
        RevenueByBranchDAO dao = new RevenueByBranchDAO();
        RevenueByBranchView revenueView = new RevenueByBranchView();

        // Instantiate Controller (wires buttons + loads data)
        new RevenueByBranchController(revenueView, dao);

        // Wire the RETURN button to come back here
        revenueView.getReturnButton().setOnAction(e -> {
            stage.setScene(view.getScene());
        });

        // Switch the scene to the report
        stage.setScene(revenueView.getScene());
    }

    private void showRentalsReport() {
        System.out.println("Opening Rentals by Branch Report...");

        RentalsReportView mtv = new RentalsReportView();
        new RentalsReportController(mtv, stage);
        stage.setScene(mtv.getScene());
    }

    private void showUtilizationReport() {
        System.out.println("Opening Car Utilization Report...");

        CarUtilizationReportView crv = new CarUtilizationReportView();
        new CarUtilizationReportController(crv, stage);
        stage.setScene(crv.getScene());
    }

    private void showViolationsReport() {
        System.out.println("Opening Violations by Branch Report...");

        // Instantiate DAO and View
        ViolationsByBranchDAO dao = new ViolationsByBranchDAO();
        ViolationsByBranchView violationsView = new ViolationsByBranchView();

        // Instantiate Controller (wires buttons + loads data)
        new ViolationsByBranchController(violationsView, dao);

        // Wire the RETURN button to come back here
        violationsView.getReturnButton().setOnAction(e -> {
            stage.setScene(view.getScene());
        });

        // Switch the scene to the report
        stage.setScene(violationsView.getScene());
    }

    private void goBack() {
        System.out.println("Returning to User Dashboard...");
        UserView userView = new UserView();
        UserController userController = new UserController(userView, stage);
        userController.setupActions();
        stage.setScene(userView.getScene());
    }
}

