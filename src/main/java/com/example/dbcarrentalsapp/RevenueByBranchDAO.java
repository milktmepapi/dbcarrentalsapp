package com.example.dbcarrentalsapp;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.RevenueByBranchRecord;

public class RevenueByBranchDAO {

    // company establishment (inclusive)
    private static final LocalDate COMPANY_ESTABLISHED = LocalDate.of(2025, 1, 1);

    // ============================
    // 1. BRANCH REVENUE (rental + penalties)
    // ============================
    public List<RevenueByBranchRecord> getRevenueByBranch(LocalDate date, String granularity) {

        List<RevenueByBranchRecord> revenues = new ArrayList<>();

        // Filters for rental (r) and penalty lookup (rd)
        String filterR;
        String filterRD;

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
                ) AS penalty_income
            FROM branch_record b
            LEFT JOIN rental_details r ON r.rental_branch_id = b.branch_id
                AND %s
                AND r.rental_datetime <= NOW()
            GROUP BY b.branch_id, b.branch_name
            ORDER BY rental_income DESC;
            """, filterRD, filterR);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // bind params in correct order (rd params first, then r params)
            if (granularity.equalsIgnoreCase("daily")) {
                stmt.setDate(1, Date.valueOf(date)); // rd
                stmt.setDate(2, Date.valueOf(date)); // r
            } else if (granularity.equalsIgnoreCase("monthly")) {
                stmt.setInt(1, date.getMonthValue()); // rd month
                stmt.setInt(2, date.getYear());       // rd year
                stmt.setInt(3, date.getMonthValue()); // r month
                stmt.setInt(4, date.getYear());       // r year
            } else { // yearly
                stmt.setInt(1, date.getYear()); // rd year
                stmt.setInt(2, date.getYear()); // r year
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                revenues.add(new RevenueByBranchRecord(
                        rs.getString("branch_id"),
                        rs.getString("branch_name"),
                        rs.getBigDecimal("rental_income"),
                        rs.getBigDecimal("penalty_income")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenues;
    }

    // ============================
    // 2. COMPANY CUMULATIVE REVENUE (SINCE ESTABLISHMENT)
    // ============================
    public RevenueByBranchRecord getCompanyRevenue(LocalDate ignoreDate, String ignoreGranularity) {
        // We intentionally ignore the date/granularity passed by the UI for company totals.
        // Company total = sum(rental_total_payment) + sum(violation_penalty_fee) from company establishment -> now

        String rentalQuery = """
            SELECT COALESCE(SUM(r.rental_total_payment), 0) AS rental_income
            FROM rental_details r
            WHERE r.rental_datetime >= ? AND r.rental_datetime <= NOW()
            """;

        String penaltyQuery = """
            SELECT COALESCE(SUM(v.violation_penalty_fee), 0) AS penalty_income
            FROM violation_details v
            INNER JOIN rental_details rd ON v.violation_rental_id = rd.rental_id
            WHERE rd.rental_datetime >= ? AND rd.rental_datetime <= NOW()
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtRental = conn.prepareStatement(rentalQuery);
             PreparedStatement stmtPenalty = conn.prepareStatement(penaltyQuery)) {

            Date established = Date.valueOf(COMPANY_ESTABLISHED);

            // rental
            stmtRental.setDate(1, established);
            ResultSet rs1 = stmtRental.executeQuery();
            java.math.BigDecimal rentalIncome = java.math.BigDecimal.ZERO;
            if (rs1.next()) rentalIncome = rs1.getBigDecimal("rental_income");

            // penalty
            stmtPenalty.setDate(1, established);
            ResultSet rs2 = stmtPenalty.executeQuery();
            java.math.BigDecimal penaltyIncome = java.math.BigDecimal.ZERO;
            if (rs2.next()) penaltyIncome = rs2.getBigDecimal("penalty_income");

            return new RevenueByBranchRecord(
                    "ALL",
                    "WHOLE COMPANY (since " + COMPANY_ESTABLISHED.toString() + ")",
                    rentalIncome,
                    penaltyIncome
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}