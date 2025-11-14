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
import model.StaffRecord;

public class StaffView {
    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<StaffRecord> tableView;
    private final Scene scene;

    public StaffView() {
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
        Text title = new Text("MANAGE STAFF");
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
        searchField.setPromptText("Search by id or branch...");
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

        TableColumn<StaffRecord, String> staffIDCol = new TableColumn<>("Staff ID");
        staffIDCol.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        TableColumn<StaffRecord, String> staffFirstNameCol = new TableColumn<>("Staff First Name");
        staffFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("staffFirstName"));

        TableColumn<StaffRecord, String> staffLastNameCol = new TableColumn<>("Staff Last Name");
        staffLastNameCol.setCellValueFactory(new PropertyValueFactory<>("staffLastName"));

        TableColumn<StaffRecord, String> staffJobIdCol = new TableColumn<>("Job ID");
        staffJobIdCol.setCellValueFactory(new PropertyValueFactory<>("staffJobId"));

        TableColumn<StaffRecord, String> staffBranchIDCol = new TableColumn<>("Branch ID");
        staffBranchIDCol.setCellValueFactory(new PropertyValueFactory<>("staffBranchId"));

        tableView.getColumns().addAll(staffIDCol, staffFirstNameCol, staffLastNameCol, staffJobIdCol, staffBranchIDCol);

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
     * Show Add Staff popup — accepts DAO + callback
     **/
    public void showAddStaffPopup(StaffDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Staff");

        Label staffIDLabel = new Label("Staff ID:");
        TextField staffIDField = new TextField();
        staffIDField.setPromptText("e.g., ABC1234");

        Label staffFirstNameLabel = new Label("Staff First Name:");
        TextField staffFirstNameField = new TextField();
        staffFirstNameField.setPromptText("Enter First Name");

        Label staffLastNameLabel = new Label("Staff Last Name:");
        TextField staffLastNameField = new TextField();
        staffLastNameField.setPromptText("Enter Last Name");

        Label staffJobIDLabel = new Label("Staff Job ID:");
        TextField staffJobIDField = new TextField();
        staffJobIDField.setPromptText("Enter Job ID");

        Label staffBranchIDLabel = new Label("Staff Branch ID:");
        TextField staffBranchIDField = new TextField();
        staffBranchIDField.setPromptText("e.g., BEN001");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String staffID = staffIDField.getText().trim();
            String staffFirstName = staffFirstNameField.getText().trim();
            String staffLastName = staffLastNameField.getText().trim();
            String staffJobID = staffJobIDField.getText().trim();
            String staffBranchID = staffBranchIDField.getText().trim();

            if (staffID.isEmpty() || staffFirstName.isEmpty() || staffLastName.isEmpty() || staffJobID.isEmpty() || staffBranchID.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.addStaff(staffID, staffFirstName, staffLastName, staffJobID, staffBranchID);
            if (success) {
                message.setText("Added successfully!");
                message.setStyle("-fx-text-fill: lightgreen;");
                reloadCallback.run(); // refresh table in controller
                popup.close();
            } else {
                message.setText("Failed: Duplicate ID or Branch ID.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                staffIDLabel, staffIDField,
                staffFirstNameLabel, staffFirstNameField,
                staffLastNameLabel, staffLastNameField,
                staffJobIDLabel, staffJobIDField,
                staffBranchIDLabel, staffBranchIDField,
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
     * Show Modify Staff popup — allows editing staff only.
     **/
    public void showModifyStaffPopup(StaffDAO dao, StaffRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a staff member to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Staff");

        Label staffIDLabel = new Label("Staff ID:");
        TextField staffIDField = new TextField(selected.getStaffId());
        staffIDField.setEditable(false);
        staffIDField.setStyle("-fx-opacity: 0.7;");

        Label staffFirstNameLabel = new Label("Staff First Name:");
        TextField staffFirstNameField = new TextField(selected.getStaffFirstName());

        Label staffLastNameLabel= new Label("Staff Last Name:");
        TextField staffLastNameField = new TextField(selected.getStaffLastName());

        Label staffJobIDLabel= new Label("Staff Job ID:");
        TextField staffJobIDField = new TextField(selected.getStaffJobId());

        Label staffBranchIDLabel= new Label("Staff Branch ID:");
        TextField staffBranchIDField = new TextField(String.valueOf(selected.getStaffBranchId()));

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        StaffRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String staffID = staffIDField.getText().trim();
            String staffFirstName = staffFirstNameField.getText().trim();
            String staffLastName = staffLastNameField.getText().trim();
            String staffJobId = staffJobIDField.getText().trim();
            String staffBranchId = staffBranchIDField.getText().trim();

            if (staffID.isEmpty() || staffFirstName.isEmpty() || staffLastName.isEmpty() || staffJobId.isEmpty() || staffBranchId.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.updateStaff(finalSelected.getStaffId(), staffFirstName, staffLastName, staffJobId, staffBranchId);
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
                staffIDLabel, staffIDField,
                staffFirstNameLabel, staffFirstNameField,
                staffLastNameLabel, staffLastNameField,
                staffJobIDLabel, staffJobIDField,
                staffBranchIDLabel, staffBranchIDField,
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
        popupScene.getRoot().requestFocus(); // prevent auto-selection
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
    public boolean showConfirmPopup(StaffRecord selected) {
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a staff member to delete.");
            return false;
        }

        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label idLabel = new Label("Staff ID:");
        TextField idField = new TextField(selected.getStaffId());
        idField.setEditable(false);
        idField.setStyle("-fx-opacity: 0.7;");

        Label firstNameLabel = new Label("First Name:");
        TextField firstNameField = new TextField(selected.getStaffFirstName());
        firstNameField.setEditable(false);
        firstNameField.setStyle("-fx-opacity: 0.7;");

        Label lastNameLabel = new Label("Last Name:");
        TextField lastNameField = new TextField(selected.getStaffLastName());
        lastNameField.setEditable(false);
        lastNameField.setStyle("-fx-opacity: 0.7;");

        Label jobIdLabel = new Label("Job ID:");
        TextField jobIdField = new TextField(selected.getStaffJobId());
        jobIdField.setEditable(false);
        jobIdField.setStyle("-fx-opacity: 0.7;");

        Label branchIdLabel = new Label("Branch ID:");
        TextField branchIdField = new TextField(selected.getStaffBranchId());
        branchIdField.setEditable(false);
        branchIdField.setStyle("-fx-opacity: 0.7;");

        Label message = new Label("Are you sure you want to delete this staff member?");
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
                firstNameLabel, firstNameField,
                lastNameLabel, lastNameField,
                jobIdLabel, jobIdField,
                branchIdLabel, branchIdField,
                message,
                buttonBox
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 340, 420);
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