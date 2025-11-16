package com.example.dbcarrentalsapp;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.DepartmentRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles CRUD operations for the department_record table.
 */
public class DepartmentDAO {

    /** Retrieve all departments */
    public ObservableList<DepartmentRecord> getAllDepartments() {
        ObservableList<DepartmentRecord> departments = FXCollections.observableArrayList();
        String query = "SELECT * FROM department_record ORDER BY department_id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DepartmentRecord department = new DepartmentRecord(
                        rs.getString("department_id"),
                        rs.getString("department_name")
                );
                departments.add(department);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }

    /** Add a new department */
    public boolean addDepartment(String id, String name) {
        String query = "INSERT INTO department_record (department_id, department_name) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Update an existing department */
    public boolean updateDepartment(String id, String name) {
        String query = "UPDATE department_record SET department_name = ? WHERE department_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Delete department by ID */
    public boolean deleteDepartment(String id) {
        String query = "DELETE FROM department_record WHERE department_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, id);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all department IDs for dropdown menus.
     */
    public List<String> getAllDepartmentIds() {
        List<String> departmentIds = new ArrayList<>();
        String query = "SELECT department_id FROM department_record ORDER BY department_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                departmentIds.add(rs.getString("department_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departmentIds;
    }

    /**
     * Retrieves all department IDs and names formatted for dropdowns,
     * e.g. "DPT001 — Sales".
     */
    public List<String> getAllDepartmentDisplayValues() {
        List<String> displayList = new ArrayList<>();
        String query = "SELECT department_id, department_name FROM department_record ORDER BY department_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("department_id");
                String name = rs.getString("department_name");
                displayList.add(id + " — " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return displayList;
    }
}
