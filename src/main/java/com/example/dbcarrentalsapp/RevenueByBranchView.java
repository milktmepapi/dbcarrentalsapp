package com.example.dbcarrentalsapp;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.RevenueByBranchRecord;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

public class RevenueByBranchView {

    public TableView<RevenueByBranchRecord> tableView;
    public Button loadButton, returnButton, companyButton;

    public DatePicker dailyPicker;
    public ComboBox<String> monthPicker;
    public ComboBox<Integer> yearPicker;

    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final ToggleGroup granularityGroup = new ToggleGroup();
    public Button pieChartButton;

    private final Scene scene;
    private final NumberFormat moneyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));

    public RevenueByBranchView() {

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
        Text title = new Text("BRANCH REVENUE REPORT");

        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 48
        );
        title.setFont(f1Font != null ? f1Font : Font.font("Arial Black", 48));
        title.setStyle("""
                -fx-fill: white;
                -fx-font-style: italic;
                -fx-font-weight: bold;
                -fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);
                """);

        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(100, 0, 0, 0));
        root.getChildren().add(title);

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

        pieChartButton = new Button("Pie Chart");
        pieChartButton.setPrefWidth(120);
        pieChartButton.getStyleClass().add("small-button");

        HBox granularityBox = new HBox(15, dailyButton, monthlyButton, yearlyButton);
        granularityBox.setAlignment(Pos.CENTER);
        granularityBox.setPadding(new Insets(0, 10, 0, 10));


        // ============================================================
        // DATE PICKERS FOR EACH MODE
        // ============================================================

        // --- DAILY ---
        dailyPicker = new DatePicker();
        dailyPicker.setPrefWidth(200);

        dailyPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-opacity: 0.3;");
                }
            }
        });

        // --- MONTHLY ---
        monthPicker = new ComboBox<>();
        monthPicker.setPrefWidth(150);
        monthPicker.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        yearPicker = new ComboBox<>();
        yearPicker.setPrefWidth(120);
        for (int y = 2020; y <= 2035; y++) {
            yearPicker.getItems().add(y);
        }

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        // Limit year options to company-established year (2025) .. current year
        yearPicker.getItems().clear();
        for (int y = 2025; y <= currentYear; y++) {
            yearPicker.getItems().add(y);
        }

        // When year changes, re-filter months (prevent future months in current year)
        yearPicker.valueProperty().addListener((obs, oldY, newY) -> {
            monthPicker.getItems().clear();
            if (newY == null) return;

            for (int m = 1; m <= 12; m++) {
                if (newY == currentYear && m > currentMonth) break;
                monthPicker.getItems().add(monthNumberToName(m));
            }
        });

        // Container that will change depending on selected mode
        HBox pickerBox = new HBox(12);
        pickerBox.setAlignment(Pos.CENTER);

        // Default: DAILY
        pickerBox.getChildren().setAll(dailyPicker);

        // Listener: change UI based on selected granularity
        granularityGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            if (newV == dailyButton) {
                pickerBox.getChildren().setAll(dailyPicker);
            } else if (newV == monthlyButton) {
                pickerBox.getChildren().setAll(monthPicker, yearPicker);
            } else if (newV == yearlyButton) {
                pickerBox.getChildren().setAll(yearPicker);
            }
        });

        // Bundle granularity + picker UI
        VBox controlBox = new VBox(10, granularityBox, pickerBox);
        controlBox.setAlignment(Pos.CENTER);

        // ============================================================
        // TABLE AREA
        // ============================================================
        tableView = new TableView<>();
        tableView.setPrefWidth(900);
        tableView.setPrefHeight(300);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        tableView.getStyleClass().add("custom-table");

        // ----- Columns -----
        TableColumn<RevenueByBranchRecord, String> branchCol =
                new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<RevenueByBranchRecord, BigDecimal> rentalCol =
                new TableColumn<>("Rental Income");
        rentalCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getRentalIncome()));
        rentalCol.setCellFactory(col -> currencyCellFactory());

        TableColumn<RevenueByBranchRecord, BigDecimal> penaltyCol =
                new TableColumn<>("Penalty Income");
        penaltyCol.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPenaltyIncome()));
        penaltyCol.setCellFactory(col -> currencyCellFactory());

        TableColumn<RevenueByBranchRecord, BigDecimal> netCol =
                new TableColumn<>("Net Revenue");
        // compute net in the view as rental + penalty (we intentionally do NOT subtract salaries here)
        netCol.setCellValueFactory(cell -> {
            RevenueByBranchRecord r = cell.getValue();
            BigDecimal rental = r.getRentalIncome() != null ? r.getRentalIncome() : BigDecimal.ZERO;
            BigDecimal penalty = r.getPenaltyIncome() != null ? r.getPenaltyIncome() : BigDecimal.ZERO;
            return new SimpleObjectProperty<>(rental.add(penalty));
        });
        netCol.setCellFactory(col -> currencyCellFactory());

        // Align numeric columns right
        rentalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        penaltyCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        netCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        tableView.getColumns().addAll(branchCol, rentalCol, penaltyCol, netCol);

        // ============================================================
        // BUTTONS
        // ============================================================
        loadButton = new Button("Load");
        loadButton.setPrefWidth(120);
        loadButton.getStyleClass().add("small-button");

        companyButton = new Button("Total Revenue");
        companyButton.setPrefWidth(120);
        companyButton.getStyleClass().add("small-button");

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
        tableCard.setMaxWidth(950);
        tableCard.setStyle("""
                -fx-background-color: rgba(25,25,35,0.85);
                -fx-background-radius: 15;
                -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);
                -fx-border-radius: 15;
                -fx-border-width: 2;
                """);

        VBox layout = new VBox(30, controlBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 0, 0, 0));

        root.getChildren().add(layout);

        // ============================================================
        // SCENE
        // ============================================================
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        // Hook pie button (view helper — controller can keep or override if desired)
        pieChartButton.setOnAction(e -> {
            var items = tableView.getItems();
            if (items == null || items.isEmpty()) {
                showSimpleAlert("No data", "Load revenue data first to view the pie chart.");
                return;
            }
            showPieChartPopup(items);
        });
    }

    // Currency cell factory (reusable)
    private TableCell<RevenueByBranchRecord, BigDecimal> currencyCellFactory() {
        return new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                } else {
                    // show as ₱ with 2 decimals
                    setText(moneyFmt.format(value));
                }
            }
        };
    }

    private void showSimpleAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    private String monthNumberToName(int m) {
        return switch (m) {
            case 1 -> "January";
            case 2 -> "February";
            case 3 -> "March";
            case 4 -> "April";
            case 5 -> "May";
            case 6 -> "June";
            case 7 -> "July";
            case 8 -> "August";
            case 9 -> "September";
            case 10 -> "October";
            case 11 -> "November";
            case 12 -> "December";
            default -> "";
        };
    }

    // Pie chart uses net = rental + penalty. Negative nets are clamped to 0 for visual correctness.
    public void showPieChartPopup(java.util.List<RevenueByBranchRecord> list) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Branch Revenue Distribution");
        dialog.setHeaderText(null);

        dialog.getDialogPane().setStyle("""
        -fx-background-color: rgba(25,25,35,0.97);
        -fx-border-color: linear-gradient(to right, #7a40ff, #b46bff);
        -fx-border-width: 2;
        -fx-border-radius: 15;
        -fx-background-radius: 15;
    """);

        // Smaller padding + smaller gaps
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);

        Label title = new Label("NET REVENUE BY BRANCH");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 20px;
        -fx-font-weight: bold;
        -fx-padding: 0 0 15 0;
    """);

        PieChart pie = new PieChart();
        // Force labels to render closer to center so small slices never hide
        pie.setLabelLineLength(8); // shorter leader lines
        pie.setLabelsVisible(true);
        pie.setLegendVisible(false);
        pie.setClockwise(true);
        pie.setStartAngle(90);

        // single setStyle call that keeps background + label color + distance
        pie.setStyle("""
        -fx-background-color: transparent;
        -fx-pie-label-distance: 0.72;   /* 0.5 = closer to center, 1.0 = default */
        -fx-pie-label-fill: white;      /* label color */
        -fx-pie-label-font-size: 13px;
    """);

        // Add slices (store base label names so binding uses immutable base)
        // ensure very small slices are still visible by giving a tiny floor
        final double MIN_SLICE = 0.0001; // absolute floor, but we'll bump tiny ones up a bit later
        for (RevenueByBranchRecord r : list) {
            BigDecimal rental = r.getRentalIncome() != null ? r.getRentalIncome() : BigDecimal.ZERO;
            BigDecimal penalty = r.getPenaltyIncome() != null ? r.getPenaltyIncome() : BigDecimal.ZERO;
            BigDecimal net = rental.add(penalty);

            boolean noProfit = net.compareTo(BigDecimal.ZERO) <= 0;
            double value = Math.max(0.0, net.doubleValue());

            String baseLabel = r.getBranchName() + (noProfit ? " (No Profit)" : "");
            // If value is extremely small (or zero), give a tiny visible wedge so the chart draws a slice
            double sliceValue = (value < 0.01) ? 0.03 : Math.max(MIN_SLICE, value);

            PieChart.Data slice = new PieChart.Data(baseLabel, sliceValue);
            pie.getData().add(slice);
        }

        // ==== enlarge the actual pie chart by wrapping it, but keep dialog reasonable ====
        StackPane pieWrapper = new StackPane(pie);
        pieWrapper.setPrefSize(900, 750); // bigger graph area but dialog won't be enormous
        pieWrapper.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(pieWrapper, Priority.ALWAYS);
        VBox.setVgrow(pieWrapper, Priority.ALWAYS);
        pie.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        pie.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        pie.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        // ==============================================================================

        // Compute total (use the values that are in pie.getData() right now)
        double total = pie.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();
        final double totalFinal = total == 0 ? 1.0 : total; // avoid div-by-zero

        // Bind each slice's nameProperty to "baseLabel (xx.xx%)"
        // We used the base label as the initial name stored in Data; capture it first.
        for (PieChart.Data data : pie.getData()) {
            final String base = data.getName(); // original base label
            // Bind nameProperty to a string that updates if pie value changes
            data.nameProperty().bind(javafx.beans.binding.Bindings.createStringBinding(
                    () -> {
                        double pct = (totalFinal == 0) ? 0.0 : (data.getPieValue() / totalFinal) * 100.0;
                        return String.format("%s (%.2f%%)", base, pct);
                    },
                    data.pieValueProperty()
            ));
        }

        // Apply label CSS when labels are created (they are drawn asynchronously)
        Platform.runLater(() -> {
            pie.lookupAll(".chart-pie-label").forEach(node ->
                    node.setStyle("-fx-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;")
            );
        });

        // Right-side list
        Label labelTitle = new Label("Branches");
        labelTitle.setStyle("-fx-text-fill: #c7b3ff; -fx-font-size: 15px; -fx-font-weight: bold;");

        VBox labelBox = new VBox(8);
        labelBox.setPadding(new Insets(10));
        labelBox.setAlignment(Pos.TOP_LEFT);

        // Use the bound nameProperty values so the right list shows same text as pie labels
        for (PieChart.Data slice : pie.getData()) {
            Label lbl = new Label();
            lbl.textProperty().bind(slice.nameProperty()); // keep in sync
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
            labelBox.getChildren().add(lbl);
        }

        ScrollPane scroll = new ScrollPane(labelBox);
        scroll.setPrefWidth(400);
        scroll.setPrefHeight(300);
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

        // Divider
        Separator divider = new Separator();
        divider.setOrientation(Orientation.VERTICAL);
        divider.setPrefHeight(350);
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.25);");

        // Main layout
        HBox content = new HBox(30, pieWrapper, divider, rightSide);
        content.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, content);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    // Company popup shows rental + penalty as net (salary intentionally omitted)
    public void showCompanyPopup(RevenueByBranchRecord r) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Company Revenue");
        dialog.setHeaderText(null);

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

        Label title = new Label("WHOLE COMPANY — TOTAL REVENUE");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            """);

        BigDecimal rental = r.getRentalIncome() != null ? r.getRentalIncome() : BigDecimal.ZERO;
        BigDecimal penalty = r.getPenaltyIncome() != null ? r.getPenaltyIncome() : BigDecimal.ZERO;
        BigDecimal net = rental.add(penalty);

        Label rentalLbl = new Label("Rental Income: " + formatMoney(rental));
        Label penaltyLbl = new Label("Penalty Income: " + formatMoney(penalty));
        Label netLbl = new Label("Net Revenue: " + formatMoney(net));

        rentalLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        penaltyLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        netLbl.setStyle("-fx-text-fill: #b46bff; -fx-font-size: 18px; -fx-font-weight: bold;");

        box.getChildren().addAll(title, rentalLbl, penaltyLbl, netLbl);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "₱0.00";
        return moneyFmt.format(value);
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

    public TableView<RevenueByBranchRecord> getTableView() { return tableView; }

    // exposed pickers so controller can validate before calling DAO
    public DatePicker getDailyPicker() { return dailyPicker; }
    public ComboBox<String> getMonthPicker() { return monthPicker; }
    public ComboBox<Integer> getYearPicker() { return yearPicker; }
}