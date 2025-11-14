package com.example.dbcarrentalsapp;

import model.BranchRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all database operations for the Location module.
 */
public class BranchDAO {

    /**
     * Retrieves all branch records, ordered by ID.
     *
     * @return List of all locations from the database.
     */
    public List<BranchRecord> getAllBranches() {
        List<BranchRecord> branches = new ArrayList<>();
        String sql = "SELECT branch_id, branch_name, branch_email_address, branch_location_id " +
                "FROM branch_record ORDER BY branch_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                branches.add(new BranchRecord(
                        rs.getString("branch_id"),
                        rs.getString("branch_name"),
                        rs.getString("branch_email_address"),
                        rs.getString("branch_location_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    /**
     * Adds a new branch if both ID and Location are unique.
     *
     * @param id       the location ID (cannot be changed)
     * @param name     the new city name
     * @param email_address the new province name
     * @param branch_location_id the branch location ID
     * @return true if added successfully, false otherwise
     */
    public boolean addBranch(String id, String name, String email_address, String branch_location_id) {
        String checkIdSql = "SELECT COUNT(*) FROM branch_record WHERE branch_id = ?";
        String checkComboSql = "SELECT COUNT(*) FROM branch_record WHERE branch_name = ? AND branch_email_address = ? AND branch_location_id = ?";
        String insertSql = "INSERT INTO branch_record (branch_id, branch_name, branch_email_address, branch_location_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {

            // === Check if ID already exists ===
            try (PreparedStatement psCheckId = conn.prepareStatement(checkIdSql)) {
                psCheckId.setString(1, id);
                ResultSet rs = psCheckId.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Branch ID already exists.");
                    return false;
                }
            }

            // === Check if (city, province) already exists ===
            try (PreparedStatement psCheckCombo = conn.prepareStatement(checkComboSql)) {
                psCheckCombo.setString(1, name);
                psCheckCombo.setString(2, email_address);
                psCheckCombo.setString(3, branch_location_id);
                ResultSet rs = psCheckCombo.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Name and Location ID combination already exists.");
                    return false;
                }
            }

            // === If both checks pass, insert the record ===
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, id);
                psInsert.setString(2, name);
                psInsert.setString(3, email_address);
                psInsert.setString(4, branch_location_id);
                psInsert.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing branch's city and province by its ID.
     *
     * @param id       the location ID (cannot be changed)
     * @param name     the new city name
     * @param email_address the new province name
     * @param branch_location_id the brnach location ID
     * @return true if update succeeded, false otherwise
     */
    public boolean updateBranch(String id, String name, String email_address, String branch_location_id) {
        String checkComboSql = "SELECT COUNT(*) FROM branch_record WHERE branch_name = ? AND branch_email_address = ? AND branch_location_id = ? AND branch_id <> ?";
        String updateSql = "UPDATE branch_record SET branch_name = ?, branch_email_address = ?, branch_location_id = ? WHERE branch_id = ?";

        try (Connection conn = DBConnection.getConnection()) {

            // === Prevent duplicate city+province ===
            try (PreparedStatement psCheck = conn.prepareStatement(checkComboSql)) {
                psCheck.setString(1, name);
                psCheck.setString(2, email_address);
                psCheck.setString(3, branch_location_id);
                psCheck.setString(4, id);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: City and province combination already exists.");
                    return false;
                }
            }

            // === Perform update ===
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, name);
                ps.setString(2, email_address);
                ps.setString(3, branch_location_id);
                ps.setString(4, id);
                int rows = ps.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a branch by its ID.
     *
     * @param id the location ID to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteBranch(String id) {
        String sql = "DELETE FROM branch_record WHERE branch_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // true if something was deleted

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}