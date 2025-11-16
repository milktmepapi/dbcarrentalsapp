package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.ReturnRecord;

import java.util.List;

public class ReturnController {

    private final ReturnDAO returnDAO = new ReturnDAO();
    private final ReturnView view;
    private final Stage stage;

    public ReturnController(ReturnView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        setupActions();
    }

    private void setupActions() {
        // Back button handled by controller
        view.backButton.setOnAction(e -> goBack());
    }

    private void goBack() {
        ManageTransactionsView mtv = new ManageTransactionsView(stage);
        new ManageTransactionsController(mtv, stage); // attach controller
        stage.setScene(mtv.getScene());
    }

    // Add return
    public boolean addReturn(ReturnRecord record) {
        return returnDAO.addReturn(record);
    }

    // Fetch all returns
    public ObservableList<ReturnRecord> getAllReturns() {
        List<ReturnRecord> list = returnDAO.getAllReturns();
        return FXCollections.observableArrayList(list);
    }

    // Update return
    public boolean updateReturn(ReturnRecord record) {
        return returnDAO.updateReturn(record);
    }

    // Delete return
    public boolean deleteReturn(String returnId) {
        return returnDAO.deleteReturn(returnId);
    }
}
