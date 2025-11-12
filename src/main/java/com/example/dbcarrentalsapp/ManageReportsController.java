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
        // TODO: implement report logic
    }

    private void showRentalsReport() {
        System.out.println("Opening Rentals by Branch Report...");
    }

    private void showUtilizationReport() {
        System.out.println("Opening Car Utilization Report...");
    }

    private void showViolationsReport() {
        System.out.println("Opening Violations by Branch Report...");
    }

    private void goBack() {
        System.out.println("Returning to User Dashboard...");
        UserView userView = new UserView();
        UserController userController = new UserController(userView, stage);
        userController.setupActions();
        stage.setScene(userView.getScene());
    }
}
