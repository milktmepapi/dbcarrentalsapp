package com.example.dbcarrentalsapp;

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

public class LocationView {

    private Scene scene;
    public Button addButton;
    public Button modifyButton;
    public Button deleteButton;
    public Button returnButton;
    public Button viewButton;

    public LocationView(Stage stage) {

        // Initialize buttons with larger size
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        deleteButton = new Button("Delete");
        viewButton = new Button("View");
        returnButton = new Button("Return");

        // Set larger button size
        addButton.setPrefSize(280, 80);
        modifyButton.setPrefSize(280, 80);
        deleteButton.setPrefSize(280, 80);
        viewButton.setPrefSize(280, 80);
        returnButton.setPrefSize(280, 80);

        // Set larger font for buttons
        String buttonFontStyle = "-fx-font-size: 20px; -fx-font-weight: bold;";
        addButton.setStyle(buttonFontStyle);
        modifyButton.setStyle(buttonFontStyle);
        deleteButton.setStyle(buttonFontStyle);
        viewButton.setStyle(buttonFontStyle);
        returnButton.setStyle(buttonFontStyle);

        // Background
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/aston_martin_dbs-wide.jpg"));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(1152);
        imageView.setFitHeight(761);
        imageView.setPreserveRatio(false);
        stackPane.getChildren().add(imageView);

        // ===== TITLE =====
        Text title = new Text("LOCATION MANAGEMENT");

        // Load ROG font with larger size
        Font f1Font = Font.loadFont(
                getClass().getResourceAsStream("/com/example/dbcarrentalsapp/Formula1-Bold_web_0.ttf"),
                56
        );
        if (f1Font != null) {
            title.setFont(f1Font);
        } else {
            title.setFont(Font.font("Arial Black", 56));
        }

        // Style title
        title.setStyle(
                "-fx-font-style: italic; " +
                        "-fx-font-weight: bold; " +
                        "-fx-fill: white; " +
                        "-fx-effect: dropshadow(gaussian, black, 4, 0.5, 1, 1);"
        );

        // Align title at top-center with margin
        StackPane.setAlignment(title, Pos.TOP_CENTER);
        javafx.geometry.Insets titleMargin = new javafx.geometry.Insets(40, 0, 0, 0);
        StackPane.setMargin(title, titleMargin);
        stackPane.getChildren().add(title);

        // Style the buttons
        String buttonStyle = "custom-button";
        addButton.getStyleClass().add(buttonStyle);
        modifyButton.getStyleClass().add(buttonStyle);
        deleteButton.getStyleClass().add(buttonStyle);
        viewButton.getStyleClass().add(buttonStyle);
        returnButton.getStyleClass().add(buttonStyle);

        // Layout for buttons with larger spacing
        VBox layout = new VBox(40);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(addButton, modifyButton, deleteButton, viewButton, returnButton);

        stackPane.getChildren().add(layout);

        // Scene setup with UserView dimensions
        scene = new Scene(stackPane, 1152, 761);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );
    }

    public Scene getScene() {
        return scene;
    }
}