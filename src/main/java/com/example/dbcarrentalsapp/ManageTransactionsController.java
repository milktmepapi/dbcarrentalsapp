package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

public class ManageTransactionsController {
    private ManageTransactionsView view;
    private Stage stage;

    public ManageTransactionsController(ManageTransactionsView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    private void setupActions() {
        view.rentalsButton.setOnAction(e -> openRentals());
        view.cancellationsButton.setOnAction(e -> openCancellations());
        view.violationsButton.setOnAction(e -> openViolations());
        view.returnsButton.setOnAction(e -> openReturns());
        view.backButton.setOnAction(e -> returnToUser());
    }

    // ===== Button Actions =====

    private void openRentals() {
        System.out.println("Opening Rentals...");
        // Example placeholder:
        // RentalsView rentalsView = new RentalsView(stage);
        // RentalsController rentalsController = new RentalsController(rentalsView, stage);
        // stage.setScene(rentalsView.getScene());
    }

    private void openCancellations() {
        System.out.println("Opening Rental Cancellations...");
        // future: navigate to RentalCancellationsView
    }

    private void openViolations() {
        System.out.println("Opening Rental Violations...");
        // future: navigate to RentalViolationsView
    }

    private void openReturns() {
        System.out.println("Opening Rental Returns...");
        // future: navigate to RentalReturnsView
    }

    private void returnToUser() {
        System.out.println("Returning to User Menu...");
        UserView userView = new UserView();
        UserController userController = new UserController(userView, stage);
        userController.setupActions();
        stage.setScene(userView.getScene());
    }
}
