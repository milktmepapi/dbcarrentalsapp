package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.RentalRecord;

import java.math.BigDecimal;
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

        TableColumn<RentalRecord, String> paymentCol = new TableColumn<>("Payment");
        paymentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("totalPayment"));

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
        branchBox.setOnAction(e -> {
            String branch = branchBox.getValue();
            carBox.getItems().clear();
            if (branch != null) {
                List<String> plates = allCars.stream()
                        .filter(c -> branch.equals(c.getCarBranchId()))
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

        Label returnLabel = new Label("Expected Return Date:");
        returnLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        DatePicker returnDate = new DatePicker(LocalDate.now().plusDays(1));
        returnDate.setPrefWidth(240);
        returnDate.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        Label paymentLabel = new Label("Total Payment (₱):");
        paymentLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextField paymentField = new TextField();
        paymentField.setPromptText("e.g. 1500.00");
        paymentField.setPrefWidth(240);
        paymentField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white;");

        Label computedLabel = new Label("Computed Total: ₱0.00");
        computedLabel.setStyle("-fx-text-fill: #66ff66; -fx-font-size: 14px; -fx-font-weight: bold;");


        Label msg = new Label();
        msg.setStyle("-fx-text-fill: orange;");

        Button addBtn = new Button("Add Rental");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        addBtn.setOnAction(e -> {
            // Basic validation done here, deeper validation in controller too
            if (renterBox.getValue() == null || branchBox.getValue() == null ||
                    carBox.getValue() == null || pickupDate.getValue() == null ||
                    returnDate.getValue() == null || paymentField.getText().trim().isEmpty()) {
                msg.setText("Please fill in all fields.");
                return;
            }

            LocalDateTime pickup = pickupDate.getValue().atTime(LocalTime.NOON);
            LocalDateTime ret = returnDate.getValue().atTime(LocalTime.NOON);

            BigDecimal payment;
            try {
                payment = new BigDecimal(paymentField.getText().trim());
            } catch (NumberFormatException ex) {
                msg.setText("Payment must be a valid number.");
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

        VBox box = new VBox(12,
                idLabel,
                renterLabel, renterBox,
                branchLabel, branchBox,
                carLabel, carBox,
                pickupLabel, pickupDate,
                returnLabel, returnDate,
                paymentLabel, paymentField,
                computedLabel,
                addBtn, cancelBtn, msg
        );

        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 10; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-width: 2;");

        Scene sc = new Scene(box, 420, 640);
        sc.getStylesheets().add(getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm());
        popup.setScene(sc);
        popup.showAndWait();
    }

    private BigDecimal computeTotal(LocalDate pickup, LocalDate ret, BigDecimal dailyFee) {
        long days = ChronoUnit.DAYS.between(pickup, ret);
        if (days < 1) days = 1; // ensure at least 1 day
        return dailyFee.multiply(BigDecimal.valueOf(days));
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
}
