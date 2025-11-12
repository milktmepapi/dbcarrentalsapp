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

public class ManageTransactionsView {

    private Scene scene;
    public Button rentalsButton;
    public Button cancellationsButton;
    public Button violationsButton;
    public Button returnsButton;
    public Button backButton;

    public ManageTransactionsView(Stage stage) {

        // ===== Initialize Buttons =====
        rentalsButton = new Button("Rentals");
        cancellationsButton = new Button("Rental Cancellations");
        violationsButton = new Button("Rental Violations");
        returnsButton = new Button("Rental Returns");
        backButton = new Button("Return");

        // Set larger button size
        rentalsButton.setPrefSize(280, 80);
        cancellationsButton.setPrefSize(280, 80);
        violationsButton.setPrefSize(280, 80);
        returnsButton.setPrefSize(280, 80);
        backButton.setPrefSize(280, 80);

        // Set larger font for buttons
        String buttonFontStyle = "-fx-font-size: 20px; -fx-font-weight: bold;";
        rentalsButton.setStyle(buttonFontStyle);
        cancellationsButton.setStyle(buttonFontStyle);
        violationsButton.setStyle(buttonFontStyle);
        returnsButton.setStyle(buttonFontStyle);
        backButton.setStyle(buttonFontStyle);

        // ===== Background =====
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream(
                "/com/example/dbcarrentalsapp/mercedes_benz_glc_400_4matic_with_eq_technology_amg_line_2026-1920x1080.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1152);
        imageView.setFitHeight(761);
        imageView.setPreserveRatio(false);
        stackPane.getChildren().add(imageView);

        // ===== Title =====
        Text title = new Text("MANAGE TRANSACTIONS");
        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"), 56
        );
        if (f1Font != null) {
            title.setFont(f1Font);
        } else {
            title.setFont(Font.font("Arial Black", 56));
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
        rentalsButton.getStyleClass().add(buttonStyle);
        cancellationsButton.getStyleClass().add(buttonStyle);
        violationsButton.getStyleClass().add(buttonStyle);
        returnsButton.getStyleClass().add(buttonStyle);
        backButton.getStyleClass().add(buttonStyle);

        // ===== Layout =====
        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(
                rentalsButton,
                cancellationsButton,
                violationsButton,
                returnsButton,
                backButton
        );

        stackPane.getChildren().add(layout);

        // ===== Scene Setup =====
        scene = new Scene(stackPane, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}