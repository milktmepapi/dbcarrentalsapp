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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * The ViolationView class represents the user interface for managing violations.
 * It provides a comprehensive GUI for viewing, adding, modifying, and deleting
 * violation records in the car rental system.
 *
 * This class follows the View layer in MVC architecture and features:
 * - Table display of all violations
 * - Search and filter functionality
 * - Add/Modify/Delete operations via dialog popups
 * - Input validation and user feedback
 * - Consistent styling with the rest of the application
 *
 */

public class ViolationView {
    // Original UI Components
    public Button addButton, modifyButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<ViolationRecord> tableView;

    // New UI Components for automated features
    public Button processReturnButton, generateReceiptButton, checkOverdueButton;

    private final Scene scene;

    public ViolationView() {
        // ===== BACKGROUND SETUP =====
        StackPane root = new StackPane();
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png"));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // ===== TITLE SETUP =====
        Text title = new Text("MANAGE VIOLATIONS");
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

        // ===== SEARCH BAR SETUP =====
        searchField = new TextField();
        searchField.setPromptText("Search violation ID, rental ID, or type...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");
        filterButton.setPrefWidth(100);

        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== TABLE VIEW SETUP =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setPrefHeight(280);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getStyleClass().add("custom-table");

        // Define table columns (same as before)
        TableColumn<ViolationRecord, String> idCol = createTableColumn("Violation ID", "violationId", 90, 120);
        TableColumn<ViolationRecord, String> rentalCol = createTableColumn("Rental ID", "rentalId", 80, 100);
        TableColumn<ViolationRecord, String> staffCol = createTableColumn("Staff ID", "staffId", 70, 90);
        TableColumn<ViolationRecord, String> typeCol = createTableColumn("Type", "violationType", 100, 130);

        TableColumn<ViolationRecord, Double> penaltyCol = new TableColumn<>("Penalty");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("penaltyFee"));
        penaltyCol.setPrefWidth(70);
        penaltyCol.setMaxWidth(90);
        penaltyCol.setCellFactory(column -> new TableCell<ViolationRecord, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-text-fill: black;");
                }
            }
        });

        TableColumn<ViolationRecord, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setPrefWidth(150);
        reasonCol.setMaxWidth(200);
        reasonCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Text text = new Text(item);
                    text.setWrappingWidth(reasonCol.getWidth() - 10);
                    text.setStyle("-fx-fill: black;");
                    setGraphic(text);
                }
            }
        });

        TableColumn<ViolationRecord, Integer> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationHours"));
        durationCol.setPrefWidth(70);
        durationCol.setMaxWidth(90);
        durationCol.setCellFactory(column -> new TableCell<ViolationRecord, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + "h");
                    setStyle("-fx-text-fill: black;");
                }
            }
        });

        TableColumn<ViolationRecord, String> timestampCol = new TableColumn<>("Time");
        timestampCol.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            String formatted = timestamp != null ?
                    timestamp.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        timestampCol.setPrefWidth(120);
        timestampCol.setMaxWidth(140);
        timestampCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: black;");
                }
            }
        });

        tableView.getColumns().addAll(
                idCol, rentalCol, staffCol, typeCol, penaltyCol, reasonCol, durationCol, timestampCol
        );

        // ===== BUTTON SETUP =====
        // Original buttons
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        returnButton = new Button("Return");

        // New buttons for automated features
        processReturnButton = new Button("Process Return");
        generateReceiptButton = new Button("Generate Receipt");
        checkOverdueButton = new Button("Check Overdue");

        // Style all buttons
        String buttonStyle = "small-button";
        addButton.getStyleClass().add(buttonStyle);
        modifyButton.getStyleClass().add(buttonStyle);
        returnButton.getStyleClass().add(buttonStyle);
        processReturnButton.getStyleClass().add(buttonStyle);
        generateReceiptButton.getStyleClass().add(buttonStyle);
        checkOverdueButton.getStyleClass().add(buttonStyle);

        addButton.setPrefWidth(120);
        modifyButton.setPrefWidth(120);
        returnButton.setPrefWidth(120);
        processReturnButton.setPrefWidth(140);
        generateReceiptButton.setPrefWidth(140);
        checkOverdueButton.setPrefWidth(140);

        // Button layout - two rows for better organization
        HBox topButtonBox = new HBox(15, addButton, modifyButton, processReturnButton);
        topButtonBox.setAlignment(Pos.CENTER);

        HBox bottomButtonBox = new HBox(15, generateReceiptButton, checkOverdueButton, returnButton);
        bottomButtonBox.setAlignment(Pos.CENTER);

        VBox buttonBox = new VBox(10, topButtonBox, bottomButtonBox);
        buttonBox.setAlignment(Pos.CENTER);

        // ===== CARD CONTAINER SETUP =====
        VBox tableCard = new VBox(15, tableView, buttonBox);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(950);
        tableCard.setStyle(
                "-fx-background-color: rgba(25,25,35,0.85);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);" +
                        "-fx-border-radius: 15;" +
                        "-fx-border-width: 2;"
        );

        // ===== MAIN LAYOUT SETUP =====
        VBox layout = new VBox(30, searchBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 0, 0, 0));
        root.getChildren().add(layout);

        // ===== SCENE AND STYLING SETUP =====
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    /**
     * Helper method to create table columns with consistent styling
     */
    private TableColumn<ViolationRecord, String> createTableColumn(String title, String property, double prefWidth, double maxWidth) {
        TableColumn<ViolationRecord, String> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(prefWidth);
        column.setMaxWidth(maxWidth);
        column.setCellFactory(col -> new TableCell<ViolationRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: black;");
                }
            }
        });
        return column;
    }

    public Scene getScene() {
        return scene;
    }

    /**
     * NEW: Shows popup for processing car returns with automatic violation detection
     */
    public void showProcessReturnPopup(ViolationDAO dao, Runnable refresh) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Process Car Return");

        // Input fields
        Label rentalLabel = new Label("Select Rental to Return:");
        rentalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> rentalComboBox = new ComboBox<>();
        rentalComboBox.setPromptText("Select Rental ID");
        rentalComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(rentalComboBox);

        Label staffLabel = new Label("Staff ID Processing Return:");
        staffLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        ComboBox<String> staffComboBox = new ComboBox<>();
        staffComboBox.setPromptText("Select Staff ID");
        staffComboBox.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(staffComboBox);

        // Info display
        TextArea infoArea = new TextArea();
        infoArea.setEditable(false);
        infoArea.setPrefHeight(120);
        infoArea.setStyle("-fx-control-inner-background: #1a1a2a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        infoArea.setText("Select a rental to see details...");

        // Populate dropdowns
        try {
            rentalComboBox.getItems().addAll(dao.getActiveRentalsForReturn());
            staffComboBox.getItems().addAll(dao.getAllStaffIds());
        } catch (Exception e) {
            e.printStackTrace();
            infoArea.setText("Error loading data: " + e.getMessage());
        }

        // Update info when rental selection changes
        rentalComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    StringBuilder info = new StringBuilder();
                    info.append("Rental ID: ").append(newVal).append("\n");

                    // Check if late
                    boolean isLate = dao.isLateReturn(newVal);
                    int lateHours = dao.calculateLateHours(newVal);
                    double penalty = dao.calculateLatePenalty(newVal);

                    if (isLate) {
                        info.append("Status: LATE RETURN\n");
                        info.append(String.format("Late by: %d hours\n", lateHours));
                        info.append(String.format("Penalty Fee: $%.2f\n", penalty));
                        info.append("\n⚠️ This will automatically create a late return violation!");
                    } else {
                        info.append("Status: ON TIME\n");
                        info.append("No penalty fees applicable.");
                    }

                    infoArea.setText(info.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    infoArea.setText("Error loading rental details: " + e.getMessage());
                }
            }
        });

        // Buttons
        Button processBtn = new Button("Process Return");
        Button cancelBtn = new Button("Cancel");

        processBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        processBtn.setOnAction(e -> {
            try {
                if (rentalComboBox.getValue() == null || staffComboBox.getValue() == null) {
                    message.setText("Please select both rental and staff!");
                    message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    return;
                }

                String rentalId = rentalComboBox.getValue();
                String staffId = staffComboBox.getValue();

                // Process the return
                ViolationRecord violation = dao.processCarReturn(rentalId, staffId);

                if (violation != null) {
                    showSuccessPopup("Return Processed",
                            "Car returned successfully!\n\n" +
                                    "Late return violation automatically created:\n" +
                                    "• Violation ID: " + violation.getViolationId() + "\n" +
                                    "• Penalty: $" + violation.getPenaltyFee() + "\n" +
                                    "• Duration: " + violation.getDurationHours() + " hours late");
                } else {
                    showSuccessPopup("Return Processed",
                            "Car returned successfully and marked as available.\nNo late return detected.");
                }

                refresh.run();
                popup.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Failed to process return: " + ex.getMessage());
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, processBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                rentalLabel, rentalComboBox,
                staffLabel, staffComboBox,
                new Label("Return Details:"), infoArea,
                buttonBox,
                message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 500, 500);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /**
     * NEW: Shows receipt in a popup window
     */
    public void showReceiptPopup(String title, String receiptContent) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle(title);

        TextArea receiptArea = new TextArea(receiptContent);
        receiptArea.setEditable(false);
        receiptArea.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px; -fx-control-inner-background: white; -fx-text-fill: black;");
        receiptArea.setPrefSize(600, 500);

        Button printBtn = new Button("Print");
        Button closeBtn = new Button("Close");

        printBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        closeBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold;");

        printBtn.setOnAction(e -> {
            // Basic print functionality - could be enhanced with proper printing
            showSuccessPopup("Print", "Receipt sent to printer.");
        });

        closeBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, printBtn, closeBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(15, receiptArea, buttonBox);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15;");

        Scene scene = new Scene(layout);
        popup.setScene(scene);
        popup.showAndWait();
    }
    /**
     * Sorts the table by violation ID in ascending order
     */
    public void sortByViolationId() {
        tableView.getSortOrder().clear();

        // Find the violation ID column
        for (TableColumn<ViolationRecord, ?> column : tableView.getColumns()) {
            if ("Violation ID".equals(column.getText())) {
                tableView.getSortOrder().add(column);
                column.setSortType(TableColumn.SortType.ASCENDING);
                break;
            }
        }

        tableView.sort();
    }

    /**
     * Sets a custom ButtonCell for a ComboBox to ensure its text (both prompt and selected)
     * is displayed in white, overriding any conflicting CSS.
     *
     * @param comboBox The ComboBox to modify.
     */
    private void setComboBoxTextWhite(ComboBox<String> comboBox) {
        comboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    // Use prompt text if empty
                    setText(comboBox.getPromptText());
                } else {
                    // Use item text if selected
                    setText(item);
                }
                // Always set text fill to white
                setStyle("-fx-text-fill: white;");
            }
        });
    }

    /**
     * Displays a popup dialog for adding a new violation record.
     * Includes input validation and dynamic field enabling.
     *
     * @param dao Data Access Object for violation operations
     * @param refresh Callback function to refresh the table after addition
     */
    public void showAddViolationPopup(ViolationDAO dao, Runnable refresh) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Violation");

        // Input fields
        Label rentalLabel = new Label("Rental ID:");
        rentalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> rentalId = new ComboBox<>();
        rentalId.setPromptText("Select Rental ID");
        rentalId.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(rentalId); // Force white text

        Label staffLabel = new Label("Staff ID:");
        staffLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> staffId = new ComboBox<>();
        staffId.setPromptText("Select Staff ID");
        staffId.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(staffId); // Force white text

        Label typeLabel = new Label("Violation Type:");
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> violationType = new ComboBox<>();
        violationType.setPromptText("Select Violation Type");
        violationType.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(violationType); // Force white text

        Label penaltyLabel = new Label("Penalty Fee:");
        penaltyLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField penaltyFee = new TextField();
        penaltyFee.setPromptText("Enter penalty amount");
        penaltyFee.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label reasonLabel = new Label("Reason:");
        reasonLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea reason = new TextArea();
        reason.setPromptText("Enter violation details...");
        reason.setPrefHeight(80);
        // Set inner background to white and text to black
        reason.setStyle("-fx-control-inner-background: white; -fx-text-fill: black; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label durationLabel = new Label("Duration Hours:");
        durationLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField durationHours = new TextField("0");
        durationHours.setPromptText("Enter duration in hours");
        durationHours.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate dropdowns with data from database
        try {
            rentalId.getItems().addAll(dao.getAllRentalIds());
            // Don't populate staffId here - it will be populated dynamically based on rental selection
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set violation type options
        violationType.getItems().addAll("Late Return", "Car Damage", "Traffic Violation", "Cleaning Fee", "Other");

        // Dynamic staff loading based on rental selection
        rentalId.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    // Get the branch of the selected rental
                    String branchId = dao.getRentalBranchId(newVal);
                    // Get Operations staff from that branch
                    List<String> operationsStaff = dao.getOperationsStaffByBranch(branchId);

                    staffId.getItems().clear();
                    staffId.getItems().addAll(operationsStaff);

                    if (operationsStaff.isEmpty()) {
                        showSuccessPopup("No Staff Available",
                                "No Operations staff available in branch " + branchId +
                                        " for rental " + newVal + ". Please select a different rental.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showSuccessPopup("Error", "Error loading staff for selected rental: " + ex.getMessage());
                }
            }
        });

        // Buttons
        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        addBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        // Improved button styling with colors
        addBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        addBtn.setOnAction(e -> {
            try {
                // Validate mandatory fields
                if (rentalId.getValue() == null || staffId.getValue() == null ||
                        violationType.getValue() == null || penaltyFee.getText().trim().isEmpty()) {
                    message.setText("Please fill in all mandatory fields!");
                    message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    return;
                }

                // Validate numeric fields
                double penalty = Double.parseDouble(penaltyFee.getText());
                int duration = Integer.parseInt(durationHours.getText());

                if (penalty < 0 || duration < 0) {
                    message.setText("Penalty and duration must be positive values!");
                    message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    return;
                }

                // NEW: Validate staff assignment
                if (!dao.validateStaffForViolation(staffId.getValue(), rentalId.getValue())) {
                    message.setText("Invalid staff selection! Staff must be from Operations department and same branch as rental.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Generate new ID and create violation record
                String generatedId = dao.generateNextViolationId();
                ViolationRecord violation = new ViolationRecord(
                        generatedId,
                        rentalId.getValue(),
                        staffId.getValue(),
                        violationType.getValue(),
                        penalty,
                        reason.getText(),
                        duration,
                        LocalDateTime.now()
                );

                dao.addViolation(violation);
                refresh.run();
                popup.close();
                showSuccessPopup("Success", "Violation added successfully!");
            } catch (NumberFormatException ex) {
                message.setText("Please enter valid numbers for penalty and duration!");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Failed to add violation. Please try again.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                rentalLabel, rentalId,
                staffLabel, staffId,
                typeLabel, violationType,
                penaltyLabel, penaltyFee,
                durationLabel, durationHours,
                reasonLabel, reason,
                buttonBox,
                message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 400, 600);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /**
     * Displays a popup dialog for modifying an existing violation record.
     * All fields except violation ID are editable.
     *
     * @param dao Data Access Object for violation operations
     * @param violation The violation record to modify
     * @param refresh Callback function to refresh the table after modification
     */
    public void showModifyViolationPopup(ViolationDAO dao, ViolationRecord violation, Runnable refresh) {
        if (violation == null) {
            showSuccessPopup("No Selection", "Please select a violation to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Violation");

        // Read-only field for violation ID
        Label idLabel = new Label("Violation ID:");
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField violationIdField = new TextField(violation.getViolationId());
        violationIdField.setEditable(false);
        violationIdField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Editable fields with current values pre-populated
        Label rentalLabel = new Label("Rental ID:");
        rentalLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> rentalId = new ComboBox<>();
        rentalId.setPromptText("Select Rental ID");
        rentalId.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(rentalId); // Force white text

        Label staffLabel = new Label("Staff ID:");
        staffLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> staffId = new ComboBox<>();
        staffId.setPromptText("Select Staff ID");
        staffId.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(staffId); // Force white text

        Label typeLabel = new Label("Violation Type:");
        typeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        ComboBox<String> violationType = new ComboBox<>();
        violationType.setPromptText("Select Violation Type");
        violationType.setStyle("-fx-background-color: #2a2a3a; -fx-border-color: #7a40ff; -fx-border-radius: 5;");
        setComboBoxTextWhite(violationType); // Force white text

        Label penaltyLabel = new Label("Penalty Fee:");
        penaltyLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField penaltyFee = new TextField(String.valueOf(violation.getPenaltyFee()));
        penaltyFee.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label reasonLabel = new Label("Reason:");
        reasonLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea reason = new TextArea(violation.getReason());
        reason.setPrefHeight(80);
        // Set inner background to white and text to black
        reason.setStyle("-fx-control-inner-background: white; -fx-text-fill: black; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        Label durationLabel = new Label("Duration Hours:");
        durationLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField durationHours = new TextField(String.valueOf(violation.getDurationHours()));
        durationHours.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate dropdowns with current data
        try {
            rentalId.getItems().addAll(dao.getAllRentalIds());
            // Don't populate staffId here - it will be populated dynamically based on rental selection
        } catch (Exception e) {
            e.printStackTrace();
        }

        violationType.getItems().addAll("Late Return", "Car Damage", "Traffic Violation", "Cleaning Fee", "Other");

        // Set current values in fields
        rentalId.setValue(violation.getRentalId());
        violationType.setValue(violation.getViolationType());

        // Dynamic staff loading based on rental selection
        rentalId.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    // Get the branch of the selected rental
                    String branchId = dao.getRentalBranchId(newVal);
                    // Get Operations staff from that branch
                    List<String> operationsStaff = dao.getOperationsStaffByBranch(branchId);

                    staffId.getItems().clear();
                    staffId.getItems().addAll(operationsStaff);

                    // Set the current staff ID if it's in the list, otherwise clear it
                    if (operationsStaff.contains(violation.getStaffId())) {
                        staffId.setValue(violation.getStaffId());
                    } else {
                        staffId.setValue(null);
                    }

                    if (operationsStaff.isEmpty()) {
                        showSuccessPopup("No Staff Available",
                                "No Operations staff available in branch " + branchId +
                                        " for rental " + newVal + ". Please select a different rental.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showSuccessPopup("Error", "Error loading staff for selected rental: " + ex.getMessage());
                }
            }
        });

        // Initialize staff dropdown with current rental's branch staff
        try {
            String branchId = dao.getRentalBranchId(violation.getRentalId());
            List<String> operationsStaff = dao.getOperationsStaffByBranch(branchId);
            staffId.getItems().addAll(operationsStaff);
            staffId.setValue(violation.getStaffId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Buttons
        Button saveBtn = new Button("Save");
        Button cancelBtn = new Button("Cancel");
        saveBtn.getStyleClass().add("small-button");
        cancelBtn.getStyleClass().add("small-button");

        // Improved button styling with colors
        saveBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        Label message = new Label();
        message.setStyle("-fx-text-fill: white; -fx-font-size: 12px;");

        saveBtn.setOnAction(e -> {
            try {
                // Validate mandatory fields
                if (rentalId.getValue() == null || staffId.getValue() == null ||
                        violationType.getValue() == null || penaltyFee.getText().trim().isEmpty()) {
                    message.setText("Please fill in all mandatory fields!");
                    message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    return;
                }

                // Validate numeric fields
                double penalty = Double.parseDouble(penaltyFee.getText());
                int duration = Integer.parseInt(durationHours.getText());

                if (penalty < 0 || duration < 0) {
                    message.setText("Penalty and duration must be positive values!");
                    message.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    return;
                }

                // NEW: Validate staff assignment
                if (!dao.validateStaffForViolation(staffId.getValue(), rentalId.getValue())) {
                    message.setText("Invalid staff selection! Staff must be from Operations department and same branch as rental.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Update the violation object with new values
                violation.setRentalId(rentalId.getValue());
                violation.setStaffId(staffId.getValue());
                violation.setViolationType(violationType.getValue());
                violation.setPenaltyFee(penalty);
                violation.setReason(reason.getText());
                violation.setDurationHours(duration);
                violation.setTimestamp(LocalDateTime.now());

                dao.updateViolation(violation);
                refresh.run();
                popup.close();
                showSuccessPopup("Success", "Violation updated successfully!");
            } catch (NumberFormatException ex) {
                message.setText("Please enter valid numbers for penalty and duration!");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Failed to update violation. Please try again.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                idLabel, violationIdField,
                rentalLabel, rentalId,
                staffLabel, staffId,
                typeLabel, violationType,
                penaltyLabel, penaltyFee,
                durationLabel, durationHours,
                reasonLabel, reason,
                buttonBox,
                message
        );
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 400, 650);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /**
     * Displays a success/information popup message to the user.
     *
     * @param title The popup window title
     * @param messageText The message to display
     */
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
        scene.getRoot().requestFocus();
        popup.showAndWait();
    }

    /**
     * Displays a confirmation dialog for deleting a violation record.
     *
     * @param violationId The ID of the violation to be deleted
     * @return true if user confirms deletion, false otherwise
     */
    public boolean showDeleteConfirmation(String violationId) {
        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label message = new Label("Are you sure you want to delete violation " + violationId + "?");
        message.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center;");
        message.setWrapText(true);

        Button yesBtn = new Button("Yes, Delete");
        Button noBtn = new Button("Cancel");
        yesBtn.getStyleClass().add("small-button");
        noBtn.getStyleClass().add("small-button");

        // Improved button styling with colors
        yesBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        noBtn.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");

        yesBtn.setOnAction(e -> {
            confirmed[0] = true;
            popup.close();
        });
        noBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(15, yesBtn, noBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(20, message, buttonBox);
        box.setPadding(new Insets(25));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(40,40,50,0.98); -fx-background-radius: 15; -fx-border-color: linear-gradient(to right, #ff4444, #ff6b6b); -fx-border-radius: 15; -fx-border-width: 2;");

        Scene popupScene = new Scene(box, 360, 160);
        popupScene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        popup.setScene(popupScene);
        popupScene.getRoot().requestFocus();
        popup.showAndWait();

        return confirmed[0];
    }
}
