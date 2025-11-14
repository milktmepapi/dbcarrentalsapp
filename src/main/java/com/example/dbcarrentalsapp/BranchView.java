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
import model.BranchRecord;

public class BranchView {

    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<BranchRecord> tableView;
    private final Scene scene;

    public BranchView() {
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
        Text title = new Text("MANAGE BRANCHES");
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
        searchField.setPromptText("Search by name...");
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

        TableColumn<BranchRecord, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        TableColumn<BranchRecord, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<BranchRecord, String> emailAddressCol = new TableColumn<>("Email Address");
        emailAddressCol.setCellValueFactory(new PropertyValueFactory<>("branchEmailAddress"));

        TableColumn<BranchRecord, String> locationIDCol = new TableColumn<>("Location ID");
        locationIDCol.setCellValueFactory(new PropertyValueFactory<>("branchLocationID"));

        tableView.getColumns().addAll(idCol, nameCol, emailAddressCol, locationIDCol);

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

    /** Popup for adding a new branch **/
    public void showAddBranchPopup(BranchDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Branch");

        Label idLabel = new Label("Branch ID:");
        TextField idField = new TextField();
        idField.setPromptText("e.g., BEN001");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter Name");

        Label emailLabel = new Label("Email Address:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");

        Label locationIDLabel = new Label("Location ID:");
        TextField locationIDField = new TextField();
        locationIDField.setPromptText("Enter Location ID");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String location = locationIDField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || location.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.addBranch(id, name, email, location);
            if (success) {
                reloadCallback.run();
                popup.close();
                showSuccessPopup("Success", "Branch added successfully!");
            } else {
                message.setText("Failed: Duplicate ID or Name + Location.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        VBox box = new VBox(12,
                idLabel, idField,
                nameLabel, nameField,
                emailLabel, emailField,
                locationIDLabel, locationIDField,
                new HBox(10, addBtn, cancelBtn),
                message
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 320, 320);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus(); // prevent ID field auto-focus
        popup.showAndWait();
    }

    /** Popup for modifying existing branch **/
    public void showModifyBranchPopup(BranchDAO dao, BranchRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a branch to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Branch");

        Label idLabel = new Label("Branch ID:");
        TextField idField = new TextField(selected.getBranchId());
        idField.setEditable(false);
        idField.setStyle("-fx-opacity: 0.7;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(selected.getBranchName());

        Label emailLabel = new Label("Email Address:");
        TextField emailField = new TextField(selected.getBranchEmailAddress());

        Label locationIDLabel = new Label("Location ID:");
        TextField locationIDField = new TextField(selected.getBranchLocationId());

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        BranchRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String location = locationIDField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || location.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.updateBranch(finalSelected.getBranchId(), name, email, location);
            if (success) {
                reloadCallback.run();
                popup.close();
                showSuccessPopup("Updated", "Branch updated successfully!");
            } else {
                message.setText("Failed: Duplicate or database error.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        VBox box = new VBox(12,
                idLabel, idField,
                nameLabel, nameField,
                emailLabel, emailField,
                locationIDLabel, locationIDField,
                new HBox(10, saveBtn, cancelBtn),
                message
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
    public boolean showConfirmPopup(BranchRecord selected) {
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a branch to delete.");
            return false;
        }

        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label idLabel = new Label("Branch ID:");
        TextField idField = new TextField(selected.getBranchId());
        idField.setEditable(false);
        idField.setStyle("-fx-opacity: 0.7;");

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(selected.getBranchName());
        nameField.setEditable(false);
        nameField.setStyle("-fx-opacity: 0.7;");

        Label emailLabel = new Label("Email Address:");
        TextField emailField = new TextField(selected.getBranchEmailAddress());
        emailField.setEditable(false);
        emailField.setStyle("-fx-opacity: 0.7;");

        Label locationIDLabel = new Label("Location ID:");
        TextField locationIDField = new TextField(selected.getBranchLocationId());
        locationIDField.setEditable(false);
        locationIDField.setStyle("-fx-opacity: 0.7;");

        Label message = new Label("Are you sure you want to delete this branch?");
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
                nameLabel, nameField,
                emailLabel, emailField,
                locationIDLabel, locationIDField,
                message,
                buttonBox
        );
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 340, 380);
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
