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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.RentalRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalId"));

        TableColumn<RentalRecord, String> dlCol = new TableColumn<>("Renter DL");
        dlCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("renterDlNumber"));

        TableColumn<RentalRecord, String> plateCol = new TableColumn<>("Car Plate");
        plateCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("carPlateNumber"));

        TableColumn<RentalRecord, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("branchId"));

        TableColumn<RentalRecord, String> pickupCol = new TableColumn<>("Pickup (Exp.)");
        pickupCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expectedPickupDateTime"));

        TableColumn<RentalRecord, String> returnCol = new TableColumn<>("Return (Exp.)");
        returnCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expectedReturnDateTime"));

        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalStatus"));

        TableColumn<RentalRecord, BigDecimal> paymentCol = new TableColumn<>("Total Payment");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("totalPayment"));

        paymentCol.setCellFactory(column -> new TableCell<RentalRecord, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                    return;
                }

                setText("₱" + String.format("%,.2f", value));
                setAlignment(Pos.CENTER_RIGHT);
            }
        });


        tableView.getColumns().addAll(
                idCol, dlCol, plateCol, branchCol, pickupCol, returnCol, statusCol, paymentCol
        );

        // ===== Buttons =====
        addButton = new Button("Add");
        modifyButton = new Button("Pickup");
        viewButton = new Button("View");
        viewButton.setOnAction(e -> {
            RentalRecord selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("No Selection", "Please select a rental record to view.");
                return;
            }
            showViewRentalPopup(selected);
        });

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

    // ========================================================================
    // Add Rental popup (outline like ViolationView; white labels; branch->car cascading)
    // ========================================================================
    public void showAddRentalPopup(String newRentalId,
                                   Consumer<RentalInputData> callback,
                                   List<String> branches,
                                   List<String> renterDLs,
                                   List<model.CarRecord> allCars) {

        Stage popup = new Stage();
        popup.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        popup.setTitle("Add Rental");

        Label idLabel = new Label("Rental ID: " + newRentalId);
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");

        Label renterLabel = new Label("Renter DL:");
        renterLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        ComboBox<String> renterBox = new ComboBox<>();
        renterBox.getItems().addAll(renterDLs);
        renterBox.setPrefWidth(240);
        renterBox.setPromptText("Select Renter DL");
        renterBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        Label branchLabel = new Label("Branch:");
        branchLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        ComboBox<String> branchBox = new ComboBox<>();
        branchBox.getItems().addAll(branches);
        branchBox.setPrefWidth(240);
        branchBox.setPromptText("Select Branch");
        branchBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        Label carLabel = new Label("Car:");
        carLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        ComboBox<String> carBox = new ComboBox<>();
        carBox.setPrefWidth(240);
        carBox.setPromptText("Select Car (filtered by branch)");
        carBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        // When branch selected, populate carBox with available cars in branch
        // When branch selected, populate carBox with available cars in branch
        branchBox.setOnAction(e -> {
            String branch = branchBox.getValue();
            carBox.getItems().clear();
            if (branch != null) {
                List<String> plates = allCars.stream()
                        .filter(c -> branch.equals(c.getCarBranchId()))
                        .filter(c -> "Available".equalsIgnoreCase(c.getCarStatus())) // Enforce status
                        .map(model.CarRecord::getCarPlateNumber)
                        .collect(Collectors.toList());
                carBox.getItems().addAll(plates);
            }
        });

        Label pickupLabel = new Label("Expected Pickup Date:");
        pickupLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        DatePicker pickupDate = new DatePicker(LocalDate.now());
        pickupDate.setPrefWidth(240);
        pickupDate.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        // Disable past dates
        pickupDate.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-opacity: 0.4;");
                }
            }
        });

        Label pickupTimeLabel = new Label("Pickup Time:");
        pickupTimeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Spinner<Integer> pickupHour = new Spinner<>(0, 23, LocalTime.now().getHour());
        pickupHour.setEditable(true);
        pickupHour.setPrefWidth(80);

        Spinner<Integer> pickupMinute = new Spinner<>(0, 59, LocalTime.now().getMinute());
        pickupMinute.setEditable(true);
        pickupMinute.setPrefWidth(80);

        pickupDate.valueProperty().addListener((obs, o, n) ->
                updatePickupTimeRestrictions(pickupDate, pickupHour, pickupMinute));

        pickupHour.valueProperty().addListener((obs, o, n) ->
                updatePickupTimeRestrictions(pickupDate, pickupHour, pickupMinute));

        pickupMinute.valueProperty().addListener((obs, o, n) ->
                updatePickupTimeRestrictions(pickupDate, pickupHour, pickupMinute));

        HBox pickupTimeBox = new HBox(8, new Label("Hour:"), pickupHour, new Label("Minute:"), pickupMinute);
        pickupTimeBox.setAlignment(Pos.CENTER_LEFT);

        Label returnLabel = new Label("Expected Return Date:");
        returnLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        DatePicker returnDate = new DatePicker(LocalDate.now().plusDays(1));
        returnDate.setPrefWidth(240);
        returnDate.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        Label returnTimeLabel = new Label("Return Time:");
        returnTimeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        returnDate.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);

                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-opacity: 0.4;");
                }
            }
        });

        Spinner<Integer> returnHour = new Spinner<>(0, 23, 12);
        returnHour.setEditable(true);
        returnHour.setPrefWidth(80);

        Spinner<Integer> returnMinute = new Spinner<>(0, 59, 0);
        returnMinute.setEditable(true);
        returnMinute.setPrefWidth(80);

        returnDate.valueProperty().addListener((obs, o, n) ->
                updatePickupTimeRestrictions(returnDate, returnHour, returnMinute));

        returnHour.valueProperty().addListener((obs, o, n) ->
                updatePickupTimeRestrictions(returnDate, returnHour, returnMinute));

        returnMinute.valueProperty().addListener((obs, o, n) ->
                updatePickupTimeRestrictions(returnDate, returnHour, returnMinute));

        HBox returnTimeBox = new HBox(8, new Label("Hour:"), returnHour, new Label("Minute:"), returnMinute);
        returnTimeBox.setAlignment(Pos.CENTER_LEFT);

        Label paymentLabel = new Label("Total Payment (₱):");
        paymentLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextField paymentField = new TextField();
        paymentField.setPromptText("e.g. 1500.00");
        paymentField.setPrefWidth(240);
        paymentField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        Label computedLabel = new Label("Computed Total: ₱0.00");
        computedLabel.setStyle("-fx-text-fill: #66ff66; -fx-font-size: 14px; -fx-font-weight: bold;");

        // --- Recompute total rental fee when car or dates change ---
        Runnable updatePrice = () -> {
            String plate = carBox.getValue();
            LocalDate p = pickupDate.getValue();
            LocalDate r = returnDate.getValue();

            if (plate == null || p == null || r == null) return;

            // Find car record
            model.CarRecord car = allCars.stream()
                    .filter(c -> c.getCarPlateNumber().equals(plate))
                    .findFirst().orElse(null);

            if (car == null) return;

            BigDecimal computed = computeTotal(p, r, BigDecimal.valueOf(car.getCarRentalFee()));
            computedLabel.setText("Computed Total: ₱" + computed);
        };

        // Attach listeners
        carBox.setOnAction(e -> updatePrice.run());
        pickupDate.setOnAction(e -> updatePrice.run());
        returnDate.setOnAction(e -> updatePrice.run());


        Label msg = new Label();
        msg.setStyle("-fx-text-fill: orange;");

        Button addBtn = new Button("Add Rental");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        addBtn.setOnAction(e -> {
            if (renterBox.getValue() == null || branchBox.getValue() == null ||
                    carBox.getValue() == null || pickupDate.getValue() == null ||
                    returnDate.getValue() == null || paymentField.getText().trim().isEmpty()) {
                msg.setText("Please fill in all fields.");
                return;
            }

            LocalDate p = pickupDate.getValue();
            LocalDate r = returnDate.getValue();

            LocalTime pickTime = LocalTime.of(pickupHour.getValue(), pickupMinute.getValue());
            LocalTime retTime = LocalTime.of(returnHour.getValue(), returnMinute.getValue());

            LocalDateTime pickup = LocalDateTime.of(p, pickTime);
            LocalDateTime ret = LocalDateTime.of(r, retTime);

            // Final validation: must be at least 24 hours
            if (ret.isBefore(pickup.plusHours(24))) {
                msg.setText("Return time must be at least 24 hours after pickup.");
                return;
            }

            // --- ENFORCE MINIMUM PICKUP TIME (NOW + 5 MINUTES) ---
            LocalDateTime minimumPickup = LocalDateTime.now().plusMinutes(5);
            if (pickup.isBefore(minimumPickup)) {
                msg.setText("Pickup time must be at least 5 minutes from now.");
                return;
            }

            BigDecimal payment;
            try {
                payment = new BigDecimal(paymentField.getText().trim());
            } catch (NumberFormatException ex) {
                msg.setText("Payment must be a valid number.");
                return;
            }

            model.CarRecord car = allCars.stream()
                    .filter(c -> c.getCarPlateNumber().equals(carBox.getValue()))
                    .findFirst().orElse(null);

            if (car == null) {
                msg.setText("Car data not found.");
                return;
            }

            BigDecimal computed = computeTotal(p, r, BigDecimal.valueOf(car.getCarRentalFee()));

            if (payment.compareTo(computed) != 0) {
                msg.setText("Payment must equal computed total: ₱" + computed);
                return;
            }

            RentalInputData data = new RentalInputData(
                    newRentalId,
                    renterBox.getValue(),
                    carBox.getValue(),
                    pickup,
                    ret,
                    branchBox.getValue(),
                    payment
            );

            callback.accept(data);
            popup.close();
        });

        cancelBtn.setOnAction(e -> popup.close());

        VBox box = new VBox(14,
                idLabel,
                renterLabel, renterBox,
                branchLabel, branchBox,
                carLabel, carBox,
                pickupLabel, pickupDate,
                pickupTimeLabel, pickupTimeBox,
                returnLabel, returnDate,
                returnTimeLabel, returnTimeBox,
                paymentLabel, paymentField,
                computedLabel,
                addBtn, cancelBtn, msg
        );

        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 10; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-width: 2;");

        Scene sc = new Scene(box, 520, 820);
        sc.getStylesheets().add(getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm());
        popup.setScene(sc);
        popup.showAndWait();
    }

    private BigDecimal computeTotal(LocalDate pickup, LocalDate ret, BigDecimal dailyFee) {
        long days = ChronoUnit.DAYS.between(pickup, ret);
        if (days < 1) days = 1; // ensure at least 1 day
        return dailyFee.multiply(BigDecimal.valueOf(days));
    }

    void showPickupPopup(RentalRecord rental) {
        try {
            StaffDAO staffDAO = new StaffDAO();
            List<String> staffList = staffDAO.getOperationsStaffForBranch(rental.getBranchId());

            if (staffList.isEmpty()) {
                showError("No Staff", "No operations staff available for this branch.");
                return;
            }

            LocalDateTime now = LocalDateTime.now();

            Stage popup = new Stage();
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.setTitle("Process Pickup");

            // -------------------------
            // Header label
            // -------------------------
            Label idLabel = new Label("Process Pickup — Rental " + rental.getRentalId());
            idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

            Label expectedLabel = new Label("Expected Pickup: " + rental.getExpectedPickupDateTime());
            expectedLabel.setStyle("-fx-text-fill: #b46bff; -fx-font-size: 14px; -fx-font-weight: bold;");

            // -------------------------
            // Actual Pickup Date
            // -------------------------
            Label actualDateLabel = new Label("Actual Pickup Date:");
            actualDateLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            DatePicker actualDate = new DatePicker(now.toLocalDate());
            actualDate.setPrefWidth(240);
            actualDate.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

            // -------------------------
            // Time fields
            // -------------------------
            Label timeLabel = new Label("Actual Pickup Time:");
            timeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            Spinner<Integer> hourSpinner = new Spinner<>(0, 23, now.getHour());
            Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, now.getMinute());
            hourSpinner.setPrefWidth(80);
            minuteSpinner.setPrefWidth(80);
            hourSpinner.setEditable(true);
            minuteSpinner.setEditable(true);

            HBox timeBox = new HBox(8,
                    new Label("Hour:"), hourSpinner,
                    new Label("Minute:"), minuteSpinner
            );
            timeBox.setAlignment(Pos.CENTER_LEFT);

            // -------------------------
            // Staff selector
            // -------------------------
            Label staffLabel = new Label("Processed By (Staff):");
            staffLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

            ComboBox<String> staffDropdown = new ComboBox<>();
            staffDropdown.getItems().addAll(staffList);
            staffDropdown.setPrefWidth(240);
            staffDropdown.setPromptText("Select Staff");
            staffDropdown.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

            // -------------------------
            // Message label
            // -------------------------
            Label msg = new Label();
            msg.setStyle("-fx-text-fill: orange;");

            // -------------------------
            // Buttons
            // -------------------------
            Button confirmBtn = new Button("Confirm Pickup");
            Button cancelBtn = new Button("Cancel");

            confirmBtn.setStyle(
                    "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"
            );
            cancelBtn.setStyle(
                    "-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;"
            );

            confirmBtn.setOnAction(ev -> {
                if (staffDropdown.getValue() == null) {
                    msg.setText("Please select a staff member.");
                    return;
                }

                LocalDate d = actualDate.getValue();
                int hh = hourSpinner.getValue();
                int mm = minuteSpinner.getValue();

                LocalDateTime actualPickup = LocalDateTime.of(d, LocalTime.of(hh, mm));

                LocalDateTime expected = rental.getExpectedPickupDateTime();

                // 5-minute grace check
                if (actualPickup.isAfter(expected.plusMinutes(5))) {
                    msg.setText("Pickup is past the 5-minute grace period — should auto-cancel.");
                    return;
                }

                try {
                    RentalDAO.processPickup(
                            rental.getRentalId(),
                            staffDropdown.getValue(),
                            actualPickup
                    );
                    loadRentals();
                    popup.close();
                    showInfo("Success", "Pickup processed successfully.");
                } catch (SQLException ex) {
                    msg.setText(ex.getMessage());
                }
            });

            cancelBtn.setOnAction(e -> popup.close());

            VBox box = new VBox(14,
                    idLabel,
                    expectedLabel,
                    actualDateLabel, actualDate,
                    timeLabel, timeBox,
                    staffLabel, staffDropdown,
                    confirmBtn, cancelBtn,
                    msg
            );

            box.setAlignment(Pos.CENTER);
            box.setPadding(new Insets(30));
            box.setStyle(
                    "-fx-background-color: rgba(40,40,50,0.98);" +
                            "-fx-background-radius: 10;" +
                            "-fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);" +
                            "-fx-border-width: 2;"
            );

            Scene sc = new Scene(box, 500, 600);
            popup.setScene(sc);
            popup.showAndWait();

        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Error", ex.getMessage());
        }
    }

    // Support data class updated to include totalPayment
    public static class RentalInputData {
        public final String rentalId;
        public final String renterDl;
        public final String carPlate;
        public final LocalDateTime pickup;
        public final LocalDateTime returnDate;
        public final String branch;
        public final BigDecimal totalPayment;

        public RentalInputData(String rentalId, String renterDl, String carPlate,
                               LocalDateTime pickup, LocalDateTime returnDate, String branch,
                               BigDecimal totalPayment) {
            this.rentalId = rentalId;
            this.renterDl = renterDl;
            this.carPlate = carPlate;
            this.pickup = pickup;
            this.returnDate = returnDate;
            this.branch = branch;
            this.totalPayment = totalPayment;
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadRentals() {
        try {
            List<RentalRecord> rentals = RentalDAO.getAllRentals();
            tableView.getItems().setAll(rentals);
        } catch (SQLException ex) {
            showError("Error", "Failed to load rentals: " + ex.getMessage());
        }
    }

    public void showViewRentalPopup(RentalRecord record) {

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Rental Details");

        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 10;");

        Label title = new Label("Rental Information");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        Label id = new Label("Rental ID: " + record.getRentalId());
        Label dl = new Label("Renter DL: " + record.getRenterDlNumber());
        Label car = new Label("Car Plate: " + record.getCarPlateNumber());
        Label branch = new Label("Branch: " + record.getBranchId());

        Label rentalDateTime = new Label("Rental Datetime: " +
                (record.getRentalDateTime() == null ? "—" : record.getRentalDateTime()));

        Label expectedPickup = new Label("Expected Pickup: " + record.getExpectedPickupDateTime());

        Label actualPickup = new Label("Actual Pickup: " +
                (record.getActualPickupDateTime() == null ? "—" : record.getActualPickupDateTime()));

        Label pickupStaff = new Label("Processed By (Pickup): " +
                (record.getStaffIdPickup() == null ? "—" : record.getStaffIdPickup()));

        Label expectedReturn = new Label("Expected Return: " + record.getExpectedReturnDateTime());

        Label actualReturn = new Label("Actual Return: " +
                (record.getActualReturnDateTime() == null ? "—" : record.getActualReturnDateTime()));

        Label returnStaff = new Label("Processed By (Return): " +
                (record.getStaffIdReturn() == null ? "—" : record.getStaffIdReturn()));

        Label payment = new Label("Total Payment: ₱" + record.getTotalPayment());
        Label status = new Label("Status: " + record.getRentalStatus());

        for (Label lbl : new Label[]{id, dl, car, branch, rentalDateTime, expectedPickup, actualPickup,
                pickupStaff, expectedReturn, actualReturn, returnStaff, payment, status}) {

            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        }

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("small-button");
        closeBtn.setOnAction(e -> popup.close());

        layout.getChildren().addAll(
                title,
                id, dl, car, branch,
                rentalDateTime,
                expectedPickup, actualPickup, pickupStaff,
                expectedReturn, actualReturn, returnStaff,
                payment, status,
                closeBtn
        );

        popup.setScene(new Scene(layout, 380, 600));
        popup.showAndWait();
    }

    private void updatePickupTimeRestrictions(DatePicker pickupDate, Spinner<Integer> hour, Spinner<Integer> minute) {
        LocalDate selectedDate = pickupDate.getValue();
        if (selectedDate == null) return;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minimum = now.plusMinutes(5);

        // If it's NOT today → no restrictions
        if (!selectedDate.equals(now.toLocalDate())) {
            return;
        }

        int minHour = minimum.getHour();
        int minMinute = minimum.getMinute();

        // Force hour
        if (hour.getValue() < minHour) {
            hour.getValueFactory().setValue(minHour);
        }

        // If hour matches min hour → restrict minutes
        if (hour.getValue() == minHour && minute.getValue() < minMinute) {
            minute.getValueFactory().setValue(minMinute);
        }
    }

    private void updateReturnRestrictions(
            DatePicker pickupDate, Spinner<Integer> pickupHour, Spinner<Integer> pickupMinute,
            DatePicker returnDate, Spinner<Integer> returnHour, Spinner<Integer> returnMinute) {

        LocalDate pDate = pickupDate.getValue();
        LocalDate rDate = returnDate.getValue();
        if (pDate == null || rDate == null) return;

        LocalDateTime pickupDT = LocalDateTime.of(
                pDate,
                LocalTime.of(pickupHour.getValue(), pickupMinute.getValue())
        );

        LocalDateTime minReturn = pickupDT.plusHours(24);

        // If return is BEFORE minimum → snap forward
        LocalDateTime currentReturn = LocalDateTime.of(
                rDate,
                LocalTime.of(returnHour.getValue(), returnMinute.getValue())
        );

        if (currentReturn.isBefore(minReturn)) {
            // Snap return date/time to minimum allowed
            returnDate.setValue(minReturn.toLocalDate());
            returnHour.getValueFactory().setValue(minReturn.getHour());
            returnMinute.getValueFactory().setValue(minReturn.getMinute());
        }
    }
}
