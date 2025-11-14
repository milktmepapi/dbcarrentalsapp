package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.BranchRecord;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.util.List;

public class BranchController {

    private final BranchView view;
    private final Stage stage;
    private final BranchDAO dao;
    private ObservableList<BranchRecord> masterList;

    public BranchController(BranchView view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.dao = new BranchDAO();

        setupActions();
        loadBranches();
    }

    /** Sets up all button and UI actions **/
    private void setupActions() {

        // ===== Return to Manage Records =====
        view.returnButton.setOnAction(e -> {
            ManageRecordsView manageView = new ManageRecordsView();
            new ManageRecordsController(manageView, stage);
            stage.setScene(manageView.getScene());
        });

        // ===== Add Branch =====
        view.addButton.setOnAction(e ->
                view.showAddBranchPopup(dao, this::loadBranches)
        );

        // ===== Modify Branch =====
        view.modifyButton.setOnAction(e -> {
            BranchRecord selected = view.tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                view.showModifyBranchPopup(dao, selected, this::loadBranches);
            } else {
                view.showSuccessPopup("No Selection", "Please select a branch to modify.");
            }
        });

        // ===== Delete Branch =====
        view.deleteButton.setOnAction(e -> {
            BranchRecord selected = view.tableView.getSelectionModel().getSelectedItem();

            if (selected == null) {
                view.showSuccessPopup("No Selection", "Please select a branch to delete.");
                return;
            }

            // Show confirmation popup with record details
            boolean confirmed = view.showConfirmPopup(selected);

            if (confirmed) {
                boolean success = dao.deleteBranch(selected.getBranchId());
                if (success) {
                    view.showSuccessPopup("Deleted", "Branch deleted successfully!");
                    loadBranches();
                    System.out.println("Deleted Branch ID: " + selected.getBranchId());
                } else {
                    view.showSuccessPopup("Error", "Failed to delete branch.");
                }
            }
        });

        // ===== Filter/Search =====
        view.filterButton.setOnAction(e -> applyFilter());

        // Optional: Press Enter in search field to filter
        view.searchField.setOnAction(e -> applyFilter());
    }

    /** Loads all branches from database **/
    public void loadBranches() {
        List<BranchRecord> branches = dao.getAllBranches();
        if (branches == null) branches = List.of(); // avoid NPE
        masterList = FXCollections.observableArrayList(branches);
        view.tableView.setItems(masterList);
    }

    /** Applies text-based filtering **/
    private void applyFilter() {
        String filterText = view.searchField.getText().toLowerCase().trim();
        String og = view.searchField.getText().trim();

        if (filterText.isEmpty()) {
            view.tableView.setItems(masterList);
            return;
        }

        ObservableList<BranchRecord> filteredList = masterList.filtered(record ->
                        record.getBranchName().toLowerCase().contains(filterText) ||
                        record.getBranchEmailAddress().contains(og) ||
                        record.getBranchId().contains(og) ||
                        record.getBranchLocationId().contains(og)
        );

        view.tableView.setItems(filteredList);
    }
}