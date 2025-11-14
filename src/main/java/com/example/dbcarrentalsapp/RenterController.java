package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.RenterRecord;

public class RenterController {

    private final RenterView view;
    private final RenterDAO dao;
    private final Stage stage;

    private final ObservableList<RenterRecord> renterList =
            FXCollections.observableArrayList();

    public RenterController(RenterView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new RenterDAO();

        loadRenters();
        setupActions();
    }

    /** Load all renters from DB */
    private void loadRenters() {
        renterList.setAll(dao.getAllRenters());
        view.tableView.setItems(renterList);
    }

    /** Setup button actions */
    private void setupActions() {

        // Add
        view.addButton.setOnAction(e ->
                view.showAddPopup(dao, this::loadRenters)
        );

        // Modify
        view.modifyButton.setOnAction(e ->
                view.showModifyPopup(dao,
                        view.tableView.getSelectionModel().getSelectedItem(),
                        this::loadRenters)
        );

        // Delete
        view.deleteButton.setOnAction(e -> {
            RenterRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (view.showConfirmPopup(selected)) {
                dao.deleteRenter(selected.getRenterDlNumber());
                loadRenters();
            }
        });

        // Search / Filter
        view.filterButton.setOnAction(e -> {
            String keyword = view.searchField.getText().trim().toLowerCase();

            if (keyword.isEmpty()) {
                loadRenters();
                return;
            }

            renterList.setAll(
                    dao.getAllRenters().stream()
                            .filter(r ->
                                    r.getRenterFirstName().toLowerCase().contains(keyword)
                                            || r.getRenterLastName().toLowerCase().contains(keyword)
                                            || r.getRenterPhoneNumber().contains(keyword)
                            )
                            .toList()
            );
        });

        // Return
        view.returnButton.setOnAction(e -> {
            ManageRecordsView mr = new ManageRecordsView();
            new ManageRecordsController(mr, stage);
            stage.setScene(mr.getScene());
        });
    }
}