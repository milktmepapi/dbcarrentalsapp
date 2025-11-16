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

    public boolean addBranch(String name, String emailAddress, String branchLocationId) {
        String id = generateNextBranchId(); // auto-generate ID here

        String checkComboSql = "SELECT COUNT(*) FROM branch_record WHERE branch_name = ? AND branch_email_address = ? AND branch_location_id = ?";
        String checkLocationSql = "SELECT COUNT(*) FROM branch_record WHERE branch_location_id = ?";
        String insertSql = "INSERT INTO branch_record (branch_id, branch_name, branch_email_address, branch_location_id) VALUES (?, ?, ?, ?)";
        String updateSeqSql = "UPDATE branch_id_sequence SET last_number = last_number + 1 WHERE id_type = 'BRANCH'";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Check if the location is already used
            try (PreparedStatement psCheckLocation = conn.prepareStatement(checkLocationSql)) {
                psCheckLocation.setString(1, branchLocationId);
                ResultSet rs = psCheckLocation.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: This location already has a branch.");
                    conn.rollback();
                    return false;
                }
            }

            // Check for duplicate (name+email+location)
            try (PreparedStatement psCheckCombo = conn.prepareStatement(checkComboSql)) {
                psCheckCombo.setString(1, name);
                psCheckCombo.setString(2, emailAddress);
                psCheckCombo.setString(3, branchLocationId);
                ResultSet rs = psCheckCombo.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: Name and Location ID combination already exists.");
                    conn.rollback();
                    return false;
                }
            }

            // Insert new branch
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setString(1, id);
                psInsert.setString(2, name);
                psInsert.setString(3, emailAddress);
                psInsert.setString(4, branchLocationId);
                psInsert.executeUpdate();
            }

            // Update sequence
            try (PreparedStatement psUpdateSeq = conn.prepareStatement(updateSeqSql)) {
                psUpdateSeq.executeUpdate();
            }

            conn.commit();
            return true;

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

            // Check if the location is already used by another branch
            String checkLocationSql = "SELECT COUNT(*) FROM branch_record WHERE branch_location_id = ? AND branch_id <> ?";

            try (PreparedStatement psCheckLocation = conn.prepareStatement(checkLocationSql)) {
                psCheckLocation.setString(1, branch_location_id);
                psCheckLocation.setString(2, id);
                ResultSet rs = psCheckLocation.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Error: This location is already assigned to another branch.");
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

    /**
     * Retrieves all branch IDs.
     *
     * @return List of branch IDs for dropdown menus.
     */
    public List<String> getAllBranchIds() {
        List<String> branchIds = new ArrayList<>();
        String query = "SELECT branch_id FROM branch_record ORDER BY branch_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                branchIds.add(rs.getString("branch_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branchIds;
    }

    public BranchRecord getBranchById(String branchId) {
        String sql = "SELECT * FROM branch_record WHERE branch_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, branchId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new BranchRecord(
                        rs.getString("branch_id"),
                        rs.getString("branch_name"),
                        rs.getString("branch_email_address"),
                        rs.getString("branch_location_id")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String generateNextBranchId() {
        String prefix = "BRN";
        String nextId = prefix + "001"; // Default fallback

        String selectSql = "SELECT last_number FROM branch_id_sequence WHERE id_type = 'BRANCH'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql);
             ResultSet rs = selectStmt.executeQuery()) {

            if (rs.next()) {
                int lastNumber = rs.getInt("last_number") + 1;
                nextId = String.format("%s%03d", prefix, lastNumber);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextId;
    }

    public List<String> getAllBranchDisplayValues() {
        List<String> list = new ArrayList<>();

        String sql = "SELECT branch_id, branch_name FROM branch_record ORDER BY branch_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("branch_id");
                String name = rs.getString("branch_name");
                list.add(id + " — " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}