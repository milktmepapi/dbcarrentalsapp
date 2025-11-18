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
import model.DepartmentRecord;

public class DepartmentView {

    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<DepartmentRecord> tableView;
    private final Scene scene;

    public DepartmentView() {
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
        Text title = new Text("MANAGE DEPARTMENTS");
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
        tableView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setPadding(new Insets(5, 8, 5, 8));

        TableColumn<DepartmentRecord, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("departmentId"));

        TableColumn<DepartmentRecord, String> nameCol = new TableColumn<>("Department Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("departmentName"));

        tableView.getColumns().addAll(idCol, nameCol);

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
                        "-fx-border-width: 2;"
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

    /** Popup for adding a new department **/
    public void showAddDepartmentPopup(DepartmentDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Department");

        Label idLabel = new Label("Department ID:");
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField idField = new TextField();
        idField.setPromptText("e.g., DEPT_HR");
        idField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label nameLabel = new Label("Department Name:");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Department Name");
        nameField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        // Improved button styling with colors
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            if (!id.matches("^DEPT_[A-Za-z]{2,3}$")) {
                message.setText("Invalid ID format! Use DEPT_XX or DEPT_XXX (letters only).");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                return;
            }

            boolean success = dao.addDepartment(id, name);
            if (success) {
                reloadCallback.run();
                popup.close();
                showSuccessPopup("Success", "Department added successfully!");
            } else {
                message.setText("Failed: Duplicate ID or Name.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                idLabel, idField,
                nameLabel, nameField,
                buttonBox,
                message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 350, 280);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /** Popup for modifying existing department **/
    public void showModifyDepartmentPopup(DepartmentDAO dao, DepartmentRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a department to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Department");

        Label idLabel = new Label("Department ID:");
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField idField = new TextField(selected.getDepartmentId());
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label nameLabel = new Label("Department Name:");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField nameField = new TextField(selected.getDepartmentName());
        nameField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        // Improved button styling with colors
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        DepartmentRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                message.setText("Please fill in the field!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            boolean success = dao.updateDepartment(finalSelected.getDepartmentId(), name);
            if (success) {
                reloadCallback.run();
                popup.close();
                showSuccessPopup("Updated", "Department updated successfully!");
            } else {
                message.setText("Failed: Duplicate or database error.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                idLabel, idField,
                nameLabel, nameField,
                buttonBox,
                message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 350, 280);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /** Confirmation Popup (for deletions) **/
    public boolean showConfirmPopup(DepartmentRecord selected) {
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a department to delete.");
            return false;
        }

        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label idLabel = new Label("Department ID:");
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField idField = new TextField(selected.getDepartmentId());
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label nameLabel = new Label("Department Name:");
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField nameField = new TextField(selected.getDepartmentName());
        nameField.setEditable(false);
        nameField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label message = new Label("Are you sure you want to delete this department?");
        message.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        message.setWrapText(true);

        Button yesBtn = new Button("Yes, Delete");
        Button noBtn = new Button("Cancel");
        yesBtn.getStyleClass().add("small-button");
        noBtn.getStyleClass().add("small-button");

        // Improved button styling with colors
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
                nameLabel, nameField,
                message,
                buttonBox
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #ff4444, #ff6b6b); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 360, 320);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();

        return confirmed[0];
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
        scene.getRoot().requestFocus();
        popup.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}