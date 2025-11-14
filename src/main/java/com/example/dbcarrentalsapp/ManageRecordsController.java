package com.example.dbcarrentalsapp;

import javafx.stage.Stage;

public class ManageRecordsController {
    private final ManageRecordsView view;
    private final Stage stage;

    public ManageRecordsController(ManageRecordsView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    public void setupActions() {

        // Locations Button
        view.locations.setOnAction(e -> {
            LocationView locationView = new LocationView();
            LocationController locationController = new LocationController(locationView, stage);
            stage.setScene(locationView.getScene());
        });

        // Departments Button
        view.departments.setOnAction(e -> {
            DepartmentView departmentView = new DepartmentView();
            new DepartmentController(departmentView, stage);
            stage.setScene(departmentView.getScene());
        });

        // Car Button
        view.cars.setOnAction(e -> {
            CarView carView = new CarView();
            CarController carController = new CarController(carView, stage);
            stage.setScene(carView.getScene());
        });

        // Staff Button
        view.staffs.setOnAction(e -> {
            StaffView staffView = new StaffView();
            StaffController staffController = new StaffController(staffView, stage);
            stage.setScene(staffView.getScene());
        });

        // Jobs Button
        view.jobs.setOnAction(e -> {
            JobView jobView = new JobView();
            JobController jobController = new JobController(jobView, stage);
            stage.setScene(jobView.getScene());
        });

        // Renter Button
        view.renters.setOnAction(e -> {
            RenterView renterView = new RenterView();
            new RenterController(renterView, stage);
            stage.setScene(renterView.getScene());
        });

        // Branches Button
        // Branches Button
        view.branches.setOnAction(e -> {
            BranchView branchView = new BranchView();
            new BranchController(branchView, stage);
            stage.setScene(branchView.getScene());
        });

        // Return Button
        view.returns.setOnAction(e -> {
            UserView userView = new UserView();
            UserController userController = new UserController(userView, stage);
            userController.setupActions();
            stage.setScene(userView.getScene());
        });


    }
}
