package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import model.BranchReport;
/*
public class CarUtilizationView {
    public TableView<BranchReport> tableView;
    public Button returnButton, filterButton;
    public TextField searchField;
    private final Scene scene;

    public RentalsReportView() {

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
        Text title = new Text("MANAGE RENTALS BY BRANCH");
        title.setStyle("-fx-font-size: 48px; -fx-fill: white; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(50, 0, 0, 0));
        root.getChildren().add(title);

        // ===== Search Bar =====
        searchField = new TextField();
        searchField.setPromptText("Search rental ID or status...");
        searchField.setPrefWidth(300);

        filterButton = new Button("Filter");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table Columns =====

        TableColumn<BranchReport, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<BranchReport, String> typeCol = new TableColumn<>("Car Transmission");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("carTransmission"));

        TableColumn<BranchReport, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("rentalDuration"));

        TableColumn<BranchReport, Integer> totalCol = new TableColumn<>("Total Rentals");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalRentals"));

        tableView.getColumns().addAll(branchCol, typeCol, durationCol, totalCol);
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

 */
