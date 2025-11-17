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
import model.RentalRecord;
import model.CancellationRecord;
import model.ReturnRecord;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CancellationView {
    // UI Components - made public for controller access
    public Button addButton, modifyButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<CancellationRecord> tableView;
    private final Scene scene;
    private CancellationController controller;
    public CancellationView() {
// ===== BACKGROUND SETUP =====
        StackPane root = new StackPane();
        // Load and set background image
        Image bgImage = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png"));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // ===== TITLE SETUP =====
        Text title = new Text("MANAGE CANCELLATIONS");
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
        searchField.setPromptText("Search cancellation ID, rental ID, or reason...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: #2a2a3a; -fx-text-fill: white; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        filterButton = new Button("Filter");
        filterButton.getStyleClass().add("small-button");
        filterButton.setPrefWidth(100);

        // Horizontal box for search components
        HBox searchBox = new HBox(10, searchField, filterButton);
        searchBox.setAlignment(Pos.CENTER);

        // ===== TABLE VIEW SETUP =====
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setPrefHeight(280);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tableView.getStyleClass().add("custom-table");

        // Define table columns with optimized widths
        TableColumn<CancellationRecord, String> idCol = new TableColumn<>("Cancellation ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("cancellationId"));
        idCol.setPrefWidth(90);  // Reduced width
        idCol.setMaxWidth(120);
        // Set text color to black
        idCol.setCellFactory(column -> new TableCell<CancellationRecord, String>() {
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

        TableColumn<CancellationRecord, String> rentalCol = new TableColumn<>("Rental ID");
        rentalCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalCol.setPrefWidth(80);
        rentalCol.setMaxWidth(100);
        // Set text color to black
        rentalCol.setCellFactory(column -> new TableCell<CancellationRecord, String>() {
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

        TableColumn<CancellationRecord, String> staffCol = new TableColumn<>("Staff ID");
        staffCol.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        staffCol.setPrefWidth(70);
        staffCol.setMaxWidth(90);
        // Set text color to black
        staffCol.setCellFactory(column -> new TableCell<CancellationRecord, String>() {
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

        TableColumn<CancellationRecord, String> timestampCol = new TableColumn<>("Date and Time");
        timestampCol.setCellValueFactory(cellData -> {
            LocalDateTime timestamp = cellData.getValue().getTimestamp();
            String formatted = timestamp != null ?
                    timestamp.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        timestampCol.setPrefWidth(100);
        timestampCol.setMaxWidth(120);
        // Set text color to black
        timestampCol.setCellFactory(column -> new TableCell<CancellationRecord, String>() {
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

        TableColumn<CancellationRecord, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setPrefWidth(150);  // More space for reason
        reasonCol.setMaxWidth(200);
        // Enable text wrapping for reason column and set text color to black
        reasonCol.setCellFactory(column -> new TableCell<CancellationRecord, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a text object with wrapping
                    Text text = new Text(item);
                    text.setWrappingWidth(reasonCol.getWidth() - 10); // Allow for padding
                    text.setStyle("-fx-fill: black;");
                    setGraphic(text);
                }
            }
        });

        TableColumn<CancellationRecord, Void> receiptCol = new TableColumn<>("Receipt");
        receiptCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View Receipt");
            {
                btn.setOnAction(e -> {
                    CancellationRecord record = getTableView().getItems().get(getIndex());
                    if (controller != null) controller.viewReceipt(record);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        // Add all columns to the table
        tableView.getColumns().addAll(
                idCol, rentalCol, staffCol, timestampCol, reasonCol, receiptCol
        );

        // ===== BUTTON SETUP =====
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        returnButton = new Button("Return");

        addButton.getStyleClass().add("small-button");
        modifyButton.getStyleClass().add("small-button");
        returnButton.getStyleClass().add("small-button");

        addButton.setPrefWidth(120);
        modifyButton.setPrefWidth(120);
        returnButton.setPrefWidth(120);

        // Horizontal box for action buttons
        HBox buttonBox = new HBox(15, addButton, modifyButton, returnButton);
        buttonBox.setAlignment(Pos.CENTER);

        // ===== CARD CONTAINER SETUP =====
        // Main content card with semi-transparent background
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
     * Returns the scene for this view to be displayed in the stage.
     *
     * @return The fully configured Scene object
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Sorts the table by cancellation ID in ascending order
     */
    public void sortByCancellationId() {
        tableView.getSortOrder().clear();

        // Find the cancellation ID column
        for (TableColumn<CancellationRecord, ?> column : tableView.getColumns()) {
            if ("Cancellation ID".equals(column.getText())) {
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
     * Displays a popup dialog for adding a new cancellation record.
     * Includes input validation and dynamic field enabling.
     *
     * @param dao Data Access Object for cancellation operations
     * @param refresh Callback function to refresh the table after addition
     */
    public void showAddCancellationPopup(CancellationDAO dao, Runnable refresh) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Add New Cancellation");

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

        Label reasonLabel = new Label("Reason:");
        reasonLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea reason = new TextArea();
        reason.setPromptText("Enter cancellation details...");
        reason.setPrefHeight(80);
        // Set inner background to white and text to black
        reason.setStyle("-fx-control-inner-background: white; -fx-text-fill: black; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate dropdowns with data from database
        try {
            rentalId.getItems().addAll(dao.getAllRentalIds());
            // Don't populate staffId here - it will be populated dynamically based on rental selection
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                // NEW: Validate staff assignment
                if (!dao.validateStaffForCancellation(staffId.getValue(), rentalId.getValue())) {
                    message.setText("Invalid staff selection! Staff must be from Operations department and same branch as rental.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Generate new ID and create cancellation record
                String generatedId = dao.generateNextCancellationId();
                CancellationRecord cancellation = new CancellationRecord(
                        generatedId,
                        rentalId.getValue(),
                        staffId.getValue(),
                        LocalDateTime.now(),
                        reason.getText()
                );

                dao.addCancellation(cancellation);
                refresh.run();
                popup.close();
                showSuccessPopup("Success", "Cancellation added successfully!");
            } catch (NumberFormatException ex) {
                message.setText("Please enter valid numbers for rental and staff ID");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Failed to add cancellation. Please try again.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, addBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                rentalLabel, rentalId,
                staffLabel, staffId,
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
     * Displays a popup dialog for modifying an existing cancellation record.
     * All fields except cancellation ID are editable.
     *
     * @param dao Data Access Object for cancellation operations
     * @param cancellation The cancellation record to modify
     * @param refresh Callback function to refresh the table after modification
     */
    public void showModifyCancellationPopup(CancellationDAO dao, CancellationRecord cancellation, Runnable refresh) {
        if (cancellation == null) {
            showSuccessPopup("No Selection", "Please select a cancellation to modify.");
            return;
        }

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Modify Cancellation");

        // Read-only field for cancellation ID
        Label idLabel = new Label("Cancellation ID:");
        idLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextField cancellationIdField = new TextField(cancellation.getCancellationId());
        cancellationIdField.setEditable(false);
        cancellationIdField.setStyle("-fx-background-color: #3a3a4a; -fx-text-fill: #cccccc; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

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

        Label reasonLabel = new Label("Reason:");
        reasonLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        TextArea reason = new TextArea(cancellation.getReason());
        reason.setPrefHeight(80);
        // Set inner background to white and text to black
        reason.setStyle("-fx-control-inner-background: white; -fx-text-fill: black; -fx-border-color: #7a40ff; -fx-border-radius: 5;");

        // Populate dropdowns with current data
        try {
            rentalId.getItems().addAll(dao.getAllRentalIds());
            // Don't populate staffId here - it will be populated dynamically based on rental selection
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                    if (operationsStaff.contains(cancellation.getCancellationStaffId())) {
                        staffId.setValue(cancellation.getCancellationStaffId());
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
            String branchId = dao.getRentalBranchId(cancellation.getCancellationRentalId());
            List<String> operationsStaff = dao.getOperationsStaffByBranch(branchId);
            staffId.getItems().addAll(operationsStaff);
            staffId.setValue(cancellation.getCancellationStaffId());
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
                // NEW: Validate staff assignment
                if (!dao.validateStaffForCancellation(staffId.getValue(), rentalId.getValue())) {
                    message.setText("Invalid staff selection! Staff must be from Operations department and same branch as rental.");
                    message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    return;
                }

                // Update the cancellation object with new values
                cancellation.setCancellationRentalId(rentalId.getValue());
                cancellation.setCancellationStaffId(staffId.getValue());
                cancellation.setTimestamp(LocalDateTime.now());
                cancellation.setReason(reason.getText());

                dao.updateCancellation(cancellation);
                refresh.run();
                popup.close();
                showSuccessPopup("Success", "Cancellation updated successfully!");
            } catch (NumberFormatException ex) {
                message.setText("Please enter valid numbers for penalty and duration!");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            } catch (Exception ex) {
                ex.printStackTrace();
                message.setText("Failed to update cancellation. Please try again.");
                message.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        cancelBtn.setOnAction(e -> popup.close());

        HBox buttonBox = new HBox(10, saveBtn, cancelBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox box = new VBox(15,
                idLabel, cancellationIdField,
                rentalLabel, rentalId,
                staffLabel, staffId,
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
     * Displays a confirmation dialog for deleting a cancellation record.
     *
     * @param cancellationId The ID of the cancellation to be deleted
     * @return true if user confirms deletion, false otherwise
     */
    public boolean showDeleteConfirmation(String cancellationId) {
        final boolean[] confirmed = {false};

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Confirm Deletion");

        Label message = new Label("Are you sure you want to delete cancellation " + cancellationId + "?");
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
    public void refreshTable(List<CancellationRecord> data) {
        tableView.getItems().setAll(data);
    }

    public CancellationRecord getSelectedRecord() { return tableView.getSelectionModel().getSelectedItem(); }
}
