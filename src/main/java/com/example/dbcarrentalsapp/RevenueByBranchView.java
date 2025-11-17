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
    public Button loadButton, returnButton;
    public RadioButton dailyButton, monthlyButton, yearlyButton;
    private final Scene scene;
    private final ToggleGroup granularityGroup = new ToggleGroup();

    public RevenueByBranchView() {
        // ===== Background =====
        StackPane root = new StackPane();
        Image bgImage = new Image(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.png")
        );
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(1152);
        bgView.setFitHeight(761);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // ===== Title =====
        Text title = new Text("BRANCH REVENUE REPORT");
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

        // ===== Controls =====
        dailyButton = new RadioButton("Daily");
        monthlyButton = new RadioButton("Monthly");
        yearlyButton = new RadioButton("Yearly");
        dailyButton.setToggleGroup(granularityGroup);
        monthlyButton.setToggleGroup(granularityGroup);
        yearlyButton.setToggleGroup(granularityGroup);
        dailyButton.setSelected(true);

        HBox granularityBox = new HBox(10, dailyButton, monthlyButton, yearlyButton);
        granularityBox.setAlignment(Pos.CENTER);
        granularityBox.setPadding(new Insets(0, 10, 0, 10));

        loadButton = new Button("LOAD");
        loadButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");

        returnButton = new Button("RETURN");
        returnButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white; -fx-font-size: 14px;");

        VBox controlBox = new VBox(10, granularityBox, loadButton);
        controlBox.setAlignment(Pos.CENTER);
        controlBox.setPadding(new Insets(10, 0, 30, 0));

        // ===== Table =====
        tableView = new TableView<>();
        tableView.setPrefWidth(800);
        tableView.setPrefHeight(350);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<RevenueByBranchRecord, String> branchCol = new TableColumn<>("Branch");
        branchCol.setCellValueFactory(new PropertyValueFactory<>("branchName"));

        TableColumn<RevenueByBranchRecord, BigDecimal> rentalCol = new TableColumn<>("Rental Income");
        rentalCol.setCellValueFactory(new PropertyValueFactory<>("rentalIncome"));

        TableColumn<RevenueByBranchRecord, BigDecimal> penaltyCol = new TableColumn<>("Penalty Income");
        penaltyCol.setCellValueFactory(new PropertyValueFactory<>("penaltyIncome"));

        TableColumn<RevenueByBranchRecord, BigDecimal> salaryCol = new TableColumn<>("Salary Expenses");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salaryExpenses"));

        TableColumn<RevenueByBranchRecord, BigDecimal> netCol = new TableColumn<>("Net Revenue");
        netCol.setCellValueFactory(new PropertyValueFactory<>("netRevenue"));

        tableView.getColumns().addAll(branchCol, rentalCol, penaltyCol, salaryCol, netCol);

        VBox tableCard = new VBox(15, tableView, returnButton);
        tableCard.setAlignment(Pos.CENTER);
        tableCard.setPadding(new Insets(20));
        tableCard.setStyle("-fx-background-color: rgba(25,25,35,0.85); -fx-background-radius: 15;"
                + "-fx-border-color: linear-gradient(to right, #7a40ff, #b46bff); -fx-border-radius: 15; -fx-border-width: 2;");

        VBox layout = new VBox(30, controlBox, tableCard);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(140, 0, 0, 0));

        root.getChildren().add(layout);

        // ===== Scene =====
        scene = new Scene(root, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }

    public RadioButton getSelectedGranularityToggle() {
        return (RadioButton) granularityGroup.getSelectedToggle();
    }

    public Button getLoadButton() {
        return loadButton;
    }

    public Button getReturnButton() {
        return returnButton;
    }

    public TableView<RevenueByBranchRecord> getTableView() {
        return tableView;
    }
}