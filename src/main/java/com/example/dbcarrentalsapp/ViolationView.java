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
    // UI Components - made public for controller access
    public Button addButton, modifyButton, returnButton, filterButton;
    public TextField searchField;
    public TableView<ViolationRecord> tableView;
    private final Scene scene;

    /**
     * Constructor - initializes the violation management interface with all UI components.
     * Sets up the layout, styling, and initial state of the view.
     */
    public ViolationView() {
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
        TableColumn<ViolationRecord, String> idCol = new TableColumn<>("Violation ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("violationId"));
        idCol.setPrefWidth(90);  // Reduced width
        idCol.setMaxWidth(120);
        // Set text color to black
        idCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
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

        TableColumn<ViolationRecord, String> rentalCol = new TableColumn<>("Rental ID");
        rentalCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        rentalCol.setPrefWidth(80);
        rentalCol.setMaxWidth(100);
        // Set text color to black
        rentalCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
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

        TableColumn<ViolationRecord, String> staffCol = new TableColumn<>("Staff ID");
        staffCol.setCellValueFactory(new PropertyValueFactory<>("staffId"));
        staffCol.setPrefWidth(70);
        staffCol.setMaxWidth(90);
        // Set text color to black
        staffCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
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

        TableColumn<ViolationRecord, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("violationType"));
        typeCol.setPrefWidth(100);
        typeCol.setMaxWidth(130);
        // Set text color to black
        typeCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
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

        TableColumn<ViolationRecord, Double> penaltyCol = new TableColumn<>("Penalty");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("penaltyFee"));
        penaltyCol.setPrefWidth(70);
        penaltyCol.setMaxWidth(90);
        // Format penalty fee display and set text color to black
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
        reasonCol.setPrefWidth(150);  // More space for reason
        reasonCol.setMaxWidth(200);
        // Enable text wrapping for reason column and set text color to black
        reasonCol.setCellFactory(column -> new TableCell<ViolationRecord, String>() {
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

        TableColumn<ViolationRecord, Integer> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("durationHours"));
        durationCol.setPrefWidth(70);
        durationCol.setMaxWidth(90);
        // Format duration display and set text color to black
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
                    timestamp.format(DateTimeFormatter.ofPattern("MM/dd HH:mm")) : "N/A";
            return new javafx.beans.property.SimpleStringProperty(formatted);
        });
        timestampCol.setPrefWidth(100);
        timestampCol.setMaxWidth(120);
        // Set text color to black
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

        // Add all columns to the table
        tableView.getColumns().addAll(
                idCol, rentalCol, staffCol, typeCol, penaltyCol, reasonCol, durationCol, timestampCol
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
            staffId.getItems().addAll(dao.getAllStaffIds());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set violation type options
        violationType.getItems().addAll("Late Return", "Car Damage", "Traffic Violation", "Cleaning Fee", "Other");

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
            staffId.getItems().addAll(dao.getAllStaffIds());
        } catch (Exception e) {
            e.printStackTrace();
        }

        violationType.getItems().addAll("Late Return", "Car Damage", "Traffic Violation", "Cleaning Fee", "Other");

        // Set current values in fields
        rentalId.setValue(violation.getRentalId());
        staffId.setValue(violation.getStaffId());
        violationType.setValue(violation.getViolationType());

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