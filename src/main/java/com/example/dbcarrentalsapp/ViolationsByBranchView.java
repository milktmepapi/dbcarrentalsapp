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
import java.time.format.DateTimeFormatter;

public class ViolationsByBranchView {

    public TableView<ViolationsByBranchRecord> tableView;
    public Button loadButton, returnButton, companyButton;
    public Button pieChartButton; // NEW: Pie chart button

    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final ToggleGroup granularityGroup = new ToggleGroup();

    private final Scene scene;

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
        // GRANULARITY TOGGLES
        // ============================================================
        dailyButton = new RadioButton("Daily");
        monthlyButton = new RadioButton("Monthly");
        yearlyButton = new RadioButton("Yearly");

        dailyButton.setToggleGroup(granularityGroup);
        monthlyButton.setToggleGroup(granularityGroup);
        yearlyButton.setToggleGroup(granularityGroup);
        dailyButton.setSelected(true);

        HBox granularityBox = new HBox(15, dailyButton, monthlyButton, yearlyButton);
        granularityBox.setAlignment(Pos.CENTER);
        granularityBox.setPadding(new Insets(0, 10, 0, 10));

        VBox controlBox = new VBox(10, granularityBox);
        controlBox.setAlignment(Pos.CENTER);

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
        branchCol.setPrefWidth(150);

        TableColumn<ViolationsByBranchRecord, Integer> totalCol =
                new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalViolations"));
        totalCol.setPrefWidth(80);

        TableColumn<ViolationsByBranchRecord, Integer> lateCol =
                new TableColumn<>("Late Returns");
        lateCol.setCellValueFactory(new PropertyValueFactory<>("lateReturnCount"));
        lateCol.setPrefWidth(100);

        TableColumn<ViolationsByBranchRecord, Integer> damageCol =
                new TableColumn<>("Car Damage");
        damageCol.setCellValueFactory(new PropertyValueFactory<>("carDamageCount"));
        damageCol.setPrefWidth(90);

        TableColumn<ViolationsByBranchRecord, Integer> trafficCol =
                new TableColumn<>("Traffic");
        trafficCol.setCellValueFactory(new PropertyValueFactory<>("trafficViolationCount"));
        trafficCol.setPrefWidth(80);

        TableColumn<ViolationsByBranchRecord, Integer> cleaningCol =
                new TableColumn<>("Cleaning");
        cleaningCol.setCellValueFactory(new PropertyValueFactory<>("cleaningFeeCount"));
        cleaningCol.setPrefWidth(80);

        TableColumn<ViolationsByBranchRecord, Integer> otherCol =
                new TableColumn<>("Other");
        otherCol.setCellValueFactory(new PropertyValueFactory<>("otherViolationCount"));
        otherCol.setPrefWidth(80);

        TableColumn<ViolationsByBranchRecord, BigDecimal> penaltyCol =
                new TableColumn<>("Total Penalty");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("totalPenaltyAmount"));
        penaltyCol.setPrefWidth(120);

        TableColumn<ViolationsByBranchRecord, BigDecimal> avgCol =
                new TableColumn<>("Avg Penalty");
        avgCol.setCellValueFactory(new PropertyValueFactory<>("averagePenalty"));
        avgCol.setPrefWidth(100);

        // Align numeric columns right
        totalCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        lateCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        damageCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        trafficCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        cleaningCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        otherCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        penaltyCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        avgCol.setStyle("-fx-alignment: CENTER-RIGHT;");

        tableView.getColumns().addAll(branchCol, totalCol, lateCol, damageCol,
                trafficCol, cleaningCol, otherCol, penaltyCol, avgCol);

        // ============================================================
        // BUTTONS
        // ============================================================
        loadButton = new Button("Load Violations");
        loadButton.setPrefWidth(140);
        loadButton.getStyleClass().add("small-button");

        companyButton = new Button("Company Summary");
        companyButton.setPrefWidth(140);
        companyButton.getStyleClass().add("small-button");

        // NEW: Pie Chart Button
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

    public void showCompanyPopup(ViolationsByBranchRecord v) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Company Violations Summary");
        dialog.setHeaderText(null);

        // Purple gradient border to match other reports
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

        Label title = new Label("COMPANY VIOLATIONS SUMMARY");
        title.setStyle("""
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            """);

        Label total = new Label("Total Violations: " + v.getTotalViolations());
        Label late = new Label("Late Returns: " + v.getLateReturnCount());
        Label damage = new Label("Car Damage: " + v.getCarDamageCount());
        Label traffic = new Label("Traffic Violations: " + v.getTrafficViolationCount());
        Label cleaning = new Label("Cleaning Fees: " + v.getCleaningFeeCount());
        Label other = new Label("Other Violations: " + v.getOtherViolationCount());
        Label penalty = new Label("Total Penalties: ₱" + formatMoney(v.getTotalPenaltyAmount()));
        Label avg = new Label("Average Penalty: ₱" + formatMoney(v.getAveragePenalty()));

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

    // NEW: Pie Chart Method for Violations
    public void showPieChartPopup(java.util.List<ViolationsByBranchRecord> list) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Branch Violations Distribution");
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

        Label title = new Label("TOTAL VIOLATIONS BY BRANCH");
        title.setStyle("""
        -fx-text-fill: white;
        -fx-font-size: 22px;
        -fx-font-weight: bold;
        -fx-padding: 0 0 20 0;
    """);

        // Pie Chart - keep default colors for slices
        PieChart pie = new PieChart();
        pie.setLabelsVisible(true);
        pie.setLegendVisible(false);
        pie.setClockwise(true);
        pie.setStartAngle(90);

        // Apply CSS to make labels white
        pie.setStyle("""
        -fx-background-color: transparent;
        .chart-pie-label {
            -fx-fill: white;
            -fx-font-weight: bold;
            -fx-font-size: 12px;
        }
    """);

        for (ViolationsByBranchRecord v : list) {
            PieChart.Data slice = new PieChart.Data(
                    v.getBranchName(),
                    Math.max(0.1, v.getTotalViolations())
            );
            pie.getData().add(slice);
        }

        // Bigger size
        pie.setPrefSize(650, 520);

        // Double ensure labels are white after rendering
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

            // Also style any text nodes directly
            for (Node node : pie.lookupAll(".text")) {
                node.setStyle("-fx-fill: white;");
            }
        });

        // Right side label list
        Label labelTitle = new Label("Branches");
        labelTitle.setStyle("-fx-text-fill: #c7b3ff; -fx-font-size: 16px; -fx-font-weight: bold;");

        VBox labelBox = new VBox(10);
        labelBox.setPadding(new Insets(12));
        labelBox.setAlignment(Pos.TOP_LEFT);

        for (PieChart.Data slice : pie.getData()) {
            Label lbl = new Label(slice.getName() + " (" + (int)slice.getPieValue() + " violations)");
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
    public Button getPieChartButton() { return pieChartButton; } // NEW: Pie chart button accessor

    public TableView<ViolationsByBranchRecord> getTableView() { return tableView; }
}