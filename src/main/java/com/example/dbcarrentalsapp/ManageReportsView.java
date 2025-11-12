package com.example.dbcarrentalsapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ManageReportsView {

    private Scene scene;
    public Button revenueButton;
    public Button rentalsButton;
    public Button utilizationButton;
    public Button violationsButton;
    public Button returnButton;

    public ManageReportsView(Stage stage) {

        // ===== Initialize Buttons =====
        revenueButton = new Button("Revenue by Branch");
        rentalsButton = new Button("Rentals by Branch");
        utilizationButton = new Button("Car Utilization");
        violationsButton = new Button("Violations by Branch");
        returnButton = new Button("Return");

        // ===== Background =====
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream(
                "/com/example/dbcarrentalsapp/audi_r_zero_concept_black-normal.jpg"));
        ImageView imageView = new ImageView(image);
        stackPane.getChildren().add(imageView);

        // ===== Title =====
        Text title = new Text("GENERATE REPORTS");
        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 48
        );
        if (f1Font != null) {
            title.setFont(f1Font);
        } else {
            title.setFont(Font.font("Arial Black", 48));
        }

        title.setStyle(
                "-fx-fill: white; " +
                        "-fx-font-style: italic; " +
                        "-fx-font-weight: bold; " +
                        "-fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);"
        );
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        StackPane.setMargin(title, new Insets(40, 0, 0, 0));
        stackPane.getChildren().add(title);

        // ===== Button Styles =====
        String buttonStyle = "custom-button";
        revenueButton.getStyleClass().add(buttonStyle);
        rentalsButton.getStyleClass().add(buttonStyle);
        utilizationButton.getStyleClass().add(buttonStyle);
        violationsButton.getStyleClass().add(buttonStyle);
        returnButton.getStyleClass().add(buttonStyle);

        // ===== Layout =====
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                revenueButton,
                rentalsButton,
                utilizationButton,
                violationsButton,
                returnButton
        );

        // Position buttons lower
        StackPane.setAlignment(layout, Pos.CENTER);
        StackPane.setMargin(layout, new Insets(80, 0, 0, 0));

        stackPane.getChildren().add(layout);

        // ===== Scene Setup =====
        scene = new Scene(stackPane, 815, 450);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}
