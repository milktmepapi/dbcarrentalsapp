package com.example.dbcarrentalsapp;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.ViolationsByBranchRecord;
import javafx.scene.chart.PieChart;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * View class for the Violations By Branch Report feature.
 * Handles all UI components, layout, and user interaction elements
 * for displaying branch violation statistics and charts.
 */
public class ViolationsByBranchView {

    // UI Component declarations
    public TableView<ViolationsByBranchRecord> tableView;
    public Button loadButton, returnButton, companyButton;
    public Button pieChartButton;

    // Granularity selection controls
    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final ToggleGroup granularityGroup = new ToggleGroup();

    // Date selection controls
    public DatePicker datePicker;
    public ComboBox<String> monthComboBox;
    public ComboBox<Integer> yearComboBox;
    private Label dateSelectionLabel;

    private final Scene scene;
    private ViolationsByBranchController controller;

    /**
     * Constructs the violations by branch view with all UI components.
     * Initializes the background, title, controls, table, and buttons.
     */
    public ViolationsByBranchView() {

        // ============================================================
        // BACKGROUND SETUP
        // ============================================================
        StackPane root = new StackPane();

        // Load and configure background image
        Image bgImage = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png")
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);

        root.getChildren().add(bgView);

        // ============================================================
        // TITLE SETUP
        // ============================================================
        Text title = new Text("VIOLATIONS BY BRANCH REPORT");

