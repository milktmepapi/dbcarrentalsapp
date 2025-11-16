package com.example.dbcarrentalsapp;

import model.ViolationRecord;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for handling all database operations related to violations.
 * This class provides CRUD operations for the violation_details table and follows
 * the DAO pattern to separate data access logic from business logic.
 *
 * Responsibilities:
 * - Generate unique violation IDs
 * - Perform CRUD operations on violation records
 * - Retrieve lists of related entities (rentals, staff)
 * - Handle database connections and SQL exceptions
 *
 */
public class ViolationDAO {

    /**
     * Generates the next sequential violation ID by querying the database
     * for the highest existing ID and incrementing it.
     *
     * @return Next available violation ID in format "VLNXXX" (e.g., "VLN001", "VLN002")
     * @throws SQLException If database access error occurs
     */
    public String generateNextViolationId() throws SQLException {
        // SQL query to get the highest existing violation ID
        String sql = "SELECT violation_id FROM violation_details WHERE violation_id LIKE 'VLN%' ORDER BY violation_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // If records exist, parse the number and increment
            if (rs.next()) {
                String lastId = rs.getString("violation_id");
                int num = Integer.parseInt(lastId.substring(3)) + 1; // Extract number part
                return String.format("VLN%03d", num); // Format as VLN001, VLN002, etc.
            }
        }
        // If no records exist, start with VLN001
        return "VLN001";
    }

    /**
     * Inserts a new violation record into the database.
     *
     * @param violation The ViolationRecord object containing all violation data
     * @throws SQLException If database access error occurs or constraint violation
     */
    public void addViolation(ViolationRecord violation) throws SQLException {
        // SQL INSERT statement with parameter placeholders
        String sql = """
            INSERT INTO violation_details (
                violation_id, violation_rental_id, violation_staff_id,
                violation_type, violation_penalty_fee, violation_reason,
                violation_duration_hours, violation_timestamp
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            stmt.setString(1, violation.getViolationId());
            stmt.setString(2, violation.getRentalId());
            stmt.setString(3, violation.getStaffId());
            stmt.setString(4, violation.getViolationType());
            stmt.setDouble(5, violation.getPenaltyFee());
            stmt.setString(6, violation.getReason());
            stmt.setInt(7, violation.getDurationHours());
            stmt.setTimestamp(8, Timestamp.valueOf(violation.getTimestamp()));

            // Execute the insert operation
            stmt.executeUpdate();
        }
    }

    /**
     * Updates an existing violation record in the database.
     *
     * @param violation The ViolationRecord object with updated data
     * @throws SQLException If database access error occurs or record not found
     */
    public void updateViolation(ViolationRecord violation) throws SQLException {
        // SQL UPDATE statement to modify existing record
        String sql = """
            UPDATE violation_details 
            SET violation_rental_id = ?, violation_staff_id = ?, violation_type = ?,
                violation_penalty_fee = ?, violation_reason = ?, violation_duration_hours = ?,
                violation_timestamp = ?
            WHERE violation_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set update parameters
            stmt.setString(1, violation.getRentalId());
            stmt.setString(2, violation.getStaffId());
            stmt.setString(3, violation.getViolationType());
            stmt.setDouble(4, violation.getPenaltyFee());
            stmt.setString(5, violation.getReason());
            stmt.setInt(6, violation.getDurationHours());
            stmt.setTimestamp(7, Timestamp.valueOf(violation.getTimestamp()));
            stmt.setString(8, violation.getViolationId());

            // Execute the update operation
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all violation records from the database, ordered by most recent first.
     *
     * @return List of all ViolationRecord objects in the database
     * @throws SQLException If database access error occurs
     */
    public List<ViolationRecord> getAllViolations() throws SQLException {
        List<ViolationRecord> list = new ArrayList<>();
        // SQL query to get all violations, newest first
        String sql = "SELECT * FROM violation_details ORDER BY violation_timestamp DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Convert each ResultSet row to ViolationRecord object
            while (rs.next()) {
                list.add(mapResultSetToViolationRecord(rs));
            }
        }
        return list;
    }

    /**
     * Retrieves a specific violation record by its ID.
     *
     * @param id The violation ID to search for
     * @return ViolationRecord object if found, null otherwise
     * @throws SQLException If database access error occurs
     */
    public ViolationRecord getViolationById(String id) throws SQLException {
        // Parameterized SQL query to find specific violation
        String sql = "SELECT * FROM violation_details WHERE violation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            // Return violation if found
            if (rs.next()) {
                return mapResultSetToViolationRecord(rs);
            }
        }
        return null; // Return null if no violation found
    }

    /**
     * Deletes a violation record from the database.
     *
     * @param violationId The ID of the violation to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database access error occurs
     */
    public boolean deleteViolation(String violationId) throws SQLException {
        // SQL DELETE statement with parameter
        String sql = "DELETE FROM violation_details WHERE violation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, violationId);
            // Return true if at least one row was affected
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all rental IDs from the database for dropdown population.
     *
     * @return List of all rental IDs as strings
     * @throws SQLException If database access error occurs
     */
    public List<String> getAllRentalIds() throws SQLException {
        List<String> rentalIds = new ArrayList<>();
        String sql = "SELECT rental_id FROM rental_details ORDER BY rental_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                rentalIds.add(rs.getString("rental_id"));
            }
        }
        return rentalIds;
    }

    /**
     * Retrieves all staff IDs from the database for dropdown population.
     *
     * @return List of all staff IDs as strings
     * @throws SQLException If database access error occurs
     */
    public List<String> getAllStaffIds() throws SQLException {
        List<String> staffIds = new ArrayList<>();
        String sql = "SELECT staff_id FROM staff_record ORDER BY staff_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                staffIds.add(rs.getString("staff_id"));
            }
        }
        return staffIds;
    }

    /**
     * Helper method to map a ResultSet row to a ViolationRecord object.
     * This method centralizes the conversion logic between database and object.
     *
     * @param rs The ResultSet containing violation data
     * @return ViolationRecord object populated with ResultSet data
     * @throws SQLException If error reading from ResultSet
     */
    private ViolationRecord mapResultSetToViolationRecord(ResultSet rs) throws SQLException {
        // Extract timestamp from ResultSet
        Timestamp timestamp = rs.getTimestamp("violation_timestamp");

        // Create and return new ViolationRecord with extracted data
        return new ViolationRecord(
                rs.getString("violation_id"),
                rs.getString("violation_rental_id"),
                rs.getString("violation_staff_id"),
                rs.getString("violation_type"),
                rs.getDouble("violation_penalty_fee"),
                rs.getString("violation_reason"),
                rs.getInt("violation_duration_hours"),
                timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now()
        );
    }
}