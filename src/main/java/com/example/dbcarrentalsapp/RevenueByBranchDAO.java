package com.example.dbcarrentalsapp;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.RevenueByBranchRecord;

public class RevenueByBranchDAO {

    public List<RevenueByBranchRecord> getRevenueByBranch(LocalDate date, String granularity) {
        List<RevenueByBranchRecord> revenues = new ArrayList<>();

        String dateFilter;
        switch (granularity.toLowerCase()) {
            case "daily":
                dateFilter = "DATE(r.rental_datetime) = ?";
                break;
            case "monthly":
                dateFilter = "MONTH(r.rental_datetime) = ? AND YEAR(r.rental_datetime) = ?";
                break;
            case "yearly":
                dateFilter = "YEAR(r.rental_datetime) = ?";
                break;
            default:
                throw new IllegalArgumentException("Invalid granularity: " + granularity);
        }

        String query = String.format("""
        SELECT
            b.branch_id,
            b.branch_name,
            COALESCE(SUM(r.rental_total_payment), 0) AS rental_income,

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
        """, dateFilter);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // BIND PARAMETERS
            if (granularity.equalsIgnoreCase("daily")) {
                stmt.setDate(1, java.sql.Date.valueOf(date));
            } else if (granularity.equalsIgnoreCase("monthly")) {
                stmt.setInt(1, date.getMonthValue());
                stmt.setInt(2, date.getYear());
            } else if (granularity.equalsIgnoreCase("yearly")) {
                stmt.setInt(1, date.getYear());
            }

            ResultSet rs = stmt.executeQuery();

            BigDecimal totalIncome = BigDecimal.ZERO;
            BigDecimal totalSalary = BigDecimal.ZERO;

            while (rs.next()) {
                BigDecimal income = rs.getBigDecimal("rental_income");
                BigDecimal salary = rs.getBigDecimal("salary_expenses");

                revenues.add(new RevenueByBranchRecord(
                        rs.getString("branch_id"),
                        rs.getString("branch_name"),
                        income,
                        BigDecimal.ZERO, // penalty removed
                        salary
                ));

                totalIncome = totalIncome.add(income);
                totalSalary = totalSalary.add(salary);
            }

            // ADD WHOLE COMPANY TOTAL
            revenues.add(new RevenueByBranchRecord(
                    "ALL",
                    "WHOLE COMPANY",
                    totalIncome,
                    BigDecimal.ZERO,
                    totalSalary
            ));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return revenues;
    }
}