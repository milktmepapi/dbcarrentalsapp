package com.example.dbcarrentalsapp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LocationView {

    private Scene scene;
    public Button addButton;
    public Button modifyButton;
    public Button deleteButton;
    public Button returnButton;

    public LocationView(Stage stage) {

        // Initialize buttons
        addButton = new Button("Add");
        modifyButton = new Button("Modify");
        deleteButton = new Button("Delete");
        returnButton = new Button("Return");

        // Background
        StackPane stackPane = new StackPane();
        Image image = new Image(getClass().getResourceAsStream("/com/example/dbcarrentalsapp/img2.png"));
        ImageView imageView = new ImageView(image);
        stackPane.getChildren().add(imageView);

        // Style the buttons
        String buttonStyle = "custom-button";
        addButton.getStyleClass().add(buttonStyle);
        modifyButton.getStyleClass().add(buttonStyle);
        deleteButton.getStyleClass().add(buttonStyle);
        returnButton.getStyleClass().add(buttonStyle);

        // Layout for buttons
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(addButton, modifyButton, deleteButton, returnButton);

        stackPane.getChildren().add(layout);

        // Scene setup
        scene = new Scene(stackPane, 815, 450);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/dbcarrentalsapp/style.css").toExternalForm()
        );

        // Return button action → go back to ManageRecordsView
        returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView(stage);
            stage.setScene(manageView.getScene());
        });
    }

    public Scene getScene() {
        return scene;
    }
}
