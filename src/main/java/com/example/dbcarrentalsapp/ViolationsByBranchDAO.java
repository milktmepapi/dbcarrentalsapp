package com.example.dbcarrentalsapp;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.ViolationsByBranchRecord;

/**
 * Data Access Object for violations by branch reporting.
 * Handles all database operations related to retrieving violation statistics
 * for individual branches and company-wide summaries.
 */
public class ViolationsByBranchDAO {

    /**
     * Retrieves violation statistics grouped by branch for the specified date and granularity.
     * Returns a list of ViolationsByBranchRecord objects containing counts and amounts per branch.
     * @param date the reference date for filtering violations
     * @param granularity the time period granularity ("daily", "monthly", or "yearly")
     * @return List of ViolationsByBranchRecord objects, empty if no violations found
     */
    public List<ViolationsByBranchRecord> getViolationsByBranch(LocalDate date, String granularity) {

        List<ViolationsByBranchRecord> violations = new ArrayList<>();

        String filter;

        // Determine the SQL filter condition based on granularity
        switch (granularity.toLowerCase()) {
            case "daily" -> filter = "DATE(v.violation_timestamp) = ?";
            case "monthly" -> filter = "MONTH(v.violation_timestamp) = ? AND YEAR(v.violation_timestamp) = ?";
            case "yearly" -> filter = "YEAR(v.violation_timestamp) = ?";
            default -> throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

        // SQL query to aggregate violation data by branch
        String query = String.format("""
        SELECT
            b.branch_id,
            b.branch_name,
            COUNT(v.violation_id) AS total_violations,
            SUM(CASE WHEN v.violation_type = 'Late Return' THEN 1 ELSE 0 END) AS late_return_count,
            SUM(CASE WHEN v.violation_type = 'Car Damage' THEN 1 ELSE 0 END) AS car_damage_count,
            SUM(CASE WHEN v.violation_type = 'Traffic Violation' THEN 1 ELSE 0 END) AS traffic_violation_count,
            SUM(CASE WHEN v.violation_type = 'Cleaning Fee' THEN 1 ELSE 0 END) AS cleaning_fee_count,
            SUM(CASE WHEN v.violation_type = 'Other' THEN 1 ELSE 0 END) AS other_violation_count,
            COALESCE(SUM(v.violation_penalty_fee), 0) AS total_penalty_amount,
            MAX(v.violation_timestamp) AS last_violation_date
            
        FROM branch_record b
        
        LEFT JOIN rental_details r ON r.rental_branch_id = b.branch_id
        LEFT JOIN violation_details v ON v.violation_rental_id = r.rental_id
            AND %s
        
        GROUP BY b.branch_id, b.branch_name
        HAVING total_violations > 0
        ORDER BY total_violations DESC, total_penalty_amount DESC;
        """, filter);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameters based on granularity
            if (granularity.equalsIgnoreCase("daily")) {
                stmt.setDate(1, Date.valueOf(date));
            }
            else if (granularity.equalsIgnoreCase("monthly")) {
                stmt.setInt(1, date.getMonthValue());
                stmt.setInt(2, date.getYear());
            }
            else { // yearly
                stmt.setInt(1, date.getYear());
            }

            ResultSet rs = stmt.executeQuery();

            // Process result set and create ViolationsByBranchRecord objects
            while (rs.next()) {
                Timestamp lastViolation = rs.getTimestamp("last_violation_date");
                LocalDateTime lastViolationDate = lastViolation != null ?
                        lastViolation.toLocalDateTime() : null;

                violations.add(new ViolationsByBranchRecord(
                        rs.getString("branch_id"),
                        rs.getString("branch_name"),
                        rs.getInt("total_violations"),
                        rs.getInt("late_return_count"),
                        rs.getInt("car_damage_count"),
                        rs.getInt("traffic_violation_count"),
                        rs.getInt("cleaning_fee_count"),
                        rs.getInt("other_violation_count"),
                        rs.getBigDecimal("total_penalty_amount"),
                        lastViolationDate
                ));
            }

        } catch (SQLException e) {
            // Log database errors - in production, use proper logging framework
            System.err.println("Database error in getViolationsByBranch: " + e.getMessage());
            e.printStackTrace();
        }

        return violations;
    }

    /**
     * Retrieves company-wide violation summary for the specified date and granularity.
     * Returns a single ViolationsByBranchRecord representing the entire company's violations.
     * @param date the reference date for filtering violations
     * @param granularity the time period granularity ("daily", "monthly", or "yearly")
     * @return ViolationsByBranchRecord with company summary, null if no violations found
     */
    public ViolationsByBranchRecord getCompanyViolations(LocalDate date, String granularity) {

        String filter;

        // Determine the SQL filter condition based on granularity
        switch (granularity.toLowerCase()) {
            case "daily" -> filter = "DATE(v.violation_timestamp) = ?";
            case "monthly" -> filter = "MONTH(v.violation_timestamp) = ? AND YEAR(v.violation_timestamp) = ?";
            case "yearly" -> filter = "YEAR(v.violation_timestamp) = ?";
            default -> throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

        // SQL query to aggregate violation data for the entire company
        String query = String.format("""
        SELECT
            COUNT(v.violation_id) AS total_violations,
            SUM(CASE WHEN v.violation_type = 'Late Return' THEN 1 ELSE 0 END) AS late_return_count,
            SUM(CASE WHEN v.violation_type = 'Car Damage' THEN 1 ELSE 0 END) AS car_damage_count,
            SUM(CASE WHEN v.violation_type = 'Traffic Violation' THEN 1 ELSE 0 END) AS traffic_violation_count,
            SUM(CASE WHEN v.violation_type = 'Cleaning Fee' THEN 1 ELSE 0 END) AS cleaning_fee_count,
            SUM(CASE WHEN v.violation_type = 'Other' THEN 1 ELSE 0 END) AS other_violation_count,
            COALESCE(SUM(v.violation_penalty_fee), 0) AS total_penalty_amount,
            MAX(v.violation_timestamp) AS last_violation_date
            
        FROM violation_details v
        WHERE %s;
        """, filter);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameters based on granularity
            if (granularity.equalsIgnoreCase("daily")) {
                stmt.setDate(1, Date.valueOf(date));
            }
            else if (granularity.equalsIgnoreCase("monthly")) {
                stmt.setInt(1, date.getMonthValue());
                stmt.setInt(2, date.getYear());
            }
            else { // yearly
                stmt.setInt(1, date.getYear());
            }

            ResultSet rs = stmt.executeQuery();

            // Create company summary record if data exists
            if (rs.next()) {
                Timestamp lastViolation = rs.getTimestamp("last_violation_date");
                LocalDateTime lastViolationDate = lastViolation != null ?
                        lastViolation.toLocalDateTime() : null;

                return new ViolationsByBranchRecord(
                        "ALL",           // Special branch ID for company summary
                        "WHOLE COMPANY", // Special branch name for company summary
                        rs.getInt("total_violations"),
                        rs.getInt("late_return_count"),
                        rs.getInt("car_damage_count"),
                        rs.getInt("traffic_violation_count"),
                        rs.getInt("cleaning_fee_count"),
                        rs.getInt("other_violation_count"),
                        rs.getBigDecimal("total_penalty_amount"),
                        lastViolationDate
                );
            }

        } catch (SQLException e) {
            // Log database errors - in production, use proper logging framework
            System.err.println("Database error in getCompanyViolations: " + e.getMessage());
            e.printStackTrace();
        }

        return null; // No violation data found
    }
}