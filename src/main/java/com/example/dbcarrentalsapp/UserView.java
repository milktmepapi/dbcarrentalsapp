package com.example.dbcarrentalsapp;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.Font;

public class UserView {
    public Button records;
    public Button transactions;
    public Button reports;
    public Button exit;

    public UserView() {
        records = new Button("Records");
        transactions = new Button("Transactions");
        reports = new Button("Reports");
        exit = new Button("Exit");
    }

    public Scene getScene() {
        StackPane stackPane = new StackPane();

        // background image
        Image image = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/dashboard.png"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1152);
        imageView.setFitHeight(761);
        imageView.setPreserveRatio(false);
        stackPane.getChildren().add(imageView);

        // title text at top (slightly left)
        Text title = new Text("FORZA RENTALS");
        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"),
                56
        );
        if (f1Font != null) {
            title.setFont(f1Font);
        } else {
            title.setFont(Font.font("Arial Black", 56));
        }

        title.setStyle(
                "-fx-font-style: italic; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: white; " +
                        "-fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);"
        );

        // Centered
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(40, 0, 0, 0)); // top, right, bottom, left

        stackPane.getChildren().add(title);

        // button layout - using the ForzaDashboard positioning
        records.getStyleClass().add("custom-button");
        transactions.getStyleClass().add("custom-button");
        reports.getStyleClass().add("custom-button");
        exit.getStyleClass().add("custom-button");

        VBox menuBox = new VBox(10, records, transactions, reports, exit);
        menuBox.setAlignment(Pos.CENTER);

        // Apply ForzaDashboard positioning
        menuBox.setTranslateY(105);
        menuBox.setTranslateX(22);

        stackPane.getChildren().add(menuBox);

        // footnote at the bottom
        Text footnote = new Text("© 2025 Forza Rentals. All rights reserved.");
        footnote.setStyle("-fx-fill: white; -fx-font-size: 10px; -fx-opacity: 0.7;");

        stackPane.getChildren().add(footnote);
        StackPane.setAlignment(footnote, Pos.BOTTOM_CENTER);
        StackPane.setMargin(footnote, new Insets(0, 0, 30, 0));

        Scene scene = new Scene(stackPane, 1152, 761); // Updated to ForzaDashboard dimensions
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        return scene;
    }
}