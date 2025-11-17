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
import model.RevenueByBranchRecord;
import javafx.scene.chart.PieChart;


import java.math.BigDecimal;

public class RevenueByBranchView {

    public TableView<RevenueByBranchRecord> tableView;
    public Button loadButton, returnButton, companyButton;

    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final ToggleGroup granularityGroup = new ToggleGroup();
    public Button pieChartButton;

    private final Scene scene;

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

        VBox controlBox = new VBox(10, granularityBox);
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
        rentalCol.setCellValueFactory(new PropertyValueFactory<>("rentalIncome"));

        TableColumn<RevenueByBranchRecord, BigDecimal> penaltyCol =
                new TableColumn<>("Penalty Income");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("penaltyIncome"));

        TableColumn<RevenueByBranchRecord, BigDecimal> salaryCol =
                new TableColumn<>("Salary Expenses");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salaryExpenses"));

        TableColumn<RevenueByBranchRecord, BigDecimal> netCol =
                new TableColumn<>("Net Revenue");
        netCol.setCellValueFactory(new PropertyValueFactory<>("netRevenue"));

        // Align numeric columns right
        rentalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        penaltyCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        salaryCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        netCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        tableView.getColumns().addAll(branchCol, rentalCol, penaltyCol, salaryCol, netCol);

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
    }

    public void showCompanyPopup(RevenueByBranchRecord r) {

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Company Revenue");
        dialog.setHeaderText(null);

        // Gradient border + dark background
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

        Label rental = new Label("Rental Income: ₱" + formatMoney(r.getRentalIncome()));
        Label penalty = new Label("Penalty Income: ₱" + formatMoney(r.getPenaltyIncome()));
        Label salary = new Label("Salary Expenses: ₱" + formatMoney(r.getSalaryExpenses()));
        Label net = new Label("Net Revenue: ₱" + formatMoney(r.getNetRevenue()));

        rental.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        penalty.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        salary.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        net.setStyle("-fx-text-fill: #b46bff; -fx-font-size: 18px; -fx-font-weight: bold;");

        box.getChildren().addAll(title, rental, penalty, salary, net);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0.00";
        return String.format("%,.2f", value);
    }

    public void showPieChartPopup(java.util.List<RevenueByBranchRecord> list)  {

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

        VBox box = new VBox(25);
        box.setPadding(new Insets(35));
        box.setAlignment(Pos.CENTER);

        Label title = new Label("NET REVENUE BY BRANCH");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 22px;
        -fx-font-weight: bold;
        -fx-padding: 0 0 20 0;
    """);

        // --- MUCH BIGGER PIE CHART ---
        PieChart pie = new PieChart();
        pie.setLabelsVisible(true);       // ensures labels appear
        pie.setLegendVisible(false);
        pie.setClockwise(true);
        pie.setStartAngle(90);
        pie.setStyle("-fx-background-color: transparent;");

        for (RevenueByBranchRecord r : list) {
            PieChart.Data slice = new PieChart.Data(
                    r.getBranchName(),
                    Math.max(0.1, r.getNetRevenue().doubleValue())
            );
            pie.getData().add(slice);
        }

        // Bigger size
        pie.setPrefSize(650, 520);

        // Make labels white (JavaFX default is black)
        // Make labels white *after* chart renders
        Platform.runLater(() -> {
            for (PieChart.Data d : pie.getData()) {
                Node label = d.getNode().lookup(".chart-pie-label");
                if (label != null) {
                    label.setStyle("-fx-text-fill: white;");
                }
            }
        });

        // --- RIGHT SIDE LABEL LIST ---
        Label labelTitle = new Label("Branches");
        labelTitle.setStyle("-fx-text-fill: #c7b3ff; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox labelBox = new VBox(10);
        labelBox.setPadding(new Insets(12));
        labelBox.setAlignment(Pos.TOP_LEFT);

        for (PieChart.Data slice : pie.getData()) {
            Label lbl = new Label(slice.getName());
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            labelBox.getChildren().add(lbl);
        }

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

        // Divider
        Separator divider = new Separator();
        divider.setOrientation(Orientation.VERTICAL);
        divider.setPrefHeight(480);
        divider.setStyle("-fx-background-color: rgba(255,255,255,0.25);");

        HBox content = new HBox(40, pie, divider, rightSide);
        content.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, content);

        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
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
}