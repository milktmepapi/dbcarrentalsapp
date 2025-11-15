package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import model.RentalRecord;

import java.util.List;
import java.util.Optional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
        idCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));

        TableColumn<RentalRecord, String> renterCol = new TableColumn<>("Renter DL");
        renterCol.setCellValueFactory(new PropertyValueFactory<>("renterDlNumber"));

        TableColumn<RentalRecord, String> carCol = new TableColumn<>("Car Plate");
        carCol.setCellValueFactory(new PropertyValueFactory<>("carPlateNumber"));

        TableColumn<RentalRecord, LocalDateTime> pickupCol = new TableColumn<>("Pickup Date");
        pickupCol.setCellValueFactory(new PropertyValueFactory<>("pickupDateTime"));

        TableColumn<RentalRecord, LocalDateTime> returnCol = new TableColumn<>("Return Date");
        returnCol.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDateTime"));

        TableColumn<RentalRecord, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("rentalStatus"));

        tableView.getColumns().addAll(idCol, renterCol, carCol, pickupCol, returnCol, statusCol);

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

    public void showAddRentalPopup(RentalDAO dao, RenterDAO renterDAO, Runnable refresh) {

        Dialog<RentalRecord> dialog = new Dialog<>();
        dialog.setTitle("Add Rental Transaction");
        dialog.setHeaderText("Enter rental details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Fields
        ComboBox<String> renterDL = new ComboBox<>();
        ComboBox<String> carPlate = new ComboBox<>();
        ComboBox<String> pickupStaff = new ComboBox<>();
        ComboBox<String> returnStaff = new ComboBox<>();
        ComboBox<String> branchId = new ComboBox<>();

        DatePicker pickupDate = new DatePicker();
        DatePicker expectedReturnDate = new DatePicker();
        DatePicker actualReturnDate = new DatePicker();

        TextField pickupTime = new TextField("10:00");
        TextField expectedReturnTime = new TextField("10:00");
        TextField actualReturnTime = new TextField("10:00");

        TextField paymentField = new TextField();
        ComboBox<RentalRecord.RentalStatus> statusBox = new ComboBox<>();

        // Fill dropdowns
        renterDL.getItems().addAll(renterDAO.getAllRenterDLs());

        CarDAO carDAO = new CarDAO();
        carPlate.getItems().addAll(carDAO.getAllCarPlates());

        StaffDAO staffDAO = new StaffDAO();
        List<String> staffIds = staffDAO.getAllStaffIds();

        // Populate both dropdowns
        pickupStaff.getItems().addAll(staffIds);
        returnStaff.getItems().addAll(staffIds);

        BranchDAO branchDAO = new BranchDAO();
        branchId.getItems().addAll(branchDAO.getAllBranchIds());

        statusBox.getItems().setAll(RentalRecord.RentalStatus.values());
        statusBox.setValue(RentalRecord.RentalStatus.UPCOMING);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.addRow(0, new Label("Renter DL:"), renterDL);
        grid.addRow(1, new Label("Car Plate:"), carPlate);
        grid.addRow(2, new Label("Branch:"), branchId);
        grid.addRow(3, new Label("Pickup Staff:"), pickupStaff);
        grid.addRow(4, new Label("Return Staff:"), returnStaff);

        grid.addRow(5, new Label("Pickup Date:"), pickupDate, pickupTime);
        grid.addRow(6, new Label("Expected Return:"), expectedReturnDate, expectedReturnTime);
        grid.addRow(7, new Label("Actual Return:"), actualReturnDate, actualReturnTime);

        grid.addRow(8, new Label("Total Payment:"), paymentField);
        grid.addRow(9, new Label("Status:"), statusBox);

        dialog.getDialogPane().setContent(grid);

        Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);

        renterDL.valueProperty().addListener((obs, oldV, newV) -> validateAddFields(addButton, renterDL, carPlate));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton != addButtonType) return null;

            // Validate
            if (pickupDate.getValue().isAfter(expectedReturnDate.getValue())) {
                showSuccessPopup("Error", "Pickup date must be before expected return date.");
                return null;
            }

            LocalDateTime rentalDateTime = LocalDateTime.now(); // AUTO GENERATED

            LocalDateTime pickup = LocalDateTime.of(pickupDate.getValue(), parseTime(pickupTime.getText()));
            LocalDateTime expectedReturn = LocalDateTime.of(expectedReturnDate.getValue(), parseTime(expectedReturnTime.getText()));

            LocalDateTime actualReturn = null;
            if (actualReturnDate.getValue() != null && !actualReturnTime.getText().isEmpty()) {
                actualReturn = LocalDateTime.of(actualReturnDate.getValue(), parseTime(actualReturnTime.getText()));
            }

            BigDecimal payment = new BigDecimal(paymentField.getText());

            RentalRecord.RentalStatus status = statusBox.getValue();
            if (status == RentalRecord.RentalStatus.COMPLETED && actualReturn == null) {
                showSuccessPopup("Error", "Completed rentals must have an actual return datetime.");
                return null;
            }

            return new RentalRecord(
                    null, // AUTO
                    renterDL.getValue(),
                    carPlate.getValue(),
                    branchId.getValue(),
                    pickupStaff.getValue(),
                    returnStaff.getValue(),
                    rentalDateTime,
                    pickup,
                    expectedReturn,
                    actualReturn,
                    payment,
                    status
            );
        });

        Optional<?> result = dialog.showAndWait();

        result.ifPresent(obj -> {
            RentalRecord rental = (RentalRecord) obj;
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


    public void showSuccessPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Additional method to modify a rental
    public void showModifyRentalPopup(RentalDAO dao, RentalRecord rental, Runnable refresh) {
        Dialog<RentalRecord> dialog = new Dialog<>();
        dialog.setTitle("Modify Rental");

        TextField renterDLField = new TextField(rental.getRenterDlNumber());
        TextField carPlateField = new TextField(rental.getCarPlateNumber());
        DatePicker pickupDatePicker = new DatePicker(rental.getPickupDateTime().toLocalDate());
        DatePicker returnDatePicker = new DatePicker(rental.getExpectedReturnDateTime().toLocalDate());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Renter DL:"), 0, 0);
        grid.add(renterDLField, 1, 0);
        grid.add(new Label("Car Plate:"), 0, 1);
        grid.add(carPlateField, 1, 1);
        grid.add(new Label("Pickup Date:"), 0, 2);
        grid.add(pickupDatePicker, 1, 2);
        grid.add(new Label("Return Date:"), 0, 3);
        grid.add(returnDatePicker, 1, 3);

        dialog.getDialogPane().setContent(grid);
        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                rental.setRenterDlNumber(renterDLField.getText());
                rental.setCarPlateNumber(carPlateField.getText());
                rental.setPickupDateTime(pickupDatePicker.getValue().atStartOfDay());
                rental.setExpectedReturnDateTime(returnDatePicker.getValue().atStartOfDay());
                return rental;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedRental -> {
            try {
                dao.updateRental(updatedRental);
                refresh.run();
                showSuccessPopup("Success", "Rental updated successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showSuccessPopup("Error", "Failed to update rental.");
            }
        });
    }

    private LocalTime parseTime(String time) {
        return LocalTime.parse(time.trim());
    }

    private void validateAddFields(Node addButton, ComboBox<String> renter, ComboBox<String> car) {
        addButton.setDisable(renter.getSelectionModel().isEmpty() || car.getSelectionModel().isEmpty());
    }

}