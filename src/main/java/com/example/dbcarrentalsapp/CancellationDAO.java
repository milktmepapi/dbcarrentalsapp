package com.example.dbcarrentalsapp;

import model.CancellationRecord;
import model.ReturnRecord;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for handling all database operations related to cancellations.
 * This class provides CRUD operations for the cancellation_details table and follows
 * the DAO pattern to separate data access logic from business logic.
 *
 * Responsibilities:
 * - Generate unique cancellation IDs
 * - Perform CRUD operations on cancellation records
 * - Retrieve lists of related entities (rentals, staff)
 * - Handle database connections and SQL exceptions
 *
 */
public class CancellationDAO {
    /**
     * Generates the next sequential cancellation ID by querying the database
     * for the highest existing ID and incrementing it.
     *
     * @return Next available cancellation ID in format "CNLXX" (e.g., "CNL001", "CNL002")
     * @throws SQLException If database access error occurs
     */
    public String generateNextCancellationId() throws SQLException {
        // SQL query to get the highest existing cancellation ID
        String sql = "SELECT cancellation_id FROM cancellation_details WHERE cancellation_id LIKE 'CNL%' ORDER BY cancellation_id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // If records exist, parse the number and increment
            if (rs.next()) {
                String lastId = rs.getString("cancellation_id");
                int num = Integer.parseInt(lastId.substring(3)) + 1; // Extract number part
                return String.format("CNL%03d", num); // Format as VLN001, VLN002, etc.
            }
        }
        // If no records exist, start with VLN001
        return "CNL001";
    }

    private String generateNextCancellationID(Connection conn) throws SQLException {
        String sql = "SELECT cancellation_id FROM cancellation_details WHERE cancellation_id LIKE 'CNL%' ORDER BY cancellation_id DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                String lastId = rs.getString("cancellation_id"); // R0001
                int number = Integer.parseInt(lastId.substring(1)) + 1;
                return String.format("CNL%04d", number);
            }
            return "CNL0001"; // first record
        }
    }
    /**
     * Inserts a new cancellation record into the database.
     *
     * @param cancellation The CancellationRecord object containing all cancellation data
     * @throws SQLException If database access error occurs or constraint cancellation
     */
    public void addCancellation(CancellationRecord cancellation) throws SQLException {
        // SQL INSERT statement with parameter placeholders
        String sql = """
            INSERT INTO cancellation_details (
                cancellation_id, cancellation_rental_id, cancellation_staff_id,
                cancellation_date, cancellation_reason
            ) VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set parameters for the prepared statement
            stmt.setString(1, cancellation.getCancellationId());
            stmt.setString(2, cancellation.getCancellationRentalId());
            stmt.setString(3, cancellation.getCancellationStaffId());
            stmt.setTimestamp(4, Timestamp.valueOf(cancellation.getTimestamp()));
            stmt.setString(5, cancellation.getReason());

            // Execute the insert operation
            stmt.executeUpdate();
        }
    }
    public boolean addCancellation(Connection conn, CancellationRecord cancellation) throws SQLException {
        String sql = """
                INSERT INTO cancellation_details (
                    cancellation_id, cancellation_rental_id, cancellation_staff_id, 
                    cancellation_date, cancellation_reason
                ) VALUES (?, ?, ?, ?, ?)
                """;
        // generate new ID
        String newId = generateNextCancellationID(conn);
        cancellation.setCancellationId(newId);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cancellation.getCancellationId());
            ps.setString(2, cancellation.getCancellationRentalId());
            ps.setString(3, cancellation.getCancellationStaffId());
            ps.setTimestamp(4, Timestamp.valueOf(cancellation.getTimestamp()));
            ps.setString(5, cancellation.getReason());
            return ps.executeUpdate() > 0;
        }
    }
    /**
     * Updates an existing cancellation record in the database.
     *
     * @param cancellation The CancellationRecord object with updated data
     * @throws SQLException If database access error occurs or record not found
     */
    public void updateCancellation(CancellationRecord cancellation) throws SQLException {
        // SQL UPDATE statement to modify existing record
        String sql = """
            UPDATE cancellation_details 
            SET cancellation_rental_id = ?, cancellation_staff_id = ?, cancellation_date= ?, 
                cancellation_reason = ?
            WHERE cancellation_id = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set update parameters
            stmt.setString(1, cancellation.getCancellationRentalId());
            stmt.setString(2, cancellation.getCancellationStaffId());
            stmt.setTimestamp(3, Timestamp.valueOf(cancellation.getTimestamp()));
            stmt.setString(4, cancellation.getReason());
            stmt.setString(5, cancellation.getCancellationId());

            // Execute the update operation
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all cancellation records from the database, ordered by most recent first.
     *
     * @return List of all CancellationRecord objects in the database
     * @throws SQLException If database access error occurs
     */
    public List<CancellationRecord> getAllCancellations() throws SQLException {
        List<CancellationRecord> list = new ArrayList<>();
        // SQL query to get all cancellations, newest first
        String sql = "SELECT * FROM cancellation_details ORDER BY cancellation_date DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Convert each ResultSet row to CancellationRecord object
            while (rs.next()) {
                list.add(mapResultSetToCancellationRecord(rs));
            }
        }
        return list;
    }
    /**
     * Retrieves a specific cancellation record by its ID.
     *
     * @param id The cancellation ID to search for
     * @return CancellationRecord object if found, null otherwise
     * @throws SQLException If database access error occurs
     */
    public CancellationRecord getCancellationById(String id) throws SQLException {
        // Parameterized SQL query to find specific cancellation
        String sql = "SELECT * FROM cancellation_details WHERE cancellation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            // Return cancellation if found
            if (rs.next()) {
                return mapResultSetToCancellationRecord(rs);
            }
        }
        return null; // Return null if no cancellation found
    }

    /**
     * Deletes a cancellation record from the database.
     *
     * @param cancellationId The ID of the cancellation to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database access error occurs
     */
    public boolean deleteCancellation(String cancellationId) throws SQLException {
        // SQL DELETE statement with parameter
        String sql = "DELETE FROM cancellation_details WHERE cancellation_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cancellationId);
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
     * Validates if a staff member belongs to the same branch as the rental
     * and is from the Operations department.
     *
     * @param staffId The staff ID to validate
     * @param rentalId The rental ID to check against
     * @return true if staff is valid for processing this cancellation
     * @throws SQLException If database access error occurs
     */
    public boolean validateStaffForCancellation(String staffId, String rentalId) throws SQLException {
        String sql = """
            SELECT sr.staff_id, sr.branch_id, jr.job_department_id
            FROM staff_record sr
            JOIN job_record jr ON sr.staff_job_id = jr.job_id
            JOIN rental_details rd ON rd.rental_branch_id = sr.staff_branch_id
            WHERE sr.staff_id = ? AND rd.rental_id = ? AND jr.job_department_id = 'DEPT_OPS'
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, staffId);
            stmt.setString(2, rentalId);
            ResultSet rs = stmt.executeQuery();

            return rs.next(); // Returns true if staff is valid
        }
    }

    /**
     * Gets the branch ID of a rental
     */
    public String getRentalBranchId(String rentalId) throws SQLException {
        String sql = "SELECT rental_branch_id FROM rental_details WHERE rental_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rentalId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() ? rs.getString("rental_branch_id") : null;
        }
    }

    /**
     * Gets Operations staff members from a specific branch
     */
    public List<String> getOperationsStaffByBranch(String branchId) throws SQLException {
        List<String> staffIds = new ArrayList<>();
        String sql = """
            SELECT sr.staff_id 
            FROM staff_record sr
            JOIN job_record jr ON sr.staff_job_id = jr.job_id
            WHERE sr.staff_branch_id = ? AND jr.job_department_id = 'DEPT_OPS'
            ORDER BY sr.staff_id
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, branchId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                staffIds.add(rs.getString("staff_id"));
            }
        }
        return staffIds;
    }

    /**
     * Helper method to map a ResultSet row to a CancellationRecord object.
     * This method centralizes the conversion logic between database and object.
     *
     * @param rs The ResultSet containing cancellation data
     * @return CancellationRecord object populated with ResultSet data
     * @throws SQLException If error reading from ResultSet
     */
    private CancellationRecord mapResultSetToCancellationRecord(ResultSet rs) throws SQLException {
        // Extract timestamp from ResultSet
        Timestamp timestamp = rs.getTimestamp("cancellation_date");

        // Create and return new CancellationRecord with extracted data
        return new CancellationRecord(
                rs.getString("cancellation_id"),
                rs.getString("cancellation_rental_id"),
                rs.getString("cancellation_staff_id"),
                timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now(),
                rs.getString("cancellation_reason")
        );
    }
}
