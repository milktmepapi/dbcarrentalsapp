package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.RentalRecord;

public class RentalView {

    public Button addButton, modifyButton, viewButton, returnButton;
    public TextField searchField;
    public TableView<RentalRecord> tableView;
    private final Scene scene;

    public RentalView() {

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
        Text title = new Text("MANAGE RENTALS");
        Font f1Font = Font.loadFont(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 48);
        title.setFont(f1Font != null ? f1Font : Font.font("Arial Black", 48));
        title.setStyle("-fx-fill: white; -fx-font-style: italic; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);");

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(100, 0, 0, 0));
        root.getChildren().add(title);

        // ===== Search Bar =====
        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        searchField = new TextField();
        searchField.setPromptText("Search rentals...");
        searchField.setPrefWidth(200);
        searchField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Button filterButton = new Button("Search");
        filterButton.getStyleClass().add("small-button");

        HBox searchBox = new HBox(15, searchLabel, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(750);
        tableView.setPrefHeight(280);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");
        tableView.setPlaceholder(new Label("No rental records found"));

        tableView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setPadding(new Insets(5, 8, 5, 8));

        TableColumn<RentalRecord, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));

        TableColumn<RentalRecord, String> dlCol = new TableColumn<>("Renter DL");
        dlCol.setCellValueFactory(new PropertyValueFactory<>("renterDlNumber"));

        TableColumn<RentalRecord, String> plateCol = new TableColumn<>("Car Plate");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("carPlateNumber"));

        TableColumn<RentalRecord, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchId"));

        TableColumn<RentalRecord, String> pickupCol = new TableColumn<>("Pickup (Exp.)");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("expectedPickupDateTime"));

        TableColumn<RentalRecord, String> returnCol = new TableColumn<>("Return (Exp.)");
        returnCol.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDateTime"));

        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("rentalStatus"));

        TableColumn<RentalRecord, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));

        tableView.getColumns().addAll(
                idCol, dlCol, plateCol, branchCol, pickupCol, returnCol, statusCol, paymentCol
        );

        // ===== Buttons =====
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        viewButton = new Button("View");
        returnButton = new Button("Return");

        addButton.getStyleClass().add("small-button");
        modifyButton.getStyleClass().add("small-button");
        viewButton.getStyleClass().add("small-button");
        returnButton.getStyleClass().add("small-button");

        addButton.setPrefWidth(120);
        modifyButton.setPrefWidth(120);
        viewButton.setPrefWidth(120);
        returnButton.setPrefWidth(120);

        HBox buttonBox = new HBox(15, addButton, modifyButton, viewButton, returnButton);

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

    public Scene getScene() {
        return scene;
    }
}