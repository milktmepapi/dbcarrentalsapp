package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.JobRecord;

public class JobView {
    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<JobRecord> tableView;
    private final Scene scene;

    public JobView() {
        // ===== Background =====
        StackPane root = new StackPane();
        Image bgImage = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.jpg")
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // ===== Title =====
        Text title = new Text("MANAGE JOBS");
        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"),
                48
        );
        title.setFont(f1Font != null ? f1Font : Font.font("Arial Black", 48));
        title.setStyle(
                "-fx-fill: white; -fx-font-style: italic; -fx-font-weight: bold;" +
                        "-fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);"
        );
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(100, 0, 0, 0));
        root.getChildren().add(title);

        // ===== Search Bar =====
        searchField = new TextField();
        searchField.setPromptText("Search by id or department...");
        searchField.setPrefWidth(300);
        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(750);
        tableView.setPrefHeight(280);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");

        // Proper fix: keep table fully inside border box
        tableView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setPadding(new Insets(5, 8, 5, 8));

        TableColumn<JobRecord, String> jobIDCol = new TableColumn<>("Job ID");
        jobIDCol.setCellValueFactory(new PropertyValueFactory<>("jobId"));

        TableColumn<JobRecord, String> jobTitleCol = new TableColumn<>("Job Title");
        jobTitleCol.setCellValueFactory(new PropertyValueFactory<>("jobTitle"));

        TableColumn<JobRecord, String> jobDepartmentIDCol = new TableColumn<>("Job Department ID");
        jobDepartmentIDCol.setCellValueFactory(new PropertyValueFactory<>("jobDepartmentId"));

        TableColumn<JobRecord, String> jobSalaryCol = new TableColumn<>("Job Salary");
        jobSalaryCol.setCellValueFactory(new PropertyValueFactory<>("jobSalary"));

        tableView.getColumns().addAll(jobIDCol, jobTitleCol, jobDepartmentIDCol, jobSalaryCol);

        // ===== Buttons =====
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        deleteButton = new Button("Delete");
        returnButton = new Button("Return");

        addButton.getStyleClass().add("small-button");
        modifyButton.getStyleClass().add("small-button");
        deleteButton.getStyleClass().add("small-button");
        returnButton.getStyleClass().add("small-button");

        addButton.setPrefWidth(120);
        modifyButton.setPrefWidth(120);
        deleteButton.setPrefWidth(120);
        returnButton.setPrefWidth(120);

        HBox buttonBox = new HBox(15, addButton, modifyButton, deleteButton, returnButton);
        buttonBox.setAlignment(Pos.CENTER);

        // ===== Card Container =====
        VBox tableCard = new VBox(15, tableView, buttonBox);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(800);
        tableCard.setStyle(
                "-fx-background-color: rgba(25,25,35,0.85);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 2;" +
                        "-fx-overflow: hidden;"
        );

        // ===== Layout =====
        VBox layout = new VBox(30, searchBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 0, 0, 0));
        root.getChildren().add(layout);

        // ===== Scene =====
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    /**
     * Show Add Job popup — accepts DAO + callback
     **/
    public void showAddJobPopup(JobDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Job");

        Label jobIDLabel = new Label("Job ID:");
        TextField jobIDField = new TextField();
        jobIDField.setPromptText("e.g., ABC1234");

        Label jobTitleLabel = new Label("Job Title:");
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Enter Job Title");

        Label jobDepartmentIDLabel = new Label("Job Department ID:");
        TextField jobDepartmentIDField = new TextField();
        jobDepartmentIDField.setPromptText("Enter Job Department ID");

        Label jobSalaryLabel = new Label("Job Salary:");
        TextField jobSalaryField = new TextField();
        jobSalaryField.setPromptText("Enter Job Salary");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String jobID = jobIDField.getText().trim();
            String jobTitle = jobTitleField.getText().trim();
            String jobDepartmentID = jobDepartmentIDField.getText().trim();
            String jobSalaryText = jobSalaryField.getText().trim();

            if (jobID.isEmpty() || jobTitle.isEmpty() || jobDepartmentID.isEmpty() || jobSalaryText.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            // Parse salary as double with error handling
            try {
                double jobSalary = Double.parseDouble(jobSalaryText);
                boolean success = dao.addJob(jobID, jobTitle, jobDepartmentID, jobSalary);
                if (success) {
                    message.setText("Added successfully!");
                    message.setStyle("-fx-text-fill: lightgreen;");
                    reloadCallback.run(); // refresh table in controller
                    popup.close();
                } else {
                    message.setText("Failed: Duplicate ID or Department ID.");
                    message.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                message.setText("Please enter a valid salary number!");
                message.setStyle("-fx-text-fill: orange;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                jobIDLabel, jobIDField,
                jobTitleLabel, jobTitleField,
                jobDepartmentIDLabel, jobDepartmentIDField,
                jobSalaryLabel, jobSalaryField,
                buttonBox, message
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 320, 320);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popup.showAndWait();
    }

    /**
     * Show Modify Job popup — allows editing jobs only.
     **/
    public void showModifyJobPopup(JobDAO dao, JobRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a job to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Job");

        Label jobIDLabel = new Label("Job ID:");
        TextField jobIDField = new TextField(selected.getJobId());
        jobIDField.setEditable(false);
        jobIDField.setStyle("-fx-opacity: 0.7;");

        Label jobTitleLabel = new Label("Job Title:");
        TextField jobTitleField = new TextField(selected.getJobTitle());

        Label jobDepartmentIDLabel= new Label("Job Department ID:");
        TextField jobDepartmentIDField = new TextField(selected.getJobDepartmentId());

        Label jobSalaryLabel= new Label("Job Salary:");
        TextField jobSalaryField = new TextField();
        jobSalaryField.setText(Double.toString(selected.getJobSalary()));

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        JobRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String jobID = jobIDField.getText().trim();
            String jobTitle = jobTitleField.getText().trim();
            String jobDepartmentID = jobDepartmentIDField.getText().trim();
            String jobSalaryText = jobSalaryField.getText().trim();

            if (jobID.isEmpty() || jobTitle.isEmpty() || jobDepartmentID.isEmpty() || jobSalaryText.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            // Parse salary as double with error handling
            try {
                double jobSalary = Double.parseDouble(jobSalaryText);
                boolean success = dao.updateJob(finalSelected.getJobId(), jobTitle, jobDepartmentID, jobSalary);
                if (success) {
                    message.setText("Updated successfully!");
                    message.setStyle("-fx-text-fill: lightgreen;");
                    reloadCallback.run(); // refresh the table
                    popup.close();
                } else {
                    message.setText("Failed: Duplicate or database error.");
                    message.setStyle("-fx-text-fill: red;");
                }
            } catch (NumberFormatException ex) {
                message.setText("Please enter a valid salary number!");
                message.setStyle("-fx-text-fill: orange;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                jobIDLabel, jobIDField,
                jobTitleLabel, jobTitleField,
                jobDepartmentIDLabel, jobDepartmentIDField,
                jobSalaryLabel, jobSalaryField,
                buttonBox, message
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 320, 320);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popup.showAndWait();
    }

    /** Reusable Success Popup **/
    public void showSuccessPopup(String title, String messageText) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);

        Label msg = new Label(messageText);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("small-button");
        okBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(15, msg, okBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-background-radius: 10;");

        Scene scene = new Scene(layout, 300, 150);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(scene);
        scene.getRoot().requestFocus(); // prevent OK from pre-focusing
        popup.showAndWait();
    }

    /** Confirmation Popup (for deletions) **/
    public boolean showConfirmPopup(JobRecord selected) {
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a job to delete.");
            return false;
        }

        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label idLabel = new Label("Job ID:");
        TextField idField = new TextField(selected.getJobId());
        idField.setEditable(false);
        idField.setStyle("-fx-opacity: 0.7;");

        Label titleLabel = new Label("Job Title:");
        TextField titleField = new TextField(selected.getJobTitle());
        titleField.setEditable(false);
        titleField.setStyle("-fx-opacity: 0.7;");

        Label departmentLabel = new Label("Department ID:");
        TextField departmentField = new TextField(selected.getJobDepartmentId());
        departmentField.setEditable(false);
        departmentField.setStyle("-fx-opacity: 0.7;");

        Label salaryLabel = new Label("Salary:");
        TextField salaryField = new TextField(String.valueOf(selected.getJobSalary()));
        salaryField.setEditable(false);
        salaryField.setStyle("-fx-opacity: 0.7;");

        Label message = new Label("Are you sure you want to delete this job?");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        message.setWrapText(true);

        Button yesBtn = new Button("Yes");
        Button noBtn = new Button("Cancel");
        yesBtn.getStyleClass().add("small-button");
        noBtn.getStyleClass().add("small-button");

        yesBtn.setOnAction(e -> {
            confirmed[0] = true;
            popup.close();
        });
        noBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, yesBtn, noBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        VBox box = new VBox(12,
                idLabel, idField,
                titleLabel, titleField,
                departmentLabel, departmentField,
                salaryLabel, salaryField,
                message,
                buttonBox
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 340, 350);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus(); // prevent field focus
        popup.showAndWait();

        return confirmed[0];
    }

    public Scene getScene() {
        return scene;
    }
}