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

public class ViolationsByBranchView {

    public TableView<ViolationsByBranchRecord> tableView;
    public Button loadButton, returnButton, companyButton;
    public Button pieChartButton;

    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final ToggleGroup granularityGroup = new ToggleGroup();

    // NEW: Date selection controls
    public DatePicker datePicker;
    public ComboBox<String> monthComboBox;
    public ComboBox<Integer> yearComboBox;
    private Label dateSelectionLabel;

    private final Scene scene;
    private ViolationsByBranchController controller;

    public ViolationsByBranchView() {

        // ============================================================
        // BACKGROUND
        // ============================================================
        StackPane root = new StackPane();

        Image bgImage = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png")
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);

        root.getChildren().add(bgView);

        // ============================================================
        // TITLE
        // ============================================================
        Text title = new Text("VIOLATIONS BY BRANCH REPORT");

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
        // DATE SELECTION CONTROLS - NEW
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
        // GRANULARITY TOGGLES
        // ============================================================
        dailyButton = new RadioButton("Daily");
        monthlyButton = new RadioButton("Monthly");
        yearlyButton = new RadioButton("Yearly");

        dailyButton.setToggleGroup(granularityGroup);
        monthlyButton.setToggleGroup(granularityGroup);
        yearlyButton.setToggleGroup(granularityGroup);
        dailyButton.setSelected(true);

        // Add listeners to enable/disable date controls based on granularity
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
        // TABLE AREA
        // ============================================================
        tableView = new TableView<>();
        tableView.setPrefWidth(1000);
        tableView.setPrefHeight(350);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");

        // ----- Columns -----
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

        // NEW: Last Violation Date column
        TableColumn<ViolationsByBranchRecord, String> lastViolationCol =
                new TableColumn<>("Last Violation");
        lastViolationCol.setCellValueFactory(new PropertyValueFactory<>("formattedLastViolationDate"));
        lastViolationCol.setPrefWidth(150);

        // Align numeric columns right
        totalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        lateCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        damageCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        trafficCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        cleaningCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        otherCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        penaltyCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        avgCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        lastViolationCol.setStyle("-fx-alignment: CENTER;");

        tableView.getColumns().addAll(branchCol, totalCol, lateCol, damageCol,
                trafficCol, cleaningCol, otherCol, penaltyCol, avgCol, lastViolationCol); // Added lastViolationCol

        // ============================================================
        // BUTTONS
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
        // TABLE CARD
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

        VBox layout = new VBox(30, controlBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(120, 0, 0, 0));

        root.getChildren().add(layout);

        // ============================================================
        // SCENE
        // ============================================================
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    // ============================================================
    // NEW: Date selection methods
    // ============================================================
    public LocalDate getSelectedDate() {
        return datePicker.getValue();
    }

    public int getSelectedMonth() {
        String month = monthComboBox.getValue();
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(month)) {
                return i + 1;
            }
        }
        return LocalDate.now().getMonthValue();
    }

    public int getSelectedYear() {
        return yearComboBox.getValue();
    }

    // ============================================================
    // NEW: Setup automatic date change listeners
    // ============================================================
    public void setController(ViolationsByBranchController controller) {
        this.controller = controller;
        setupDateChangeListeners();
    }

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

    // Rest of the methods remain the same...
    public void showCompanyPopup(ViolationsByBranchRecord v) {
        // ... existing implementation ...
    }

    public void showPieChartPopup(java.util.List<ViolationsByBranchRecord> list) {
        // ... existing implementation ...
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value);
    }

    // -----------------------------------------------------------------
    // ACCESSORS FOR CONTROLLER
    // -----------------------------------------------------------------
    public Scene getScene() { return scene; }

    public RadioButton getSelectedGranularityToggle() {
        return (RadioButton) granularityGroup.getSelectedToggle();
    }

    public Button getLoadButton() { return loadButton; }
    public Button getReturnButton() { return returnButton; }
    public Button getCompanyButton() { return companyButton; }
    public Button getPieChartButton() { return pieChartButton; }

    public TableView<ViolationsByBranchRecord> getTableView() { return tableView; }

    // NEW: Date control accessors
    public DatePicker getDatePicker() { return datePicker; }
    public ComboBox<String> getMonthComboBox() { return monthComboBox; }
    public ComboBox<Integer> getYearComboBox() { return yearComboBox; }
}