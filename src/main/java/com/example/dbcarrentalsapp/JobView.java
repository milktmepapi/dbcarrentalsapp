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
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png")
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
        searchField.setPromptText("Search by attribute...");
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
        jobIDLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField jobIDField = new TextField();
        jobIDField.setPromptText("e.g., ADM004");
        jobIDField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label jobTitleLabel = new Label("Job Title:");
        jobTitleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField jobTitleField = new TextField();
        jobTitleField.setPromptText("Enter Job Title");
        jobTitleField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label jobDepartmentIDLabel = new Label("Job Department ID:");
        jobDepartmentIDLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> jobDepartmentIDComboBox = new ComboBox<>();
        jobDepartmentIDComboBox.setPromptText("Select Department ID");
        jobDepartmentIDComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate department IDs
        DepartmentDAO departmentDAO = new DepartmentDAO();
        jobDepartmentIDComboBox.getItems().addAll(departmentDAO.getAllDepartmentIds());

        Label jobSalaryLabel = new Label("Job Salary:");
        jobSalaryLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField jobSalaryField = new TextField();
        jobSalaryField.setPromptText("Enter Job Salary");
        jobSalaryField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String jobID = jobIDField.getText().trim();
            String jobTitle = jobTitleField.getText().trim();
            String jobDepartmentID = jobDepartmentIDComboBox.getValue();
            String jobSalaryText = jobSalaryField.getText().trim();

            if (jobID.isEmpty() || jobTitle.isEmpty() || jobDepartmentID == null || jobSalaryText.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            try {
                double jobSalary = Double.parseDouble(jobSalaryText);
                boolean success = dao.addJob(jobID, jobTitle, jobDepartmentID, jobSalary);
                if (success) {
                    reloadCallback.run();
                    popup.close();
                    showSuccessPopup("Success", "Job added successfully!");
                } else {
                    message.setText("Failed: Duplicate ID or invalid department ID.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                message.setText("Please enter a valid salary number!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                jobIDLabel, jobIDField,
                jobTitleLabel, jobTitleField,
                jobDepartmentIDLabel, jobDepartmentIDComboBox,
                jobSalaryLabel, jobSalaryField,
                buttonBox, message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 350, 400);
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
        jobIDLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField jobIDField = new TextField(selected.getJobId());
        jobIDField.setEditable(false);
        jobIDField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label jobTitleLabel = new Label("Job Title:");
        jobTitleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField jobTitleField = new TextField(selected.getJobTitle());
        jobTitleField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label jobDepartmentIDLabel= new Label("Job Department ID:");
        jobDepartmentIDLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> jobDepartmentIDComboBox = new ComboBox<>(); // CHANGED TO COMBOBOX
        jobDepartmentIDComboBox.setPromptText("Select Department ID");
        jobDepartmentIDComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate department IDs and set current value
        DepartmentDAO departmentDAO = new DepartmentDAO();
        jobDepartmentIDComboBox.getItems().addAll(departmentDAO.getAllDepartmentIds());
        jobDepartmentIDComboBox.setValue(selected.getJobDepartmentId());

        Label jobSalaryLabel= new Label("Job Salary:");
        jobSalaryLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField jobSalaryField = new TextField();
        jobSalaryField.setText(Double.toString(selected.getJobSalary()));
        jobSalaryField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        JobRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String jobID = jobIDField.getText().trim();
            String jobTitle = jobTitleField.getText().trim();
            String jobDepartmentID = jobDepartmentIDComboBox.getValue(); // CHANGED TO COMBOBOX
            String jobSalaryText = jobSalaryField.getText().trim();

            if (jobID.isEmpty() || jobTitle.isEmpty() || jobDepartmentID == null || jobSalaryText.isEmpty()) { // CHANGED CHECK
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            // Parse salary as double with error handling
            try {
                double jobSalary = Double.parseDouble(jobSalaryText);
                boolean success = dao.updateJob(finalSelected.getJobId(), jobTitle, jobDepartmentID, jobSalary);
                if (success) {
                    reloadCallback.run(); // refresh the table
                    popup.close();
                    showSuccessPopup("Updated", "Job updated successfully!");
                } else {
                    message.setText("Failed: Database error.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                message.setText("Please enter a valid salary number!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                jobIDLabel, jobIDField,
                jobTitleLabel, jobTitleField,
                jobDepartmentIDLabel, jobDepartmentIDComboBox, // CHANGED TO COMBOBOX
                jobSalaryLabel, jobSalaryField,
                buttonBox, message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 350, 400);
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
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        msg.setWrapText(true);

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("small-button");
        okBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20;");
        okBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(20, msg, okBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #4CAF50, #8BC34A); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene scene = new Scene(layout, 320, 160);
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
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField idField = new TextField(selected.getJobId());
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label titleLabel = new Label("Job Title:");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField titleField = new TextField(selected.getJobTitle());
        titleField.setEditable(false);
        titleField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label departmentLabel = new Label("Department ID:");
        departmentLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField departmentField = new TextField(selected.getJobDepartmentId());
        departmentField.setEditable(false);
        departmentField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label salaryLabel = new Label("Salary:");
        salaryLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField salaryField = new TextField(String.valueOf(selected.getJobSalary()));
        salaryField.setEditable(false);
        salaryField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label message = new Label("Are you sure you want to delete this job?");
        message.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        message.setWrapText(true);

        Button yesBtn = new Button("Yes, Delete");
        Button noBtn = new Button("Cancel");
        yesBtn.getStyleClass().add("small-button");
        noBtn.getStyleClass().add("small-button");
        yesBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        noBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        yesBtn.setOnAction(e -> {
            confirmed[0] = true;
            popup.close();
        });
        noBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, yesBtn, noBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                idLabel, idField,
                titleLabel, titleField,
                departmentLabel, departmentField,
                salaryLabel, salaryField,
                message,
                buttonBox
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #ff4444, #ff6b6b); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 360, 450);
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