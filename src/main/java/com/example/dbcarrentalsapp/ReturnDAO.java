
package com.example.dbcarrentalsapp;

import model.ReturnRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for handling return-related database operations.
 * Provides methods for retrieving rental details, checking for all returns,
 * recording the Staff ID, and Rental ID.
 */

public class ReturnDAO {

    private String generateNewReturnID(Connection conn) throws SQLException {
        String sql = "SELECT return_id FROM return_details ORDER BY return_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("return_id"); // R0001
                int number = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("R%04d", number);
            }
            return "R0001"; // first record
        }
    }

    // Add return record
    public boolean addReturn(ReturnRecord record) {
        String sql = "INSERT INTO return_details (return_id, return_rental_id, return_staff_id) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Generate new return ID
            String newId = generateNewReturnID(conn);
            record.setReturnID(newId);

            ps.setString(1, record.getReturnID());
            ps.setString(2, record.getReturnRentalID());
            ps.setString(3, record.getReturnStaffID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fetch all return records
    public List<ReturnRecord> getAllReturns() {
        List<ReturnRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM return_details ORDER BY return_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ReturnRecord rr = new ReturnRecord(
                        rs.getString("return_id"),
                        rs.getString("return_rental_id"),
                        rs.getString("return_staff_id")
                );
                list.add(rr);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // UPDATE: Update a return record
    public boolean updateReturn(ReturnRecord record) {
        String sql = "UPDATE return_details SET return_rental_id = ?, return_staff_id = ? WHERE return_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, record.getReturnRentalID());
            ps.setString(2, record.getReturnStaffID());
            ps.setString(3, record.getReturnID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE: Delete a return record
    public boolean deleteReturn(String returnId) {
        String sql = "DELETE FROM return_details WHERE return_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, returnId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

        public boolean addReturn(Connection conn, ReturnRecord record) throws SQLException {
        String sql = "INSERT INTO return_details (return_id, return_rental_id, return_staff_id) VALUES (?, ?, ?)";
        // generate new ID
        String newId = generateNewReturnID(conn);
        record.setReturnID(newId);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, record.getReturnID());
            ps.setString(2, record.getReturnRentalID());
            ps.setString(3, record.getReturnStaffID());
            return ps.executeUpdate() > 0;
        }
    }
}


