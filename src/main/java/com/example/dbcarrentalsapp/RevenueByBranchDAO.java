package com.example.dbcarrentalsapp;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.RevenueByBranchRecord;

public class RevenueByBranchDAO {

    // ============================
    // 1. BRANCH REVENUE
    // ============================
    public List<RevenueByBranchRecord> getRevenueByBranch(LocalDate date, String granularity) {

        List<RevenueByBranchRecord> revenues = new ArrayList<>();

        // Split filters
        String filterR;      // rental_details r (main)
        String filterRD;     // rental_details rd (penalty)

        switch (granularity.toLowerCase()) {
            case "daily" -> {
                filterR  = "DATE(r.rental_datetime) = ?";
                filterRD = "DATE(rd.rental_datetime) = ?";
            }
            case "monthly" -> {
                filterR  = "MONTH(r.rental_datetime) = ? AND YEAR(r.rental_datetime) = ?";
                filterRD = "MONTH(rd.rental_datetime) = ? AND YEAR(rd.rental_datetime) = ?";
            }
            case "yearly" -> {
                filterR  = "YEAR(r.rental_datetime) = ?";
                filterRD = "YEAR(rd.rental_datetime) = ?";
            }
            default -> throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

        String query = String.format("""
        SELECT
            b.branch_id,
            b.branch_name,

            COALESCE(SUM(r.rental_total_payment), 0) AS rental_income,

            (
                SELECT COALESCE(SUM(v.violation_penalty_fee), 0)
                FROM violation_details v
                INNER JOIN rental_details rd ON v.violation_rental_id = rd.rental_id
                WHERE rd.rental_branch_id = b.branch_id
                  AND %s
            ) AS penalty_income,

            (
                SELECT SUM(j.job_salary)
                FROM staff_record s
                INNER JOIN job_record j ON s.staff_job_id = j.job_id
                WHERE s.staff_branch_id = b.branch_id
            ) AS salary_expenses

        FROM branch_record b

        LEFT JOIN rental_details r ON r.rental_branch_id = b.branch_id
            AND %s
            AND r.rental_datetime <= NOW()

        GROUP BY b.branch_id, b.branch_name
        ORDER BY rental_income DESC;
        """, filterRD, filterR);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Bind parameters in correct sequence
            if (granularity.equalsIgnoreCase("daily")) {
                stmt.setDate(1, Date.valueOf(date)); // rd
                stmt.setDate(2, Date.valueOf(date)); // r
            }
            else if (granularity.equalsIgnoreCase("monthly")) {
                stmt.setInt(1, date.getMonthValue()); // rd
                stmt.setInt(2, date.getYear());
                stmt.setInt(3, date.getMonthValue()); // r
                stmt.setInt(4, date.getYear());
            }
            else { // yearly
                stmt.setInt(1, date.getYear()); // rd
                stmt.setInt(2, date.getYear()); // r
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                revenues.add(new RevenueByBranchRecord(
                        rs.getString("branch_id"),
                        rs.getString("branch_name"),
                        rs.getBigDecimal("rental_income"),
                        rs.getBigDecimal("penalty_income"),
                        rs.getBigDecimal("salary_expenses")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenues;
    }

    // ============================
    // 2. COMPANY REVENUE
    // ============================
    public RevenueByBranchRecord getCompanyRevenue(LocalDate date, String granularity) {

        String filterR;   // rental_details r
        String filterR2;  // rental_details r2 (penalty)

        switch (granularity.toLowerCase()) {
            case "daily" -> {
                filterR  = "DATE(r.rental_datetime) = ?";
                filterR2 = "DATE(r2.rental_datetime) = ?";
            }
            case "monthly" -> {
                filterR  = "MONTH(r.rental_datetime) = ? AND YEAR(r.rental_datetime) = ?";
                filterR2 = "MONTH(r2.rental_datetime) = ? AND YEAR(r2.rental_datetime) = ?";
            }
            case "yearly" -> {
                filterR  = "YEAR(r.rental_datetime) = ?";
                filterR2 = "YEAR(r2.rental_datetime) = ?";
            }
            default -> throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

        String query = String.format("""
        SELECT
            COALESCE(SUM(rental_total_payment), 0) AS rental_income,

            (
                SELECT COALESCE(SUM(v.violation_penalty_fee), 0)
                FROM violation_details v
                INNER JOIN rental_details r2 ON v.violation_rental_id = r2.rental_id
                WHERE %s
            ) AS penalty_income,

            (
                SELECT SUM(j.job_salary)
                FROM staff_record s
                INNER JOIN job_record j ON s.staff_job_id = j.job_id
            ) AS salary_expenses

        FROM rental_details r
        WHERE %s
          AND r.rental_datetime <= NOW();
        """, filterR2, filterR);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (granularity.equalsIgnoreCase("daily")) {
                stmt.setDate(1, Date.valueOf(date)); // r2
                stmt.setDate(2, Date.valueOf(date)); // r
            }
            else if (granularity.equalsIgnoreCase("monthly")) {
                stmt.setInt(1, date.getMonthValue()); // r2
                stmt.setInt(2, date.getYear());
                stmt.setInt(3, date.getMonthValue()); // r
                stmt.setInt(4, date.getYear());
            }
            else { // yearly
                stmt.setInt(1, date.getYear()); // r2
                stmt.setInt(2, date.getYear()); // r
            }

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new RevenueByBranchRecord(
                        "ALL",
                        "WHOLE COMPANY",
                        rs.getBigDecimal("rental_income"),
                        rs.getBigDecimal("penalty_income"),
                        rs.getBigDecimal("salary_expenses")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}