package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/mclaren_speedtail_2-1920x1080.jpg")); // Make sure the image path is correct
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
        deleteButton = new Button("Delete");
        returnButton = new Button("Return");

        HBox buttonBox = new HBox(15, addButton, modifyButton, deleteButton, returnButton);
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

    // Getter for Scene
    public Scene getScene() {
        return scene;
    }

    // =============================
    //       POPUP HELPERS
    // =============================

    public void showSuccessPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public boolean showConfirmPopup(RentalRecord record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete this rental?");
        alert.setContentText(
                "Rental ID: " + record.getRentalId() + "\n" +
                        "Renter DL: " + record.getRenterDlNumber() + "\n" +
                        "Car Plate: " + record.getCarPlateNumber() + "\n" +
                        "Start: " + record.getPickupDateTime() + "\n" +
                        "Expected Return: " + record.getExpectedReturnDateTime() + "\n" +
                        "Status: " + record.getRentalStatus() + "\n\n" +
                        "This cannot be undone."
        );

        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    // =============================
    //     PLACEHOLDER POPUPS
    // =============================

    public void showAddRentalPopup(RentalDAO dao, Runnable refresh) {
        Dialog<RentalRecord> dialog = new Dialog<>();
        dialog.setTitle("Add New Rental");

        // Renters: Fields
        TextField renterDLField = new TextField();
        renterDLField.setPromptText("Driver's License Number");

        TextField carPlateField = new TextField();
        carPlateField.setPromptText("Car Plate Number");

        TextField branchIdField = new TextField();
        branchIdField.setPromptText("Branch ID");

        // Add more fields for initial details
        // You can use ComboBoxes here if data exists (example: renter, staff lists)

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Renter DL:"), 0, 0);
        grid.add(renterDLField, 1, 0);

        grid.add(new Label("Car Plate:"), 0, 1);
        grid.add(carPlateField, 1, 1);

        grid.add(new Label("Branch ID:"), 0, 2);
        grid.add(branchIdField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType addButton = new ButtonType("Add Rental", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                // Create new RentalRecord with dummy/default values for now
                return new RentalRecord(
                        "RENT" + System.currentTimeMillis(), // ID
                        renterDLField.getText(),
                        carPlateField.getText(),
                        branchIdField.getText(),
                        null, null, // staff IDs
                        LocalDateTime.now(), // created now
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(3),
                        null, // actual return
                        BigDecimal.ZERO, // total payment
                        RentalRecord.RentalStatus.ACTIVE
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(record -> {
            try {
                dao.addRental(record);
                refresh.run();
                showSuccessPopup("Success", "Rental added successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                showSuccessPopup("Error", "Failed to add rental.");
            }
        });
    }


    public void showModifyRentalPopup(RentalDAO dao, RentalRecord record, Runnable refresh) {
        System.out.println("<TODO: MODIFY RENTAL POPUP>");
    }
}
