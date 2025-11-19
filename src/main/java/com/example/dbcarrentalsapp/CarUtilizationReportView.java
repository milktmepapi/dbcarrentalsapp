package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import model.CarUtilizationReport;

public class CarUtilizationReportView {

    public TableView<CarUtilizationReport> tableView;
    public Button returnButton, filterButton;
    public TextField searchField;
    private final Scene scene;

    public CarUtilizationReportView() {

        // ===== TABLE (Revenue-style) =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setPrefHeight(300);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");  // same as Revenue

        // ===== BACKGROUND =====
        StackPane root = new StackPane();
        Image bgImage = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/audi_r_zero_concept_black-normal.png")
        );

        BackgroundImage bg = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false)
        );

        root.setBackground(new Background(bg));

        // ===== TITLE (kept same structure, revenue style) =====
        Text title = new Text("CAR UTILIZATION REPORT");
        title.setStyle("""
                -fx-fill: white;
                -fx-font-size: 48px;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);
        """);

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(80, 0, 0, 0));
        root.getChildren().add(title);

        // ===== SEARCH BAR =====
        searchField = new TextField();
        searchField.setPromptText("Search car plate or model...");
        searchField.setPrefWidth(300);
        searchField.getStyleClass().add("search-field");

        filterButton = new Button("Filter");
        filterButton.setPrefWidth(120);
        filterButton.getStyleClass().add("small-button");  // revenue-style button

        HBox searchRow = new HBox(10, searchField, filterButton);
        searchRow.setAlignment(Pos.CENTER);

        // ===== TABLE COLUMNS (unchanged logic) =====

        TableColumn<CarUtilizationReport, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<CarUtilizationReport, String> plateCol = new TableColumn<>("Plate No.");
        plateCol.setCellValueFactory(new PropertyValueFactory<>("carPlateNumber"));

        TableColumn<CarUtilizationReport, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("carBrand"));

        TableColumn<CarUtilizationReport, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        TableColumn<CarUtilizationReport, String> transCol = new TableColumn<>("Trans.");
        transCol.setCellValueFactory(new PropertyValueFactory<>("carTransmission"));

        TableColumn<CarUtilizationReport, Integer> totalRentalsCol = new TableColumn<>("Total Rentals");
        totalRentalsCol.setCellValueFactory(new PropertyValueFactory<>("totalRentals"));

        TableColumn<CarUtilizationReport, Integer> totalDaysCol = new TableColumn<>("No. Of Days");
        totalDaysCol.setCellValueFactory(new PropertyValueFactory<>("totalRentalDays"));

        TableColumn<CarUtilizationReport, Double> rateCol = new TableColumn<>("Util. Rate (%)");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("utilizationRate"));

        rateCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f%%", value));  // 2 decimal places + % sign
                }

                setAlignment(Pos.CENTER); // optional: center align like revenue table
            }
        });

        tableView.getColumns().addAll(
                branchCol, plateCol, brandCol, modelCol,
                transCol, totalRentalsCol, totalDaysCol, rateCol
        );

        // ===== RETURN BUTTON (revenue style) =====
        returnButton = new Button("Return");
        returnButton.setPrefWidth(140);
        returnButton.getStyleClass().add("small-button");

        HBox buttonRow = new HBox(returnButton);
        buttonRow.setAlignment(Pos.CENTER);

        // ===== TABLE CARD (exact same design as Revenue) =====
        VBox tableCard = new VBox(20, tableView, buttonRow);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(25));
        tableCard.setMaxWidth(1000);

        tableCard.setStyle("""
                -fx-background-color: rgba(25,25,35,0.85);
                -fx-background-radius: 15;
                -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);
                -fx-border-radius: 15;
                -fx-border-width: 2;
        """);

        // ===== LAYOUT =====
        VBox layout = new VBox(35, searchRow, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(150, 0, 0, 0));

        root.getChildren().add(layout);

        // ===== SCENE =====
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}