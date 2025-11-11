package com.example.dbcarrentalsapp;

import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;

public class UserView {
    public Button records;
    public Button transactions;
    public Button exit;

    public UserView() {
        records = new Button("Records");
        transactions = new Button("Transactions");
        exit = new Button("Exit");
    }

    public Scene getScene() {
        // Use StackPane to layer the image and buttons
        StackPane stackPane = new StackPane();

        // ✅ Corrected image path
        Image image = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/img.png"));
        ImageView imageView = new ImageView(image);

        stackPane.getChildren().add(imageView);

        records.getStyleClass().add("custom-button");
        transactions.getStyleClass().add("custom-button");
        exit.getStyleClass().add("custom-button");

        VBox root = new VBox(20); // 20px spacing between elements
        root.setAlignment(Pos.CENTER); // Center the buttons
        root.setStyle("-fx-background-color: transparent");
        root.setPadding(new Insets(0, 0, 0, 50));
        root.setMaxWidth(VBox.USE_PREF_SIZE);
        root.getChildren().addAll(records, transactions, exit);

        stackPane.getChildren().add(root);
        StackPane.setAlignment(root, Pos.CENTER_LEFT);

        Scene scene = new Scene(stackPane, 815, 450);

        // Fixed the CSS path
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        return scene;
    }
}
