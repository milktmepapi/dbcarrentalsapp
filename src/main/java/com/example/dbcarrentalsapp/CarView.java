package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.CarRecord;

public class CarView {
    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<CarRecord> tableView;
    private final Scene scene;

    public CarView() {
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
        Text title = new Text("MANAGE CARS");
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
        searchField.setPromptText("Search by plate or brand...");
        searchField.setPrefWidth(300);
        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900); // Wider table
        tableView.setPrefHeight(300);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CarRecord, String> plateNumberCol = new TableColumn<>("Plate Number");
        plateNumberCol.setCellValueFactory(new PropertyValueFactory<>("carPlateNumber"));

        TableColumn<CarRecord, String> transmissionCol = new TableColumn<>("Transmission");
        transmissionCol.setCellValueFactory(new PropertyValueFactory<>("carTransmission"));

        TableColumn<CarRecord, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        TableColumn<CarRecord, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("carBrand"));

        TableColumn<CarRecord, String> yearManufacturedCol = new TableColumn<>("Year Manufactured");
        yearManufacturedCol.setCellValueFactory(new PropertyValueFactory<>("carYearManufactured"));

        TableColumn<CarRecord, String> mileageCol = new TableColumn<>("Mileage");
        mileageCol.setCellValueFactory(new PropertyValueFactory<>("carMileage"));

        TableColumn<CarRecord, String> seatNumberCol = new TableColumn<>("Seat Number");
        seatNumberCol.setCellValueFactory(new PropertyValueFactory<>("carSeatNumber"));

        TableColumn<CarRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("carStatus"));

        TableColumn<CarRecord, String> branchIDCol = new TableColumn<>("Branch ID");
        branchIDCol.setCellValueFactory(new PropertyValueFactory<>("carBranchID"));

        tableView.getColumns().addAll(plateNumberCol, transmissionCol, modelCol, brandCol, yearManufacturedCol, mileageCol, seatNumberCol, statusCol, branchIDCol);

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

        // ===== Layout =====
        VBox layout = new VBox(30, searchBox, tableView, buttonBox); // more spacing between sections
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 0, 0, 0));

        root.getChildren().add(layout);

        // ===== Scene =====
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    /**
     * Show Add Car popup — accepts DAO + callback
     **/
    public void showAddCarPopup(CarDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Car");

        Label plateNumberLabel = new Label("Plate Number:");
        TextField plateNumberField = new TextField();
        plateNumberField.setPromptText("e.g., ABC1234");

        Label transmissionLabel = new Label("Transmission:");
        TextField transmissionField = new TextField();
        transmissionField.setPromptText("Enter Transmission");

        Label modelLabel = new Label("Model:");
        TextField modelField = new TextField();
        modelField.setPromptText("Enter Model");

        Label brandLabel = new Label("Brand:");
        TextField brandField = new TextField();
        brandField.setPromptText("Enter Brand");

        Label yearManufacturedLabel = new Label("Year Manufactured:");
        TextField yearManufacturedField = new TextField();
        yearManufacturedField.setPromptText("Enter Year Manufactured");

        Label mileageLabel = new Label("Mileage:");
        TextField mileageField = new TextField();
        mileageField.setPromptText("Enter Mileage");

        Label seatNumberLabel = new Label("Seat Number:");
        TextField seatNumberField = new TextField();
        seatNumberField.setPromptText("Enter Seat Number");

        Label statusLabel = new Label("Status:");
        TextField statusField = new TextField();
        statusField.setPromptText("Status");

        Label branchIDLabel = new Label("Branch ID:");
        TextField branchIDField = new TextField();
        branchIDField.setPromptText("e.g., BEN001");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String plateNumber = plateNumberField.getText().trim();
            String transmission = transmissionField.getText().trim();
            String model = modelField.getText().trim();
            String brand = brandField.getText().trim();
            String yearManufactured = yearManufacturedField.getText().trim();
            int year = Integer.parseInt(yearManufactured);
            String mileage = mileageField.getText().trim();
            int distance = Integer.parseInt(mileage);
            String seatNumber = seatNumberField.getText().trim();
            int seat = Integer.parseInt(seatNumber);
            String status = statusField.getText().trim();
            String branchId = branchIDField.getText().trim();

            if (plateNumber.isEmpty() || transmission.isEmpty() || model.isEmpty() || brand.isEmpty() || yearManufactured.isEmpty() || mileage.isEmpty() || seatNumber.isEmpty() || status.isEmpty() || branchId.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.addCar(plateNumber, transmission, model, brand, year, distance, seat, status, branchId);
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
                plateNumberLabel, plateNumberField,
                transmissionLabel, transmissionField,
                modelLabel, modelField,
                brandLabel, brandField,
                yearManufacturedLabel, yearManufacturedField,
                mileageLabel, mileageField,
                seatNumberLabel, seatNumberField,
                statusLabel, statusField,
                branchIDLabel, branchIDField,
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

    /**
     * Show Modify Car popup — allows editing city and province only.
     **/
    public void showModifyCarPopup(CarDAO dao, CarRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText(null);
            alert.setContentText("Please select a Car to modify.");
            alert.showAndWait();
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Car");

            Label plateNumberLabel = new Label("Plate Number:");
        TextField plateNumberField = new TextField(selected.getCarPlateNumber());
        plateNumberField.setEditable(false);
        plateNumberField.setStyle("-fx-opacity: 0.7;");

        Label transmissionLabel = new Label("Transmission:");
        TextField transmissionField = new TextField(selected.getCarTransmission());

        Label modelLabel= new Label("Model:");
        TextField modelField = new TextField(selected.getCarModel());

        Label brandLabel= new Label("Brand:");
        TextField brandField = new TextField(selected.getCarBrand());

        Label yearManufacturedLabel= new Label("Year Manufactured:");
        TextField yearManufacturedField = new TextField(String.valueOf(selected.getCarYearManufactured()));

        Label mileageLabel= new Label("Mileage:");
        TextField mileageField = new TextField(String.valueOf(selected.getCarMileage()));

        Label seatNumberLabel= new Label("Seat Number:");
        TextField seatNumberField = new TextField(String.valueOf(selected.getCarSeatNumber()));

        Label statusLabel= new Label("Status:");
        TextField statusField = new TextField(selected.getCarStatus());

        Label branchIDLabel= new Label("Branch ID:");
        TextField branchIDField = new TextField(selected.getCarBranchId());

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        CarRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String plateNumber = plateNumberField.getText().trim();
            String transmission = transmissionField.getText().trim();
            String model = modelField.getText().trim();
            String brand = brandField.getText().trim();
            String yearManufactured = yearManufacturedField.getText().trim();
            int year = Integer.parseInt(yearManufactured);
            String mileage = mileageField.getText().trim();
            int distance = Integer.parseInt(mileage);
            String seatNumber = seatNumberField.getText().trim();
            int seat = Integer.parseInt(seatNumber);
            String status = statusField.getText().trim();
            String branchId = branchIDField.getText().trim();

            if (plateNumber.isEmpty() || transmission.isEmpty() || model.isEmpty() || brand.isEmpty() || yearManufactured.isEmpty() || mileage.isEmpty() || seatNumber.isEmpty() || status.isEmpty() || branchId.isEmpty()) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange;");
                return;
            }

            boolean success = dao.updateCar(finalSelected.getCarPlateNumber(), transmission, model, brand, year, distance, seat, status, branchId);
            if (success) {
                message.setText("Updated successfully!");
                message.setStyle("-fx-text-fill: lightgreen;");
                reloadCallback.run(); // refresh the table
                popup.close();
            } else {
                message.setText("Failed: Duplicate or database error.");
                message.setStyle("-fx-text-fill: red;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                plateNumberLabel, plateNumberField,
                transmissionLabel, transmissionField,
                modelLabel, modelField,
                brandLabel, brandField,
                yearManufacturedLabel, yearManufacturedField,
                mileageLabel, mileageField,
                seatNumberLabel, seatNumberField,
                statusLabel, statusField,
                branchIDLabel, branchIDField,
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
