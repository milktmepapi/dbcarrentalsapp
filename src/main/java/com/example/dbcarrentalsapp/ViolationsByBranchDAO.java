package com.example.dbcarrentalsapp;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.ViolationsByBranchRecord;

public class ViolationsByBranchDAO {

    // ============================
    // 1. BRANCH VIOLATIONS
    // ============================
    public List<ViolationsByBranchRecord> getViolationsByBranch(LocalDate date, String granularity) {

        List<ViolationsByBranchRecord> violations = new ArrayList<>();

        String filter;

        switch (granularity.toLowerCase()) {
            case "daily" -> filter = "DATE(v.violation_timestamp) = ?";
            case "monthly" -> filter = "MONTH(v.violation_timestamp) = ? AND YEAR(v.violation_timestamp) = ?";
            case "yearly" -> filter = "YEAR(v.violation_timestamp) = ?";
            default -> throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

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

            // Bind parameters
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
            e.printStackTrace();
        }

        return violations;
    }

    // ============================
    // 2. COMPANY VIOLATIONS SUMMARY
    // ============================
    public ViolationsByBranchRecord getCompanyViolations(LocalDate date, String granularity) {

        String filter;

        switch (granularity.toLowerCase()) {
            case "daily" -> filter = "DATE(v.violation_timestamp) = ?";
            case "monthly" -> filter = "MONTH(v.violation_timestamp) = ? AND YEAR(v.violation_timestamp) = ?";
            case "yearly" -> filter = "YEAR(v.violation_timestamp) = ?";
            default -> throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

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

            // Bind parameters
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

            if (rs.next()) {
                Timestamp lastViolation = rs.getTimestamp("last_violation_date");
                LocalDateTime lastViolationDate = lastViolation != null ?
                        lastViolation.toLocalDateTime() : null;

                return new ViolationsByBranchRecord(
                        "ALL",
                        "WHOLE COMPANY",
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
            e.printStackTrace();
        }

        return null;
    }
}