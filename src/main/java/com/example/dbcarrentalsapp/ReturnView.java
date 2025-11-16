package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.RentalRecord;
import model.ReturnRecord;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

public class ReturnView {

    public Button backButton, addButton, modifyButton, deleteButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<ReturnRecord> tableView;
    private final Scene scene;

    public ReturnView() {
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
        Text title = new Text("MANAGE RETURNS");
        title.setStyle("-fx-font-size: 48px; -fx-fill: white; -fx-font-weight: bold;");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(50, 0, 0, 0));
        root.getChildren().add(title);

        // ===== Back Button =====
        backButton = new Button("Return");

        // ===== Search Bar =====
        searchField = new TextField();
        searchField.setPromptText("Search return ID or renter ID");
        searchField.setPrefWidth(300);

        filterButton = new Button("Filter");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<ReturnRecord, String> idCol = new TableColumn<>("Return ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("returnId"));

        TableColumn<ReturnRecord, String> rentalIDCol = new TableColumn<>("Rental ID");
        rentalIDCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("renterID"));

        TableColumn<ReturnRecord, String> staffIdCol = new TableColumn<>("Staff ID");
        staffIdCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("staffID"));

        TableColumn<ReturnRecord, Void> receiptCol = new TableColumn<>("Receipt");
        receiptCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View Receipt");
            {
                btn.setOnAction(e -> {
                    ReturnRecord record = getTableView().getItems().get(getIndex());
                    showReturnReceipt(record);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });

        tableView.getColumns().addAll(idCol, rentalIDCol, staffIdCol, receiptCol);

        // ===== Buttons =====
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        returnButton = new Button("Select");

        HBox buttonBox = new HBox(15, returnButton, backButton);
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

        // ===== Return Button Action =====
        returnButton.setOnAction(e -> {
            ReturnRecord selected = tableView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showPopup("No Selection", "Please select a rental to return.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Return");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to process this return?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) return;

            try {
                String currentStaffId = "STAFF001"; // Replace with logged-in staff ID
                processReturn(selected.getReturnRentalID(), currentStaffId);
                refreshTable();

            } catch (Exception ex) {
                ex.printStackTrace();
                showPopup("Error", "Failed to process return.");
            }
        });
    }

    public Scene getScene() {
        return scene;
    }

    private void processReturn(String rentalId, String staffId) throws SQLException {
        RentalDAO rentalDAO = new RentalDAO();
        CarDAO carDAO = new CarDAO();
        ReturnDAO returnDAO = new ReturnDAO();
        BranchDAO branchDAO = new BranchDAO();

        // Step 1: Get rental details
        RentalRecord rental = rentalDAO.getRentalById(rentalId);
        if (rental == null) {
            showPopup("Error", "Rental not found.");
            return;
        }

        // Step 2: Validate rental status
        if (rental.getRentalStatus() != RentalRecord.RentalStatus.ACTIVE) {
            showPopup("Invalid Return", "This rental is not active.");
            return;
        }

        // Step 3: Update car status
        carDAO.updateCarStatus(rental.getCarPlateNumber(), "Available");

        // Step 4: Record return
        ReturnRecord returnRecord = new ReturnRecord(null,  rental.getRentalId(), staffId);
        returnDAO.addReturn(returnRecord);

        // Step 5: Update rental status
        rental.setRentalStatus(RentalRecord.RentalStatus.COMPLETED);
        rentalDAO.updateRentalStatus(rental);

        // Step 6: Show automatic receipt
        showReturnReceipt(returnRecord);
    }
    // ===== Show Receipt Popup =====
    private void showReturnReceipt(ReturnRecord record) {
        try {
            RentalDAO rentalDAO = new RentalDAO();
            RentalRecord rental = rentalDAO.getRentalById(record.getReturnRentalID());
            if (rental == null) {
                showPopup("Error", "Rental details not found.");
                return;
            }

            Alert receipt = new Alert(Alert.AlertType.INFORMATION);
            receipt.setTitle("Return Receipt");
            receipt.setHeaderText("Car Returned Successfully!");
            receipt.setContentText(
                    "Rental ID: " + rental.getRentalId() + "\n" +
                            "Renter DL: " + rental.getRenterDlNumber() + "\n" +
                            "Car Plate: " + rental.getCarPlateNumber() + "\n" +
                            "Return Date & Time: " + LocalDateTime.now() + "\n" +
                            "Total Payment: " + rental.getTotalPayment()
            );
            receipt.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
            showPopup("Error", "Failed to generate receipt.");
        }
    }

    private void showPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshTable() {
        ReturnDAO returnDAO = new ReturnDAO();
        tableView.getItems().setAll(returnDAO.getAllReturns());
    }
}

