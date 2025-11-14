package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        searchField.setPromptText("Search by attribute...");
        searchField.setPrefWidth(300);
        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900); // Wider table
        tableView.setPrefHeight(300);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

        // ===== Layout =====
        VBox layout = new VBox(30, searchBox, tableView, buttonBox); // more spacing between sections
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
            String jobSalary = jobSalaryField.getText().trim();

            if (jobID.isEmpty() || jobTitle.isEmpty() || jobDepartmentID.isEmpty() || jobSalary.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.addJob(jobID, jobTitle, jobDepartmentID, Double.valueOf(jobSalary));
            if (success) {
                message.setText("Added successfully!");
                message.setStyle("-fx-text-fill: lightgreen;");
                reloadCallback.run(); // refresh table in controller
                popup.close();
            } else {
                message.setText("Failed: Duplicate ID or Department ID.");
                message.setStyle("-fx-text-fill: red;");
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a location to modify.");
            alert.showAndWait();
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Staff");

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
            String jobSalary = jobSalaryField.getText().trim();

            if (jobID.isEmpty() || jobTitle.isEmpty() || jobDepartmentID.isEmpty() || jobSalary.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.updateJob(finalSelected.getJobId(), jobTitle, jobDepartmentID, Double.valueOf(jobSalary));
            if (success) {
                message.setText("Updated successfully!");
                message.setStyle("-fx-text-fill: lightgreen;");
                reloadCallback.run(); // refresh the table
                popup.close();
            } else {
                message.setText("Failed: Duplicate or database error.");
                message.setStyle("-fx-text-fill: red;");
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

    public Scene getScene() {
        return scene;
    }
}
