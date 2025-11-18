package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.CarUtilizationReport;

public class CarUtilizationReportView {
    public TableView<CarUtilizationReport> tableView;
    public Button returnButton, filterButton;
    public TextField searchField;
    private final Scene scene;

    public CarUtilizationReportView() {

        // ===== Initialize TableView FIRST =====
        tableView = new TableView<>();

        // ===== Background =====
        StackPane root = new StackPane();
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/mclaren_speedtail_2-1920x1080.jpg"));
        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false)
        );
        root.setBackground(new Background(backgroundImage));

        // ===== Title =====
        Text title = new Text("MANAGE CAR UTILIZATION");
        title.setStyle("-fx-font-size: 48px; -fx-fill: white; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(50, 0, 0, 0));
        root.getChildren().add(title);

        // ===== Search Bar =====
        searchField = new TextField();
        searchField.setPromptText("Search car plate number or model...");
        searchField.setPrefWidth(300);

        filterButton = new Button("Filter");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table Columns =====

        TableColumn<CarUtilizationReport, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<CarUtilizationReport, String> carPlateNumberCol = new TableColumn<>("Car Plate Number");
        carPlateNumberCol.setCellValueFactory(new PropertyValueFactory<>("carPlateNumber"));

        TableColumn<CarUtilizationReport, String> carBrandCol = new TableColumn<>("Car Brand");
        carBrandCol.setCellValueFactory(new PropertyValueFactory<>("carBrand"));

        TableColumn<CarUtilizationReport, String> carModelCol = new TableColumn<>("Car Model");
        carModelCol.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        TableColumn<CarUtilizationReport, String> carTransmissionCol = new TableColumn<>("Car Transmission");
        carTransmissionCol.setCellValueFactory(new PropertyValueFactory<>("carTransmission"));

        TableColumn<CarUtilizationReport, Integer> totalCol = new TableColumn<>("Total Rentals");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalRentals"));

        TableColumn<CarUtilizationReport, Integer> totalDaysCol = new TableColumn<>("Total Rental Days");
        totalDaysCol.setCellValueFactory(new PropertyValueFactory<>("totalRentalDays"));

        TableColumn<CarUtilizationReport, Double> utilizationRateCol = new TableColumn<>("Utilization Rate");
        utilizationRateCol.setCellValueFactory(new PropertyValueFactory<>("utilizationRate"));

        tableView.getColumns().addAll(branchCol, carPlateNumberCol, carModelCol, carBrandCol, carTransmissionCol, totalCol, totalDaysCol, utilizationRateCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        // ===== Return button =====
        returnButton = new Button("Return");
        returnButton.setPrefWidth(120);

        HBox buttonBox = new HBox(15, returnButton);
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
        layout.setPadding(new Insets(120, 0, 0, 0));

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

