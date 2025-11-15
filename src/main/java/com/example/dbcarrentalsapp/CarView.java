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
        tableView.setPrefHeight(280);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");

        // Proper fix: keep table fully inside border box
        tableView.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableView.setPadding(new Insets(5, 8, 5, 8));

        TableColumn<CarRecord, String> plateNumberCol = new TableColumn<>("Plate Number");
        plateNumberCol.setCellValueFactory(new PropertyValueFactory<>("carPlateNumber"));

        TableColumn<CarRecord, String> transmissionCol = new TableColumn<>("Transmission");
        transmissionCol.setCellValueFactory(new PropertyValueFactory<>("carTransmission"));

        TableColumn<CarRecord, String> modelCol = new TableColumn<>("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        TableColumn<CarRecord, String> brandCol = new TableColumn<>("Brand");
        brandCol.setCellValueFactory(new PropertyValueFactory<>("carBrand"));

        TableColumn<CarRecord, Integer> yearManufacturedCol = new TableColumn<>("Year Manufactured");
        yearManufacturedCol.setCellValueFactory(new PropertyValueFactory<>("carYearManufactured"));

        TableColumn<CarRecord, Integer> mileageCol = new TableColumn<>("Mileage");
        mileageCol.setCellValueFactory(new PropertyValueFactory<>("carMileage"));

        TableColumn<CarRecord, Integer> seatNumberCol = new TableColumn<>("Seat Number");
        seatNumberCol.setCellValueFactory(new PropertyValueFactory<>("carSeatNumber"));

        TableColumn<CarRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("carStatus"));

        // FIX: Changed from "carBranchID" to "carBranchId" to match the getter method
        TableColumn<CarRecord, String> branchIDCol = new TableColumn<>("Branch ID");
        branchIDCol.setCellValueFactory(new PropertyValueFactory<>("carBranchId"));

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

        // ===== Card Container =====
        VBox tableCard = new VBox(15, tableView, buttonBox);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(950);
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

    /**
     * Show Add Car popup — accepts DAO + callback
     **/
    public void showAddCarPopup(CarDAO dao, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Car");

        Label plateNumberLabel = new Label("Plate Number:");
        plateNumberLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField plateNumberField = new TextField();
        plateNumberField.setPromptText("e.g., ABC1234");
        plateNumberField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label transmissionLabel = new Label("Transmission:");
        transmissionLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> transmissionComboBox = new ComboBox<>();
        transmissionComboBox.getItems().addAll("Manual", "Automatic");
        transmissionComboBox.setPromptText("Select Transmission");
        transmissionComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label modelLabel = new Label("Model:");
        modelLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField modelField = new TextField();
        modelField.setPromptText("Enter Model");
        modelField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label brandLabel = new Label("Brand:");
        brandLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField brandField = new TextField();
        brandField.setPromptText("Enter Brand");
        brandField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label yearManufacturedLabel = new Label("Year Manufactured:");
        yearManufacturedLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField yearManufacturedField = new TextField();
        yearManufacturedField.setPromptText("Enter Year");
        yearManufacturedField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label mileageLabel = new Label("Mileage:");
        mileageLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField mileageField = new TextField();
        mileageField.setPromptText("Enter Mileage");
        mileageField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label seatNumberLabel = new Label("Seat Number:");
        seatNumberLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<Integer> seatNumberComboBox = new ComboBox<>();
        seatNumberComboBox.getItems().addAll(2, 4, 5, 7, 8);
        seatNumberComboBox.setPromptText("Select Seats");
        seatNumberComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Available", "Rented", "Under Maintenance");
        statusComboBox.setPromptText("Select Status");
        statusComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label branchIDLabel = new Label("Branch ID:");
        branchIDLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> branchIDComboBox = new ComboBox<>();
        branchIDComboBox.setPromptText("Select Branch ID");
        branchIDComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate branch IDs
        BranchDAO branchDAO = new BranchDAO();
        branchIDComboBox.getItems().addAll(branchDAO.getAllBranchIds());

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            String plateNumber = plateNumberField.getText().trim();
            String transmission = transmissionComboBox.getValue();
            String model = modelField.getText().trim();
            String brand = brandField.getText().trim();
            String yearManufactured = yearManufacturedField.getText().trim();
            String mileage = mileageField.getText().trim();
            Integer seatNumber = seatNumberComboBox.getValue();
            String status = statusComboBox.getValue();
            String branchId = branchIDComboBox.getValue();

            if (plateNumber.isEmpty() || transmission == null || model.isEmpty() || brand.isEmpty() ||
                    yearManufactured.isEmpty() || mileage.isEmpty() || seatNumber == null ||
                    status == null || branchId == null) {
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            try {
                int year = Integer.parseInt(yearManufactured);
                int distance = Integer.parseInt(mileage);
                int seat = seatNumber;

                boolean success = dao.addCar(plateNumber, transmission, model, brand, year, distance, seat, status, branchId);
                if (success) {
                    reloadCallback.run();
                    popup.close();
                    showSuccessPopup("Success", "Car added successfully!");
                } else {
                    message.setText("Failed: Duplicate plate number or invalid data.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                message.setText("Please enter valid numbers for year and mileage!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                plateNumberLabel, plateNumberField,
                transmissionLabel, transmissionComboBox,
                modelLabel, modelField,
                brandLabel, brandField,
                yearManufacturedLabel, yearManufacturedField,
                mileageLabel, mileageField,
                seatNumberLabel, seatNumberComboBox,
                statusLabel, statusComboBox,
                branchIDLabel, branchIDComboBox,
                buttonBox, message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 380, 750);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popup.showAndWait();
    }

    /**
     * Show Modify Car popup
     **/
    public void showModifyCarPopup(CarDAO dao, CarRecord selected, Runnable reloadCallback) {
        selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a Car to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Car");

        Label plateNumberLabel = new Label("Plate Number:");
        plateNumberLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField plateNumberField = new TextField(selected.getCarPlateNumber());
        plateNumberField.setEditable(false);
        plateNumberField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label transmissionLabel = new Label("Transmission:");
        transmissionLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> transmissionComboBox = new ComboBox<>();
        transmissionComboBox.getItems().addAll("Manual", "Automatic", "CVT");
        transmissionComboBox.setValue(selected.getCarTransmission());
        transmissionComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label modelLabel= new Label("Model:");
        modelLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField modelField = new TextField(selected.getCarModel());
        modelField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label brandLabel= new Label("Brand:");
        brandLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField brandField = new TextField(selected.getCarBrand());
        brandField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label yearManufacturedLabel= new Label("Year Manufactured:");
        yearManufacturedLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField yearManufacturedField = new TextField(String.valueOf(selected.getCarYearManufactured()));
        yearManufacturedField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label mileageLabel= new Label("Mileage:");
        mileageLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField mileageField = new TextField(String.valueOf(selected.getCarMileage()));
        mileageField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label seatNumberLabel= new Label("Seat Number:");
        seatNumberLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<Integer> seatNumberComboBox = new ComboBox<>();
        seatNumberComboBox.getItems().addAll(2, 4, 5, 7, 8);
        seatNumberComboBox.setValue(selected.getCarSeatNumber());
        seatNumberComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label statusLabel= new Label("Status:");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> statusComboBox = new ComboBox<>();
        statusComboBox.getItems().addAll("Available", "Rented", "Maintenance");
        statusComboBox.setValue(selected.getCarStatus());
        statusComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label branchIDLabel= new Label("Branch ID:");
        branchIDLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> branchIDComboBox = new ComboBox<>(); // CHANGED TO COMBOBOX
        branchIDComboBox.setPromptText("Select Branch ID");
        branchIDComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate branch IDs and set current value
        BranchDAO branchDAO = new BranchDAO();
        branchIDComboBox.getItems().addAll(branchDAO.getAllBranchIds());
        branchIDComboBox.setValue(selected.getCarBranchId());

        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        CarRecord finalSelected = selected;
        saveBtn.setOnAction(e -> {
            String plateNumber = plateNumberField.getText().trim();
            String transmission = transmissionComboBox.getValue();
            String model = modelField.getText().trim();
            String brand = brandField.getText().trim();
            String yearManufactured = yearManufacturedField.getText().trim();
            String mileage = mileageField.getText().trim();
            Integer seatNumber = seatNumberComboBox.getValue();
            String status = statusComboBox.getValue();
            String branchId = branchIDComboBox.getValue(); // CHANGED TO COMBOBOX

            if (plateNumber.isEmpty() || transmission == null || model.isEmpty() || brand.isEmpty() ||
                    yearManufactured.isEmpty() || mileage.isEmpty() || seatNumber == null ||
                    status == null || branchId == null) { // CHANGED CHECK
                message.setText("Please fill in all fields!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            try {
                int year = Integer.parseInt(yearManufactured);
                int distance = Integer.parseInt(mileage);
                int seat = seatNumber;

                boolean success = dao.updateCar(finalSelected.getCarPlateNumber(), transmission, model, brand, year, distance, seat, status, branchId);
                if (success) {
                    reloadCallback.run(); // refresh the table
                    popup.close();
                    showSuccessPopup("Updated", "Car updated successfully!");
                } else {
                    message.setText("Failed: Database error.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                }
            } catch (NumberFormatException ex) {
                message.setText("Please enter valid numbers for year and mileage!");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                plateNumberLabel, plateNumberField,
                transmissionLabel, transmissionComboBox,
                modelLabel, modelField,
                brandLabel, brandField,
                yearManufacturedLabel, yearManufacturedField,
                mileageLabel, mileageField,
                seatNumberLabel, seatNumberComboBox,
                statusLabel, statusComboBox,
                branchIDLabel, branchIDComboBox, // CHANGED TO COMBOBOX
                buttonBox, message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 380, 750);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popup.showAndWait();
    }

    /** Reusable Success Popup **/
    public void showSuccessPopup(String title, String messageText) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);

        Label msg = new Label(messageText);
        msg.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        msg.setWrapText(true);

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().add("small-button");
        okBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 20;");
        okBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(20, msg, okBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #4CAF50, #8BC34A); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene scene = new Scene(layout, 320, 160);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(scene);
        scene.getRoot().requestFocus(); // prevent OK from pre-focusing
        popup.showAndWait();
    }

    /** Confirmation Popup (for deletions) **/
    public boolean showConfirmPopup(CarRecord selected) {
        if (selected == null) {
            showSuccessPopup("No Selection", "Please select a car to delete.");
            return false;
        }

        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label plateLabel = new Label("Plate Number:");
        plateLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField plateField = new TextField(selected.getCarPlateNumber());
        plateField.setEditable(false);
        plateField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label modelLabel = new Label("Model:");
        modelLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField modelField = new TextField(selected.getCarModel());
        modelField.setEditable(false);
        modelField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label brandLabel = new Label("Brand:");
        brandLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField brandField = new TextField(selected.getCarBrand());
        brandField.setEditable(false);
        brandField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label transmissionLabel = new Label("Transmission:");
        transmissionLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField transmissionField = new TextField(selected.getCarTransmission());
        transmissionField.setEditable(false);
        transmissionField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label branchLabel = new Label("Branch ID:");
        branchLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField branchField = new TextField(selected.getCarBranchId());
        branchField.setEditable(false);
        branchField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label message = new Label("Are you sure you want to delete this car?");
        message.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        message.setWrapText(true);

        Button yesBtn = new Button("Yes, Delete");
        Button noBtn = new Button("Cancel");
        yesBtn.getStyleClass().add("small-button");
        noBtn.getStyleClass().add("small-button");
        yesBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        noBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        yesBtn.setOnAction(e -> {
            confirmed[0] = true;
            popup.close();
        });
        noBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, yesBtn, noBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                plateLabel, plateField,
                modelLabel, modelField,
                brandLabel, brandField,
                transmissionLabel, transmissionField,
                branchLabel, branchField,
                message,
                buttonBox
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #ff4444, #ff6b6b); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 380, 500);
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