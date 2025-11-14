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
import model.RenterRecord;

public class RenterView {

    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<RenterRecord> tableView;
    private final Scene scene;

    public RenterView() {

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
        Text title = new Text("MANAGE RENTERS");
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
        tableView.setPrefWidth(900);
        tableView.setPrefHeight(300);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");

        // match LocationView approach so table fills card nicely
        tableView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setPadding(new Insets(5, 8, 5, 8));

        TableColumn<RenterRecord, String> idCol = new TableColumn<>("DL Number");
        idCol.setCellValueFactory(new PropertyValueFactory<>("renterDlNumber"));

        TableColumn<RenterRecord, String> fnCol = new TableColumn<>("First Name");
        fnCol.setCellValueFactory(new PropertyValueFactory<>("renterFirstName"));

        TableColumn<RenterRecord, String> lnCol = new TableColumn<>("Last Name");
        lnCol.setCellValueFactory(new PropertyValueFactory<>("renterLastName"));

        TableColumn<RenterRecord, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("renterPhoneNumber"));

        TableColumn<RenterRecord, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("renterEmailAddress"));

        tableView.getColumns().addAll(idCol, fnCol, lnCol, phoneCol, emailCol);

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

        // ===== Card =====
        VBox tableCard = new VBox(15, tableView, buttonBox);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(950);
        tableCard.setStyle(
                "-fx-background-color: rgba(25,25,35,0.85);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 2;"
        );

        VBox layout = new VBox(30, searchBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 0, 0, 0));

        root.getChildren().add(layout);

        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }


    /* ===========================
       POPUPS (ADD / MODIFY / DELETE)
       =========================== */

    public void showAddPopup(RenterDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Renter");

        // ===== LABELS + TEXTFIELDS (same style as Location Add Popup) =====
        Label idLabel = new Label("Driver's License Number:");
        TextField idField = new TextField();
        idField.setPromptText("e.g., LL000000001");

        Label fnLabel = new Label("First Name:");
        TextField fnField = new TextField();
        fnField.setPromptText("Enter First Name");

        Label lnLabel = new Label("Last Name:");
        TextField lnField = new TextField();
        lnField.setPromptText("Enter Last Name");

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Enter Phone Number");

        Label emailLabel = new Label("Email Address:");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter Email Address");

        // ===== BUTTONS =====
        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        // ===== LOGIC =====
        addBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String fn = fnField.getText().trim();
            String ln = lnField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (id.isEmpty() || fn.isEmpty() || ln.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.addRenter(
                    new RenterRecord(id, fn, ln, phone, email)
            );

            if (success) {
                reloadCallback.run();
                popup.close();
                showSuccessPopup("Success", "Renter added successfully!");
            } else {
                message.setText("Failed: Duplicate DL Number.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        // ===== LAYOUT (EXACT same structure as Location Add Popup) =====
        VBox box = new VBox(
                12,
                idLabel, idField,
                fnLabel, fnField,
                lnLabel, lnField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                new HBox(10, addBtn, cancelBtn),
                message
        );

        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 350, 480);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }

    public void showModifyPopup(RenterDAO dao, RenterRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a renter to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Renter");

        // --- FIELDS (FOLLOWING LOCATIONVIEW MODIFY EXACTLY) ---
        Label dlLabel = new Label("Driver's License Number:");
        TextField dlField = new TextField(selected.getRenterDlNumber());
        dlField.setEditable(false);
        dlField.setStyle("-fx-opacity: 0.7;");

        Label firstLabel = new Label("First Name:");
        TextField firstField = new TextField(selected.getRenterFirstName());

        Label lastLabel = new Label("Last Name:");
        TextField lastField = new TextField(selected.getRenterLastName());

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField(selected.getRenterPhoneNumber());

        Label emailLabel = new Label("Email Address:");
        TextField emailField = new TextField(selected.getRenterEmailAddress());

        // Buttons
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        saveBtn.setOnAction(e -> {
            String first = firstField.getText().trim();
            String last = lastField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (first.isEmpty() || last.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            // Create UPDATED renter object for DAO
            RenterRecord updated = new RenterRecord(
                    dlField.getText(),   // renter_dl_number
                    first,
                    last,
                    phone,
                    email
            );

            boolean success = dao.updateRenter(updated);

            if (success) {
                reloadCallback.run();
                popup.close();
                showSuccessPopup("Updated", "Renter updated successfully!");
            } else {
                message.setText("Failed: Duplicate or database error.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        // --- LAYOUT EXACT SAME AS LOCATION ---
        VBox box = new VBox(12,
                dlLabel, dlField,
                firstLabel, firstField,
                lastLabel, lastField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                new HBox(10, saveBtn, cancelBtn),
                message
        );

        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene popupScene = new Scene(box, 350, 450);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }


    public boolean showConfirmPopup(RenterRecord selected) {
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a renter to delete.");
            return false;
        }

        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        // ===== LABEL + READ-ONLY FIELDS (matching LocationView) =====
        Label idLabel = new Label("DL Number:");
        TextField idField = new TextField(selected.getRenterDlNumber());
        idField.setEditable(false);
        idField.setStyle("-fx-opacity: 0.7;");

        Label fnLabel = new Label("First Name:");
        TextField fnField = new TextField(selected.getRenterFirstName());
        fnField.setEditable(false);
        fnField.setStyle("-fx-opacity: 0.7;");

        Label lnLabel = new Label("Last Name:");
        TextField lnField = new TextField(selected.getRenterLastName());
        lnField.setEditable(false);
        lnField.setStyle("-fx-opacity: 0.7;");

        Label phoneLabel = new Label("Phone Number:");
        TextField phoneField = new TextField(selected.getRenterPhoneNumber());
        phoneField.setEditable(false);
        phoneField.setStyle("-fx-opacity: 0.7;");

        Label emailLabel = new Label("Email Address:");
        TextField emailField = new TextField(selected.getRenterEmailAddress());
        emailField.setEditable(false);
        emailField.setStyle("-fx-opacity: 0.7;");

        // ===== MESSAGE =====
        Label message = new Label("Are you sure you want to delete this renter?");
        message.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        message.setWrapText(true);

        // ===== BUTTONS =====
        Button yesBtn = new Button("Yes");
        Button cancelBtn = new Button("Cancel");

        yesBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        yesBtn.setOnAction(e -> {
            confirmed[0] = true;
            popup.close();
        });
        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, yesBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        // ===== POPUP LAYOUT (exact same vertical structure as LocationView) =====
        VBox layout = new VBox(
                12,
                idLabel, idField,
                fnLabel, fnField,
                lnLabel, lnField,
                phoneLabel, phoneField,
                emailLabel, emailField,
                message,
                buttonBox
        );

        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: rgba(30,30,30,0.95); -fx-background-radius: 10;");

        Scene scene = new Scene(layout, 360, 500);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(scene);
        scene.getRoot().requestFocus();
        popup.showAndWait();

        return confirmed[0];
    }

    public void showSuccessPopup(String title, String text) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        Label msg = new Label(text);
        msg.setStyle("-fx-text-fill: white;");

        Button ok = new Button("OK");
        ok.getStyleClass().add("small-button");
        ok.setOnAction(e -> popup.close());

        VBox layout = new VBox(10, msg, ok);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(20,20,20,0.95); -fx-background-radius: 10;");

        Scene scene = new Scene(layout, 300, 150);
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