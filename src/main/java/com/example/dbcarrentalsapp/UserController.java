package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

public class UserController {
    private UserView view;
    private Stage stage;

    public UserController(UserView view, Stage stage) {
        this.view = view;
        this.stage = stage;
    }

    public void setupActions() {
        view.records.setOnAction(e -> openRecords());
        view.transactions.setOnAction(e -> openTransactions());
        view.reports.setOnAction(e -> openReports());
        view.exit.setOnAction(e -> System.exit(0));
    }

    private void openRecords() {
        ManageRecordsView manageView = new ManageRecordsView();
        ManageRecordsController manageController = new ManageRecordsController(manageView, stage);
        stage.setScene(manageView.getScene());
    }

    private void openTransactions() {
        ManageTransactionsView manageTransView = new ManageTransactionsView(stage);
        ManageTransactionsController manageTransController = new ManageTransactionsController(manageTransView, stage);
        stage.setScene(manageTransView.getScene());
    }

    private void openReports() {
        ManageReportsView reportsView = new ManageReportsView(stage);
        ManageReportsController reportsController = new ManageReportsController(reportsView, stage);
        stage.setScene(reportsView.getScene());
    }
}
