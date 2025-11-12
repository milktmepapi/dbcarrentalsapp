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
import model.LocationRecord;

public class LocationView {

    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<LocationRecord> tableView;
    private final Scene scene;

    public LocationView() {
        // ===== Background =====
        StackPane root = new StackPane();
        Image bgImage = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.jpg")
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(815);
        bgView.setFitHeight(450);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // ===== Title =====
        Text title = new Text("MANAGE LOCATIONS");
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
        StackPane.setMargin(title, new Insets(40, 0, 0, 0));
        root.getChildren().add(title);

        // ===== Search Bar =====
        searchField = new TextField();
        searchField.setPromptText("Search by city or province...");
        searchField.setPrefWidth(200);
        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefHeight(200);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<LocationRecord, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("locationId"));

        TableColumn<LocationRecord, String> cityCol = new TableColumn<>("City");
        cityCol.setCellValueFactory(new PropertyValueFactory<>("locationCity"));

        TableColumn<LocationRecord, String> provinceCol = new TableColumn<>("Province");
        provinceCol.setCellValueFactory(new PropertyValueFactory<>("locationProvince"));

        tableView.getColumns().addAll(idCol, cityCol, provinceCol);

        // ===== Buttons =====
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        deleteButton = new Button("Delete");
        returnButton = new Button("Return");

        addButton.getStyleClass().add("small-button");
        modifyButton.getStyleClass().add("small-button");
        deleteButton.getStyleClass().add("small-button");
        returnButton.getStyleClass().add("small-button");

        HBox buttonBox = new HBox(15, addButton, modifyButton, deleteButton, returnButton);
        buttonBox.setAlignment(Pos.CENTER);

        // ===== Layout =====
        VBox layout = new VBox(20, searchBox, tableView, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(100, 0, 0, 0));

        root.getChildren().add(layout);

        // ===== Scene =====
        scene = new Scene(root, 815, 450);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    /**
     * Show Add Location popup — accepts DAO + callback
     **/
    public void showAddLocationPopup(LocationDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Location");

        Label idLabel = new Label("Location ID:");
        TextField idField = new TextField();
        idField.setPromptText("e.g., BEN001");

        Label cityLabel = new Label("City:");
        TextField cityField = new TextField();
        cityField.setPromptText("Enter City");

        Label provinceLabel = new Label("Province:");
        TextField provinceField = new TextField();
        provinceField.setPromptText("Enter Province");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String id = idField.getText().trim();
            String city = cityField.getText().trim();
            String province = provinceField.getText().trim();

            if (id.isEmpty() || city.isEmpty() || province.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.addLocation(id, city, province);
            if (success) {
                message.setText("Added successfully!");
                message.setStyle("-fx-text-fill: lightgreen;");
                reloadCallback.run(); // refresh table in controller
                popup.close();
            } else {
                message.setText("Failed: Duplicate ID or City + Province.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                idLabel, idField,
                cityLabel, cityField,
                provinceLabel, provinceField,
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

    public Scene getScene() {
        return scene;
    }
}