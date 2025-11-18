package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.RentalRecord; // --- FIX: Import RentalRecord
// --- FIX: ReturnRecord is no longer needed here unless for 'viewReceipt'
// import model.ReturnRecord;

import java.time.LocalDateTime; // --- FIX: Import LocalDateTime for columns
import java.util.List;
// import java.util.Optional; // --- FIX: No longer needed

public class ReturnView {

    public Button backButton, returnButton, filterButton;
    public TextField searchField;
    // --- FIX: The TableView must hold RentalRecord objects
    public TableView<RentalRecord> tableView;
    private final Scene scene;
    private ReturnController controller;

    public ReturnView() {

// ===== Background =====
        StackPane root = new StackPane();
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/audi_r_zero_concept_black-normal.png"));
        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false)
        );
        root.setBackground(new Background(backgroundImage));

        // ===== Title =====
        Text title = new Text("MANAGE RETURN");
        Font f1Font = Font.loadFont(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 48);
        title.setFont(f1Font != null ? f1Font : Font.font("Arial Black", 48));
        title.setStyle("-fx-fill: white; -fx-font-style: italic; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);");

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(100, 0, 0, 0));
        root.getChildren().add(title);

        backButton = new Button("Back");
        backButton.getStyleClass().add("small-button");
        backButton.setPrefWidth(120);
        searchField = new TextField();
        // --- FIX: Update prompt text to reflect new data
        searchField.setPromptText("Search Rental ID, Renter DL, or Car Plate...");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");
        filterButton.setPrefWidth(120);
        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // --- FIX: The TableView must hold RentalRecord objects
        tableView = new TableView<RentalRecord>();
        tableView.setPrefWidth(750);
        tableView.setPrefHeight(280);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");
        tableView.setPlaceholder(new Label("No rental records found"));

        tableView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setPadding(new Insets(5, 8, 5, 8));


        // --- FIX: Columns must be for RentalRecord, not ReturnRecord
        TableColumn<RentalRecord, String> idCol = new TableColumn<>("Rental ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalId"));

        TableColumn<RentalRecord, String> rentalIDCol = new TableColumn<>("Car Plate");
        rentalIDCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("carPlateNumber"));

        TableColumn<RentalRecord, String> staffIdCol = new TableColumn<>("Renter DL");
        staffIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("renterDlNumber"));

        TableColumn<RentalRecord, LocalDateTime> expectedReturnCol = new TableColumn<>("Expected Return");
        expectedReturnCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expectedReturnDateTime"));

        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalStatus"));

        // --- FIX: Removed the "View Receipt" column.
        // The receipt is now shown *after* clicking "Process Return",
        // not for items already in the list.

        // --- FIX: Add the new columns to the table
        tableView.getColumns().clear(); // Clear old columns if any
        tableView.getColumns().addAll(idCol, rentalIDCol, staffIdCol, expectedReturnCol, statusCol);

        returnButton = new Button("Process Return");
        returnButton.getStyleClass().add("small-button");
        returnButton.setPrefWidth(120);
        HBox buttonBox = new HBox(15, returnButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

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

        VBox layout = new VBox(30, searchBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140,0,0,0));
        root.getChildren().add(layout);

        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() { return scene; }

    // --- FIX: This method must accept List<RentalRecord>
    public void refreshTable(List<RentalRecord> data) {
        tableView.getItems().setAll(data);
    }

    // --- FIX: Add this method for the controller to get the selected item
    public RentalRecord getSelectedRecord() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    // --- FIX: Add this method so the controller can register itself
    public void setController(ReturnController controller) {
        this.controller = controller;
    }

    // --- Getters for buttons ---
    public Button getBackButton() {
        return backButton;
    }

    public Button getReturnButton() {
        return returnButton;
    }
}

