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
import model.RevenueByBranchRecord;

import java.math.BigDecimal;

public class RevenueByBranchView {

    public TableView<RevenueByBranchRecord> tableView;
    public Button loadButton, returnButton, companyButton;

    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final ToggleGroup granularityGroup = new ToggleGroup();

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

        HBox bottomButtons = new HBox(20, loadButton, companyButton, returnButton);
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