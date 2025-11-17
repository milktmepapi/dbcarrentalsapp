package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import model.ReturnRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReturnView {

    public Button backButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<ReturnRecord> tableView;
    private final Scene scene;
    private ReturnController controller;

    public ReturnView() {

        StackPane root = new StackPane();
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/mclaren_speedtail_2-1920x1080.jpg"));
        root.setBackground(new Background(new BackgroundImage(bgImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, true, false))));

        Text title = new Text("MANAGE RETURNS");
        title.setStyle("-fx-font-size: 48px; -fx-fill: white; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(50, 0, 0, 0));
        root.getChildren().add(title);

        backButton = new Button("Back");
        searchField = new TextField();
        searchField.setPromptText("Search return ID or renter ID");
        searchField.setPrefWidth(300);

        filterButton = new Button("Filter");
        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<ReturnRecord, String> idCol = new TableColumn<>("Return ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("returnID"));

        TableColumn<ReturnRecord, String> rentalIDCol = new TableColumn<>("Rental ID");
        rentalIDCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("returnRentalID"));

        TableColumn<ReturnRecord, String> staffIdCol = new TableColumn<>("Staff ID");
        staffIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("returnStaffID"));

        TableColumn<ReturnRecord, Void> receiptCol = new TableColumn<>("Receipt");
        receiptCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View Receipt");
            {
                btn.setOnAction(e -> {
                    ReturnRecord record = getTableView().getItems().get(getIndex());
                    if (controller != null) controller.viewReceipt(record);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tableView.getColumns().addAll(idCol, rentalIDCol, staffIdCol, receiptCol);

        returnButton = new Button("Process Return");
        HBox buttonBox = new HBox(15, returnButton, backButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox tableCard = new VBox(15, tableView, buttonBox);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(950);
        tableCard.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-border-color: #9575cd; -fx-border-radius: 15; -fx-border-width: 2;");

        VBox layout = new VBox(30, searchBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(120,0,0,0));
        root.getChildren().add(layout);

        scene = new Scene(root, 1152, 761);
    }

    public Scene getScene() { return scene; }

    public void refreshTable(List<ReturnRecord> data) {
        tableView.getItems().setAll(data);
    }

    public void setController(ReturnController controller) { this.controller = controller; }

    public ReturnRecord getSelectedRecord() { return tableView.getSelectionModel().getSelectedItem(); }

    public Button getBackButton() { return backButton; }
    public Button getReturnButton() { return returnButton; }
}


