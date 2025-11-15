package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import model.RentalRecord;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundSize;

public class RentalView {
    public Button addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<RentalRecord> tableView;
    private final Scene scene;

    public RentalView() {
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
        Text title = new Text("MANAGE RENTALS");
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

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<RentalRecord, String> idCol = new TableColumn<>("Rental ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalId"));

        TableColumn<RentalRecord, String> renterCol = new TableColumn<>("Renter DL");
        renterCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("renterDlNumber"));

        TableColumn<RentalRecord, String> carCol = new TableColumn<>("Car Plate");
        carCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("carPlateNumber"));

        TableColumn<RentalRecord, LocalDateTime> createdCol = new TableColumn<>("Date Created");
        createdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalDateTime"));

        TableColumn<RentalRecord, LocalDateTime> pickupCol = new TableColumn<>("Expected Pickup");
        pickupCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expectedPickupDateTime"));

        TableColumn<RentalRecord, LocalDateTime> returnCol = new TableColumn<>("Expected Return");
        returnCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("expectedReturnDateTime"));

        TableColumn<RentalRecord, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("branchId"));

        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rentalStatus"));

        tableView.getColumns().addAll(
                idCol,
                renterCol,
                carCol,
                createdCol,
                pickupCol,
                returnCol,
                branchCol, // ← added here
                statusCol
        );


        // ===== Buttons =====
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        returnButton = new Button("Return");

        HBox buttonBox = new HBox(15, addButton, modifyButton, returnButton);
        buttonBox.setAlignment(Pos.CENTER);

        // ===== Card Container =====
        VBox tableCard = new VBox(15, tableView, buttonBox);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(950);
        tableCard.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: #9575cd;" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 2;"
        );

        VBox layout = new VBox(30, searchBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(120, 0, 0, 0));

        root.getChildren().add(layout);

        // ===== Scene and Styling =====
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }

    /**
     * Add popup:
     * - No staff fields
     * - Status implicitly UPCOMING
     * - Car & Branch dropdowns show "id — name/model"
     * - Total payment must be entered
     */
    public void showAddRentalPopup(RentalDAO dao, RenterDAO renterDAO, Runnable refresh) {

        Dialog<RentalRecord> dialog = new Dialog<>();
        dialog.setTitle("Add Rental Transaction");
        dialog.setHeaderText("Enter rental details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Fields
        ComboBox<String> renterDL = new ComboBox<>();
        ComboBox<String> carPlate = new ComboBox<>();
        ComboBox<String> branchId = new ComboBox<>();

        DatePicker expectedPickupDate = new DatePicker();
        TextField expectedPickupTime = new TextField("10:00");

        DatePicker expectedReturnDate = new DatePicker();
        TextField expectedReturnTime = new TextField("10:00");

        TextField totalPaymentField = new TextField();

        // Fill dropdowns
        renterDL.getItems().addAll(renterDAO.getAllRenterDLs());

        CarDAO carDAO = new CarDAO();
        carDAO.getAllCars().forEach(car ->
                carPlate.getItems().add(car.getCarPlateNumber() + " — " + car.getCarModel())
        );

        BranchDAO branchDAO = new BranchDAO();
        branchDAO.getAllBranches().forEach(branch ->
                branchId.getItems().add(branch.getBranchId() + " — " + branch.getBranchName())
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Renter DL:"), renterDL);
        grid.addRow(1, new Label("Car Plate & Model:"), carPlate);
        grid.addRow(2, new Label("Branch:"), branchId);

        grid.addRow(3, new Label("Expected Pickup:"), expectedPickupDate, expectedPickupTime);
        grid.addRow(4, new Label("Expected Return:"), expectedReturnDate, expectedReturnTime);

        grid.addRow(5, new Label("Total Payment:"), totalPaymentField);

        dialog.getDialogPane().setContent(grid);

        // Enable add button only when mandatory fields are set
        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        // Validate: renter, car, branch, both dates set, times not blank, payment not blank
        renterDL.valueProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate, branchId, expectedPickupDate, expectedReturnDate, totalPaymentField));
        carPlate.valueProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate, branchId, expectedPickupDate, expectedReturnDate, totalPaymentField));
        branchId.valueProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate, branchId, expectedPickupDate, expectedReturnDate, totalPaymentField));
        expectedPickupDate.valueProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate, branchId, expectedPickupDate, expectedReturnDate, totalPaymentField));
        expectedReturnDate.valueProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate, branchId, expectedPickupDate, expectedReturnDate, totalPaymentField));
        totalPaymentField.textProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate, branchId, expectedPickupDate, expectedReturnDate, totalPaymentField));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != addButtonType) return null;

            LocalDateTime rentalDateTime = LocalDateTime.now();

            String plateOnly = carPlate.getValue().split(" — ")[0];
            String branchOnly = branchId.getValue().split(" — ")[0];

            LocalDateTime expectedPickup = LocalDateTime.of(
                    expectedPickupDate.getValue(),
                    parseTime(expectedPickupTime.getText())
            );

            LocalDateTime expectedReturn = LocalDateTime.of(
                    expectedReturnDate.getValue(),
                    parseTime(expectedReturnTime.getText())
            );

            BigDecimal totalPayment = new BigDecimal(totalPaymentField.getText().trim());

            return new RentalRecord(
                    null,
                    renterDL.getValue(),
                    plateOnly,
                    branchOnly,
                    null, // pickup staff removed for ADD
                    null, // return staff removed
                    rentalDateTime,
                    expectedPickup,
                    null, // actual pickup
                    expectedReturn,
                    null, // actual return
                    totalPayment,
                    RentalRecord.RentalStatus.UPCOMING
            );
        });

        Optional<RentalRecord> result = dialog.showAndWait();

        result.ifPresent(rental -> {
            try {
                dao.addRental(rental);
                refresh.run();
                showSuccessPopup("Success", "Rental added successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                showSuccessPopup("Database Error", "Failed to add rental.");
            }
        });
    }

    private void validateAddFields(Node addButton,
                                   ComboBox<String> renter,
                                   ComboBox<String> car,
                                   ComboBox<String> branch,
                                   DatePicker pickupDate,
                                   DatePicker returnDate,
                                   TextField payment) {
        boolean isDisabled =
                renter.getSelectionModel().isEmpty() ||
                        car.getSelectionModel().isEmpty() ||
                        branch.getSelectionModel().isEmpty() ||
                        pickupDate.getValue() == null ||
                        returnDate.getValue() == null ||
                        payment.getText().trim().isEmpty();
        addButton.setDisable(isDisabled);
    }

    public void showSuccessPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Modify popup: ONLY editable fields:
     *   - actual pickup datetime (DatePicker + time text)
     *   - pickup staff (dropdown) — only staff from the rental's branch, no preselect
     *   - rental status (dropdown)
     *
     * Everything else is read-only.
     *
     * Note: this view calls dao.updateRentalPartial(updatedRental) — implement that DAO method
     * to only update the columns: rental_actual_pickup_datetime, rental_staff_id_pickup, rental_status
     */
    public void showModifyRentalPopup(RentalDAO dao, RentalRecord rental, Runnable refresh) {

        // Block editing if rental is FINAL
        if (rental.getRentalStatus() == RentalRecord.RentalStatus.COMPLETED ||
                rental.getRentalStatus() == RentalRecord.RentalStatus.CANCELLED) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Modification Not Allowed");
            alert.setHeaderText("This rental can no longer be modified.");
            alert.setContentText("Rental is already " + rental.getRentalStatus() + ".");
            alert.showAndWait();
            return;
        }

        Dialog<RentalRecord> dialog = new Dialog<>();
        dialog.setTitle("Modify Rental: " + rental.getRentalId());

        // Read-only fields (GRAYED OUT)
        TextField rentalIdField = new TextField(rental.getRentalId());
        disableField(rentalIdField);

        TextField renterField = new TextField(rental.getRenterDlNumber());
        disableField(renterField);

        CarDAO carDAO = new CarDAO();
        String carDisplay = rental.getCarPlateNumber();
        try {
            carDisplay += " — " + carDAO.getCarByPlate(rental.getCarPlateNumber()).getCarModel();
        } catch (Exception ignored) {}
        TextField carField = new TextField(carDisplay);
        disableField(carField);

        BranchDAO branchDAO = new BranchDAO();
        String branchDisplay = rental.getBranchId();
        try {
            branchDisplay += " — " + branchDAO.getBranchById(rental.getBranchId()).getBranchName();
        } catch (Exception ignored) {}
        TextField branchField = new TextField(branchDisplay);
        disableField(branchField);

        TextField paymentField = new TextField(rental.getTotalPayment().toString());
        disableField(paymentField);

        // Editable fields only
        ComboBox<RentalRecord.RentalStatus> statusBox = new ComboBox<>();
        statusBox.getItems().setAll(
                RentalRecord.RentalStatus.UPCOMING,
                RentalRecord.RentalStatus.ACTIVE
        );

        if (rental.getRentalStatus() == RentalRecord.RentalStatus.UPCOMING ||
                rental.getRentalStatus() == RentalRecord.RentalStatus.ACTIVE) {
            statusBox.setValue(rental.getRentalStatus());
        } else {
            statusBox.setDisable(true);
        }

        // Pickup date/time
        DatePicker actualPickupDate = new DatePicker();
        TextField actualPickupTime = new TextField("10:00");

        if (rental.getActualPickupDateTime() != null) {
            actualPickupDate.setValue(rental.getActualPickupDateTime().toLocalDate());
            actualPickupTime.setText(rental.getActualPickupDateTime().toLocalTime().toString());
        }

        // Staff dropdown
        StaffDAO staffDAO = new StaffDAO();
        ComboBox<String> staffBox = new ComboBox<>();
        staffBox.getItems().addAll(staffDAO.getStaffIdsByBranch(rental.getBranchId()));
        staffBox.setPromptText("Select Staff");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Rental ID:"), rentalIdField);
        grid.addRow(1, new Label("Renter DL:"), renterField);
        grid.addRow(2, new Label("Car:"), carField);
        grid.addRow(3, new Label("Branch:"), branchField);
        grid.addRow(4, new Label("Total Payment:"), paymentField);
        grid.addRow(5, new Label("Rental Status:"), statusBox);
        grid.addRow(6, new Label("Actual Pickup Date:"), actualPickupDate, actualPickupTime);
        grid.addRow(7, new Label("Pickup Staff:"), staffBox);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveButton) {

                rental.setRentalStatus(statusBox.getValue());

                if (actualPickupDate.getValue() != null &&
                        !actualPickupTime.getText().trim().isEmpty()) {

                    rental.setActualPickupDateTime(
                            actualPickupDate.getValue().atTime(parseTime(actualPickupTime.getText()))
                    );
                }

                rental.setStaffIdPickup(staffBox.getValue());
                return rental;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            try {
                dao.updateRentalPartial(updated);
                refresh.run();
                showSuccessPopup("Success", "Rental updated successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
                showSuccessPopup("Error", "Failed to update rental.");
            }
        });
    }

    private void disableField(TextField field) {
        field.setEditable(false);
        field.setStyle("-fx-opacity: 0.6; -fx-control-inner-background: #e0e0e0;");
    }

    private LocalTime parseTime(String time) {
        return LocalTime.parse(time.trim());
    }
}