        // Load custom font with fallback to system font
        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 42
        );
        title.setFont(f1Font != null ? f1Font : Font.font("Arial Black", 42));
        title.setStyle("""
                -fx-fill: white;
                -fx-font-style: italic;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);
                """);

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(80, 0, 0, 0));
        root.getChildren().add(title);

        // ============================================================
        // DATE SELECTION CONTROLS
        // ============================================================
        dateSelectionLabel = new Label("Select Date:");
        dateSelectionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // Date Picker for daily granularity
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setPrefWidth(150);
        datePicker.setStyle("-fx-font-size: 14px;");

        // Month ComboBox for monthly granularity
        monthComboBox = new ComboBox<>();
        monthComboBox.getItems().addAll("January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");
        monthComboBox.setValue(LocalDate.now().getMonth().toString());
        monthComboBox.setPrefWidth(120);
        monthComboBox.setStyle("-fx-font-size: 14px;");
        monthComboBox.setDisable(true); // Initially disabled

        // Year ComboBox for monthly and yearly granularity
        yearComboBox = new ComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 5; year <= currentYear + 1; year++) {
            yearComboBox.getItems().add(year);
        }
        yearComboBox.setValue(currentYear);
        yearComboBox.setPrefWidth(100);
        yearComboBox.setStyle("-fx-font-size: 14px;");
        yearComboBox.setDisable(true); // Initially disabled

        // ============================================================
        // GRANULARITY TOGGLE BUTTONS
        // ============================================================
        dailyButton = new RadioButton("Daily");
        monthlyButton = new RadioButton("Monthly");
        yearlyButton = new RadioButton("Yearly");

        dailyButton.setToggleGroup(granularityGroup);
        monthlyButton.setToggleGroup(granularityGroup);
        yearlyButton.setToggleGroup(granularityGroup);
        dailyButton.setSelected(true); // Default selection

        // Add listeners to enable/disable date controls based on granularity selection
        granularityGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == dailyButton) {
                datePicker.setDisable(false);
                monthComboBox.setDisable(true);
                yearComboBox.setDisable(true);
            } else if (newToggle == monthlyButton) {
                datePicker.setDisable(true);
                monthComboBox.setDisable(false);
                yearComboBox.setDisable(false);
            } else if (newToggle == yearlyButton) {
                datePicker.setDisable(true);
                monthComboBox.setDisable(true);
                yearComboBox.setDisable(false);
            }
        });

        HBox granularityBox = new HBox(15, dailyButton, monthlyButton, yearlyButton);
        granularityBox.setAlignment(Pos.CENTER);

        HBox dateSelectionBox = new HBox(10, dateSelectionLabel, datePicker, monthComboBox, yearComboBox);
        dateSelectionBox.setAlignment(Pos.CENTER);
        dateSelectionBox.setPadding(new Insets(10, 0, 0, 0));

        VBox controlBox = new VBox(10, granularityBox, dateSelectionBox);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(0, 10, 0, 10));

        // ============================================================
        // TABLE AREA SETUP
        // ============================================================
        tableView = new TableView<>();
        tableView.setPrefWidth(1000);
        tableView.setPrefHeight(350);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");

        // ----- Table Column Definitions -----
        TableColumn<ViolationsByBranchRecord, String> branchCol =
                new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        branchCol.setPrefWidth(120);

        TableColumn<ViolationsByBranchRecord, Integer> totalCol =
                new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalViolations"));
        totalCol.setPrefWidth(70);

        TableColumn<ViolationsByBranchRecord, Integer> lateCol =
                new TableColumn<>("Late Returns");
        lateCol.setCellValueFactory(new PropertyValueFactory<>("lateReturnCount"));
        lateCol.setPrefWidth(90);

        TableColumn<ViolationsByBranchRecord, Integer> damageCol =
                new TableColumn<>("Car Damage");
        damageCol.setCellValueFactory(new PropertyValueFactory<>("carDamageCount"));
        damageCol.setPrefWidth(80);

        TableColumn<ViolationsByBranchRecord, Integer> trafficCol =
                new TableColumn<>("Traffic");
        trafficCol.setCellValueFactory(new PropertyValueFactory<>("trafficViolationCount"));
        trafficCol.setPrefWidth(70);

        TableColumn<ViolationsByBranchRecord, Integer> cleaningCol =
                new TableColumn<>("Cleaning");
        cleaningCol.setCellValueFactory(new PropertyValueFactory<>("cleaningFeeCount"));
        cleaningCol.setPrefWidth(70);

        TableColumn<ViolationsByBranchRecord, Integer> otherCol =
                new TableColumn<>("Other");
        otherCol.setCellValueFactory(new PropertyValueFactory<>("otherViolationCount"));
        otherCol.setPrefWidth(70);

        TableColumn<ViolationsByBranchRecord, BigDecimal> penaltyCol =
                new TableColumn<>("Total Penalty");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("totalPenaltyAmount"));
        penaltyCol.setPrefWidth(100);

        TableColumn<ViolationsByBranchRecord, BigDecimal> avgCol =
                new TableColumn<>("Avg Penalty");
        avgCol.setCellValueFactory(new PropertyValueFactory<>("averagePenalty"));
        avgCol.setPrefWidth(90);

        // Last Violation Date column
        TableColumn<ViolationsByBranchRecord, String> lastViolationCol =
                new TableColumn<>("Last Violation");
        lastViolationCol.setCellValueFactory(new PropertyValueFactory<>("formattedLastViolationDate"));
        lastViolationCol.setPrefWidth(150);

        // Align numeric columns to the right for better readability
        totalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        lateCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        damageCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        trafficCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        cleaningCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        otherCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        penaltyCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        avgCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        lastViolationCol.setStyle("-fx-alignment: CENTER;");

        // Add all columns to the table
        tableView.getColumns().addAll(branchCol, totalCol, lateCol, damageCol,
                trafficCol, cleaningCol, otherCol, penaltyCol, avgCol, lastViolationCol);

        // ============================================================
        // BUTTONS SETUP
        // ============================================================
        loadButton = new Button("Refresh Data");
        loadButton.setPrefWidth(140);
        loadButton.getStyleClass().add("small-button");

        companyButton = new Button("Company Summary");
        companyButton.setPrefWidth(140);
        companyButton.getStyleClass().add("small-button");

        pieChartButton = new Button("Pie Chart");
        pieChartButton.setPrefWidth(120);
        pieChartButton.getStyleClass().add("small-button");

        returnButton = new Button("Return");
        returnButton.setPrefWidth(120);
        returnButton.getStyleClass().add("small-button");

        HBox bottomButtons = new HBox(20, loadButton, companyButton, pieChartButton, returnButton);
        bottomButtons.setAlignment(Pos.CENTER);
        bottomButtons.setPadding(new Insets(10, 0, 0, 0));

        // ============================================================
        // TABLE CARD CONTAINER
        // ============================================================
        VBox tableCard = new VBox(15, tableView, bottomButtons);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setMaxWidth(1050);
        tableCard.setStyle("""
                -fx-background-color: rgba(25,25,35,0.85);
                -fx-background-radius: 15;
                -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);
                -fx-border-radius: 15;
                -fx-border-width: 2;
                """);

        // Main layout assembly
        VBox layout = new VBox(30, controlBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(120, 0, 0, 0));

        root.getChildren().add(layout);

        // ============================================================
        // SCENE FINALIZATION
        // ============================================================
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    /**
     * Gets the currently selected date from the date picker.
     * @return the selected LocalDate, or null if no date selected
     */
    public LocalDate getSelectedDate() {
        return datePicker.getValue();
    }

    /**
     * Converts the selected month name to its numeric representation (1-12).
     * @return the month as integer (1=January, 12=December)
     */
    public int getSelectedMonth() {
        String month = monthComboBox.getValue();
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(month)) {
                return i + 1;
            }
        }
        return LocalDate.now().getMonthValue(); // Fallback to current month
    }

    /**
     * Gets the currently selected year from the year combo box.
     * @return the selected year as integer
     */
    public int getSelectedYear() {
        return yearComboBox.getValue();
    }

    /**
     * Sets the controller and initializes date change listeners.
     * @param controller the ViolationsByBranchController to handle user interactions
     */
    public void setController(ViolationsByBranchController controller) {
        this.controller = controller;
        setupDateChangeListeners();
    }

    /**
     * Sets up listeners for date and granularity changes to trigger automatic data reload.
     */
    private void setupDateChangeListeners() {
        // Listen to date picker changes
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dailyButton.isSelected()) {
                controller.handleDateChange();
            }
        });

        // Listen to month combobox changes
        monthComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && monthlyButton.isSelected()) {
                controller.handleDateChange();
            }
        });

        // Listen to year combobox changes
        yearComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && (monthlyButton.isSelected() || yearlyButton.isSelected())) {
                controller.handleDateChange();
            }
        });

        // Listen to granularity changes
        granularityGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Small delay to ensure UI has updated before loading data
                new Thread(() -> {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    Platform.runLater(() -> controller.handleDateChange());
                }).start();
            }
        });
    }

    /**
     * Displays a popup dialog with company-wide violation summary.
     * @param v the ViolationsByBranchRecord containing company summary data
     */
    public void showCompanyPopup(ViolationsByBranchRecord v) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Company Violations Summary");
        dialog.setHeaderText(null);

        // Style the dialog with purple gradient border to match application theme
        dialog.getDialogPane().setStyle("""
            -fx-background-color: rgba(20,20,30,0.95);
            -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);
            -fx-border-width: 2;
            -fx-border-radius: 10;
            -fx-background-radius: 10;
            """);

        VBox box = new VBox(12);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER_LEFT);

        // Title label
        Label title = new Label("COMPANY VIOLATIONS SUMMARY");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            """);

        // Data labels
        Label total = new Label("Total Violations: " + v.getTotalViolations());
        Label late = new Label("Late Returns: " + v.getLateReturnCount());
        Label damage = new Label("Car Damage: " + v.getCarDamageCount());
        Label traffic = new Label("Traffic Violations: " + v.getTrafficViolationCount());
        Label cleaning = new Label("Cleaning Fees: " + v.getCleaningFeeCount());
        Label other = new Label("Other Violations: " + v.getOtherViolationCount());
        Label penalty = new Label("Total Penalties: ₱" + formatMoney(v.getTotalPenaltyAmount()));
        Label avg = new Label("Average Penalty: ₱" + formatMoney(v.getAveragePenalty()));

        // Apply consistent styling
        String labelStyle = "-fx-text-fill: white; -fx-font-size: 14px;";
        total.setStyle(labelStyle);
        late.setStyle(labelStyle);
        damage.setStyle(labelStyle);
        traffic.setStyle(labelStyle);
        cleaning.setStyle(labelStyle);
        other.setStyle(labelStyle);
        penalty.setStyle("-fx-text-fill: #b46bff; -fx-font-size: 16px; -fx-font-weight: bold;");
        avg.setStyle("-fx-text-fill: #b46bff; -fx-font-size: 16px; -fx-font-weight: bold;");

        box.getChildren().addAll(title, total, late, damage, traffic, cleaning, other, penalty, avg);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    /**
     * Displays a popup dialog with a pie chart showing violations distribution by branch.
     * @param list the list of ViolationsByBranchRecord objects to visualize
     */
    public void showPieChartPopup(java.util.List<ViolationsByBranchRecord> list) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Branch Violations Distribution");
        dialog.setHeaderText(null);

        // Style the dialog to match application theme
        dialog.getDialogPane().setStyle("""
        -fx-background-color: rgba(25,25,35,0.97);
        -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);
        -fx-border-width: 2;
        -fx-border-radius: 15;
        -fx-background-radius: 15;
    """);

        VBox box = new VBox(25);
        box.setPadding(new Insets(35));
        box.setAlignment(Pos.CENTER);

        // Chart title
        Label title = new Label("TOTAL VIOLATIONS BY BRANCH");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 22px;
        -fx-font-weight: bold;
        -fx-padding: 0 0 20 0;
    """);

        // Create and configure pie chart
        PieChart pie = new PieChart();
        pie.setLabelsVisible(true);
        pie.setLegendVisible(false);
        pie.setClockwise(true);
        pie.setStartAngle(90);

        // Add data slices for each branch
        for (ViolationsByBranchRecord v : list) {
            PieChart.Data slice = new PieChart.Data(
                    v.getBranchName(),
                    Math.max(0.1, v.getTotalViolations()) // Ensure minimum value for visibility
            );
            pie.getData().add(slice);
        }

        // Set chart size
        pie.setPrefSize(650, 520);

        // Ensure labels are styled correctly after chart rendering
        Platform.runLater(() -> {
            for (PieChart.Data d : pie.getData()) {
                Node label = d.getNode().lookup(".chart-pie-label");
                if (label != null) {
                    label.setStyle("""
                    -fx-fill: white;
                    -fx-text-fill: white;
                    -fx-font-size: 12px;
                    -fx-font-weight: bold;
                """);
                }
            }

            // Style any additional text nodes
            for (Node node : pie.lookupAll(".text")) {
                node.setStyle("-fx-fill: white;");
            }
        });

        // Create legend area on the right side
        Label labelTitle = new Label("Branches");
        labelTitle.setStyle("-fx-text-fill: #c7b3ff; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox labelBox = new VBox(10);
        labelBox.setPadding(new Insets(12));
        labelBox.setAlignment(Pos.TOP_LEFT);

        // Add branch labels with violation counts
        for (PieChart.Data slice : pie.getData()) {
            Label lbl = new Label(slice.getName() + " (" + (int)slice.getPieValue() + " violations)");
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            labelBox.getChildren().add(lbl);
        }

        // Scrollable legend container
        ScrollPane scroll = new ScrollPane(labelBox);
        scroll.setPrefWidth(280);
        scroll.setPrefHeight(480);
        scroll.setFitToWidth(true);
        scroll.setStyle("""
        -fx-background: transparent;
        -fx-background-color: transparent;
        -fx-border-color: rgba(255,255,255,0.15);
        -fx-border-width: 1.2;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
    """);

        VBox rightSide = new VBox(10, labelTitle, scroll);
        rightSide.setAlignment(Pos.TOP_CENTER);

        // Vertical divider between chart and legend
        Separator divider = new Separator();
        divider.setOrientation(Orientation.VERTICAL);
        divider.setPrefHeight(480);
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.25);");

        // Main content layout
        HBox content = new HBox(40, pie, divider, rightSide);
        content.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, content);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    /**
     * Formats a BigDecimal value as a currency string.
     * @param value the monetary value to format
     * @return formatted currency string, or "0.00" if value is null
     */
    private String formatMoney(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value);
    }

    // -----------------------------------------------------------------
    // ACCESSOR METHODS FOR CONTROLLER
    // -----------------------------------------------------------------

    /**
     * @return the main scene for this view
     */
    public Scene getScene() { return scene; }

    /**
     * @return the currently selected granularity radio button
     */
    public RadioButton getSelectedGranularityToggle() {
        return (RadioButton) granularityGroup.getSelectedToggle();
    }

    /**
     * @return the load/refresh button
     */
    public Button getLoadButton() { return loadButton; }

    /**
     * @return the return/navigation button
     */
    public Button getReturnButton() { return returnButton; }

    /**
     * @return the company summary button
     */
    public Button getCompanyButton() { return companyButton; }

    /**
     * @return the pie chart button
     */
    public Button getPieChartButton() { return pieChartButton; }

    /**
     * @return the main table view
     */
    public TableView<ViolationsByBranchRecord> getTableView() { return tableView; }

    // Date control accessors
    /**
     * @return the date picker control
     */
    public DatePicker getDatePicker() { return datePicker; }

    /**
     * @return the month selection combo box
     */
    public ComboBox<String> getMonthComboBox() { return monthComboBox; }

    /**
     * @return the year selection combo box
     */
    public ComboBox<Integer> getYearComboBox() { return yearComboBox; }
}