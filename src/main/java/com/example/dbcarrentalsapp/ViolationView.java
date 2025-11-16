// ViolationView.java
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
import model.ViolationRecord;
import model.RentalDetails;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * View class for the violation processing interface.
 * Provides the user interface for processing violations, generating reports,
 * and viewing violation history.
 */
public class ViolationView {

    public Button processButton, reportButton, returnButton, refreshButton;
    public TextField rentalIdField;
    public ComboBox<String> branchFilterComboBox;
    public TableView<ViolationRecord> tableView;
    private final Scene scene;

    public ViolationView() {
        // Background setup
        StackPane root = new StackPane();
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png"));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // Title
        Text title = new Text("PROCESS VIOLATIONS");
        Font f1Font = Font.loadFont(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 48);
        title.setFont(f1Font != null ? f1Font : Font.font("Arial Black", 48));
        title.setStyle("-fx-fill: white; -fx-font-style: italic; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);");
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(100, 0, 0, 0));
        root.getChildren().add(title);

        // Input Section
        Label rentalIdLabel = new Label("Rental ID:");
        rentalIdLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        rentalIdField = new TextField();
        rentalIdField.setPromptText("Enter Rental ID");
        rentalIdField.setPrefWidth(200);
        rentalIdField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        processButton = new Button("Process Violation");
        processButton.getStyleClass().add("small-button");

        HBox inputBox = new HBox(15, rentalIdLabel, rentalIdField, processButton);
        inputBox.setAlignment(Pos.CENTER);

        // Filter Section
        Label branchFilterLabel = new Label("Filter by Branch:");
        branchFilterLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        branchFilterComboBox = new ComboBox<>();
        branchFilterComboBox.getItems().add("All");
        branchFilterComboBox.setValue("All");
        branchFilterComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate branch IDs
        ViolationDAO dao = new ViolationDAO();
        branchFilterComboBox.getItems().addAll(dao.getAllBranchIds());

        reportButton = new Button("Generate Report");
        reportButton.getStyleClass().add("small-button");

        refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("small-button");

        HBox filterBox = new HBox(15, branchFilterLabel, branchFilterComboBox, reportButton, refreshButton);
        filterBox.setAlignment(Pos.CENTER);

        // Table
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setPrefHeight(300);
        tableView.setStyle("-fx-background-color: rgba(25,25,35,0.85);");

        TableColumn<ViolationRecord, String> idCol = new TableColumn<>("Violation ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("violationId"));

        TableColumn<ViolationRecord, String> rentalCol = new TableColumn<>("Rental ID");
        rentalCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));

        TableColumn<ViolationRecord, String> typeCol = new TableColumn<>("Violation Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));

        TableColumn<ViolationRecord, Double> penaltyCol = new TableColumn<>("Penalty Fee");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("penaltyFee"));

        TableColumn<ViolationRecord, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<ViolationRecord, Integer> durationCol = new TableColumn<>("Duration Hours");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationHours"));

        TableColumn<ViolationRecord, Date> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        tableView.getColumns().addAll(idCol, rentalCol, typeCol, penaltyCol, reasonCol, durationCol, dateCol);

        // Return Button
        returnButton = new Button("Return");
        returnButton.getStyleClass().add("small-button");

        // Card Container
        VBox tableCard = new VBox(15, inputBox, filterBox, tableView, returnButton);
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

        // Layout
        VBox layout = new VBox(20, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 50, 50, 50));

        root.getChildren().add(layout);

        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm());
    }

    public void showViolationProcessingPopup(ViolationDAO dao, RentalDetails rental, double penalty, Runnable reloadCallback) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Process Violation");

        // Violation details
        Label rentalLabel = new Label("Rental ID: " + rental.getRentalId());
        rentalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label renterLabel = new Label("Renter DL: " + rental.getRenterDlNumber());
        renterLabel.setStyle("-fx-text-fill: white;");

        Label carLabel = new Label("Car Plate: " + rental.getCarPlateNumber());
        carLabel.setStyle("-fx-text-fill: white;");

        Label penaltyLabel = new Label("Calculated Penalty: ₱" + String.format("%.2f", penalty));
        penaltyLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold;");

        // Violation type selection
        Label typeLabel = new Label("Violation Type:");
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Late Return", "Car Damage", "Traffic Violation", "Cleaning Fee", "Other");
        typeComboBox.setValue("Late Return");
        typeComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Reason input
        Label reasonLabel = new Label("Reason:");
        reasonLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextArea reasonTextArea = new TextArea();
        reasonTextArea.setPrefHeight(80);
        reasonTextArea.setPromptText("Enter violation details...");
        reasonTextArea.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff;");

        // Staff ID
        Label staffLabel = new Label("Staff ID:");
        staffLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        TextField staffField = new TextField();
        staffField.setPromptText("Enter your Staff ID");
        staffField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Button processBtn = new Button("Record Violation");
        Button cancelBtn = new Button("Cancel");
        processBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");
        processBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        processBtn.setOnAction(e -> {
            if (staffField.getText().trim().isEmpty() || reasonTextArea.getText().trim().isEmpty()) {
                message.setText("Please fill in all fields.");
                message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                return;
            }

            // Calculate duration in hours for late returns
            int durationHours = 0;
            if (typeComboBox.getValue().equals("Late Return")) {
                long diffMs = rental.getActualReturnDatetime().getTime() - rental.getExpectedReturnDatetime().getTime();
                durationHours = (int) ((diffMs / (1000 * 60 * 60)) + 1);
            }

            // Generate auto-incrementing violation ID
            String violationId = dao.generateNextViolationId();

            // Create violation record
            ViolationRecord violation = new ViolationRecord(
                    violationId, // Auto-generated ID
                    rental.getRentalId(),
                    staffField.getText().trim(),
                    typeComboBox.getValue(),
                    penalty,
                    reasonTextArea.getText().trim(),
                    durationHours,
                    new java.util.Date()
            );

            // Record violation and update car status
            boolean success = dao.recordViolation(violation);
            if (success) {
                dao.updateCarStatus(rental.getCarPlateNumber(), "Available");
                reloadCallback.run();
                popup.close();
                generateViolationReceipt(rental, violation);
                showSuccessPopup("Success", "Violation recorded successfully!\nViolation ID: " + violationId);
            } else {
                message.setText("Failed to record violation.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, processBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox content = new VBox(15, rentalLabel, renterLabel, carLabel, penaltyLabel,
                typeLabel, typeComboBox, reasonLabel, reasonTextArea,
                staffLabel, staffField, buttonBox, message);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15;");

        Scene popupScene = new Scene(content, 450, 500);
        popupScene.getStylesheets().add(getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm());
        popup.setScene(popupScene);
        popup.showAndWait();
    }

    public void generateViolationReport(List<ViolationRecord> violations) {
        Stage reportStage = new Stage();
        reportStage.initModality(Modality.APPLICATION_MODAL);
        reportStage.setTitle("Violations Report");

        TextArea reportArea = new TextArea();
        reportArea.setEditable(false);
        reportArea.setPrefSize(600, 400);
        reportArea.setStyle("-fx-control-inner-background: #2a2a3a; -fx-text-fill: white;");

        StringBuilder report = new StringBuilder();
        report.append("VIOLATIONS REPORT\n");
        report.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())).append("\n");
        report.append("=").append(repeat("=", 50)).append("\n\n");

        double totalPenalties = 0;
        for (ViolationRecord violation : violations) {
            report.append("Violation ID: ").append(violation.getViolationId()).append("\n");
            report.append("Rental ID: ").append(violation.getRentalId()).append("\n");
            report.append("Type: ").append(violation.getViolationType()).append("\n");
            report.append("Penalty: ₱").append(String.format("%.2f", violation.getPenaltyFee())).append("\n");
            report.append("Reason: ").append(violation.getReason()).append("\n");
            report.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(violation.getTimestamp())).append("\n");
            report.append("-".repeat(40)).append("\n");
            totalPenalties += violation.getPenaltyFee();
        }

        report.append("\nTOTAL PENALTIES: ₱").append(String.format("%.2f", totalPenalties));
        report.append("\nTOTAL VIOLATIONS: ").append(violations.size());

        reportArea.setText(report.toString());

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("small-button");
        closeBtn.setOnAction(e -> reportStage.close());

        VBox layout = new VBox(20, reportArea, closeBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98);");

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm());
        reportStage.setScene(scene);
        reportStage.showAndWait();
    }

    private void generateViolationReceipt(RentalDetails rental, ViolationRecord violation) {
        Stage receiptStage = new Stage();
        receiptStage.initModality(Modality.APPLICATION_MODAL);
        receiptStage.setTitle("Violation Receipt");

        TextArea receiptArea = new TextArea();
        receiptArea.setEditable(false);
        receiptArea.setPrefSize(400, 300);
        receiptArea.setStyle("-fx-control-inner-background: #2a2a3a; -fx-text-fill: white;");

        StringBuilder receipt = new StringBuilder();
        receipt.append("VIOLATION RECEIPT\n");
        receipt.append("=").append(repeat("=", 30)).append("\n");
        receipt.append("Violation ID: ").append(violation.getViolationId()).append("\n");
        receipt.append("Rental ID: ").append(rental.getRentalId()).append("\n");
        receipt.append("Renter DL: ").append(rental.getRenterDlNumber()).append("\n");
        receipt.append("Car Plate: ").append(rental.getCarPlateNumber()).append("\n");
        receipt.append("Violation Type: ").append(violation.getViolationType()).append("\n");
        receipt.append("Penalty Fee: ₱").append(String.format("%.2f", violation.getPenaltyFee())).append("\n");
        receipt.append("Reason: ").append(violation.getReason()).append("\n");
        receipt.append("Processed by Staff: ").append(violation.getStaffId()).append("\n");
        receipt.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(violation.getTimestamp())).append("\n");

        receiptArea.setText(receipt.toString());

        Button closeBtn = new Button("Close");
        closeBtn.getStyleClass().add("small-button");
        closeBtn.setOnAction(e -> receiptStage.close());

        VBox layout = new VBox(20, receiptArea, closeBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98);");

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm());
        receiptStage.setScene(scene);
        receiptStage.showAndWait();
    }

    // Utility methods
    private String repeat(String str, int times) {
        return new String(new char[times]).replace("\0", str);
    }

    public void showErrorPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showSuccessPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}